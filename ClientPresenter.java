import java.io.FileInputStream;
import java.util.Properties;
import java.awt.*;

public class ClientPresenter {
    protected ClientModel m;
    protected ClientView v;
    private Properties config_properties = new Properties();
    int runden;
    double xmin, xmax, ymin, ymax; // Parameter des Ausschnitts
    double cr, ci;
    double zoomRate;
    boolean restartVideo = false;
    boolean isEnd = false;
    boolean stopVideo = false;
    boolean hide_process = false;
    long startTime;
    long currentTime;
    Color[][][] mandel_result;
    int client_threads, workers_threads, max_iter, yChunk, xChunk;
    int xpix, ypix;
    double max_betrag, add_iter;
    float farbe_number;
    boolean show_chunk_line;

    public void init(ClientModel m, ClientView v) {
        this.m = m;
        this.v = v;
        v.setDim();
        try (FileInputStream config_input = new FileInputStream("client.config")) {
            config_properties.load(config_input);
            v.input_max_betrag.setText(config_properties.getProperty("input.max_betrag"));
            v.input_max_iter.setText(config_properties.getProperty("input.max_iter"));
            v.input_add_iter.setText(config_properties.getProperty("input.add_iter"));
            v.input_xpix.setText(config_properties.getProperty("input.xpix"));
            v.input_ypix.setText(config_properties.getProperty("input.ypix"));
            v.input_farbe.setText(config_properties.getProperty("input.farbe"));
            v.input_ci.setText(config_properties.getProperty("input.ci"));
            v.input_cr.setText(config_properties.getProperty("input.cr"));
            v.input_zoom_rate.setText(config_properties.getProperty("input.zoom_rate"));
            v.input_chunk_y.setText(config_properties.getProperty("input.chunk_y"));
            v.input_chunk_x.setText(config_properties.getProperty("input.chunk_x"));
            v.input_client_threads.setText(config_properties.getProperty("input.client_threads"));
            v.input_workers_threads.setText(config_properties.getProperty("input.workers_threads"));
            v.input_runden.setText(config_properties.getProperty("input.runden"));
            xmin = Double.parseDouble(config_properties.getProperty("input.xmin"));
            xmax = Double.parseDouble(config_properties.getProperty("input.xmax"));
            ymin = Double.parseDouble(config_properties.getProperty("input.ymin"));
            ymax = Double.parseDouble(config_properties.getProperty("input.ymax"));
        } catch (Exception e) {
            v.update_info("Config not found!");
        }
    }

    /** Komplette Berechnung und Anzeige aller Bilder */
    void apfelVideo() {
        isEnd = false;
        stopVideo = false;
        v.replay_button_mandel.setVisible(false);
        v.stop_button_mandel.setVisible(true);

        new Thread(() -> {
            mandel_result = m.apfel_bild(xmin, xmax, ymin, ymax);

            if(restartVideo){
                restartVideo = false;
                apfelVideo();
            }else{
                isEnd = true;
                replay_video();
            }
        }).start();
    }

    void replay_video(){
        new Thread(() -> {
            isEnd = false;
            stopVideo = false;
            v.replay_button_mandel.setText("Pause");
            v.stop_button_mandel.setVisible(false);
            v.replay_button_mandel.setVisible(true);
            v.update_button_mandel.setVisible(false);
            for (int i = 0; i < runden; i++) {
                if(!stopVideo){
                    if(mandel_result[i][0][0] != null){
                        v.update(mandel_result[i]);
                        try {
                            Thread.sleep((int) zoomRate * 90);
                        } catch (InterruptedException e) {
                            v.update_info("Error: Videowiedergabe-Thread");
                        }
                    }else{
                        break;
                    }
                }
            }
            isEnd = true;
            v.update_button_mandel.setVisible(true);
            v.replay_button_mandel.setText("Replay");
        }).start();
    }
}
