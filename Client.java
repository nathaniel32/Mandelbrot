import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Client extends UnicastRemoteObject {

    Client(MasterInterface master) throws RemoteException {
        ApfelPresenter p = new ApfelPresenter();
        ApfelView v = new ApfelView(p);
        ApfelModel m = new ApfelModel(v, master);
        p.setModelAndView(m, v);
        //p.apfelVideo();
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

    int runden;
    double xmin = -1.666, xmax = 1, ymin = -1, ymax = 1; // Parameter des Ausschnitts
    double cr, ci;
    double zoomRate;
    Boolean restartVideo = false;
    Boolean isEnd = false;
    Boolean isError = false;
    private long startTime;
    private long currentTime;
    //int xpix = 640, ypix = 480;

    public void setModelAndView(ApfelModel m, ApfelView v) {
        this.m = m;
        this.v = v;
        //v.setDim(xpix, ypix);
        v.setDim();
    }

    /** Komplette Berechnung und Anzeige aller Bilder */
    void apfelVideo() {
        //Color[][] c = new Color[xpix][ypix];
        //c = m.apfel_bild(xmin, xmax, ymin, ymax);
        //v.update(c);

        startTime = System.currentTimeMillis()/1000;
        xmin = -1.666;
        xmax = 1;
        ymin = -1;
        ymax = 1;
        isEnd = false;
        isError = false;

        new Thread(() -> {
            for (int i = 1; i <= runden; i++) { // Iterationen bis zum Endpunkt
                if(restartVideo || isError){
                    break;
                }
                v.update_info(i + " Vergrößerung: " + 2.6 / (xmax - xmin) + " xmin: " + xmin + " xmax: " + xmax);
          
                currentTime = System.currentTimeMillis()/1000;
                v.update_zeit(currentTime - startTime);

                Color[][] c = m.apfel_bild(xmin, xmax, ymin, ymax);
                v.update(c);
                double xdim = xmax - xmin;
                double ydim = ymax - ymin;
                xmin = cr - xdim / 2 / zoomRate;
                xmax = cr + xdim / 2 / zoomRate;
                ymin = ci - ydim / 2 / zoomRate;
                ymax = ci + ydim / 2 / zoomRate;
            }

            isEnd = true;

            if(restartVideo){
                apfelVideo();
                restartVideo = false;
            }
        }).start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle action events here if needed
    }
}

/* ************************* View *************************** */
class ApfelView {
    ApfelPresenter p;
    private ApfelPanel ap = new ApfelPanel();
    //int xpix, ypix;
    int xpix, ypix;
    int client_threads, workers_threads, max_iter, layer;
    float farbe_number;
    BufferedImage image;
    boolean show_layer_line;
    JTextField input_farbe, input_cr, input_ci, input_zoom_rate, input_client_threads, input_max_iter, input_layer, input_runden, input_workers_threads;
    JCheckBox input_show_layer_line;
    JLabel label_max_iter = new JLabel("Max Iterations:");
    JLabel label_ci = new JLabel("Ci:");
    JLabel label_cr = new JLabel("Cr:");
    JLabel label_zoom_rate = new JLabel("Zoom Rate:");
    JLabel label_farbe = new JLabel("Farbe:");
    JLabel label_layer = new JLabel("Layers/Bild:");
    JLabel label_client_threads = new JLabel("Client Threads:");
    JLabel label_workers_threads = new JLabel("Workers Threads:");
    JLabel label_runden = new JLabel("Runden:");
    JLabel label_zeit = new JLabel();
    JLabel label_info = new JLabel("<html>Mandelbrot<br>-Layer sollte größer als der Client-Thread sein<br>-Layer sollte kleiner als die Y-Pixels geteilt durch Threads des Workers sein</html>");

    public ApfelView(ApfelPresenter p) {
        this.p = p;
    }

    public void setDim() {
        JFrame frame_home = new JFrame("Mandelbrot-Setting");
        JPanel layout_home = new JPanel(new FlowLayout());
        JButton start_button_home = new JButton("Start");

        JLabel label_xpix = new JLabel("X Pixels:");
        JLabel label_ypix = new JLabel("Y Pixels:");

        input_max_iter = new JTextField("5000");
        JTextField input_xpix = new JTextField("1024");
        JTextField input_ypix = new JTextField("768");
        input_farbe = new JTextField("50.5");
        input_ci = new JTextField("-0.6065038451823017");
        input_cr = new JTextField("-0.34837308755059104");
        input_zoom_rate = new JTextField("1.5");
        input_client_threads = new JTextField("4");
        input_workers_threads = new JTextField("10");
        input_layer = new JTextField("20");
        input_show_layer_line = new JCheckBox("layer line");
        input_runden = new JTextField("65");

        /* layout_home.add(input_max_iter);
        layout_home.add(input_xpix);
        layout_home.add(input_ypix);
        layout_home.add(input_ci);
        layout_home.add(input_cr);
        layout_home.add(input_client_threads);
        layout_home.add(start_button_home); */

        layout_home.add(label_info);

        layout_home.add(label_runden);
        layout_home.add(input_runden);

        layout_home.add(label_max_iter);
        layout_home.add(input_max_iter);

        layout_home.add(label_farbe);
        layout_home.add(input_farbe);

        layout_home.add(label_xpix);
        layout_home.add(input_xpix);

        layout_home.add(label_ypix);
        layout_home.add(input_ypix);

        layout_home.add(label_ci);
        layout_home.add(input_ci);

        layout_home.add(label_cr);
        layout_home.add(input_cr);

        layout_home.add(label_zoom_rate);
        layout_home.add(input_zoom_rate);

        layout_home.add(label_layer);
        layout_home.add(input_layer);

        layout_home.add(input_show_layer_line);

        layout_home.add(label_client_threads);
        layout_home.add(input_client_threads);

        layout_home.add(label_workers_threads);
        layout_home.add(input_workers_threads);
        
        layout_home.add(start_button_home);

        layout_home.setLayout(new BoxLayout(layout_home, BoxLayout.Y_AXIS));
        frame_home.add(layout_home, BorderLayout.SOUTH);
        frame_home.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame_home.pack();
        frame_home.setVisible(true);

        start_button_home.addActionListener(e -> {
            frame_home.setVisible(false);
            
            xpix = Integer.parseInt(input_xpix.getText());
            ypix = Integer.parseInt(input_ypix.getText());
            updateInputData();

            image = new BufferedImage(xpix, ypix, BufferedImage.TYPE_INT_RGB);
            p.m.setParameter(xpix, ypix);

            initView();
        });
    }

    public void update_zeit(long zeit){
        label_zeit.setText("Zeit: "+ zeit +"s");
    }

    public void update_info(String text){
        label_info.setText(text);
    }

    private void initView() {
        JFrame frame_mandel = new JFrame("Mandelbrot");
        JPanel layout_mandel = new JPanel(new FlowLayout());
        JButton update_button_mandel = new JButton("Update");

        layout_mandel.add(label_info);

        layout_mandel.add(label_zeit);

        layout_mandel.add(label_runden);
        layout_mandel.add(input_runden);

        layout_mandel.add(label_max_iter);
        layout_mandel.add(input_max_iter);

        layout_mandel.add(label_farbe);
        layout_mandel.add(input_farbe);

        layout_mandel.add(label_ci);
        layout_mandel.add(input_ci);

        layout_mandel.add(label_cr);
        layout_mandel.add(input_cr);

        layout_mandel.add(label_zoom_rate);
        layout_mandel.add(input_zoom_rate);

        layout_mandel.add(label_layer);
        layout_mandel.add(input_layer);

        layout_mandel.add(input_show_layer_line);

        layout_mandel.add(label_client_threads);
        layout_mandel.add(input_client_threads);

        layout_mandel.add(label_workers_threads);
        layout_mandel.add(input_workers_threads);

        layout_mandel.add(update_button_mandel);

        layout_mandel.setLayout(new BoxLayout(layout_mandel, BoxLayout.Y_AXIS));
        frame_mandel.add(ap, BorderLayout.CENTER);
        frame_mandel.add(layout_mandel, BorderLayout.SOUTH);
        frame_mandel.setSize(xpix, ypix + 400);
        frame_mandel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame_mandel.setVisible(true);

        update_button_mandel.addActionListener(e1 -> {
            updateInputData();
            p.restartVideo = true;
            if(p.isEnd){
                p.apfelVideo();
            }
        });

        p.apfelVideo();
    }

    private void updateInputData(){
        p.runden = Integer.parseInt(input_runden.getText());
        p.cr = Double.parseDouble(input_cr.getText());
        p.ci = Double.parseDouble(input_ci.getText());
        p.zoomRate = Double.parseDouble(input_zoom_rate.getText());
        client_threads = Integer.parseInt(input_client_threads.getText());
        max_iter = Integer.parseInt(input_max_iter.getText());
        layer = Integer.parseInt(input_layer.getText());
        workers_threads = Integer.parseInt(input_workers_threads.getText());
        farbe_number = Float.parseFloat(input_farbe.getText());
        show_layer_line = input_show_layer_line.isSelected();
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
    //int max_iter = 5000;
    double max_betrag = 4.0;
    MasterInterface master;
    int indexLayer;
    int rowsPerLayer;
    int Y_LAYER;
    Thread[] threads;

    public ApfelModel(ApfelView v, MasterInterface master) {
        this.v = v;
        this.master = master;
    }

    public void setParameter(int xpix, int ypix) {
        this.xpix = xpix;
        this.ypix = ypix;
        bild = new Color[xpix][ypix];
    }

    synchronized public void getLayer(){
        if(Y_LAYER > indexLayer){
            int y_start = indexLayer * rowsPerLayer;
            int y_end = (indexLayer == Y_LAYER - 1) ? ypix : y_start + rowsPerLayer;
    
            threads[indexLayer] = new Thread(new ApfelWorker(y_start, y_end));
            threads[indexLayer].start();
    
            indexLayer++;
        }
    }

    /** Erzeuge ein komplettes Bild mit Threads */
    Color[][] apfel_bild(double xmin, double xmax, double ymin, double ymax) {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;

        int THREAD_COUNT = v.client_threads;
        Y_LAYER = v.layer;
        indexLayer = 0;

        threads = new Thread[Y_LAYER];
        rowsPerLayer = ypix / Y_LAYER;

        for (int i = 0; i < THREAD_COUNT; i++) {
            getLayer();
        }

        //System.out.println("Threads: " + Thread.activeCount());

        for (int i = 0; i < Y_LAYER; i++) {
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
                int max_iter = v.max_iter;
                //bild = server.work(max_iter, max_betrag, y_sta, y_sto, xpix, ypix, xmin, xmax, ymin, ymax);
                
                int result_index = 0;
                Color[][] result = master.bild_rechnen(v.show_layer_line, v.farbe_number, v.workers_threads, max_iter, max_betrag, y_sta, y_sto, xpix, ypix, xmin, xmax, ymin, ymax);
                for (int y = y_sta; y < y_sto; y++) {
                    for (int x = 0; x < xpix; x++) {
                        bild[x][y] = result[x][result_index];
                    }
                    result_index++;
                }
                getLayer();
            } catch (RemoteException e) {
                //e.printStackTrace();
                //System.out.println("error");
                v.p.isError = true;
                v.update_info("Thread Error!");
            }
        }
    }
}