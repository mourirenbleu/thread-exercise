import java.sql.Connection;

public class TesKoneksi {
    public static void main(String[] args) {
        try (Connection conn = Koneksi.getConnection()) {
            if (conn != null) {
                System.out.println("Koneksi ke database db_perpustakaan BERHASIL!");
            }
        } catch (Exception e) {
            System.err.println("Koneksi GAGAL: " + e.getMessage());
        }
    }
}