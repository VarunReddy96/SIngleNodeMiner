import java.util.ArrayList;

public class MinerNotifier implements MinerNotifierInterface{
    private ArrayList<MinerListenerInterface> listeners = new ArrayList<>();
    private boolean[] stopper;
    private int blocknum;

    public MinerNotifier(boolean[] stopper,int num){
        this.stopper = stopper;
        this.blocknum = num;

    }


    public void addListener(MinerListenerInterface newListener){
        listeners.add(newListener);
    }
    
    public void foundNonce(int val){
        this.stopper[0] = true;
        //System.out.println("The val of nonce is: " +val);

        for (MinerListenerInterface listener : listeners) {
            //System.out.println("In NOTIFIER_________________________");
            listener.nonceFound(this.blocknum);
        }
    }
}
