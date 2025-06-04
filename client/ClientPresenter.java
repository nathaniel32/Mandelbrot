import java.io.FileInputStream;
import java.util.Properties;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ClientPresenter {
    protected ClientModel m;
    protected ClientView v;
    private Properties config_properties = new Properties();
    BufferedImage buff_image;
    boolean restartVideo = false;
    boolean isEnd = false;
    boolean stopVideo = false;
    long startTime, currentTime, time_stamp;
    int input_replayDelay;
    int stufenanzahl;
    double xMinimum, xMaximum, yMinimum, yMaximum;
    double cr, ci;
    double zoomfaktor;
    int master_threads, workersThreads, maxIterations, yChunk, xChunk;
    int xpix, ypix;
    double maxBetrag, add_iter;
    float farbe_number;

    Color[][][] bild;

    public void init(ClientModel m, ClientView v) {
        this.m = m;
        this.v = v;
        v.setDim();
        try (FileInputStream config_input = new FileInputStream("client/client.config")) {
            config_properties.load(config_input);
            v.input_maxBetrag.setText(config_properties.getProperty("input.maxBetrag"));
            v.input_maxIterations.setText(config_properties.getProperty("input.maxIterations"));
            v.input_add_iter.setText(config_properties.getProperty("input.add_iter"));
            v.input_xpix.setText(config_properties.getProperty("input.xpix"));
            v.input_ypix.setText(config_properties.getProperty("input.ypix"));
            v.input_farbe.setText(config_properties.getProperty("input.farbe"));
            v.input_ci.setText(config_properties.getProperty("input.ci"));
            v.input_cr.setText(config_properties.getProperty("input.cr"));
            v.input_zoom_rate.setText(config_properties.getProperty("input.zoomfaktor"));
            v.input_chunk_y.setText(config_properties.getProperty("input.chunk_y"));
            v.input_chunk_x.setText(config_properties.getProperty("input.chunk_x"));
            v.input_master_threads.setText(config_properties.getProperty("input.master_threads"));
            v.input_workersThreads.setText(config_properties.getProperty("input.workersThreads"));
            v.input_stufenanzahl.setText(config_properties.getProperty("input.stufenanzahl"));
            v.input_xMinimum.setText(config_properties.getProperty("input.xMinimum"));
            v.input_yMinimum.setText(config_properties.getProperty("input.yMinimum"));
            v.input_yMaximum.setText(config_properties.getProperty("input.yMaximum"));
            v.input_controlWidth = Integer.parseInt(config_properties.getProperty("input.controlWidth"));
            input_replayDelay = Integer.parseInt(config_properties.getProperty("input.replayDelay"));
        } catch (Exception e) {
            v.showInfo("Config not found!");
        }
    }

    void mandelbrotVideo() {
        isEnd = false;
        stopVideo = false;
        v.replay_button_mandel.setVisible(false);
        v.stop_button_mandel.setVisible(true);
        v.update_button_mandel.setVisible(false);
        bild = new Color[stufenanzahl][xpix][ypix];

        new Thread(() -> {
            startTime = System.currentTimeMillis();
            m.startMandelbrot();

            if(restartVideo){
                restartVideo = false;
                mandelbrotVideo();
            }else{
                isEnd = true;
                replayVideo();
            }
        }).start();
    }

    void replayVideo(){
        new Thread(() -> {
            isEnd = false;
            stopVideo = false;
            v.replay_button_mandel.setText("Pause");
            v.stop_button_mandel.setVisible(false);
            v.replay_button_mandel.setVisible(true);
            v.update_button_mandel.setVisible(false);
            for (int i = 0; i < stufenanzahl; i++) {
                if(!stopVideo){
                    if(bild[i][0][0] != null){
                        v.updatePanel(bild[i]);
                        try {
                            Thread.sleep(input_replayDelay);
                        } catch (InterruptedException e) {
                            v.showInfo("Error: Videowiedergabe");
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

    void start_button_home(){
        v.frame_home.setVisible(false);
        xpix = Integer.parseInt(v.input_xpix.getText());
        ypix = Integer.parseInt(v.input_ypix.getText());
        v.updateInputData();

        buff_image = new BufferedImage(xpix, ypix, BufferedImage.TYPE_INT_RGB);

        v.initView();
    }

    void update_button_mandel(){
        v.updateInputData();
        stopVideo = true;
        if(isEnd){
            mandelbrotVideo();
        }else{
            restartVideo = true;
        }
    }

    void replay_button_mandel(){
        stopVideo = true;
        if(isEnd){
            replayVideo();
        }
    }

    void stop_button_mandel(){
        m.stopMandelbrot();
        stopVideo = true;
    }
}
