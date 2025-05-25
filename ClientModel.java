import java.rmi.RemoteException;
import java.awt.*;

public class ClientModel {
    private ClientPresenter p;
    private double xmin, xmax, ymin, ymax;
    private Color[][][] bild;
    private MasterInterface master;
    private int totalThread;
    private int indexRunden, indexRundenChunk;
    private int indexChunkY, rowsPerBlock;
    private int indexChunkX, columnPerBlock;
    private Thread[] threads;

    public ClientModel(ClientPresenter p, MasterInterface master) {
        this.p = p;
        this.master = master;
    }

    synchronized public void getChunk(){
        if(!p.stopVideo){
            if(p.yChunk > indexChunkY && totalThread > indexRundenChunk){
                if(p.xChunk > indexChunkX){
                    int y_start = indexChunkY * rowsPerBlock;
                    int y_end = (indexChunkY == p.yChunk - 1) ? p.ypix : y_start + rowsPerBlock;

                    int x_start = indexChunkX * columnPerBlock;
                    int x_end = (indexChunkX == p.xChunk - 1) ? p.xpix : x_start + columnPerBlock;

                    //System.out.println(x_start + "- " + x_end + " | " + y_start + " - " + y_end);

                    threads[indexRundenChunk] = new Thread(new ApfelWorker(y_start, y_end, x_start, x_end, indexRunden, p.max_iter, xmin, xmax, ymin, ymax));
                    threads[indexRundenChunk].start();

                    indexChunkX++;
                    indexRundenChunk++;
                }else{
                    indexChunkX = 0;
                    indexChunkY++;
                    getChunk();
                }
            }else if(p.runden > indexRunden){
                p.max_iter = (int)(p.max_iter + p.zoomRate * p.add_iter);
    
                double xdim = xmax - xmin;
                double ydim = ymax - ymin;
                xmin = p.cr - xdim / 2 / p.zoomRate;
                xmax = p.cr + xdim / 2 / p.zoomRate;
                ymin = p.ci - ydim / 2 / p.zoomRate;
                ymax = p.ci + ydim / 2 / p.zoomRate;
    
                if(!p.hide_process){
                    p.v.update(bild[indexRunden]);
                }
    
                //p.update_info("Runden: " + (indexRunden + 1) + " | Max-Iterations: " + p.max_iter);
    
                indexChunkY = 0;
                indexRunden++;
                getChunk();
            }else{
                System.out.println("End!");
            }
        }else{
            System.out.println("Forced Stop!");
            for (; indexRundenChunk < totalThread; indexRundenChunk++) {
                threads[indexRundenChunk] = new Thread();
            }
        }
        p.currentTime = System.currentTimeMillis();
        p.v.update_zeit(p.currentTime - p.startTime);
        p.v.update_info("Chunks: " + indexRundenChunk + "/" + totalThread + " | Runden: " + indexRunden + " | Max-Iterations: " + p.max_iter + " | Threads: " + Thread.activeCount());
    }

    /** Erzeuge ein komplettes Bild mit Threads */
    Color[][][] apfel_bild(double xmin, double xmax, double ymin, double ymax) {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;

        bild = new Color[p.runden][p.xpix][p.ypix];
        indexRunden = 0;
        indexRundenChunk = 0;

        indexChunkY = 0;
        rowsPerBlock = p.ypix / p.yChunk;

        indexChunkX = 0;
        columnPerBlock = p.xpix / p.xChunk;

        totalThread = p.xChunk * p.yChunk * p.runden;

        threads = new Thread[totalThread];

        p.startTime = System.currentTimeMillis();

        for (int i = 0; i < p.client_threads; i++) {
            getChunk();
        }

        for (int i = 0; i < totalThread; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        return bild;
    }

    class ApfelWorker implements Runnable {
        int worker_y_start, worker_y_stop, worker_x_start, worker_x_stop;
        int worker_runden, worker_max_iter;
        double worker_xmin, worker_xmax, worker_ymin, worker_ymax;

        public ApfelWorker(int worker_y_start, int worker_y_stop, int worker_x_start, int worker_x_stop, int worker_runden, int worker_max_iter, double worker_xmin, double worker_xmax, double worker_ymin, double worker_ymax) {
            this.worker_y_start = worker_y_start;
            this.worker_y_stop = worker_y_stop;
            this.worker_x_start = worker_x_start;
            this.worker_x_stop = worker_x_stop;
            this.worker_runden = worker_runden;
            this.worker_max_iter = worker_max_iter;
            this.worker_xmin = worker_xmin;
            this.worker_xmax = worker_xmax;
            this.worker_ymin = worker_ymin;
            this.worker_ymax = worker_ymax;
        }

        @Override
        public void run() {
            try {
                int resultY_index = 0;
                int resultX_index = 0;
                int[][] result = master.bild_rechnen(p.workers_threads, worker_max_iter, p.max_betrag, worker_y_start, worker_y_stop,  worker_x_start, worker_x_stop, p.xpix, p.ypix, worker_xmin, worker_xmax, worker_ymin, worker_ymax);
                for (int y = worker_y_start; y < worker_y_stop; y++) {
                    for (int x = worker_x_start; x < worker_x_stop; x++) {
                        int iter = result[resultX_index][resultY_index];
                        
                        /* if(iter == max_iter){
                            bild[this_runden][x][y] = Color.BLACK;
                        }else{
                            float c = (float) iter / max_iter * p.farbe_number;
                            bild[this_runden][x][y] = Color.getHSBColor(c, 1f, 1f);
                        } */

                        if (iter == worker_max_iter) {
                            if(p.show_chunk_line && (y == worker_y_stop - 1 || x == worker_x_stop - 1)){
                                bild[worker_runden][x][y] = Color.getHSBColor(1f, 1f, 1f);
                            }else{
                                bild[worker_runden][x][y] = Color.BLACK;
                            }
                        } else {
                            if(p.show_chunk_line && (y == worker_y_stop - 1 || x == worker_x_stop - 1)){
                                bild[worker_runden][x][y] = Color.BLACK;
                            }else{
                                float c = (float) iter / worker_max_iter * p.farbe_number;
                                bild[worker_runden][x][y] = Color.getHSBColor(c, 1f, 1f);
                            }
                        }
                        resultX_index++;
                    }
                    resultX_index = 0;
                    resultY_index++;
                }
                getChunk();
            } catch (RemoteException e) {
                p.stopVideo = true;
                p.v.update_info("Thread Error!");
            }
        }
    }
}
