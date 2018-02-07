import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.Random;

public class CP_LAB3 {
    static Semaphore mutex;
    static Semaphore bus;
    static Semaphore gate;
    static int waiting;
    static Random random;
    long clock;
    long nextBus;
    long nextRider;
    float busLambda;
    float riderLambda;
    ArrayList<Rider> riders = new ArrayList<>();
    ArrayList<Bus> buses = new ArrayList<>();

    CP_LAB3(float busLambda, float riderLambda) {
        this.busLambda = busLambda;
        this.riderLambda = riderLambda;
        this.nextRider = 0L;
        this.nextBus = 0L;
        random = new Random();
        System.out.println("init");
        init();
    }

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

    public void updateClock(long clock) {
        this.clock = clock;
        if (nextBus != 0L && nextRider != 0L) {
            if (this.clock >= nextBus) {
                synchronized (this) {
                    Bus bus = new Bus(buses.size());
                    buses.add(bus);
                    nextBus += nextTime(busLambda);
                    bus.start();
                }
            }
            if (this.clock >= nextRider) {
                synchronized (this) {
                    Rider rider = new Rider(riders.size());
                    riders.add(rider);
                    nextRider += nextTime(riderLambda);
                    rider.start();
                }
            }
//            System.out.println(nextBus + " "+ nextRider+ " "+ this.clock);
        } else {
//            System.out.println("bus"+nextTime(busLambda));
            nextBus = nextTime(busLambda);
            nextRider = nextTime(riderLambda);
            System.out.println(nextBus + " " + nextRider);
        }

    }

    public long nextTime(float arrivalMeanTime) {
        float lambda = 1 / arrivalMeanTime;
        return Math.round(-Math.log(1 - random.nextFloat()) / lambda);
    }

    void init() {
        mutex = new Semaphore(1);
        bus = new Semaphore(0);
        gate = new Semaphore(0);
        waiting = 0;
//        Rider[] riders = new Rider[100];
//        for (int i = 0; i < 100; i++) {
//            riders[i] = new Rider(i);
//            riders[i].start();
//        }
//        Bus nus = new Bus(1);
//        nus.start();

    }
}
