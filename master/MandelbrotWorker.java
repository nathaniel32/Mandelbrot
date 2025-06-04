import java.rmi.RemoteException;

public class MandelbrotWorker implements Runnable{
    int worker_yStart, worker_yStop, worker_xStart, worker_xStop;
    int worker_stufenanzahl, worker_maxIterations;
    double worker_xMinimum, worker_xMaximum, worker_yMinimum, worker_yMaximum;
    Master master;
    
    MandelbrotWorker(Master master, int worker_yStart, int worker_yStop, int worker_xStart, int worker_xStop, int worker_stufenanzahl, int worker_maxIterations, double worker_xMinimum, double worker_xMaximum, double worker_yMinimum, double worker_yMaximum) {
        this.master = master;
        this.worker_yStart = worker_yStart;
        this.worker_yStop = worker_yStop;
        this.worker_xStart = worker_xStart;
        this.worker_xStop = worker_xStop;
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
            int[][] result = master.calculateMandelbrotImage(master.workersThreads, worker_maxIterations, master.maxBetrag, worker_yStart, worker_yStop,  worker_xStart, worker_xStop, master.xpix, master.ypix, worker_xMinimum, worker_xMaximum, worker_yMinimum, worker_yMaximum);
            master.client.setResultMandelbrot(result, master.indexstufenanzahlChunk, master.totalThread, master.indexstufenanzahl, worker_yStart, worker_yStop, worker_xStart, worker_xStop, worker_maxIterations, worker_stufenanzahl, Thread.activeCount());
        } catch (RemoteException e) {
            master.stopVideo = true;
            String message = "Worker Error!";
            //p.v.showInfo(message);
            System.out.println(message);
        }
        master.getChunk();
    }
}
