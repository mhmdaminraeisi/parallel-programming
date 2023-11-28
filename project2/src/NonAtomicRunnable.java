import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class NonAtomicRunnable implements Runnable {
    private final AtomicIntegerArray list;
    private final AtomicInteger startedNumber;
    private final int threadNumber;

    public NonAtomicRunnable(AtomicIntegerArray list, AtomicInteger startedNumber, int threadNumber) {
        this.list = list;
        this.startedNumber = startedNumber;
        this.threadNumber = threadNumber;
    }

    @Override
    public void run() {
//        startedNumber.incrementAndGet();
//
//        while (startedNumber.get() < threadNumber);
        while (SizeHolder.size < 100) {
            int sz = SizeHolder.size;
            if (sz < 100) {
                list.addAndGet(sz, sz);
                SizeHolder.size += 1;
            }
        }
    }
}
