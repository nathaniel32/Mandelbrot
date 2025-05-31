import java.awt.Color;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WorkerInterface extends Remote {
    Color[][] bild_rechnen_worker(int max_iter, double max_betrag, int y_sta, int y_sto, int xpix, int ypix, double xmin, double xmax, double ymin, double ymax, int thread) throws RemoteException;
}