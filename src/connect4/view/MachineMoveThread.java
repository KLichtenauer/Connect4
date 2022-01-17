package connect4.view;

import connect4.model.Board;

class MachineMoveThread extends Thread {
    private final Board game;
    private Board gameAfterMove;

    public MachineMoveThread(Board currentGame) {
        game = currentGame;

    }

    @Override
    public void run() {
        gameAfterMove = game.machineMove();

    }

    public Board getGameAfterMove() {
        return gameAfterMove.clone();
    }

    public void kill() {
    }
}
