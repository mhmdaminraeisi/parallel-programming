import java.util.concurrent.atomic.AtomicIntegerArray;

public class NonAtomicRunnable implements Runnable {
    private final AtomicIntegerArray list;

    public NonAtomicRunnable(AtomicIntegerArray list) {
        this.list = list;
    }

    @Override
    public void run() {
        int sz = SizeHolder.size;
        while (sz < 100) {
            list.addAndGet(sz + 1, sz);
            list.addAndGet(0, 1);
            sz = SizeHolder.size ++;
        }
    }
}
