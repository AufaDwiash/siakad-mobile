package ac.id.staialyasini.siakad.presentation.dosen.approval

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ac.id.staialyasini.siakad.core.ui.components.BadgeVariant
import ac.id.staialyasini.siakad.core.ui.components.MonoCodeChip
import ac.id.staialyasini.siakad.core.ui.components.PrimaryEmeraldButton
import ac.id.staialyasini.siakad.core.ui.components.SecondaryOutlineButton
import ac.id.staialyasini.siakad.core.ui.components.SectionHeader
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
import ac.id.staialyasini.siakad.core.ui.theme.StatusInfo
import ac.id.staialyasini.siakad.core.ui.theme.StatusInfoBg
import ac.id.staialyasini.siakad.core.ui.theme.StatusSuccess
import ac.id.staialyasini.siakad.core.ui.theme.StatusSuccessBg
import ac.id.staialyasini.siakad.core.ui.theme.StatusWarning
import ac.id.staialyasini.siakad.core.ui.theme.SurfaceBase
import ac.id.staialyasini.siakad.core.ui.theme.SurfaceCard
import ac.id.staialyasini.siakad.core.ui.theme.SurfaceMuted
import ac.id.staialyasini.siakad.core.ui.theme.TextMuted
import ac.id.staialyasini.siakad.core.ui.theme.TextPrimary
import ac.id.staialyasini.siakad.core.ui.theme.TextSecondary
import ac.id.staialyasini.siakad.domain.model.JadwalKuliahItem
import ac.id.staialyasini.siakad.domain.model.KrsPerwalianApprovalItem
import ac.id.staialyasini.siakad.domain.model.KrsStatus
import ac.id.staialyasini.siakad.domain.model.PesertaKelasPresensiItem
import ac.id.staialyasini.siakad.domain.model.SkripsiApprovalQueueItem
import ac.id.staialyasini.siakad.domain.model.UserRole
import ac.id.staialyasini.siakad.presentation.dosen.DosenViewModel
import ac.id.staialyasini.siakad.presentation.mahasiswa.dashboard.JadwalKuliahCrudDialog
import ac.id.staialyasini.siakad.presentation.mahasiswa.dashboard.QrMatrixIllustration
import androidx.compose.material.icons.filled.AddCircleOutline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosenDashboardApprovalScreen(
    viewModel: DosenViewModel,
    initialTabIndex: Int = 0
) {
    val session by viewModel.activeSession.collectAsStateWithLifecycle()
    val jadwalMengajar by viewModel.jadwalMengajarHariIni.collectAsStateWithLifecycle()
    val krsQueue by viewModel.krsApprovalQueue.collectAsStateWithLifecycle()
    val skripsiQueue by viewModel.skripsiApprovalQueue.collectAsStateWithLifecycle()
    val pesertaKelas by viewModel.pesertaKelasList.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()

    val isKaprodi = session?.role == UserRole.KAPRODI
    var selectedApprovalTab by remember(initialTabIndex) { mutableIntStateOf(initialTabIndex) }

    // Dialog Tolak & Minta Revisi (`catatan_wali`)
    var rejectTargetKrs by remember { mutableStateOf<KrsPerwalianApprovalItem?>(null) }
    var catatanWaliInput by remember { mutableStateOf("SKS terlalu padat untuk mata kuliah pilihan, mohon ganti PAI-519 ke semester depan.") }

    // Modal In-Class Teaching Companion & CRUD Jadwal Kuliah Dosen
    var showTeachingCompanionSheet by remember { mutableStateOf(false) }
    var showAddJadwalDosenDialog by remember { mutableStateOf(false) }
    var editingJadwalDosen by remember { mutableStateOf<JadwalKuliahItem?>(null) }

    if (showAddJadwalDosenDialog || editingJadwalDosen != null) {
        JadwalKuliahCrudDialog(
            initialItem = editingJadwalDosen,
            onDismiss = {
                showAddJadwalDosenDialog = false
                editingJadwalDosen = null
            },
            onSave = { id, kodeMk, namaMk, sks, kelas, hari, jamMulai, jamSelesai, ruangan, dosen, pertemuan ->
                viewModel.simpanJadwalKuliahDosen(
                    id = id,
                    kodeMk = kodeMk,
                    namaMk = namaMk,
                    sksStr = sks,
                    kelas = kelas,
                    hari = hari,
                    jamMulai = jamMulai,
                    jamSelesai = jamSelesai,
                    ruangan = ruangan,
                    dosenPengajar = dosen,
                    pertemuanKeStr = pertemuan,
                    onSuccessClose = {
                        showAddJadwalDosenDialog = false
                        editingJadwalDosen = null
                    }
                )
            }
        )
    }

    if (rejectTargetKrs != null) {
        val target = rejectTargetKrs!!
        AlertDialog(
            onDismissRequest = { rejectTargetKrs = null },
            title = {
                Text(
                    text = "Tolak & Minta Revisi KRS",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = StatusDanger,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Mahasiswa: ${target.namaMahasiswa} (${target.nim})",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    OutlinedTextField(
                        value = catatanWaliInput,
                        onValueChange = { catatanWaliInput = it },
                        label = { Text("Catatan Revisi Dosen Wali (catatan_wali)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                PrimaryEmeraldButton(
                    text = "Kirim Catatan Revisi",
                    containerColor = StatusDanger,
                    onClick = {
                        viewModel.tolakDanMintaRevisiKrs(
                            krsId = target.krsId,
                            namaMahasiswa = target.namaMahasiswa,
                            catatanWali = catatanWaliInput
                        )
                        rejectTargetKrs = null
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { rejectTargetKrs = null }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showTeachingCompanionSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTeachingCompanionSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            InClassTeachingCompanionSheet(
                pesertaList = pesertaKelas,
                onUpdateStatus = viewModel::ubahStatusPresensiMahasiswa,
                onMarkAllPresent = viewModel::tandaiSemuaHadir,
                onSaveJournal = { materi, metode ->
                    viewModel.simpanJurnalPerkuliahan(materi, metode)
                    showTeachingCompanionSheet = false
                },
                onClose = { showTeachingCompanionSheet = false }
            )
        }
    }

    val pendingKrsCount = krsQueue.count { it.status == KrsStatus.DIAJUKAN }
    val pendingSkripsiCount = skripsiQueue.count { !it.isAccSidangMunaqasyah }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBase)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 28.dp)
    ) {
        // 1. HEADER DOSEN / KAPRODI + RINGKASAN ANTREAN PENDING DENGAN BADGE MERAH
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(BrandPrimaryDark, BrandPrimary)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(BrandAccentLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isKaprodi) Icons.Default.SupervisorAccount else Icons.Default.Person,
                            contentDescription = null,
                            tint = BrandPrimaryDark,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatusBadge(
                                text = if (isKaprodi) "KAPRODI + DOSEN WALI" else "DOSEN PENGAJAR & WALI",
                                variant = BadgeVariant.ACCENT,
                                leadingDot = false
                            )
                            Surface(
                                color = Color.White.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "NIDN: ${session?.identifier ?: "2114087801"}",
                                    fontFamily = JetBrainsMonoFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = BrandAccentLight,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = session?.name ?: "Dr. KH. Ahmad Muzakki, M.Ag.",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = session?.prodi ?: "Program Studi S1 Pendidikan Agama Islam",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        )
                    }
                }

                // Quick Counter Cards dengan Badge Merah Pending
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedApprovalTab = 0 }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Antrean KRS Wali",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                                )
                                Text(
                                    text = "$pendingKrsCount Mahasiswa",
                                    fontFamily = JetBrainsMonoFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = BrandAccentLight
                                )
                            }
                            if (pendingKrsCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(StatusDanger),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = pendingKrsCount.toString(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedApprovalTab = 1 }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Antrean Skripsi",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                                )
                                Text(
                                    text = "$pendingSkripsiCount Bimbingan",
                                    fontFamily = JetBrainsMonoFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = BrandAccentLight
                                )
                            }
                            if (pendingSkripsiCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(StatusDanger),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = pendingSkripsiCount.toString(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (actionMessage != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StatusSuccessBg,
                    border = BorderStroke(1.dp, StatusSuccess.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = actionMessage ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BrandPrimaryDark,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = viewModel::dismissMessage,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // IN-CLASS TEACHING COMPANION & KELOLA JADWAL MENGAJAR (OTORITAS DOSEN / KAPRODI)
            SectionHeader(
                title = "Jadwal Mengajar & Kelola Kelas (${jadwalMengajar.size} Kelas)",
                subtitle = "Otoritas Dosen/Kaprodi: Tambah, Edit, Hapus Jadwal (Sinkron ke Mahasiswa)",
                actionText = "+ Jadwal Baru",
                onActionClick = { showAddJadwalDosenDialog = true }
            )

            jadwalMengajar.forEach { kelasItem ->
                SiakadCard(
                    containerColor = BrandPrimaryLight.copy(alpha = 0.65f),
                    borderColor = BrandPrimary.copy(alpha = 0.35f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            MonoCodeChip(
                                text = kelasItem.kodeMk,
                                backgroundColor = Color.White,
                                textColor = BrandPrimaryDark
                            )
                            MonoCodeChip(
                                text = "${kelasItem.sks} SKS • ${kelasItem.kelas}",
                                backgroundColor = Color.White,
                                textColor = BrandPrimaryDark
                            )
                        }
                        StatusBadge(
                            text = "Pertemuan #${kelasItem.pertemuanKe}",
                            variant = BadgeVariant.SUCCESS
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = kelasItem.namaMk,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${kelasItem.hari}, ${kelasItem.jamMulai} - ${kelasItem.jamSelesai} WIB • ${kelasItem.ruangan}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Text(
                        text = "Pengampu: ${kelasItem.dosenPengajar}",
                        style = MaterialTheme.typography.bodySmall.copy(color = BrandPrimaryDark)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PrimaryEmeraldButton(
                            text = "Jurnal & QR Kelas",
                            leadingIcon = Icons.Default.QrCode2,
                            modifier = Modifier.weight(1f),
                            height = 40.dp,
                            onClick = { showTeachingCompanionSheet = true }
                        )
                        SecondaryOutlineButton(
                            text = "Edit",
                            borderColor = BrandPrimary,
                            contentColor = BrandPrimaryDark,
                            height = 40.dp,
                            onClick = { editingJadwalDosen = kelasItem }
                        )
                        SecondaryOutlineButton(
                            text = "Hapus",
                            borderColor = StatusDanger,
                            contentColor = StatusDanger,
                            height = 40.dp,
                            onClick = {
                                viewModel.hapusJadwalKuliahDosen(kelasItem.id, kelasItem.namaMk)
                            }
                        )
                    }
                }
            }

            // 2. APPROVAL CENTER (PUSAT PERSETUJUAN 1-KLIK)
            SectionHeader(
                title = "Approval Center (Pusat Persetujuan 1-Klik)",
                subtitle = if (isKaprodi) {
                    "Mode Kaprodi Aktif: Perwalian KRS, Bimbingan Skripsi & Plotting Pembimbing"
                } else {
                    "Persetujuan cepat KRS Mahasiswa Wali & Logbook Bimbingan Skripsi"
                }
            )

            TabRow(
                selectedTabIndex = selectedApprovalTab,
                containerColor = SurfaceMuted,
                contentColor = BrandPrimary,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedApprovalTab == 0,
                    onClick = { selectedApprovalTab = 0 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Approval KRS Wali", fontWeight = FontWeight.SemiBold)
                            if (pendingKrsCount > 0) {
                                StatusBadge(
                                    text = pendingKrsCount.toString(),
                                    variant = BadgeVariant.DANGER,
                                    leadingDot = false
                                )
                            }
                        }
                    }
                )
                Tab(
                    selected = selectedApprovalTab == 1,
                    onClick = { selectedApprovalTab = 1 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Skripsi & Proposal", fontWeight = FontWeight.SemiBold)
                            if (pendingSkripsiCount > 0) {
                                StatusBadge(
                                    text = pendingSkripsiCount.toString(),
                                    variant = BadgeVariant.DANGER,
                                    leadingDot = false
                                )
                            }
                        }
                    }
                )
            }

            if (selectedApprovalTab == 0) {
                // TAB 1: APPROVAL KRS PERWALIAN (`krs`) DENGAN BATCH APPROVAL
                val checkedCount = krsQueue.count { it.status == KrsStatus.DIAJUKAN && it.isCheckedForBatch }

                SiakadCard(
                    containerColor = SurfaceCard,
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Checkbox(
                                checked = pendingKrsCount > 0 && checkedCount == pendingKrsCount,
                                onCheckedChange = { viewModel.selectAllPendingKrs(it) },
                                colors = CheckboxDefaults.colors(checkedColor = BrandPrimary)
                            )
                            Column {
                                Text(
                                    text = "Batch Approval KRS ($checkedCount Terpilih)",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "Centang beberapa mahasiswa untuk disetujui 1-klik",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        PrimaryEmeraldButton(
                            text = "Setujui ($checkedCount)",
                            leadingIcon = Icons.Default.DoneAll,
                            enabled = checkedCount > 0,
                            height = 38.dp,
                            onClick = viewModel::setujuiBatchKrsTerpilih
                        )
                    }
                }

                krsQueue.forEach { item ->
                    KrsPerwalianApprovalCard(
                        item = item,
                        onToggleBatch = { viewModel.toggleBatchKrsCheck(item.krsId) },
                        onApproveClick = {
                            viewModel.setujuiSatuKrs(item.krsId, item.namaMahasiswa)
                        },
                        onRejectClick = {
                            rejectTargetKrs = item
                        }
                    )
                }
            } else {
                // TAB 2: APPROVAL SKRIPSI & BIMBINGAN (+ PLOTTING PEMBIMBING KAPRODI)
                skripsiQueue.forEach { item ->
                    SkripsiApprovalCard(
                        item = item,
                        isKaprodi = isKaprodi,
                        onAccBimbingan = { catatan ->
                            viewModel.accBimbinganSkripsi(item.id, item.namaMahasiswa, catatan)
                        },
                        onTerbitkanAccSidang = {
                            viewModel.terbitkanAccSidangMunaqasyah(item.id, item.namaMahasiswa)
                        },
                        onApproveProposalKaprodi = { pembimbing ->
                            viewModel.setujuiProposalKaprodi(item.id, item.namaMahasiswa, pembimbing)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun KrsPerwalianApprovalCard(
    item: KrsPerwalianApprovalItem,
    onToggleBatch: () -> Unit,
    onApproveClick: () -> Unit,
    onRejectClick: () -> Unit
) {
    SiakadCard(contentPadding = PaddingValues(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (item.status == KrsStatus.DIAJUKAN) {
                    Checkbox(
                        checked = item.isCheckedForBatch,
                        onCheckedChange = { onToggleBatch() },
                        colors = CheckboxDefaults.colors(checkedColor = BrandPrimary)
                    )
                }
                Column {
                    Text(
                        text = item.namaMahasiswa,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MonoCodeChip(text = "NIM: ${item.nim}")
                        Text(
                            text = "• Smt ${item.semester} (${item.waktuPengajuan})",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            StatusBadge(
                text = item.status.code.uppercase(),
                variant = when (item.status) {
                    KrsStatus.DIAJUKAN -> BadgeVariant.WARNING
                    KrsStatus.DISETUJUI -> BadgeVariant.SUCCESS
                    KrsStatus.DITOLAK -> BadgeVariant.DANGER
                    KrsStatus.DRAFT -> BadgeVariant.NEUTRAL
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Ringkasan IPS Semester Lalu & Total SKS Diajukan
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = SurfaceMuted,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "IPS Lalu: ${String.format("%.2f", item.ipsLalu)}",
                    fontFamily = JetBrainsMonoFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Total SKS: ${item.totalSksDiajukan} / ${item.batasMaxSks} SKS",
                    fontFamily = JetBrainsMonoFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = BrandPrimaryDark
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Rincian Mata Kuliah Diajukan:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        item.rincianMatakuliah.forEach { mk ->
            Text(
                text = "• $mk",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
        }

        if (!item.catatanWali.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Catatan Wali: ${item.catatanWali}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (item.status == KrsStatus.DITOLAK) StatusDanger else BrandPrimaryDark,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        if (item.status == KrsStatus.DIAJUKAN) {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Tombol Setujui KRS (Emerald #0D7C66)
                PrimaryEmeraldButton(
                    text = "Setujui KRS",
                    leadingIcon = Icons.Default.Check,
                    containerColor = BrandPrimary,
                    modifier = Modifier.weight(1f),
                    height = 42.dp,
                    onClick = onApproveClick
                )

                // Tombol Tolak & Minta Revisi (Danger #EF4444)
                PrimaryEmeraldButton(
                    text = "Tolak & Revisi",
                    leadingIcon = Icons.Default.Close,
                    containerColor = StatusDanger,
                    modifier = Modifier.weight(1f),
                    height = 42.dp,
                    onClick = onRejectClick
                )
            }
        }
    }
}

@Composable
private fun SkripsiApprovalCard(
    item: SkripsiApprovalQueueItem,
    isKaprodi: Boolean,
    onAccBimbingan: (String) -> Unit,
    onTerbitkanAccSidang: () -> Unit,
    onApproveProposalKaprodi: (String) -> Unit
) {
    var catatanKoreksi by remember { mutableStateOf("Revisi sudah sesuai pedoman karya ilmiah STAI Al-Yasini. Lanjutkan.") }
    var selectedPembimbing by remember { mutableStateOf("Dr. KH. Ahmad Muzakki, M.Ag.") }

    SiakadCard(contentPadding = PaddingValues(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                MonoCodeChip(text = "NIM: ${item.nim}")
                MonoCodeChip(
                    text = "Bimbingan: ${item.jumlahBimbinganAcc}/${item.targetMinimalBimbingan}x",
                    backgroundColor = if (item.jumlahBimbinganAcc >= 8) StatusSuccessBg else BrandAccentLight,
                    textColor = if (item.jumlahBimbinganAcc >= 8) BrandPrimaryDark else Color(0xFF92400E)
                )
            }
            StatusBadge(
                text = when {
                    item.isAccSidangMunaqasyah -> "ACC SIDANG MUNAQASYAH"
                    item.jumlahBimbinganAcc >= 8 -> "SIAP ACC SIDANG (8/8)"
                    else -> "PROSES BIMBINGAN"
                },
                variant = if (item.isAccSidangMunaqasyah || item.jumlahBimbinganAcc >= 8) {
                    BadgeVariant.SUCCESS
                } else {
                    BadgeVariant.WARNING
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.namaMahasiswa,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = item.judulSkripsi,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Tahap: ${item.tahap} • ${item.tanggalKirim}",
            style = MaterialTheme.typography.bodySmall.copy(color = BrandPrimaryDark)
        )
        Text(
            text = "\"${item.catatanTerbaruMahasiswa}\"",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Fitur Khusus Kaprodi: Persetujuan Proposal & Plotting Dosen Pembimbing
        if (isKaprodi && !item.isProposalApprovedByKaprodi) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BrandAccentLight.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, BrandAccent.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Otoritas Kaprodi: Plotting Dosen Pembimbing Skripsi",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    OutlinedTextField(
                        value = selectedPembimbing,
                        onValueChange = { selectedPembimbing = it },
                        label = { Text("Tetapkan Dosen Pembimbing") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    PrimaryEmeraldButton(
                        text = "Setujui Judul Proposal & Tetapkan Pembimbing",
                        leadingIcon = Icons.Default.SupervisorAccount,
                        onClick = { onApproveProposalKaprodi(selectedPembimbing) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Kontrol Dosen Pembimbing: Catatan Koreksi, ACC Bimbingan, dan Terbitkan ACC Sidang Munaqasyah (>= 8x)
        if (!item.isAccSidangMunaqasyah) {
            OutlinedTextField(
                value = catatanKoreksi,
                onValueChange = { catatanKoreksi = it },
                label = { Text("Catatan Koreksi Dosen Pembimbing") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PrimaryEmeraldButton(
                    text = "ACC Bimbingan",
                    leadingIcon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f),
                    height = 42.dp,
                    onClick = { onAccBimbingan(catatanKoreksi) }
                )

                if (item.jumlahBimbinganAcc >= item.targetMinimalBimbingan) {
                    PrimaryEmeraldButton(
                        text = "Terbitkan ACC Sidang",
                        leadingIcon = Icons.Default.Verified,
                        containerColor = BrandAccent,
                        modifier = Modifier.weight(1f),
                        height = 42.dp,
                        onClick = onTerbitkanAccSidang
                    )
                }
            }
        }
    }
}

/**
 * In-Class Teaching Companion (Jurnal Perkuliahan & 2 Mode Presensi: QR Code Kelas / Checklist Manual Cepat)
 */
@Composable
private fun InClassTeachingCompanionSheet(
    pesertaList: List<PesertaKelasPresensiItem>,
    onUpdateStatus: (String, String) -> Unit,
    onMarkAllPresent: () -> Unit,
    onSaveJournal: (String, String) -> Unit,
    onClose: () -> Unit
) {
    var selectedMode by remember { mutableIntStateOf(0) } // 0 = QR Code Kelas, 1 = Checklist Manual Cepat
    var materiPokok by remember { mutableStateOf("Konsep Maqashid Syariah dalam Fatwa Ekonomi Kontemporer") }
    var metodeKuliah by remember { mutableStateOf("Ceramah Interaktif & Bahtsul Masail") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "In-Class Teaching Companion",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "PAI-501 • Pertemuan Ke-7 • Ruang R.204",
                    fontFamily = JetBrainsMonoFamily,
                    fontSize = 11.sp,
                    color = BrandPrimary
                )
            }
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
            }
        }

        // Form Jurnal Perkuliahan
        SiakadCard(contentPadding = PaddingValues(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Input Jurnal Perkuliahan (jurnal_perkuliahans)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                SecondaryOutlineButton(
                    text = "Auto-Fill",
                    leadingIcon = Icons.Default.AutoFixHigh,
                    height = 32.dp,
                    onClick = {
                        materiPokok = "Metodologi Penelitian Tindakan Kelas (PTK) dalam Pembelajaran PAI"
                        metodeKuliah = "Diskusi Kelompok & Praktik Penyusunan Siklus"
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = materiPokok,
                onValueChange = { materiPokok = it },
                label = { Text("Materi Pokok Perkuliahan") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = metodeKuliah,
                onValueChange = { metodeKuliah = it },
                label = { Text("Metode Perkuliahan") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )
        }

        TabRow(
            selectedTabIndex = selectedMode,
            containerColor = SurfaceMuted,
            contentColor = BrandPrimary,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedMode == 0,
                onClick = { selectedMode = 0 },
                text = { Text("Mode 1: Tampilkan QR Kelas", fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedMode == 1,
                onClick = { selectedMode = 1 },
                text = { Text("Mode 2: Checklist Manual", fontWeight = FontWeight.SemiBold) }
            )
        }

        var qrSessionToken by remember { mutableStateOf("QR-ALYASINI-PAI501-PERT7") }

        if (selectedMode == 0) {
            SiakadCard(
                containerColor = BrandPrimaryLight,
                borderColor = BrandPrimary.copy(alpha = 0.35f),
                contentPadding = PaddingValues(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Tampilkan QR Ini di Layar / Proyektor Kelas",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = BrandPrimaryDark,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(2.dp, BrandPrimary),
                        modifier = Modifier.size(160.dp)
                    ) {
                        Box(modifier = Modifier.padding(14.dp)) {
                            QrMatrixIllustration(seed = qrSessionToken)
                        }
                    }
                    MonoCodeChip(
                        text = "SESSION TOKEN: $qrSessionToken",
                        backgroundColor = Color.White,
                        textColor = BrandPrimaryDark
                    )
                    SecondaryOutlineButton(
                        text = "Refresh Token QR (Anti-Titip)",
                        leadingIcon = Icons.Default.Sync,
                        height = 36.dp,
                        onClick = {
                            qrSessionToken = "QR-ALYASINI-PAI501-PERT7-${System.currentTimeMillis() % 1000}"
                        }
                    )
                    Text(
                        text = "Mahasiswa memindai QR ini melalui menu Scan Presensi (Validasi GPS Radius 150m).",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            // Mode 2: Checklist Manual Cepat (Hadir, Izin, Sakit, Alpha + Tandai Semua Hadir)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daftar Peserta Kelas (${pesertaList.size} Mahasiswa)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                SecondaryOutlineButton(
                    text = "Tandai Semua Hadir",
                    leadingIcon = Icons.Default.DoneAll,
                    borderColor = BrandPrimary,
                    contentColor = BrandPrimaryDark,
                    height = 36.dp,
                    onClick = onMarkAllPresent
                )
            }

            val statuses = listOf("Hadir", "Izin", "Sakit", "Alpha")
            pesertaList.forEach { mhs ->
                SiakadCard(contentPadding = PaddingValues(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = mhs.nama,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = mhs.nim,
                                fontFamily = JetBrainsMonoFamily,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            statuses.forEach { st ->
                                val active = mhs.statusKehadiran == st
                                val chipColor = when {
                                    !active -> SurfaceMuted
                                    st == "Hadir" -> StatusSuccess
                                    st == "Izin" -> StatusInfo
                                    st == "Sakit" -> StatusWarning
                                    else -> StatusDanger
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = chipColor,
                                    modifier = Modifier.clickable { onUpdateStatus(mhs.nim, st) }
                                ) {
                                    Text(
                                        text = st.first().toString(),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = if (active) Color.White else TextSecondary,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        PrimaryEmeraldButton(
            text = "Simpan Jurnal & Rekap Presensi Kelas",
            leadingIcon = Icons.Default.FactCheck,
            modifier = Modifier.fillMaxWidth(),
            onClick = { onSaveJournal(materiPokok, metodeKuliah) }
        )
    }
}
