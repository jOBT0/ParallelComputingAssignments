package ca.mcgill.ecse420.a1;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {
	
	public static void main(String[] args) {

		int numberOfPhilosophers = 5;
                Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
                ReentrantLock[] chopsticks = new ReentrantLock[numberOfPhilosophers];
                
                // Initialize chopstick objects
                for (int i = 0; i < numberOfPhilosophers; i++) {
                    chopsticks[i] = new ReentrantLock(true);
                }
                
                ExecutorService executorService = Executors.newFixedThreadPool(numberOfPhilosophers);

                for (int i = 0; i < numberOfPhilosophers; i++) {
                	ReentrantLock leftChopstick = chopsticks[i];
                	ReentrantLock rightChopstick = chopsticks[(i + 1) % numberOfPhilosophers];

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
        private final ReentrantLock leftChopstick;
        private final ReentrantLock rightChopstick;
        
        public Philosopher(int id, ReentrantLock leftChopstick, ReentrantLock rightChopstick) {
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

        private void pickUpChopsticks() throws InterruptedException { 	
        	while (true) {
                // Try to pick up both locks without getting stuck
                boolean leftAcquired = leftChopstick.tryLock();
                boolean rightAcquired = rightChopstick.tryLock();

                if (leftAcquired && rightAcquired) {
                    System.out.println("Philosopher " + id + " picked up both chopsticks.");
                    return;
                }

                // If only one was acquired, release it and retry after backoff
                if (leftAcquired) {
                    leftChopstick.unlock();
                }
                if (rightAcquired) {
                    rightChopstick.unlock();
                }

                Thread.sleep((int) (Math.random() * 20)); // small backoff to reduce contention
            }
        }

        private void eat() throws InterruptedException {
            System.out.println("Philosopher " + id + " is eating.");
            //Thread.sleep((int)(Math.random() * 100));
        }

        private void putDownChopsticks() {
        	leftChopstick.unlock();
            rightChopstick.unlock();
            System.out.println("Philosopher " + id + " put down chopsticks.");
        }
			

	}

}
