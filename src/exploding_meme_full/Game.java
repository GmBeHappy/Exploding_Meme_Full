package exploding_meme_full;

import java.awt.Desktop;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

public class Game implements MqttCallback {

    public static boolean isStart;
    public static boolean isEndGame;
    public static boolean isMyTurn;
    public static boolean isAttack;

    public static Deck deck;
    public static Deck dropedDeck;

    public static ArrayList<Player> players = new ArrayList<Player>();
    public static ArrayList<String> turnList = new ArrayList<String>();

    public static String playerName;

    private String topic;
    private String gameRoom;
    private MqttClient client;
    private final int qos = 2;
    private final String broker = "tcp://mqtt.gmtech.co.th:1883";
    private String clientId;
    private final String USERNAME = "OOP_Exploding_Meme";
    private final String PASSWORD = "ZjFjfNv.VZ-bKh2";

    public Game(String playerName, ArrayList<String> playerNames, String gameRoom) throws MqttException, InterruptedException {
        System.out.println("gameeeee");
        clientId = "EXPM" + UUID.randomUUID().toString();
        this.connectServer(gameRoom);
        Game.playerName = playerName;
        for (int i = 0; i < playerNames.size(); i++) {
            Game.players.add(new Player(playerNames.get(i)));
            Game.turnList.add(playerNames.get(i));
        }

        Game.dropedDeck = new Deck("dropedDeck");

        if (Lobby.isHead) {
            //TimeUnit.SECONDS.sleep(5);
            Game.deck = new Deck("deck");

            Game.deck.refill(playerNames.size());
            Game.deck.shuffle();
            for (int i = 0; i < playerNames.size(); i++) {
                for (int j = 0; j < 4; j++) {
                    Card newCard = Game.deck.drawCard();
                    Game.players.get(i).getHand().addCard(newCard);
                }
                Game.players.get(i).getHand().addCard(new Card(11));
            }
            for (int i = 0; i < playerNames.size() - 1; i++) {
                Game.deck.cards.add(new Card(12));
            }
            Game.deck.shuffle();

            //public deck, Array Player
            this.updateDeck();
            //public player 
            this.updatePlayerHand();
            //public turn list
            this.updateTurnList();
        }

        Game.isStart = true;
        Game.isEndGame = false;
        Game.isMyTurn = false;
        Game.isAttack = false;
    }

    public static boolean endTurn() {
        try {
            String last = turnList.get(0);
            turnList.remove(0);
            turnList.add(last);
            //public turnList
        } catch (Exception e) {
            return false;
        } finally {
            return true;
        }
    }

    private void connectServer(String gameRoom) throws MqttException {
        MqttConnectOptions conOpt = setUpConnectionOptions(USERNAME, PASSWORD);
        this.topic = "EXPM/" + gameRoom + "/update";
        this.client = new MqttClient(broker, clientId, new MemoryPersistence());
        this.client.setCallback(this);
        this.client.connect(conOpt);

        this.client.subscribe(this.topic, qos);

    }

    private static MqttConnectOptions setUpConnectionOptions(String username, String password) {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(username);
        connOpts.setPassword(password.toCharArray());
        return connOpts;
    }

    public void sendMessage(String payload) throws MqttException {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);
        this.client.publish(this.topic, message); // Blocking publish
    }

    public void connectionLost(Throwable cause) {
        System.out.println("Connection lost because: " + cause);
        System.exit(1);
    }

    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    public void updatePlayerHand() throws MqttException {
        JSONObject objUpdatePlayersHand = new JSONObject();
        objUpdatePlayersHand.put("typeUpdate", "playerHandUpdate");
        JSONArray playerHandArray = new JSONArray();
        for (int i = 0; i < players.size(); i++) {
            JSONObject objPlayerHand = new JSONObject();
            objPlayerHand.put("playerName", Game.players.get(i).getPlayerName());
            JSONArray handCardIdArray = new JSONArray();
            for (int j = 0; j < Game.players.get(i).hand.cards.size(); j++) {
                JSONObject objCard = new JSONObject();
                objCard.put("cardID",Game.players.get(i).hand.cards.get(j).getCardId());
                objCard.put("cardIndex",Game.players.get(i).hand.cards.get(j).getIndex());
                handCardIdArray.add(objCard);
            }
            objPlayerHand.put("cards", handCardIdArray);
            playerHandArray.add(objPlayerHand);
            System.out.println(handCardIdArray);
        }
        objUpdatePlayersHand.put("data", playerHandArray);
        System.out.println(objUpdatePlayersHand);
        this.sendMessage(objUpdatePlayersHand.toJSONString());
    }
    
    public void updateTurnList() throws MqttException{
        JSONObject objUpdateTurnList = new JSONObject();
        objUpdateTurnList.put("typeUpdate", "turnListUpdate");
        JSONArray turnListArray = new JSONArray();
        for (int i = 0; i < turnList.size(); i++) {
            
            turnListArray.add(turnList.get(i));
        }
        objUpdateTurnList.put("turnList", turnListArray);
        System.out.println(objUpdateTurnList);
        this.sendMessage(objUpdateTurnList.toJSONString());
    }

    public void updateDeck() throws MqttException {
        JSONObject objUpdateDeck = new JSONObject();
        JSONArray cardIdArray = new JSONArray();
        for (int i = 0; i < deck.cards.size(); i++) {
            cardIdArray.add(deck.cards.get(i).getCardId());
        }
        System.out.println(cardIdArray);
        objUpdateDeck.put("typeUpdate", "deckUpdate");
        objUpdateDeck.put("deckName", deck.getDeckName());
        objUpdateDeck.put("deck", cardIdArray);
        System.out.println(objUpdateDeck);
        this.sendMessage(objUpdateDeck.toJSONString());
    }

    public void drawCard() throws MqttException {
        for (int i = 0; i < players.size(); i++) {
            if(players.get(i).getPlayerName().equals(this.playerName)){
                Card newCard = Game.deck.drawCard();
                Game.players.get(i).hand.addCard(newCard);
                System.out.println(players.get(i).getPlayerName() + " draw " + newCard);
            }
        }
        this.updatePlayerHand();
        this.updateDeck();
    }
    
    public void turnHaveEnd() throws MqttException{
        for (int i = 0; i < players.size(); i++) {
            if(players.get(i).getPlayerName().equals(this.playerName)){
                if(Game.players.get(i).hand.checkHaveExplo()){
                    if (Game.players.get(i).hand.checkHaveDefuse()){
                        Game.players.get(i).hand.removeDefuse();
                        System.out.println(players.get(i).getPlayerName() + " removed defuse! ");
                        this.updatePlayerHand();
                    }
                    else
                        turnList.remove(0);
                        
                }
                else {
                    this.endTurn();
                }
                System.out.println(players.get(i).getPlayerName() + " end turn! ");
            }
        }
        this.updateTurnList();
        
    }

    public void messageArrived(String topic, MqttMessage message) throws MqttException {
        System.out.println(String.format("[%s] %s", topic, new String(message.getPayload())));
        JSONParser parser = new JSONParser();
        String msg = new String(message.getPayload());
        try {
            JSONObject json = (JSONObject) parser.parse(msg);
            if (!Lobby.isHead) {
                if (json.get("typeUpdate").equals("deckUpdate")) {
                    System.out.println("Updating Deck...");
                    Game.deck = new Deck(json.get("deckName").toString());
                    Object o = parser.parse(json.get("deck").toString());
                    JSONArray cardArray = (JSONArray) o;
                    System.out.println(cardArray);
                    for (int i = 0; i < cardArray.size(); i++) {
                        Game.deck.addCard(new Card(Integer.parseInt(cardArray.get(i).toString())));
                    }
                    System.out.println("Updated");
                }
            }
            if (json.get("typeUpdate").equals("playerHandUpdate")) {
                System.out.println("Updating Hand...");
                Object o = parser.parse(json.get("data").toString());
                JSONArray playersHandArray = (JSONArray) o;
                //System.out.println(playersHandArray);
                for (int i = 0; i < playersHandArray.size(); i++) {
                    JSONObject data = (JSONObject) parser.parse(playersHandArray.get(i).toString());
                    for (int j = 0; j < this.players.size(); j++) {
                        if (data.get("playerName").equals(this.players.get(j).getPlayerName())) {
                            System.out.println("Updating " + this.players.get(j).getPlayerName());
                            Object oo = parser.parse(data.get("cards").toString());
                            JSONArray cards = (JSONArray) oo;
                            this.players.get(i).hand.cards.clear();
                            for (int k = 0; k < cards.size(); k++) {
                                JSONObject card = (JSONObject) parser.parse(cards.get(k).toString());
                                this.players.get(i).hand.addCard(new Card(Integer.parseInt(card.get("cardID").toString()), Integer.parseInt(card.get("cardIndex").toString())));
                            }
                            System.out.println(this.players.get(j).getPlayerName() + "'s hand updated");
                        }
                    }
                    System.out.println(this.players.get(i).hand.cards);
                }
                System.out.println("Hands Updated");
            }
            if (json.get("typeUpdate").equals("turnListUpdate")) {
                System.out.println("Updating Turn list...");
                Object o = parser.parse(json.get("turnList").toString());
                JSONArray turnListArray = (JSONArray) o;
                //System.out.println(playersHandArray);
                turnList.clear();
                for (int i = 0; i < turnListArray.size(); i++) {
                    turnList.add(turnListArray.get(i).toString());
                }
                System.out.println(turnList);
                System.out.println("Turn list Updated");
            }
        } catch (ParseException pe) {
            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        }
    }
}
