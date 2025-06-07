import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WorkerInterface extends Remote {
    void calculateMandelbrotImage_worker(ClientInterface client, int workersThreads, int maxIterations, double maxBetrag, int yStart, int yStop, int xStart, int xStop, int xpix, int ypix, double xMinimum, double xMaximum, double yMinimum, double yMaximum, int totalTask, int indexStufenanzahl, int indexstufenanzahlChunk, int masterThread) throws RemoteException;
}