public class Tick implements Runnable {
    float tickTosec;
    long time;
    long startTime;
    private CP_LAB3 listner;

    Tick(float ratio, CP_LAB3 listner) {
        this.tickTosec = ratio;
        this.time = 0L;
        this.listner = listner;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void run() {

        long time_passed = System.currentTimeMillis() - startTime;
        long delta_time;
        long time_per_timestep = (long) (1000 / tickTosec);
        while (true) { // keep running
            // update game logic once for every tick passed
            long now = System.currentTimeMillis();
            while (time_passed >= time_per_timestep) {
                time += 1;
                listner.updateClock(time);
                time_passed -= time_per_timestep;
            }
            // update timing
            delta_time = System.currentTimeMillis() - now;
            time_passed += delta_time;
        }
    }
}