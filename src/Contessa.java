public class Contessa extends Advisor {
    private boolean alive;
    public Contessa() {
        alive = true;
    }

    public String getName() {
        return "Contessa";
    }

    public boolean isAlive() {
        return alive;
    }
}
