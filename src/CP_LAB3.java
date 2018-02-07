import java.util.concurrent.Semaphore;

public class CP_LAB3 {
    Semaphore mutex;
    Semaphore bus;
    Semaphore gate;
    int waiting;

    private class Bus extends Thread {
        int busNumber;

        Bus(int number) {
            busNumber = number;
        }

        @Override
        public void run() {
            try {
                System.out.println("Trying to acquire mutex by Bus " + busNumber);
                mutex.acquire();
                System.out.println("Mutex acquired by Bus " + busNumber);
                int permitted = Integer.min(waiting, 50);
                try {
                    gate.release(permitted);
                    bus.acquire(permitted);
                } finally {

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Releasing acquired mutex by Bus " + busNumber);
                mutex.release();
                System.out.println("Mutex released by Bus " + busNumber);
            }
        }
    }
}
