import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class ThreadStatusRuangan extends Thread {
    private final JLabel labelStatus;
    private volatile boolean berjalan = true;

    private final String[] daftarRuangan = {
        "Ruang Baca Utama - Tersedia",
        "Ruang Diskusi A - Penuh",
        "Ruang Referensi - Tersedia",
        "Ruang Multimedia - Tersedia",
        "Ruang Koleksi Langka - Terbatas"
    };

    public ThreadStatusRuangan(JLabel labelStatus) {
        this.labelStatus = labelStatus;
        setDaemon(true);
    }

    public void hentikan() {
        berjalan = false;
        this.interrupt();
    }

    @Override
    public void run() {
        int index = 0;
        while (berjalan) {
            String statusSaatIni = daftarRuangan[index];

            SwingUtilities.invokeLater(() -> labelStatus.setText("Status: " + statusSaatIni));

            index = (index + 1) % daftarRuangan.length;

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                if (!berjalan) {
                    break;
                }
            }
        }
    }
}