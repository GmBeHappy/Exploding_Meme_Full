package exploding_meme_full;

public class Player {
    private String playerName;
    public Deck hand;
    private boolean isAlive;

    public Player(String playerName) {
        this.playerName = playerName;
        this.isAlive = true;
        this.hand = new Deck(playerName);
    }

    public String getPlayerName() {
        return playerName;
    }

    public Deck getHand() {
        return hand;
    }

    public boolean isIsAlive() {
        return isAlive;
    }
    
    
}
