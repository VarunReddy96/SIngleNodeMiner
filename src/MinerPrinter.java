import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MinerPrinter implements MinerListenerInterface {
    private Future[] vals;
    private boolean foundval = false;
    private long start;
    private MinerThreadPoolExecutor threadpool;
    private boolean status;

    public MinerPrinter(long start,MinerThreadPoolExecutor tp) {
        this.start = start;
        this.threadpool = tp;
        this.status = false;
    }
    public boolean getstatus(){
        return this.status;
    }



    @Override
    public void nonceFound(int blocknum) {
        for (Future val : vals) {
            //System.out.println("IN for loop :");
            try {
                if (val.isDone() && !this.foundval && (int)val.get() != 0) {

                    long end = System.currentTimeMillis();
                    System.out.println("Nonce Found:" + val.get() + " time taken(ms): " + (end - this.start));
                    this.foundval = true;
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            } catch(ExecutionException e){
                e.printStackTrace();
            }
        }
        System.out.println("Done with priniting");
        this.status = false;
        if(blocknum==10) {
            System.out.println("Shutting tp down");
            threadpool.shutdownNow();
        }
    }

    public void setFutureArray(Future[] vals) {
        this.status = true;
        this.vals = vals;
    }
}
