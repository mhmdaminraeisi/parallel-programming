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

        for (int i = 0; i < 1024; i++) {
            skipList.add(i);
        }
//        skipList.print();
    }
}
