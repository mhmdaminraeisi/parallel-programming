package taojava.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A randomized implementation of sorted lists.  
 * 
 * @author Samuel A. Rebelsky
 * @author Your Name Here
 */
public class SkipList<T extends Comparable<T>>
    implements SortedList<T>
{
  // +--------+----------------------------------------------------------
  // | Fields |
  // +--------+
  private final int height = 5;
  private int size = 0;
  private final ArrayList<Node> startNodes = new ArrayList<>();
//  private

  // +------------------+------------------------------------------------
  // | Internal Classes |
  // +------------------+

  /**
   * Nodes for skip lists.
   */

  public class Node
  {
    // +--------+--------------------------------------------------------
    // | Fields |
    // +--------+

    /**
     * The value stored in the node.
     */
    private final T val;
    private final int level;
    private final boolean isLeft;
    private final boolean isRight;

    @Setter
    private Node right;
    @Setter
    private Node left;
    @Setter
    private Node bottom;

    public Node(T val, int level, boolean isLeft, boolean isRight, Node left, Node right, Node bottom) {
      this.val = val;
      this.level = level;
      this.isLeft = isLeft;
      this.isRight = isRight;
      this.left = left;
      this.right = right;
      this.bottom = bottom;
    }
  } // class Node

  // +--------------+----------------------------------------------------
  // | Constructors |
  // +--------------+

  public SkipList() {
    Node prevRight = null;
    Node prevLeft = null;
    for (int i = 0; i < height; i++) {
      Node right = new Node(null, i, false, true, null, null, prevRight);
      Node left = new Node(null, i, true, false, null, right, prevLeft);
      right.left = left;
      startNodes.add(left);
      prevRight = right;
      prevLeft = left;
    }
  }

  public void print() {
    int l = height - 1;
    while (l >= 0) {
      Node currNode = startNodes.get(l);
      System.out.println("level = " + l);
      while (currNode != null) {
        System.out.print(currNode.val + " ");
        currNode = currNode.right;
      }
      System.out.println();
      System.out.println();
      l --;
    }
  }

  // +-------------------------+-----------------------------------------
  // | Internal Helper Methods |
  // +-------------------------+

  // +-----------------------+-------------------------------------------
  // | Methods from Iterable |
  // +-----------------------+

  /**
   * Return a read-only iterator (one that does not implement the remove
   * method) that iterates the values of the list from smallest to
   * largest.
   */
  public Iterator<T> iterator()
  {

    return new Iterator<T>()
    {
      // An underlying iterator.
      private Node currNode = startNodes.get(0).right;

      public T next()
      {
        if (!hasNext()) {
          currNode = null;
          return null;
        }
        currNode = currNode.right;
        return currNode.val;
      } // next()

      public boolean hasNext()
      {
        if (currNode.isRight) return false;
        if (currNode.right.isRight) return false;
        return true;
      } // hasNext()

      public void remove()
      {
        if (currNode != null && currNode.val != null) {
          SkipList.this.remove(currNode.val);
        }
      } // remove()
    }; // new Iterator<T>
  } // iterator()

  // +------------------------+------------------------------------------
  // | Methods from SimpleSet |
  // +------------------------+

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
    Node currNode = startNodes.get(level);
    while (currNode != null) {
      currNode = getLastSmallerOrEqualNodeInLevel(currNode, val);
      Node newNode = new Node(val, currNode.level, false, false, currNode, currNode.right, null);
      currNode.right = newNode;
      if (prevNewNode != null) {
        prevNewNode.bottom = newNode;
      }
      prevNewNode = newNode;

      currNode = currNode.bottom;
    }

    size ++;
    // STUB
  } // add(T val)

  /**
   * Determine if the set contains a particular value.
   */
  public boolean contains(T val)
  {
    Node node = findFirstOccurrence(val);
    return node != null;
  } // contains(T)

  /**
   * Remove an element from the set.
   *
   * @post !contains(val)
   * @post For all lav != val, if contains(lav) held before the call
   *   to remove, contains(lav) continues to hold.
   */
  public void remove(T val)
  {
    Node currNode = findFirstOccurrence(val);
    if (currNode == null) return;
    while (currNode != null) {
      Node left = currNode.left;
      Node right = currNode.right;
      left.right = right;
      right.left = left;
      currNode = currNode.bottom;
    }
    // STUB
    size --;
  } // remove(T)

  // +--------------------------+----------------------------------------
  // | Methods from SemiIndexed |
  // +--------------------------+

  /**
   * Get the element at index i.
   *
   * @throws IndexOutOfBoundsException
   *   if the index is out of range (index < 0 || index >= length)
   */
  public T get(int i)
  {
    if (i < 0 || i >= size) {
      throw new IndexOutOfBoundsException();
    }
    Node currNode = startNodes.get(0);
    while (i >= 0) {
      currNode = currNode.right;
      i --;
    }

    return currNode.val;
  } // get(int)

  /**
   * Determine the number of elements in the collection.
   */
  public int length()
  {
    return size;
  } // length()

  private Node findFirstOccurrence(T val) {
    Node currNode = startNodes.get(height - 1);
    while (currNode != null) {
      currNode = getLastSmallerOrEqualNodeInLevel(currNode, val);
      if (isEqualVal(currNode, val)) {
        return currNode;
      }
      currNode = currNode.bottom;
    }
    return null;
  }

  private boolean isEqualVal(Node node, T val) {
    return node.val != null && node.val.compareTo(val) == 0;
  }

  private Node getLastSmallerOrEqualNodeInLevel(Node currNode, T val) {
    while (!isGreaterNode(currNode.right, val)) {
      currNode = currNode.right;
    }
    return currNode;
  }

  private boolean isGreaterNode(Node node, T val) {
    if (node.isRight) return true;
    if (node.right == null) return true;
    return node.val.compareTo(val) > 0;
  }

  private int getRandomMaxLevel() {
    int currLevel = height - 1;
    while (currLevel > 0) {
      if (Math.random() < Math.pow(0.5, currLevel)) {
        return currLevel;
      }
      currLevel --;
    }
    return currLevel;
  }

} // class SkipList<T>
