package ac.id.staialyasini.siakad.presentation.mahasiswa.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ac.id.staialyasini.siakad.core.ui.components.BadgeVariant
import ac.id.staialyasini.siakad.core.ui.components.MonoCodeChip
import ac.id.staialyasini.siakad.core.ui.components.OfficialDocumentPreviewDialog
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
import ac.id.staialyasini.siakad.core.ui.theme.StatusWarningBg
import ac.id.staialyasini.siakad.core.ui.theme.SurfaceBase
import ac.id.staialyasini.siakad.core.ui.theme.SurfaceMuted
import ac.id.staialyasini.siakad.core.ui.theme.TextMuted
import ac.id.staialyasini.siakad.core.ui.theme.TextPrimary
import ac.id.staialyasini.siakad.core.ui.theme.TextSecondary
import ac.id.staialyasini.siakad.core.util.CurrencyFormatter
import ac.id.staialyasini.siakad.domain.model.JadwalKuliahItem
import ac.id.staialyasini.siakad.domain.model.KrsStatus
import ac.id.staialyasini.siakad.presentation.mahasiswa.MahasiswaViewModel
import ac.id.staialyasini.siakad.presentation.mahasiswa.QrSimulationMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MahasiswaDashboardScreen(
    viewModel: MahasiswaViewModel,
    onNavigateToPayment: () -> Unit,
    onNavigateToKrs: () -> Unit,
    onNavigateToAkademik: () -> Unit
) {
    val session by viewModel.activeSession.collectAsStateWithLifecycle()
    val tagihan by viewModel.tagihanUkt.collectAsStateWithLifecycle()
    val jadwalList by viewModel.jadwalHariIni.collectAsStateWithLifecycle()
    val krsStatus by viewModel.krsStatus.collectAsStateWithLifecycle()
    val feedbackBanner by viewModel.feedbackBanner.collectAsStateWithLifecycle()
    val errorBanner by viewModel.errorBanner.collectAsStateWithLifecycle()

    var showKtmAndExamSheet by remember { mutableStateOf(false) }
    var activeQrScanJadwal by remember { mutableStateOf<JadwalKuliahItem?>(null) }
    var showKtmPdfPreview by remember { mutableStateOf(false) }
    var showExamCardPdfPreview by remember { mutableStateOf(false) }

    // Dialog Cetak PDF Resmi KTM Digital
    if (showKtmPdfPreview) {
        OfficialDocumentPreviewDialog(
            documentTitle = "KARTU TANDA MAHASISWA ELEKTRONIK (E-KTM)",
            documentNumber = "KTM-2023010042/STAI-ASY/2026",
            studentName = session?.name ?: "Ahmad Fauzi",
            studentIdentifier = session?.identifier ?: "2023010042",
            prodi = session?.prodi ?: "S1 Pendidikan Agama Islam (PAI)",
            extraMetadata = listOf(
                "Jenjang" to "Strata Satu (S1)",
                "Semester" to "${session?.semester ?: 5}",
                "Status Mahasiswa" to "AKTIF TERDAFTAR (DIKTI)"
            ),
            itemsTable = listOf(
                Triple("Perpustakaan", "Akses Repository & Peminjaman", "Aktif"),
                Triple("Laboratorium", "Komputer & Microteaching", "Aktif"),
                Triple("Portal SIAKAD", "Single Sign-On (SSO)", "Aktif"),
                Triple("Presensi GPS", "Validasi Kampus 150m Anti-Mock", "Aktif")
            ),
            tableHeader = Triple("Layanan", "Keterangan Fasilitas", "Status"),
            totalSummary = "Status Keanggotaan" to "BERLAKU S/D 2027",
            signatoryTitle = "Wakil Ketua I Bidang Akademik",
            signatoryName = "Dr. KH. Ahmad Muzakki, M.Ag.",
            signatoryNidn = "NIDN: 2114087801",
            onDismiss = { showKtmPdfPreview = false }
        )
    }

    // Dialog Cetak PDF Resmi Kartu Peserta Ujian UTS/UAS
    if (showExamCardPdfPreview) {
        OfficialDocumentPreviewDialog(
            documentTitle = "KARTU PESERTA UJIAN AKHIR SEMESTER (UAS)",
            documentNumber = "KPU-UAS/2026-GANJIL/042",
            studentName = session?.name ?: "Ahmad Fauzi",
            studentIdentifier = session?.identifier ?: "2023010042",
            prodi = session?.prodi ?: "S1 Pendidikan Agama Islam (PAI)",
            extraMetadata = listOf(
                "No. Kursi Ujian" to "PAI-5A-014",
                "Status UKT" to "LUNAS TERVERIFIKASI (100%)",
                "Tahun Akademik" to "Semester Ganjil 2026/2027"
            ),
            itemsTable = jadwalList.map { mk ->
                Triple(mk.kodeMk, "${mk.namaMk} (${mk.ruangan})", "${mk.sks} SKS")
            },
            tableHeader = Triple("Kode MK", "Mata Kuliah & Ruang", "Beban SKS"),
            totalSummary = "Total Mata Uji" to "${jadwalList.size} Mata Kuliah",
            signatoryTitle = "Ketua Panitia Ujian Semester",
            signatoryName = "Dr. Hj. Siti Marifah, M.Pd.I.",
            signatoryNidn = "NIDN: 0701058002",
            onDismiss = { showExamCardPdfPreview = false }
        )
    }

    // State untuk CRUD Tambah / Edit / Hapus Jadwal Kuliah (Room Database)
    var showAddJadwalDialog by remember { mutableStateOf(false) }
    var editingJadwalItem by remember { mutableStateOf<JadwalKuliahItem?>(null) }
    var deletingJadwalItem by remember { mutableStateOf<JadwalKuliahItem?>(null) }

    // Dialog Form Tambah / Edit & Simpan Jadwal Kuliah ke Room SQLite
    if (showAddJadwalDialog || editingJadwalItem != null) {
        JadwalKuliahCrudDialog(
            initialItem = editingJadwalItem,
            onDismiss = {
                showAddJadwalDialog = false
                editingJadwalItem = null
            },
            onSave = { id, kodeMk, namaMk, sks, kelas, hari, jamMulai, jamSelesai, ruangan, dosen, pertemuan ->
                viewModel.simpanJadwalKuliah(
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
                        showAddJadwalDialog = false
                        editingJadwalItem = null
                    }
                )
            }
        )
    }

    // Dialog Konfirmasi Hapus Jadwal Kuliah
    if (deletingJadwalItem != null) {
        val target = deletingJadwalItem!!
        AlertDialog(
            onDismissRequest = { deletingJadwalItem = null },
            title = {
                Text(
                    text = "Hapus Jadwal Kuliah?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = StatusDanger,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus jadwal '${target.kodeMk} - ${target.namaMk}' secara permanen dari tabel cached_jadwal_kuliah di Room Database?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                PrimaryEmeraldButton(
                    text = "Ya, Hapus Jadwal",
                    containerColor = StatusDanger,
                    onClick = {
                        viewModel.hapusJadwalKuliah(target.id, target.namaMk)
                        deletingJadwalItem = null
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { deletingJadwalItem = null }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Modal KTM Digital & Kartu Ujian UTS/UAS
    if (showKtmAndExamSheet) {
        ModalBottomSheet(
            onDismissRequest = { showKtmAndExamSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            KtmAndExamCardSheetContent(
                nama = session?.name ?: "Ahmad Fauzi",
                nim = session?.identifier ?: "2023010042",
                prodi = session?.prodi ?: "S1 Pendidikan Agama Islam (PAI)",
                semester = session?.semester ?: 5,
                isUktLunas = tagihan.statusLunas,
                sisaTunggakan = tagihan.sisaTagihan,
                jadwalUjian = jadwalList,
                onDownloadKtmPdf = {
                    showKtmAndExamSheet = false
                    showKtmPdfPreview = true
                },
                onDownloadExamCardPdf = {
                    showKtmAndExamSheet = false
                    showExamCardPdfPreview = true
                },
                onPayNowClick = {
                    showKtmAndExamSheet = false
                    onNavigateToPayment()
                },
                onClose = { showKtmAndExamSheet = false }
            )
        }
    }

    // Modal CameraX + ML Kit QR Scanner & Geolocation Campus Validation
    if (activeQrScanJadwal != null) {
        val targetJadwal = activeQrScanJadwal!!
        QrAttendanceScannerDialog(
            jadwal = targetJadwal,
            onDismiss = { activeQrScanJadwal = null },
            onScanMode = { mode ->
                viewModel.scanQrPresensiKelas(
                    jadwalId = targetJadwal.id,
                    mode = mode
                )
                activeQrScanJadwal = null
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBase)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 28.dp)
    ) {
        // 1. HEADER SAPAAN, FOTO PROFIL, NIM (FONT MONO), PRODI & STATUS AKADEMIK
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
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(BrandAccentLight)
                                .border(2.dp, BrandAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Foto Profil Mahasiswa",
                                tint = BrandPrimaryDark,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Assalamu'alaikum,",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BrandPrimaryLight.copy(alpha = 0.85f)
                                )
                            )
                            Text(
                                text = session?.name ?: "Ahmad Fauzi",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.16f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "NIM: ${session?.identifier ?: "2023010042"}",
                                        fontFamily = JetBrainsMonoFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = BrandAccentLight,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                StatusBadge(
                                    text = session?.academicStatus ?: "Aktif",
                                    variant = BadgeVariant.SUCCESS
                                )
                            }
                            Text(
                                text = "${session?.prodi ?: "S1 Pendidikan Agama Islam"} • Semester ${session?.semester ?: 5}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            )
                        }
                    }
                }

                // Tombol Akses Cepat KTM Digital & Kartu Ujian
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showKtmAndExamSheet = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                tint = BrandAccentLight,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "KTM Digital & Kartu Ujian",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BrandAccent,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                val firstUnattended = jadwalList.firstOrNull { !it.sudahPresensiHariIni }
                                    ?: jadwalList.firstOrNull()
                                activeQrScanJadwal = firstUnattended
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Scan QR Kelas",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            )
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
            // Feedback Banner Sukses / Error dari Room Database CRUD
            if (feedbackBanner != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StatusSuccessBg,
                    border = BorderStroke(1.dp, StatusSuccess.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = StatusSuccess,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = feedbackBanner ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BrandPrimaryDark,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        IconButton(
                            onClick = viewModel::dismissBanner,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = BrandPrimaryDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            if (errorBanner != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StatusDangerBg,
                    border = BorderStroke(1.dp, StatusDanger.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = errorBanner ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF991B1B),
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = viewModel::dismissBanner, modifier = Modifier.size(24.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // 2. QUICK SUMMARY CARDS (IPK KUMULATIF, TOTAL SKS LULUS, STATUS TAGIHAN UKT)
            SectionHeader(
                title = "Ringkasan Akademik & Keuangan",
                subtitle = "Tahun Akademik ${tagihan.tahunAjaran}",
                actionText = if (tagihan.statusLunas) "Simulasikan Belum Lunas" else "Simulasikan Lunas",
                onActionClick = { viewModel.toggleSimulasiStatusUkt(!tagihan.statusLunas) }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SiakadCard(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(14.dp),
                    onClick = onNavigateToAkademik
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(text = "IPK Kumulatif", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "3.78",
                        fontFamily = JetBrainsMonoFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = BrandPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    StatusBadge(text = "Cumlaude", variant = BadgeVariant.SUCCESS)
                }

                SiakadCard(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(14.dp),
                    onClick = onNavigateToAkademik
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Assignment,
                            contentDescription = null,
                            tint = BrandAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(text = "Total SKS", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "110 / 144",
                        fontFamily = JetBrainsMonoFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    StatusBadge(text = "Syarat Skripsi OK", variant = BadgeVariant.INFO)
                }
            }

            // Card Status Tagihan UKT Semester Aktif + Guard Logic
            SiakadCard(
                containerColor = if (tagihan.statusLunas) StatusSuccessBg else StatusWarningBg,
                borderColor = if (tagihan.statusLunas) StatusSuccess.copy(alpha = 0.4f) else StatusWarning.copy(alpha = 0.45f),
                contentPadding = PaddingValues(16.dp),
                onClick = onNavigateToPayment
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (tagihan.statusLunas) StatusSuccess.copy(alpha = 0.15f)
                                    else StatusWarning.copy(alpha = 0.2f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (tagihan.statusLunas) {
                                    Icons.Default.CheckCircle
                                } else {
                                    Icons.Default.AccountBalanceWallet
                                },
                                contentDescription = null,
                                tint = if (tagihan.statusLunas) StatusSuccess else Color(0xFFB45309)
                            )
                        }
                        Column {
                            Text(
                                text = "Status Tagihan UKT Semester Aktif",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = if (tagihan.statusLunas) {
                                    "Seluruh komponen UKT Lunas (${CurrencyFormatter.formatRupiah(tagihan.totalTagihan)})"
                                } else {
                                    "Sisa Tagihan Termin 3: ${CurrencyFormatter.formatRupiah(tagihan.sisaTagihan)}"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }

                    StatusBadge(
                        text = if (tagihan.statusLunas) "LUNAS" else "BELUM LUNAS",
                        variant = if (tagihan.statusLunas) BadgeVariant.SUCCESS else BadgeVariant.WARNING
                    )
                }

                if (!tagihan.statusLunas) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = StatusWarning.copy(alpha = 0.25f))
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = StatusDanger,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Cekal Keuangan Aktif: Pengajuan KRS & Kartu Ujian dikunci hingga UKT dilunasi.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF991B1B),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        PrimaryEmeraldButton(
                            text = "Bayar UKT",
                            onClick = onNavigateToPayment,
                            height = 38.dp
                        )
                    }
                }
            }

            // Status Kontrak KRS Shortcut Card
            SiakadCard(
                contentPadding = PaddingValues(14.dp),
                onClick = onNavigateToKrs
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (tagihan.statusLunas) Icons.Default.EditCalendar else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (tagihan.statusLunas) BrandPrimary else StatusDanger
                        )
                        Column {
                            Text(
                                text = "Kontrak KRS Semester Ganjil",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = if (!tagihan.statusLunas) {
                                    "Terkunci (Cekal Keuangan UKT)"
                                } else {
                                    "Status KRS: ${krsStatus.label}"
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    StatusBadge(
                        text = if (!tagihan.statusLunas) "TERKUNCI" else krsStatus.code.uppercase(),
                        variant = when {
                            !tagihan.statusLunas -> BadgeVariant.DANGER
                            krsStatus == KrsStatus.DISETUJUI -> BadgeVariant.SUCCESS
                            krsStatus == KrsStatus.DIAJUKAN -> BadgeVariant.INFO
                            krsStatus == KrsStatus.DITOLAK -> BadgeVariant.DANGER
                            else -> BadgeVariant.NEUTRAL
                        }
                    )
                }
            }

            // 3. TODAY'S CLASS SCHEDULE (READ-ONLY UNTUK MAHASISWA + TOMBOL SCAN PRESENSI QR)
            SectionHeader(
                title = "Jadwal Kuliah Hari Ini (${jadwalList.size} Kelas)",
                subtitle = "Tersinkronisasi Offline-First dari jadwal yang ditetapkan Kaprodi/Dosen",
                actionText = "Lihat KTM/Ujian",
                onActionClick = { showKtmAndExamSheet = true }
            )

            jadwalList.forEach { item ->
                JadwalHariIniCard(
                    item = item,
                    onScanPresensiClick = { activeQrScanJadwal = item },
                    onAlreadyPresensiClick = {
                        viewModel.showSuccessBanner("Presensi pertemuan #${item.pertemuanKe} untuk ${item.namaMk} telah tercatat tervalidasi.")
                    }
                )
            }
        }
    }
}

@Composable
private fun JadwalHariIniCard(
    item: JadwalKuliahItem,
    onScanPresensiClick: () -> Unit,
    onAlreadyPresensiClick: () -> Unit = {}
) {
    SiakadCard(contentPadding = PaddingValues(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MonoCodeChip(
                    text = item.kodeMk,
                    backgroundColor = BrandPrimaryLight,
                    textColor = BrandPrimaryDark
                )
                MonoCodeChip(text = "${item.sks} SKS • ${item.kelas}")
            }
            StatusBadge(
                text = if (item.sudahPresensiHariIni) "Hadir Tercatat" else "Pertemuan Ke-${item.pertemuanKe}",
                variant = if (item.sudahPresensiHariIni) BadgeVariant.SUCCESS else BadgeVariant.WARNING
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = item.namaMk,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = BrandPrimary,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = "${item.hari}, ${item.jamMulai} - ${item.jamSelesai} WIB",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(text = "•", color = TextMuted)
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = BrandAccent,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = item.ruangan,
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Dosen: ${item.dosenPengajar} • Kehadiran: ${item.persentaseKehadiran}%",
            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (item.sudahPresensiHariIni) {
                    "Presensi QR & GPS tervalidasi"
                } else {
                    "Sesi presensi kelas sedang dibuka"
                },
                style = MaterialTheme.typography.labelMedium.copy(
                    color = if (item.sudahPresensiHariIni) StatusSuccess else BrandAccent
                )
            )

            if (item.sudahPresensiHariIni) {
                SecondaryOutlineButton(
                    text = "Sudah Presensi",
                    onClick = onAlreadyPresensiClick,
                    leadingIcon = Icons.Default.CheckCircle,
                    height = 38.dp
                )
            } else {
                PrimaryEmeraldButton(
                    text = "Scan Presensi",
                    onClick = onScanPresensiClick,
                    leadingIcon = Icons.Default.QrCodeScanner,
                    height = 40.dp
                )
            }
        }
    }
}

/**
 * Dialog Reusable CRUD Tambah / Edit & Simpan Jadwal Kuliah ke Room SQLite (`cached_jadwal_kuliah`).
 */
@Composable
fun JadwalKuliahCrudDialog(
    initialItem: JadwalKuliahItem?,
    onDismiss: () -> Unit,
    onSave: (
        id: Long?,
        kodeMk: String,
        namaMk: String,
        sks: String,
        kelas: String,
        hari: String,
        jamMulai: String,
        jamSelesai: String,
        ruangan: String,
        dosen: String,
        pertemuan: String
    ) -> Unit
) {
    var kodeMk by remember(initialItem) { mutableStateOf(initialItem?.kodeMk ?: "") }
    var namaMk by remember(initialItem) { mutableStateOf(initialItem?.namaMk ?: "") }
    var sks by remember(initialItem) { mutableStateOf((initialItem?.sks ?: 3).toString()) }
    var kelas by remember(initialItem) { mutableStateOf(initialItem?.kelas ?: "PAI-5A") }
    var hari by remember(initialItem) { mutableStateOf(initialItem?.hari ?: "Selasa") }
    var jamMulai by remember(initialItem) { mutableStateOf(initialItem?.jamMulai ?: "08:00") }
    var jamSelesai by remember(initialItem) { mutableStateOf(initialItem?.jamSelesai ?: "10:30") }
    var ruangan by remember(initialItem) { mutableStateOf(initialItem?.ruangan ?: "") }
    var dosen by remember(initialItem) { mutableStateOf(initialItem?.dosenPengajar ?: "") }
    var pertemuan by remember(initialItem) { mutableStateOf((initialItem?.pertemuanKe ?: 8).toString()) }
    var validationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (initialItem == null) "Tambah Jadwal Kuliah" else "Ubah Data Jadwal",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Room SQLite: cached_jadwal_kuliah",
                        fontFamily = JetBrainsMonoFamily,
                        fontSize = 11.sp,
                        color = BrandPrimary
                    )
                }
                SecondaryOutlineButton(
                    text = "Auto-Fill",
                    leadingIcon = Icons.Default.AutoFixHigh,
                    height = 34.dp,
                    onClick = {
                        kodeMk = "PAI-512"
                        namaMk = "Tafsir Ayat Tarbawi II"
                        sks = "3"
                        kelas = "PAI-5B"
                        hari = "Rabu"
                        jamMulai = "09:30"
                        jamSelesai = "12:00"
                        ruangan = "Gedung Al-Khawarizmi R.202"
                        dosen = "Dr. KH. Ahmad Muzakki, M.Ag."
                        pertemuan = "7"
                        validationError = null
                    }
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (validationError != null) {
                    Surface(
                        color = StatusDangerBg,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, StatusDanger.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = validationError ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = StatusDanger,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = kodeMk,
                        onValueChange = { kodeMk = it; validationError = null },
                        label = { Text("Kode MK*") },
                        placeholder = { Text("PAI-520") },
                        modifier = Modifier.weight(0.55f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = sks,
                        onValueChange = { sks = it; validationError = null },
                        label = { Text("SKS*") },
                        modifier = Modifier.weight(0.45f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                OutlinedTextField(
                    value = namaMk,
                    onValueChange = { namaMk = it; validationError = null },
                    label = { Text("Nama Mata Kuliah*") },
                    placeholder = { Text("Contoh: Bahtsul Masail Kontemporer") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = kelas,
                        onValueChange = { kelas = it },
                        label = { Text("Kelas") },
                        modifier = Modifier.weight(0.5f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = hari,
                        onValueChange = { hari = it },
                        label = { Text("Hari") },
                        modifier = Modifier.weight(0.5f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = jamMulai,
                        onValueChange = { jamMulai = it },
                        label = { Text("Jam Mulai") },
                        modifier = Modifier.weight(0.5f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = jamSelesai,
                        onValueChange = { jamSelesai = it },
                        label = { Text("Jam Selesai") },
                        modifier = Modifier.weight(0.5f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                OutlinedTextField(
                    value = ruangan,
                    onValueChange = { ruangan = it; validationError = null },
                    label = { Text("Ruangan Kuliah*") },
                    placeholder = { Text("Gedung Al-Ghazali R.104") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dosen,
                        onValueChange = { dosen = it; validationError = null },
                        label = { Text("Dosen Pengajar*") },
                        placeholder = { Text("Dr. KH. Ahmad...") },
                        modifier = Modifier.weight(0.68f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = pertemuan,
                        onValueChange = { pertemuan = it },
                        label = { Text("Pert. Ke") },
                        modifier = Modifier.weight(0.32f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            PrimaryEmeraldButton(
                text = "Simpan Data ke Room",
                leadingIcon = Icons.Default.Save,
                onClick = {
                    if (kodeMk.isBlank() || namaMk.isBlank() || ruangan.isBlank() || dosen.isBlank()) {
                        validationError = "Kode MK, Nama MK, Ruangan, dan Dosen Pengajar wajib diisi!"
                    } else {
                        onSave(
                            initialItem?.id,
                            kodeMk,
                            namaMk,
                            sks,
                            kelas,
                            hari,
                            jamMulai,
                            jamSelesai,
                            ruangan,
                            dosen,
                            pertemuan
                        )
                    }
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
private fun KtmAndExamCardSheetContent(
    nama: String,
    nim: String,
    prodi: String,
    semester: Int,
    isUktLunas: Boolean,
    sisaTunggakan: Long,
    jadwalUjian: List<JadwalKuliahItem>,
    onDownloadKtmPdf: () -> Unit,
    onDownloadExamCardPdf: () -> Unit,
    onPayNowClick: () -> Unit,
    onClose: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "KTM Digital & Kartu Ujian",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Tersimpan secara Offline-First di perangkat Anda",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceMuted,
            contentColor = BrandPrimary,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("KTM Digital Virtual", fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isUktLunas) Icons.Default.Verified else Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (isUktLunas) StatusSuccess else StatusDanger
                        )
                        Text("Kartu Ujian UTS/UAS", fontWeight = FontWeight.SemiBold)
                    }
                }
            )
        }

        if (selectedTab == 0) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.5.dp, BrandAccent.copy(alpha = 0.6f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(BrandPrimaryDark, BrandPrimary)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "STAI AL-YASINI PASURUAN",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = BrandAccentLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "KARTU TANDA MAHASISWA ELEKTRONIK",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            StatusBadge(text = "AKTIF", variant = BadgeVariant.ACCENT)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = nama,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = nim,
                                    fontFamily = JetBrainsMonoFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = BrandAccentLight
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = prodi,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                                )
                                Text(
                                    text = "Semester $semester • Berlaku s/d Agustus 2027",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                modifier = Modifier.size(88.dp)
                            ) {
                                Box(
                                    modifier = Modifier.padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    QrMatrixIllustration(seed = nim)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            PrimaryEmeraldButton(
                text = "Unduh PDF KTM Digital Virtual",
                leadingIcon = Icons.Default.Download,
                onClick = onDownloadKtmPdf,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            if (!isUktLunas) {
                SiakadCard(
                    containerColor = StatusDangerBg,
                    borderColor = StatusDanger.copy(alpha = 0.4f),
                    contentPadding = PaddingValues(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(StatusDanger.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = StatusDanger,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Text(
                            text = "Kartu Ujian UTS/UAS Terkunci",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color(0xFF991B1B),
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Anda masih memiliki tunggakan UKT Semester Aktif sebesar ${CurrencyFormatter.formatRupiah(sisaTunggakan)}. Silakan lunasi melalui Payment Gateway agar Kartu Ujian terbuka otomatis.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF7F1D1D),
                                textAlign = TextAlign.Center
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        PrimaryEmeraldButton(
                            text = "Lunasi UKT Sekarang",
                            onClick = onPayNowClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                SiakadCard(
                    containerColor = StatusSuccessBg,
                    borderColor = StatusSuccess.copy(alpha = 0.4f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Kartu Peserta Ujian Semester Ganjil",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimaryDark
                                )
                            )
                            Text(
                                text = "No. Kursi: PAI-5A-014 • UKT Terverifikasi Lunas",
                                fontFamily = JetBrainsMonoFamily,
                                fontSize = 11.sp,
                                color = BrandPrimary
                            )
                        }
                        StatusBadge(text = "SAH / AKTIF", variant = BadgeVariant.SUCCESS)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    jadwalUjian.forEach { mk ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${mk.kodeMk} - ${mk.namaMk}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            MonoCodeChip(text = "${mk.sks} SKS")
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    PrimaryEmeraldButton(
                        text = "Unduh PDF Kartu Ujian Resmi",
                        onClick = onDownloadExamCardPdf,
                        leadingIcon = Icons.Default.Download,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun QrAttendanceScannerDialog(
    jadwal: JadwalKuliahItem,
    onDismiss: () -> Unit,
    onScanMode: (QrSimulationMode) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = BrandPrimary
                )
                Column {
                    Text(
                        text = "Scan QR Presensi Perkuliahan",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${jadwal.kodeMk} • Pertemuan Ke-${jadwal.pertemuanKe}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A))
                        .border(2.dp, BrandPrimary, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = null,
                            tint = BrandAccentLight,
                            modifier = Modifier.size(68.dp)
                        )
                        Text(
                            text = "CameraX + ML Kit Barcode Analyzer Aktif",
                            style = MaterialTheme.typography.labelMedium.copy(color = Color.White)
                        )
                        MonoCodeChip(
                            text = "TOKEN: ALYASINI-${jadwal.kodeMk}-P${jadwal.pertemuanKe}",
                            backgroundColor = BrandPrimaryDark,
                            textColor = BrandAccentLight
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BrandPrimaryLight,
                    border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Koordinat GPS Kampus STAI Al-Yasini",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = BrandPrimaryDark,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Lat: -7.669420, Lng: 112.895140 (Radius Maks 150m • Anti-Mock GPS Aktif)",
                                fontFamily = JetBrainsMonoFamily,
                                fontSize = 10.sp,
                                color = BrandPrimaryDark
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PrimaryEmeraldButton(
                    text = "Pindai QR (Lokasi Valid di Kampus <=150m)",
                    onClick = { onScanMode(QrSimulationMode.VALID_CAMPUS) },
                    modifier = Modifier.fillMaxWidth()
                )
                SecondaryOutlineButton(
                    text = "Uji Coba di Luar Radius Kampus (>150m - Ditolak)",
                    onClick = { onScanMode(QrSimulationMode.OUTSIDE_CAMPUS) },
                    borderColor = BrandAccent,
                    contentColor = BrandAccent,
                    modifier = Modifier.fillMaxWidth()
                )
                SecondaryOutlineButton(
                    text = "Uji Coba Fake / Mock GPS Terdeteksi (Ditolak)",
                    onClick = { onScanMode(QrSimulationMode.MOCK_GPS) },
                    borderColor = StatusDanger,
                    contentColor = StatusDanger,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
fun QrMatrixIllustration(seed: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val grid = 7
        val cellW = size.width / grid
        val cellH = size.height / grid
        for (r in 0 until grid) {
            for (c in 0 until grid) {
                val isCorner = (r < 2 && c < 2) || (r < 2 && c >= grid - 2) || (r >= grid - 2 && c < 2)
                val hashBit = ((r * 31 + c * 17 + seed.hashCode()) % 2 == 0)
                if (isCorner || hashBit) {
                    drawRect(
                        color = Color(0xFF0F172A),
                        topLeft = Offset(c * cellW, r * cellH),
                        size = Size(cellW * 0.88f, cellH * 0.88f)
                    )
                }
            }
        }
    }
}
