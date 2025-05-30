import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WorkerInterface extends Remote {
    int[][] calculateMandelbrotImage_worker(int workersThreads, int maxIterations, double maxBetrag, int yStart, int yStop, int xStart, int xStop, int xpix, int ypix, double xMinimum, double xMaximum, double yMinimum, double yMaximum) throws RemoteException;
}