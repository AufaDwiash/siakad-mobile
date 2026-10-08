package ac.id.staialyasini.siakad.domain.repository

import ac.id.staialyasini.siakad.domain.model.BimbinganLogItem
import ac.id.staialyasini.siakad.domain.model.JadwalKuliahItem
import ac.id.staialyasini.siakad.domain.model.KelasDitawarkanItem
import ac.id.staialyasini.siakad.domain.model.KrsPerwalianApprovalItem
import ac.id.staialyasini.siakad.domain.model.KrsStatus
import ac.id.staialyasini.siakad.domain.model.PesertaKelasPresensiItem
import ac.id.staialyasini.siakad.domain.model.PmbProgressState
import ac.id.staialyasini.siakad.domain.model.SemesterKhsItem
import ac.id.staialyasini.siakad.domain.model.SkripsiApprovalQueueItem
import ac.id.staialyasini.siakad.domain.model.TagihanUktSummary
import ac.id.staialyasini.siakad.domain.model.UserRole
import ac.id.staialyasini.siakad.domain.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SiakadRepository {
    val activeSessionFlow: Flow<UserSession?>
    val isBiometricEnabledFlow: Flow<Boolean>
    val savedRoleForBiometricFlow: Flow<UserRole>

    // Reactive shared states across Mahasiswa, Keuangan, Dosen, and PMB screens (backed by Room SQLite)
    val tagihanUktState: StateFlow<TagihanUktSummary>
    val jadwalHariIniState: StateFlow<List<JadwalKuliahItem>>
    val krsStudentStatusState: StateFlow<KrsStatus>
    val krsCatatanWaliState: StateFlow<String?>
    val kelasDitawarkanState: StateFlow<List<KelasDitawarkanItem>>
    val khsSemesterListState: StateFlow<List<SemesterKhsItem>>
    val bimbinganLogsState: StateFlow<List<BimbinganLogItem>>
    val ahmadFauziSkripsiState: StateFlow<SkripsiApprovalQueueItem>

    // Dosen & Kaprodi reactive states
    val krsApprovalQueueState: StateFlow<List<KrsPerwalianApprovalItem>>
    val skripsiApprovalQueueState: StateFlow<List<SkripsiApprovalQueueItem>>
    val pesertaKelasState: StateFlow<List<PesertaKelasPresensiItem>>

    // PMB reactive state
    val pmbProgressState: StateFlow<PmbProgressState>

    // Auth Actions
    suspend fun login(identifier: String, password: String, preferredRole: UserRole? = null): Result<UserSession>
    suspend fun loginWithBiometric(): Result<UserSession>
    suspend fun setBiometricEnabled(enabled: Boolean)
    suspend fun switchDemoRole(role: UserRole): UserSession
    suspend fun logout()

    // Mahasiswa Actions & CRUD Buku Log Bimbingan Skripsi (Persisted in Room)
    suspend fun bayarCicilanAtauLunasUkt(terminId: Long?, bayarPenuh: Boolean, metode: String)
    suspend fun toggleSimulasiStatusUkt(lunas: Boolean)
    suspend fun togglePilihKelasKrs(kelasId: Long): Result<Unit>
    suspend fun ajukanKrsMahasiswa(): Result<Unit>
    suspend fun resetKrsToDraft(): Result<Unit>
    suspend fun catatPresensiQrMahasiswa(jadwalId: Long, qrToken: String, lat: Double, lng: Double, isMockLocation: Boolean = false): Result<String>

    suspend fun tambahLogBimbinganMahasiswa(
        pertemuanKe: Int,
        tanggal: String,
        bab: String,
        catatan: String
    ): Result<Unit>

    suspend fun updateLogBimbinganMahasiswa(
        logId: Long,
        pertemuanKe: Int,
        tanggal: String,
        bab: String,
        catatan: String
    ): Result<Unit>

    suspend fun hapusLogBimbinganMahasiswa(logId: Long): Result<Unit>

    // CRUD Jadwal Kuliah (Persisted in Room `cached_jadwal_kuliah`)
    suspend fun simpanJadwalKuliah(
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
    ): Result<Unit>

    suspend fun hapusJadwalKuliah(jadwalId: Long): Result<Unit>

    // Dosen & Kaprodi Actions
    suspend fun toggleBatchSelectionKrs(krsId: Long)
    suspend fun selectAllPendingKrs(selectAll: Boolean)
    suspend fun setujuiKrsMahasiswa(krsId: Long)
    suspend fun setujuiBatchKrsTerpilih(): Int
    suspend fun tolakKrsMahasiswa(krsId: Long, catatanWali: String)
    suspend fun accBimbinganSkripsiDosen(queueId: Long, catatanDosen: String)
    suspend fun terbitkanAccSidangMunaqasyah(queueId: Long)
    suspend fun approveProposalKaprodi(queueId: Long, dosenPembimbing: String)
    suspend fun updatePresensiManualMahasiswa(nim: String, status: String)
    suspend fun tandaiSemuaHadirKelas()

    // Calon Mahasiswa (PMB) CRUD Biodata & Berkas (Persisted in Room `pmb_biodata` & `pmb_berkas`)
    suspend fun simpanBiodataPmb(
        namaLengkap: String,
        gelombang: String,
        pilihanProdi1: String,
        pilihanProdi2: String
    ): Result<Unit>

    suspend fun tambahAtauSimpanBerkasPmb(
        berkasId: Long?,
        namaBerkas: String,
        wajib: Boolean,
        status: String = "Terverifikasi"
    ): Result<Unit>

    suspend fun hapusBerkasPmb(berkasId: Long): Result<Unit>
    suspend fun reuploadBerkasPmb(berkasId: Long)
    suspend fun bayarDaftarUlangPmb()
}
