package connect4.model;

/**
 * Describes an Element of the recursion tree for getting the next optimal move
 * for the bot, depending on the set level.
 */
public class Node {

    private final Game game;
    private final int valueOfNode;

    /**
     * Constructor sets the game-value (which is needed for building up the
     * game-tree) and the game of the current tree-node.
     *
     * @param valueOfNode The value of the simulated move of current
     *                    {@code game}.
     * @param game The current game.
     */
    public Node(int valueOfNode, Game game) {
            this.valueOfNode = valueOfNode;
            this.game = game;
    }

    /**
      * Getter for the representative value of the board.
      *
      * @return The value of the current {@code game}.
      */
    public int getValueOfNode() {
        return valueOfNode;
    }

    /**
     * Getter for the game instance of the node.
     *
     * @return The game of the current {@code node}.
     */
    public Game getGame() {
        return game;
    }
}
