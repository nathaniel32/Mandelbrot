import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MasterInterface extends Remote {
    String workerLogin(WorkerInterface worker) throws RemoteException;
    void workerLogout(WorkerInterface worker) throws RemoteException;
    void clientLogin(ClientInterface worker) throws RemoteException;
    void setMandelbrotVariable(int xpix, int ypix, int stufenanzahl, int maxIterations, double add_iter, double maxBetrag, double zoomfaktor, double cr, double ci, double xMinimum, double xMaximum, double yMinimum, double yMaximum, int yChunk, int xChunk, int client_threads, int workersThreads) throws RemoteException;
    void startMandelbrot() throws RemoteException;
    void stopMandelbrot() throws RemoteException;
}