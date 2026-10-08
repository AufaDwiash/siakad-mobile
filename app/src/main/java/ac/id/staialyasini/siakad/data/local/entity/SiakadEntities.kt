package ac.id.staialyasini.siakad.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_jadwal_kuliah")
data class JadwalKuliahEntity(
    @PrimaryKey val id: Long,
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
    val sudahPresensiHariIni: Boolean,
    val persentaseKehadiran: Int
)

@Entity(tableName = "cached_ktm_digital")
data class KtmDigitalEntity(
    @PrimaryKey val nim: String,
    val namaMahasiswa: String,
    val prodi: String,
    val angkatan: String,
    val masaBerlaku: String,
    val qrPayload: String,
    val updatedAtEpoch: Long
)

/**
 * Entity Room untuk CRUD Buku Log Bimbingan Skripsi Mahasiswa (Persisten di SQLite).
 */
@Entity(tableName = "bimbingan_log_skripsi")
data class BimbinganLogEntity(
    @PrimaryKey val id: Long,
    val pertemuanKe: Int,
    val tanggal: String,
    val babBahasan: String,
    val catatanMahasiswa: String,
    val balasanDosen: String?,
    val isAcc: Boolean
)

/**
 * Entity Room untuk menyimpan Biodata Pendaftar PMB secara permanen di SQLite.
 */
@Entity(tableName = "pmb_biodata")
data class PmbBiodataEntity(
    @PrimaryKey val nomorPendaftaran: String,
    val namaLengkap: String,
    val gelombang: String,
    val pilihanProdi1: String,
    val pilihanProdi2: String,
    val currentStepIndex: Int,
    val nilaiUjianSeleksi: Int?,
    val statusKelulusan: String,
    val tagihanDaftarUlang: Long,
    val isDaftarUlangLunas: Boolean,
    val nimTerbit: String?
)

/**
 * Entity Room untuk menyimpan daftar Berkas Persyaratan PMB secara permanen di SQLite.
 */
@Entity(tableName = "pmb_berkas")
data class PmbBerkasEntity(
    @PrimaryKey val id: Long,
    val namaBerkas: String,
    val wajib: Boolean,
    val status: String,
    val alasanPenolakan: String?
)
