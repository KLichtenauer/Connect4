package model;

/** Models the players of a {@link Game}. */
public enum Player {

    /** human */
    HUMAN("X"),

    /** bot */
    BOT("O"),

    /** no one */
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
