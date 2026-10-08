package ac.id.staialyasini.siakad.data.repository

import ac.id.staialyasini.siakad.core.util.LocationValidator
import ac.id.staialyasini.siakad.data.local.SessionDataStore
import ac.id.staialyasini.siakad.data.local.dao.SiakadCacheDao
import ac.id.staialyasini.siakad.data.local.entity.BimbinganLogEntity
import ac.id.staialyasini.siakad.data.local.entity.JadwalKuliahEntity
import ac.id.staialyasini.siakad.data.local.entity.PmbBerkasEntity
import ac.id.staialyasini.siakad.data.local.entity.PmbBiodataEntity
import ac.id.staialyasini.siakad.data.remote.SiakadApiService
import ac.id.staialyasini.siakad.domain.model.BerkasPmbItem
import ac.id.staialyasini.siakad.domain.model.BimbinganLogItem
import ac.id.staialyasini.siakad.domain.model.CicilanTagihanItem
import ac.id.staialyasini.siakad.domain.model.JadwalKuliahItem
import ac.id.staialyasini.siakad.domain.model.KelasDitawarkanItem
import ac.id.staialyasini.siakad.domain.model.KrsPerwalianApprovalItem
import ac.id.staialyasini.siakad.domain.model.KrsStatus
import ac.id.staialyasini.siakad.domain.model.NilaiMatakuliahItem
import ac.id.staialyasini.siakad.domain.model.PesertaKelasPresensiItem
import ac.id.staialyasini.siakad.domain.model.PmbProgressState
import ac.id.staialyasini.siakad.domain.model.SemesterKhsItem
import ac.id.staialyasini.siakad.domain.model.SkripsiApprovalQueueItem
import ac.id.staialyasini.siakad.domain.model.TagihanUktSummary
import ac.id.staialyasini.siakad.domain.model.UserRole
import ac.id.staialyasini.siakad.domain.model.UserSession
import ac.id.staialyasini.siakad.domain.repository.SiakadRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementasi Offline-First Repository SIAKAD STAI Al-Yasini Mobile.
 * Seluruh operasi CRUD (Jadwal Kuliah, Buku Log Bimbingan Skripsi, dan Biodata/Berkas PMB)
 * disimpan secara permanen (persisten) ke Room Database SQLite (`SiakadCacheDao`).
 */
@Singleton
class SiakadRepositoryImpl @Inject constructor(
    private val apiService: SiakadApiService,
    private val sessionDataStore: SessionDataStore,
    private val cacheDao: SiakadCacheDao
) : SiakadRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val activeSessionFlow: Flow<UserSession?> = sessionDataStore.activeSessionFlow
    override val isBiometricEnabledFlow: Flow<Boolean> = sessionDataStore.isBiometricEnabledFlow
    override val savedRoleForBiometricFlow: Flow<UserRole> = sessionDataStore.savedRoleForBiometricFlow

    // ==================== INITIAL SEED DATA ====================

    private val defaultJadwalSeed = listOf(
        JadwalKuliahItem(
            id = 101L,
            kodeMk = "PAI-501",
            namaMk = "Ushul Fiqh Kontemporer & Maqashid Syariah",
            sks = 3,
            kelas = "PAI-5A",
            hari = "Selasa",
            jamMulai = "08:00",
            jamSelesai = "10:30",
            ruangan = "Gedung KH. Murtadho Lt. 2 - R.204",
            dosenPengajar = "Dr. KH. Ahmad Muzakki, M.Ag.",
            pertemuanKe = 7,
            sudahPresensiHariIni = false,
            persentaseKehadiran = 94
        ),
        JadwalKuliahItem(
            id = 102L,
            kodeMk = "PAI-504",
            namaMk = "Metodologi Penelitian Pendidikan Islam",
            sks = 3,
            kelas = "PAI-5A",
            hari = "Selasa",
            jamMulai = "10:45",
            jamSelesai = "13:15",
            ruangan = "Gedung Al-Ghazali Lt. 1 - R.102",
            dosenPengajar = "Dr. Hj. Siti Marifah, M.Pd.I.",
            pertemuanKe = 7,
            sudahPresensiHariIni = true,
            persentaseKehadiran = 100
        ),
        JadwalKuliahItem(
            id = 103L,
            kodeMk = "PAI-508",
            namaMk = "Pengembangan Kurikulum Pesantren & Madrasah",
            sks = 2,
            kelas = "PAI-5A",
            hari = "Selasa",
            jamMulai = "13:30",
            jamSelesai = "15:10",
            ruangan = "Lab Microteaching STAI Al-Yasini",
            dosenPengajar = "H. M. Ridwan Hisyam, M.Pd.",
            pertemuanKe = 7,
            sudahPresensiHariIni = false,
            persentaseKehadiran = 88
        )
    )

    private val defaultBimbinganSeed = listOf(
        BimbinganLogItem(1L, 1, "12 Agu 2026", "Pengajuan & Pemetaan Judul Skripsi", "Konsultasi fokus penelitian efektivitas metode Sorogan di Madrasah Al-Yasini.", "Judul sangat relevan. Lanjutkan ke penyusunan latar belakang Bab I.", true),
        BimbinganLogItem(2L, 2, "20 Agu 2026", "Bab I - Pendahuluan & Rumusan Masalah", "Mengirimkan draf Bab I lengkap dengan gap penelitian terdahulu.", "Pertajam rumusan masalah ke-2 agar terukur secara kuantitatif.", true),
        BimbinganLogItem(3L, 3, "29 Agu 2026", "Revisi Bab I & Outline Bab II", "Perbaikan rumusan masalah sesuai arahan dan kerangka teori kitab kuning.", "ACC Bab I. Lanjutkan penulisan Bab II kajian pustaka.", true),
        BimbinganLogItem(4L, 4, "08 Sep 2026", "Bab II - Kajian Teori & Kerangka Berpikir", "Menambahkan rujukan turats klasik dan jurnal Sinta 2 terbaru.", "ACC Bab II. Hipotesis penelitian sudah jelas.", true),
        BimbinganLogItem(5L, 5, "17 Sep 2026", "Bab III - Metodologi Penelitian & Instrumen", "Menyusun kisi-kisi angket dan pedoman observasi santri.", "Uji validitas instrumen ke 30 responden terlebih dahulu.", true),
        BimbinganLogItem(6L, 6, "25 Sep 2026", "Hasil Uji Validitas & Reliabilitas Bab III", "Melampirkan output SPSS uji validitas instrumen penelitian.", "ACC Bab III. Silakan turun lapangan ambil data.", true),
        BimbinganLogItem(7L, 7, "01 Okt 2026", "Bab IV - Hasil Penelitian & Analisis Data", "Tabulasi data hasil observasi dan uji regresi di MA Al-Yasini.", "ACC Bab IV. Interpretasi pembahasan sudah mendalam.", true),
        BimbinganLogItem(8L, 8, "05 Okt 2026", "Bab V - Penutup, Abstrak Arab-Inggris & Lampiran", "Draf final Bab I s/d V siap cek plagiasi Turnitin.", "Menunggu review Dosen Pembimbing...", false)
    )

    private val defaultPmbState = PmbProgressState(
        nomorPendaftaran = "PMB-2026-GEL2-0194",
        namaLengkap = "Zaidan Al-Farisi",
        gelombang = "Gelombang II - Tahun Akademik 2026/2027",
        pilihanProdi1 = "S1 Pendidikan Agama Islam (PAI)",
        pilihanProdi2 = "S1 Hukum Ekonomi Syariah (HES)",
        currentStepIndex = 4,
        nilaiUjianSeleksi = 86,
        statusKelulusan = "LULUS SELEKSI UTAMA",
        tagihanDaftarUlang = 2_450_000L,
        isDaftarUlangLunas = false,
        nimTerbit = null,
        berkasList = listOf(
            BerkasPmbItem(1L, "Ijazah / SKL SMA/MA/SMK Sederajat", true, "Terverifikasi"),
            BerkasPmbItem(2L, "Kartu Tanda Penduduk (KTP) / KIA", true, "Terverifikasi"),
            BerkasPmbItem(3L, "Kartu Keluarga (KK)", true, "Ditolak", "Foto KK agak buram pada bagian NIK. Mohon foto ulang dengan pencahayaan terang."),
            BerkasPmbItem(4L, "Pasfoto Resmi Latar Hijau/Merah (3x4)", true, "Terverifikasi"),
            BerkasPmbItem(5L, "Sertifikat Tahfidz / Syahadah Pesantren (Opsional)", false, "Terverifikasi")
        )
    )

    private val _tagihanUktState = MutableStateFlow(
        TagihanUktSummary(
            id = 20261L,
            nomorInvoice = "INV/ALYASINI/2026/GJL/0042",
            tahunAjaran = "2026/2027 Ganjil",
            namaTagihan = "UKT Semester V - Pendidikan Agama Islam",
            totalTagihan = 2_400_000L,
            totalTerbayar = 1_600_000L,
            statusLunas = false,
            virtualAccountBsi = "90042812023010042",
            virtualAccountBri = "888102023010042",
            snapToken = "SNAP-ALYASINI-99412-MIDTRANS",
            cicilanList = listOf(
                CicilanTagihanItem(
                    id = 1L,
                    terminKe = 1,
                    keterangan = "Termin 1 (Herregistrasi & Awal Semester)",
                    nominal = 800_000L,
                    jatuhTempo = "10 Agustus 2026",
                    isPaid = true,
                    tanggalBayar = "08 Agustus 2026",
                    metodeBayar = "BSI Virtual Account"
                ),
                CicilanTagihanItem(
                    id = 2L,
                    terminKe = 2,
                    keterangan = "Termin 2 (Syarat UTS Semester Ganjil)",
                    nominal = 800_000L,
                    jatuhTempo = "05 Oktober 2026",
                    isPaid = true,
                    tanggalBayar = "02 Oktober 2026",
                    metodeBayar = "QRIS Midtrans"
                ),
                CicilanTagihanItem(
                    id = 3L,
                    terminKe = 3,
                    keterangan = "Termin 3 (Pelunasan UAS & Pengajuan KRS)",
                    nominal = 800_000L,
                    jatuhTempo = "25 November 2026",
                    isPaid = false,
                    tanggalBayar = null,
                    metodeBayar = null
                )
            )
        )
    )
    override val tagihanUktState: StateFlow<TagihanUktSummary> = _tagihanUktState.asStateFlow()

    private val _jadwalHariIniState = MutableStateFlow(defaultJadwalSeed)
    override val jadwalHariIniState: StateFlow<List<JadwalKuliahItem>> = _jadwalHariIniState.asStateFlow()

    private val _krsStudentStatusState = MutableStateFlow(KrsStatus.DRAFT)
    override val krsStudentStatusState: StateFlow<KrsStatus> = _krsStudentStatusState.asStateFlow()

    private val _krsCatatanWaliState = MutableStateFlow<String?>(null)
    override val krsCatatanWaliState: StateFlow<String?> = _krsCatatanWaliState.asStateFlow()

    private val _kelasDitawarkanState = MutableStateFlow(
        listOf(
            KelasDitawarkanItem(
                id = 501L,
                kodeMk = "PAI-501",
                namaMk = "Ushul Fiqh Kontemporer & Maqashid Syariah",
                sks = 3,
                semesterPaket = 5,
                kelas = "PAI-5A",
                hari = "Selasa",
                jamMulai = "08:00",
                jamSelesai = "10:30",
                ruangan = "R.204",
                namaDosen = "Dr. KH. Ahmad Muzakki, M.Ag.",
                kuotaTerisi = 32,
                kuotaMaksimal = 35,
                isSelected = true
            ),
            KelasDitawarkanItem(
                id = 502L,
                kodeMk = "PAI-504",
                namaMk = "Metodologi Penelitian Pendidikan Islam",
                sks = 3,
                semesterPaket = 5,
                kelas = "PAI-5A",
                hari = "Selasa",
                jamMulai = "10:45",
                jamSelesai = "13:15",
                ruangan = "R.102",
                namaDosen = "Dr. Hj. Siti Marifah, M.Pd.I.",
                kuotaTerisi = 30,
                kuotaMaksimal = 35,
                isSelected = true
            ),
            KelasDitawarkanItem(
                id = 503L,
                kodeMk = "PAI-508",
                namaMk = "Pengembangan Kurikulum Pesantren & Madrasah",
                sks = 2,
                semesterPaket = 5,
                kelas = "PAI-5A",
                hari = "Selasa",
                jamMulai = "13:30",
                jamSelesai = "15:10",
                ruangan = "Lab Microteaching",
                namaDosen = "H. M. Ridwan Hisyam, M.Pd.",
                kuotaTerisi = 28,
                kuotaMaksimal = 35,
                isSelected = true
            ),
            KelasDitawarkanItem(
                id = 504L,
                kodeMk = "PAI-510",
                namaMk = "Tafsir Tarbawi & Hadits Pendidikan",
                sks = 4,
                semesterPaket = 5,
                kelas = "PAI-5A",
                hari = "Rabu",
                jamMulai = "08:00",
                jamSelesai = "11:20",
                ruangan = "R.201",
                namaDosen = "KH. Abdul Halim Yasini, Lc., M.H.I.",
                kuotaTerisi = 34,
                kuotaMaksimal = 35,
                isSelected = true
            ),
            KelasDitawarkanItem(
                id = 505L,
                kodeMk = "PAI-512",
                namaMk = "Evaluasi Pembelajaran PAI Berbasis Digital",
                sks = 3,
                semesterPaket = 5,
                kelas = "PAI-5A",
                hari = "Kamis",
                jamMulai = "09:00",
                jamSelesai = "11:30",
                ruangan = "Lab Komputer 1",
                namaDosen = "M. Bahrul Ulum, M.Kom.",
                kuotaTerisi = 25,
                kuotaMaksimal = 35,
                isSelected = true
            ),
            KelasDitawarkanItem(
                id = 506L,
                kodeMk = "PAI-515",
                namaMk = "Sosiologi & Antropologi Pesantren",
                sks = 3,
                semesterPaket = 5,
                kelas = "PAI-5B",
                hari = "Selasa",
                jamMulai = "08:00",
                jamSelesai = "10:30",
                ruangan = "R.206",
                namaDosen = "Dr. H. Zainul Arifin, M.Sos.",
                kuotaTerisi = 29,
                kuotaMaksimal = 35,
                isSelected = false
            ),
            KelasDitawarkanItem(
                id = 507L,
                kodeMk = "PAI-519",
                namaMk = "Manajemen Kewirausahaan Lembaga Islam",
                sks = 3,
                semesterPaket = 5,
                kelas = "PAI-5A",
                hari = "Jumat",
                jamMulai = "08:00",
                jamSelesai = "10:30",
                ruangan = "R.105",
                namaDosen = "Hj. Nurul Hidayati, M.E.I.",
                kuotaTerisi = 22,
                kuotaMaksimal = 35,
                isSelected = false
            )
        )
    )
    override val kelasDitawarkanState: StateFlow<List<KelasDitawarkanItem>> = _kelasDitawarkanState.asStateFlow()

    private val _khsSemesterListState = MutableStateFlow(
        listOf(
            SemesterKhsItem(
                semester = 1,
                tahunAjaran = "2024/2025 Ganjil",
                ips = 3.55,
                sksSemester = 20,
                matakuliahNilai = listOf(
                    NilaiMatakuliahItem("PAI-101", "Bahasa Arab Intensif I", 3, 88.0, "A", 4.00),
                    NilaiMatakuliahItem("PAI-102", "Ulumul Qur'an", 3, 84.0, "A-", 3.75),
                    NilaiMatakuliahItem("PAI-103", "Ilmu Pendidikan Islam", 3, 81.0, "B+", 3.50),
                    NilaiMatakuliahItem("MKU-101", "Pancasila & Kewarganegaraan", 2, 86.0, "A", 4.00)
                )
            ),
            SemesterKhsItem(
                semester = 2,
                tahunAjaran = "2024/2025 Genap",
                ips = 3.68,
                sksSemester = 22,
                matakuliahNilai = listOf(
                    NilaiMatakuliahItem("PAI-201", "Ulumul Hadits", 3, 89.0, "A", 4.00),
                    NilaiMatakuliahItem("PAI-204", "Psikologi Perkembangan Peserta Didik", 3, 85.0, "A-", 3.75),
                    NilaiMatakuliahItem("PAI-206", "Fiqh Ibadah & Muamalah", 3, 87.0, "A", 4.00)
                )
            ),
            SemesterKhsItem(
                semester = 3,
                tahunAjaran = "2025/2026 Ganjil",
                ips = 3.74,
                sksSemester = 24,
                matakuliahNilai = listOf(
                    NilaiMatakuliahItem("PAI-301", "Sejarah Peradaban Islam & Nusantara", 3, 90.0, "A", 4.00),
                    NilaiMatakuliahItem("PAI-305", "Strategi & Media Pembelajaran PAI", 4, 86.0, "A", 4.00),
                    NilaiMatakuliahItem("PAI-308", "Filsafat Pendidikan Islam", 3, 82.0, "B+", 3.50)
                )
            ),
            SemesterKhsItem(
                semester = 4,
                tahunAjaran = "2025/2026 Genap",
                ips = 3.82,
                sksSemester = 24,
                matakuliahNilai = listOf(
                    NilaiMatakuliahItem("PAI-401", "Statistika Pendidikan Islam", 3, 91.0, "A", 4.00),
                    NilaiMatakuliahItem("PAI-403", "Desain Perencanaan Pembelajaran PAI", 4, 89.0, "A", 4.00),
                    NilaiMatakuliahItem("PAI-407", "Studi Kitab Kuning (Fathul Mu'in)", 3, 87.0, "A", 4.00),
                    NilaiMatakuliahItem("PAI-410", "Bimbingan & Konseling Islam", 3, 84.0, "A-", 3.75)
                )
            )
        )
    )
    override val khsSemesterListState: StateFlow<List<SemesterKhsItem>> = _khsSemesterListState.asStateFlow()

    private val _bimbinganLogsState = MutableStateFlow(defaultBimbinganSeed)
    override val bimbinganLogsState: StateFlow<List<BimbinganLogItem>> = _bimbinganLogsState.asStateFlow()

    // ==================== DOSEN & KAPRODI QUEUES ====================

    private val _krsApprovalQueueState = MutableStateFlow(
        listOf(
            KrsPerwalianApprovalItem(
                krsId = 901L,
                nim = "2023010042",
                namaMahasiswa = "Ahmad Fauzi",
                prodi = "Pendidikan Agama Islam (S1)",
                semester = 5,
                ipsLalu = 3.82,
                batasMaxSks = 24,
                totalSksDiajukan = 18,
                waktuPengajuan = "10 menit yang lalu",
                status = KrsStatus.DIAJUKAN,
                rincianMatakuliah = listOf(
                    "PAI-501 Ushul Fiqh Kontemporer (3 SKS)",
                    "PAI-504 Metodologi Penelitian PAI (3 SKS)",
                    "PAI-508 Pengembangan Kurikulum Pesantren (2 SKS)",
                    "PAI-510 Tafsir Tarbawi & Hadits Pendidikan (4 SKS)",
                    "PAI-512 Evaluasi Pembelajaran PAI Digital (3 SKS)",
                    "PAI-519 Manajemen Kewirausahaan Islam (3 SKS)"
                ),
                isCheckedForBatch = true
            ),
            KrsPerwalianApprovalItem(
                krsId = 902L,
                nim = "2023010018",
                namaMahasiswa = "Siti Nur Halimah",
                prodi = "Pendidikan Agama Islam (S1)",
                semester = 5,
                ipsLalu = 3.65,
                batasMaxSks = 24,
                totalSksDiajukan = 21,
                waktuPengajuan = "35 menit yang lalu",
                status = KrsStatus.DIAJUKAN,
                rincianMatakuliah = listOf(
                    "PAI-501 Ushul Fiqh Kontemporer (3 SKS)",
                    "PAI-504 Metodologi Penelitian PAI (3 SKS)",
                    "PAI-508 Pengembangan Kurikulum Pesantren (2 SKS)",
                    "PAI-510 Tafsir Tarbawi & Hadits Pendidikan (4 SKS)",
                    "PAI-512 Evaluasi Pembelajaran PAI Digital (3 SKS)",
                    "PAI-515 Sosiologi Pesantren (3 SKS)",
                    "PAI-519 Manajemen Kewirausahaan Islam (3 SKS)"
                ),
                isCheckedForBatch = true
            ),
            KrsPerwalianApprovalItem(
                krsId = 903L,
                nim = "2023010075",
                namaMahasiswa = "Muhammad Syifa'ul Qulub",
                prodi = "Pendidikan Agama Islam (S1)",
                semester = 5,
                ipsLalu = 2.92,
                batasMaxSks = 20,
                totalSksDiajukan = 20,
                waktuPengajuan = "1 jam yang lalu",
                status = KrsStatus.DIAJUKAN,
                rincianMatakuliah = listOf(
                    "PAI-501 Ushul Fiqh Kontemporer (3 SKS)",
                    "PAI-504 Metodologi Penelitian PAI (3 SKS)",
                    "PAI-510 Tafsir Tarbawi & Hadits Pendidikan (4 SKS)",
                    "PAI-512 Evaluasi Pembelajaran PAI Digital (3 SKS)",
                    "PAI-515 Sosiologi Pesantren (3 SKS)",
                    "MKU-302 Bahasa Arab Akademik (4 SKS)"
                ),
                isCheckedForBatch = false
            )
        )
    )
    override val krsApprovalQueueState: StateFlow<List<KrsPerwalianApprovalItem>> = _krsApprovalQueueState.asStateFlow()

    private val _skripsiApprovalQueueState = MutableStateFlow(
        listOf(
            SkripsiApprovalQueueItem(
                id = 701L,
                nim = "2022010011",
                namaMahasiswa = "Nabila Khoirunnisa",
                judulSkripsi = "Implementasi Metode Amtsilati dalam Meningkatkan Kemampuan Membaca Kitab Fathul Qarib di PP Terpadu Al-Yasini",
                tahap = "Bimbingan Bab V & Draft Final (Ke-8)",
                jumlahBimbinganAcc = 8,
                targetMinimalBimbingan = 8,
                catatanTerbaruMahasiswa = "Assalamu'alaikum Kyai/Ustadz, berikut revisi abstrak Bahasa Arab dan hasil cek Turnitin (14%). Mohon ACC Sidang Munaqasyah.",
                tanggalKirim = "Hari ini, 08:15 WIB",
                dosenPembimbingSaatIni = "Dr. KH. Ahmad Muzakki, M.Ag.",
                isAccBimbingan = true,
                isAccSidangMunaqasyah = false,
                isProposalApprovedByKaprodi = true
            ),
            SkripsiApprovalQueueItem(
                id = 702L,
                nim = "2022010034",
                namaMahasiswa = "M. Rizqi Maulana",
                judulSkripsi = "Pengaruh Literasi Keuangan Syariah terhadap Minat Menabung Santri di Bank Syariah Indonesia",
                tahap = "Bimbingan Bab IV - Analisis Data (Ke-5)",
                jumlahBimbinganAcc = 4,
                targetMinimalBimbingan = 8,
                catatanTerbaruMahasiswa = "Mohon koreksi hasil uji regresi linier berganda pada sub-bab 4.2.",
                tanggalKirim = "Kemarin, 19:40 WIB",
                dosenPembimbingSaatIni = "Dr. KH. Ahmad Muzakki, M.Ag.",
                isAccBimbingan = false,
                isAccSidangMunaqasyah = false,
                isProposalApprovedByKaprodi = true
            ),
            SkripsiApprovalQueueItem(
                id = 703L,
                nim = "2023010042",
                namaMahasiswa = "Ahmad Fauzi",
                judulSkripsi = "Pengembangan Media Pembelajaran Fiqh Mawaris Berbasis Android di MA Al-Yasini Pasuruan",
                tahap = "Pengajuan Judul & Plotting Pembimbing (Kaprodi)",
                jumlahBimbinganAcc = 0,
                targetMinimalBimbingan = 8,
                catatanTerbaruMahasiswa = "Pengajuan proposal skripsi baru (Total SKS Lulus: 110 SKS, IPK: 3.78).",
                tanggalKirim = "Hari ini, 09:05 WIB",
                dosenPembimbingSaatIni = "Belum Ditetapkan",
                isAccBimbingan = false,
                isAccSidangMunaqasyah = false,
                isProposalApprovedByKaprodi = false
            )
        )
    )
    override val skripsiApprovalQueueState: StateFlow<List<SkripsiApprovalQueueItem>> = _skripsiApprovalQueueState.asStateFlow()

    private val _ahmadFauziSkripsiState = MutableStateFlow(
        _skripsiApprovalQueueState.value.first { it.nim == "2023010042" }
    )
    override val ahmadFauziSkripsiState: StateFlow<SkripsiApprovalQueueItem> = _ahmadFauziSkripsiState.asStateFlow()

    private val _pesertaKelasState = MutableStateFlow(
        listOf(
            PesertaKelasPresensiItem("2023010042", "Ahmad Fauzi", "Hadir"),
            PesertaKelasPresensiItem("2023010018", "Siti Nur Halimah", "Hadir"),
            PesertaKelasPresensiItem("2023010075", "Muhammad Syifa'ul Qulub", "Hadir"),
            PesertaKelasPresensiItem("2023010029", "Dewi Masitoh", "Izin"),
            PesertaKelasPresensiItem("2023010051", "Abdul Rohman Wahid", "Sakit"),
            PesertaKelasPresensiItem("2023010088", "Fathur Rozi", "Alpha")
        )
    )
    override val pesertaKelasState: StateFlow<List<PesertaKelasPresensiItem>> = _pesertaKelasState.asStateFlow()

    private val _pmbProgressState = MutableStateFlow(defaultPmbState)
    override val pmbProgressState: StateFlow<PmbProgressState> = _pmbProgressState.asStateFlow()

    init {
        // Seed initial Room SQLite tables if empty, then observe Room flows continuously
        repositoryScope.launch {
            runCatching {
                if (cacheDao.countJadwal() == 0) {
                    cacheDao.upsertJadwalList(defaultJadwalSeed.map { it.toEntity() })
                }
                if (cacheDao.countBimbinganLogs() == 0) {
                    cacheDao.upsertBimbinganLogList(defaultBimbinganSeed.map { it.toEntity() })
                }
                if (cacheDao.countPmbBiodata() == 0) {
                    cacheDao.upsertPmbBiodata(defaultPmbState.toBiodataEntity())
                }
                if (cacheDao.countPmbBerkas() == 0) {
                    cacheDao.upsertPmbBerkasList(defaultPmbState.berkasList.map { it.toEntity() })
                }
            }

            // 1. Observe Jadwal Kuliah from Room Database
            launch {
                cacheDao.observeCachedJadwal().collect { entities ->
                    if (entities.isNotEmpty()) {
                        _jadwalHariIniState.value = entities.map { it.toDomain() }
                    } else {
                        _jadwalHariIniState.value = emptyList()
                    }
                }
            }

            // 2. Observe Bimbingan Logs from Room Database
            launch {
                cacheDao.observeBimbinganLogs().collect { entities ->
                    _bimbinganLogsState.value = entities.map { it.toDomain() }
                }
            }

            // 3. Observe PMB Biodata & Berkas from Room Database
            launch {
                combine(
                    cacheDao.observePmbBiodata(),
                    cacheDao.observePmbBerkasList()
                ) { biodataEntity, berkasEntities ->
                    val base = biodataEntity?.toDomain(
                        berkasList = berkasEntities.map { it.toDomain() }
                    ) ?: defaultPmbState.copy(
                        berkasList = berkasEntities.map { it.toDomain() }
                    )
                    base
                }.collect { combinedState ->
                    _pmbProgressState.value = combinedState
                }
            }
        }
    }

    // ==================== AUTHENTICATION & MULTI-ROLE SESSION ====================

    override suspend fun login(
        identifier: String,
        password: String,
        preferredRole: UserRole?
    ): Result<UserSession> {
        delay(350)
        if (identifier.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email/NIM/NIDN dan Password wajib diisi."))
        }

        val detectedRole = preferredRole ?: when {
            identifier.contains("kaprodi", ignoreCase = true) || identifier.startsWith("0701") -> UserRole.KAPRODI
            identifier.contains("dosen", ignoreCase = true) || identifier.startsWith("07") || identifier.startsWith("21") -> UserRole.DOSEN
            identifier.contains("pmb", ignoreCase = true) || identifier.startsWith("PMB", ignoreCase = true) -> UserRole.CALON_MAHASISWA
            else -> UserRole.MAHASISWA
        }

        val session = buildDemoSessionForRole(detectedRole, customIdentifier = identifier)
        sessionDataStore.saveSession(session)
        return Result.success(session)
    }

    override suspend fun loginWithBiometric(): Result<UserSession> {
        val lastRole = sessionDataStore.getSavedRole()
        val session = buildDemoSessionForRole(lastRole)
        sessionDataStore.saveSession(session)
        return Result.success(session)
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        sessionDataStore.setBiometricEnabled(enabled)
    }

    override suspend fun switchDemoRole(role: UserRole): UserSession {
        val session = buildDemoSessionForRole(role)
        sessionDataStore.saveSession(session)
        return session
    }

    override suspend fun logout() {
        runCatching { apiService.logout() }
        sessionDataStore.clearSession()
    }

    private fun buildDemoSessionForRole(
        role: UserRole,
        customIdentifier: String? = null
    ): UserSession {
        return when (role) {
            UserRole.MAHASISWA -> UserSession(
                userId = 1042L,
                name = "Ahmad Fauzi",
                identifier = customIdentifier?.takeIf { it.isNotBlank() } ?: "2023010042",
                email = "ahmad.fauzi@mhs.staialyasini.ac.id",
                prodi = "S1 Pendidikan Agama Islam (PAI)",
                role = UserRole.MAHASISWA,
                academicStatus = "Aktif",
                semester = 5,
                token = "sanctum_mhs_token_alyasini_2026_9f8a7b"
            )
            UserRole.DOSEN -> UserSession(
                userId = 2011L,
                name = "Dr. KH. Ahmad Muzakki, M.Ag.",
                identifier = customIdentifier?.takeIf { it.isNotBlank() } ?: "2114087801",
                email = "ahmad.muzakki@staialyasini.ac.id",
                prodi = "Dosen Tetap & Wali S1 PAI",
                role = UserRole.DOSEN,
                academicStatus = "Aktif Mengajar",
                semester = 5,
                token = "sanctum_dosen_token_alyasini_2026_3c2d1e"
            )
            UserRole.KAPRODI -> UserSession(
                userId = 2001L,
                name = "Dr. Hj. Siti Marifah, M.Pd.I.",
                identifier = customIdentifier?.takeIf { it.isNotBlank() } ?: "0701058002",
                email = "kaprodi.pai@staialyasini.ac.id",
                prodi = "Kaprodi S1 Pendidikan Agama Islam",
                role = UserRole.KAPRODI,
                academicStatus = "Pejabat Struktural Aktif",
                semester = 5,
                token = "sanctum_kaprodi_token_alyasini_2026_7a6b5c"
            )
            UserRole.CALON_MAHASISWA -> UserSession(
                userId = 90194L,
                name = _pmbProgressState.value.namaLengkap,
                identifier = customIdentifier?.takeIf { it.isNotBlank() } ?: "PMB-2026-GEL2-0194",
                email = "zaidan.alfarisi@gmail.com",
                prodi = "Pilihan 1: ${_pmbProgressState.value.pilihanProdi1}",
                role = UserRole.CALON_MAHASISWA,
                academicStatus = "Lulus Seleksi PMB",
                semester = 1,
                token = "sanctum_pmb_token_alyasini_2026_1x2y3z"
            )
        }
    }

    // ==================== MAHASISWA ACTIONS & CRUD LOG BIMBINGAN SKRIPSI ====================

    override suspend fun bayarCicilanAtauLunasUkt(
        terminId: Long?,
        bayarPenuh: Boolean,
        metode: String
    ) {
        delay(400)
        _tagihanUktState.update { current ->
            val updatedCicilan = current.cicilanList.map { item ->
                if (bayarPenuh || item.id == terminId) {
                    item.copy(
                        isPaid = true,
                        tanggalBayar = "08 Oktober 2026",
                        metodeBayar = metode
                    )
                } else {
                    item
                }
            }
            val totalPaid = updatedCicilan.filter { it.isPaid }.sumOf { it.nominal }
            val isAllPaid = totalPaid >= current.totalTagihan
            current.copy(
                totalTerbayar = totalPaid,
                statusLunas = isAllPaid,
                cicilanList = updatedCicilan
            )
        }
    }

    override suspend fun toggleSimulasiStatusUkt(lunas: Boolean) {
        _tagihanUktState.update { current ->
            val updatedCicilan = current.cicilanList.mapIndexed { idx, item ->
                if (lunas) {
                    item.copy(
                        isPaid = true,
                        tanggalBayar = item.tanggalBayar ?: "08 Oktober 2026",
                        metodeBayar = item.metodeBayar ?: "Midtrans Snap QRIS"
                    )
                } else {
                    if (idx == current.cicilanList.lastIndex) {
                        item.copy(isPaid = false, tanggalBayar = null, metodeBayar = null)
                    } else {
                        item
                    }
                }
            }
            val totalPaid = updatedCicilan.filter { it.isPaid }.sumOf { it.nominal }
            current.copy(
                totalTerbayar = totalPaid,
                statusLunas = lunas,
                cicilanList = updatedCicilan
            )
        }
    }

    override suspend fun togglePilihKelasKrs(kelasId: Long): Result<Unit> {
        val currentList = _kelasDitawarkanState.value
        val target = currentList.firstOrNull { it.id == kelasId }
            ?: return Result.failure(IllegalArgumentException("Kelas tidak ditemukan"))

        if (!target.isSelected) {
            if (target.sisaKuota <= 0) {
                return Result.failure(IllegalStateException("Kuota kelas ${target.namaMk} sudah penuh!"))
            }
            val currentSks = currentList.filter { it.isSelected }.sumOf { it.sks }
            if (currentSks + target.sks > 24) {
                return Result.failure(IllegalStateException("Melebihi batas maksimal 24 SKS berdasarkan IPS semester lalu (3.82)."))
            }
            val conflict = currentList.firstOrNull {
                it.isSelected &&
                    it.id != target.id &&
                    it.hari.equals(target.hari, ignoreCase = true) &&
                    it.jamMulai == target.jamMulai
            }
            if (conflict != null) {
                return Result.failure(
                    IllegalStateException(
                        "Bentrok jadwal pada hari ${target.hari} (${target.jamMulai}) dengan mata kuliah ${conflict.namaMk}!"
                    )
                )
            }
        }

        _kelasDitawarkanState.update { list ->
            list.map {
                if (it.id == kelasId) it.copy(isSelected = !it.isSelected) else it
            }
        }
        return Result.success(Unit)
    }

    override suspend fun ajukanKrsMahasiswa(): Result<Unit> {
        if (!_tagihanUktState.value.statusLunas) {
            return Result.failure(IllegalStateException("Cekal Keuangan: Tagihan UKT semester aktif belum lunas."))
        }
        val selected = _kelasDitawarkanState.value.filter { it.isSelected }
        if (selected.isEmpty()) {
            return Result.failure(IllegalArgumentException("Pilih minimal 1 mata kuliah sebelum mengajukan KRS."))
        }
        _krsStudentStatusState.value = KrsStatus.DIAJUKAN
        _krsCatatanWaliState.value = null
        val totalSks = selected.sumOf { it.sks }
        val courseNames = selected.map { "${it.kodeMk} ${it.namaMk} (${it.sks} SKS)" }

        // Sinkronisasi Real-Time ke Antrean Approval Dosen Wali untuk Ahmad Fauzi (krsId = 901L)
        _krsApprovalQueueState.update { list ->
            list.map { item ->
                if (item.krsId == 901L) {
                    item.copy(
                        status = KrsStatus.DIAJUKAN,
                        totalSksDiajukan = totalSks,
                        rincianMatakuliah = courseNames,
                        waktuPengajuan = "Baru saja diajukan",
                        catatanWali = null,
                        isCheckedForBatch = true
                    )
                } else item
            }
        }
        return Result.success(Unit)
    }

    override suspend fun resetKrsToDraft(): Result<Unit> {
        _krsStudentStatusState.value = KrsStatus.DRAFT
        _krsCatatanWaliState.value = null
        _krsApprovalQueueState.update { list ->
            list.map { item ->
                if (item.krsId == 901L) {
                    item.copy(
                        status = KrsStatus.DRAFT,
                        catatanWali = null,
                        isCheckedForBatch = false
                    )
                } else item
            }
        }
        return Result.success(Unit)
    }

    override suspend fun catatPresensiQrMahasiswa(
        jadwalId: Long,
        qrToken: String,
        lat: Double,
        lng: Double,
        isMockLocation: Boolean
    ): Result<String> {
        val validation = LocationValidator.validateCampusProximity(
            userLat = lat,
            userLng = lng,
            isMockLocation = isMockLocation
        )
        if (!validation.isWithinRadius) {
            return Result.failure(IllegalStateException(validation.message))
        }
        val target = _jadwalHariIniState.value.firstOrNull { it.id == jadwalId }
        if (target != null) {
            val updated = target.copy(
                sudahPresensiHariIni = true,
                persentaseKehadiran = (target.persentaseKehadiran + 2).coerceAtMost(100)
            )
            cacheDao.insertOrUpdateJadwal(updated.toEntity())
        }
        return Result.success("Presensi berhasil dicatat! (${validation.message})")
    }

    override suspend fun tambahLogBimbinganMahasiswa(
        pertemuanKe: Int,
        tanggal: String,
        bab: String,
        catatan: String
    ): Result<Unit> {
        if (bab.isBlank() || catatan.isBlank() || tanggal.isBlank()) {
            return Result.failure(IllegalArgumentException("Pertemuan, Tanggal, Bab Bahasan, dan Catatan wajib diisi!"))
        }
        val newEntity = BimbinganLogEntity(
            id = System.currentTimeMillis(),
            pertemuanKe = pertemuanKe.coerceAtLeast(1),
            tanggal = tanggal.trim(),
            babBahasan = bab.trim(),
            catatanMahasiswa = catatan.trim(),
            balasanDosen = "Menunggu review Dosen Pembimbing...",
            isAcc = false
        )
        cacheDao.insertOrUpdateBimbinganLog(newEntity)

        // Sinkronisasi ke Antrean Bimbingan Skripsi Dosen untuk Ahmad Fauzi
        _skripsiApprovalQueueState.update { list ->
            list.map { item ->
                if (item.nim == "2023010042") {
                    item.copy(
                        tahap = "Bimbingan Pertemuan Ke-$pertemuanKe ($bab)",
                        catatanTerbaruMahasiswa = catatan.trim(),
                        tanggalKirim = "Baru saja diajukan",
                        isAccBimbingan = false
                    )
                } else item
            }
        }
        _ahmadFauziSkripsiState.value = _skripsiApprovalQueueState.value.first { it.nim == "2023010042" }
        return Result.success(Unit)
    }

    override suspend fun updateLogBimbinganMahasiswa(
        logId: Long,
        pertemuanKe: Int,
        tanggal: String,
        bab: String,
        catatan: String
    ): Result<Unit> {
        if (bab.isBlank() || catatan.isBlank() || tanggal.isBlank()) {
            return Result.failure(IllegalArgumentException("Tanggal, Bab Bahasan, dan Catatan tidak boleh kosong!"))
        }
        val existing = _bimbinganLogsState.value.firstOrNull { it.id == logId }
            ?: return Result.failure(IllegalArgumentException("Data log bimbingan tidak ditemukan."))

        val updatedEntity = BimbinganLogEntity(
            id = existing.id,
            pertemuanKe = pertemuanKe.coerceAtLeast(1),
            tanggal = tanggal.trim(),
            babBahasan = bab.trim(),
            catatanMahasiswa = catatan.trim(),
            balasanDosen = existing.balasanDosen,
            isAcc = existing.isAcc
        )
        cacheDao.updateBimbinganLog(updatedEntity)
        return Result.success(Unit)
    }

    override suspend fun hapusLogBimbinganMahasiswa(logId: Long): Result<Unit> {
        cacheDao.deleteBimbinganLogById(logId)
        return Result.success(Unit)
    }

    // ==================== CRUD JADWAL KULIAH (ROOM SQLITE) ====================

    override suspend fun simpanJadwalKuliah(
        id: Long?,
        kodeMk: String,
        namaMk: String,
        sks: Int,
        kelas: String,
        hari: String,
        jamMulai: String,
        jamSelesai: String,
        ruangan: String,
        dosenPengajar: String,
        pertemuanKe: Int
    ): Result<Unit> {
        if (kodeMk.isBlank() || namaMk.isBlank() || ruangan.isBlank() || dosenPengajar.isBlank()) {
            return Result.failure(IllegalArgumentException("Kode MK, Nama MK, Ruangan, dan Dosen Pengajar wajib diisi!"))
        }
        val existing = id?.let { targetId -> _jadwalHariIniState.value.firstOrNull { it.id == targetId } }
        val entity = JadwalKuliahEntity(
            id = existing?.id ?: System.currentTimeMillis(),
            kodeMk = kodeMk.trim().uppercase(),
            namaMk = namaMk.trim(),
            sks = sks.coerceIn(1, 6),
            kelas = kelas.trim().ifBlank { "PAI-5A" },
            hari = hari.trim().ifBlank { "Selasa" },
            jamMulai = jamMulai.trim().ifBlank { "08:00" },
            jamSelesai = jamSelesai.trim().ifBlank { "10:30" },
            ruangan = ruangan.trim(),
            dosenPengajar = dosenPengajar.trim(),
            pertemuanKe = pertemuanKe.coerceAtLeast(1),
            sudahPresensiHariIni = existing?.sudahPresensiHariIni ?: false,
            persentaseKehadiran = existing?.persentaseKehadiran ?: 100
        )
        cacheDao.insertOrUpdateJadwal(entity)
        return Result.success(Unit)
    }

    override suspend fun hapusJadwalKuliah(jadwalId: Long): Result<Unit> {
        cacheDao.deleteJadwalById(jadwalId)
        return Result.success(Unit)
    }

    // ==================== DOSEN & KAPRODI ACTIONS ====================

    override suspend fun toggleBatchSelectionKrs(krsId: Long) {
        _krsApprovalQueueState.update { list ->
            list.map { item ->
                if (item.krsId == krsId) item.copy(isCheckedForBatch = !item.isCheckedForBatch) else item
            }
        }
    }

    override suspend fun selectAllPendingKrs(selectAll: Boolean) {
        _krsApprovalQueueState.update { list ->
            list.map { item ->
                if (item.status == KrsStatus.DIAJUKAN) item.copy(isCheckedForBatch = selectAll) else item
            }
        }
    }

    override suspend fun setujuiKrsMahasiswa(krsId: Long) {
        _krsApprovalQueueState.update { list ->
            list.map { item ->
                if (item.krsId == krsId) {
                    item.copy(
                        status = KrsStatus.DISETUJUI,
                        catatanWali = "KRS disetujui Dosen Wali. Selamat menempuh perkuliahan semester ganjil.",
                        isCheckedForBatch = false
                    )
                } else {
                    item
                }
            }
        }
        if (krsId == 901L) {
            _krsStudentStatusState.value = KrsStatus.DISETUJUI
            _krsCatatanWaliState.value = "KRS disetujui oleh Dr. KH. Ahmad Muzakki, M.Ag."
        }
    }

    override suspend fun setujuiBatchKrsTerpilih(): Int {
        var count = 0
        _krsApprovalQueueState.update { list ->
            list.map { item ->
                if (item.status == KrsStatus.DIAJUKAN && item.isCheckedForBatch) {
                    count++
                    if (item.krsId == 901L) {
                        _krsStudentStatusState.value = KrsStatus.DISETUJUI
                        _krsCatatanWaliState.value = "KRS disetujui melalui Batch Approval Dosen Wali."
                    }
                    item.copy(
                        status = KrsStatus.DISETUJUI,
                        catatanWali = "Disetujui serentak (Batch Approval) oleh Dosen Wali.",
                        isCheckedForBatch = false
                    )
                } else {
                    item
                }
            }
        }
        return count
    }

    override suspend fun tolakKrsMahasiswa(krsId: Long, catatanWali: String) {
        _krsApprovalQueueState.update { list ->
            list.map { item ->
                if (item.krsId == krsId) {
                    item.copy(
                        status = KrsStatus.DITOLAK,
                        catatanWali = catatanWali,
                        isCheckedForBatch = false
                    )
                } else {
                    item
                }
            }
        }
        if (krsId == 901L) {
            _krsStudentStatusState.value = KrsStatus.DITOLAK
            _krsCatatanWaliState.value = catatanWali
        }
    }

    override suspend fun accBimbinganSkripsiDosen(queueId: Long, catatanDosen: String) {
        _skripsiApprovalQueueState.update { list ->
            list.map { item ->
                if (item.id == queueId) {
                    val newCount = (item.jumlahBimbinganAcc + 1).coerceAtLeast(item.jumlahBimbinganAcc)
                    item.copy(
                        jumlahBimbinganAcc = newCount,
                        isAccBimbingan = true,
                        catatanTerbaruMahasiswa = "Catatan Dosen: $catatanDosen"
                    )
                } else {
                    item
                }
            }
        }
        _ahmadFauziSkripsiState.value = _skripsiApprovalQueueState.value.first { it.nim == "2023010042" }

        // Sinkronisasi Reaktif ke Buku Log Bimbingan Mahasiswa (Persist ke Room SQLite)
        val pendingLog = _bimbinganLogsState.value.firstOrNull { !it.isAcc }
        if (pendingLog != null) {
            val updated = pendingLog.copy(
                isAcc = true,
                balasanDosen = catatanDosen.ifBlank { "ACC Bimbingan. Lanjutkan penulisan ke tahap berikutnya." }
            )
            cacheDao.updateBimbinganLog(updated.toEntity())
        }
    }

    override suspend fun terbitkanAccSidangMunaqasyah(queueId: Long) {
        _skripsiApprovalQueueState.update { list ->
            list.map { item ->
                if (item.id == queueId || (item.nim == "2023010042" && item.jumlahBimbinganAcc >= item.targetMinimalBimbingan)) {
                    item.copy(
                        isAccBimbingan = true,
                        isAccSidangMunaqasyah = true,
                        tahap = "ACC Sidang Munaqasyah Diterbitkan"
                    )
                } else {
                    item
                }
            }
        }
        _ahmadFauziSkripsiState.value = _skripsiApprovalQueueState.value.first { it.nim == "2023010042" }
    }

    override suspend fun approveProposalKaprodi(queueId: Long, dosenPembimbing: String) {
        _skripsiApprovalQueueState.update { list ->
            list.map { item ->
                if (item.id == queueId) {
                    item.copy(
                        isProposalApprovedByKaprodi = true,
                        dosenPembimbingSaatIni = dosenPembimbing,
                        tahap = "Proposal Disetujui Kaprodi & Pembimbing Ditetapkan"
                    )
                } else {
                    item
                }
            }
        }
        _ahmadFauziSkripsiState.value = _skripsiApprovalQueueState.value.first { it.nim == "2023010042" }
    }

    override suspend fun updatePresensiManualMahasiswa(nim: String, status: String) {
        _pesertaKelasState.update { list ->
            list.map {
                if (it.nim == nim) it.copy(statusKehadiran = status) else it
            }
        }
    }

    override suspend fun tandaiSemuaHadirKelas() {
        _pesertaKelasState.update { list ->
            list.map { it.copy(statusKehadiran = "Hadir") }
        }
    }

    // ==================== CALON MAHASISWA BARU (PMB) CRUD ACTIONS ====================

    override suspend fun simpanBiodataPmb(
        namaLengkap: String,
        gelombang: String,
        pilihanProdi1: String,
        pilihanProdi2: String
    ): Result<Unit> {
        if (namaLengkap.isBlank() || pilihanProdi1.isBlank() || pilihanProdi2.isBlank()) {
            return Result.failure(IllegalArgumentException("Nama Lengkap, Pilihan Prodi 1, dan Pilihan Prodi 2 tidak boleh kosong!"))
        }
        val current = _pmbProgressState.value
        val updatedEntity = current.copy(
            namaLengkap = namaLengkap.trim(),
            gelombang = gelombang.trim().ifBlank { current.gelombang },
            pilihanProdi1 = pilihanProdi1.trim(),
            pilihanProdi2 = pilihanProdi2.trim()
        ).toBiodataEntity()

        cacheDao.upsertPmbBiodata(updatedEntity)
        return Result.success(Unit)
    }

    override suspend fun tambahAtauSimpanBerkasPmb(
        berkasId: Long?,
        namaBerkas: String,
        wajib: Boolean,
        status: String
    ): Result<Unit> {
        if (namaBerkas.isBlank()) {
            return Result.failure(IllegalArgumentException("Nama dokumen/berkas persyaratan tidak boleh kosong!"))
        }
        val entity = PmbBerkasEntity(
            id = berkasId ?: System.currentTimeMillis(),
            namaBerkas = namaBerkas.trim(),
            wajib = wajib,
            status = status,
            alasanPenolakan = null
        )
        cacheDao.insertOrUpdatePmbBerkas(entity)
        return Result.success(Unit)
    }

    override suspend fun hapusBerkasPmb(berkasId: Long): Result<Unit> {
        cacheDao.deletePmbBerkasById(berkasId)
        return Result.success(Unit)
    }

    override suspend fun reuploadBerkasPmb(berkasId: Long) {
        val target = _pmbProgressState.value.berkasList.firstOrNull { it.id == berkasId } ?: return
        cacheDao.insertOrUpdatePmbBerkas(
            PmbBerkasEntity(
                id = target.id,
                namaBerkas = target.namaBerkas,
                wajib = target.wajib,
                status = "Terverifikasi",
                alasanPenolakan = null
            )
        )
    }

    override suspend fun bayarDaftarUlangPmb() {
        val current = _pmbProgressState.value
        val updated = current.copy(
            currentStepIndex = 5,
            isDaftarUlangLunas = true,
            nimTerbit = "2026010194"
        )
        cacheDao.upsertPmbBiodata(updated.toBiodataEntity())
    }

    // ==================== ENTITY <-> DOMAIN MAPPERS ====================

    private fun JadwalKuliahItem.toEntity() = JadwalKuliahEntity(
        id = id,
        kodeMk = kodeMk,
        namaMk = namaMk,
        sks = sks,
        kelas = kelas,
        hari = hari,
        jamMulai = jamMulai,
        jamSelesai = jamSelesai,
        ruangan = ruangan,
        dosenPengajar = dosenPengajar,
        pertemuanKe = pertemuanKe,
        sudahPresensiHariIni = sudahPresensiHariIni,
        persentaseKehadiran = persentaseKehadiran
    )

    private fun JadwalKuliahEntity.toDomain() = JadwalKuliahItem(
        id = id,
        kodeMk = kodeMk,
        namaMk = namaMk,
        sks = sks,
        kelas = kelas,
        hari = hari,
        jamMulai = jamMulai,
        jamSelesai = jamSelesai,
        ruangan = ruangan,
        dosenPengajar = dosenPengajar,
        pertemuanKe = pertemuanKe,
        sudahPresensiHariIni = sudahPresensiHariIni,
        persentaseKehadiran = persentaseKehadiran
    )

    private fun BimbinganLogItem.toEntity() = BimbinganLogEntity(
        id = id,
        pertemuanKe = pertemuanKe,
        tanggal = tanggal,
        babBahasan = babBahasan,
        catatanMahasiswa = catatanMahasiswa,
        balasanDosen = balasanDosen,
        isAcc = isAcc
    )

    private fun BimbinganLogEntity.toDomain() = BimbinganLogItem(
        id = id,
        pertemuanKe = pertemuanKe,
        tanggal = tanggal,
        babBahasan = babBahasan,
        catatanMahasiswa = catatanMahasiswa,
        balasanDosen = balasanDosen,
        isAcc = isAcc
    )

    private fun PmbProgressState.toBiodataEntity() = PmbBiodataEntity(
        nomorPendaftaran = nomorPendaftaran,
        namaLengkap = namaLengkap,
        gelombang = gelombang,
        pilihanProdi1 = pilihanProdi1,
        pilihanProdi2 = pilihanProdi2,
        currentStepIndex = currentStepIndex,
        nilaiUjianSeleksi = nilaiUjianSeleksi,
        statusKelulusan = statusKelulusan,
        tagihanDaftarUlang = tagihanDaftarUlang,
        isDaftarUlangLunas = isDaftarUlangLunas,
        nimTerbit = nimTerbit
    )

    private fun PmbBiodataEntity.toDomain(berkasList: List<BerkasPmbItem>) = PmbProgressState(
        nomorPendaftaran = nomorPendaftaran,
        namaLengkap = namaLengkap,
        gelombang = gelombang,
        pilihanProdi1 = pilihanProdi1,
        pilihanProdi2 = pilihanProdi2,
        currentStepIndex = currentStepIndex,
        nilaiUjianSeleksi = nilaiUjianSeleksi,
        statusKelulusan = statusKelulusan,
        tagihanDaftarUlang = tagihanDaftarUlang,
        isDaftarUlangLunas = isDaftarUlangLunas,
        nimTerbit = nimTerbit,
        berkasList = berkasList
    )

    private fun BerkasPmbItem.toEntity() = PmbBerkasEntity(
        id = id,
        namaBerkas = namaBerkas,
        wajib = wajib,
        status = status,
        alasanPenolakan = alasanPenolakan
    )

    private fun PmbBerkasEntity.toDomain() = BerkasPmbItem(
        id = id,
        namaBerkas = namaBerkas,
        wajib = wajib,
        status = status,
        alasanPenolakan = alasanPenolakan
    )
}
