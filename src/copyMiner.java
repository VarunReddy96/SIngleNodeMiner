

import java.net.InetAddress;
import java.util.concurrent.Future;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static javax.xml.crypto.dsig.DigestMethod.SHA256;

public class copyMiner {
    private int start, end;
    private boolean noncefound = false;

    public copyMiner(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /**
     * convert byte[] to hex string
     *
     * @param hash
     * @return hex string
     */
    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * get a sha256 of the input string
     *
     * @param inputString
     * @return resulting hash in hex string
     */
    public static String SHA256(String inputString) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return bytesToHex(sha256.digest(inputString.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            System.err.println(ex.toString());
            return null;
        }
    }

    /**
     * get a randomized target hash
     *
     * @return randomized target hash
     */
    public static String getTargetHash() {
        Random rand = new Random();
        int randInt = rand.nextInt(1000);
        return SHA256(String.valueOf(randInt));
    }

    /**
     * perform the proof-of-work
     *
     * @return nonce (a 32-bit integer) that satisfies the requirements
     */

    public void mine(String inputhash, String targethash, long start) {
        int nonce = 0;
        String tmp_hash = "undefined";
        // omp parallel for
        for (nonce = this.start; nonce <= this.end; nonce++) {
            if (!this.noncefound) {
                tmp_hash = SHA256(SHA256(inputhash + String.valueOf(nonce)));
                if (targethash.compareTo(tmp_hash) > 0) {
                    // omp critical
                    {
                        //this.notifier.nonceFound(nonce, InetAddress.getLocalHost(), 100);
                        this.noncefound = true;
                        long end = System.currentTimeMillis();
                        System.out.println("Nonce found: " + nonce + " Time taken(ms): " + (end - start));

                    }
                }
            }else {
                break;
            }

        }
    }


    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        copyMiner miner = new copyMiner(Integer.MIN_VALUE, Integer.MAX_VALUE);
        String tmp_hash = "undefined";
        boolean noncefound = false;
        // omp parallel for
//        for (int nonce = Integer.MIN_VALUE; nonce <= Integer.MAX_VALUE; nonce++) {
//            if (!noncefound) {
//                tmp_hash = SHA256(SHA256( SHA256("CSCI-654 Foundations of Parallel Computing")+ String.valueOf(nonce)));
//                if ("0000092a6893b712892a41e8438e3ff2242a68747105de0395826f60b38d88dc".compareTo(tmp_hash) > 0) {
//                    // omp critical
//                    {
//                        //this.notifier.nonceFound(nonce, InetAddress.getLocalHost(), 100);
//                        long end = System.currentTimeMillis();
//                        System.out.println("Nonce found: " + nonce + " Time taken(ms): " + (end - start));
//                        noncefound = true;
//                    }
//                }
//            }
//
//        }

        miner.mine(SHA256("CSCI-654 Foundations of Parallel Computing"), "0000092a6893b712892a41e8438e3ff2242a68747105de0395826f60b38d88dc", start);
    }
}
