import java.util.concurrent.Semaphore;

public class CP_LAB3 {
    static Semaphore mutex;
    static Semaphore bus;
    static Semaphore gate;
    static int waiting;

    private static class Bus extends Thread {
        int busNumber;

        Bus(int number) {
            this.busNumber = number;
        }

        @Override
        public void run() {
            try {
                System.out.println("Trying to acquire mutex by Bus " + this.busNumber);
                mutex.acquire();
                System.out.println("Mutex acquired by Bus " + this.busNumber);
                int permitted = Integer.min(waiting, 50);
                try {
                    bus.release(permitted);
                    gate.acquire(permitted);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    waiting = Integer.max(waiting - 50, 0);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Releasing acquired mutex by Bus " + this.busNumber);
                mutex.release();
                System.out.println("Mutex released by Bus " + this.busNumber);
                System.out.println((char) 27 + "[32m" + "Bus " + this.busNumber + " Departed" + (char) 27 + "[0m");
            }
        }
    }

    private static class Rider extends Thread {
        int riderNumber;

        Rider(int riderNumber) {
            this.riderNumber = riderNumber;
        }

        @Override
        public void run() {
            try {
                System.out.println("Trying to acquire mutex by Rider " + this.riderNumber);
                System.out.println("Mutex acquired by Rider " + this.riderNumber);
                waiting += 1;
                System.out.println((char) 27 + "[31m" + "Rider " + this.riderNumber + " waiting for a bus..." + (char) 27 + "[0m");
                mutex.release();

                bus.acquire();
                System.out.println((char) 27 + "[33m" + "Rider " + this.riderNumber + " Boarded the Bus" + (char) 27 + "[0m");
                gate.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        mutex = new Semaphore(1);
        bus = new Semaphore(0);
        gate = new Semaphore(0);
        waiting = 0;
        Rider[] riders = new Rider[100];
        for (int i = 0; i < 100; i++) {
            riders[i] = new Rider(i);
            riders[i].start();
        }
        Bus nus = new Bus(1);
        nus.start();

    }
}
