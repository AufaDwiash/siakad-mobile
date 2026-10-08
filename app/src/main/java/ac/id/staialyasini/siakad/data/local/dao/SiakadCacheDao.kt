package ac.id.staialyasini.siakad.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ac.id.staialyasini.siakad.data.local.entity.BimbinganLogEntity
import ac.id.staialyasini.siakad.data.local.entity.JadwalKuliahEntity
import ac.id.staialyasini.siakad.data.local.entity.KtmDigitalEntity
import ac.id.staialyasini.siakad.data.local.entity.PmbBerkasEntity
import ac.id.staialyasini.siakad.data.local.entity.PmbBiodataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SiakadCacheDao {

    // ==================== 1. CRUD JADWAL KULIAH ====================

    @Query("SELECT * FROM cached_jadwal_kuliah ORDER BY jamMulai ASC, id ASC")
    fun observeCachedJadwal(): Flow<List<JadwalKuliahEntity>>

    @Query("SELECT COUNT(*) FROM cached_jadwal_kuliah")
    suspend fun countJadwal(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertJadwalList(items: List<JadwalKuliahEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateJadwal(item: JadwalKuliahEntity)

    @Query("DELETE FROM cached_jadwal_kuliah WHERE id = :jadwalId")
    suspend fun deleteJadwalById(jadwalId: Long)

    // ==================== 2. CRUD LOG BIMBINGAN SKRIPSI ====================

    @Query("SELECT * FROM bimbingan_log_skripsi ORDER BY pertemuanKe ASC, id ASC")
    fun observeBimbinganLogs(): Flow<List<BimbinganLogEntity>>

    @Query("SELECT COUNT(*) FROM bimbingan_log_skripsi")
    suspend fun countBimbinganLogs(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBimbinganLogList(items: List<BimbinganLogEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBimbinganLog(item: BimbinganLogEntity)

    @Update
    suspend fun updateBimbinganLog(item: BimbinganLogEntity)

    @Query("DELETE FROM bimbingan_log_skripsi WHERE id = :logId")
    suspend fun deleteBimbinganLogById(logId: Long)

    // ==================== 3. CRUD BIODATA & BERKAS PMB ====================

    @Query("SELECT * FROM pmb_biodata LIMIT 1")
    fun observePmbBiodata(): Flow<PmbBiodataEntity?>

    @Query("SELECT COUNT(*) FROM pmb_biodata")
    suspend fun countPmbBiodata(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPmbBiodata(entity: PmbBiodataEntity)

    @Query("SELECT * FROM pmb_berkas ORDER BY id ASC")
    fun observePmbBerkasList(): Flow<List<PmbBerkasEntity>>

    @Query("SELECT COUNT(*) FROM pmb_berkas")
    suspend fun countPmbBerkas(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPmbBerkasList(items: List<PmbBerkasEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePmbBerkas(item: PmbBerkasEntity)

    @Query("DELETE FROM pmb_berkas WHERE id = :berkasId")
    suspend fun deletePmbBerkasById(berkasId: Long)

    // ==================== 4. CACHE KTM DIGITAL ====================

    @Query("SELECT * FROM cached_ktm_digital WHERE nim = :nim LIMIT 1")
    fun observeKtmByNim(nim: String): Flow<KtmDigitalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertKtm(entity: KtmDigitalEntity)
}
