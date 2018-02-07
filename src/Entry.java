public class Entry {
    public static void main(String[] args) {
        CP_LAB3 listener = new CP_LAB3(30 * 60L, 30L);
        Tick myRunnable = new Tick(100, listener);

        new Thread(myRunnable).start();
    }
}
