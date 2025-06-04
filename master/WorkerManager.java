public class WorkerManager {
    WorkerInterface worker;
    String worker_id;
    int aufgabe = 0;
    int totalAufgabe = 0;
    WorkerManager(WorkerInterface worker, String worker_id) {
        this.worker = worker;
        this.worker_id = worker_id;
    }
    void worker_arbeit_start() {
        aufgabe++;
        totalAufgabe++;
    }
    void worker_arbeit_end() {
        aufgabe--;
    }
    WorkerInterface getWorker() {
        return worker;
    }
}
