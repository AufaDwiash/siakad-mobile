package ac.id.staialyasini.siakad.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import ac.id.staialyasini.siakad.domain.model.UserRole
import ac.id.staialyasini.siakad.domain.model.UserSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.siakadDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "siakad_alyasini_prefs"
)

/**
 * Jetpack DataStore untuk menyimpan Sanctum Bearer Token, User Session, Role Aktif,
 * dan preferensi Biometric Login.
 */
@Singleton
class SessionDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val AUTH_TOKEN = stringPreferencesKey("sanctum_bearer_token")
        val USER_ID = longPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val IDENTIFIER = stringPreferencesKey("user_identifier") // NIM / NIDN / No. PMB
        val EMAIL = stringPreferencesKey("user_email")
        val PRODI = stringPreferencesKey("user_prodi")
        val USER_ROLE = stringPreferencesKey("user_role")
        val ACADEMIC_STATUS = stringPreferencesKey("academic_status")
        val SEMESTER = intPreferencesKey("semester")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val LAST_ROLE_KEY = stringPreferencesKey("last_role_key")
    }

    // Volatile cached token for synchronous OkHttp AuthInterceptor header injection
    @Volatile
    var cachedToken: String? = null
        private set

    val activeSessionFlow: Flow<UserSession?> = context.siakadDataStore.data.map { prefs ->
        val token = prefs[Keys.AUTH_TOKEN]
        cachedToken = token
        if (token.isNullOrBlank()) {
            null
        } else {
            UserSession(
                userId = prefs[Keys.USER_ID] ?: 1L,
                name = prefs[Keys.USER_NAME] ?: "Ahmad Fauzi",
                identifier = prefs[Keys.IDENTIFIER] ?: "2023010042",
                email = prefs[Keys.EMAIL] ?: "ahmad.fauzi@mhs.staialyasini.ac.id",
                prodi = prefs[Keys.PRODI] ?: "Pendidikan Agama Islam (S1)",
                role = UserRole.fromKey(prefs[Keys.USER_ROLE] ?: UserRole.MAHASISWA.key),
                academicStatus = prefs[Keys.ACADEMIC_STATUS] ?: "Aktif",
                semester = prefs[Keys.SEMESTER] ?: 5,
                token = token
            )
        }
    }

    val isBiometricEnabledFlow: Flow<Boolean> = context.siakadDataStore.data.map { prefs ->
        prefs[Keys.BIOMETRIC_ENABLED] ?: true
    }

    val savedRoleForBiometricFlow: Flow<UserRole> = context.siakadDataStore.data.map { prefs ->
        UserRole.fromKey(prefs[Keys.LAST_ROLE_KEY] ?: UserRole.MAHASISWA.key)
    }

    suspend fun saveSession(session: UserSession) {
        cachedToken = session.token
        context.siakadDataStore.edit { prefs ->
            prefs[Keys.AUTH_TOKEN] = session.token
            prefs[Keys.USER_ID] = session.userId
            prefs[Keys.USER_NAME] = session.name
            prefs[Keys.IDENTIFIER] = session.identifier
            prefs[Keys.EMAIL] = session.email
            prefs[Keys.PRODI] = session.prodi
            prefs[Keys.USER_ROLE] = session.role.key
            prefs[Keys.ACADEMIC_STATUS] = session.academicStatus
            prefs[Keys.SEMESTER] = session.semester
            prefs[Keys.LAST_ROLE_KEY] = session.role.key
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.siakadDataStore.edit { prefs ->
            prefs[Keys.BIOMETRIC_ENABLED] = enabled
        }
    }

    suspend fun getSavedRole(): UserRole {
        val prefs = context.siakadDataStore.data.first()
        return UserRole.fromKey(prefs[Keys.LAST_ROLE_KEY] ?: UserRole.MAHASISWA.key)
    }

    suspend fun clearSession() {
        cachedToken = null
        context.siakadDataStore.edit { prefs ->
            prefs.remove(Keys.AUTH_TOKEN)
        }
    }
}
