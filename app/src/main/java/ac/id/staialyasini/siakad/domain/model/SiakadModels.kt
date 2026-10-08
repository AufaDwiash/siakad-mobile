package ac.id.staialyasini.siakad.domain.model

/**
 * 4 Role Pengguna Lapangan SIAKAD STAI Al-Yasini Mobile (Spatie Roles & user_type)
 */
enum class UserRole(val key: String, val displayName: String) {
    MAHASISWA("mahasiswa", "Mahasiswa Aktif"),
    DOSEN("dosen", "Dosen Pengajar & Wali"),
    KAPRODI("kaprodi", "Ketua Program Studi"),
    CALON_MAHASISWA("calon_mahasiswa", "Calon Mahasiswa (PMB)");

    companion object {
        fun fromKey(key: String): UserRole =
            entries.firstOrNull { it.key.equals(key, ignoreCase = true) } ?: MAHASISWA
    }
}

data class UserSession(
    val userId: Long,
    val name: String,
    val identifier: String, // NIM / NIDN / No. Pendaftaran PMB
    val email: String,
    val prodi: String,
    val role: UserRole,
    val academicStatus: String = "Aktif",
    val semester: Int = 5,
    val token: String,
    val avatarUrl: String? = null
)

// ==================== MODUL A: MAHASISWA ====================

data class JadwalKuliahItem(
    val id: Long,
    val kodeMk: String,
    val namaMk: String,
    val sks: Int,
    val kelas: String,
    val hari: String,
    val jamMulai: String,
    val jamSelesai: String,
    val ruangan: String,
    val dosenPengajar: String,
    val pertemuanKe: Int,
    val sudahPresensiHariIni: Boolean = false,
    val persentaseKehadiran: Int = 92
)

data class CicilanTagihanItem(
    val id: Long,
    val terminKe: Int,
    val keterangan: String,
    val nominal: Long,
    val jatuhTempo: String,
    val isPaid: Boolean,
    val tanggalBayar: String? = null,
    val metodeBayar: String? = null
)

data class TagihanUktSummary(
    val id: Long,
    val nomorInvoice: String,
    val tahunAjaran: String,
    val namaTagihan: String,
    val totalTagihan: Long,
    val totalTerbayar: Long,
    val statusLunas: Boolean,
    val virtualAccountBsi: String,
    val virtualAccountBri: String,
    val snapToken: String,
    val cicilanList: List<CicilanTagihanItem>
) {
    val sisaTagihan: Long get() = (totalTagihan - totalTerbayar).coerceAtLeast(0L)
}

enum class KrsStatus(val code: String, val label: String) {
    DRAFT("draft", "Draft"),
    DIAJUKAN("diajukan", "Menunggu Approval Dosen Wali"),
    DISETUJUI("disetujui", "Disetujui Dosen Wali"),
    DITOLAK("ditolak", "Revisi Dosen Wali")
}

data class KelasDitawarkanItem(
    val id: Long,
    val kodeMk: String,
    val namaMk: String,
    val sks: Int,
    val semesterPaket: Int,
    val kelas: String,
    val hari: String,
    val jamMulai: String,
    val jamSelesai: String,
    val ruangan: String,
    val namaDosen: String,
    val kuotaTerisi: Int,
    val kuotaMaksimal: Int,
    val isSelected: Boolean = false
) {
    val sisaKuota: Int get() = (kuotaMaksimal - kuotaTerisi).coerceAtLeast(0)
}

data class SemesterKhsItem(
    val semester: Int,
    val tahunAjaran: String,
    val ips: Double,
    val sksSemester: Int,
    val matakuliahNilai: List<NilaiMatakuliahItem>
)

data class NilaiMatakuliahItem(
    val kodeMk: String,
    val namaMk: String,
    val sks: Int,
    val nilaiAngka: Double,
    val nilaiHuruf: String,
    val indeksBobot: Double
)

data class BimbinganLogItem(
    val id: Long,
    val pertemuanKe: Int,
    val tanggal: String,
    val babBahasan: String,
    val catatanMahasiswa: String,
    val balasanDosen: String?,
    val isAcc: Boolean
)

// ==================== MODUL B: DOSEN & KAPRODI ====================

data class KrsPerwalianApprovalItem(
    val krsId: Long,
    val nim: String,
    val namaMahasiswa: String,
    val prodi: String,
    val semester: Int,
    val ipsLalu: Double,
    val batasMaxSks: Int,
    val totalSksDiajukan: Int,
    val waktuPengajuan: String,
    val status: KrsStatus,
    val catatanWali: String? = null,
    val rincianMatakuliah: List<String>,
    val isCheckedForBatch: Boolean = false
)

data class SkripsiApprovalQueueItem(
    val id: Long,
    val nim: String,
    val namaMahasiswa: String,
    val judulSkripsi: String,
    val tahap: String, // "Bimbingan Bab IV" atau "Pengajuan Proposal (Kaprodi)"
    val jumlahBimbinganAcc: Int,
    val targetMinimalBimbingan: Int = 8,
    val catatanTerbaruMahasiswa: String,
    val tanggalKirim: String,
    val dosenPembimbingSaatIni: String? = null,
    val isAccBimbingan: Boolean = false,
    val isAccSidangMunaqasyah: Boolean = false,
    val isProposalApprovedByKaprodi: Boolean = false
)

data class PesertaKelasPresensiItem(
    val nim: String,
    val nama: String,
    val statusKehadiran: String // "Hadir", "Izin", "Sakit", "Alpha"
)

// ==================== MODUL C: CALON MAHASISWA (PMB) ====================

data class BerkasPmbItem(
    val id: Long,
    val namaBerkas: String,
    val wajib: Boolean,
    val status: String, // "Terverifikasi", "Ditolak", "Belum Upload", "Menunggu Verifikasi"
    val alasanPenolakan: String? = null
)

data class PmbProgressState(
    val nomorPendaftaran: String,
    val namaLengkap: String,
    val gelombang: String,
    val pilihanProdi1: String,
    val pilihanProdi2: String,
    val currentStepIndex: Int, // 0..5
    val nilaiUjianSeleksi: Int? = 84,
    val statusKelulusan: String = "LULUS SELEKSI",
    val tagihanDaftarUlang: Long = 2_450_000L,
    val isDaftarUlangLunas: Boolean = false,
    val nimTerbit: String? = null,
    val berkasList: List<BerkasPmbItem>
)
