import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.awt.Color;

public class Client extends UnicastRemoteObject implements ClientInterface{
    ClientPresenter p;
    Client(MasterInterface master) throws RemoteException {
        p = new ClientPresenter();
        ClientView v = new ClientView(p);
        ClientModel m = new ClientModel(p, master);
        p.init(m, v);
    }

    @Override
    public void setResultMandelbrot(int[][] result, int indexstufenanzahlChunk, int totalTask, int indexstufenanzahl, int worker_yStart, int worker_yStop, int worker_xStart, int worker_xStop, int worker_maxIterations, int master_thread){
        new Thread(() -> {
            p.currentTime = System.currentTimeMillis();
            p.time_stamp = p.currentTime - p.startTime;
            p.v.update_time(p.time_stamp);
            p.v.showInfo("Chunks: " + indexstufenanzahlChunk + "/" + totalTask + " | Stufenanzahl: " + indexstufenanzahl + " | Max-Iterations: " + worker_maxIterations + " | Client-Threads: " + Thread.activeCount() + " | Master-Threads: " + master_thread);

            int resultY_index = 0;
            int resultX_index = 0;
            for (int y = worker_yStart; y < worker_yStop; y++) {
                for (int x = worker_xStart; x < worker_xStop; x++) {
                    int iter = result[resultX_index][resultY_index];
                    
                    if(iter == worker_maxIterations){
                        p.bild[indexstufenanzahl][x][y] = Color.BLACK;
                    }else{
                        double zn = Math.log(x * x + y * y) / 2;
                        double nu = Math.log(zn / Math.log(2)) / Math.log(2);
                        float smoothIter = (float)(iter + 1 - nu);
                        float hue = 0.95f + 10f * smoothIter / worker_maxIterations * p.farbe_number;
                        p.bild[indexstufenanzahl][x][y] = Color.getHSBColor(hue % 1f, 0.6f, 1f);
                    }

                    p.buff_image.setRGB(x, y, p.bild[indexstufenanzahl][x][y].getRGB());
                    
                    resultX_index++;
                }
                resultX_index = 0;
                resultY_index++;
            }
        }).start();
    }

    @Override
    public void drawMandelbrot(){
        p.v.mandelbrot_panel.repaint();
    }

    @Override
    public void endMandelbrot(String[] summary, int indexstufenanzahlChunk){
        System.out.println("\n============ Summary ============");
        System.out.println("* Time\t\t: " + p.time_stamp + " ms");
        System.out.println("* Total Chunks\t: " + indexstufenanzahlChunk);
        System.out.println("* Chunks per Worker");
        for (String line : summary) {
            System.out.println(line);
        }
    }

    public static void main(String[] args) {
        try {
            NetworkConfig rmiconfig = new NetworkConfig(args);
            System.setProperty("java.rmi.server.hostname", rmiconfig.getLocalAddress());
            MasterInterface master = (MasterInterface) java.rmi.registry.LocateRegistry.getRegistry(rmiconfig.getTargetAddress(), rmiconfig.getMasterPort()).lookup(rmiconfig.getMasterService());
            Client client = new Client(master);
            master.clientLogin(client);
            System.out.println("\n\n=> Client hat Verbindung zum Master hergestellt\nMaster Address\t: " + rmiconfig.getTargetAddress() + "\nMaster Port\t: " + rmiconfig.getMasterPort() + "\nMaster Service\t: " + rmiconfig.getMasterService() + "\n");
        } catch (Exception e) {
            System.err.println("Fehler beim Starten des Clients");
            e.printStackTrace();
        }
    }
}