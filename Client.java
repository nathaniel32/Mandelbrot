import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Client extends UnicastRemoteObject implements ClientInterface {

    Client(MasterInterface master) throws RemoteException {
        ApfelPresenter p = new ApfelPresenter();
        ApfelView v = new ApfelView(p);
        ApfelModel m = new ApfelModel(v, master);
        p.setModelAndView(m, v);
        p.apfelVideo();
    }

    @Override
    public String setColor() throws RemoteException {
        return "ok";
    }

    public static void main(String[] args) {
        if (args.length == 2){
            try {
                String masterIP = args[0];
                int masterPort = Integer.parseInt(args[1]);

                MasterInterface master = (MasterInterface) java.rmi.registry.LocateRegistry.getRegistry(masterIP, masterPort).lookup("MasterServer");
                
                new Client(master);

                System.out.println("Client hat eine Verbindung zum Master-Port: " + masterPort + " hergestellt\n\n");
            } catch (Exception e) {
                System.err.println("Client exception:");
                e.printStackTrace();
            }
        }else{
            System.out.println("Erforderliche Parameter: <Master IP> <Master Port>");
        }
    }
}

/* ************************** Presenter ********************** */
class ApfelPresenter implements ActionListener {
    protected ApfelModel m;
    protected ApfelView v;

    double xmin = -1.666, xmax = 1, ymin = -1, ymax = 1; // Parameter des Ausschnitts
    double cr = -0.743643887035151, ci = 0.131825904205330;
    double zoomRate = 1.5;
    int xpix = 640, ypix = 480;

    public void setModelAndView(ApfelModel m, ApfelView v) {
        this.m = m;
        this.v = v;
        v.setDim(xpix, ypix);
        m.setParameter(xpix, ypix);
    }

    /** Komplette Berechnung und Anzeige aller Bilder */
    void apfelVideo() {
        Color[][] c = new Color[xpix][ypix];
        //c = m.apfel_bild(xmin, xmax, ymin, ymax);
        //v.update(c);

        for (int i = 1; i < 65; i++) { // Iterationen bis zum Endpunkt
            System.out.println(i + " VergrÃ¶ÃŸerung: " + 2.6 / (xmax - xmin) + " xmin: " + xmin + " xmax: " + xmax);
            System.out.println("Threads: " + Thread.activeCount());
      
            //ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            //long threadId = Thread.currentThread().getId();
            //long cpuTime = threadMXBean.getThreadCpuTime(threadId);
            //int cpuTime_sec = (int)(cpuTime / 1000000000); // menghitung core yang sedang aktif
            //System.out.println("Time: " + cpuTime_sec + " second | Thread: " + Thread.currentThread().getName());
      
            c = m.apfel_bild(xmin, xmax, ymin, ymax);
            v.update(c);
            double xdim = xmax - xmin;
            double ydim = ymax - ymin;
            xmin = cr - xdim / 2 / zoomRate;
            xmax = cr + xdim / 2 / zoomRate;
            ymin = ci - ydim / 2 / zoomRate;
            ymax = ci + ydim / 2 / zoomRate;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle action events here if needed
    }
}

/* ************************* View *************************** */
class ApfelView {
    private ApfelPresenter p;
    private ApfelPanel ap = new ApfelPanel();
    int xpix, ypix;
    BufferedImage image;
    JTextField tfr, tfi;

    public ApfelView(ApfelPresenter p) {
        this.p = p;
    }

    public void setDim(int xpix, int ypix) {
        this.xpix = xpix;
        this.ypix = ypix;
        image = new BufferedImage(xpix, ypix, BufferedImage.TYPE_INT_RGB);
        initView();
    }

    private void initView() {
        JFrame f = new JFrame();
        JPanel sp = new JPanel(new FlowLayout());
        JButton sb = new JButton("Start");

        tfr = new JTextField("-0.743643887037151");
        tfi = new JTextField("0.131825904205330");
        sp.add(tfr);
        sp.add(tfi);
        sp.add(sb);

        f.add(ap, BorderLayout.CENTER);
        f.add(sp, BorderLayout.SOUTH);
        f.setSize(xpix, ypix + 100);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

        sb.addActionListener(e -> {
            double real = Double.parseDouble(tfr.getText());
            double imag = Double.parseDouble(tfi.getText());
            p.cr = real;
            p.ci = imag;
            p.apfelVideo();
        });
    }

    public void update(Color[][] c) {
        for (int y = 0; y < ypix; y++) {
            for (int x = 0; x < xpix; x++) {
                if (c[x][y] != null) image.setRGB(x, y, c[x][y].getRGB());
            }
        }
        ap.repaint();
    }

    class ApfelPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, null); // see javadoc
        }
    }
}

/* *********** Model **************************** */
// Threads and writing to arrays
// http://docs.oracle.com/javase/specs/jls/se7/html/jls-17.html#jls-17.6
class ApfelModel {
    ApfelView v;
    int xpix, ypix;
    double xmin, xmax, ymin, ymax;
    Color[][] bild;
    int max_iter = 5000;
    double max_betrag = 4.0;
    final int THREAD_COUNT = 4;
    MasterInterface master;

    public ApfelModel(ApfelView v, MasterInterface master) {
        this.v = v;
        this.master = master;
    }

    public void setParameter(int xpix, int ypix) {
        this.xpix = xpix;
        this.ypix = ypix;
        bild = new Color[xpix][ypix];
    }

    /** Erzeuge ein komplettes Bild mit Threads */
    Color[][] apfel_bild(double xmin, double xmax, double ymin, double ymax) {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;

        Thread[] threads = new Thread[THREAD_COUNT];
        int rowsPerThread = ypix / THREAD_COUNT;

        for (int i = 0; i < THREAD_COUNT; i++) {
            int y_start = i * rowsPerThread;
            int y_end = (i == THREAD_COUNT - 1) ? ypix : y_start + rowsPerThread;

            threads[i] = new Thread(new ApfelWorker(y_start, y_end));
            threads[i].start();

            //System.out.println(i);
        }

        for (int i = 0; i < THREAD_COUNT; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        return bild;
    }

    class ApfelWorker implements Runnable {
        int y_sta, y_sto;

        public ApfelWorker(int y_start, int y_stopp) {
            this.y_sta = y_start;
            this.y_sto = y_stopp;
        }

        @Override
        public void run() {
            try {
                //bild = server.work(max_iter, max_betrag, y_sta, y_sto, xpix, ypix, xmin, xmax, ymin, ymax);
                Color[][] result = master.bild_rechnen(max_iter, max_betrag, y_sta, y_sto, xpix, ypix, xmin, xmax, ymin, ymax);
                for (int y = y_sta; y < y_sto; y++) {
                    for (int x = 0; x < xpix; x++) {
                        bild[x][y] = result[x][y];
                    }
                }
            } catch (RemoteException e) {
                //e.printStackTrace();
                System.out.println("Thread Error!");
            }
        }
    }
}