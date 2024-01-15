import taojava.util.SkipList;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        SkipList<Integer> skipList = new SkipList();
//        skipList.add(3);
//        skipList.add(5);
//        skipList.add(8);
//        skipList.add(1);
//        skipList.add(7);
//        skipList.add(20);
        int[] counter = new int[100];
        Random random = new Random();
        int khar = 0;
        for (int i = 0; i < 10; i++) {
            int rnd = random.nextInt(100);
            if (i == 5) khar = rnd;
            skipList.add(rnd);
        }
        skipList.print();
        System.out.println("#############################");
        System.out.println("khar = " + khar);
//        skipList.remove(khar);
        skipList.print();

        System.out.println(skipList.get(5));
        System.out.println(skipList.get(0));
        System.out.println(skipList.get(7));
    }
}
