import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PengunjungDAO {
    private static final String COUNTER_KEY = "pengunjung_harian";

    public int getJumlahPengunjung() {
        String sql = "SELECT jumlah FROM counter_pengunjung WHERE nama_counter = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, COUNTER_KEY);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("jumlah");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error ambil data: " + e.getMessage());
        }
        return 0;
    }

    public void tambahPengunjung() {
        String sql = "UPDATE counter_pengunjung SET jumlah = jumlah + 1 WHERE nama_counter = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, COUNTER_KEY);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error tambah pengunjung: " + e.getMessage());
        }
    }

    public void resetPengunjung() {
        String sql = "UPDATE counter_pengunjung SET jumlah = 0 WHERE nama_counter = ?";
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, COUNTER_KEY);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error reset pengunjung: " + e.getMessage());
        }
    }
}