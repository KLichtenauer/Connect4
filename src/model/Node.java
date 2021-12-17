package model;

public class Node {

    Node parent;
    Node[] children;
    int level;
    Game game;
    int valueOfBoard;
    int chosenCol;

    public Node(Node parent, int level, Game game) {
            this.parent = parent;
            this.level = level;
            this.game = game;
            children = new Node[7];
            setChildren();
    }

    // TODO: 14.12.2021 move kann illegalArugmentException schmeißen, was damit tun?
    // TODO: 17.12.2021 schauen wenn game over ist nichts mehr machen!
    // TODO: 17.12.2021 überprüfen ob mehrere Spiele hintereinadner ausgeführt
    //  werden können bzw die einstellungen gespeichert werden
    public void setChildren() {
        int currentValue = game.evaluateMove();
        int col = 0;
        if (level <= 0) {
            valueOfBoard = currentValue;
        } else if (game.getCurrentPlayer() == Player.HUMAN) { //human move
            int minValue = Integer.MAX_VALUE;
            for (int i = 0; i < 7; i++) {
               children[i] = simulateMove(i + 1, Player.HUMAN);
                int value = children[i].valueOfBoard;
                if (value < minValue) {
                    minValue = value;
                    col = i;
                }
            }
            valueOfBoard = parent != null ? currentValue + minValue : minValue;
            //valueOfBoard = currentValue + minValue;
            //valueOfBoard = minValue;
        } else {
            int maxValue = Integer.MIN_VALUE;
            for (int i = 0; i < 7; i++) {
                children[i] = simulateMove(i + 1, Player.BOT);
                int value = children[i].valueOfBoard;
                if (value > maxValue) {
                    maxValue = value;
                    col = i;
                }
            }
            //valueOfBoard = currentValue + maxValue;
            //valueOfBoard = maxValue;
            valueOfBoard = parent != null ? currentValue + maxValue : maxValue;

        }
        //chosenCol = col;
        chosenCol = col + 1; // + 1 because in the method machineMove
                             // universalMove needs an col between 1 - 7
    }

    private Node simulateMove(int col, Player player) {
        Game boardAfterMove = (Game) game.universalMove(col);
        int levelForMethod = level - 1;
        return new Node(this, levelForMethod, boardAfterMove);
    }

    public int getChosenCol() {
        return chosenCol;
    }
}
