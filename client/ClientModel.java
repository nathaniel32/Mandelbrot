import java.rmi.RemoteException;
import java.awt.*;

public class ClientModel {
    private ClientPresenter p;
    private double xMinimum, xMaximum, yMinimum, yMaximum;
    private Color[][][] bild;
    private MasterInterface master;
    private int totalThread;
    private int indexstufenanzahl, indexstufenanzahlChunk;
    private int indexChunkY, rowsPerBlock;
    private int indexChunkX, columnPerBlock;
    private Thread[] threads;

    public ClientModel(ClientPresenter p, MasterInterface master) {
        this.p = p;
        this.master = master;
    }

    synchronized public void getChunk(){
        if(!p.stopVideo){
            if(p.yChunk > indexChunkY && totalThread > indexstufenanzahlChunk){
                if(p.xChunk > indexChunkX){
                    int yStart = indexChunkY * rowsPerBlock;
                    int y_end = (indexChunkY == p.yChunk - 1) ? p.ypix : yStart + rowsPerBlock;

                    int xStartrt = indexChunkX * columnPerBlock;
                    int x_end = (indexChunkX == p.xChunk - 1) ? p.xpix : xStartrt + columnPerBlock;

                    threads[indexstufenanzahlChunk] = new Thread(new MandelbrotWorker(yStart, y_end, xStartrt, x_end, indexstufenanzahl, p.maxIterations, xMinimum, xMaximum, yMinimum, yMaximum));
                    threads[indexstufenanzahlChunk].start();

                    indexChunkX++;
                    indexstufenanzahlChunk++;
                }else{
                    indexChunkX = 0;
                    indexChunkY++;
                    getChunk();
                }
            }else if(p.stufenanzahl > indexstufenanzahl){
                p.maxIterations = (int)(p.maxIterations + p.zoomfaktor * p.add_iter);
                double xdim = xMaximum - xMinimum;
                double ydim = yMaximum - yMinimum;
                xMinimum = p.cr - xdim / 2 / p.zoomfaktor;
                xMaximum = p.cr + xdim / 2 / p.zoomfaktor;
                yMinimum = p.ci - ydim / 2 / p.zoomfaktor;
                yMaximum = p.ci + ydim / 2 / p.zoomfaktor;
                
                p.v.updatePanel(bild[indexstufenanzahl]);
                
                indexChunkY = 0;
                indexstufenanzahl++;
                getChunk();
            }
            //else == end
        }else{
            //forced to stop
            for (int restIndexstufenanzahlChunk = indexstufenanzahlChunk; restIndexstufenanzahlChunk < totalThread; restIndexstufenanzahlChunk++) {
                threads[restIndexstufenanzahlChunk] = new Thread();
            }
        }
        p.currentTime = System.currentTimeMillis();
        p.v.update_time(p.currentTime - p.startTime);
        p.v.showInfo("Chunks: " + indexstufenanzahlChunk + "/" + totalThread + " | Stufenanzahl: " + indexstufenanzahl + " | Max-Iterations: " + p.maxIterations + " | Threads: " + Thread.activeCount());
    }

    Color[][][] mandelbrotImage(double xMinimum, double xMaximum, double yMinimum, double yMaximum) {
        this.xMinimum = xMinimum;
        this.xMaximum = xMaximum;
        this.yMinimum = yMinimum;
        this.yMaximum = yMaximum;

        bild = new Color[p.stufenanzahl][p.xpix][p.ypix];
        indexstufenanzahl = 0;
        indexstufenanzahlChunk = 0;

        indexChunkY = 0;
        rowsPerBlock = p.ypix / p.yChunk;

        indexChunkX = 0;
        columnPerBlock = p.xpix / p.xChunk;

        totalThread = p.xChunk * p.yChunk * p.stufenanzahl;

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

        try {
            String[] summary = master.getSummary(indexstufenanzahlChunk);
            System.out.println("\nSummary Chunks/Worker");
            for (String line : summary) {
                System.out.println(line);
            }
            System.out.println("Total Chunks\t: " + indexstufenanzahlChunk);
        } catch (RemoteException e) {
            String message = "Summary Error";
            p.v.showInfo(message);
        }
        
        return bild;
    }

    class MandelbrotWorker implements Runnable {
        int worker_yStart, worker_yStopp, worker_xStartrt, worker_xStopp;
        int worker_stufenanzahl, worker_maxIterations;
        double worker_xMinimum, worker_xMaximum, worker_yMinimum, worker_yMaximum;

        public MandelbrotWorker(int worker_yStart, int worker_yStopp, int worker_xStartrt, int worker_xStopp, int worker_stufenanzahl, int worker_maxIterations, double worker_xMinimum, double worker_xMaximum, double worker_yMinimum, double worker_yMaximum) {
            this.worker_yStart = worker_yStart;
            this.worker_yStopp = worker_yStopp;
            this.worker_xStartrt = worker_xStartrt;
            this.worker_xStopp = worker_xStopp;
            this.worker_stufenanzahl = worker_stufenanzahl;
            this.worker_maxIterations = worker_maxIterations;
            this.worker_xMinimum = worker_xMinimum;
            this.worker_xMaximum = worker_xMaximum;
            this.worker_yMinimum = worker_yMinimum;
            this.worker_yMaximum = worker_yMaximum;
        }

        @Override
        public void run() {
            try {
                int resultY_index = 0;
                int resultX_index = 0;
                int[][] result = master.calculateMandelbrotImage(p.workersThreads, worker_maxIterations, p.maxBetrag, worker_yStart, worker_yStopp,  worker_xStartrt, worker_xStopp, p.xpix, p.ypix, worker_xMinimum, worker_xMaximum, worker_yMinimum, worker_yMaximum);
                for (int y = worker_yStart; y < worker_yStopp; y++) {
                    for (int x = worker_xStartrt; x < worker_xStopp; x++) {
                        int iter = result[resultX_index][resultY_index];
                        
                        if(iter == worker_maxIterations){
                            bild[worker_stufenanzahl][x][y] = Color.BLACK;
                        }else{
                            double zn = Math.log(x * x + y * y) / 2;
                            double nu = Math.log(zn / Math.log(2)) / Math.log(2);
                            float smoothIter = (float)(iter + 1 - nu);
                            float hue = 0.95f + 10f * smoothIter / worker_maxIterations * p.farbe_number;
                            bild[worker_stufenanzahl][x][y] = Color.getHSBColor(hue % 1f, 0.6f, 1f);
                        }
                        
                        resultX_index++;
                    }
                    resultX_index = 0;
                    resultY_index++;
                }
                getChunk();
            } catch (RemoteException e) {
                p.stopVideo = true;
                String message = "Worker/Master Error!";
                p.v.showInfo(message);
                System.out.println(message);
            }
        }
    }
}
