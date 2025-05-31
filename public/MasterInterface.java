import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MasterInterface extends Remote {
    String[] getSummary() throws RemoteException;
    String workerLogin(WorkerInterface worker) throws RemoteException;
    void workerLogout(WorkerInterface worker) throws RemoteException;
    int[][] calculateMandelbrotImage(int workersThreads, int maxIterations, double maxBetrag, int yStart, int yStop, int xStart, int xStop, int xpix, int ypix, double xMinimum, double xMaximum, double yMinimum, double yMaximum) throws RemoteException;
}