package model;

import kotlin.reflect.jvm.internal.ReflectProperties;

import java.util.Collection;

public class Game implements Board {

    Player[][] gameBoard;
    Player firstPlayer;
    Player humanPlayer = Player.HUMAN;
    Player botPlayer = Player.BOT;
    int level;

    public Game(boolean isFirstPlayerHuman) {
        createBoard();
        level = 4;
        setPlayers(isFirstPlayerHuman);
    }

    private void setPlayers(boolean isFirstPlayerHuman) {
        if (isFirstPlayerHuman) {
            firstPlayer = Player.HUMAN;
        } else {
            firstPlayer = Player.BOT;
        }
    }

    private void createBoard() {
        gameBoard = new Player[Board.ROWS][Board.COLS];
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[0].length; j++) {
                gameBoard[i][j] = Player.NOBODY;
            }
        }
    }

    @Override
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    // TODO: 11.12.2021 Utility klasse mit überprüfung ob move zulässig ist.
    // TODO: Es sollte dabei gleich überprüft werden ob in der Spalte noch was frei ist.
    // TODO: Nur mgl. wenn er auch dran ist. this.clone() oder nur clone() ???
    @Override
    public Board move(int col) {
        Game gameAfterMove = null;
        if (Validate.isMoveValid(col)) {
            gameAfterMove = (Game) this.clone();
            int colConvToIndex = col - 1;
            boolean iterate = true;
            for (int i = Board.ROWS - 1; i >= 0 && iterate; i--) {
                if(gameBoard[i][colConvToIndex] == Player.NOBODY) {
                    gameAfterMove.gameBoard[i][colConvToIndex] = Player.HUMAN;
                    iterate = false;
                }
           }
       }
       return gameAfterMove;
    }

    @Override
    public Board machineMove() {
        return null;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public boolean isGameOver() {
        return false;
    }

    @Override
    public Player getWinner() {
        return null;
    }

    @Override
    public Collection<Coordinates2D> getWitness() {
        return null;
    }

    @Override
    public Player getSlot(int row, int col) {
        Player player;
        Player content = gameBoard[row][col];
        if (content.equals(Player.BOT)) {
            player = botPlayer;
        } else if (content.equals(Player.HUMAN)) {
            player = humanPlayer;
        } else {
            player = Player.NOBODY;
        }
        return player;
    }

    @Override
    public Board clone() {
        Game clonedBoard = new Game(humanPlayer == firstPlayer);
        clonedBoard.gameBoard = gameBoard.clone();
        clonedBoard.level = level;
        return clonedBoard;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col < Board.COLS; col++) {
                builder.append(gameBoard[row][col]);
                if(!(col == Board.COLS - 1)) {
                    builder.append(" ");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    private void getVerticalGroups() {

        int[][] groups = new int[2][];
        int[] groupsOfHuman = new int[4];
        int[] groupsOfBot = new int[4];
        groups[0] = groupsOfHuman;
        groups[1] = groupsOfBot;
        int counter = 0;
        Player currentPlayer = Player.NOBODY;
        for (int row = 0; row < Board.ROWS ; row++) {
            for (int col = 0; col < Board.COLS; col++) {
                if (gameBoard[row][col] != currentPlayer
                        || col == Board.COLS - 1) {
                    if (currentPlayer == Player.BOT) {
                        groupsOfBot[counter - 1]++;
                    } else if (currentPlayer == Player.HUMAN) {
                        groupsOfHuman[counter - 1]++;
                    }
                    counter = 1;
                    currentPlayer = gameBoard[row][col];
                } else {
                    counter++;
                }
            }
        }
    }
}
