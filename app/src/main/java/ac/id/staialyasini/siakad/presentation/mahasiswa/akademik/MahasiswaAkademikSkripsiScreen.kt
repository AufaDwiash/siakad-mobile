package ac.id.staialyasini.siakad.presentation.mahasiswa.akademik

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import ac.id.staialyasini.siakad.core.ui.theme.BrandPrimary
import ac.id.staialyasini.siakad.core.ui.theme.BrandPrimaryDark
import ac.id.staialyasini.siakad.core.ui.theme.BrandPrimaryLight
import ac.id.staialyasini.siakad.core.ui.theme.JetBrainsMonoFamily
import ac.id.staialyasini.siakad.core.ui.theme.StatusDanger
import ac.id.staialyasini.siakad.core.ui.theme.StatusDangerBg
import ac.id.staialyasini.siakad.core.ui.theme.StatusSuccess
import ac.id.staialyasini.siakad.core.ui.theme.StatusSuccessBg
import ac.id.staialyasini.siakad.core.ui.theme.SurfaceBase
import ac.id.staialyasini.siakad.core.ui.theme.SurfaceMuted
import ac.id.staialyasini.siakad.core.ui.theme.TextMuted
import ac.id.staialyasini.siakad.core.ui.theme.TextPrimary
import ac.id.staialyasini.siakad.core.ui.theme.TextSecondary
import ac.id.staialyasini.siakad.domain.model.BimbinganLogItem
import ac.id.staialyasini.siakad.presentation.mahasiswa.MahasiswaViewModel

@Composable
fun MahasiswaAkademikSkripsiScreen(
    viewModel: MahasiswaViewModel
) {
    val khsList by viewModel.khsSemesterList.collectAsStateWithLifecycle()
    val bimbinganLogs by viewModel.bimbinganLogs.collectAsStateWithLifecycle()
    val ahmadFauziSkripsi by viewModel.ahmadFauziSkripsi.collectAsStateWithLifecycle()
    val feedbackBanner by viewModel.feedbackBanner.collectAsStateWithLifecycle()
    val errorBanner by viewModel.errorBanner.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(1) } // Default buka tab Skripsi & Log Bimbingan untuk demo CRUD PBL
    var selectedSemesterIdx by remember { mutableIntStateOf((khsList.size - 1).coerceAtLeast(0)) }
    val currentKhs = khsList.getOrNull(selectedSemesterIdx)

    // State Dialog Dokumen Cetak Resmi (PDF Preview)
    var showKhsPreviewDialog by remember { mutableStateOf(false) }
    var showAccSidangPreviewDialog by remember { mutableStateOf(false) }

    // State Form Tambah & Simpan Data Log Bimbingan Skripsi
    val suggestedNextPertemuan = ((bimbinganLogs.maxOfOrNull { it.pertemuanKe } ?: 0) + 1).toString()
    var inputPertemuanKe by remember(bimbinganLogs.size) { mutableStateOf(suggestedNextPertemuan) }
    var inputTanggal by remember { mutableStateOf("08 Okt 2026") }
    var inputBab by remember { mutableStateOf("") }
    var inputCatatan by remember { mutableStateOf("") }
    var localValidationError by remember { mutableStateOf<String?>(null) }

    // State Dialog Edit (Update) & Konfirmasi Hapus (Delete)
    var editingLogItem by remember { mutableStateOf<BimbinganLogItem?>(null) }
    var deletingLogItem by remember { mutableStateOf<BimbinganLogItem?>(null) }

    // Dialog Edit (Update) Catatan Bimbingan Skripsi
    if (editingLogItem != null) {
        val target = editingLogItem!!
        var editPertemuan by remember(target) { mutableStateOf(target.pertemuanKe.toString()) }
        var editTanggal by remember(target) { mutableStateOf(target.tanggal) }
        var editBab by remember(target) { mutableStateOf(target.babBahasan) }
        var editCatatan by remember(target) { mutableStateOf(target.catatanMahasiswa) }
        var editError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { editingLogItem = null },
            title = {
                Text(
                    text = "Ubah Data Log Bimbingan (#${target.pertemuanKe})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (editError != null) {
                        Text(
                            text = editError ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = StatusDanger,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editPertemuan,
                            onValueChange = { editPertemuan = it; editError = null },
                            label = { Text("Pertemuan Ke") },
                            modifier = Modifier.weight(0.45f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = editTanggal,
                            onValueChange = { editTanggal = it; editError = null },
                            label = { Text("Tanggal") },
                            modifier = Modifier.weight(0.55f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                    OutlinedTextField(
                        value = editBab,
                        onValueChange = { editBab = it; editError = null },
                        label = { Text("Bab Bahasan") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = editCatatan,
                        onValueChange = { editCatatan = it; editError = null },
                        label = { Text("Catatan Mahasiswa") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                PrimaryEmeraldButton(
                    text = "Simpan Perubahan",
                    leadingIcon = Icons.Default.Save,
                    onClick = {
                        if (editPertemuan.isBlank() || editTanggal.isBlank() || editBab.isBlank() || editCatatan.isBlank()) {
                            editError = "Semua kolom wajib diisi!"
                        } else {
                            viewModel.updateLogBimbingan(
                                logId = target.id,
                                pertemuanKeStr = editPertemuan,
                                tanggal = editTanggal,
                                bab = editBab,
                                catatan = editCatatan,
                                onSuccessClose = { editingLogItem = null }
                            )
                        }
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { editingLogItem = null }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Dialog Konfirmasi Hapus (Delete) Log Bimbingan
    if (deletingLogItem != null) {
        val target = deletingLogItem!!
        AlertDialog(
            onDismissRequest = { deletingLogItem = null },
            title = {
                Text(
                    text = "Hapus Log Bimbingan?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = StatusDanger,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus permanen log Pertemuan #${target.pertemuanKe} (${target.babBahasan}) dari Room Database?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                PrimaryEmeraldButton(
                    text = "Ya, Hapus Data",
                    containerColor = StatusDanger,
                    onClick = {
                        viewModel.hapusLogBimbingan(target.id, target.babBahasan)
                        deletingLogItem = null
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { deletingLogItem = null }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Dialog Preview Resmi PDF KHS
    if (showKhsPreviewDialog && currentKhs != null) {
        OfficialDocumentPreviewDialog(
            documentTitle = "KARTU HASIL STUDI (KHS) MAHASISWA",
            documentNumber = "B-042/STAI-ASY/KHS/${currentKhs.semester}/2026",
            studentName = "Ahmad Fauzi",
            studentIdentifier = "2023010042",
            prodi = "S1 Pendidikan Agama Islam (PAI)",
            extraMetadata = listOf(
                "Semester" to "${currentKhs.semester}",
                "Tahun Akademik" to currentKhs.tahunAjaran,
                "Dosen Wali" to "Dr. KH. Ahmad Muzakki, M.Ag.",
                "Indeks Prestasi Semester (IPS)" to String.format("%.2f", currentKhs.ips)
            ),
            itemsTable = currentKhs.matakuliahNilai.map { mk ->
                Triple(mk.kodeMk, mk.namaMk, "${mk.sks} SKS • Nilai: ${mk.nilaiHuruf}")
            },
            tableHeader = Triple("Kode MK", "Mata Kuliah", "SKS & Nilai"),
            totalSummary = "Total SKS: ${currentKhs.sksSemester} SKS" to "IP Semester: ${String.format("%.2f", currentKhs.ips)}",
            signatoryTitle = "Ketua Program Studi PAI",
            signatoryName = "Dr. Hj. Siti Marifah, M.Pd.I.",
            signatoryNidn = "NIDN: 0701058002",
            onDismiss = { showKhsPreviewDialog = false }
        )
    }

    // Dialog Preview Resmi PDF Lembar ACC Sidang Munaqasyah
    if (showAccSidangPreviewDialog) {
        OfficialDocumentPreviewDialog(
            documentTitle = "LEMBAR PERSETUJUAN (ACC) MUNAQASYAH SKRIPSI",
            documentNumber = "B-088/STAI-ASY/MUNAQASYAH/X/2026",
            studentName = "Ahmad Fauzi",
            studentIdentifier = "2023010042",
            prodi = "S1 Pendidikan Agama Islam (PAI)",
            extraMetadata = listOf(
                "Judul Skripsi" to ahmadFauziSkripsi.judulSkripsi,
                "Dosen Pembimbing" to (ahmadFauziSkripsi.dosenPembimbingSaatIni ?: "Dr. H. Ahmad Shodiq, M.Pd.I."),
                "Status Kelayakan" to "MEMENUHI SYARAT SIDANG (>= 8 ACC)"
            ),
            itemsTable = bimbinganLogs.filter { it.isAcc }.mapIndexed { idx, log ->
                Triple("#${idx + 1}", log.babBahasan, "ACC (${log.tanggal})")
            },
            tableHeader = Triple("No", "Bab Bahasan / Topik", "Status ACC & Tanggal"),
            totalSummary = "Total Sesi ACC" to "${bimbinganLogs.count { it.isAcc }} Pertemuan",
            signatoryTitle = "Dosen Pembimbing Skripsi",
            signatoryName = ahmadFauziSkripsi.dosenPembimbingSaatIni ?: "Dr. H. Ahmad Shodiq, M.Pd.I.",
            signatoryNidn = "NIDN: 2114087801",
            onDismiss = { showAccSidangPreviewDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBase)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceMuted,
            contentColor = BrandPrimary,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("KHS, Transkrip & Grafik IPS", fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Skripsi & CRUD Log Bimbingan", fontWeight = FontWeight.SemiBold) }
            )
        }

        // Visual Banner Sukses / Error dari Room Database CRUD
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
                    IconButton(onClick = viewModel::dismissBanner, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        val activeError = errorBanner ?: localValidationError
        if (activeError != null) {
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
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = StatusDanger,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = activeError,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF991B1B),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    IconButton(
                        onClick = {
                            viewModel.dismissBanner()
                            localValidationError = null
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        if (selectedTab == 0) {
            // GRAFIK GARIS (LINE CHART) PERKEMBANGAN IPS SEMESTER 1 - BERJALAN
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
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ShowChart,
                            contentDescription = null,
                            tint = BrandPrimary
                        )
                        Column {
                            Text(
                                text = "Grafik Perkembangan IPS",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Tren Indeks Prestasi Semester 1 s/d 4",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    MonoCodeChip(
                        text = "IPK: 3.78",
                        backgroundColor = BrandPrimaryLight,
                        textColor = BrandPrimaryDark
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val ipsPoints = khsList.map { it.ips.toFloat() }
                IpsLineChart(ipsValues = ipsPoints)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    khsList.forEach { sem ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Smt ${sem.semester}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = String.format("%.2f", sem.ips),
                                fontFamily = JetBrainsMonoFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = BrandPrimaryDark
                            )
                        }
                    }
                }
            }

            // Pemilih Semester KHS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                khsList.forEachIndexed { idx, item ->
                    val active = idx == selectedSemesterIdx
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (active) BrandPrimary else Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedSemesterIdx = idx }
                    ) {
                        Text(
                            text = "Smt ${item.semester}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = if (active) Color.White else TextSecondary
                            ),
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            val currentKhs = khsList.getOrNull(selectedSemesterIdx)
            if (currentKhs != null) {
                SiakadCard(contentPadding = PaddingValues(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Kartu Hasil Studi (Semester ${currentKhs.semester})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${currentKhs.tahunAjaran} • Total ${currentKhs.sksSemester} SKS",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        StatusBadge(
                            text = "IPS ${String.format("%.2f", currentKhs.ips)}",
                            variant = BadgeVariant.SUCCESS
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    currentKhs.matakuliahNilai.forEach { mk ->
                        HorizontalDivider(color = BorderDefault)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    MonoCodeChip(text = mk.kodeMk)
                                    MonoCodeChip(text = "${mk.sks} SKS")
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = mk.namaMk,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = TextPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = mk.nilaiHuruf,
                                    fontFamily = JetBrainsMonoFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = BrandPrimaryDark
                                )
                                Text(
                                    text = "${mk.nilaiAngka} (Bobot ${mk.indeksBobot})",
                                    fontFamily = JetBrainsMonoFamily,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    PrimaryEmeraldButton(
                        text = "Unduh PDF KHS & Transkrip Sementara",
                        leadingIcon = Icons.Default.Download,
                        onClick = { showKhsPreviewDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            // TAB 2: TUGAS AKHIR & CRUD BUKU LOG BIMBINGAN SKRIPSI (ROOM DATABASE PERSISTENT)
            val accCount = bimbinganLogs.count { it.isAcc }

            SiakadCard(
                containerColor = BrandPrimaryLight,
                borderColor = BrandPrimary.copy(alpha = 0.35f),
                contentPadding = PaddingValues(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tugas Akhir / Skripsi (SKS Lulus: 110 >= 100)",
                            style = MaterialTheme.typography.labelLarge.copy(color = BrandPrimaryDark)
                        )
                        MonoCodeChip(
                            text = "ROOM SQLITE CRUD",
                            backgroundColor = Color.White,
                            textColor = BrandPrimaryDark
                        )
                    }
                    Text(
                        text = ahmadFauziSkripsi.judulSkripsi,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Pembimbing: ${ahmadFauziSkripsi.dosenPembimbingSaatIni}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MonoCodeChip(
                        text = "Total Log: ${bimbinganLogs.size} • ACC: $accCount/8",
                        backgroundColor = Color.White,
                        textColor = BrandPrimaryDark
                    )
                    StatusBadge(
                        text = if (accCount >= 8) "Syarat Sidang Terpenuhi" else "Dalam Bimbingan",
                        variant = if (accCount >= 8) BadgeVariant.SUCCESS else BadgeVariant.WARNING
                    )
                }
            }

            // Banner Khusus ACC Sidang Munaqasyah jika sudah disetujui Dosen Pembimbing
            if (ahmadFauziSkripsi.isAccSidangMunaqasyah) {
                SiakadCard(
                    containerColor = StatusSuccessBg,
                    borderColor = StatusSuccess.copy(alpha = 0.5f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = StatusSuccess,
                            modifier = Modifier.size(32.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ACC Sidang Munaqasyah Diterbitkan!",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = BrandPrimaryDark,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Dosen Pembimbing (${ahmadFauziSkripsi.dosenPembimbingSaatIni}) telah menerbitkan rekomendasi sidang munaqasyah resmi.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    PrimaryEmeraldButton(
                        text = "Unduh Lembar ACC Sidang Munaqasyah (PDF)",
                        leadingIcon = Icons.Default.Download,
                        onClick = { showAccSidangPreviewDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // FORM TAMBAH & SIMPAN DATA LOG BIMBINGAN SKRIPSI KE ROOM DATABASE
            SiakadCard(contentPadding = PaddingValues(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tambah & Simpan Data Log Bimbingan",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Tersimpan permanen di tabel SQLite bimbingan_log_skripsi",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    SecondaryOutlineButton(
                        text = "Auto-Fill",
                        leadingIcon = Icons.Default.AutoFixHigh,
                        height = 36.dp,
                        onClick = {
                            inputBab = "Revisi Bab IV: Analisis Hasil & Pembahasan Instrumen"
                            inputCatatan = "Telah diperbaiki triangulasi data wawancara guru PAI & kuesioner respon siswa MA Al-Yasini."
                            inputTanggal = "09 Okt 2026"
                            localValidationError = null
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = inputPertemuanKe,
                        onValueChange = {
                            inputPertemuanKe = it
                            localValidationError = null
                        },
                        label = { Text("Pertemuan Ke-*") },
                        placeholder = { Text("Contoh: 9") },
                        modifier = Modifier.weight(0.42f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = inputTanggal,
                        onValueChange = {
                            inputTanggal = it
                            localValidationError = null
                        },
                        label = { Text("Tanggal Bimbingan*") },
                        placeholder = { Text("08 Okt 2026") },
                        modifier = Modifier.weight(0.58f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = inputBab,
                    onValueChange = {
                        inputBab = it
                        localValidationError = null
                    },
                    label = { Text("Bab Bahasan / Topik Bimbingan*") },
                    placeholder = { Text("Contoh: Revisi Bab IV & Lampiran Instrumen") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = inputCatatan,
                    onValueChange = {
                        inputCatatan = it
                        localValidationError = null
                    },
                    label = { Text("Catatan Mahasiswa untuk Dosen Pembimbing*") },
                    placeholder = { Text("Tuliskan rincian progres atau revisi yang telah dikerjakan...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                PrimaryEmeraldButton(
                    text = "Tambah & Simpan Data ke Room Database",
                    leadingIcon = Icons.Default.AddComment,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (inputPertemuanKe.isBlank() || inputTanggal.isBlank() || inputBab.isBlank() || inputCatatan.isBlank()) {
                            localValidationError = "Validasi Form: Pertemuan Ke, Tanggal, Bab Bahasan, dan Catatan Mahasiswa tidak boleh kosong!"
                        } else {
                            viewModel.tambahDanSimpanLogBimbingan(
                                pertemuanKeStr = inputPertemuanKe,
                                tanggal = inputTanggal,
                                bab = inputBab,
                                catatan = inputCatatan,
                                onSuccessClearForm = {
                                    inputBab = ""
                                    inputCatatan = ""
                                    localValidationError = null
                                }
                            )
                        }
                    }
                )
            }

            SectionHeader(
                title = "Daftar Buku Log Bimbingan (${bimbinganLogs.size} Data)",
                subtitle = "Item berstatus 'Menunggu Review' dapat di-Edit (Update) atau Dihapus (Delete)"
            )

            bimbinganLogs.reversed().forEach { log ->
                SiakadCard(contentPadding = PaddingValues(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(if (log.isAcc) StatusSuccess else BrandAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "#${log.pertemuanKe}",
                                    fontFamily = JetBrainsMonoFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }
                            Column {
                                Text(
                                    text = log.babBahasan,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "Tanggal: ${log.tanggal}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                                )
                            }
                        }
                        StatusBadge(
                            text = if (log.isAcc) "ACC Dosen" else "Menunggu Review",
                            variant = if (log.isAcc) BadgeVariant.SUCCESS else BadgeVariant.WARNING
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Catatan Mahasiswa: ${log.catatanMahasiswa}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    if (!log.balasanDosen.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Status / Balasan Dosen: ${log.balasanDosen}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BrandPrimaryDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    // Tombol Edit (Update) & Hapus (Delete) untuk item yang belum di-ACC (masih menunggu review)
                    if (!log.isAcc) {
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = BorderDefault)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SecondaryOutlineButton(
                                text = "Edit Catatan",
                                leadingIcon = Icons.Default.Edit,
                                borderColor = BrandPrimary,
                                contentColor = BrandPrimaryDark,
                                height = 36.dp,
                                onClick = { editingLogItem = log }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            SecondaryOutlineButton(
                                text = "Hapus",
                                leadingIcon = Icons.Default.DeleteOutline,
                                borderColor = StatusDanger,
                                contentColor = StatusDanger,
                                height = 36.dp,
                                onClick = { deletingLogItem = log }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IpsLineChart(ipsValues: List<Float>) {
    if (ipsValues.isEmpty()) return
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        val minIps = 3.0f
        val maxIps = 4.0f
        val stepX = if (ipsValues.size > 1) size.width / (ipsValues.size - 1) else size.width

        val points = ipsValues.mapIndexed { index, ips ->
            val normalizedY = ((ips - minIps) / (maxIps - minIps)).coerceIn(0f, 1f)
            Offset(
                x = index * stepX,
                y = size.height - (normalizedY * size.height)
            )
        }

        for (i in 0..2) {
            val y = size.height * (i / 2f)
            drawLine(
                color = Color(0xFFE2E8F0),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 2f
            )
        }

        val path = Path().apply {
            points.forEachIndexed { idx, pt ->
                if (idx == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
            }
        }

        drawPath(
            path = path,
            color = Color(0xFF0D7C66),
            style = Stroke(width = 6f, cap = StrokeCap.Round)
        )

        points.forEach { pt ->
            drawCircle(
                color = Color(0xFFD97706),
                radius = 10f,
                center = pt
            )
            drawCircle(
                color = Color.White,
                radius = 5f,
                center = pt
            )
        }
    }
}
