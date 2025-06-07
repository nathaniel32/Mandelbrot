import java.rmi.RemoteException;

public class MandelbrotChunk implements Runnable{
    int worker_yStart, worker_yStop, worker_xStart, worker_xStop;
    int worker_indexStufenanzahl, worker_maxIterations, worker_indexstufenanzahlChunk;
    double worker_xMinimum, worker_xMaximum, worker_yMinimum, worker_yMaximum;
    Master master;
    
    MandelbrotChunk(Master master, int worker_yStart, int worker_yStop, int worker_xStart, int worker_xStop, int worker_indexStufenanzahl, int worker_maxIterations, double worker_xMinimum, double worker_xMaximum, double worker_yMinimum, double worker_yMaximum, int worker_indexstufenanzahlChunk) {
        this.master = master;
        this.worker_yStart = worker_yStart;
        this.worker_yStop = worker_yStop;
        this.worker_xStart = worker_xStart;
        this.worker_xStop = worker_xStop;
        this.worker_indexStufenanzahl = worker_indexStufenanzahl;
        this.worker_maxIterations = worker_maxIterations;
        this.worker_xMinimum = worker_xMinimum;
        this.worker_xMaximum = worker_xMaximum;
        this.worker_yMinimum = worker_yMinimum;
        this.worker_yMaximum = worker_yMaximum;
        this.worker_indexstufenanzahlChunk = worker_indexstufenanzahlChunk;
    }

    @Override
    public void run() {
        try {
            master.calculateMandelbrotImage(worker_maxIterations, worker_yStart, worker_yStop,  worker_xStart, worker_xStop, worker_xMinimum, worker_xMaximum, worker_yMinimum, worker_yMaximum, worker_indexStufenanzahl, worker_indexstufenanzahlChunk);
        } catch (RemoteException e) {
            master.stopVideo = true;
            System.out.println("Worker Error!");
        }
        master.getChunk();
    }
}
