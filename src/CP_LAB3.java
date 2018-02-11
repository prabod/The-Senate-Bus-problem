import java.util.concurrent.Semaphore;
import java.util.Random;

public class CP_LAB3 {
    static Semaphore mutex;
    static Semaphore bus;
    static Semaphore gate;
    static int waiting;
    static Random random;
    static long clock;
    long nextBus;
    long nextRider;
    float busLambda;
    float riderLambda;
    int riders = 0;
    int buses = 0;

    CP_LAB3(float busLambda, float riderLambda) {
        this.busLambda = busLambda;
        this.riderLambda = riderLambda;
        this.nextRider = 0L;
        this.nextBus = 0L;
        random = new Random();
        init();
    }


    /**
     * Bus class represents a BUS
     */
    private static class Bus extends Thread {
        int busNumber;
        int permitted;
        long leavingTime;

        Bus(int number) {
            this.busNumber = number;
        }

        @Override
        public void run() {
            try {
                System.out.println("Trying to acquire mutex by Bus " + this.busNumber);
                // acquire mutex to board passengers to the bus
                mutex.acquire();
                System.out.println("Mutex acquired by Bus " + this.busNumber);
                permitted = Integer.min(waiting, 50);   //set permitted number of people in the bus
                try {
                    bus.release(permitted);         //increment the semaphore by 50
                    gate.acquire(permitted);        //decrement the gate semaphore by 50
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    waiting = Integer.max(waiting - 50, 0);     //set the number of waiting
                    leavingTime = CP_LAB3.clock;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Releasing acquired mutex by Bus " + this.busNumber);
                mutex.release();        //release the mutex
                System.out.println("Mutex released by Bus " + this.busNumber);
                long hours = leavingTime / 3600;
                long minutes = (leavingTime % 3600) / 60;
                long seconds = leavingTime % 60;

                String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                System.out.println((char) 27 + "[32m" + "At T=" + timeString + " Bus " + this.busNumber + " Departed with " + permitted + ", " + " leaving " + waiting +
                        " passengers waiting" + (char) 27 + "[0m");
            }
        }
    }


    /**
     * Rider class represents a passenger
     */
    private static class Rider extends Thread {
        int riderNumber;

        Rider(int riderNumber) {
            this.riderNumber = riderNumber;
        }

        @Override
        public void run() {
            try {
                System.out.println("Trying to acquire mutex by Rider " + this.riderNumber);
                mutex.acquire();    //acquire the mutex to wait for the bus
                System.out.println("Mutex acquired by Rider " + this.riderNumber);
                waiting += 1;       //Increment waiting
                System.out.println((char) 27 + "[31m" + "Rider " + this.riderNumber + " waiting for a bus..." + (char) 27 + "[0m");
                mutex.release();    //release the mutex

                bus.acquire();      //try to board the bus
                System.out.println((char) 27 + "[33m" + "Rider " + this.riderNumber + " Boarded the Bus" + (char) 27 + "[0m");
                gate.release();     //release the gate
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * update the simulation clock
     *
     * @param clock
     */
    public void updateClock(long clock) {
        this.clock = clock;
        if (nextBus != 0L && nextRider != 0L) {
            if (this.clock >= nextBus) {
                synchronized (this) {
                    Bus bus = new Bus(buses);
                    buses++;
                    nextBus += nextTime(busLambda);
                    bus.start();
                }
            }
            if (this.clock >= nextRider) {
                synchronized (this) {
                    Rider rider = new Rider(riders);
                    riders++;
                    nextRider += nextTime(riderLambda);
                    rider.start();
                }
            }
        } else {
            nextBus = nextTime(busLambda);
            nextRider = nextTime(riderLambda);
        }

    }

    /**
     * given the arrival mean time return the next arrival time
     * @param arrivalMeanTime
     * @return next arrival time
     */
    public long nextTime(float arrivalMeanTime) {
        float lambda = 1 / arrivalMeanTime;
        return Math.round(-Math.log(1 - random.nextFloat()) / lambda);
    }

    /**
     * Initiate class
     */
    void init() {
        mutex = new Semaphore(1);
        bus = new Semaphore(0);
        gate = new Semaphore(0);
        waiting = 0;

    }
}
