import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
    void setResultMandelbrot(int[][] result, int indexstufenanzahlChunk, int totalThread, int indexstufenanzahl, int worker_yStart, int worker_yStop, int worker_xStart, int worker_xStop, int worker_maxIterations, int worker_stufenanzahl, int master_thread) throws RemoteException;
    void drawMandelbrot(int indexstufenanzahl) throws RemoteException;
    void endMandelbrot(String[] summary, int indexstufenanzahlChunk) throws RemoteException;
}