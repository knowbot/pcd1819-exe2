package pcd2018.exe2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class DiffieHellmanUtilsTest {

  @Test
  @Tag("Exercise-2")
  public void modPowTest() {
    long[] res = { 2, 4, 8, 5, 10, 9, 7, 3, 6, 1 };
    for (int i = 1; i < res.length; i++) {
      assertEquals(res[i], DiffieHellmanUtils.modPow(2, i, 11));
    }
  }

  @Test
  @Tag("Exercise-2")
  public void isPrimeRootTest() {
    assertTrue(DiffieHellmanUtils.isPrimeRoot(2, 11));
    assertTrue(DiffieHellmanUtils.isPrimeRoot(5, 23));
    assertFalse(DiffieHellmanUtils.isPrimeRoot(7, 15));
  }

  @Test
  @Tag("Exercise-2")
  public void exchangeTest() {
    long p = 23, base = 5;
    assertTrue(DiffieHellmanUtils.isPrimeRoot(5, 23));
    long secretA = 4;
    long publicA = DiffieHellmanUtils.modPow(base, secretA, p);
    long secretB = 3;
    long publicB = DiffieHellmanUtils.modPow(base, secretB, p);
    assertEquals(DiffieHellmanUtils.modPow(publicB, secretA, p), DiffieHellmanUtils.modPow(publicA, secretB, p));
  }

  @Test
  @Tag("Exercise-2")
  public void primeTest() {
    List<Long> primes = DiffieHellmanUtils.findPrimes(70, 2000, 300);
    int idx = 19;
    for (long n : primes) {
      assertEquals(DiffieHellmanUtils.firstPrimes[idx], n, "prime " + n);
      idx++;
    }
  }

  @Test
  @Tag("Exercise-2")
  public void searchTest() {
    long p = 8503057;

    System.out.println(String.format("%x", p));

    assertTrue(DiffieHellmanUtils.isPrimeRoot(10009, p));

    long base = 10009;
    long secretA = 42;
    long publicA = DiffieHellmanUtils.modPow(base, secretA, p);
    long secretB = 123;
    long publicB = DiffieHellmanUtils.modPow(base, secretB, p);
    assertEquals(DiffieHellmanUtils.modPow(publicB, secretA, p), DiffieHellmanUtils.modPow(publicA, secretB, p));

    System.out.println(String.format("a %d b %d A %d B %d", secretA, secretB, publicA, publicB));

    System.out.println(DiffieHellmanUtils.modPow(publicB, secretA, p));
  }

}
