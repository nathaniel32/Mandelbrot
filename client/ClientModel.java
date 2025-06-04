import java.rmi.RemoteException;

public class ClientModel {
    private ClientPresenter p;
    private MasterInterface master;

    public ClientModel(ClientPresenter p, MasterInterface master) {
        this.p = p;
        this.master = master;
    }

    void stopMandelbrot(){
        try {
            master.stopMandelbrot();
        } catch (RemoteException e) {
            p.stopVideo = true;
            String message = "Master Error!";
            p.v.showInfo(message);
            System.out.println(message);
        }
    }

    void startMandelbrot(){
        try {
            master.setMandelbrotVariable(p.xpix, p.ypix, p.stufenanzahl, p.maxIterations, p.add_iter, p.maxBetrag, p.zoomfaktor, p.cr, p.ci, p.xMinimum, p.xMaximum, p.yMinimum, p.yMaximum, p.yChunk, p.xChunk, p.client_threads, p.workersThreads);
            master.startMandelbrot();
        } catch (RemoteException e) {
            p.stopVideo = true;
            String message = "Worker/Master Error!";
            p.v.showInfo(message);
            System.out.println(message);
        }
    }
}