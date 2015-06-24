import java.util.TreeSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ADMIN on 23.06.2015.
 */

public class Test {

    public static ArrayBlockingQueue<Integer> buffer = new ArrayBlockingQueue<Integer>(3);
    public static CopyOnWriteArrayList<Integer> primes = new CopyOnWriteArrayList<Integer>();
    public static AtomicInteger countFinds = new AtomicInteger(0);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        int num = 100;
        TreeSet<Integer> resultArr = new TreeSet<Integer>();

        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Thread threadBuf = new Buffer(num);
        threadBuf.start();

        Future future = null;
        int arg;

        while (threadBuf.isAlive()) {
            arg = buffer.take();
            future = service.submit(new FindPrimes(arg));
        }

        assert future != null;
        future.get();
        service.shutdown();

        resultArr.addAll(primes);

        System.out.println("Found " + countFinds + " primes");
        System.out.println("Primes from 2 to " + num + ": " + resultArr.toString());

    }

    public static class FindPrimes implements Callable {

        private int num;

        public FindPrimes(int num) {
            this.num = num;
        }

        @Override
        public Object call() throws Exception{

            for (int i = 2; i < num; i++){

                if (num % i == 0) {
                    return null;
                }

            }

            primes.add(num);
            countFinds.getAndIncrement();

            return null;
        }

    }

    public static class Buffer extends Thread {

        private int num;

        public Buffer(int num) {
            this.num = num;
        }

        @Override
        public void run() {

            for (int i = 2; i <= num; i++) {

                try {
                    buffer.put(i);
                } catch (InterruptedException e) {
                    e.getStackTrace();
                }

            }
        }
    }
}
