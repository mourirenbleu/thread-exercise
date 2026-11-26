# Sistem Counter Pengunjung Perpustakaan & Monitor Status Ruangan

Aplikasi desktop berbasis **Java Swing** yang menerapkan konsep **Multithreading**, integrasi database **MySQL (JDBC)**, serta pola desain **DAO (Data Access Object)**. Aplikasi ini dirancang untuk mencatat jumlah pengunjung perpustakaan secara *real-time* ke dalam database sembari menampilkan status ketersediaan ruangan secara bergantian di latar belakang (*background thread*) tanpa membuat antarmuka pengguna membeku (*freeze*).

---

## Konsep Utama yang Diterapkan

1. **Java Multithreading (`Thread`)**: Menjalankan proses pembaruan teks status ruangan secara paralel di latar belakang agar interaksi tombol pada GUI tetap responsif.
2. **Thread-Safe GUI Update (`SwingUtilities.invokeLater`)**: Memastikan setiap perubahan tampilan pada komponen Swing dari *background thread* dijalankan dengan aman melalui *Event Dispatch Thread* (EDT).
3. **JDBC (Java Database Connectivity)**: Menghubungkan aplikasi Java ke server database MySQL menggunakan driver `mysql-connector-j-26.10.0.jar`.
4. **Pola Desain DAO (Data Access Object)**: Memisahkan logika *query* SQL dari logika antarmuka pengguna agar kode lebih rapi, modular, dan mudah dirawat.

---

## Struktur Direktori

```text
proyek-perpustakaan/
├── sql/
│   ├── mysql-connector-j-26.10.0.jar   # Driver JDBC MySQL (wajib di folder sql/)
│   └── db_perpustakaan.sql             # Skrip inisialisasi database & tabel
├── ss/
│   └── preview.png                     # Dokumentasi tangkapan layar aplikasi
├── Koneksi.java                        # Pengaturan koneksi JDBC ke MySQL
├── TesKoneksi.java                     # Program pengujian koneksi database
├── PengunjungDAO.java                  # Kelas DAO untuk operasi database
├── ThreadStatusRuangan.java            # Background thread pemantau ruangan
├── PerpustakaanApp.java                # Kelas utama (GUI Java Swing)
└── README.md                           # Dokumentasi proyek
```

---

## Penjelasan Kode per File

### 1. `sql/db_perpustakaan.sql`
Skrip SQL untuk menyiapkan skema database `db_perpustakaan` beserta tabel `counter_pengunjung`.
- **`nama_counter`**: Penanda unik (*UNIQUE*) untuk mengidentifikasi jenis penghitung (`pengunjung_harian`).
- **`jumlah`**: Menyimpan total angka pengunjung (nilai awal `0`).
- **`terakhir_update`**: Mencatat waktu pembaruan terakhir secara otomatis setiap kali data diubah.

### 2. `Koneksi.java`
Kelas utilitas yang bertugas membuka jalur komunikasi antara Java dan server MySQL.
- Menyimpan konstanta `URL` (`jdbc:mysql://localhost:3306/db_perpustakaan?serverTimezone=Asia/Makassar`), `USER`, dan `PASS`.
- Method `getConnection()` memuat driver `com.mysql.cj.jdbc.Driver` secara eksplisit menggunakan `Class.forName(...)` lalu mengembalikan objek `Connection` melalui `DriverManager.getConnection(...)`.

### 3. `TesKoneksi.java`
Kelas *standalone* sederhana yang digunakan untuk memverifikasi apakah konfigurasi database dan *classpath* driver di folder `sql/` sudah benar sebelum menjalankan aplikasi utama.
- Menggunakan blok **`try-with-resources`** (`try (Connection conn = Koneksi.getConnection())`) agar koneksi otomatis ditutup setelah pengecekan selesai.

### 4. `PengunjungDAO.java`
Mengimplementasikan pola **Data Access Object (DAO)** untuk mengelola seluruh interaksi SQL terhadap tabel `counter_pengunjung`. Seluruh *query* menggunakan **`PreparedStatement`** agar aman dari *SQL Injection*.
- **`getJumlahPengunjung()`**: Menjalankan perintah `SELECT` untuk mengambil total pengunjung terakhir yang tersimpan di MySQL.
- **`tambahPengunjung()`**: Menjalankan perintah `UPDATE ... SET jumlah = jumlah + 1` untuk menambah jumlah pengunjung secara atomik di sisi database.
- **`resetPengunjung()`**: Menjalankan perintah `UPDATE ... SET jumlah = 0` untuk mengembalikan hitungan ke nol.

### 5. `ThreadStatusRuangan.java`
Kelas turunan dari `Thread` (`extends Thread`) yang berfungsi menampilkan daftar status ruangan perpustakaan secara bergantian setiap 1,5 detik.
- **`volatile boolean berjalan`**: Flag pengendali perulangan yang bersifat *thread-safe* agar perubahan status dari *main thread* langsung terbaca oleh *background thread*.
- **`setDaemon(true)`**: Menandai *thread* sebagai *daemon thread* sehingga otomatis berhenti saat jendela aplikasi utama ditutup.
- **`run()`**: Melakukan perulangan daftar ruangan menggunakan operasi modulo (`(index + 1) % daftarRuangan.length`), memperbarui `JLabel` melalui `SwingUtilities.invokeLater(...)`, dan memberi jeda menggunakan `Thread.sleep(1500)`.
- **`hentikan()`**: Mengubah flag `berjalan` menjadi `false` dan memanggil `this.interrupt()` untuk langsung membangunkan *thread* yang sedang tidur (*sleep*) agar segera berhenti dengan bersih.

### 6. `PerpustakaanApp.java`
Kelas utama berbasis `JFrame` yang menyatukan tampilan GUI, *background thread*, dan operasi database.
- **Tata Letak (`BorderLayout`)**:
  - **Bagian Atas (`NORTH`)**: Menampilkan `lblRuangan` yang teksnya diperbarui terus-menerus oleh `ThreadStatusRuangan`.
  - **Bagian Tengah (`CENTER`)**: Menampilkan `lblCounter` yang memuat data awal pengunjung dari `PengunjungDAO`.
  - **Bagian Bawah (`SOUTH`)**: Berisi tiga tombol kontrol utama di dalam `JPanel`:
    - **Tambah Pengunjung (+1)**: Memanggil `dao.tambahPengunjung()` lalu menyegarkan angka pada layar.
    - **Reset Counter**: Memanggil `dao.resetPengunjung()` dan mereset tampilan ke `0`.
    - **Hentikan / Mulai Monitor**: Mengontrol siklus hidup `ThreadStatusRuangan` (menghentikan *thread* yang sedang aktif atau membuat instance *thread* baru untuk memulainya kembali).

---

## Prasyarat & Persiapan

1. **Java Development Kit (JDK)** versi 11 atau lebih baru.
2. **MySQL Server** (melalui XAMPP, Laragon, atau instalasi mandiri).
3. Pastikan file `mysql-connector-j-26.10.0.jar` sudah diletakkan di dalam folder `sql/`.
4. Impor file `sql/db_perpustakaan.sql` ke MySQL (via phpMyAdmin atau terminal MySQL).
5. Sesuaikan `USER` dan `PASS` di dalam `Koneksi.java` apabila server MySQL Anda menggunakan kredensial khusus.

---

## Cara Menjalankan Program

### Melalui Terminal / CMD / PowerShell
Buka terminal di direktori utama proyek, lalu jalankan perintah berikut secara berurutan:

1. **Kompilasi seluruh file Java:**
   ```bash
   javac -cp ".;sql/mysql-connector-j-26.10.0.jar" *.java
   ```
   > **Catatan:** Jika menggunakan Linux atau macOS, ganti tanda titik koma (`;`) menjadi titik dua (`:`).

2. **Uji koneksi ke database:**
   ```bash
   java -cp ".;sql/mysql-connector-j-26.10.0.jar" TesKoneksi
   ```
   Pastikan muncul pesan: `Koneksi ke database db_perpustakaan BERHASIL!`

3. **Jalankan aplikasi utama:**
   ```bash
   java -cp ".;sql/mysql-connector-j-26.10.0.jar" PerpustakaanApp
   ```

### Melalui Visual Studio Code
Tambahkan konfigurasi berikut pada file `.vscode/settings.json` agar ekstensi Java mengenali driver di folder `sql/`:

```json
{
    "java.project.referencedLibraries": [
        "sql/mysql-connector-j-26.10.0.jar"
    ]
}
```
Setelah itu, buka `PerpustakaanApp.java` dan klik tombol **Run** di atas method `main`.

---

## Dokumentasi Tampilan

Letakkan tangkapan layar aplikasi Anda di dalam folder `ss/` agar tampil otomatis pada bagian berikut:

![Preview Aplikasi Perpustakaan](ss/preview.png)