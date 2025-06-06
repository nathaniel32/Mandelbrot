import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Master extends UnicastRemoteObject implements MasterInterface {
    private int workerIdIndex = 1;
    private List<WorkerManager> worker_manager_list = new ArrayList<>();

    private class WorkerManager {
        private WorkerInterface worker;
        private String worker_id;
        private int aufgabe = 0;
        private int totalAufgabe = 0;
        private WorkerManager(WorkerInterface worker, String worker_id) {
            this.worker = worker;
            this.worker_id = worker_id;
        }
        private void worker_arbeit_start() {
            aufgabe++;
            totalAufgabe++;
        }
        private void worker_arbeit_end() {
            aufgabe--;
        }
        private WorkerInterface getWorker() {
            return worker;
        }
    }

    private WorkerManager searchWorker() throws RemoteException{
        WorkerManager selected_worker_manager = null;
        for (WorkerManager wm : worker_manager_list) {
            //System.out.println(this_worker_manager.worker + ", " + this_worker_manager.aufgabe);

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
        
        /* for (WorkerManager manager : worker_manager_list) {
            System.out.println("ID: " + manager.worker_id);
        } */
    }

    private void removeWorker(WorkerManager worker){
        if (worker_manager_list.remove(worker)) {
            System.out.println("Worker removed: " + worker.worker_id);
        } else {
            System.out.println("Worker not found: " + worker.worker_id);
        }
        printActiveWorkers();
    }

    Master() throws RemoteException {}

    @Override
    public void workerLogout(WorkerInterface worker){
        //worker_manager_list.removeIf(manager -> manager.getWorker().equals(worker));
        
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
    public String[] getSummary(int totalChunks) {
        //List<WorkerManager> toRemove = new ArrayList<>();
        String[] result = new String[worker_manager_list.size()];
        int i = 0;
        for (WorkerManager wm : worker_manager_list) {
            double percentage = wm.totalAufgabe == 0 ? 0 : ((double) wm.totalAufgabe / totalChunks) * 100;
            result[i] = String.format("  - %s\t: %d\t(%.2f%%)", wm.worker_id, wm.totalAufgabe, percentage);
            wm.totalAufgabe = 0;
            i++;

            /* if (wm.aufgabe != 0){
                toRemove.add(wm);
            }; */
        }
        /* for (WorkerManager wm : toRemove) {
            System.out.println("\n" + wm.worker_id + " is error!\nActive Task: " + wm.aufgabe);
            removeWorker(wm);
        } */
        return result;
    }

    @Override
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
            //Master master = new Master();
            MasterInterface master = new Master();
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