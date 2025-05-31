import java.awt.Color;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Master extends UnicastRemoteObject implements MasterInterface {
    private List<WorkerInterface> worker_list = new ArrayList<>();
    private int indexverteilung_worker = 0;
    private ClientInterface client;
    Color[][] bild;
    int runden;
    double zoomRate;
    int master_thread;
    int worker_thread;
    int max_iter;
    int xpix, ypix;
    double xmin, xmax, ymin, ymax;
    double cr, ci;
    double max_betrag;
    
    Master() throws RemoteException {

    }

    @Override
    public void worker_anmelden(WorkerInterface worker){
        worker_list.add(worker);
    }

    @Override
    public void client_anmelden(ClientInterface client){
        this.client = client;
    }

    private Color[][] bild_rechnen(int y_sta, int y_sto, double xmin, double xmax, double ymin, double ymax) throws RemoteException {
        WorkerInterface worker;
        synchronized (this) {
            worker = worker_list.get(indexverteilung_worker);
            System.out.println("Give to Worker " + indexverteilung_worker);

            indexverteilung_worker = (indexverteilung_worker + 1) % worker_list.size();
        }
        return worker.bild_rechnen_worker(max_iter, max_betrag, y_sta, y_sto, xpix, ypix, xmin, xmax, ymin, ymax, worker_thread);
    }

    private void split_image() {
        int row_length = ypix / master_thread;
        Thread[] threads = new Thread[master_thread];

        for (int i = 0; i < master_thread; i++) {
            int y_layer_start = i * row_length;
            int y_layer_end = (i == worker_thread - 1) ? ypix : y_layer_start + row_length;

            System.out.println(y_layer_end - y_layer_start);
            threads[i] = new Thread(new MandelWorker(y_layer_start, y_layer_end));
            threads[i].start();
        }

        for (int i = 0; i < master_thread; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void video_init(int runden, double xmin, double xmax, double ymin, double ymax, double zoomRate, int xpix, int ypix, double cr, double ci, int max_iter, int master_thread, int worker_thread, double max_betrag){
        bild = new Color[xpix][ypix];
        this.xpix = xpix;
        this.ypix = ypix;
        this.runden = runden;
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
        this.zoomRate = zoomRate;
        this.cr = cr;
        this.ci = ci;
        this.max_iter = max_iter;
        this.master_thread = master_thread;
        this.worker_thread = worker_thread;
        this.max_betrag = max_betrag;

        System.out.println("video_init called with:");
        System.out.println("runden = " + runden);
        System.out.println("xmin = " + xmin);
        System.out.println("xmax = " + xmax);
        System.out.println("ymin = " + ymin);
        System.out.println("ymax = " + ymax);
        System.out.println("zoomRate = " + zoomRate);
        System.out.println("xpix = " + xpix);
        System.out.println("ypix = " + ypix);
        System.out.println("cr = " + cr);
        System.out.println("ci = " + ci);
        System.out.println("max_iter = " + max_iter);
        System.out.println("master_thread = " + master_thread);
        System.out.println("worker_thread = " + worker_thread);
    }

    public void video_start(){
        for (int i = 1; i <= runden; i++) {
            split_image();
            double xdim = xmax - xmin;
            double ydim = ymax - ymin;
            xmin = cr - xdim / 2 / zoomRate;
            xmax = cr + xdim / 2 / zoomRate;
            ymin = ci - ydim / 2 / zoomRate;
            ymax = ci + ydim / 2 / zoomRate;
            try {
                client.draw_mandelbrot(bild, i);
            } catch (Exception e) {
                System.out.println("Client Error!");
            }
        }
    }

    class MandelWorker implements Runnable {
        int y_sta, y_sto;

        public MandelWorker(int y_start, int y_stopp) {
            this.y_sta = y_start;
            this.y_sto = y_stopp;
        }

        @Override
        public void run() {
            try {
                Color[][] result = bild_rechnen(y_sta, y_sto, xmin, xmax, ymin, ymax);
                for (int y = y_sta; y < y_sto; y++) {
                    for (int x = 0; x < xpix; x++) {
                        bild[x][y] = result[x][y];
                    }
                }
            } catch (RemoteException e) {
                System.out.println("error");
            }
        }
    }

    public static void main(String[] args) {
        if (args.length == 1){
            try {
                InetAddress localhost = InetAddress.getLocalHost();
                String currentIP = localhost.getHostAddress();
                int masterPort = Integer.parseInt(args[0]);
                String masterUrl = "rmi://" + currentIP + ":" + masterPort + "/MasterServer";
                Master master = new Master();
                LocateRegistry.createRegistry(masterPort);
                Naming.rebind(masterUrl, master);
                System.out.println("Master ist gestartet...\nURL: " + masterUrl + "\n\n");
            } catch (Exception e) {
                System.err.println("Master exception:");
                e.printStackTrace();
            }
        }else{
            System.out.println("Erforderliche Parameter: <Port>");
        }
    }
}