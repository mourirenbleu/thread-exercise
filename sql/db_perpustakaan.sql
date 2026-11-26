CREATE DATABASE IF NOT EXISTS db_perpustakaan;
USE db_perpustakaan;

CREATE TABLE counter_pengunjung (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nama_counter VARCHAR(50) NOT NULL UNIQUE,
    jumlah INT DEFAULT 0,
    terakhir_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO counter_pengunjung (nama_counter, jumlah) VALUES ('pengunjung_harian', 0);