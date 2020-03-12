package design;

public class Brush {

    public static final String PURP = "\u001B[35m";
    public static final String RED = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RESET = "\u001B[0m";

    public static String purple(String a) {
        return PURP + a + RESET;
    }

    public static String yellow(String a) {
        return YELLOW + a + RESET;
    }

    public static String red(String a) {
        return RED + a + RESET;
    }

}
