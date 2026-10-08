package ac.id.staialyasini.siakad.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ac.id.staialyasini.siakad.core.ui.components.BadgeVariant
import ac.id.staialyasini.siakad.core.ui.components.MonoCodeChip
import ac.id.staialyasini.siakad.core.ui.components.PrimaryEmeraldButton
import ac.id.staialyasini.siakad.core.ui.components.SecondaryOutlineButton
import ac.id.staialyasini.siakad.core.ui.components.SiakadCard
import ac.id.staialyasini.siakad.core.ui.components.StatusBadge
import ac.id.staialyasini.siakad.core.ui.theme.BorderDefault
import ac.id.staialyasini.siakad.core.ui.theme.BrandAccent
import ac.id.staialyasini.siakad.core.ui.theme.BrandAccentLight
import ac.id.staialyasini.siakad.core.ui.theme.BrandPrimary
import ac.id.staialyasini.siakad.core.ui.theme.BrandPrimaryDark
import ac.id.staialyasini.siakad.core.ui.theme.BrandPrimaryLight
import ac.id.staialyasini.siakad.core.ui.theme.JetBrainsMonoFamily
import ac.id.staialyasini.siakad.core.ui.theme.StatusDanger
import ac.id.staialyasini.siakad.core.ui.theme.StatusDangerBg
import ac.id.staialyasini.siakad.core.ui.theme.SurfaceBase
import ac.id.staialyasini.siakad.core.ui.theme.SurfaceMuted
import ac.id.staialyasini.siakad.core.ui.theme.TextMuted
import ac.id.staialyasini.siakad.core.ui.theme.TextPrimary
import ac.id.staialyasini.siakad.core.ui.theme.TextSecondary
import ac.id.staialyasini.siakad.core.util.BiometricHelper
import ac.id.staialyasini.siakad.domain.model.UserRole

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (UserRole) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsStateWithLifecycle()
    val savedBiometricRole by viewModel.savedBiometricRole.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is AuthUiEffect.NavigateToRoleHome -> onLoginSuccess(effect.role)
                is AuthUiEffect.ShowToast -> {}
            }
        }
    }

    // Dialog Penawaran Aktivasi Biometric Login setelah login pertama berhasil
    if (uiState.showOfferBiometricDialog && uiState.pendingSession != null) {
        AlertDialog(
            onDismissRequest = { viewModel.confirmBiometricOffer(false) },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(BrandPrimaryLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Aktifkan Login Biometrik?",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Halo ${uiState.pendingSession?.name}, aktifkan Sidik Jari / Face Unlock agar Anda dapat masuk lebih cepat pada sesi berikutnya tanpa mengetik password.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                PrimaryEmeraldButton(
                    text = "Aktifkan Sekarang",
                    onClick = { viewModel.confirmBiometricOffer(true) }
                )
            },
            dismissButton = {
                TextButton(onClick = { viewModel.confirmBiometricOffer(false) }) {
                    Text("Nanti Saja", color = TextSecondary)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBase)
            .verticalScroll(rememberScrollState())
    ) {
        // Header Islami Modern STAI Al-Yasini Pasuruan
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(BrandPrimaryDark, BrandPrimary)
                    )
                )
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, BrandAccent.copy(alpha = 0.7f)),
                    modifier = Modifier.size(68.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = "Logo STAI Al-Yasini",
                            tint = BrandAccentLight,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                StatusBadge(
                    text = "COMPANION APP RESMI • LARAVEL 13 SANCTUM",
                    variant = BadgeVariant.ACCENT,
                    leadingDot = false
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "SIAKAD STAI Al-Yasini",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Pondok Pesantren Terpadu Al-Yasini Pasuruan",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = BrandPrimaryLight.copy(alpha = 0.9f)
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quick Role Switcher Card (untuk pengujian 4 role lapangan)
            SiakadCard(
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Pilih Simulasi Role Pengguna",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Klik untuk mengisi otomatis kredensial tiap role Spatie",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    MonoCodeChip(text = uiState.selectedDemoRole.key)
                }

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    UserRole.entries.forEach { role ->
                        val isSelected = uiState.selectedDemoRole == role
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) BrandPrimaryLight else SurfaceMuted,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) BrandPrimary else BorderDefault
                            ),
                            modifier = Modifier.clickable { viewModel.onSelectDemoRole(role) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = if (isSelected) BrandPrimary else TextMuted,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = role.displayName,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isSelected) BrandPrimaryDark else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Form Login Utama (/api/v1/auth/login)
            SiakadCard(
                contentPadding = PaddingValues(20.dp)
            ) {
                Text(
                    text = "Masuk ke Akun Akademik",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Endpoint: POST /api/v1/auth/login",
                    style = MaterialTheme.typography.labelSmall.copy(color = BrandPrimary)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.errorMessage != null) {
                    Surface(
                        color = StatusDangerBg,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, StatusDanger.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = StatusDanger,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Text(
                    text = "Email / NIM / NIDN / No. Pendaftaran",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.identifier,
                    onValueChange = viewModel::onIdentifierChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = JetBrainsMonoFamily,
                        fontWeight = FontWeight.SemiBold
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = BrandPrimary
                        )
                    },
                    placeholder = { Text("Contoh: 2023010042 / NIDN / Email") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = BorderDefault,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = SurfaceBase
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Kata Sandi SIAKAD",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = BrandPrimary
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = "Toggle Password",
                                tint = TextMuted
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = BorderDefault,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = SurfaceBase
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                PrimaryEmeraldButton(
                    text = "Masuk ke Portal (${uiState.selectedDemoRole.displayName})",
                    onClick = viewModel::submitPasswordLogin,
                    isLoading = uiState.isLoading,
                    leadingIcon = Icons.Default.Login,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tombol Biometric Login (Fingerprint / Face Unlock)
                SecondaryOutlineButton(
                    text = if (isBiometricEnabled) {
                        "Login Cepat Biometrik (${savedBiometricRole.displayName})"
                    } else {
                        "Gunakan Biometrik (Sidik Jari / Wajah)"
                    },
                    leadingIcon = Icons.Default.Fingerprint,
                    borderColor = BrandPrimary,
                    contentColor = BrandPrimaryDark,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val activity = context as? FragmentActivity
                        if (activity != null && BiometricHelper.canAuthenticate(context)) {
                            BiometricHelper.showBiometricPrompt(
                                activity = activity,
                                onSuccess = { viewModel.onBiometricAuthSuccess() },
                                onError = { msg -> viewModel.onBiometricAuthError(msg) }
                            )
                        } else {
                            // Fallback jika emulator tidak memiliki hardware sensor biometrik terdaftar
                            viewModel.onBiometricAuthSuccess()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(BrandPrimaryLight)
                        .border(1.dp, BrandPrimary.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Dilindungi enkripsi Bearer Token Laravel Sanctum & penyimpanan Offline-First Room Database.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BrandPrimaryDark,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}
