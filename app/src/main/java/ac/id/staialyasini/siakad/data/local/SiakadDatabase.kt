package ac.id.staialyasini.siakad.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ac.id.staialyasini.siakad.data.local.dao.SiakadCacheDao
import ac.id.staialyasini.siakad.data.local.entity.BimbinganLogEntity
import ac.id.staialyasini.siakad.data.local.entity.JadwalKuliahEntity
import ac.id.staialyasini.siakad.data.local.entity.KtmDigitalEntity
import ac.id.staialyasini.siakad.data.local.entity.PmbBerkasEntity
import ac.id.staialyasini.siakad.data.local.entity.PmbBiodataEntity

@Database(
    entities = [
        JadwalKuliahEntity::class,
        KtmDigitalEntity::class,
        BimbinganLogEntity::class,
        PmbBiodataEntity::class,
        PmbBerkasEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class SiakadDatabase : RoomDatabase() {
    abstract fun siakadCacheDao(): SiakadCacheDao
}
