package ac.id.staialyasini.siakad.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import ac.id.staialyasini.siakad.core.ui.theme.SurfaceCard
import ac.id.staialyasini.siakad.core.ui.theme.SurfaceMuted
import ac.id.staialyasini.siakad.core.ui.theme.TextMuted
import ac.id.staialyasini.siakad.core.ui.theme.TextPrimary
import ac.id.staialyasini.siakad.core.ui.theme.TextSecondary

/**
 * Semantic Badge Variant identik dengan token Web SIAKAD STAI Al-Yasini
 */
enum class BadgeVariant {
    SUCCESS,
    WARNING,
    DANGER,
    INFO,
    ACCENT,
    NEUTRAL
}

/**
 * Card utama SIAKAD dengan sudut membulat 16.dp, background putih bersih (#FFFFFF),
 * dan border tipis 1.dp (#E2E8F0).
 */
@Composable
fun SiakadCard(
    modifier: Modifier = Modifier,
    containerColor: Color = SurfaceCard,
    borderColor: Color = BorderDefault,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val cardModifier = if (onClick != null) {
        modifier
            .clip(shape)
            .clickable { onClick() }
    } else {
        modifier
    }

    Card(
        modifier = cardModifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            content = content
        )
    }
}

/**
 * Badge status dengan warna latar lembut dan teks kontras tinggi sesuai semantic status.
 */
@Composable
fun StatusBadge(
    text: String,
    variant: BadgeVariant,
    modifier: Modifier = Modifier,
    leadingDot: Boolean = true
) {
    val (bgColor, textColor) = when (variant) {
        BadgeVariant.SUCCESS -> StatusSuccessBg to Color(0xFF047857)
        BadgeVariant.WARNING -> StatusWarningBg to Color(0xFFB45309)
        BadgeVariant.DANGER -> StatusDangerBg to Color(0xFFB91C1C)
        BadgeVariant.INFO -> StatusInfoBg to Color(0xFF1D4ED8)
        BadgeVariant.ACCENT -> BrandAccentLight to Color(0xFF92400E)
        BadgeVariant.NEUTRAL -> SurfaceMuted to TextSecondary
    }

    val dotColor = when (variant) {
        BadgeVariant.SUCCESS -> StatusSuccess
        BadgeVariant.WARNING -> StatusWarning
        BadgeVariant.DANGER -> StatusDanger
        BadgeVariant.INFO -> StatusInfo
        BadgeVariant.ACCENT -> BrandAccent
        BadgeVariant.NEUTRAL -> TextMuted
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = bgColor,
        border = BorderStroke(1.dp, dotColor.copy(alpha = 0.22f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (leadingDot) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = textColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Tombol utama Emerald (#0D7C66) dengan RoundedCornerShape(12.dp).
 */
@Composable
fun PrimaryEmeraldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    containerColor: Color = BrandPrimary,
    contentColor: Color = Color.White,
    height: Dp = 48.dp
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier.height(height),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = BorderDefault,
            disabledContentColor = TextMuted
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                color = if (enabled) contentColor else TextMuted,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

/**
 * Tombol sekunder outline dengan RoundedCornerShape(12.dp).
 */
@Composable
fun SecondaryOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    borderColor: Color = BorderDefault,
    contentColor: Color = TextPrimary,
    height: Dp = 46.dp
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(height),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (enabled) borderColor else BorderDefault),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                color = contentColor,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

/**
 * Komponen teks Monospace (JetBrains Mono) untuk NIM, NIDN, Kode MK, dan Nomor Virtual Account / Invoice.
 */
@Composable
fun MonoCodeChip(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = SurfaceMuted,
    textColor: Color = TextPrimary,
    fontSize: TextUnit = 12.sp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .border(1.dp, BorderDefault, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = JetBrainsMonoFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = fontSize,
            color = textColor
        )
    }
}

/**
 * Section Header standar halaman SIAKAD
 */
@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                )
            }
        }
        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = BrandPrimary,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onActionClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

/**
 * Dialog Pratinjau Dokumen Akademik Resmi STAI Al-Yasini Pasuruan (PDF Preview).
 * Menampilkan Kop Surat Resmi, data mahasiswa, rincian tabel, dan tanda tangan digital/stempel sah.
 */
@Composable
fun OfficialDocumentPreviewDialog(
    documentTitle: String,
    documentNumber: String,
    studentName: String,
    studentIdentifier: String,
    prodi: String,
    extraMetadata: List<Pair<String, String>> = emptyList(),
    itemsTable: List<Triple<String, String, String>> = emptyList(), // Col1: Kode/Item, Col2: Nama/Ket, Col3: Nilai/SKS/Nominal
    tableHeader: Triple<String, String, String> = Triple("Kode", "Mata Kuliah / Keterangan", "SKS / Nilai"),
    totalSummary: Pair<String, String>? = null,
    signatoryTitle: String = "Ketua STAI Al-Yasini / Dosen Wali",
    signatoryName: String = "Dr. KH. Ahmad Muzakki, M.Ag.",
    signatoryNidn: String = "NIDN: 2114087801",
    onDismiss: () -> Unit,
    onPrintOrSave: (String) -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // KOP SURAT RESMI STAI AL-YASINI
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(BrandPrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AY",
                            fontFamily = JetBrainsMonoFamily,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimaryDark,
                            fontSize = 14.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SEKOLAH TINGGI AGAMA ISLAM AL-YASINI",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimaryDark,
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = "SK Kemenag RI No. 351/2008 • Terakreditasi BAN-PT",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 9.sp
                            )
                        )
                        Text(
                            text = "Jl. Pesantren Terpadu Al-Yasini No. 01 Wonorejo Pasuruan 67173",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 8.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(thickness = 2.dp, color = BrandPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                HorizontalDivider(thickness = 1.dp, color = BrandAccent)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Judul Dokumen & Nomor Surat
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BrandPrimaryLight,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = documentTitle,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimaryDark,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Text(
                        text = "Nomor: $documentNumber",
                        fontFamily = JetBrainsMonoFamily,
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }

                // Identitas Mahasiswa
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceMuted,
                    border = BorderStroke(1.dp, BorderDefault),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Nama", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                            Text(text = studentName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "NIM / ID", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                            Text(text = studentIdentifier, fontFamily = JetBrainsMonoFamily, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Prodi", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                            Text(text = prodi, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
                        }
                        extraMetadata.forEach { (k, v) ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = k, style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                                Text(text = v, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
                            }
                        }
                    }
                }

                // Tabel Item (Matakuliah / Nilai / Tagihan)
                if (itemsTable.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, BorderDefault),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            // Header Tabel
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(BrandPrimaryLight)
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = tableHeader.first, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = BrandPrimaryDark), modifier = Modifier.weight(0.28f))
                                Text(text = tableHeader.second, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = BrandPrimaryDark), modifier = Modifier.weight(0.50f))
                                Text(text = tableHeader.third, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = BrandPrimaryDark), textAlign = TextAlign.End, modifier = Modifier.weight(0.22f))
                            }
                            HorizontalDivider(color = BorderDefault)

                            // Baris Tabel
                            itemsTable.forEachIndexed { idx, (c1, c2, c3) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (idx % 2 == 0) Color.White else SurfaceMuted)
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = c1, fontFamily = JetBrainsMonoFamily, fontSize = 10.sp, modifier = Modifier.weight(0.28f))
                                    Text(text = c2, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), modifier = Modifier.weight(0.50f))
                                    Text(text = c3, fontFamily = JetBrainsMonoFamily, fontWeight = FontWeight.Bold, fontSize = 10.sp, textAlign = TextAlign.End, color = BrandPrimaryDark, modifier = Modifier.weight(0.22f))
                                }
                                if (idx < itemsTable.lastIndex) {
                                    HorizontalDivider(color = BorderDefault.copy(alpha = 0.5f))
                                }
                            }

                            if (totalSummary != null) {
                                HorizontalDivider(color = BrandPrimary)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(BrandPrimaryLight)
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = totalSummary.first, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                    Text(text = totalSummary.second, fontFamily = JetBrainsMonoFamily, fontWeight = FontWeight.Bold, color = BrandPrimaryDark)
                                }
                            }
                        }
                    }
                }

                // Pengesahan / Stempel & Tanda Tangan Digital
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceMuted,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(text = "Pasuruan, 08 Oktober 2026", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = TextMuted))
                            Text(text = signatoryTitle, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(14.dp))
                                Text(text = "TERVALIDASI ELEKTRONIK", style = MaterialTheme.typography.labelSmall.copy(color = StatusSuccess, fontWeight = FontWeight.Bold, fontSize = 9.sp))
                            }
                            Text(text = signatoryName, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp))
                            Text(text = signatoryNidn, fontFamily = JetBrainsMonoFamily, fontSize = 9.sp, color = TextMuted)
                        }

                        // Stempel Barcode Verifikasi PDDIKTI / SIAKAD
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(modifier = Modifier.padding(4.dp), contentAlignment = Alignment.Center) {
                                SiakadQrIllustration(seed = documentNumber)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            PrimaryEmeraldButton(
                text = "Cetak / Simpan PDF",
                leadingIcon = Icons.Default.Download,
                height = 42.dp,
                onClick = {
                    onPrintOrSave(documentNumber)
                    onDismiss()
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = TextSecondary)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * Ilustrasi QR Code matriks untuk stempel verifikasi dokumen dan scan barcode.
 */
@Composable
fun SiakadQrIllustration(seed: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
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

