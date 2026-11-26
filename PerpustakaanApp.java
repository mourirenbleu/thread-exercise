import java.awt.*;
import javax.swing.*;

public class PerpustakaanApp extends JFrame {
    private final JLabel lblRuangan;
    private final JLabel lblCounter;
    private final PengunjungDAO dao;
    private ThreadStatusRuangan threadRuangan;

    public PerpustakaanApp() {
        dao = new PengunjungDAO();

        setTitle("Monitor Perpustakaan & Counter Pengunjung");
        setSize(450, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel Atas: Status Ruangan (berjalan di background thread)
        lblRuangan = new JLabel("Status: Memuat...", SwingConstants.CENTER);
        lblRuangan.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblRuangan.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        add(lblRuangan, BorderLayout.NORTH);

        // Panel Tengah: Total Pengunjung dari MySQL
        int jumlahAwal = dao.getJumlahPengunjung();
        lblCounter = new JLabel("Total Pengunjung: " + jumlahAwal, SwingConstants.CENTER);
        lblCounter.setFont(new Font("SansSerif", Font.PLAIN, 22));
        add(lblCounter, BorderLayout.CENTER);

        // Panel Bawah: Tombol Kontrol
        JPanel panelTombol = new JPanel(new FlowLayout());
        JButton btnTambah = new JButton("Tambah Pengunjung (+1)");
        JButton btnReset = new JButton("Reset Counter");
        JButton btnToggleThread = new JButton("Hentikan Monitor");

        panelTombol.add(btnTambah);
        panelTombol.add(btnReset);
        panelTombol.add(btnToggleThread);
        add(panelTombol, BorderLayout.SOUTH);

        btnTambah.addActionListener(e -> {
            dao.tambahPengunjung();
            lblCounter.setText("Total Pengunjung: " + dao.getJumlahPengunjung());
        });

        btnReset.addActionListener(e -> {
            dao.resetPengunjung();
            lblCounter.setText("Total Pengunjung: " + dao.getJumlahPengunjung());
        });

        btnToggleThread.addActionListener(e -> {
            if (threadRuangan != null && threadRuangan.isAlive()) {
                threadRuangan.hentikan();
                lblRuangan.setText("Status: Monitor Dihentikan");
                btnToggleThread.setText("Mulai Monitor");
            } else {
                mulaiThread();
                btnToggleThread.setText("Hentikan Monitor");
            }
        });

        mulaiThread();
    }

    private void mulaiThread() {
        threadRuangan = new ThreadStatusRuangan(lblRuangan);
        threadRuangan.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PerpustakaanApp().setVisible(true));
    }
}