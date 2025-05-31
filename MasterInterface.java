import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MasterInterface extends Remote {
    void video_init(int runden, double xmin, double xmax, double ymin, double ymax, double zoomRate, int xpix, int ypix, double cr, double ci, int max_iter, int layer, int thread, double max_betrag) throws RemoteException;
    void video_start() throws RemoteException;
    void worker_anmelden(WorkerInterface worker) throws RemoteException;
    void client_anmelden(ClientInterface client) throws RemoteException;
}