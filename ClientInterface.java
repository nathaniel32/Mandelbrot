import java.awt.Color;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
    void draw_mandelbrot(Color[][] bild, int round) throws RemoteException;
}