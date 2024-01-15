package taojava.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;

/**
 * A randomized implementation of sorted lists.  
 * 
 * @author Samuel A. Rebelsky
 * @author Your Name Here
 */
public class SkipList<T extends Comparable<T>>
    implements SortedList<T>
{
  private Random random = new Random();
  private final int height = 30;
  private HashMap<T, Node> valToNode = new HashMap<>();
  private final ArrayList<Node> startNodes = new ArrayList<>();

  /**
   * Nodes for skip lists.
   */

  @RequiredArgsConstructor
  public class Node
  {
    /**
     * The value stored in the node.
     */
    private final T val;
    private final int level;

    @Setter
    private Node right;
    @Setter
    private Node left;
    @Setter
    private Node bottom;
    @Setter
    private Node top;

    @Setter
    private int skipNodes = 0;
  }

  public SkipList() {
    Node prevRight = null;
    Node prevLeft = null;
    for (int i = 0; i < height; i++) {
      Node right = new Node(null, i);
      Node left = new Node(null, i);
      setLeftRightPointers(left, right);
      setBottomTopPointers(prevRight, right);
      setBottomTopPointers(prevLeft, left);

      startNodes.add(left);
      prevRight = right;
      prevLeft = left;
    }
  }

  public void print() {
    int l = height - 1;
    while (l >= 0) {
      Node currNode = startNodes.get(l);
      if (currNode.right.right == null) {
        l --;
        continue;
      }
      System.out.println("level = " + l);
      while (currNode != null) {
        System.out.print(currNode.val + ":" + currNode.skipNodes + "     ");
        currNode = currNode.right;
      }
      System.out.println();
      System.out.println();
      l --;
    }
  }

  /**
   * Return a read-only iterator (one that does not implement the remove
   * method) that iterates the values of the list from smallest to
   * largest.
   */
  public Iterator<T> iterator()
  {

    return new Iterator<T>()
    {
      private Node currNode = startNodes.get(0).right;

      public T next()
      {
        T val = currNode.val;
        currNode = currNode.right;
        return val;
      }

      public boolean hasNext()
      {
        return currNode != null && currNode.right != null;
      }

      public void remove()
      {
        if (currNode != null && currNode.val != null) {
          SkipList.this.remove(currNode.val);
        }
      }
    };
  }

  /**
   * Add a value to the set.
   *
   * @post contains(val)
   * @post For all lav != val, if contains(lav) held before the call
   *   to add, contains(lav) continues to hold.
   */

  public void add(T val)
  {
    if (contains(val)) return;

    int level = getRandomMaxLevel();

    Node prevNewNode = null;
    Node currNode = startNodes.get(height - 1);

    int index = getIndex(val);
    int passNodes = 0;

    while (currNode != null) {
      while (!isGreaterNode(currNode.right, val)) {
        if (currNode.left != null) passNodes += 1;
        currNode = currNode.right;
        passNodes += currNode.skipNodes;
      }

      if (currNode.level <= level) {
        Node newNode = new Node(val, currNode.level);
        newNode.setSkipNodes(index - passNodes - (currNode.left == null ? 0 : 1));
        currNode.right.setSkipNodes(currNode.right.skipNodes - newNode.skipNodes);

        setLeftRightPointers(newNode, currNode.right);
        setLeftRightPointers(currNode, newNode);
        setBottomTopPointers(newNode, prevNewNode);

        prevNewNode = newNode;
      } else {
        currNode.right.setSkipNodes(currNode.right.skipNodes + 1);
      }

      currNode = currNode.bottom;
    }

    valToNode.put(val, prevNewNode);
  }

  public int getIndex(T val) {
    Node currNode = startNodes.get(height - 1);
    int index = 0;

    while (currNode != null) {
      while (!isGreaterNode(currNode.right, val)) {
        if (currNode.left != null) index += 1;
        currNode = currNode.right;
        index += currNode.skipNodes;
      }
      if (currNode.bottom == null && currNode.left != null) index += 1;
      currNode = currNode.bottom;
    }
    return index;
  }
  /**
   * Determine if the set contains a particular value.
   */
  public boolean contains(T val)
  {
    return valToNode.containsKey(val);
  }

  /**
   * Remove an element from the set.
   *
   * @post !contains(val)
   * @post For all lav != val, if contains(lav) held before the call
   *   to remove, contains(lav) continues to hold.
   */
  public void remove(T val)
  {
    if (!valToNode.containsKey(val)) return;
    int nodeMaxLevel = 0;
    Node currNode = valToNode.get(val);
    while (currNode != null) {
      nodeMaxLevel = currNode.level;
      Node left = currNode.left;
      Node right = currNode.right;
      setLeftRightPointers(left, right);
      right.setSkipNodes(right.skipNodes + currNode.skipNodes);
      currNode = currNode.top;
    }
    updateHigherLevelsSkipNodesAfterRemove(nodeMaxLevel, val);
    valToNode.remove(val);
  }

  private void updateHigherLevelsSkipNodesAfterRemove(int maxNodeLevel, T val) {
    Node currNode = startNodes.get(height - 1);
    while (currNode.level > maxNodeLevel) {
      while (!isGreaterNode(currNode.right, val)) {
        currNode = currNode.right;
      }
      currNode.right.setSkipNodes(currNode.right.skipNodes - 1);
      currNode = currNode.bottom;
    }
  }
  /**
   * Get the element at index i.
   *
   * @throws IndexOutOfBoundsException
   *   if the index is out of range (index < 0 || index >= length)
   */
  public T get(int i)
  {
    if (i < 0 || i >= length()) {
      throw new IndexOutOfBoundsException();
    }
    Node currNode = startNodes.get(height - 1);
    int passNodes = 0;

    while (currNode != null) {
      while (passNodes < i || currNode.left == null) {
        if (currNode.left != null) passNodes += 1;
        currNode = currNode.right;
        passNodes += currNode.skipNodes;
      }
      if (passNodes == i) return currNode.val;

      passNodes -= currNode.skipNodes;
      currNode = currNode.left;
      if (currNode.left != null) passNodes --;

      currNode = currNode.bottom;
    }

    return null;
  }

  /**
   * Determine the number of elements in the collection.
   */
  public int length()
  {
    return valToNode.size();
  }

  private void setLeftRightPointers(Node left, Node right) {
    if (left != null) left.setRight(right);
    if (right != null) right.setLeft(left);
  }

  private void setBottomTopPointers(Node bottom, Node top) {
    if (bottom != null) bottom.setTop(top);
    if (top != null) top.setBottom(bottom);
  }

  private boolean isGreaterNode(Node node, T val) {
    if (node.right == null) return true;
    return node.val.compareTo(val) > 0;
  }

  private int getRandomMaxLevel() {
    int level = 0;
    while (level < height - 1) {
      double rnd = random.nextDouble();
      if (rnd < 0.5) level ++;
      else break;
    }
    return level;
  }
}
