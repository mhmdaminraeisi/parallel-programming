import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {
    public static void main(String[] args) {
        Date date = new Date();
        Integer nonAtomic = 0;
        for (int i = 0; i < 10000000; i++) {
            nonAtomic += 1;
        }
        Date newDate = new Date();
        long milliSeconds = (newDate.getTime() - date.getTime());
        System.out.println("Integer Non atomic = " + milliSeconds);

        Date date2 = new Date();
        int nonAtomicInt = 0;
        for (int i = 0; i < 10000000; i++) {
            nonAtomicInt += 1;
        }
        Date newDate2 = new Date();
        long milliSeconds2 = (newDate2.getTime() - date2.getTime());
        System.out.println("int non atomic = " + milliSeconds2);


        Date date3 = new Date();
        AtomicInteger atomicInteger =  new AtomicInteger(0);
        for (int i = 0; i < 10000000; i++) {
            atomicInteger.incrementAndGet();
        }
        Date newDate3 = new Date();
        long milliSeconds3 = (newDate3.getTime() - date3.getTime());
        System.out.println("atomic = " + milliSeconds3);

    }
}
