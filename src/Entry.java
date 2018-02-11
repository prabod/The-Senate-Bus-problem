/**
 * Entry point for Lab 3
 */
public class Entry {
    public static void main(String[] args) {
        if (args.length > 0) {
            CP_LAB3 listener = new CP_LAB3(Long.parseLong(args[0]) * 60L, Long.parseLong(args[1]));
            Tick myRunnable = new Tick(Integer.parseInt(args[2]), listener);
            new Thread(myRunnable).start();
        }

    }
}
