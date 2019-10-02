import java.util.*;

class CoupGame {
    private Queue<Integer> deck = new LinkedList<>();
    private ArrayList<Integer> revealed = new ArrayList<>();
    private ArrayList<Player> players = new ArrayList<>();
    private Scanner input;

    CoupGame(ArrayList<String> names) {
        this.createDeck();
        for (int i = 0; i < names.size(); i++) {
            players.add(i, new Player(names.get(i)));
            players.get(i).drawAdvisor(deck.remove());
            players.get(i).drawAdvisor(deck.remove());
        }
    }

    private void createDeck() {
        for (int role = 0; role < 5; role++) {
            //_0_ 1 2 _3_ 4 5 _6_ 7 8 _9_ 10 11 _12_ 13 14
            for (int i = 0; i < 3; i++) {
                deck.add(role);
            }
        }
        shuffleDeck();
    }

    //Fisher–Yates shuffle
    private void shuffleDeck() {
        Random rand = new Random();
        int[] deckarr = new int[deck.size()];
        for (int i = 0; i < deckarr.length; i++) {
            deckarr[i] = deck.remove();
        }
        for (int i = deckarr.length - 1; i > 0; i--) {
            int switchIndex = rand.nextInt(i + 1);
            int temp = deckarr[i];
            deckarr[i] = deckarr[switchIndex];
            deckarr[switchIndex] = temp;
        }
        for (int i : deckarr) {
            deck.add(i);
        }
    }

    void playManualGame() {
        input = new Scanner(System.in);
        while (players.size() > 1) {
            Player current = players.remove(0);
            if (current.isAlive()) {
                giveTurn(current);
                if (current.isAlive()) {
                    players.add(current);
                }
            }
        }
        System.out.println(players.get(0).getName() + " you are the winner!");
        input.close();
    }

    private void giveTurn(Player player) {
        System.out.println(player.getName() + ", what is your move?");
        System.out.println("Tokens: " + player.getTokens());
        System.out.println("Hand: " + Arrays.toString(player.getHand()) + "\n");
        System.out.println("Revealed Cards: " + revealed);
        String move = null;
        do {
            if (move != null) {
                System.out.println("You did not input a valid move please try again");
            }
            move = offerChoices(player);
        } while (!isValidMove(move, player));
        switch (move) {
            case "I":                                                                          //Income
                player.takeIncome();
                break;
            case "C": {                                                                  //Coup
                Player target = getTarget(player);
                revealed.add(loseAdvisor(player.coup(target)));
                break;
            }
            case "T": {                                                                  //Taxes
                ArrayList<Player> challengers = offerMoveChallenge(player, "T", null);
                if (player.takeTaxes()) {
                    player.addTokens(3);
                    for (Player p : challengers) {
                        System.out.println("\nYou lost the challenge");
                        revealed.add(loseAdvisor(p));
                    }
                } else if (challengers.size() == 0) {
                    player.addTokens(3);
                } else {
                    System.out.println("\nYou lost to a challenge");
                    revealed.add(loseAdvisor(player));
                }
                break;
            }
            case "E": {                                                                  //Exchanges
                ArrayList<Player> challengers = offerMoveChallenge(player, "E", null);
                if (player.swapAdvisors()) {
                    player.drawAdvisor(deck.remove());
                    player.drawAdvisor(deck.remove());
                    deck.add(loseAdvisor(player));
                    deck.add(loseAdvisor(player));
                    for (Player p : challengers) {
                        System.out.println("\nYou lost the challenge");
                        revealed.add(loseAdvisor(p));
                    }
                } else if (challengers.size() == 0) {
                    player.drawAdvisor(deck.remove());
                    player.drawAdvisor(deck.remove());
                    deck.add(loseAdvisor(player));
                    deck.add(loseAdvisor(player));
                } else {
                    System.out.println("\nYou lost to a challenge");
                    revealed.add(loseAdvisor(player));
                }
                break;
            }
            case "A": {                                                                  //Assassinate
                Player target = getTarget(player);
                player.loseTokens(3);
                ArrayList<Player> challengers = offerMoveChallenge(player, "A", target);
                if (challengers.size() != 0) {
                    if (player.assassinate()) {
                        for (Player p : challengers) {
                            System.out.println("\nYou lost the challenge");
                            revealed.add(loseAdvisor(p));
                        }
                        if (!challengers.contains(target)) {
                            BlockAssassination(player, target);
                        } else if (target.isAlive()) {
                            System.out.println("You were assassinated");
                            revealed.add(loseAdvisor(target));
                        }
                    } else {
                        System.out.println("\nYou lost to a challenge");
                        revealed.add(loseAdvisor(player));
                    }
                } else {
                    BlockAssassination(player, target);
                }
                break;
            }
            case "S": {                                                                  //Steal
                Player target = getTarget(player);
                ArrayList<Player> challengers = offerMoveChallenge(player, "S", target);
                if (challengers.size() != 0) {
                    if (player.steal()) {
                        for (Player p : challengers) {
                            System.out.println("\nYou lost the challenge");
                            revealed.add(loseAdvisor(p));
                        }
                        if (!challengers.contains(target)) {
                            BlockSteal(player, target);
                        } else if (target.isAlive()) {
                            if (target.getTokens() < 2) {
                                int i = target.getTokens();
                                target.loseTokens(i);
                                player.addTokens(i);
                            } else {
                                target.loseTokens(2);
                                player.addTokens(2);
                            }
                        }
                    } else {
                        System.out.println("\nYou lost to a challenge");
                        revealed.add(loseAdvisor(player));
                    }
                } else {
                    BlockSteal(player, target);
                }
                break;
            }
            case "F":                                                                   //Foreign Aid
                boolean isBlocked = false;
                for (Player p : players) {
                    if (!isBlocked && offerBlock(player, "F", p)) {
                        if (!offerBlockChallenge(player, "F", p)) {
                            isBlocked = true;
                        } else if (p.blockForeignAid()) {
                            isBlocked = true;
                            System.out.println("\nYou lost the challenge");
                            revealed.add(loseAdvisor(player));
                        }
                    }
                }
                if (!isBlocked) {
                    player.addTokens(2);
                }
                break;
        }
    }

    private String offerChoices(Player player) {
        if (player.getTokens() >= 10) {
            System.out.println("C - Coup");
        } else {
            System.out.println("I - Take income\nT - Take taxes\nF - Take foreign aid\nE - Exchange cards\nS - Steal 2 income");
            if (player.getTokens() >= 3) {
                System.out.println("A - Assassinate");
            }
            if (player.getTokens() >= 7) {
                System.out.println("C - Coup");
            }
        }
        return input.nextLine().toUpperCase();
    }

    private boolean isValidMove(String move, Player player) {
        if (player.getTokens() >= 10) {
            return move.equals("C");
        } else {
            if (move.equals("I")) return true;
            if (move.equals("T")) return true;
            if (move.equals("F")) return true;
            if (move.equals("E")) return true;
            if (move.equals("S")) return true;
            if (move.equals("A")) if (player.getTokens() >= 3) return true;
            if (move.equals("C")) return player.getTokens() >= 7;
            return false;
        }
    }

    private Player getTarget(Player player) {
        System.out.println(player.getName() + ", who do you want to target");
        int chosenIndex = -1;
        do {
            for (int i = 0; i < players.size(); i++) {
                System.out.println(i + " - " + players.get(i).getName());
            }
            try {
                chosenIndex = Integer.parseInt(input.nextLine());
            } catch (Exception e) {
                System.out.println("You did not enter a valid response");
            }
        } while (chosenIndex == -1);
        return players.get(chosenIndex);
    }

    private int loseAdvisor(Player player) {
        System.out.println(player.getName() + ", what advisor do you want to lose?");
        System.out.println("Hand: " + Arrays.toString(player.getHand()) + "\n");
        int chosenIndex;
        do {
            try {
                chosenIndex = Integer.parseInt(input.nextLine());
                if (player.getHand()[chosenIndex] == 0) {
                    chosenIndex = -1;
                    System.out.println("You did not enter a valid response");
                }
            } catch (Exception e) {
                chosenIndex = -1;
                System.out.println("You did not enter a valid response");
            }
        } while (chosenIndex == -1);
        return player.loseAdvisor(chosenIndex);
    }

    private ArrayList<Player> offerMoveChallenge(Player player, String move, Player target) {
        String movePrint = "";
        switch (move) {
            case "T":
                movePrint = "take taxes.";
                break;
            case "E":
                movePrint = "exchange cards";
                break;
            case "S":
                movePrint = "steal from " + target.getName() + ".";
                break;
            case "A":
                movePrint = "assassinate " + target.getName() + ".";
        }
        ArrayList<Player> challengers = new ArrayList<>();
        for (Player p : players) {
            System.out.println(p.getName() + ", " + player.getName() + " attempts to " + movePrint + " Do you challenge?");
            String response = input.nextLine().toUpperCase();
            if (response.equals("Y") || response.equals("YES")) {
                challengers.add(p);
            }
        }
        return challengers;
    }

    private boolean offerBlock(Player player, String move, Player target) {
        String movePrint = "";
        switch (move) {
            case "S":
                movePrint = "steal from you.";
                break;
            case "A":
                movePrint = "assassinate you.";
                break;
            case "F":
                movePrint = "take foreign aid.";
        }
        System.out.println(target.getName() + ", " + player.getName() + " attempts to " + movePrint + " Do you block?");
        String response = input.nextLine().toUpperCase();
        return (response.equals("Y") || response.equals("YES"));
    }

    private boolean offerBlockChallenge(Player player, String move, Player target) {
        String movePrint = "";
        switch (move) {
            case "S":
                movePrint = "block your steal.";
                break;
            case "A":
                movePrint = "block your assassination.";
                break;
            case "F":
                movePrint = "block your foreign aid.";
        }
        System.out.println(player.getName() + ", " + target.getName() + " attempts to " + movePrint + " Do you challenge?");
        String response = input.nextLine().toUpperCase();
        return (response.equals("Y") || response.equals("YES"));
    }

    private void BlockAssassination(Player player, Player target) {
        boolean block = offerBlock(player, "A", target);
        if (!block) {
            System.out.println("You were assassinated");
            revealed.add(loseAdvisor(target));
        } else {
            boolean blockChallenge = offerBlockChallenge(player, "A", target);
            if (blockChallenge && target.blockAssassination()) {
                System.out.println("\nYou lost to a challenge");
                revealed.add(loseAdvisor(player));
            }
        }
    }

    private void BlockSteal(Player player, Player target) {
        boolean block = offerBlock(player, "S", target);
        if (!block) {
            if (target.getTokens() < 2) {
                int i = target.getTokens();
                target.loseTokens(i);
                player.addTokens(i);
            } else {
                target.loseTokens(2);
                player.addTokens(2);
            }
        } else {
            boolean blockChallenge = offerBlockChallenge(player, "S", target);
            if (blockChallenge && target.blockSteal()) {
                System.out.println("\nYou lost to a challenge");
                revealed.add(loseAdvisor(player));
            }
        }
    }

    public String toString() {
        return "" + deck + "\n" + players;
    }
}