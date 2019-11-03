
import javax.swing.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.Future;


public class ParallelMiner {
    private int start,end;

    public ParallelMiner(int start, int end){
        this.start = start;
        this.end = end;
    }


    /**
     * convert byte[] to hex string
     * @param hash
     * @return hex string
     */
    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * get a sha256 of the input string
     * @param inputString
     * @return resulting hash in hex string
     */
    public static String SHA256(String inputString) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return bytesToHex(sha256.digest(inputString.getBytes(StandardCharsets.UTF_8)));
        }catch (NoSuchAlgorithmException ex) {
            System.err.println(ex.toString());
            return null;
        }
    }

    public static String HexValueDivideBy(String hexValue, int val) {
        BigInteger tmp = new BigInteger(hexValue,16);
        tmp = tmp.divide(BigInteger.valueOf(val));
        String newHex = bytesToHex(tmp.toByteArray());
        while (newHex.length() < hexValue.length()) {
            newHex = '0' + newHex;
        }
        return newHex;
    }

    public static String HexValueMultipleBy(String hexValue, int val) {
        BigInteger tmp = new BigInteger(hexValue,16);
        tmp = tmp.multiply(BigInteger.valueOf(val));
        String newHex = bytesToHex(tmp.toByteArray());
        while (newHex.length() < hexValue.length()) {
            newHex = '0' + newHex;
        }
        return newHex;
    }

    /**
     * get a randomized target hash
     * @return randomized target hash
     */
    public static String getTargetHash() {
        Random rand = new Random();
        int randInt = rand.nextInt(1000);
        return SHA256(String.valueOf(randInt));
    }
    public static void main(String[] args){

        // number of blocks to be generated or number of rounds; default to 5
        int numberOfBlocks=10;
        // average block generation time, default to 30 Secs.
        double avgBlockGenerationTimeInSec = 30.0;

        // init block hash
        String initBlockHash = SHA256("CSCI-654 Foundations of Parallel Computing");
        //System.out.println("Initial Block Hash:  " + initBlockHash);
        // init target hash
        String initTargetHash = "0000092a6893b712892a41e8438e3ff2242a68747105de0395826f60b38d88dc";
        //System.out.println("Initial Target Hash: " + initTargetHash);
        System.out.println();

        int currentBlockID = 1;
        int nonce = 0;
        String tmpBlockHash = initBlockHash;
        String tmpTargetHash = initTargetHash;
        MyTimer myTimer;
        MinerListener listener;

        Future[] futures;
        boolean[] stopper = {false};
        MinerNotifier notifier =  new MinerNotifier(stopper,currentBlockID);
        long start = System.currentTimeMillis();
        MinerThreadPoolExecutor threadPool = new MinerThreadPoolExecutor(notifier);
        MinerPrinter print = new MinerPrinter(start,threadPool);
        int coreCount = Runtime.getRuntime().availableProcessors();
        while(currentBlockID <= numberOfBlocks) {
            System.out.println();
            if(!print.getstatus()) {

                stopper[0] = false;
                threadPool.setnotification();
                System.out.println("New Block Hash:  " + tmpBlockHash);
                System.out.println("New Target Hash: " + tmpTargetHash);
                System.out.println();
                start = System.currentTimeMillis();

                listener = new MinerListener();
                notifier = new MinerNotifier(stopper,currentBlockID);
                //notifier.addListener(listener);
                print = new MinerPrinter(start,threadPool);
                threadPool.setnotifier(notifier);
                notifier.addListener(print);

                listener.addShutdown(threadPool);
                futures = new Future[coreCount];
                print.setFutureArray(futures);
                //System.out.println("The boolean is: "+ notifier.getstatus()+" boolean val stopper: "+!stopper[0]);
                int intMax = Integer.MAX_VALUE;
                int intMin = Integer.MIN_VALUE;
                int temp = intMin;
                if (coreCount == 1) {
                    futures[coreCount - 1] = threadPool.submit(new MinerCallable(intMin, intMax,stopper));
                } else {
                    double splitwise = ((double)intMax - (double)intMin)/  coreCount;
                    int split = (int)splitwise;
                    //System.out.println(split+"----------");
                    //System.out.println((double)intMax-intMin+"----------------------");
                    try{for (int cntr = 0; cntr < coreCount - 1; cntr++) {
                        //System.out.println("cntr: "+cntr+" start of the thread: "+ temp+" end of the thread: "+ (temp +split)+ " split:"+split+ " corecount: "+ coreCount);
                        //System.out.println("The boolean is: "+ notifier.getstatus());
                        //System.out.println(" boolean val stopper: "+!stopper[0]+" in for loop");
                        futures[cntr] = threadPool.submit(new MinerCallable(temp, temp + split,stopper));
                        temp = temp + split + 1;
                    }

                    futures[coreCount - 1] = threadPool.submit(new MinerCallable(temp, intMax,stopper));}catch (Exception e){e.printStackTrace();}
                    //System.out.println("The boolean is after sending all: "+ notifier.getstatus());

                }
                //System.out.println("The boolean is before tmphasj: "+ print.getstatus());
                tmpBlockHash = SHA256(tmpBlockHash+"|"+nonce);

                currentBlockID++;
                //System.out.println("The boolean is at the end of main: "+ notifier.getstatus());
            }

        }
    }
}




