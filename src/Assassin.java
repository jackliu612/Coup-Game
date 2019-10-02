public class Assassin extends Advisor {
    private boolean alive;
    public Assassin() {
        alive = true;
    }

    public String getName() {
        return "Assassin";
    }

    public boolean isAlive() {
        return alive;
    }
}
