package connect4.model;

/** Models the players of a {@link Game}. */
public enum Player {

    /** Representation for human. */
    HUMAN("X"),

    /** Representation for bot. */
    BOT("O"),

    /** Representation for no one. */
    NOBODY(".");

    private final String player;

    Player(final String sPlayer) {
        player = sPlayer;
    }

    /**
     * Provides the abbreviation of the player.
     *
     * @return The abbreviation of the player
     */
    public String getPlayer() {
        return player;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getPlayer();
    }
}
