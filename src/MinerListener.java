public class MinerListener implements MinerListenerInterface {
    private MinerThreadPoolExecutor tp;

    public void nonceFound(int val){
        tp.shutdownNow();
        //System.out.println("Shutting down tp");
    }

    public void addShutdown(MinerThreadPoolExecutor tp){
        this.tp = tp;
    }
}
