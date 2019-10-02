import java.util.Arrays;

public class Player {
    private String name;
    /*
        holds number of each role in alphabetical order
        0 -- Ambassador
        1 -- Assassin
        2 -- Captain
        3 -- Contessa
        4 -- Duke
     */
    private int[] hand = new int[5];
    private int tokens = 2;
    private boolean alive = true;

    Player(String n) {
        name = n;
    }

    //Various getters
    String getName() {
        return name;
    }

    int getTokens() {
        return tokens;
    }

    int[] getHand() {
        return hand;
    }

    boolean isAlive() {
        return alive;
    }

    private boolean hasAmbassador() {
        return hand[0] != 0;
    }

    private boolean hasAssassin() {
        return hand[1] != 0;
    }

    private boolean hasCaptain() {
        return hand[2] != 0;
    }

    private boolean hasContessa() {
        return hand[3] != 0;
    }

    private boolean hasDuke() {
        return hand[4] != 0;
    }

    //Setters for tokens
    void addTokens(int num) {
        tokens += num;
    }

    void loseTokens(int num) {
        tokens -= num;
    }

    //Setter for advisors
    int loseAdvisor(int index) {
        hand[index]--;
        boolean temp = false;
        for (int i : hand) {
            if (i != 0) {
                temp = true;
                break;
            }
        }
        alive = temp;
        return index;
    }

    void drawAdvisor(int index) {
        hand[index]++;
    }

    //Game Actions
    //Only initializes the action. Resolution happens after all challenges are resolved.
    //Actions that can be challenged returns a boolean representing if it was legal or a bluff

    void takeIncome() {
        this.addTokens(1);
    }

    Player coup(Player target) {
        loseTokens(7);
        return target;
    }

    boolean takeTaxes() {
        return this.hasDuke();
    }

    boolean assassinate() {
        return this.hasAssassin();
    }

    boolean blockAssassination() {
        return this.hasContessa();
    }

    boolean steal() {
        return this.hasCaptain();
    }

    boolean blockSteal() {
        return this.hasCaptain() || this.hasAmbassador();
    }

    boolean swapAdvisors() {
        return this.hasAmbassador();
    }

    boolean blockForeignAid() {
        return hasDuke();
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", hand=" + Arrays.toString(hand) +
                ", tokens=" + tokens +
                ", alive=" + alive +
                '}';
    }
}
