public class Captain extends Advisor {
    private boolean alive;
    public Captain() {
        alive = true;
    }

    public String getName() {
        return "Captain";
    }

    public boolean isAlive() {
        return alive;
    }
}
