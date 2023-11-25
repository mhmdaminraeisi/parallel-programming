import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class NonAtomicRunnable implements Runnable {
    private final ArrayList<Integer> list;
    private final AtomicInteger startedNumber;
    private final int threadNumber;

    public NonAtomicRunnable(ArrayList<Integer> list, AtomicInteger startedNumber, int threadNumber) {
        this.list = list;
        this.startedNumber = startedNumber;
        this.threadNumber = threadNumber;
    }

    @Override
    public void run() {
        startedNumber.incrementAndGet();

        while (startedNumber.get() < threadNumber);

        int size = list.size();
        while (size < 100) {
            list.add(size);
            size = list.size();
        }
    }
}
