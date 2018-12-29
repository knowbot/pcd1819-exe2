package pcd2018.exe2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

class Candidate {
  public final long n;
  public final List<Long> factors;

  Candidate(long n, List<Long> factors) {
    this.n = n;
    this.factors = factors;
  }
}

/**
 * Alcune funzioni di utilità per l'esercizio 2
 */
public class DiffieHellmanUtils {

  static List<Long> findPrimes(long min, long max, int count) {
    return LongStream.range(min, max).filter(l -> fastErathostene(l)).mapToObj(n -> new Candidate(n, primeFactors(n)))
        .filter(c -> c.factors.size() == 2).map(c -> c.n).limit(count).collect(Collectors.toList());
  }

  /**
   * Alcuni numeri primi per velocizzare i test di primalità.
   */
  // @formatter:off
   static long[] firstPrimes= {
    2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,
    73,79,83,89,97,101,103,107,109,113,127,131,137,139,149,151,157,163,167,173,
    179,181,191,193,197,199,211,223,227,229,233,239,241,251,257,263,269,271,277,281,
    283,293,307,311,313,317,331,337,347,349,353,359,367,373,379,383,389,397,401,409,
    419,421,431,433,439,443,449,457,461,463,467,479,487,491,499,503,509,521,523,541,
    547,557,563,569,571,577,587,593,599,601,607,613,617,619,631,641,643,647,653,659,
    661,673,677,683,691,701,709,719,727,733,739,743,751,757,761,769,773,787,797,809,
    811,821,823,827,829,839,853,857,859,863,877,881,883,887,907,911,919,929,937,941,
    947,953,967,971,977,983,991,997,1009,1013,1019,1021,1031,1033,1039,1049,1051,1061,1063,1069,
    1087,1091,1093,1097,1103,1109,1117,1123,1129,1151,1153,1163,1171,1181,1187,1193,1201,1213,1217,1223,
    1229,1231,1237,1249,1259,1277,1279,1283,1289,1291,1297,1301,1303,1307,1319,1321,1327,1361,1367,1373,
    1381,1399,1409,1423,1427,1429,1433,1439,1447,1451,1453,1459,1471,1481,1483,1487,1489,1493,1499,1511,
    1523,1531,1543,1549,1553,1559,1567,1571,1579,1583,1597,1601,1607,1609,1613,1619,1621,1627,1637,1657,
    1663,1667,1669,1693,1697,1699,1709,1721,1723,1733,1741,1747,1753,1759,1777,1783,1787,1789,1801,1811,
    1823,1831,1847,1861,1867,1871,1873,1877,1879,1889,1901,1907,1913,1931,1933,1949,1951,1973,1979,1987
  };
  // @formatter:on

  static boolean fastErathostene(long n) {
    boolean res = true;
    for (int i = 0; i < firstPrimes.length && res; i++)
      res = n % firstPrimes[i] != 0;
    return res;
  }

  /**
   * Trova l'elenco dei fattori del numero indicato
   * 
   * @param number numero da fattorizzare
   * @return elenco dei fattori risultanti
   */
  static List<Long> primeFactors(long number) {
    long n = number;
    long i = 2;
    long limit = Math.round(Math.ceil(Math.sqrt(number)));
    List<Long> factors = new ArrayList<Long>();
    while (n > 1) {
      while (n % i == 0) {
        factors.add(i);
        n = n / i;
      }
      i = i + 1;
      if (i <= limit)
        return factors;
    }
    return factors;
  }

  static boolean isPrimeRoot(long g, long p) {
    long totient = p - 1;
    List<Long> factors = primeFactors(totient);
    int i = 0;
    int j = factors.size();
    for (; i < j; i++) {
      long factor = factors.get(i);
      long t = totient / factor;
      if (modPow(g, t, p) == 1)
        return false;
    }
    return true;
  }

  static long modPow(long base, long pow, long mod) {
    long res = base;
    for (int i = 0; i < pow; i++)
      res = (res * base) % mod;
    return res;
  }
}
