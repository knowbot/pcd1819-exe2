package pcd2018.exe2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Classe da completare per l'esercizio 2.
 */
public class DiffieHellman {
  /**
   * Limite massimo dei valori segreti da cercare
   */
  private static final int LIMIT = 65536;

  private final long p;
  private final long g;

  public DiffieHellman(long p, long g) {
    this.p = p;
    this.g = g;
  }

  /**
   * Custom class implementing the Supplier interface
   */
  public class DHSupplier implements Supplier<Callable<List<Integer>>> {

    private List<Long> computedA; // list of 's' valued computed for each 'a'
    private List<Long> computedB; // list of 's' values computed for each 'b'

    private int lowerBound; // lower bound of value interval to be checked
    private int upperBound; // upper bound of value interval to be checked

    private int currentStep; // keeps track of how many steps have been taken

    private final int threadStep; // range of values to be evaluated in a single thread, it's equal among all threads

    private DHSupplier(List<Long> computedA, List<Long> computedB, int lowerBound, int upperBound, int threadStep) {
      this.computedA = computedA;
      this.computedB = computedB;
      this.lowerBound = lowerBound;
      this.upperBound = upperBound;
      this.currentStep = 0;
      this.threadStep = threadStep;

      try {
        validate();
      } catch (IllegalArgumentException e) {
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
    }

    /**
     * Getter methods
     *
     * @return the respective field
     */

    public List<Long> getComputedA() {
      return computedA;
    }

    public List<Long> getComputedB() {
      return computedB;
    }

    public int getLowerBound() {
      return lowerBound;
    }

    public int getUpperBound() {
      return upperBound;
    }

    public int getThreadStep() {
      return threadStep;
    }

    public int getCurrentStep() {
      return currentStep;
    }

    /**
     * Launches an IllegalArgumentException if the object's fields are invalid
     */
    private void validate() {
      if (!(lowerBound < upperBound))
        throw new IllegalArgumentException("Invalid lowerBound or upperBound argument, lowerBound must be less than upperBound.");
      if (threadStep == 0)
        throw new IllegalArgumentException("Invalid threadStep argument, threadStep cannot be 0.");
    }

    /**
     * @return a new DHCallableTask object operating on a range of values defined by currentStep and threadStep
     */
    @Override
    public Callable<List<Integer>> get() {
      int startValue, stopValue; //
      /*
        Allows for incremental or decremental steps, at the user's discretion.
       */
      if (threadStep > 0) { // if threadStep is a positive value
        //compute range of values
        startValue = lowerBound + (threadStep * currentStep);
        stopValue = lowerBound + (threadStep * (currentStep + 1));

        if (stopValue < upperBound) // if stopValue is less than the upper bound
          currentStep++; // currentStep is incremented, new range of values will be used next time
        else
          stopValue = upperBound; // currentStep not incremented (no next step) and stopValue replaced by upperBound
        // this seemingly overcomplicated ordeal is in place to avoid IndexOutOfBounds errors
      } else { //if threadStep is a negative value
        //compute range of values
        startValue = upperBound + (threadStep * (currentStep + 1));
        stopValue = upperBound + (threadStep * currentStep);

        if (startValue > lowerBound) // if startValue is greater than the lower bound
          currentStep++; // currentStep is incremented, new range of values will be used next time
        else
          startValue = lowerBound; // currentStep not incremented and startValue replaced by lowerBound
      }
      return new DHCallableTask(computedA, computedB, startValue, stopValue);
    }

    /**
     * Custom class implementing the Callable interface
     */
    public class DHCallableTask implements Callable<List<Integer>> {

      private List<Long> computedA; // list of 's' valued computed for each 'a'
      private List<Long> computedB; // list of 's' values computed for each 'b'

      private int start;  // starting value
      private int stop; // stop of values to be checked

      DHCallableTask(List<Long> AList, List<Long> BList, int start, int stop) {
        this.computedA = AList;
        this.computedB = BList;
        this.start = start;
        this.stop = stop;
      }

      public List<Long> getComputedA() {
        return computedA;
      }

      public List<Long> getComputedB() {
        return computedB;
      }

      public int getStart() {
        return start;
      }

      public int getStop() {
        return stop;
      }

      /**
       * @return a List<Integer> containing all the a,b couples (a values on even indexes, b values on odd indexes) that
       * fulfill the condition 's'; the couples' order is sorted by the value of a (ascending).
       */
      @Override
      public List<Integer> call() {
        System.out.println("Starting thread #" + Thread.currentThread().getId() + " with value interval for 'a' -> [" + this.start + ", " + this.stop + "].");

        List<Integer> taskRes = new ArrayList<>(); // list where we'll store the task's results

        computedA.subList(start, stop)// cut the sublist we want to examine in the task
                .forEach(aVal -> { // for each value in the sublist
                  /*
                   * Two variants of the same operation, I believe the second one should be slightly faster since it
                   * removes O(n) operations (doesn't need to execute the contains() method!).
                   **/
                  // VARIANT 1
                  /*
                    if there is an identical value in computedB (a and b produce the same s value), indB equals the
                    b value; else, it's -1 (indexOf returns -1 if its argument is not in the list).

                  if (computedB.contains(aVal)) {
                    taskRes.add(computedA.indexOf(aVal)); // add the index of the computed s for a, which is a's value
                    taskRes.add(computedB.indexOf(aVal)); // same for b
                  }*/
                  // VARIANT 2
                  int indB = computedB.indexOf(aVal); // finds the index of the first matching element
                  if (indB != -1) { // if it didn't find any, returns -1 and skips this part
                    taskRes.add(computedA.indexOf(aVal)); // otherwise, add the a value we were checking
                    taskRes.add(indB); // and add b
                  }

                });

        System.out.println("Ending thread #" + Thread.currentThread().getId() + ".");
        return taskRes;
      }
    }
  }


  /**
   * Metodo da completare
   *
   * @param publicA valore di A
   * @param publicB valore di B
   * @return tutte le coppie di possibili segreti a,b
   */
  public List<Integer> crack(long publicA, long publicB) {
    List<Integer> res = new ArrayList<Integer>();
    List<Long> aList, bList;

    /*
      Compute (pubB^a)mod(p) for all possible values of 'a'
      ATTENTION: for better understanding of the code, please note that by doing this the index of any value in the list
      actually corresponds to the 'a' value that was used to compute it! Same goes for the next list, but with 'b'
    */
    aList = IntStream
            .rangeClosed(0, LIMIT) // generate all possible values of a
            .parallel() // make the operation parallel to save time
            .sorted() // sort the results, as parallelism doesn't maintain order
            .mapToObj(a -> DiffieHellmanUtils.modPow(publicB, a, p)) // apply the function
            .collect(Collectors.toList()); // anc collect the results into a list of values
    /*
      Compute (pubA^b) mod p for all possible values of b
    */
    bList = IntStream
            .rangeClosed(0, LIMIT)
            .parallel()
            .sorted()
            .mapToObj(b -> DiffieHellmanUtils.modPow(publicA, b, p))
            .collect(Collectors.toList());
    /*
      available processors, since we want to instantiate one thread per processor unit
    */
    int availCPUs = Runtime.getRuntime().availableProcessors();
    int step = Math.round(LIMIT / availCPUs);
    /*
      Just like the example during class
    */
    ThreadPoolExecutor executor =
            (ThreadPoolExecutor) Executors.newFixedThreadPool(availCPUs);
    /*
      Create a supplier for the tasks we want to run
    */
    DHSupplier dhSupplier = new DHSupplier(aList, bList, 0, LIMIT, step);
    /*
      Create the task list
    */
    List<Callable<List<Integer>>> dhCallableList = new ArrayList<>();
    /*
      Fill the task list with a task for every available core
    */
    for (int i = 0; i < availCPUs; i++)
      dhCallableList.add(dhSupplier.get());
    /*
      Schedule the execution of our tasks
    */
    System.out.println("Scheduling computations, please stand by...");
    List<Future<List<Integer>>> futures = null;
    try {
      futures = executor.invokeAll(dhCallableList);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("Done scheduling.");

    /*
      Collect the tasks' results into a single variable
    */
    System.out.println("Collecting results from computations, please stand by...");
    for (Future<List<Integer>> future : futures) {
      try {
        res.addAll(future.get());
      } catch (ExecutionException | InterruptedException e) {
        e.printStackTrace();
      }
    }

    System.out.println("Done collecting results from computations. Total number of couples found: " + res.size() / 2);
    return res;
  }
}