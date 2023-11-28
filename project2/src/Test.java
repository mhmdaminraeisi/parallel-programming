import java.util.concurrent.atomic.AtomicIntegerArray;

public class Test {
    public static void main(String[] args) {
        AtomicIntegerArray list = new AtomicIntegerArray(200);
        System.out.println(SizeHolder.size);
        SizeHolder.size += 1;
        System.out.println(SizeHolder.size);
    }
}
