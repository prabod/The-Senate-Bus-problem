import java.util.Random;
import java.util.concurrent.Semaphore;

public class CP_LAB3 {

	static Semaphore mutex;

	static Semaphore bus;

	static Semaphore gate;

	static int waiting;

	static Random random;

	static final int RIDER_ARRIVAL_MEAN_TIME = 30;   // 30/1000 milliseconds

	static final int BUS_ARRIVAL_MEAN_TIME = 20 * 60;    // 20*60/1000 milliseconds

	private static class Bus extends Thread {

		int busNumber;
		long arrivalTime;

		Bus(int number) {
			this.busNumber = number;
		}

		public void setArrivalTime(long arrivalTime) {
			this.arrivalTime = arrivalTime;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(arrivalTime);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				System.out.println("Trying to acquire mutex by Bus " + this.busNumber);
				mutex.acquire();
				System.out.println("Mutex acquired by Bus " + this.busNumber);
				int permitted = Integer.min(waiting, 50);
				try {
					bus.release(permitted);
					gate.acquire(permitted);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				finally {
					waiting = Integer.max(waiting - 50, 0);
				}
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally {
				System.out.println("Releasing acquired mutex by Bus " + this.busNumber);
				mutex.release();
				System.out.println("Mutex released by Bus " + this.busNumber);
				System.out.println((char) 27 + "[32m" + "Bus " + this.busNumber + " Departed" + (char) 27 + "[0m");
			}
		}
	}

	private static class Rider extends Thread {

		int riderNumber;
		long arrivalTime;

		Rider(int riderNumber) {
			this.riderNumber = riderNumber;
		}

		public void setArrivalTime(long arrivalTime) {
			this.arrivalTime = arrivalTime;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(arrivalTime);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				System.out.println("Trying to acquire mutex by Rider " + this.riderNumber);
				System.out.println("Mutex acquired by Rider " + this.riderNumber);
				waiting += 1;
				System.out.println(
						(char) 27 + "[31m" + "Rider " + this.riderNumber + " waiting for a bus..." + (char) 27 + "[0m");
				mutex.release();

				bus.acquire();
				System.out
						.println((char) 27 + "[33m" + "Rider " + this.riderNumber + " Boarded the Bus" + (char) 27 + "[0m");
				gate.release();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		mutex = new Semaphore(1);
		bus = new Semaphore(0);
		gate = new Semaphore(0);
		waiting = 0;
		random = new Random();

		int riderCount = 100;
		int busCount = 5;

		Rider[] riders = new Rider[busCount];
		for (int i = 0; i < busCount; i++) {
			riders[i] = new Rider(i);
			long riderArrivalTime = Math.round(-Math.log(1 - random.nextFloat() * RIDER_ARRIVAL_MEAN_TIME));
			riders[i].setArrivalTime(riderArrivalTime);
			riders[i].start();
		}

		Bus bus[] = new Bus[busCount];
		for (int i = 0; i < busCount; i++) {
			bus[i] = new Bus(i);
			long busArrivalTime = Math.round(-Math.log(1 - random.nextFloat() * BUS_ARRIVAL_MEAN_TIME));
			bus[i].setArrivalTime(busArrivalTime);
			bus[i].start();
		}

	}
}
