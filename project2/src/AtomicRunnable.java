import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicRunnable implements Runnable {
    private final AtomicIntegerArray list;
    private final AtomicInteger size;
    private final AtomicInteger startedNumber;
    private final int threadNumber;

    public AtomicRunnable(
            AtomicIntegerArray list,
            AtomicInteger size,
            AtomicInteger startedNumber,
            int threadNumber
    ) {
        this.list = list;
        this.size = size;
        this.startedNumber = startedNumber;
        this.threadNumber = threadNumber;
    }

    @Override
    public void run() {
//        startedNumber.incrementAndGet();
//
//        while (startedNumber.get() < threadNumber);

        int sz = size.getAndIncrement();
        while (sz < 100) {
            list.addAndGet(sz, sz);
            sz = size.getAndIncrement();
        }
    }
}
