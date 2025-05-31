import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Client extends UnicastRemoteObject implements ClientInterface{
    private Presenter p;
    Client(MasterInterface master) throws RemoteException {
        p = new Presenter();
        View v = new View(p);
        Model m = new Model(p, master);
        p.setModelAndView(m, v);
    }

    @Override
    public void draw_mandelbrot(Color[][] bild, int round){
        new Thread(() -> {
            p.v.update(bild);
            p.v.update_info("Round: " + round);
            p.currentTime = System.currentTimeMillis();
            p.v.update_time(p.currentTime-p.startTime);
        }).start();
    }

    public static void main(String[] args) {
        if (args.length == 2){
            try {
                String masterIP = args[0];
                int masterPort = Integer.parseInt(args[1]);

                MasterInterface master = (MasterInterface) java.rmi.registry.LocateRegistry.getRegistry(masterIP, masterPort).lookup("MasterServer");
                
                Client client = new Client(master);
                master.client_anmelden(client);

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

class Presenter {
    protected Model m;
    protected View v;
    int xpix, ypix;
    int worker_thread, max_iter, master_thread;
    int round;
    double xmin, xmax, ymin, ymax;
    double cr, ci;
    double zoomRate;
    double max_betrag;
    long startTime;
    long currentTime;
    BufferedImage image;

    public void setModelAndView(Model m, View v) {
        this.m = m;
        this.v = v;
        v.set_dim();
    }

    void start_init_video_btn() {
        updateParameter();
        startTime = System.currentTimeMillis();
        m.start_video();
    }

    void start_display_btn(){
        v.frame_home.setVisible(false);
        xpix = Integer.parseInt(v.input_xpix.getText());
        ypix = Integer.parseInt(v.input_ypix.getText());
        image = new BufferedImage(xpix, ypix, BufferedImage.TYPE_INT_RGB);
        v.init_view();
    }

    void updateParameter(){
        round = Integer.parseInt(v.input_round.getText());
        cr = Double.parseDouble(v.input_cr.getText());
        ci = Double.parseDouble(v.input_ci.getText());
        worker_thread = Integer.parseInt(v.input_worker_thread.getText());
        max_iter = Integer.parseInt(v.input_max_iter.getText());
        master_thread = Integer.parseInt(v.input_master_thread.getText());
        xmin = Double.parseDouble(v.input_xmin.getText());
        xmax = Double.parseDouble(v.input_xmax.getText());
        ymin = Double.parseDouble(v.input_ymin.getText());
        ymax = Double.parseDouble(v.input_ymax.getText());
        zoomRate = Double.parseDouble(v.input_zoomRate.getText());
        max_betrag = Double.parseDouble(v.input_max_betrag.getText());
    }
}

class View {
    Presenter p;
    private ApfelPanel ap = new ApfelPanel();
    JTextField input_cr, input_ci, input_worker_thread, input_max_iter, input_master_thread, input_round;
    JTextField input_xmin, input_xmax, input_ymin, input_ymax, input_zoomRate, input_max_betrag;
    JFrame frame_home;
    JTextField input_xpix;
    JTextField input_ypix;
    JLabel label_time = new JLabel();
    JLabel label_info = new JLabel("Mandelbrot");
    JButton start_button_mandel;

    public View(Presenter p) {
        this.p = p;
    }

    public void set_dim() {
        frame_home = new JFrame("Mandelbrot-Setting");
        JPanel layout_dim = new JPanel(new FlowLayout());
        JButton start_button_home = new JButton("Start");

        JLabel label_xpix = new JLabel("X Pixels:");
        JLabel label_ypix = new JLabel("Y Pixels:");

        input_max_iter = new JTextField("1000");
        input_xpix = new JTextField("1024");
        input_ypix = new JTextField("768");
        input_ci = new JTextField("0.131825904205330");
        input_cr = new JTextField("-0.743643887035151");
        input_worker_thread = new JTextField("24");
        input_master_thread = new JTextField("8");
        input_round = new JTextField("100");
        input_xmin = new JTextField("-1.666");
        input_xmax = new JTextField("1");
        input_ymin = new JTextField("-1");
        input_ymax = new JTextField("1");
        input_zoomRate = new JTextField("1.1");
        input_max_betrag = new JTextField("4.0");

        layout_dim.add(label_info);
        layout_dim.add(label_xpix);
        layout_dim.add(input_xpix);
        layout_dim.add(label_ypix);
        layout_dim.add(input_ypix);
        layout_dim.add(start_button_home);

        layout_dim.setLayout(new BoxLayout(layout_dim, BoxLayout.Y_AXIS));
        frame_home.add(layout_dim, BorderLayout.SOUTH);
        frame_home.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame_home.pack();
        frame_home.setVisible(true);

        start_button_home.addActionListener(e -> { //START BUTTON MANDELBROT
            p.start_display_btn();
        });
    }

    public void update_time(long time){
        label_time.setText("Time: "+ time +" ms");
    }

    public void update_info(String text){
        label_info.setText(text);
    }

    void init_view() {
        JFrame frame_mandel = new JFrame("Mandelbrot");
        frame_mandel.setLayout(new BorderLayout());

        frame_mandel.add(ap, BorderLayout.CENTER); // ap

        JPanel layout_panel = new JPanel(new GridLayout(0, 2, 5, 5));
        layout_panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        layout_panel.add(new JLabel("Round:"));
        layout_panel.add(input_round);
        layout_panel.add(new JLabel("Max Iterations:"));
        layout_panel.add(input_max_iter);
        layout_panel.add(new JLabel("Ci Value:"));
        layout_panel.add(input_ci);
        layout_panel.add(new JLabel("Cr Value:"));
        layout_panel.add(input_cr);
        layout_panel.add(new JLabel("Master Threads:"));
        layout_panel.add(input_master_thread);
        layout_panel.add(new JLabel("Worker Threads:"));
        layout_panel.add(input_worker_thread);
        layout_panel.add(new JLabel("X min:"));
        layout_panel.add(input_xmin);
        layout_panel.add(new JLabel("X max:"));
        layout_panel.add(input_xmax);
        layout_panel.add(new JLabel("Y min:"));
        layout_panel.add(input_ymin);
        layout_panel.add(new JLabel("Y max:"));
        layout_panel.add(input_ymax);
        layout_panel.add(new JLabel("Zoom Rate:"));
        layout_panel.add(input_zoomRate);
        layout_panel.add(new JLabel("Max Betrag:"));
        layout_panel.add(input_max_betrag);

        layout_panel.add(new JLabel(""));
        layout_panel.add(start_button_mandel = new JButton("Start"));

        JPanel top_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top_panel.add(label_info);
        top_panel.add(label_time);

        frame_mandel.add(top_panel, BorderLayout.NORTH);
        frame_mandel.add(layout_panel, BorderLayout.EAST);

        frame_mandel.setSize(p.xpix + 300, p.ypix + 50);
        frame_mandel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame_mandel.setVisible(true);

        start_button_mandel.addActionListener(e -> {
            p.start_init_video_btn();
        });
    }


    public void update(Color[][] c) {
        for (int y = 0; y < p.ypix; y++) {
            for (int x = 0; x < p.xpix; x++) {
                if (c[x][y] != null) p.image.setRGB(x, y, c[x][y].getRGB());
            }
        }
        ap.repaint();
    }

    class ApfelPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(p.image, 0, 0, null);
        }
    }
}

class Model {
    Presenter p;
    MasterInterface master;

    public Model(Presenter p, MasterInterface master) {
        this.p = p;
        this.master = master;
    }

    public void start_video(){
        new Thread(() -> {
            try {
                p.v.start_button_mandel.setVisible(false);
                master.video_init(p.round, p.xmin, p.xmax, p.ymin, p.ymax, p.zoomRate, p.xpix, p.ypix, p.cr, p.ci, p.max_iter, p.master_thread, p.worker_thread, p.max_betrag);
                master.video_start(); //ASYNC
                p.v.start_button_mandel.setVisible(true);
            } catch (Exception e) {
                System.out.println("ERROR");
            }
        }).start();
    }
}