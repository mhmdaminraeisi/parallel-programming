import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Philosopher implements Runnable {
    // Only an outline is presented here.  You have to fill in the bulk of the
    // code.
    private final int id;
    private final Table table;

    /** Current state, one of THINKING, LOOKING, ASKING, or TALKING */
    private int state = THINKING;

    /** State indicating that this philosopher is thinking (not interested in
     * talking to anyone.
     */
    private static final int THINKING = 1;

    /** State indicating that this philosopher is looking for someone to talk
     * to.
     */
    private static final int LOOKING = 2;

    /** State indicating that this philosopher has asked someone to talk and
     * is waiting for an answer.
     */
    private static final int ASKING = 3;

    /** State indicating that this philosopher has chosen a correspondent and is
     * currently engaged in a conversation.
     */
    private static final int TALKING = 4;

    /** State names for debugging messages */
    private static final String[] stateName =
        { "0", "THINKING", "LOOKING", "ASKING", "TALKING" };

    private final List<Integer> friends = new ArrayList<>();

    private final Object matchedFriendLock = new Object();
    private volatile Integer matchedFriend;
    private final List<Integer> callBackFriends = new ArrayList<>();

    /** Diagnostic output to indicate a state change */
    private void stateChange(String label, int newState) {
        if (Table.getDebugLevel() > 1) {
            Table.pl("In " + label
                + ", Philosopher " + id
                + ": changing state from " + stateName[state]
                + " to " + stateName[newState]);
        }
    }

    private boolean isFriend(int caller) {
        for (int friend : friends) {
            if (friend == caller) return true;
        }
        return false;
    }

    /** Constructs a new Philosopher object.
     * @param id the philosopher id of this Philosopher.
     * @param table a Table object used to coordinate all the philosophers.
     */
    public Philosopher(int id, Table table) {
        this.id = id;
        this.table = table;
    }

    public synchronized void setMatchedFriend(Integer caller) {
        synchronized (matchedFriendLock) {
            matchedFriend = caller;
        }
    }

    /** Called if the caller wants to talk to this philosopher.
     * The calling thread will be delayed if the this philosopher is
     * in ASKING state and has a larger id than the caller.  In all other
     * cases, return is immediate.
     * @param caller the id of the calling philosopher.
     * @return true if this philosopher is immediately willing to talk
     * to the caller, foresaking all others; false in otherwise.
     */
    public synchronized boolean canWeTalk(int caller) throws InterruptedException {
        // You must provide the appropriate code for this function.
        if (state == THINKING || state == TALKING) {
            return false;
        }
        if (!isFriend(caller)) {
            return false;
        }
        if (state == LOOKING) {
            setMatchedFriend(caller);
            return true;
        }

        if (id < caller) {
            while (state == ASKING) {
                wait();
            }

            if (state == LOOKING) {
                setMatchedFriend(caller);
                return true;
            }
            return false;
        }
        synchronized (callBackFriends) {
            if (!callBackFriends.contains(caller)) {
                callBackFriends.add(caller);
            }
        }
        return false;
    } // canWeTalk

    private synchronized void startAsking() {
        state = ASKING;
    }
    private synchronized void startLooking() {
        state = LOOKING;
        notify();
    }

    private synchronized void startTalking() {
        state = TALKING;
        notifyAll();
    }

    private void askingFriends(List<Integer> askFriends) throws InterruptedException {
        startAsking();
        for (int friend : askFriends) {
            Philosopher callee = table.getPhilosopher(friend);
            if (callee.canWeTalk(id)) {
                synchronized (matchedFriendLock) {
                    if (matchedFriend == null) matchedFriend = friend;
                }
                return;
            }
        }
    }

    /** Choose a friend to talk to.
     * This method should only be called when state==THINKING.
     * It causes the state to alternate between LOOKING and ASKING,
     * finally going to TALKING.
     * This method is not synchronized because it calls out to
     * the canWeTalk() methods of other philosophers, and during those calls,
     * this Philosopher should allow incoming calls to canWeTalk().
     * Instead, it uses synchronized helper methods to change and inspect local
     * state variables.
//     * @param friends the set of potential conversants.
     * @return the chosen friend.
     */
    private int choose() throws InterruptedException {
        // You must write the contents of this method as well as any
        // synchronized methods it calls.

        askingFriends(friends);
        if (matchedFriend != null) {
            return matchedFriend;
        }

        startLooking();

        while (matchedFriend == null) {
            Thread.onSpinWait();
            if (!callBackFriends.isEmpty()) {
                synchronized (callBackFriends) {
                    askingFriends(callBackFriends);
                    callBackFriends.clear();
                }
            }
        }
        return matchedFriend;
    }


    /** Set the local state to THINKING */
    private synchronized void startThinking() {
        stateChange("startThinking",THINKING);
        state = THINKING;
    }

    private void fillFriendsListFromArray(int[] frs) {
        friends.clear();
        for (int f : frs) {
            friends.add(f);
        }
    }

    private void resetStates() {
        setMatchedFriend(null);
        friends.clear();
        callBackFriends.clear();
        state = THINKING;
    }

    /** Main loop of a philosopher.
     * Alternate forever between thinking and talking.
     */
    public void run() {
        try {
            for (;;) {
                resetStates();

                fillFriendsListFromArray(table.think(id));

                int choice = choose();
                startTalking();
                table.talk(id, choice);

                startThinking();
            }
        }
        catch (TalkingException e) {
            System.err.println("Table complains:");
            e.printStackTrace();
            Table.abort();
            //System.exit(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
