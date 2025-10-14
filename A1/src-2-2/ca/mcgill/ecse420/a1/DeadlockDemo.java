package ca.mcgill.ecse420.a1;

import ca.mcgill.ecse420.a1.DeadlockDemo.Friend;

public class DeadlockDemo {
	static class Friend {
        private final String name;

        Friend(String name) {
            this.name = name;
        }

        synchronized void bow(Friend bower) {
        	Friend first = this.name.compareTo(bower.name) < 0 ? this : bower;
            Friend second = this == first ? bower : this;

            synchronized (first) {
                synchronized (second) {
                    System.out.println(this.name + " bows to " + bower.name);
                    bower.bowBack(this);
                }
            }
        }

        synchronized void bowBack(Friend bower) {
            System.out.println(this.name + " bows back to " + bower.name);
        }
    }

    public static void main(String[] args) {
        Friend alice = new Friend("Alice");
        Friend bob = new Friend("Bob");

        // Thread 1: Alice tries to bow to Bob
        new Thread(() -> alice.bow(bob)).start();

        // Thread 2: Bob tries to bow to Alice
        new Thread(() -> bob.bow(alice)).start();
    }


}
