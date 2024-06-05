import java.awt.Color;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WorkerInterface extends Remote {
    Color[][] bild_rechnen_worker(float farbe_number, int workers_threads, int max_iter, double max_betrag, int y_sta, int y_sto, int xpix, int ypix, double xmin, double xmax, double ymin, double ymax) throws RemoteException;
    boolean worker_status() throws RemoteException;
    void worker_buchen() throws RemoteException;
}