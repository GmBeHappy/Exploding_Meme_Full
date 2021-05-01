package exploding_meme_full;

import java.util.ArrayList;

public class Game{
    public static boolean isStart;
    public static boolean isEndGame;
    public static boolean isMyTurn;
    public static boolean isAttack;
    
    public static Deck deck;
    public static Deck dropedDeck;
    
    public static ArrayList<Player> players;
    public static ArrayList<String> turnList;
    
    public static String playerName;

    public Game(String playerName, ArrayList<String> playerNames) {
        Game.playerName = playerName;
        for (int i = 0; i < playerNames.size(); i++) {
            Game.players.add(new Player(playerNames.get(i)));
        }
        for (int i = 0; i < playerNames.size(); i++) {
            Game.players.get(i).getHand().addCard(new Card(11));
        }
        Game.deck = new Deck("deck");
        Game.dropedDeck = new Deck("dropedDeck");
        
        if(Lobby.isHead) {
            Game.deck.refill(playerNames.size());
            for (int i = 0; i < playerNames.size(); i++) {
                for (int j = 0; j < 4; j++) {
                    Card newCard = Game.deck.drawCard();
                    Game.players.get(i).getHand().addCard(newCard);
                }
            }
            //public mqtt
        }
        
        Game.isStart = true;
        Game.isEndGame = false;
        Game.isMyTurn = false;
        Game.isAttack = false;
    }
    
    
}
