import java.awt.Color;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MasterInterface extends Remote {
    void worker_anmelden(WorkerInterface worker) throws RemoteException;
    Color[][] bild_rechnen(int workers_threads, int max_iter, double max_betrag, int y_sta, int y_sto, int xpix, int ypix, double xmin, double xmax, double ymin, double ymax) throws RemoteException;
}