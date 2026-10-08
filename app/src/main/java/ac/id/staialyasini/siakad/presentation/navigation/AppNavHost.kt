package ac.id.staialyasini.siakad.presentation.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ac.id.staialyasini.siakad.core.ui.theme.BorderDefault
import ac.id.staialyasini.siakad.core.ui.theme.BrandAccentLight
import ac.id.staialyasini.siakad.core.ui.theme.BrandPrimary
import ac.id.staialyasini.siakad.core.ui.theme.BrandPrimaryDark
import ac.id.staialyasini.siakad.core.ui.theme.BrandPrimaryLight
import ac.id.staialyasini.siakad.core.ui.theme.SurfaceCard
import ac.id.staialyasini.siakad.core.ui.theme.TextMuted
import ac.id.staialyasini.siakad.core.ui.theme.TextPrimary
import ac.id.staialyasini.siakad.domain.model.UserRole
import ac.id.staialyasini.siakad.presentation.auth.AuthViewModel
import ac.id.staialyasini.siakad.presentation.auth.LoginScreen
import ac.id.staialyasini.siakad.presentation.dosen.DosenViewModel
import ac.id.staialyasini.siakad.presentation.dosen.approval.DosenDashboardApprovalScreen
import ac.id.staialyasini.siakad.presentation.mahasiswa.MahasiswaViewModel
import ac.id.staialyasini.siakad.presentation.mahasiswa.akademik.MahasiswaAkademikSkripsiScreen
import ac.id.staialyasini.siakad.presentation.mahasiswa.dashboard.MahasiswaDashboardScreen
import ac.id.staialyasini.siakad.presentation.mahasiswa.keuangan.UktPaymentScreen
import ac.id.staialyasini.siakad.presentation.mahasiswa.krs.MahasiswaKrsScreen
import ac.id.staialyasini.siakad.presentation.pmb.PmbTrackerScreen
import ac.id.staialyasini.siakad.presentation.pmb.PmbViewModel

object SiakadDestinations {
    const val LOGIN = "login"
    const val MAIN_PORTAL = "main_portal/{roleKey}"
    fun mainPortalRoute(role: UserRole) = "main_portal/${role.key}"
}

private data class RoleBottomNavItem(
    val label: String,
    val icon: ImageVector
)

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = SiakadDestinations.LOGIN
    ) {
        composable(SiakadDestinations.LOGIN) {
            val authViewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { role ->
                    navController.navigate(SiakadDestinations.mainPortalRoute(role)) {
                        popUpTo(SiakadDestinations.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(SiakadDestinations.MAIN_PORTAL) { backStackEntry ->
            val roleKey = backStackEntry.arguments?.getString("roleKey") ?: UserRole.MAHASISWA.key
            val initialRole = UserRole.fromKey(roleKey)

            MainRolePortalScaffold(
                initialRole = initialRole,
                onLogout = {
                    navController.navigate(SiakadDestinations.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainRolePortalScaffold(
    initialRole: UserRole,
    onLogout: () -> Unit
) {
    val mahasiswaViewModel: MahasiswaViewModel = hiltViewModel()
    val dosenViewModel: DosenViewModel = hiltViewModel()
    val pmbViewModel: PmbViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()

    var currentRole by rememberSaveable { mutableStateOf(initialRole) }
    var selectedBottomIndex by rememberSaveable(currentRole) { mutableIntStateOf(0) }
    var showRoleMenu by remember { mutableStateOf(false) }

    val bottomNavItems = remember(currentRole) {
        when (currentRole) {
            UserRole.MAHASISWA -> listOf(
                RoleBottomNavItem("Beranda", Icons.Default.Dashboard),
                RoleBottomNavItem("Keuangan UKT", Icons.Default.AccountBalanceWallet),
                RoleBottomNavItem("Kontrak KRS", Icons.Default.EditCalendar),
                RoleBottomNavItem("KHS & Skripsi", Icons.Default.MenuBook)
            )
            UserRole.DOSEN, UserRole.KAPRODI -> listOf(
                RoleBottomNavItem("Approval KRS", Icons.Default.AssignmentTurnedIn),
                RoleBottomNavItem("Skripsi & Prodi", Icons.Default.FactCheck),
                RoleBottomNavItem("Simulasi Mhs", Icons.Default.School)
            )
            UserRole.CALON_MAHASISWA -> listOf(
                RoleBottomNavItem("Tracker PMB", Icons.Default.HowToReg),
                RoleBottomNavItem("Pembayaran", Icons.Default.AccountBalanceWallet)
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "SIAKAD STAI Al-Yasini",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Role Aktif: ${currentRole.displayName}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BrandAccentLight,
                                fontSize = 11.sp
                            )
                        )
                    }
                },
                actions = {
                    // Quick Role Switcher di TopBar agar penguji dapat berpindah role dengan 1 klik
                    Box {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .clickable { showRoleMenu = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Ganti Role",
                                    tint = BrandAccentLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Ganti Role",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            UserRole.entries.forEach { roleOption ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = roleOption.displayName,
                                            fontWeight = if (roleOption == currentRole) FontWeight.Bold else FontWeight.Normal,
                                            color = if (roleOption == currentRole) BrandPrimary else TextPrimary
                                        )
                                    },
                                    onClick = {
                                        showRoleMenu = false
                                        authViewModel.onSelectDemoRole(roleOption)
                                        authViewModel.submitPasswordLogin()
                                        currentRole = roleOption
                                        selectedBottomIndex = 0
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Keluar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrandPrimaryDark
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceCard,
                tonalElevation = 4.dp
            ) {
                bottomNavItems.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedBottomIndex == index,
                        onClick = { selectedBottomIndex = index },
                        icon = {
                            Icon(
                                imageVector = navItem.icon,
                                contentDescription = navItem.label
                            )
                        },
                        label = {
                            Text(
                                text = navItem.label,
                                fontWeight = if (selectedBottomIndex == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandPrimaryDark,
                            selectedTextColor = BrandPrimaryDark,
                            indicatorColor = BrandPrimaryLight,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRole) {
                UserRole.MAHASISWA -> {
                    when (selectedBottomIndex) {
                        0 -> MahasiswaDashboardScreen(
                            viewModel = mahasiswaViewModel,
                            onNavigateToPayment = { selectedBottomIndex = 1 },
                            onNavigateToKrs = { selectedBottomIndex = 2 },
                            onNavigateToAkademik = { selectedBottomIndex = 3 }
                        )
                        1 -> UktPaymentScreen(viewModel = mahasiswaViewModel)
                        2 -> MahasiswaKrsScreen(
                            viewModel = mahasiswaViewModel,
                            onNavigateToPayment = { selectedBottomIndex = 1 }
                        )
                        else -> MahasiswaAkademikSkripsiScreen(viewModel = mahasiswaViewModel)
                    }
                }

                UserRole.DOSEN, UserRole.KAPRODI -> {
                    when (selectedBottomIndex) {
                        0 -> DosenDashboardApprovalScreen(
                            viewModel = dosenViewModel,
                            initialTabIndex = 0
                        )
                        1 -> DosenDashboardApprovalScreen(
                            viewModel = dosenViewModel,
                            initialTabIndex = 1
                        )
                        else -> MahasiswaDashboardScreen(
                            viewModel = mahasiswaViewModel,
                            onNavigateToPayment = {
                                currentRole = UserRole.MAHASISWA
                                selectedBottomIndex = 1
                            },
                            onNavigateToKrs = {
                                currentRole = UserRole.MAHASISWA
                                selectedBottomIndex = 2
                            },
                            onNavigateToAkademik = {
                                currentRole = UserRole.MAHASISWA
                                selectedBottomIndex = 3
                            }
                        )
                    }
                }

                UserRole.CALON_MAHASISWA -> {
                    when (selectedBottomIndex) {
                        0 -> PmbTrackerScreen(viewModel = pmbViewModel)
                        else -> UktPaymentScreen(viewModel = mahasiswaViewModel)
                    }
                }
            }
        }
    }
}
