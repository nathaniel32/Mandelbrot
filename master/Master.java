import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Master extends UnicastRemoteObject implements MasterInterface {
    private int workerIdIndex = 1;
    private List<WorkerManager> worker_manager_list = new ArrayList<>();
    ClientInterface client;
    boolean stopVideo = false;
    int xpix;
    int ypix;
    double maxBetrag;
    int workersThreads;
    int totalThread;
    int indexstufenanzahl, indexstufenanzahlChunk;
    private int stufenanzahl;
    private int maxIterations;
    private double add_iter;
    private double zoomfaktor;
    private double cr;
    private double ci;
    private double xMinimum;
    private double xMaximum;
    private double yMinimum;
    private double yMaximum;
    private int yChunk;
    private int xChunk;
    private int master_threads;
    private int indexChunkY, rowsPerBlock;
    private int indexChunkX, columnPerBlock;
    private Thread[] threads;

    private WorkerManager searchWorker() throws RemoteException{
        WorkerManager selected_worker_manager = null;
        for (WorkerManager wm : worker_manager_list) {
            if (wm.aufgabe == 0) {
                return wm;
            }

            if (selected_worker_manager == null || wm.aufgabe < selected_worker_manager.aufgabe) {
                selected_worker_manager = wm;
            }
        }

        if (selected_worker_manager == null) {
            System.out.println("ERROR: No valid worker found!");
            throw new RemoteException("No valid worker found.");
        }
        return selected_worker_manager;
    }

    private void printActiveWorkers(){
        System.out.println("\nActive Worker: " + worker_manager_list.size());
    }

    private void removeWorker(WorkerManager worker){
        if (worker_manager_list.remove(worker)) {
            System.out.println("Worker removed: " + worker.worker_id);
        } else {
            System.out.println("Worker not found: " + worker.worker_id);
        }
        printActiveWorkers();
    }

    synchronized void getChunk(){
        if(!stopVideo){
            if(yChunk > indexChunkY && totalThread > indexstufenanzahlChunk){
                if(xChunk > indexChunkX){
                    int yStart = indexChunkY * rowsPerBlock;
                    int y_end = (indexChunkY == yChunk - 1) ? ypix : yStart + rowsPerBlock;

                    int xStart = indexChunkX * columnPerBlock;
                    int x_end = (indexChunkX == xChunk - 1) ? xpix : xStart + columnPerBlock;

                    threads[indexstufenanzahlChunk] = new Thread(new MandelbrotWorker(this, yStart, y_end, xStart, x_end, indexstufenanzahl, maxIterations, xMinimum, xMaximum, yMinimum, yMaximum));
                    threads[indexstufenanzahlChunk].start();

                    indexChunkX++;
                    indexstufenanzahlChunk++;
                }else{
                    indexChunkX = 0;
                    indexChunkY++;
                    getChunk();
                }
            }else if(stufenanzahl > indexstufenanzahl){
                maxIterations = (int)(maxIterations + zoomfaktor * add_iter);
                double xdim = xMaximum - xMinimum;
                double ydim = yMaximum - yMinimum;
                xMinimum = cr - xdim / 2 / zoomfaktor;
                xMaximum = cr + xdim / 2 / zoomfaktor;
                yMinimum = ci - ydim / 2 / zoomfaktor;
                yMaximum = ci + ydim / 2 / zoomfaktor;
                
                try {
                    client.drawMandelbrot(indexstufenanzahl);
                } catch (RemoteException e) {
                    stopVideo = true;
                    String message = "Client Error!";
                    System.out.println(message);
                }
                
                indexChunkY = 0;
                indexstufenanzahl++;
                getChunk();
            }
        }else{
            System.out.println("F Stop: ");
            for (int restIndexstufenanzahlChunk = indexstufenanzahlChunk; restIndexstufenanzahlChunk < totalThread; restIndexstufenanzahlChunk++) {
                threads[restIndexstufenanzahlChunk] = new Thread();
            }
        }
    }

    private String[] getSummary(int totalChunks) {
        List<WorkerManager> toRemove = new ArrayList<>();
        String[] result = new String[worker_manager_list.size()];
        int i = 0;
        for (WorkerManager wm : worker_manager_list) {
            double percentage = wm.totalAufgabe == 0 ? 0 : ((double) wm.totalAufgabe / totalChunks) * 100;
            result[i] = String.format("  - %s\t: %d\t(%.2f%%)", wm.worker_id, wm.totalAufgabe, percentage);
            wm.totalAufgabe = 0;
            i++;

            if (wm.aufgabe != 0){
                toRemove.add(wm);
            };
        }
        for (WorkerManager wm : toRemove) {
            System.out.println("\n" + wm.worker_id + " is error!\nActive Task: " + wm.aufgabe);
            removeWorker(wm);
        }
        return result;
    }

    Master() throws RemoteException {}

    @Override
    public void setMandelbrotVariable(int xpix, int ypix, int stufenanzahl, int maxIterations, double add_iter, double maxBetrag, double zoomfaktor, double cr, double ci, double xMinimum, double xMaximum, double yMinimum, double yMaximum, int yChunk, int xChunk, int master_threads, int workersThreads){
        this.xpix = xpix;
        this.ypix = ypix;
        this.stufenanzahl = stufenanzahl;
        this.maxIterations = maxIterations;
        this.add_iter = add_iter;
        this.maxBetrag = maxBetrag;
        this.zoomfaktor = zoomfaktor;
        this.cr = cr;
        this.ci = ci;
        this.xMinimum = xMinimum;
        this.xMaximum = xMaximum;
        this.yMinimum = yMinimum;
        this.yMaximum = yMaximum;
        this.yChunk = yChunk;
        this.xChunk = xChunk;
        this.master_threads = master_threads;
        this.workersThreads = workersThreads;
    }

    @Override
    public void startMandelbrot() {
        indexstufenanzahl = 0;
        indexstufenanzahlChunk = 0;

        indexChunkY = 0;
        rowsPerBlock = ypix / yChunk;

        indexChunkX = 0;
        columnPerBlock = xpix / xChunk;

        totalThread = xChunk * yChunk * stufenanzahl;

        threads = new Thread[totalThread];

        for (int i = 0; i < master_threads; i++) {
            getChunk();
        }

        for (int i = 0; i < totalThread; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            client.endMandelbrot(getSummary(indexstufenanzahlChunk), indexstufenanzahlChunk);
        } catch (RemoteException e) {
            stopVideo = true;
            String message = "Client Error!";
            System.out.println(message);
        }

        stopVideo = false;
    }

    @Override
    public void stopMandelbrot(){
        stopVideo = true;
    }

    @Override
    public void workerLogout(WorkerInterface worker){        
        for (WorkerManager wm : worker_manager_list) {
            if (wm.getWorker().equals(worker)) {
                removeWorker(wm);
                break;
            }
        }
    }

    @Override
    synchronized public String workerLogin(WorkerInterface worker){
        String workerID = "worker_" + workerIdIndex;
        WorkerManager worker_manager = new WorkerManager(worker, workerID);
        worker_manager_list.add(worker_manager);
        workerIdIndex++;
        printActiveWorkers();
        return workerID;
    }

    @Override
    public void clientLogin(ClientInterface client){
        this.client = client;
    }

    public int[][] calculateMandelbrotImage(int workersThreads, int maxIterations, double maxBetrag, int yStart, int yStop, int xStart, int xStop, int xpix, int ypix, double xMinimum, double xMaximum, double yMinimum, double yMaximum) throws RemoteException {
        WorkerManager worker_manager;
        synchronized (this) {
            worker_manager = searchWorker();
            worker_manager.worker_arbeit_start();
        }
        int[][] colors = worker_manager.worker.calculateMandelbrotImage_worker(workersThreads, maxIterations, maxBetrag, yStart, yStop, xStart, xStop, xpix, ypix, xMinimum, xMaximum, yMinimum, yMaximum);
        worker_manager.worker_arbeit_end();
        return colors;
    }

    public static void main(String[] args) {
        try {
            NetworkConfig rmiconfig = new NetworkConfig(args);

            System.setProperty("java.rmi.server.hostname", rmiconfig.getLocalAddress());
            Master master = new Master();
            LocateRegistry.createRegistry(rmiconfig.getMasterPort());

            String masterUrl = "rmi://" + rmiconfig.getLocalAddress() + ":" + rmiconfig.getMasterPort() + "/" + rmiconfig.getMasterService();
            Naming.rebind(masterUrl, master);

            System.out.println("\n\n=> Master gestartet...\nURL: " + masterUrl + "\nMaster Address\t: " + rmiconfig.getLocalAddress() + "\nMaster Port\t: " + rmiconfig.getMasterPort() + "\nMaster Service\t: " + rmiconfig.getMasterService() + "\n");
        } catch (Exception e) {
            System.err.println("Master exception:");
            e.printStackTrace();
        }
    }
}