import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ArrayList<Integer> threadNumbers = new ArrayList<>(Arrays.asList(1, 10, 50, 100));

        for (Integer threadNumber : threadNumbers) {
            System.out.println("Results non atomic run for thread number = " + threadNumber);
            for (int i = 0; i < 10; i++) {
                ArrayList<Integer> list = runNonAtomic(threadNumber);
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

        compareRunningTimes();
    }

    private static void compareRunningTimes() throws InterruptedException {
        Date date = new Date();
        System.out.print("Running for non atomic: ");
        for (int i = 0; i < 1000; i++) {
            if (i % 50 == 1) System.out.print(i + " ");
            runNonAtomic(10);
        }
        System.out.println();
        Date newDate = new Date();
        long seconds = (newDate.getTime() - date.getTime()) / 1000;
        System.out.println("Total time = " + seconds);
        System.out.println();

        date = new Date();
        System.out.print("Running for atomic: ");
        for (int i = 0; i < 1000; i++) {
            if (i % 50 == 1) System.out.print(i + " ");
            runAtomic(10);
        }
        System.out.println();
        newDate = new Date();
        seconds = (newDate.getTime() - date.getTime()) / 1000;
        System.out.println("Total time = " + seconds);
        System.out.println();
    }

    private static AtomicIntegerArray runAtomic(int threadNumber) throws InterruptedException {
        AtomicIntegerArray list = new AtomicIntegerArray(100);
        ArrayList<Thread> threads = new ArrayList<>();
        AtomicInteger startedNumber = new AtomicInteger(0);
        AtomicInteger size = new AtomicInteger(0);

        for (int i = 0; i < threadNumber; i++) {
            AtomicRunnable runnable = new AtomicRunnable(list, size, startedNumber, threadNumber);
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

    private static ArrayList<Integer> runNonAtomic(int threadNumber) throws InterruptedException {
        ArrayList<Integer> list = new ArrayList<>(100);
        ArrayList<Thread> threads = new ArrayList<>();
        AtomicInteger startedNumber = new AtomicInteger(0);

        for (int i = 0; i < threadNumber; i++) {
            NonAtomicRunnable runnable = new NonAtomicRunnable(list, startedNumber, threadNumber);
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
