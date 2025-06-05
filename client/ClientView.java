import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.*;

public class ClientView {
    private ClientPresenter p;
    MandelbrotPanel mandelbrot_panel = new MandelbrotPanel();
    BufferedImage buff_image;
    int input_controlWidth = 0;
    JTextField input_xMinimum, input_yMinimum, input_yMaximum, input_xpix, input_ypix, input_add_iter, input_maxBetrag, input_farbe, input_cr, input_ci, input_zoom_rate, input_client_threads, input_maxIterations, input_chunk_y, input_chunk_x, input_stufenanzahl, input_workersThreads;
    private JLabel label_stufenanzahl = new JLabel("Stufenanzahl:");
    private JLabel label_maxIterationsations = new JLabel("Start Max Iterations:");
    private JLabel label_add_iterations = new JLabel("Add Max Iterations:");
    private JLabel label_ci = new JLabel("Ci:");
    private JLabel label_cr = new JLabel("Cr:");
    private JLabel label_zoomfaktor = new JLabel("Zoomfaktor:");
    private JLabel label_farbe = new JLabel("Farbe:");
    private JLabel label_chunk_y = new JLabel("*YChunks/Bild:");
    private JLabel label_chunk_x = new JLabel("*XChunks/Bild:");
    private JLabel label_client_threads = new JLabel("Client Threads:");
    private JLabel label_workersThreads = new JLabel("*Workers-Threads/YChunk:");
    private JLabel label_x_mininum = new JLabel("X Mininum");
    private JLabel label_y_mininum = new JLabel("Y Mininum");
    private JLabel label_y_maximum = new JLabel("Y Maxinum");
    private JLabel label_zeit = new JLabel();
    private JLabel label_info = new JLabel("Setting");
    private JLabel label_maxBetrag = new JLabel("Max Betrag");
    JButton update_button_mandel = new JButton("Update");
    JButton replay_button_mandel = new JButton("Replay");
    JButton stop_button_mandel = new JButton("Stop");

    public ClientView(ClientPresenter p) {
        this.p = p;
    }

    public void setDim() {
        JFrame frame_home = new JFrame("Mandelbrot-Setting");
        JPanel layout_home = new JPanel(new GridLayout(0, 2, 10, 5));
        layout_home.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton start_button_home = new JButton("Start");

        JLabel label_xpix = new JLabel("X Pixels:");
        JLabel label_ypix = new JLabel("*Y Pixels:");

        input_maxBetrag = new JTextField("0.0");
        input_maxIterations = new JTextField("0");
        input_add_iter = new JTextField("0");
        input_xpix = new JTextField("0");
        input_ypix = new JTextField("0");
        input_farbe = new JTextField("0.0");
        input_ci = new JTextField("0.0");
        input_cr = new JTextField("0.0");
        input_xMinimum = new JTextField("0.0");
        input_yMinimum = new JTextField("0.0");
        input_yMaximum = new JTextField("0.0");
        input_zoom_rate = new JTextField("0.0");
        input_chunk_y = new JTextField("0");
        input_chunk_x = new JTextField("0");
        input_client_threads = new JTextField("0");
        input_workersThreads = new JTextField("0");
        input_stufenanzahl = new JTextField("0");
        layout_home.add(label_info);
        layout_home.add(new JLabel(""));
        
        layout_home.add(label_xpix);
        layout_home.add(input_xpix);

        layout_home.add(label_ypix);
        layout_home.add(input_ypix);

        layout_home.add(label_stufenanzahl);
        layout_home.add(input_stufenanzahl);

        layout_home.add(label_maxIterationsations);
        layout_home.add(input_maxIterations);

        layout_home.add(label_add_iterations);
        layout_home.add(input_add_iter);

        layout_home.add(label_maxBetrag);
        layout_home.add(input_maxBetrag);

        layout_home.add(label_farbe);
        layout_home.add(input_farbe);

        layout_home.add(label_cr);
        layout_home.add(input_cr);

        layout_home.add(label_ci);
        layout_home.add(input_ci);

        layout_home.add(label_zoomfaktor);
        layout_home.add(input_zoom_rate);

        layout_home.add(label_chunk_y);
        layout_home.add(input_chunk_y);

        layout_home.add(label_chunk_x);
        layout_home.add(input_chunk_x);

        layout_home.add(label_workersThreads);
        layout_home.add(input_workersThreads);

        layout_home.add(label_client_threads);
        layout_home.add(input_client_threads);

        layout_home.add(label_x_mininum);
        layout_home.add(input_xMinimum);

        layout_home.add(label_y_mininum);
        layout_home.add(input_yMinimum);

        layout_home.add(label_y_maximum);
        layout_home.add(input_yMaximum);

        layout_home.add(start_button_home);

        frame_home.setLayout(new BorderLayout(10, 10));
        frame_home.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame_home.add(layout_home, BorderLayout.CENTER);
        frame_home.pack();
        frame_home.setLocationRelativeTo(null);
        frame_home.setVisible(true);

        start_button_home.addActionListener(e -> {
            frame_home.setVisible(false);
            p.xpix = Integer.parseInt(input_xpix.getText());
            p.ypix = Integer.parseInt(input_ypix.getText());
            updateInputData();

            buff_image = new BufferedImage(p.xpix, p.ypix, BufferedImage.TYPE_INT_RGB);

            initView();
        });
    }

    public void update_time(long time){
        label_zeit.setText("Time: "+ time +"ms");
    }

    public void showInfo(String text){
        label_info.setText(text);
    }

    private void initView() {
        JFrame frame_mandel = new JFrame("Mandelbrot");
        JPanel layout_mandel = new JPanel(new FlowLayout());

        layout_mandel.add(label_info);

        layout_mandel.add(label_zeit);


        layout_mandel.add(label_stufenanzahl);
        layout_mandel.add(input_stufenanzahl);

        layout_mandel.add(label_maxIterationsations);
        layout_mandel.add(input_maxIterations);

        layout_mandel.add(label_add_iterations);
        layout_mandel.add(input_add_iter);

        layout_mandel.add(label_maxBetrag);
        layout_mandel.add(input_maxBetrag);

        layout_mandel.add(label_farbe);
        layout_mandel.add(input_farbe);

        layout_mandel.add(label_cr);
        layout_mandel.add(input_cr);

        layout_mandel.add(label_ci);
        layout_mandel.add(input_ci);

        layout_mandel.add(label_zoomfaktor);
        layout_mandel.add(input_zoom_rate);

        layout_mandel.add(label_chunk_y);
        layout_mandel.add(input_chunk_y);

        layout_mandel.add(label_chunk_x);
        layout_mandel.add(input_chunk_x);

        layout_mandel.add(label_workersThreads);
        layout_mandel.add(input_workersThreads);

        layout_mandel.add(label_client_threads);
        layout_mandel.add(input_client_threads);

        layout_mandel.add(label_x_mininum);
        layout_mandel.add(input_xMinimum);

        layout_mandel.add(label_y_mininum);
        layout_mandel.add(input_yMinimum);

        layout_mandel.add(label_y_maximum);
        layout_mandel.add(input_yMaximum);

        layout_mandel.add(update_button_mandel);
        layout_mandel.add(replay_button_mandel);
        layout_mandel.add(stop_button_mandel);

        layout_mandel.setLayout(new BoxLayout(layout_mandel, BoxLayout.Y_AXIS));
        layout_mandel.setPreferredSize(new Dimension(input_controlWidth, layout_mandel.getPreferredSize().height));
        frame_mandel.add(mandelbrot_panel, BorderLayout.CENTER);
        frame_mandel.add(layout_mandel, BorderLayout.WEST);
        frame_mandel.setSize(input_controlWidth+p.xpix, p.ypix + 40);
        frame_mandel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame_mandel.setLocationRelativeTo(null);
        frame_mandel.setVisible(true);

        update_button_mandel.addActionListener(e -> {
            updateInputData();
            p.stopVideo = true;
            if(p.isEnd){
                p.mandelbrotVideo();
            }else{
                p.restartVideo = true;
            }
        });

        replay_button_mandel.addActionListener(e -> {
            p.stopVideo = true;
            if(p.isEnd){
                p.replayVideo();
            }
        });

        stop_button_mandel.addActionListener(e -> {
            p.stopVideo = true;
        });

        p.mandelbrotVideo();
    }

    private void updateInputData(){
        p.stufenanzahl = Integer.parseInt(input_stufenanzahl.getText());
        p.cr = Double.parseDouble(input_cr.getText());
        p.ci = Double.parseDouble(input_ci.getText());
        p.zoomfaktor = Double.parseDouble(input_zoom_rate.getText());
        p.client_threads = Integer.parseInt(input_client_threads.getText());
        p.maxIterations = Integer.parseInt(input_maxIterations.getText());
        p.add_iter = Double.parseDouble(input_add_iter.getText());
        p.yChunk = Integer.parseInt(input_chunk_y.getText());
        p.xChunk = Integer.parseInt(input_chunk_x.getText());
        p.workersThreads = Integer.parseInt(input_workersThreads.getText());
        p.farbe_number = Float.parseFloat(input_farbe.getText());
        p.maxBetrag = Double.parseDouble(input_maxBetrag.getText());
        p.xMinimum = Double.parseDouble(input_xMinimum.getText());
        p.yMinimum = Double.parseDouble(input_yMinimum.getText());
        p.yMaximum = Double.parseDouble(input_yMaximum.getText());
        //auto xmax
        p.xMaximum = ((double) p.xpix / p.ypix) * (p.yMaximum - p.yMinimum) + p.xMinimum;
        System.out.println("\nxmax: " + p.xMaximum);
    }

    public void updatePanel(Color[][] c) {
        for (int y = 0; y < p.ypix; y++) {
            for (int x = 0; x < p.xpix; x++) {
                if (c[x][y] != null) buff_image.setRGB(x, y, c[x][y].getRGB());
            }
        }
        mandelbrot_panel.repaint();
    }

    class MandelbrotPanel extends JPanel {
        @Override
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            graphics.drawImage(buff_image, 0, 0, null);
        }
    }
}
