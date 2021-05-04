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

    public boolean isStart;
    public boolean isEndGame;
    public boolean isMyTurn;
    public boolean isAttack;
    public boolean isFavor;
    public boolean isSkip;
    public boolean isNope;
    public boolean isSeeTheFuture;
    public boolean isSuffle;
    public boolean isNormal2;
    public boolean isNormal3;
    public boolean isNormal5;
    public boolean isAlive;

    public String effect;
    public String effectUser;

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

    private String target;
    private int cardIdTarget;

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

        this.isStart = true;
        this.isAttack = false;
        this.isFavor = false;
        this.isNope = false;
        this.isSeeTheFuture = false;
        this.isSkip = false;
        this.isSuffle = false;
        this.isNormal2 = false;
        this.isNormal3 = false;
        this.isNormal5 = false;
        this.isEndGame = false;
        this.isAlive = false;
    }

    public boolean endTurn() {
        try {
            if (!this.isAttack) {
                String last = turnList.get(0);
                turnList.remove(0);
                turnList.add(last);
            } else {
                this.isAttack = false;
            }
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

    private MqttConnectOptions setUpConnectionOptions(String username, String password) {
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
                objCard.put("cardID", Game.players.get(i).hand.cards.get(j).getCardId());
                objCard.put("cardIndex", Game.players.get(i).hand.cards.get(j).getIndex());
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

    public void updateTurnList() throws MqttException {
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
        //System.out.println(cardIdArray);
        objUpdateDeck.put("typeUpdate", "deckUpdate");
        objUpdateDeck.put("deckName", deck.getDeckName());
        objUpdateDeck.put("deck", cardIdArray);
        System.out.println("Sending Update deck");
        System.out.println(objUpdateDeck);
        this.sendMessage(objUpdateDeck.toJSONString());
        System.out.println("sent");
    }

    public void updateDropedDeck() throws MqttException {
        JSONObject objUpdateDropedDeck = new JSONObject();
        JSONArray cardIdArray = new JSONArray();
        for (int i = 0; i < dropedDeck.cards.size(); i++) {
            JSONObject card = new JSONObject();
            card.put("cardID", dropedDeck.cards.get(i).getCardId());
            card.put("cardIndex", dropedDeck.cards.get(i).getIndex());
            cardIdArray.add(card);
        }
        //System.out.println(cardIdArray);
        objUpdateDropedDeck.put("typeUpdate", "dropDeckUpdate");
        objUpdateDropedDeck.put("deckName", dropedDeck.getDeckName());
        objUpdateDropedDeck.put("cards", cardIdArray);
        System.out.println("Sending Update dropdeck");
        System.out.println(objUpdateDropedDeck);
        this.sendMessage(objUpdateDropedDeck.toJSONString());
        System.out.println("sent");
    }

    public void drawCard() throws MqttException {
        if (!this.isSkip) {
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getPlayerName().equals(this.playerName)) {
                    Card newCard = Game.deck.drawCard();
                    Game.players.get(i).hand.addCard(newCard);
                    System.out.println(players.get(i).getPlayerName() + " draw " + newCard);
                }
            }
        } else {
            this.isSkip = false;
        }
        //this.updatePlayerHand();
        //this.updateDeck();
    }

    public void turnHaveEnd() throws MqttException {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getPlayerName().equals(this.playerName)) {
                if (Game.players.get(i).hand.checkHaveExplo()) {
                    System.out.println("Got exploding");
                    if (Game.players.get(i).hand.checkHaveDefuse()) {
                        int defuseIndex = Game.players.get(i).hand.removeDefuse();
                        Game.dropedDeck.addCard(new Card(11, defuseIndex));
                        System.out.println("defuse dropped");
                        System.out.println(players.get(i).getPlayerName() + " removed defuse! ");
                        System.out.println("removing exploding from " + players.get(i).getPlayerName() + "hand");
                        int exploIndex = Game.players.get(i).hand.removeExploding();
                        System.out.println("removed exploding from " + players.get(i).getPlayerName() + "hand");
                        Game.deck.addCard(new Card(12, exploIndex));
                        System.out.println("return exploding to deck");
                        Game.deck.shuffle();
                        System.out.println("deck suffle");

                        this.endTurn();
                    } else {
                        System.out.println("Lost");
                        System.out.println("remove " + players.get(i).getPlayerName() + "from turn list");
                        turnList.remove(0);
                        System.out.println("remove success");
                    }

                } else {
                    this.endTurn();
                }
                System.out.println(players.get(i).getPlayerName() + " end turn! ");
            }
        }
        this.updateDeck();
        this.updateDropedDeck();
        this.updatePlayerHand();
        this.updateTurnList();

    }

    public void messageArrived(String topic, MqttMessage message) throws MqttException {
        System.out.println(String.format("[%s] %s", topic, new String(message.getPayload())));
        JSONParser parser = new JSONParser();
        String msg = new String(message.getPayload());
        try {
            JSONObject json = (JSONObject) parser.parse(msg);

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
                isAlive = false;
                for (int i = 0; i < turnList.size(); i++) {
                    if (turnList.get(i).equals(playerName)) {
                        isAlive = true;
                    }
                }
                System.out.println(turnList);
                System.out.println("Turn list Updated");
                if (turnList.size() <= 1) {
                    isEndGame = true;
                    //public isEndGame
                }
            }

            if (json.get("typeUpdate").equals("dropDeckUpdate")) {
                System.out.println("Updating drop Deck...");
                Game.dropedDeck = new Deck(json.get("deckName").toString());
                Object o = parser.parse(json.get("cards").toString());
                JSONArray cardsArray = (JSONArray) o;
                //System.out.println(cardsArray);
                for (int i = 0; i < cardsArray.size(); i++) {
                    JSONObject data = (JSONObject) parser.parse(cardsArray.get(i).toString());
                    Game.dropedDeck.addCard(new Card(Integer.parseInt(data.get("cardID").toString()), Integer.parseInt(data.get("cardIndex").toString())));
                }
                System.out.println(Game.dropedDeck.cards);
                System.out.println("Updated");
            }

            if (json.get("typeUpdate").equals("effectUpdate")) {
                System.out.println("Updating effect...");
                System.out.print(json.get("usePlayer").toString() + " use ");
                this.effectUser = json.get("usePlayer").toString();
                this.effect = json.get("effect").toString();
                switch (json.get("effect").toString()) {
                    case "attack":
                        this.isAttack = true;
                        System.out.println("Attacked");
                        break;
                    case "favor":
                        this.isFavor = true;
                        System.out.println("Favored");
                        break;
                    case "nope":
                        this.isNope = true;
                        System.out.println("Nope");
                        break;
                    case "seeTeFuture":
                        this.isSeeTheFuture = true;
                        System.out.println("See the future");
                        break;
                    case "suffle":
                        this.isSuffle = true;
                        System.out.println("suffle");
                        break;
                    case "skip":
                        this.isSkip = true;
                        System.out.println("skip");
                        break;
                    case "normal2":
                        this.isNormal2 = true;
                        System.out.println("normal2");
                        break;
                    case "norma3":
                        this.isNormal3 = true;
                        System.out.println("normal3");
                        break;
                    case "normal5":
                        this.isNormal5 = true;
                        System.out.println("normal5");
                        break;
                }
                System.out.println("Effect updated");
            }

            if (json.get("typeUpdate").equals("clearEffect")) {
                this.isAttack = false;
                this.isFavor = false;
                this.isNope = false;
                this.isSeeTheFuture = false;
                this.isSkip = false;
                this.isSuffle = false;
                this.isNormal2 = false;
                this.isNormal3 = false;
                this.isNormal5 = false;
                this.effect = "";
                this.effectUser = "";
            }
        } catch (ParseException pe) {
            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        }
    }

    public void useCard(Card card) throws MqttException {
        effect(card.getCardId());
    }

    public void useCard(ArrayList<Card> cards) {
        switch (cards.size()) {
            case 2:
                System.out.println("sending normal2...");
                JSONObject normal2Msg = new JSONObject();
                normal2Msg.put("usePlayer", this.playerName);
                normal2Msg.put("typeUpdate", "effectUpdate");
                normal2Msg.put("effect", "normal2");
                normal2Msg.put("target", this.target);
                normal2Msg.put("data", this.cardIdTarget);
                System.out.println(normal2Msg);
                break;
            case 3:
                System.out.println("sending normal3...");
                JSONObject normal3Msg = new JSONObject();
                normal3Msg.put("usePlayer", this.playerName);
                normal3Msg.put("typeUpdate", "effectUpdate");
                normal3Msg.put("effect", "normal2");
                normal3Msg.put("target", this.target);
                normal3Msg.put("data", this.cardIdTarget);
                System.out.println(normal3Msg);
                break;
            case 5:
                System.out.println("sending normal5...");
                JSONObject normal5Msg = new JSONObject();
                normal5Msg.put("usePlayer", this.playerName);
                normal5Msg.put("typeUpdate", "effectUpdate");
                normal5Msg.put("effect", "normal2");
                normal5Msg.put("data", this.cardIdTarget);
                System.out.println(normal5Msg);
                break;
        }
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setCardIdTarget(int cardIdTarget) {
        this.cardIdTarget = cardIdTarget;
    }

    public void clearEffect() throws MqttException {
        System.out.println("sending clear effect...");
        JSONObject clearMsg = new JSONObject();
        clearMsg.put("typeUpdate", "clearEffect");
        System.out.println(clearMsg);
        this.sendMessage(clearMsg.toJSONString());
    }

    private void effect(int id) throws MqttException {
        switch (id) {
            case 0:
                System.out.println("sending Attack...");
                JSONObject attackMsg = new JSONObject();
                attackMsg.put("usePlayer", this.playerName);
                attackMsg.put("typeUpdate", "effectUpdate");
                attackMsg.put("effect", "attack");
                attackMsg.put("target", turnList.get(1));
                System.out.println(attackMsg);
                this.sendMessage(attackMsg.toJSONString());
                break;
            case 1:
                System.out.println("sending Favor...");
                JSONObject favorMsg = new JSONObject();
                favorMsg.put("usePlayer", this.playerName);
                favorMsg.put("typeUpdate", "effectUpdate");
                favorMsg.put("effect", "favor");
                favorMsg.put("target", this.target);
                System.out.println(favorMsg);
                this.sendMessage(favorMsg.toJSONString());
                break;
            case 2:
                System.out.println("sending Nope...");
                JSONObject nopeMsg = new JSONObject();
                nopeMsg.put("usePlayer", this.playerName);
                nopeMsg.put("typeUpdate", "effectUpdate");
                nopeMsg.put("effect", "nope");
                System.out.println(nopeMsg);
                this.sendMessage(nopeMsg.toJSONString());
                break;
            case 3:
                System.out.println("sending See the future...");
                JSONObject seeMsg = new JSONObject();
                seeMsg.put("usePlayer", this.playerName);
                seeMsg.put("typeUpdate", "effectUpdate");
                seeMsg.put("effect", "seeTheFuture");
                System.out.println(seeMsg);
                this.sendMessage(seeMsg.toJSONString());
                break;
            case 4:
                System.out.println("sending Suffle...");
                JSONObject suffleMsg = new JSONObject();
                suffleMsg.put("usePlayer", this.playerName);
                suffleMsg.put("typeUpdate", "effectUpdate");
                suffleMsg.put("effect", "suffle");
                System.out.println(suffleMsg);
                this.sendMessage(suffleMsg.toJSONString());
                break;
            case 5:
                System.out.println("sending skip...");
                JSONObject skipMsg = new JSONObject();
                skipMsg.put("usePlayer", this.playerName);
                skipMsg.put("typeUpdate", "effectUpdate");
                skipMsg.put("effect", "skip");
                System.out.println(skipMsg);
                break;
        }
    }
}
