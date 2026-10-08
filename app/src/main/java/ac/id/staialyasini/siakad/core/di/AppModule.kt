package ac.id.staialyasini.siakad.core.di

import android.content.Context
import androidx.room.Room
import ac.id.staialyasini.siakad.BuildConfig
import ac.id.staialyasini.siakad.data.local.SiakadDatabase
import ac.id.staialyasini.siakad.data.local.dao.SiakadCacheDao
import ac.id.staialyasini.siakad.data.remote.SiakadApiService
import ac.id.staialyasini.siakad.data.remote.interceptor.SanctumAuthInterceptor
import ac.id.staialyasini.siakad.data.repository.SiakadRepositoryImpl
import ac.id.staialyasini.siakad.domain.repository.SiakadRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppNetworkAndDatabaseModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        sanctumAuthInterceptor: SanctumAuthInterceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(sanctumAuthInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideSiakadApiService(retrofit: Retrofit): SiakadApiService {
        return retrofit.create(SiakadApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSiakadDatabase(
        @ApplicationContext context: Context
    ): SiakadDatabase {
        return Room.databaseBuilder(
            context,
            SiakadDatabase::class.java,
            "siakad_alyasini_offline.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideSiakadCacheDao(database: SiakadDatabase): SiakadCacheDao {
        return database.siakadCacheDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindingModule {

    @Binds
    @Singleton
    abstract fun bindSiakadRepository(
        impl: SiakadRepositoryImpl
    ): SiakadRepository
}
