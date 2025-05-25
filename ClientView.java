import java.awt.image.BufferedImage;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class ClientView {
    private ClientPresenter p;
    private ApfelPanel ap = new ApfelPanel();
    private BufferedImage image;
    JTextField input_xpix, input_ypix, input_add_iter, input_max_betrag, input_farbe, input_cr, input_ci, input_zoom_rate, input_client_threads, input_max_iter, input_chunk_y, input_chunk_x, input_runden, input_workers_threads;
    private JLabel label_max_iter = new JLabel("Start Max Iterations:");
    private JLabel label_add_iter = new JLabel("Add Max Iterations:");
    private JLabel label_ci = new JLabel("Ci:");
    private JLabel label_cr = new JLabel("Cr:");
    private JLabel label_zoom_rate = new JLabel("Zoom Rate:");
    private JLabel label_farbe = new JLabel("Farbe:");
    private JLabel label_chunk_y = new JLabel("*YChunks/Bild:");
    private JLabel label_chunk_x = new JLabel("*XChunks/Bild:");
    private JLabel label_client_threads = new JLabel("Client Threads:");
    private JLabel label_workers_threads = new JLabel("*Workers-Threads/YChunk:");
    private JLabel label_runden = new JLabel("Runden:");
    private JLabel label_zeit = new JLabel();
    private JLabel label_info = new JLabel("Setting");
    private JLabel label_max_betrag = new JLabel("Max Betrag");
    private JCheckBox input_show_chunk_line = new JCheckBox("Chunk Line");
    private JCheckBox input_hide_process = new JCheckBox("Vorgang ausblenden");
    JButton update_button_mandel = new JButton("Update");
    JButton replay_button_mandel = new JButton("Replay");
    JButton stop_button_mandel = new JButton("Stop");

    public ClientView(ClientPresenter p) {
        this.p = p;
    }

    public void setDim() {
        JFrame frame_home = new JFrame("Mandelbrot-Setting");
        JPanel layout_home = new JPanel(new FlowLayout());
        JButton start_button_home = new JButton("Start");

        JLabel label_xpix = new JLabel("X Pixels:");
        JLabel label_ypix = new JLabel("*Y Pixels:");

        input_max_betrag = new JTextField("0.0");
        input_max_iter = new JTextField("0");
        input_add_iter = new JTextField("0");
        input_xpix = new JTextField("0");
        input_ypix = new JTextField("0");
        input_farbe = new JTextField("0.0");
        input_ci = new JTextField("0.0");
        input_cr = new JTextField("0.0");
        input_zoom_rate = new JTextField("0.0");
        input_chunk_y = new JTextField("0");
        input_chunk_x = new JTextField("0");
        input_client_threads = new JTextField("0");
        input_workers_threads = new JTextField("0");
        input_runden = new JTextField("0");
        
        layout_home.add(label_info);

        layout_home.add(input_hide_process);

        layout_home.add(label_runden);
        layout_home.add(input_runden);

        layout_home.add(label_max_iter);
        layout_home.add(input_max_iter);

        layout_home.add(label_add_iter);
        layout_home.add(input_add_iter);

        layout_home.add(label_max_betrag);
        layout_home.add(input_max_betrag);

        layout_home.add(label_farbe);
        layout_home.add(input_farbe);

        layout_home.add(label_ci);
        layout_home.add(input_ci);

        layout_home.add(label_cr);
        layout_home.add(input_cr);

        layout_home.add(label_zoom_rate);
        layout_home.add(input_zoom_rate);

        layout_home.add(label_xpix);
        layout_home.add(input_xpix);

        layout_home.add(label_ypix);
        layout_home.add(input_ypix);

        layout_home.add(label_chunk_y);
        layout_home.add(input_chunk_y);

        layout_home.add(label_chunk_x);
        layout_home.add(input_chunk_x);

        layout_home.add(input_show_chunk_line);

        layout_home.add(label_workers_threads);
        layout_home.add(input_workers_threads);

        layout_home.add(label_client_threads);
        layout_home.add(input_client_threads);
        
        layout_home.add(start_button_home);

        layout_home.setLayout(new BoxLayout(layout_home, BoxLayout.Y_AXIS));
        frame_home.add(layout_home, BorderLayout.SOUTH);
        frame_home.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame_home.pack();
        frame_home.setVisible(true);

        start_button_home.addActionListener(e -> {
            frame_home.setVisible(false);
            
            p.xpix = Integer.parseInt(input_xpix.getText());
            p.ypix = Integer.parseInt(input_ypix.getText());
            updateInputData();

            image = new BufferedImage(p.xpix, p.ypix, BufferedImage.TYPE_INT_RGB);

            initView();
        });

        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                vorschlag();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                vorschlag();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                vorschlag();
            }

            public void vorschlag() {
                try {
                    int chunk_value = Integer.parseInt(input_chunk_y.getText());
                    int workers_threads_value = Integer.parseInt(input_workers_threads.getText());
                    int ypix_value = Integer.parseInt(input_ypix.getText());

                    if(chunk_value > 0 && workers_threads_value > 0 && ypix_value > 0){
                        double result = (double) ypix_value / chunk_value / workers_threads_value;
                        update_info(ypix_value + "/" + chunk_value + "/" + workers_threads_value + " = " + result + " Y-Pix/Worker-thread");     
                    }else{
                        update_info("Nummer muss größer sein als 0!");
                    }
                } catch (NumberFormatException e) {
                    update_info("Y-Pixel oder Worker-Threads oder Chunk sind ungültig");
                }
            }
        };

        input_ypix.getDocument().addDocumentListener(documentListener);
        input_chunk_y.getDocument().addDocumentListener(documentListener);
        input_workers_threads.getDocument().addDocumentListener(documentListener);
    }

    public void update_zeit(long zeit){
        label_zeit.setText("Zeit: "+ zeit +"ms");
    }

    public void update_info(String text){
        label_info.setText(text);
    }

    private void initView() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        DisplayMode mode = gd.getDisplayMode();
        int screenWidth = mode.getWidth() * 75/100;
        
        JFrame frame_mandel = new JFrame("Mandelbrot");
        JPanel layout_mandel = new JPanel(new FlowLayout());

        layout_mandel.add(label_info);

        layout_mandel.add(label_zeit);

        layout_mandel.add(input_hide_process);

        layout_mandel.add(label_runden);
        layout_mandel.add(input_runden);

        layout_mandel.add(label_max_iter);
        layout_mandel.add(input_max_iter);

        layout_mandel.add(label_add_iter);
        layout_mandel.add(input_add_iter);

        layout_mandel.add(label_max_betrag);
        layout_mandel.add(input_max_betrag);

        layout_mandel.add(label_farbe);
        layout_mandel.add(input_farbe);

        layout_mandel.add(label_ci);
        layout_mandel.add(input_ci);

        layout_mandel.add(label_cr);
        layout_mandel.add(input_cr);

        layout_mandel.add(label_zoom_rate);
        layout_mandel.add(input_zoom_rate);

        layout_mandel.add(label_chunk_y);
        layout_mandel.add(input_chunk_y);

        layout_mandel.add(label_chunk_x);
        layout_mandel.add(input_chunk_x);

        layout_mandel.add(input_show_chunk_line);

        layout_mandel.add(label_workers_threads);
        layout_mandel.add(input_workers_threads);

        layout_mandel.add(label_client_threads);
        layout_mandel.add(input_client_threads);

        layout_mandel.add(update_button_mandel);
        layout_mandel.add(replay_button_mandel);
        layout_mandel.add(stop_button_mandel);

        layout_mandel.setLayout(new BoxLayout(layout_mandel, BoxLayout.Y_AXIS));
        layout_mandel.setPreferredSize(new Dimension(screenWidth-p.xpix, layout_mandel.getPreferredSize().height));
        frame_mandel.add(ap, BorderLayout.CENTER);
        frame_mandel.add(layout_mandel, BorderLayout.WEST);
        frame_mandel.setSize(screenWidth, p.ypix + 40);
        frame_mandel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame_mandel.setVisible(true);

        update_button_mandel.addActionListener(e -> {
            updateInputData();
            p.stopVideo = true;
            if(p.isEnd){
                p.apfelVideo();
            }else{
                p.restartVideo = true;
            }
        });

        replay_button_mandel.addActionListener(e -> {
            p.stopVideo = true;
            if(p.isEnd){
                p.replay_video();
            }
        });

        stop_button_mandel.addActionListener(e -> {
            p.stopVideo = true;
        });

        p.apfelVideo();
    }

    private void updateInputData(){
        p.runden = Integer.parseInt(input_runden.getText());
        p.cr = Double.parseDouble(input_cr.getText());
        p.ci = Double.parseDouble(input_ci.getText());
        p.zoomRate = Double.parseDouble(input_zoom_rate.getText());
        p.client_threads = Integer.parseInt(input_client_threads.getText());
        p.max_iter = Integer.parseInt(input_max_iter.getText());
        p.add_iter = Double.parseDouble(input_add_iter.getText());
        p.yChunk = Integer.parseInt(input_chunk_y.getText());
        p.xChunk = Integer.parseInt(input_chunk_x.getText());
        p.workers_threads = Integer.parseInt(input_workers_threads.getText());
        p.farbe_number = Float.parseFloat(input_farbe.getText());
        p.show_chunk_line = input_show_chunk_line.isSelected();
        p.hide_process = input_hide_process.isSelected();
        p.max_betrag = Double.parseDouble(input_max_betrag.getText());
    }

    public void update(Color[][] c) {
        for (int y = 0; y < p.ypix; y++) {
            for (int x = 0; x < p.xpix; x++) {
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
