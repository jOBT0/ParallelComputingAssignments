package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {
	
	public static void main(String[] args) {

		int numberOfPhilosophers = 5;
                Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
                Object[] chopsticks = new Object[numberOfPhilosophers];
                
                // Initialize chopstick objects
                for (int i = 0; i < numberOfPhilosophers; i++) {
                    chopsticks[i] = new Object();
                }
                
                ExecutorService executorService = Executors.newFixedThreadPool(numberOfPhilosophers);

                for (int i = 0; i < numberOfPhilosophers; i++) {
                    Object leftChopstick = chopsticks[i];
                    Object rightChopstick = chopsticks[(i + 1) % numberOfPhilosophers];

                    // For philosopher 0, reverse the chopsticks
                    if (i == 0) {
                        philosophers[i] = new Philosopher(i, rightChopstick, leftChopstick); // reversed
                    } else {
                        philosophers[i] = new Philosopher(i, leftChopstick, rightChopstick);
                    }
                    executorService.execute(philosophers[i]);
                }

                executorService.shutdown();
	}
	
	

	public static class Philosopher implements Runnable {

		private final int id;
        private final Object leftChopstick;
        private final Object rightChopstick;
        
        public Philosopher(int id, Object leftChopstick, Object rightChopstick) {
            this.id = id;
            this.leftChopstick = leftChopstick;
            this.rightChopstick = rightChopstick;
        }

		@Override
		public void run() {
			
            try {
                while (true) {
                    think();
                    pickUpChopsticks();
                    eat();
                    putDownChopsticks();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        private void think() throws InterruptedException {
            System.out.println("Philosopher " + id + " is thinking.");
            //Thread.sleep((int)(Math.random() * 100));
        }

        private void pickUpChopsticks() {
            synchronized (leftChopstick) {
                System.out.println("Philosopher " + id + " picked up left chopstick.");
                synchronized (rightChopstick) {
                    System.out.println("Philosopher " + id + " picked up right chopstick.");
                }
            }
        }

        private void eat() throws InterruptedException {
            System.out.println("Philosopher " + id + " is eating.");
            //Thread.sleep((int)(Math.random() * 100));
        }

        private void putDownChopsticks() {
            System.out.println("Philosopher " + id + " put down chopsticks.");
        }
			

	}

}
