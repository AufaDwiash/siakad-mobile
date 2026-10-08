package ac.id.staialyasini.siakad.presentation.mahasiswa.keuangan

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import ac.id.staialyasini.siakad.core.ui.theme.StatusInfo
import ac.id.staialyasini.siakad.core.ui.theme.StatusInfoBg
import ac.id.staialyasini.siakad.core.ui.theme.StatusSuccess
import ac.id.staialyasini.siakad.core.ui.theme.StatusSuccessBg
import ac.id.staialyasini.siakad.core.ui.theme.StatusWarning
import ac.id.staialyasini.siakad.core.ui.theme.SurfaceBase
import ac.id.staialyasini.siakad.core.ui.theme.SurfaceMuted
import ac.id.staialyasini.siakad.core.ui.theme.TextMuted
import ac.id.staialyasini.siakad.core.ui.theme.TextPrimary
import ac.id.staialyasini.siakad.core.ui.theme.TextSecondary
import ac.id.staialyasini.siakad.core.util.CurrencyFormatter
import ac.id.staialyasini.siakad.domain.model.CicilanTagihanItem
import ac.id.staialyasini.siakad.presentation.mahasiswa.MahasiswaViewModel
import ac.id.staialyasini.siakad.presentation.mahasiswa.dashboard.QrMatrixIllustration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UktPaymentScreen(
    viewModel: MahasiswaViewModel
) {
    val tagihan by viewModel.tagihanUkt.collectAsStateWithLifecycle()
    val feedbackBanner by viewModel.feedbackBanner.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current

    var selectedCicilanForSnap by remember { mutableStateOf<CicilanTagihanItem?>(null) }
    var showSnapGatewaySheet by remember { mutableStateOf(false) }
    var showKwitansiPreviewDialog by remember { mutableStateOf(false) }
    var showUploadManualDialog by remember { mutableStateOf(false) }
    var localInfoMessage by remember { mutableStateOf<String?>(null) }

    // Dialog Preview Resmi PDF Kwitansi UKT
    if (showKwitansiPreviewDialog) {
        OfficialDocumentPreviewDialog(
            documentTitle = "KWITANSI PEMBAYARAN BIAYA PENDIDIKAN (UKT)",
            documentNumber = "KWT-${tagihan.nomorInvoice}/2026",
            studentName = "Ahmad Fauzi",
            studentIdentifier = "2023010042",
            prodi = "S1 Pendidikan Agama Islam (PAI)",
            extraMetadata = listOf(
                "Jenis Tagihan" to tagihan.namaTagihan,
                "Tahun Akademik" to tagihan.tahunAjaran,
                "Status Pembayaran" to if (tagihan.statusLunas) "LUNAS (TERVERIFIKASI)" else "BELUM LUNAS"
            ),
            itemsTable = tagihan.cicilanList.map { c ->
                Triple(
                    "Termin ${c.terminKe}",
                    c.keterangan,
                    "${CurrencyFormatter.formatRupiah(c.nominal)} (${if (c.isPaid) "LUNAS" else "BELUM"})"
                )
            },
            tableHeader = Triple("Termin", "Keterangan Cicilan", "Nominal & Status"),
            totalSummary = "Total Pembayaran" to CurrencyFormatter.formatRupiah(tagihan.totalTerbayar),
            signatoryTitle = "Bendahara Penerimaan BAAK & Keuangan",
            signatoryName = "M. Syafi'i, S.E., M.Ak.",
            signatoryNidn = "NIP: 198504122011011003",
            onDismiss = { showKwitansiPreviewDialog = false }
        )
    }

    // Dialog Upload Bukti Transfer Manual
    if (showUploadManualDialog) {
        UploadBuktiManualDialog(
            sisaTagihan = tagihan.sisaTagihan,
            onDismiss = { showUploadManualDialog = false },
            onSubmitProof = { bank, _, _ ->
                viewModel.bayarTagihanUkt(
                    terminId = null,
                    bayarPenuh = true,
                    metode = "Manual Transfer ($bank)"
                )
                showUploadManualDialog = false
                localInfoMessage = "Bukti transfer $bank berhasil diverifikasi! Tagihan UKT kini berstatus LUNAS."
            }
        )
    }

    if (showSnapGatewaySheet) {
        ModalBottomSheet(
            onDismissRequest = { showSnapGatewaySheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            MidtransSnapPaymentGatewaySheet(
                nomorInvoice = tagihan.nomorInvoice,
                snapToken = tagihan.snapToken,
                vaBsi = tagihan.virtualAccountBsi,
                vaBri = tagihan.virtualAccountBri,
                nominalBayar = selectedCicilanForSnap?.nominal ?: tagihan.sisaTagihan,
                keterangan = selectedCicilanForSnap?.keterangan ?: "Pelunasan Tagihan UKT Semester V",
                onCopyVa = { va ->
                    clipboardManager.setText(AnnotatedString(va))
                    localInfoMessage = "Nomor Virtual Account $va berhasil disalin!"
                },
                onConfirmWebhookPaid = { channel ->
                    viewModel.bayarTagihanUkt(
                        terminId = selectedCicilanForSnap?.id,
                        bayarPenuh = selectedCicilanForSnap == null,
                        metode = channel
                    )
                    showSnapGatewaySheet = false
                },
                onClose = { showSnapGatewaySheet = false }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBase)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card Tagihan UKT & Invoice Monospace
        Surface(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
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
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = tagihan.tahunAjaran,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BrandPrimaryLight.copy(alpha = 0.9f)
                                )
                            )
                            Text(
                                text = tagihan.namaTagihan,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        StatusBadge(
                            text = if (tagihan.statusLunas) "LUNAS" else "BELUM LUNAS",
                            variant = if (tagihan.statusLunas) BadgeVariant.SUCCESS else BadgeVariant.ACCENT
                        )
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "No. Invoice:",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                            )
                            Text(
                                text = tagihan.nomorInvoice,
                                fontFamily = JetBrainsMonoFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = BrandAccentLight
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Total Tagihan",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            )
                            Text(
                                text = CurrencyFormatter.formatRupiah(tagihan.totalTagihan),
                                fontFamily = JetBrainsMonoFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Sisa Tagihan",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            )
                            Text(
                                text = CurrencyFormatter.formatRupiah(tagihan.sisaTagihan),
                                fontFamily = JetBrainsMonoFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = if (tagihan.statusLunas) BrandPrimaryLight else BrandAccentLight
                            )
                        }
                    }

                    val progress = (tagihan.totalTerbayar.toFloat() / tagihan.totalTagihan.toFloat())
                        .coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(99.dp)),
                        color = BrandAccent,
                        trackColor = Color.White.copy(alpha = 0.25f)
                    )
                }
            }
        }

        // Info / Webhook Feedback Banner
        val bannerText = feedbackBanner ?: localInfoMessage
        if (bannerText != null) {
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
                        text = bannerText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BrandPrimaryDark,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            viewModel.dismissBanner()
                            localInfoMessage = null
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = BrandPrimaryDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Virtual Account Cepat & Tombol Bayar Sekarang (Payment Gateway)
        SiakadCard(contentPadding = PaddingValues(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Payment Gateway Terintegrasi",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Midtrans Snap • BSI VA • BRI VA • QRIS Otomatis",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                MonoCodeChip(
                    text = "WEBHOOK REALTIME",
                    backgroundColor = StatusInfoBg,
                    textColor = StatusInfo
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // BSI Virtual Account Row
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceMuted,
                border = BorderStroke(1.dp, BorderDefault),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Bank Syariah Indonesia (BSI VA)",
                            style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
                        )
                        Text(
                            text = tagihan.virtualAccountBsi,
                            fontFamily = JetBrainsMonoFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }
                    SecondaryOutlineButton(
                        text = "Salin VA",
                        leadingIcon = Icons.Default.ContentCopy,
                        height = 36.dp,
                        onClick = {
                            clipboardManager.setText(AnnotatedString(tagihan.virtualAccountBsi))
                            localInfoMessage = "Nomor VA BSI (${tagihan.virtualAccountBsi}) disalin!"
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!tagihan.statusLunas) {
                PrimaryEmeraldButton(
                    text = "Bayar Sekarang (${CurrencyFormatter.formatRupiah(tagihan.sisaTagihan)})",
                    leadingIcon = Icons.Default.Payment,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        selectedCicilanForSnap = tagihan.cicilanList.firstOrNull { !it.isPaid }
                        showSnapGatewaySheet = true
                    }
                )
            } else {
                PrimaryEmeraldButton(
                    text = "Unduh Kwitansi PDF Resmi (Lunas)",
                    leadingIcon = Icons.Default.Download,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showKwitansiPreviewDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SecondaryOutlineButton(
                    text = "Upload Bukti Manual",
                    leadingIcon = Icons.Default.UploadFile,
                    modifier = Modifier.weight(1f),
                    onClick = { showUploadManualDialog = true }
                )
                SecondaryOutlineButton(
                    text = "Kwitansi PDF",
                    leadingIcon = Icons.Default.ReceiptLong,
                    modifier = Modifier.weight(1f),
                    onClick = { showKwitansiPreviewDialog = true }
                )
            }
        }

        // Daftar Rincian Termin Cicilan (`cicilan_tagihans`)
        SectionHeader(
            title = "Rincian Termin Cicilan (cicilan_tagihans)",
            subtitle = "Skema pembayaran bertahap STAI Al-Yasini"
        )

        tagihan.cicilanList.forEach { cicilan ->
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
                            text = "TERMIN #${cicilan.terminKe}",
                            backgroundColor = if (cicilan.isPaid) BrandPrimaryLight else SurfaceMuted,
                            textColor = if (cicilan.isPaid) BrandPrimaryDark else TextPrimary
                        )
                        Text(
                            text = CurrencyFormatter.formatRupiah(cicilan.nominal),
                            fontFamily = JetBrainsMonoFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                    }
                    StatusBadge(
                        text = if (cicilan.isPaid) "Lunas" else "Belum Dibayar",
                        variant = if (cicilan.isPaid) BadgeVariant.SUCCESS else BadgeVariant.WARNING
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = cicilan.keterangan,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (cicilan.isPaid) {
                    Text(
                        text = "Dibayar pada ${cicilan.tanggalBayar} via ${cicilan.metodeBayar}",
                        style = MaterialTheme.typography.bodySmall.copy(color = StatusSuccess)
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Jatuh Tempo: ${cicilan.jatuhTempo}",
                            style = MaterialTheme.typography.bodySmall.copy(color = StatusWarning)
                        )
                        PrimaryEmeraldButton(
                            text = "Bayar Termin Ini",
                            height = 36.dp,
                            onClick = {
                                selectedCicilanForSnap = cicilan
                                showSnapGatewaySheet = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MidtransSnapPaymentGatewaySheet(
    nomorInvoice: String,
    snapToken: String,
    vaBsi: String,
    vaBri: String,
    nominalBayar: Long,
    keterangan: String,
    onCopyVa: (String) -> Unit,
    onConfirmWebhookPaid: (String) -> Unit,
    onClose: () -> Unit
) {
    var selectedChannel by remember { mutableStateOf("QRIS Dinamis Midtrans") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
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
                    text = "Midtrans Snap Payment Gateway",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Token: $snapToken",
                    fontFamily = JetBrainsMonoFamily,
                    fontSize = 11.sp,
                    color = BrandPrimary
                )
            }
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
            }
        }

        SiakadCard(
            containerColor = BrandPrimaryLight,
            borderColor = BrandPrimary.copy(alpha = 0.3f),
            contentPadding = PaddingValues(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = keterangan,
                        style = MaterialTheme.typography.labelLarge.copy(color = BrandPrimaryDark)
                    )
                    Text(
                        text = nomorInvoice,
                        fontFamily = JetBrainsMonoFamily,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                Text(
                    text = CurrencyFormatter.formatRupiah(nominalBayar),
                    fontFamily = JetBrainsMonoFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = BrandPrimaryDark
                )
            }
        }

        Text(
            text = "Pilih Metode Pembayaran Otomatis",
            style = MaterialTheme.typography.titleSmall.copy(
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        )

        val channels = listOf(
            Triple("QRIS Dinamis Midtrans", "Bebas biaya admin • M-Banking / GoPay / OVO / Dana", Icons.Default.QrCode2),
            Triple("BSI Virtual Account", "No. VA: $vaBsi", Icons.Default.AccountBalance),
            Triple("BRI Virtual Account (BRIVA)", "No. VA: $vaBri", Icons.Default.CreditCard)
        )

        channels.forEach { (title, desc, icon) ->
            val selected = selectedChannel == title
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (selected) BrandPrimaryLight else SurfaceBase,
                border = BorderStroke(1.5.dp, if (selected) BrandPrimary else BorderDefault),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { selectedChannel = title }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selected) BrandPrimary else TextSecondary
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = desc,
                            fontFamily = if (desc.contains("VA:")) JetBrainsMonoFamily else MaterialTheme.typography.bodySmall.fontFamily,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    if (title.contains("Virtual Account")) {
                        IconButton(
                            onClick = {
                                onCopyVa(if (title.contains("BSI")) vaBsi else vaBri)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Salin VA",
                                tint = BrandPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        if (selectedChannel.contains("QRIS")) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderDefault),
                    modifier = Modifier.size(132.dp)
                ) {
                    Box(modifier = Modifier.padding(12.dp)) {
                        QrMatrixIllustration(seed = snapToken)
                    }
                }
            }
        }

        Divider(color = BorderDefault)

        PrimaryEmeraldButton(
            text = "Simulasikan Webhook Callback Lunas ($selectedChannel)",
            leadingIcon = Icons.Default.Sync,
            modifier = Modifier.fillMaxWidth(),
            onClick = { onConfirmWebhookPaid(selectedChannel) }
        )
    }
}

@Composable
private fun UploadBuktiManualDialog(
    sisaTagihan: Long,
    onDismiss: () -> Unit,
    onSubmitProof: (bank: String, noRek: String, nominal: Long) -> Unit
) {
    var bankPengirim by remember { mutableStateOf("Bank Syariah Indonesia (BSI)") }
    var noRekening by remember { mutableStateOf("") }
    var nominalStr by remember { mutableStateOf(sisaTagihan.toString()) }
    var fileName by remember { mutableStateOf<String?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upload Bukti Manual",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                SecondaryOutlineButton(
                    text = "Auto-Fill",
                    leadingIcon = Icons.Default.AutoFixHigh,
                    height = 32.dp,
                    onClick = {
                        bankPengirim = "BSI (Bank Syariah Indonesia)"
                        noRekening = "712984102948"
                        nominalStr = sisaTagihan.toString()
                        fileName = "bukti_transfer_ukt_fauzi.jpg"
                        validationError = null
                    }
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (validationError != null) {
                    Text(
                        text = validationError ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFDC2626),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                OutlinedTextField(
                    value = bankPengirim,
                    onValueChange = { bankPengirim = it; validationError = null },
                    label = { Text("Bank Pengirim") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = noRekening,
                    onValueChange = { noRekening = it; validationError = null },
                    label = { Text("Nomor Rekening Pengirim") },
                    placeholder = { Text("Contoh: 712984102948") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = nominalStr,
                    onValueChange = { nominalStr = it; validationError = null },
                    label = { Text("Nominal Ditransfer (Rp)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(10.dp)
                )
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceMuted,
                    border = BorderStroke(1.dp, BorderDefault),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            fileName = "bukti_transfer_${System.currentTimeMillis() % 10000}.jpg"
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = null,
                            tint = BrandPrimary
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = fileName ?: "Pilih File Foto Bukti Struk/ATM",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (fileName != null) FontWeight.Bold else FontWeight.Normal,
                                    color = if (fileName != null) BrandPrimaryDark else TextSecondary
                                )
                            )
                            Text(
                                text = if (fileName != null) "File terlampir siap diverifikasi" else "Klik untuk simulasi lampirkan foto",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            PrimaryEmeraldButton(
                text = "Kirim Bukti Pembayaran",
                onClick = {
                    if (bankPengirim.isBlank() || noRekening.isBlank() || nominalStr.isBlank()) {
                        validationError = "Bank, No Rekening, dan Nominal wajib diisi!"
                    } else if (fileName == null) {
                        validationError = "Silakan klik lampiran untuk melampirkan file bukti transfer!"
                    } else {
                        val nom = nominalStr.toLongOrNull() ?: sisaTagihan
                        onSubmitProof(bankPengirim, noRekening, nom)
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
        shape = RoundedCornerShape(16.dp)
    )
}
