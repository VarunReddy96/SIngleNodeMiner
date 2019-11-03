import java.util.concurrent.*;

public class MinerThreadPoolExecutor extends ThreadPoolExecutor {
    private MinerNotifierInterface notify;
    private boolean notificationDone = false;

    public MinerThreadPoolExecutor(MinerNotifier notify) {
        super(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(),
                100, TimeUnit.HOURS, new LinkedBlockingQueue());
        this.notify = notify;
    }

    public void setnotification(){
        this.notificationDone = false;
    }
    public void setnotifier(MinerNotifier notify) {
        this.notify = notify;
    }

    @Override
    public void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null && r instanceof Future<?> && !notificationDone) {
            try {
                Object result = ((Future<?>) r).get();
                if ((int) result != 0) {
                    synchronized ((Object) this.notificationDone) {
                        System.out.println("In threadpoolservice val reached: " + (int) result);
                        notify.foundNonce((int) result);
                        this.notificationDone = true;

                    }
                }
            } catch (Exception ce) {
                t = ce;

            }
        }
    }
}
