# Diagram Flowchart Sistem Mobile SIAKAD STAI Al-Yasini

Dokumen ini berisi rangkaian **Diagram Flowchart** yang merepresentasikan alur sistem, logika bisnis, dan arsitektur dari aplikasi **SIAKAD STAI Al-Yasini Mobile** berdasarkan implementasi kode sumber yang telah terbentuk.

---

## 1. Master Flowchart: Autentikasi & Multi-Role Routing

Merepresentasikan alur masuk pengguna pada [`AppNavHost.kt`](file:///Users/Maheswara/AndroidStudioProjects/siakad-kampus-alyasini-mobile/app/src/main/java/ac/id/staialyasini/siakad/presentation/navigation/AppNavHost.kt), [`LoginScreen.kt`](file:///Users/Maheswara/AndroidStudioProjects/siakad-kampus-alyasini-mobile/app/src/main/java/ac/id/staialyasini/siakad/presentation/auth/LoginScreen.kt), dan [`AuthViewModel.kt`](file:///Users/Maheswara/AndroidStudioProjects/siakad-kampus-alyasini-mobile/app/src/main/java/ac/id/staialyasini/siakad/presentation/auth/AuthViewModel.kt).

```mermaid
flowchart TD
    Start(["Mulai Aplikasi (MainActivity)"]) --> CheckSession{"Cek Sesi Aktif di SessionDataStore"}
    
    CheckSession -- "Belum Login" --> LoginScreen["Tampilkan LoginScreen"]
    CheckSession -- "Sesi Tersimpan" --> DetectRole["Baca UserRole Aktif"]

    LoginScreen --> AuthMethod{"Pilih Metode Login"}
    
    AuthMethod -- "Input NIM / NIDN / No.PMB & Password" --> ValidateInput{"Input Valid?"}
    ValidateInput -- "Tidak" --> ErrorLogin["Tampilkan Pesan Error Validasi"] --> LoginScreen
    ValidateInput -- "Ya" --> ApiLogin["Request POST /api/v1/auth/login (Sanctum Token)"]
    
    AuthMethod -- "Biometric Fingerprint" --> BioCheck{"BiometricPrompt Valid?"}
    BioCheck -- "Gagal / Batal" --> LoginScreen
    BioCheck -- "Sukses" --> LoadBioSession["Muat Role Terakhir dari SessionDataStore"]
    
    AuthMethod -- "Quick Demo Role Selector" --> SelectRole["Pilih Role Simulasi (Mahasiswa / Dosen / Kaprodi / PMB)"]
    
    ApiLogin --> SaveSession["Simpan UserSession & Token ke SessionDataStore"]
    LoadBioSession --> SaveSession
    SelectRole --> SaveSession
    
    SaveSession --> DetectRole
    
    DetectRole --> RoleBranch{"Routing Berdasarkan UserRole"}
    
    RoleBranch -- "MAHASISWA" --> PortalMhs["Portal Mahasiswa (4 Tab BottomNav)"]
    RoleBranch -- "DOSEN / KAPRODI" --> PortalDosen["Portal Dosen & Kaprodi (3 Tab BottomNav)"]
    RoleBranch -- "CALON_MAHASISWA" --> PortalPmb["Portal PMB Calon Mahasiswa (2 Tab BottomNav)"]
    
    PortalMhs --> ActionTopBar{"Aksi TopAppBar"}
    PortalDosen --> ActionTopBar
    PortalPmb --> ActionTopBar
    
    ActionTopBar -- "Quick Role Switcher" --> SelectRole
    ActionTopBar -- "Klik Logout" --> ClearSession["POST /auth/logout & Hapus SessionDataStore"]
    ClearSession --> LoginScreen
```

---

## 2. Flowchart Modul A: Mahasiswa Aktif (UKT, KRS, Presensi QR GPS, & Akademik)

Merepresentasikan alur terintegrasi antara pembayaran UKT, pencekalan KRS, validasi SKS/jadwal, presensi QR dengan radius GPS Haversine, serta bimbingan skripsi pada [`MahasiswaViewModel.kt`](file:///Users/Maheswara/AndroidStudioProjects/siakad-kampus-alyasini-mobile/app/src/main/java/ac/id/staialyasini/siakad/presentation/mahasiswa/MahasiswaViewModel.kt) dan [`SiakadRepositoryImpl.kt`](file:///Users/Maheswara/AndroidStudioProjects/siakad-kampus-alyasini-mobile/app/src/main/java/ac/id/staialyasini/siakad/data/repository/SiakadRepositoryImpl.kt).

```mermaid
flowchart TD
    StartMhs(["Portal Mahasiswa Aktif"]) --> PilihMenu{"Pilih Menu Navigasi"}

    %% ================= ALUR 1: BERANDA & PRESENSI QR =================
    PilihMenu -- "1. Tab Beranda" --> DashboardMhs["MahasiswaDashboardScreen (Ringkasan IPK, UKT, Jadwal Hari Ini)"]
    DashboardMhs --> AksiBeranda{"Aksi pada Beranda"}
    
    AksiBeranda -- "Buka KTM & Kartu Ujian" --> CekUktKtm{"Status UKT Lunas?"}
    CekUktKtm -- "Lunas" --> ShowKtm["Tampilkan KTM Digital & Kartu Ujian (Aktif)"]
    CekUktKtm -- "Belum Lunas" --> LockKartu["Kartu Ujian Terkunci (Cekal Keuangan)"]
    
    AksiBeranda -- "Scan QR Presensi Kuliah" --> ScanQrSheet["Buka BottomSheet Scanner QR & Cek Lokasi GPS"]
    ScanQrSheet --> CheckMock{"LocationValidator: Terdeteksi Fake GPS / Mock?"}
    CheckMock -- "Ya (Mock Detected)" --> RejectMock["Tolak Presensi: Peringatan Anti-Fake GPS"]
    CheckMock -- "Tidak" --> CheckRadius{"Jarak Haversine <= Radius Kampus (150m)?"}
    CheckRadius -- "Di Luar Radius" --> RejectRadius["Tolak Presensi: Di Luar Area Kampus STAI Al-Yasini"]
    CheckRadius -- "Dalam Radius" --> SubmitPresensi["POST /mahasiswa/presensi/scan-qr & Update Kehadiran"]

    %% ================= ALUR 2: KEUANGAN UKT =================
    PilihMenu -- "2. Tab Keuangan UKT" --> UktScreen["UktPaymentScreen (Invoice, VA BSI/BRI, Rincian 3 Termin)"]
    UktScreen --> PilihBayar{"Pilih Pembayaran"}
    PilihBayar -- "Bayar Termin / Pelunasan Penuh" --> MetodeBayar["Pilih Metode (Midtrans QRIS / BSI VA / BRI VA)"]
    MetodeBayar --> SnapToken["POST /mahasiswa/pembayaran/snap-token"]
    SnapToken --> UpdateUkt["Update TagihanUktSummary (statusLunas = true) & Buka Kunci KRS"]

    %% ================= ALUR 3: KONTRAK KRS =================
    PilihMenu -- "3. Tab Kontrak KRS" --> KrsScreen["MahasiswaKrsScreen"]
    KrsScreen --> CekCekalUkt{"Apakah UKT Semester Aktif Lunas?"}
    CekCekalUkt -- "Belum Lunas" --> BannerCekal["Tampilkan Banner Cekal Keuangan & Arahkan ke Menu UKT"]
    BannerCekal --> UktScreen
    CekCekalUkt -- "Sudah Lunas" --> PilihKelas["Pilih / Toggle Kelas Mata Kuliah"]
    
    PilihKelas --> ValKuota{"Sisa Kuota > 0?"}
    ValKuota -- "Penuh" --> ErrKrs["Gagal Pilih: Kuota Kelas Penuh"]
    ValKuota -- "Tersedia" --> ValSks{"Total SKS <= Batas Maks (24 SKS)?"}
    ValSks -- "Melebihi 24 SKS" --> ErrKrs2["Gagal Pilih: Melebihi Batas Maksimal SKS"]
    ValSks -- "Valid" --> ValBentrok{"Ada Bentrok Hari & Jam Mulai?"}
    ValBentrok -- "Bentrok" --> ErrKrs3["Gagal Pilih: Jadwal Bentrok dengan MK Lain"]
    ValBentrok -- "Tidak Bentrok" --> UpdateDraftKrs["Tambahkan MK ke Draft KRS"]
    
    UpdateDraftKrs --> SubmitKrs{"Klik Ajukan KRS?"}
    SubmitKrs -- "Ya (Minimal 1 MK)" --> StatusDiajukan["Ubah Status KRS -> DIAJUKAN (Menunggu Dosen Wali)"]

    %% ================= ALUR 4: KHS & SKRIPSI =================
    PilihMenu -- "4. Tab KHS & Skripsi" --> AkademikScreen["MahasiswaAkademikSkripsiScreen"]
    AkademikScreen --> SubTabAkademik{"Pilih Sub-Fitur"}
    SubTabAkademik -- "Kartu Hasil Studi (KHS)" --> LihatKhs["Pilih Semester -> Tampilkan IPS, IPK, & Tabel Nilai MK"]
    SubTabAkademik -- "Log Bimbingan Skripsi" --> InputBimbingan["Input Bab Bahasan & Catatan Bimbingan Baru"]
    InputBimbingan --> SimpanLog["Tambah Log Bimbingan (Status: Menunggu Review Dosen)"]
```

---

## 3. Flowchart Modul B: Dosen Pengajar, Dosen Wali, & Kaprodi

Merepresentasikan alur kerja pada [`DosenDashboardApprovalScreen.kt`](file:///Users/Maheswara/AndroidStudioProjects/siakad-kampus-alyasini-mobile/app/src/main/java/ac/id/staialyasini/siakad/presentation/dosen/approval/DosenDashboardApprovalScreen.kt) dan [`DosenViewModel.kt`](file:///Users/Maheswara/AndroidStudioProjects/siakad-kampus-alyasini-mobile/app/src/main/java/ac/id/staialyasini/siakad/presentation/dosen/DosenViewModel.kt), termasuk pembedaan hak akses khusus **Kaprodi**.

```mermaid
flowchart TD
    StartDosen(["Portal Dosen / Kaprodi"]) --> PilihTabDosen{"Pilih Menu / Tab"}

    %% ================= ALUR 1: MENGAJAR & PRESENSI KELAS =================
    PilihTabDosen -- "Panel Jadwal Mengajar" --> JadwalDosen["Daftar Kelas Mengajar Hari Ini"]
    JadwalDosen --> AksiKelas{"Pilih Aksi Kelas"}
    AksiKelas -- "Tampilkan QR Presensi" --> ShowQrModal["Buka Modal QR Code Dinamis Kelas untuk Discan Mahasiswa"]
    AksiKelas -- "Kelola Presensi Manual" --> SheetPeserta["Buka Daftar Peserta Kelas"]
    SheetPeserta --> UpdateStatusHadir["Ubah Status (Hadir / Izin / Sakit / Alpha) atau Tandai Semua Hadir"]

    %% ================= ALUR 2: APPROVAL KRS PERWALIAN =================
    PilihTabDosen -- "Tab 1: Approval KRS" --> AntreanKrs["Daftar Pengajuan KRS Mahasiswa Wali"]
    AntreanKrs --> ModeApproval{"Pilih Tindakan Perwalian"}
    
    ModeApproval -- "Setujui Satuan" --> ApproveSingle["POST /dosen/perwalian/krs/{id}/approve"]
    ModeApproval -- "Pilih Beberapa / Semua (Checkbox)" --> ApproveBatch["POST /dosen/perwalian/krs/batch-approve"]
    ModeApproval -- "Tolak / Minta Revisi" --> DialogRevisi["Input Alasan / Catatan Wali (Wajib Diisi)"]
    DialogRevisi --> RejectKrs["POST /dosen/perwalian/krs/{id}/reject"]
    
    ApproveSingle --> SyncKrsDisetujui["Update Status KRS -> DISETUJUI (Sinkron ke Akun Mahasiswa)"]
    ApproveBatch --> SyncKrsDisetujui
    RejectKrs --> SyncKrsDitolak["Update Status KRS -> DITOLAK + Kirim Catatan Revisi Wali"]

    %% ================= ALUR 3: SKRIPSI & OTORITAS KAPRODI =================
    PilihTabDosen -- "Tab 2: Skripsi & Prodi" --> AntreanSkripsi["Daftar Antrean Bimbingan & Proposal Skripsi"]
    AntreanSkripsi --> CekTahap{"Cek Tahap & Role Pengguna"}
    
    CekTahap -- "Bimbingan Reguler (Dosen Pembimbing)" --> InputCatatanDosen["Input Catatan Balasan Dosen & Klik ACC Bimbingan"]
    InputCatatanDosen --> TambahHitunganAcc["Jumlah Bimbingan ACC Bertambah (+1)"]
    TambahHitunganAcc --> CekMinBimbingan{"Jumlah ACC >= Minimal (8x)?"}
    CekMinBimbingan -- "Ya" --> TerbitSidang["Aktifkan Tombol & Terbitkan ACC Sidang Munaqasyah"]
    CekMinBimbingan -- "Belum (< 8x)" --> TungguBimbinganBerikutnya["Lanjutkan Bimbingan Bab Berikutnya"]
    
    CekTahap -- "Pengajuan Proposal Baru (Khusus Role KAPRODI)" --> PilihPembimbing["Kaprodi Memilih Dosen Pembimbing dari Daftar Dosen"]
    PilihPembimbing --> SetujuiProposal["Approve Proposal & Tetapkan Dosen Pembimbing"]
```

---

## 4. Flowchart Modul C: Calon Mahasiswa Baru (PMB Tracker)

Merepresentasikan 6 tahapan seleksi Penerimaan Mahasiswa Baru pada [`PmbTrackerScreen.kt`](file:///Users/Maheswara/AndroidStudioProjects/siakad-kampus-alyasini-mobile/app/src/main/java/ac/id/staialyasini/siakad/presentation/pmb/PmbTrackerScreen.kt).

```mermaid
flowchart TD
    StartPmb(["Portal Calon Mahasiswa (PMB)"]) --> LoadProgress["Muat PmbProgressState (No. Pendaftaran & Gelombang)"]
    
    LoadProgress --> Step1["Tahap 1: Isi Biodata & Pilihan Prodi (PAI / HES)"]
    Step1 --> Step2["Tahap 2: Upload Berkas Persyaratan (Ijazah, KTP, KK, Pasfoto, Syahadah)"]
    Step2 --> Step3{"Tahap 3: Verifikasi Berkas oleh Panitia"}
    
    Step3 -- "Ada Berkas Ditolak (Misal: Foto KK Buram)" --> AlertTolak["Tampilkan Alasan Penolakan Berkas"]
    AlertTolak --> ReuploadCamera["Klik Foto Ulang / Re-upload Berkas via Kamera"]
    ReuploadCamera --> BerkasVerified["Status Berkas Berubah -> Terverifikasi"]
    
    Step3 -- "Semua Berkas Valid" --> BerkasVerified
    BerkasVerified --> Step4["Tahap 4: Pelaksanaan Ujian Seleksi PMB & Penilaian"]
    Step4 --> Step5{"Tahap 5: Pengumuman Kelulusan"}
    
    Step5 -- "LULUS SELEKSI" --> TagihanDaftarUlang["Tampilkan Tagihan Daftar Ulang (Rp 2.450.000)"]
    TagihanDaftarUlang --> BayarPmb["Klik Bayar Daftar Ulang via Payment Gateway"]
    BayarPmb --> Step6["Tahap 6: Pembayaran Lunas -> Generate NIM Otomatis (2026010194)"]
    Step6 --> AktifMhs(["Siap Masuk sebagai Mahasiswa Aktif"])
```

---

## 5. Flowchart Arsitektur Data Offline-First (Clean Architecture)

Merepresentasikan aliran data reaktif antara UI Compose, ViewModel, Repository, Room Database, DataStore, dan Retrofit REST API pada [`AppModule.kt`](file:///Users/Maheswara/AndroidStudioProjects/siakad-kampus-alyasini-mobile/app/src/main/java/ac/id/staialyasini/siakad/core/di/AppModule.kt) dan [`SiakadDatabase.kt`](file:///Users/Maheswara/AndroidStudioProjects/siakad-kampus-alyasini-mobile/app/src/main/java/ac/id/staialyasini/siakad/data/local/SiakadDatabase.kt).

```mermaid
flowchart LR
    subgraph Presentation["Presentation Layer (Jetpack Compose)"]
        UI["Screens (Mahasiswa / Dosen / Kaprodi / PMB)"]
        VM["Hilt ViewModels (Auth / Mahasiswa / Dosen / Pmb)"]
    end

    subgraph Domain["Domain Layer"]
        UC["ValidateCampusAttendanceUseCase"]
        RepoInterface["SiakadRepository (Interface & StateFlows)"]
    end

    subgraph Data["Data Layer (Offline-First)"]
        RepoImpl["SiakadRepositoryImpl (Single Source of Truth)"]
        RoomDB[("Room Database (SiakadCacheDao)")]
        DataStore[("Preferences DataStore (Session & Biometric)")]
        Retrofit["SiakadApiService + SanctumAuthInterceptor"]
    end

    UI -- "User Events / Actions" --> VM
    VM -- "Collect StateFlow" --> UI
    VM --> UC
    VM --> RepoInterface
    UC --> RepoInterface
    RepoInterface --> RepoImpl
    RepoImpl -- "Cache Jadwal & Tagihan" --> RoomDB
    RepoImpl -- "Read/Write Bearer Token & Role" --> DataStore
    RepoImpl -- "REST API (/api/v1/...)" --> Retrofit
```
