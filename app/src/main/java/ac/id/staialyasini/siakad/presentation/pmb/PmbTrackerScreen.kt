package ac.id.staialyasini.siakad.presentation.pmb

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
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
import ac.id.staialyasini.siakad.core.ui.theme.StatusSuccess
import ac.id.staialyasini.siakad.core.ui.theme.StatusSuccessBg
import ac.id.staialyasini.siakad.core.ui.theme.SurfaceBase
import ac.id.staialyasini.siakad.core.ui.theme.SurfaceMuted
import ac.id.staialyasini.siakad.core.ui.theme.TextMuted
import ac.id.staialyasini.siakad.core.ui.theme.TextPrimary
import ac.id.staialyasini.siakad.core.ui.theme.TextSecondary
import ac.id.staialyasini.siakad.core.util.CurrencyFormatter
import ac.id.staialyasini.siakad.domain.model.BerkasPmbItem
import ac.id.staialyasini.siakad.domain.model.PmbProgressState
import ac.id.staialyasini.siakad.domain.repository.SiakadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PmbViewModel @Inject constructor(
    private val repository: SiakadRepository
) : ViewModel() {

    val pmbState: StateFlow<PmbProgressState> = repository.pmbProgressState

    private val _feedbackBanner = MutableStateFlow<String?>(null)
    val feedbackBanner: StateFlow<String?> = _feedbackBanner.asStateFlow()

    private val _errorBanner = MutableStateFlow<String?>(null)
    val errorBanner: StateFlow<String?> = _errorBanner.asStateFlow()

    fun dismissBanner() {
        _feedbackBanner.value = null
        _errorBanner.value = null
    }

    fun simpanBiodataPendaftar(
        namaLengkap: String,
        gelombang: String,
        pilihanProdi1: String,
        pilihanProdi2: String,
        onSuccessClose: () -> Unit
    ) {
        if (namaLengkap.isBlank() || pilihanProdi1.isBlank() || pilihanProdi2.isBlank()) {
            _errorBanner.value = "Validasi Gagal: Nama Lengkap, Pilihan Prodi 1, dan Pilihan Prodi 2 wajib diisi!"
            return
        }
        viewModelScope.launch {
            val res = repository.simpanBiodataPmb(
                namaLengkap = namaLengkap,
                gelombang = gelombang,
                pilihanProdi1 = pilihanProdi1,
                pilihanProdi2 = pilihanProdi2
            )
            res.fold(
                onSuccess = {
                    _errorBanner.value = null
                    _feedbackBanner.value = "Data berhasil disimpan! Biodata pendaftar '$namaLengkap' telah tersimpan permanen di Room Database."
                    onSuccessClose()
                },
                onFailure = { err ->
                    _errorBanner.value = err.message
                }
            )
        }
    }

    fun tambahAtauSimpanBerkasPendaftar(
        berkasId: Long?,
        namaBerkas: String,
        wajib: Boolean,
        onSuccessClose: () -> Unit
    ) {
        if (namaBerkas.isBlank()) {
            _errorBanner.value = "Validasi Gagal: Nama berkas persyaratan tidak boleh kosong!"
            return
        }
        viewModelScope.launch {
            val res = repository.tambahAtauSimpanBerkasPmb(
                berkasId = berkasId,
                namaBerkas = namaBerkas,
                wajib = wajib
            )
            res.fold(
                onSuccess = {
                    _errorBanner.value = null
                    val label = if (berkasId == null) "ditambahkan & disimpan" else "diperbarui"
                    _feedbackBanner.value = "Data berhasil disimpan! Berkas '$namaBerkas' telah $label di Room Database SQLite."
                    onSuccessClose()
                },
                onFailure = { err ->
                    _errorBanner.value = err.message
                }
            )
        }
    }

    fun hapusBerkasPendaftar(berkasId: Long, namaBerkas: String) {
        viewModelScope.launch {
            repository.hapusBerkasPmb(berkasId)
            _errorBanner.value = null
            _feedbackBanner.value = "Berkas '$namaBerkas' berhasil dihapus dari Room Database."
        }
    }

    fun cameraReuploadBerkas(berkasId: Long) {
        viewModelScope.launch {
            repository.reuploadBerkasPmb(berkasId)
            _errorBanner.value = null
            _feedbackBanner.value = "Foto berkas berhasil diunggah ulang & disimpan permanen (Terverifikasi)."
        }
    }

    fun bayarDaftarUlangViaGateway() {
        viewModelScope.launch {
            repository.bayarDaftarUlangPmb()
            _errorBanner.value = null
            _feedbackBanner.value = "Pembayaran Daftar Ulang Lunas! NIM Resmi 2026010194 telah terbit & disimpan di Room Database."
        }
    }
}

@Composable
fun PmbTrackerScreen(
    viewModel: PmbViewModel
) {
    val state by viewModel.pmbState.collectAsStateWithLifecycle()
    val feedbackBanner by viewModel.feedbackBanner.collectAsStateWithLifecycle()
    val errorBanner by viewModel.errorBanner.collectAsStateWithLifecycle()

    var showEditBiodataDialog by remember { mutableStateOf(false) }
    var showAddBerkasDialog by remember { mutableStateOf(false) }
    var editingBerkasItem by remember { mutableStateOf<BerkasPmbItem?>(null) }
    var showKartuUjianPmbPreview by remember { mutableStateOf(false) }
    var showBuktiLulusPmbPreview by remember { mutableStateOf(false) }

    // Dialog Cetak Kartu Peserta Ujian PMB (PDF)
    if (showKartuUjianPmbPreview) {
        OfficialDocumentPreviewDialog(
            documentTitle = "KARTU PESERTA UJIAN SELEKSI MASUK PMB",
            documentNumber = "KPU-PMB/2026/042",
            studentName = state.namaLengkap,
            studentIdentifier = state.nomorPendaftaran,
            prodi = "${state.pilihanProdi1} (Pilihan 1)",
            extraMetadata = listOf(
                "Pilihan Prodi 2" to state.pilihanProdi2,
                "Gelombang" to state.gelombang,
                "Jadwal Ujian CBT" to "14 Juli 2026 • 08:30 WIB",
                "Lokasi Tes" to "Lab Komputer CBT Gedung Al-Ghazali"
            ),
            itemsTable = listOf(
                Triple("TPA", "Tes Potensi Akademik", "60 Menit"),
                Triple("TBI/TBA", "Bahasa Arab & Dirasah Islamiyah", "45 Menit"),
                Triple("ENG", "Bahasa Inggris Dasar", "45 Menit"),
                Triple("WCR", "Wawancara & Baca Kitab Kuning", "30 Menit")
            ),
            tableHeader = Triple("Kode Tes", "Materi Ujian Seleksi", "Alokasi Waktu"),
            totalSummary = "Status Keikutsertaan" to "TERDAFTAR RESMI",
            signatoryTitle = "Ketua Panitia PMB",
            signatoryName = "Dr. Hj. Siti Marifah, M.Pd.I.",
            signatoryNidn = "NIDN: 0701058002",
            onDismiss = { showKartuUjianPmbPreview = false }
        )
    }

    // Dialog Cetak Surat Keterangan Kelulusan PMB (PDF)
    if (showBuktiLulusPmbPreview) {
        OfficialDocumentPreviewDialog(
            documentTitle = "SURAT KETERANGAN KELULUSAN SELEKSI PMB",
            documentNumber = "SK-PMB/STAI-ASY/LULUS/VII/2026",
            studentName = state.namaLengkap,
            studentIdentifier = state.nomorPendaftaran,
            prodi = state.pilihanProdi1,
            extraMetadata = listOf(
                "NIM Resmi Terbit" to (state.nimTerbit ?: "Menunggu Daftar Ulang"),
                "Jalur Penerimaan" to state.gelombang,
                "Status Registrasi" to if (state.isDaftarUlangLunas) "DAFTAR ULANG LUNAS (NIM AKTIF)" else "MENUNGGU DAFTAR ULANG"
            ),
            itemsTable = listOf(
                Triple("Verifikasi", "Ijazah & Transkrip Nilai Asli", "Terverifikasi Valid"),
                Triple("Biaya Registrasi", "Daftar Ulang & Jas Almamater", if (state.isDaftarUlangLunas) "LUNAS" else "Belum Lunas"),
                Triple("Nomor Induk", "Penerbitan NIM Resmi SIAKAD", state.nimTerbit ?: "Dalam Proses")
            ),
            tableHeader = Triple("Komponen", "Keterangan Persyaratan", "Status"),
            totalSummary = "Hasil Seleksi" to "DITERIMA SEBAGAI MAHASISWA BARU",
            signatoryTitle = "Ketua STAI Al-Yasini Pasuruan",
            signatoryName = "Dr. KH. Ahmad Muzakki, M.Ag.",
            signatoryNidn = "NIDN: 2114087801",
            onDismiss = { showBuktiLulusPmbPreview = false }
        )
    }

    val steps = listOf(
        "Isi Biodata",
        "Upload Berkas",
        "Verifikasi Panitia",
        "Ujian Seleksi",
        "Pengumuman Kelulusan",
        "Bayar Daftar Ulang & Terbit NIM"
    )

    // Dialog Form Isi / Edit & Simpan Biodata Pendaftar PMB (Room SQLite)
    if (showEditBiodataDialog) {
        var namaInput by remember(state) { mutableStateOf(state.namaLengkap) }
        var gelombangInput by remember(state) { mutableStateOf(state.gelombang) }
        var prodi1Input by remember(state) { mutableStateOf(state.pilihanProdi1) }
        var prodi2Input by remember(state) { mutableStateOf(state.pilihanProdi2) }
        var localError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showEditBiodataDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Isi / Edit Biodata PMB",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Room SQLite: pmb_biodata",
                            fontFamily = JetBrainsMonoFamily,
                            fontSize = 11.sp,
                            color = BrandPrimary
                        )
                    }
                    SecondaryOutlineButton(
                        text = "Auto-Fill",
                        leadingIcon = Icons.Default.AutoFixHigh,
                        height = 32.dp,
                        onClick = {
                            namaInput = "Muhammad Fajar Shodiq"
                            gelombangInput = "Gelombang I - Reguler"
                            prodi1Input = "Pendidikan Agama Islam (S1)"
                            prodi2Input = "Ekonomi Syariah (S1)"
                            localError = null
                        }
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (localError != null) {
                        Text(
                            text = localError ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = StatusDanger,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    OutlinedTextField(
                        value = namaInput,
                        onValueChange = { namaInput = it; localError = null },
                        label = { Text("Nama Lengkap Pendaftar*") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = gelombangInput,
                        onValueChange = { gelombangInput = it; localError = null },
                        label = { Text("Gelombang Pendaftaran") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = prodi1Input,
                        onValueChange = { prodi1Input = it; localError = null },
                        label = { Text("Pilihan Program Studi 1*") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = prodi2Input,
                        onValueChange = { prodi2Input = it; localError = null },
                        label = { Text("Pilihan Program Studi 2*") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                PrimaryEmeraldButton(
                    text = "Simpan Biodata Permanen",
                    leadingIcon = Icons.Default.Save,
                    onClick = {
                        if (namaInput.isBlank() || prodi1Input.isBlank() || prodi2Input.isBlank()) {
                            localError = "Nama Lengkap, Pilihan Prodi 1, dan Pilihan Prodi 2 wajib diisi!"
                        } else {
                            viewModel.simpanBiodataPendaftar(
                                namaLengkap = namaInput,
                                gelombang = gelombangInput,
                                pilihanProdi1 = prodi1Input,
                                pilihanProdi2 = prodi2Input,
                                onSuccessClose = { showEditBiodataDialog = false }
                            )
                        }
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showEditBiodataDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Dialog Form Tambah / Edit & Simpan Berkas Persyaratan PMB (Room SQLite)
    if (showAddBerkasDialog || editingBerkasItem != null) {
        val initial = editingBerkasItem
        var namaBerkasInput by remember(initial) { mutableStateOf(initial?.namaBerkas ?: "") }
        var wajibChecked by remember(initial) { mutableStateOf(initial?.wajib ?: true) }
        var localError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = {
                showAddBerkasDialog = false
                editingBerkasItem = null
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (initial == null) "Tambah Berkas PMB" else "Ubah Data Berkas",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Room SQLite: pmb_berkas",
                            fontFamily = JetBrainsMonoFamily,
                            fontSize = 11.sp,
                            color = BrandPrimary
                        )
                    }
                    SecondaryOutlineButton(
                        text = "Auto-Fill",
                        leadingIcon = Icons.Default.AutoFixHigh,
                        height = 32.dp,
                        onClick = {
                            namaBerkasInput = "Sertifikat Hafalan Al-Qur'an & Piagam Prestasi"
                            wajibChecked = false
                            localError = null
                        }
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (localError != null) {
                        Text(
                            text = localError ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = StatusDanger,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    OutlinedTextField(
                        value = namaBerkasInput,
                        onValueChange = { namaBerkasInput = it; localError = null },
                        label = { Text("Nama Dokumen / Berkas Persyaratan*") },
                        placeholder = { Text("Contoh: Surat Keterangan Sehat /SKCK") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = wajibChecked,
                            onCheckedChange = { wajibChecked = it },
                            colors = CheckboxDefaults.colors(checkedColor = BrandPrimary)
                        )
                        Text(
                            text = "Dokumen Syarat Wajib Pendaftaran",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            },
            confirmButton = {
                PrimaryEmeraldButton(
                    text = "Simpan Berkas ke Room",
                    leadingIcon = Icons.Default.Save,
                    onClick = {
                        if (namaBerkasInput.isBlank()) {
                            localError = "Nama berkas persyaratan tidak boleh kosong!"
                        } else {
                            viewModel.tambahAtauSimpanBerkasPendaftar(
                                berkasId = initial?.id,
                                namaBerkas = namaBerkasInput,
                                wajib = wajibChecked,
                                onSuccessClose = {
                                    showAddBerkasDialog = false
                                    editingBerkasItem = null
                                }
                            )
                        }
                    }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddBerkasDialog = false
                        editingBerkasItem = null
                    }
                ) {
                    Text("Batal", color = TextSecondary)
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
            .padding(bottom = 28.dp)
    ) {
        // Header Calon Mahasiswa Baru (PMB)
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
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(text = "PORTAL PMB AL-YASINI • ROOM SQLITE", variant = BadgeVariant.ACCENT, leadingDot = false)
                    Surface(
                        color = Color.White.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = state.nomorPendaftaran,
                            fontFamily = JetBrainsMonoFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = BrandAccentLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = state.namaLengkap,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "${state.gelombang} • Pilihan 1: ${state.pilihanProdi1}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )
                Text(
                    text = "Pilihan 2: ${state.pilihanProdi2}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BrandAccentLight.copy(alpha = 0.9f)
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PrimaryEmeraldButton(
                        text = "Isi / Edit & Simpan Biodata PMB",
                        leadingIcon = Icons.Default.Edit,
                        containerColor = BrandAccent,
                        height = 40.dp,
                        onClick = { showEditBiodataDialog = true }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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

            // 1. STEPPER VISUAL TAHAPAN PENDAFTARAN PMB
            SiakadCard(contentPadding = PaddingValues(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tracker Status Pendaftaran PMB",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    SecondaryOutlineButton(
                        text = "Edit Biodata",
                        leadingIcon = Icons.Default.Edit,
                        borderColor = BrandPrimary,
                        contentColor = BrandPrimaryDark,
                        height = 34.dp,
                        onClick = { showEditBiodataDialog = true }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                steps.forEachIndexed { idx, label ->
                    val isCompleted = idx < state.currentStepIndex || state.isDaftarUlangLunas
                    val isCurrent = idx == state.currentStepIndex && !state.isDaftarUlangLunas

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCompleted -> StatusSuccess
                                        isCurrent -> BrandAccent
                                        else -> SurfaceMuted
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = "${idx + 1}",
                                    fontFamily = JetBrainsMonoFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isCurrent) Color.White else TextMuted
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = if (isCompleted || isCurrent) TextPrimary else TextMuted,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold
                                )
                            )
                        }

                        StatusBadge(
                            text = when {
                                isCompleted -> "Selesai"
                                isCurrent -> "Tahap Aktif"
                                else -> "Menunggu"
                            },
                            variant = when {
                                isCompleted -> BadgeVariant.SUCCESS
                                isCurrent -> BadgeVariant.ACCENT
                                else -> BadgeVariant.NEUTRAL
                            },
                            leadingDot = false
                        )
                    }
                }
            }

            // 2. KARTU UJIAN & PEMBAYARAN DAFTAR ULANG / TERBIT NIM
            SiakadCard(
                containerColor = if (state.isDaftarUlangLunas) StatusSuccessBg else BrandPrimaryLight,
                borderColor = BrandPrimary.copy(alpha = 0.35f),
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hasil Seleksi PMB (Skor: ${state.nilaiUjianSeleksi}/100)",
                            style = MaterialTheme.typography.labelLarge.copy(color = BrandPrimaryDark)
                        )
                        Text(
                            text = state.statusKelulusan,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = BrandPrimaryDark,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    StatusBadge(
                        text = if (state.isDaftarUlangLunas) "NIM TERBIT" else "SIAP DAFTAR ULANG",
                        variant = BadgeVariant.SUCCESS
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (state.isDaftarUlangLunas && state.nimTerbit != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, StatusSuccess),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Selamat! NIM Resmi Anda Telah Terbit:",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = state.nimTerbit ?: "",
                                    fontFamily = JetBrainsMonoFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = BrandPrimaryDark
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = StatusSuccess,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Tagihan Daftar Ulang & UKT Awal: ${CurrencyFormatter.formatRupiah(state.tagihanDaftarUlang)}",
                        fontFamily = JetBrainsMonoFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    PrimaryEmeraldButton(
                        text = "Bayar Daftar Ulang & Terbitkan NIM Sekarang",
                        leadingIcon = Icons.Default.Payment,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = viewModel::bayarDaftarUlangViaGateway
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                SecondaryOutlineButton(
                    text = "Unduh Kartu Peserta Ujian PMB (PDF)",
                    leadingIcon = Icons.Default.Download,
                    borderColor = BrandPrimary,
                    contentColor = BrandPrimaryDark,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showKartuUjianPmbPreview = true }
                )

                if (state.statusKelulusan.contains("LULUS", ignoreCase = true)) {
                    Spacer(modifier = Modifier.height(8.dp))
                    PrimaryEmeraldButton(
                        text = "Unduh Surat Keterangan Lulus PMB (PDF)",
                        leadingIcon = Icons.Default.Download,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showBuktiLulusPmbPreview = true }
                    )
                }
            }

            // 3. CRUD BERKAS PERSYARATAN PMB (TAMBAH, SIMPAN, EDIT, HAPUS DI ROOM DATABASE)
            SectionHeader(
                title = "Berkas Persyaratan PMB (${state.berkasList.size} Berkas)",
                subtitle = "Tersimpan permanen di tabel SQLite pmb_berkas",
                actionText = "+ Tambah Berkas",
                onActionClick = { showAddBerkasDialog = true }
            )

            PrimaryEmeraldButton(
                text = "Tambah & Simpan Berkas Persyaratan Baru",
                leadingIcon = Icons.Default.AddCircleOutline,
                modifier = Modifier.fillMaxWidth(),
                onClick = { showAddBerkasDialog = true }
            )

            state.berkasList.forEach { berkas ->
                val isRejected = berkas.status.equals("Ditolak", ignoreCase = true)
                SiakadCard(
                    containerColor = if (isRejected) StatusDangerBg else Color.White,
                    borderColor = if (isRejected) StatusDanger.copy(alpha = 0.45f) else BrandPrimary.copy(alpha = 0.2f),
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = berkas.namaBerkas,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = if (berkas.wajib) "Syarat Wajib" else "Opsional",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        StatusBadge(
                            text = berkas.status,
                            variant = if (isRejected) BadgeVariant.DANGER else BadgeVariant.SUCCESS
                        )
                    }

                    if (isRejected && !berkas.alasanPenolakan.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = StatusDanger,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Alasan Ditolak Panitia: ${berkas.alasanPenolakan}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF991B1B),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        PrimaryEmeraldButton(
                            text = "Foto & Unggah Ulang via Kamera (Auto-Compress)",
                            leadingIcon = Icons.Default.CameraAlt,
                            containerColor = StatusDanger,
                            modifier = Modifier.fillMaxWidth(),
                            height = 40.dp,
                            onClick = { viewModel.cameraReuploadBerkas(berkas.id) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = BorderDefault)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SecondaryOutlineButton(
                            text = "Edit Berkas",
                            leadingIcon = Icons.Default.Edit,
                            borderColor = BrandPrimary,
                            contentColor = BrandPrimaryDark,
                            height = 34.dp,
                            onClick = { editingBerkasItem = berkas }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        SecondaryOutlineButton(
                            text = "Hapus",
                            leadingIcon = Icons.Default.DeleteOutline,
                            borderColor = StatusDanger,
                            contentColor = StatusDanger,
                            height = 34.dp,
                            onClick = {
                                viewModel.hapusBerkasPendaftar(berkas.id, berkas.namaBerkas)
                            }
                        )
                    }
                }
            }
        }
    }
}
