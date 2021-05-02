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

public class Game implements MqttCallback{
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
    private String clientId ;
    private final String USERNAME = "OOP_Exploding_Meme";
    private final String PASSWORD = "ZjFjfNv.VZ-bKh2";

    public Game(String playerName, ArrayList<String> playerNames,String gameRoom) throws MqttException, InterruptedException {
        System.out.println("gameeeee");
        clientId = "EXPM" + UUID.randomUUID().toString();
        this.connectServer(gameRoom);
        Game.playerName = playerName;
        for (int i = 0; i < playerNames.size(); i++) {
            Game.players.add(new Player(playerNames.get(i)));
            Game.turnList.add(playerNames.get(i));
        }
        for (int i = 0; i < playerNames.size(); i++) {
            Game.players.get(i).getHand().addCard(new Card(11));
        }
        Game.dropedDeck = new Deck("dropedDeck");
        
        if(Lobby.isHead) {
            TimeUnit.SECONDS.sleep(2);
            Game.deck = new Deck("deck");
            
            Game.deck.refill(playerNames.size());
            Game.deck.shuffle();
            for (int i = 0; i < playerNames.size(); i++) {
                for (int j = 0; j < 4; j++) {
                    Card newCard = Game.deck.drawCard();
                    Game.players.get(i).getHand().addCard(newCard);
                }
            }
            
            //public deck, Array Player
            JSONObject objUpdateDeck = new JSONObject();
            String typeUpdate = "deckUpdate";
            String deskName = deck.getDeckName();
            JSONArray cardIdArray = new JSONArray();
            for (int i = 0; i < deck.cards.size(); i++) {
                cardIdArray.add(deck.cards.get(i).getCardId());
            }
            System.out.println(cardIdArray);
            objUpdateDeck.put("typeUpdate", typeUpdate);
            objUpdateDeck.put("deskName", deck.getDeckName());
            objUpdateDeck.put("deck", cardIdArray);
            System.out.println(objUpdateDeck);
            this.sendMessage(objUpdateDeck.toJSONString());
        }
        
        Game.isStart = true;
        Game.isEndGame = false;
        Game.isMyTurn = false;
        Game.isAttack = false;
    }
    
    public static boolean endTurn(){
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
    
    private void connectServer(String gameRoom) throws MqttException{
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
    
    public void messageArrived(String topic, MqttMessage message) throws MqttException {
        System.out.println(String.format("[%s] %s", topic, new String(message.getPayload())));
        JSONParser parser = new JSONParser();
        String msg = new String(message.getPayload());
        try{
            JSONObject json = (JSONObject) parser.parse(msg);
            if(!Lobby.isHead){
                if(json.get("typeUpdate").equals("deckUpdate")){
                    Game.deck = new Deck(json.get("deskName").toString());
                    Object o = parser.parse(json.get("deck").toString());
                    JSONArray cardArray = (JSONArray) o;
                    System.out.println(cardArray);
                    for (int i = 0; i < cardArray.size(); i++) {
                        Game.deck.addCard(new Card(cardArray.get(i).toString()));
                    }
                }
            }
            else{
                
            }
        }
        catch(ParseException pe){	
            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        }
    }
}
