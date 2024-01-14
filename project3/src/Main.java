import taojava.util.SkipList;

public class Main {
    public static void main(String[] args) {
        SkipList<Integer> skipList = new SkipList();
        skipList.add(3);
        skipList.add(5);
        skipList.add(1);
        skipList.remove(5);
        System.out.println(skipList.get(1));

//        skipList.remove(1);
    }
}
