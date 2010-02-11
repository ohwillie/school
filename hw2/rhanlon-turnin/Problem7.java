import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Problem7 {
  public static void main(String[] args) throws NoSuchAlgorithmException {
    for (int i = 8; i <= 40; i += 8) {
      System.out.println("-------");
      System.out.println("n = " + i);
      System.out.println("-------");
      for (int j = 0; j < 5; j++) {
        new Problem7(i);
      }
      System.out.println("--------------");
    }
  }

  public Problem7(int n) throws NoSuchAlgorithmException {
    int nBytes = n / 8;
    
    Random rand = new Random();
    MessageDigest md = MessageDigest.getInstance("SHA-512"); 
    Map<String, BigInteger> partials = new HashMap<String, BigInteger>();
    Map<BigInteger, String> full = new HashMap<BigInteger, String>();
    BigInteger cur = new BigInteger(rand.nextInt(512), rand);

    long now = System.currentTimeMillis();
    while (true) {
      cur = cur.add(BigInteger.ONE);
      byte[] bytes = cur.toByteArray();
      String digestStr = new String(md.digest(bytes)),
             digestStrTrunc = digestStr.substring(0, nBytes);
      full.put(cur, digestStr);
      BigInteger ret = partials.put(digestStrTrunc, cur);
      if (ret != null && !ret.equals(cur)) {
        System.out.println("This run took " + (System.currentTimeMillis() - now) + " ms");
        printResults(n, cur, ret, full.get(ret), full.get(cur));
        return;
      }
    }
  }
  
  private void printResults(int n, BigInteger m, BigInteger ret, String hash1, String hash2) {
    System.out.println("Found a collision for n = " + n + "!");
    System.out.println("Strings hashed:");
    System.out.println("    " + m);
    System.out.println("    " + ret);
    System.out.println("The hashes:");
    System.out.print("    0x");
    byte[] hash1bytes = hash1.getBytes();
    for (int i = 0; i < hash1bytes.length; i++) {
      System.out.printf("%02X ", hash1bytes[i]);
    }
    System.out.println();
    System.out.print("    0x");
    byte[] hash2bytes = hash2.getBytes();
    for (int i = 0; i < hash2bytes.length; i++) {
      System.out.printf("%02X ", hash2bytes[i]);
    }
    System.out.println();
    System.out.flush();
  }
}
