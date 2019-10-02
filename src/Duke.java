public class Duke extends Advisor {
    private boolean alive;
    public Duke() {
        alive = true;
    }

    public String getName() {
        return "Duke";
    }

    public boolean isAlive() {
        return alive;
    }
}
