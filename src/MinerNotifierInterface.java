public interface MinerNotifierInterface {

    public void addListener(MinerListenerInterface listener);

    public void foundNonce(int val);
}
