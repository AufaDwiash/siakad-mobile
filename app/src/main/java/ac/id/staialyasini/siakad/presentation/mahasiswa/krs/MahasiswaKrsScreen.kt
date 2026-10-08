package ac.id.staialyasini.siakad.presentation.mahasiswa.krs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import ac.id.staialyasini.siakad.core.ui.theme.StatusInfo
import ac.id.staialyasini.siakad.core.ui.theme.StatusInfoBg
import ac.id.staialyasini.siakad.core.ui.theme.StatusSuccess
import ac.id.staialyasini.siakad.core.ui.theme.StatusSuccessBg
import ac.id.staialyasini.siakad.core.ui.theme.StatusWarning
import ac.id.staialyasini.siakad.core.ui.theme.SurfaceBase
import ac.id.staialyasini.siakad.core.ui.theme.SurfaceCard
import ac.id.staialyasini.siakad.core.ui.theme.TextMuted
import ac.id.staialyasini.siakad.core.ui.theme.TextPrimary
import ac.id.staialyasini.siakad.core.ui.theme.TextSecondary
import ac.id.staialyasini.siakad.domain.model.KrsStatus
import ac.id.staialyasini.siakad.presentation.mahasiswa.MahasiswaViewModel

@Composable
fun MahasiswaKrsScreen(
    viewModel: MahasiswaViewModel,
    onNavigateToPayment: () -> Unit
) {
    val tagihan by viewModel.tagihanUkt.collectAsStateWithLifecycle()
    val krsStatus by viewModel.krsStatus.collectAsStateWithLifecycle()
    val catatanWali by viewModel.krsCatatanWali.collectAsStateWithLifecycle()
    val kelasList by viewModel.kelasDitawarkan.collectAsStateWithLifecycle()
    val feedbackBanner by viewModel.feedbackBanner.collectAsStateWithLifecycle()

    val totalSksSelected = kelasList.filter { it.isSelected }.sumOf { it.sks }
    val maxSks = 24

    var showKrsPdfPreview by remember { mutableStateOf(false) }

    if (showKrsPdfPreview) {
        val selectedClasses = kelasList.filter { it.isSelected }
        OfficialDocumentPreviewDialog(
            documentTitle = "KARTU RENCANA STUDI (KRS) RESMI",
            documentNumber = "STAI.Y/KRS/2026/0942",
            studentName = "Ahmad Fauzi",
            studentIdentifier = "2023010042",
            prodi = "S1 Pendidikan Agama Islam (PAI)",
            extraMetadata = listOf(
                "Semester" to "5 (Ganjil)",
                "Tahun Akademik" to "2026/2027",
                "Dosen Pembimbing Wali" to "Dr. KH. Ahmad Muzakki, M.Ag.",
                "Status Pengesahan" to "DISETUJUI DOSEN WALI"
            ),
            itemsTable = selectedClasses.map {
                Triple(it.kodeMk, "${it.namaMk} (${it.kelas})", "${it.sks} SKS")
            },
            tableHeader = Triple("Kode MK", "Mata Kuliah & Kelas", "Beban SKS"),
            totalSummary = "Total SKS Disetujui" to "$totalSksSelected SKS",
            signatoryTitle = "Dosen Pembimbing Akademik / Wali",
            signatoryName = "Dr. KH. Ahmad Muzakki, M.Ag.",
            signatoryNidn = "NIDN: 2114087801",
            onDismiss = { showKrsPdfPreview = false },
            onPrintOrSave = { docNum ->
                viewModel.showSuccessBanner("KRS Resmi ($docNum) berhasil dicetak & disimpan dalam format PDF!")
            }
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
        // GUARD LOGIC CEKAL KEUANGAN: Kunci pengajuan KRS jika UKT belum lunas
        if (!tagihan.statusLunas) {
            SiakadCard(
                containerColor = StatusDangerBg,
                borderColor = StatusDanger.copy(alpha = 0.45f),
                contentPadding = PaddingValues(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = StatusDanger,
                        modifier = Modifier.size(28.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Akses Kontrak KRS Dikunci (Cekal Keuangan)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color(0xFF991B1B),
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Tagihan UKT Semester Aktif belum lunas. Silakan selesaikan pembayaran melalui Payment Gateway untuk membuka pengajuan KRS.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF7F1D1D))
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                PrimaryEmeraldButton(
                    text = "Ke Halaman Pembayaran UKT",
                    onClick = onNavigateToPayment,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Card Kuota SKS & Status KRS
        SiakadCard(contentPadding = PaddingValues(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Kontrak KRS Semester V",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "IPS Semester Lalu: 3.82 • Maksimal Ambil: 24 SKS",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                StatusBadge(
                    text = krsStatus.label,
                    variant = when (krsStatus) {
                        KrsStatus.DISETUJUI -> BadgeVariant.SUCCESS
                        KrsStatus.DIAJUKAN -> BadgeVariant.INFO
                        KrsStatus.DITOLAK -> BadgeVariant.DANGER
                        KrsStatus.DRAFT -> BadgeVariant.NEUTRAL
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progress SKS Terpilih",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = "$totalSksSelected / $maxSks SKS",
                    fontFamily = JetBrainsMonoFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = BrandPrimaryDark
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { (totalSksSelected.toFloat() / maxSks.toFloat()).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(99.dp)),
                color = BrandPrimary,
                trackColor = BrandPrimaryLight
            )

            if (!catatanWali.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (krsStatus == KrsStatus.DITOLAK) StatusDangerBg else StatusSuccessBg,
                    border = BorderStroke(
                        1.dp,
                        if (krsStatus == KrsStatus.DITOLAK) StatusDanger.copy(alpha = 0.35f)
                        else StatusSuccess.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Catatan Dosen Wali: $catatanWali",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (krsStatus == KrsStatus.DITOLAK) Color(0xFF991B1B) else BrandPrimaryDark,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }

        if (feedbackBanner != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StatusInfoBg,
                border = BorderStroke(1.dp, StatusInfo.copy(alpha = 0.35f)),
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
                            tint = StatusInfo,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = feedbackBanner ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF1E40AF),
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                    IconButton(onClick = viewModel::dismissBanner, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        SectionHeader(
            title = "Daftar Kelas Kuliah Ditawarkan",
            subtitle = "Validasi otomatis bentrok jadwal & sisa kuota kelas real-time"
        )

        kelasList.forEach { mk ->
            SiakadCard(
                containerColor = if (mk.isSelected) BrandPrimaryLight.copy(alpha = 0.55f) else SurfaceCard,
                borderColor = if (mk.isSelected) BrandPrimary else BorderDefault,
                contentPadding = PaddingValues(14.dp),
                onClick = {
                    if (tagihan.statusLunas && krsStatus != KrsStatus.DISETUJUI && krsStatus != KrsStatus.DIAJUKAN) {
                        viewModel.togglePilihKelasKrs(mk.id)
                    }
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Checkbox(
                        checked = mk.isSelected,
                        onCheckedChange = {
                            if (tagihan.statusLunas && krsStatus != KrsStatus.DISETUJUI && krsStatus != KrsStatus.DIAJUKAN) {
                                viewModel.togglePilihKelasKrs(mk.id)
                            }
                        },
                        enabled = tagihan.statusLunas && krsStatus != KrsStatus.DISETUJUI && krsStatus != KrsStatus.DIAJUKAN,
                        colors = CheckboxDefaults.colors(checkedColor = BrandPrimary)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                MonoCodeChip(
                                    text = mk.kodeMk,
                                    backgroundColor = BrandPrimaryLight,
                                    textColor = BrandPrimaryDark
                                )
                                MonoCodeChip(text = "${mk.sks} SKS • ${mk.kelas}")
                            }
                            StatusBadge(
                                text = "Sisa Kuota: ${mk.sisaKuota}/${mk.kuotaMaksimal}",
                                variant = if (mk.sisaKuota <= 3) BadgeVariant.WARNING else BadgeVariant.SUCCESS,
                                leadingDot = false
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = mk.namaMk,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "${mk.hari}, ${mk.jamMulai} - ${mk.jamSelesai} WIB • Ruang ${mk.ruangan}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BrandPrimaryDark,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = "Dosen: ${mk.namaDosen}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }
                }
            }
        }

        // Action Buttons: 100% Berfungsi (Unduh PDF Resmi, Ajukan KRS, Re-Simulasi)
        when (krsStatus) {
            KrsStatus.DISETUJUI -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PrimaryEmeraldButton(
                        text = "Unduh PDF KRS Resmi (Disetujui Dosen Wali)",
                        leadingIcon = Icons.Default.Download,
                        onClick = { showKrsPdfPreview = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                    SecondaryOutlineButton(
                        text = "Ubah / Ajukan Ulang KRS (Re-Simulasi Demo)",
                        leadingIcon = Icons.Default.Refresh,
                        borderColor = BrandAccent,
                        contentColor = BrandAccent,
                        onClick = viewModel::resetKrsKeDraft,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            KrsStatus.DITOLAK -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PrimaryEmeraldButton(
                        text = "Ubah Pilihan & Ajukan Ulang KRS",
                        leadingIcon = Icons.Default.Refresh,
                        containerColor = BrandAccent,
                        onClick = viewModel::resetKrsKeDraft,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            KrsStatus.DIAJUKAN -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BrandPrimaryLight,
                        border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "KRS Menunggu Approval Dosen Wali",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimaryDark
                                )
                            )
                            Text(
                                text = "Data KRS ($totalSksSelected SKS) telah tersinkron ke antrean Dosen Wali. Silakan gunakan tombol 'Ganti Role' di TopBar ke 'Dosen Pengajar & Wali' untuk menyetujui KRS ini.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }
                    SecondaryOutlineButton(
                        text = "Batalkan & Ubah Pilihan KRS",
                        leadingIcon = Icons.Default.Close,
                        borderColor = BorderDefault,
                        contentColor = TextSecondary,
                        onClick = viewModel::resetKrsKeDraft,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            KrsStatus.DRAFT -> {
                PrimaryEmeraldButton(
                    text = "Ajukan KRS ke Dosen Wali ($totalSksSelected SKS)",
                    leadingIcon = Icons.Default.Send,
                    enabled = tagihan.statusLunas && totalSksSelected > 0,
                    onClick = viewModel::ajukanKrsKeDosenWali,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
