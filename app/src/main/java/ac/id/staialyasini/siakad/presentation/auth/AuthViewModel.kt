package ac.id.staialyasini.siakad.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ac.id.staialyasini.siakad.domain.model.UserRole
import ac.id.staialyasini.siakad.domain.model.UserSession
import ac.id.staialyasini.siakad.domain.repository.SiakadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val identifier: String = "2023010042",
    val password: String = "alyasini2026",
    val selectedDemoRole: UserRole = UserRole.MAHASISWA,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showOfferBiometricDialog: Boolean = false,
    val pendingSession: UserSession? = null
)

sealed interface AuthUiEffect {
    data class NavigateToRoleHome(val role: UserRole) : AuthUiEffect
    data class ShowToast(val message: String) : AuthUiEffect
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: SiakadRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AuthUiEffect>()
    val uiEffect: SharedFlow<AuthUiEffect> = _uiEffect.asSharedFlow()

    val isBiometricEnabled: StateFlow<Boolean> = repository.isBiometricEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val savedBiometricRole: StateFlow<UserRole> = repository.savedRoleForBiometricFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserRole.MAHASISWA)

    fun onIdentifierChange(value: String) {
        _uiState.update { it.copy(identifier = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onSelectDemoRole(role: UserRole) {
        val presetIdentifier = when (role) {
            UserRole.MAHASISWA -> "2023010042"
            UserRole.DOSEN -> "2114087801"
            UserRole.KAPRODI -> "0701058002"
            UserRole.CALON_MAHASISWA -> "PMB-2026-GEL2-0194"
        }
        _uiState.update {
            it.copy(
                selectedDemoRole = role,
                identifier = presetIdentifier,
                password = "alyasini2026",
                errorMessage = null
            )
        }
    }

    fun submitPasswordLogin() {
        val current = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.login(
                identifier = current.identifier,
                password = current.password,
                preferredRole = current.selectedDemoRole
            )
            result.fold(
                onSuccess = { session ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            showOfferBiometricDialog = true,
                            pendingSession = session
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Gagal masuk ke server SIAKAD."
                        )
                    }
                }
            )
        }
    }

    fun confirmBiometricOffer(enableBiometric: Boolean) {
        val session = _uiState.value.pendingSession ?: return
        viewModelScope.launch {
            repository.setBiometricEnabled(enableBiometric)
            _uiState.update { it.copy(showOfferBiometricDialog = false, pendingSession = null) }
            _uiEffect.emit(AuthUiEffect.NavigateToRoleHome(session.role))
        }
    }

    fun onBiometricAuthSuccess() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.loginWithBiometric()
            result.onSuccess { session ->
                _uiState.update { it.copy(isLoading = false) }
                _uiEffect.emit(AuthUiEffect.NavigateToRoleHome(session.role))
            }
        }
    }

    fun onBiometricAuthError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }
}
