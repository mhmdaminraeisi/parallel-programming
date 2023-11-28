import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ArrayList<Integer> threadNumbers = new ArrayList<>(Arrays.asList(1, 10, 50, 100));

        for (Integer threadNumber : threadNumbers) {
            System.out.println("Results non atomic run for thread number = " + threadNumber);
            for (int i = 0; i < 10; i++) {
                AtomicIntegerArray list = runNonAtomic(threadNumber);
                System.out.println(list);
            }
            System.out.println();
        }

        for (Integer threadNumber : threadNumbers) {
            System.out.println("Results atomic run for thread number = " + threadNumber);
            for (int i = 0; i < 10; i++) {
                AtomicIntegerArray list = runAtomic(threadNumber);
                System.out.println(list);
            }
            System.out.println();
        }

        for (Integer threadNumber : threadNumbers) {
            System.out.println();
            System.out.println("Compare for thread number " + threadNumber);
            compareRunningTimes(threadNumber);
            System.out.println();
        }
    }

    private static void compareRunningTimes(int threadNumber) throws InterruptedException {
        Date date = new Date();
        System.out.print("Running for non atomic: ");
        for (int i = 0; i < 10000; i++) {
//            if (i % 50 == 1) System.out.print(i + " ");
            runNonAtomic(threadNumber);
        }
        System.out.println();
        Date newDate = new Date();
        long milliSeconds = (newDate.getTime() - date.getTime());
        System.out.println("Total time = " + milliSeconds);
        System.out.println();

        date = new Date();
        System.out.print("Running for atomic: ");
        for (int i = 0; i < 1000; i++) {
//            if (i % 50 == 1) System.out.print(i + " ");
            runAtomic(threadNumber);
        }
        System.out.println();
        newDate = new Date();
        milliSeconds = (newDate.getTime() - date.getTime());
        System.out.println("Total time = " + milliSeconds);
        System.out.println();
    }

    private static AtomicIntegerArray runAtomic(int threadNumber) throws InterruptedException {
        SizeHolder.atomicSize.set(0);
        AtomicIntegerArray list = new AtomicIntegerArray(1000);
        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadNumber; i++) {
            AtomicRunnable runnable = new AtomicRunnable(list);
            threads.add(new Thread(runnable));
        }
        for (int i = 0; i < threadNumber; i++) {
            threads.get(i).start();
        }

        for (int i = 0; i < threadNumber; i++) {
            threads.get(i).join();
        }

        return list;
    }

    private static AtomicIntegerArray runNonAtomic(int threadNumber) throws InterruptedException {
        SizeHolder.size = 0;
        AtomicIntegerArray list = new AtomicIntegerArray(1000);
        ArrayList<Thread> threads = new ArrayList<>();
        AtomicInteger startedNumber = new AtomicInteger(0);

        for (int i = 0; i < threadNumber; i++) {
            NonAtomicRunnable runnable = new NonAtomicRunnable(list);
            threads.add(new Thread(runnable));
        }
        for (int i = 0; i < threadNumber; i++) {
            threads.get(i).start();
        }

        for (int i = 0; i < threadNumber; i++) {
            threads.get(i).join();
        }

//        System.out.println(list);

        return list;
    }
}
