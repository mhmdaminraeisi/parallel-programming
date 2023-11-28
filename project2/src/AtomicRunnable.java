import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicRunnable implements Runnable {
    private final AtomicIntegerArray list;

    public AtomicRunnable(AtomicIntegerArray list) {
        this.list = list;
    }

    @Override
    public void run() {
        int sz = SizeHolder.atomicSize.getAndIncrement();
        while (sz < 100) {
            list.addAndGet(sz, sz);
            list.addAndGet(0, 1);
            sz = SizeHolder.atomicSize.getAndIncrement();
        }
    }
}
