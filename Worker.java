import java.awt.Color;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Worker extends UnicastRemoteObject implements WorkerInterface {

    Worker() throws RemoteException {

    }

    @Override
    synchronized public Color[][] bild_rechnen_worker(int max_iter, double max_betrag, int y_sta, int y_sto, int xpix, int ypix, double xmin, double xmax, double ymin, double ymax, int thread_sum) throws RemoteException {
        //System.out.println("max_iter = " + max_iter);
        //System.out.println("max_betrag = " + max_betrag);
        //System.out.println("y_sta = " + y_sta);
        //System.out.println("y_sto = " + y_sto);
        //System.out.println("xpix = " + xpix);
        //System.out.println("ypix = " + ypix);
        //System.out.println("xmin = " + xmin);
        //System.out.println("xmax = " + xmax);
        //System.out.println("ymin = " + ymin);
        //System.out.println("ymax = " + ymax);
        //System.out.println("thread_sum = " + thread_sum);

        System.out.println("xmin "+ xmin + " ymin " + ymin + " - y: " + y_sta + " bis " + y_sto);
        
        Color[][] colors = new Color[xpix][ypix];
        
        Thread[] threads = new Thread[thread_sum];
        int rowsPerThread = (y_sto-y_sta) / thread_sum;

        for (int i = 0; i < thread_sum; i++) {
            int y_start = i * rowsPerThread + y_sta;
            int y_end = (i == thread_sum - 1) ? y_sto : y_start + rowsPerThread;

            //System.out.println(y_start + " bis " + y_end);

            threads[i] = new Thread(() -> {
                double c_re, c_im;
                for (int y = y_start; y < y_end; y++) {
                    c_im = ymin + (ymax - ymin) * y / ypix;
    
                    for (int x = 0; x < xpix; x++) {
                        c_re = xmin + (xmax - xmin) * x / xpix;
                        int iter = calc(max_iter, max_betrag, c_re, c_im);
                        if (iter == max_iter) {
                            colors[x][y] = Color.BLACK;
                        } else {
                            float c = (float) iter / max_iter;
                            colors[x][y] = Color.getHSBColor(c, 1f, 1f);
                        }
                    }
                }
            });

            threads[i].start();
        }

        for (int i = 0; i < thread_sum; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return colors;
    }

    public int calc(int max_iter, double max_betrag, double cr, double ci) {
        int iter;
        double zr = 0, zi = 0, zr2 = 0, zi2 = 0, zri = 0, betrag = 0;
        for (iter = 0; iter < max_iter && betrag <= max_betrag; iter++) {
            zr = zr2 - zi2 + cr;
            zi = zri + zri + ci;

            zr2 = zr * zr;
            zi2 = zi * zi;
            zri = zr * zi;
            betrag = zr2 + zi2;
        }
        return iter;
    }

    public static void main(String[] args) {
        if (args.length == 2){
            try {
                String masterIP = args[0];
                int masterPort = Integer.parseInt(args[1]);

                MasterInterface master = (MasterInterface) java.rmi.registry.LocateRegistry.getRegistry(masterIP, masterPort).lookup("MasterServer");
                
                Worker worker = new Worker();

                master.worker_anmelden(worker);

                System.out.println("Worker hat eine Verbindung zum Master-Port: " + masterPort + " hergestellt\n\n");
            } catch (Exception e) {
                System.err.println("Worker exception:");
                e.printStackTrace();
            }
        }else{
            System.out.println("Erforderliche Parameter: <Master IP> <Master Port>");
        }
    }
}