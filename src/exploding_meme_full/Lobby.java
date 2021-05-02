/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exploding_meme_full;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Smart
 */
public class Lobby implements MqttCallback{
    public String playerName;
    public String code;
    public ArrayList<String> playerNames = new ArrayList<String>();
    public Game game;
    public static boolean isRoomFull;
    public static boolean isInLobby;
    public static boolean isHead;
    public static boolean isJoin;
    public static boolean isCreate ;
    public static boolean isInGame;
    
    private String topic;
    private String gameRoom;
    private MqttClient client;
    private final int qos = 2;
    private final String broker = "tcp://mqtt.gmtech.co.th:1883";
    private String clientId ;
    private final String USERNAME = "OOP_Exploding_Meme";
    private final String PASSWORD = "ZjFjfNv.VZ-bKh2";
    
    public int playerInLobby=0;
    public boolean isSuccessCreateRoom;

    public Lobby(String playerName) throws MqttException {
        clientId = "EXPM" + UUID.randomUUID().toString();
        isHead = true;
        this.playerName = playerName;
        this.createGameRoom();
        this.connectServer(this.gameRoom);
        JSONObject msg = new JSONObject();
        JSONArray msgArray = new JSONArray();
        msg.put("name", this.playerName);
        msgArray.add(msg);
        sendMessage(msgArray.toJSONString());
        
        
    }
    
    public Lobby(String playerName, String code) throws MqttException {
        clientId = "EXPM" + UUID.randomUUID().toString();
        this.playerName = playerName;
        isHead = false;
        this.connectServer(code);
        this.joinGame();
    }
    
    public void startGame() throws MqttException{
        this.game = new Game(playerName, playerNames,this.gameRoom);
        JSONObject msg = new JSONObject();
        JSONArray msgArray = new JSONArray();
        msg.put("isStart", "true");
        msgArray.add(msg);
        sendMessage(msgArray.toJSONString());
        this.client.disconnect();
        
    }
    
    private void connectServer(String gameRoom) throws MqttException{
        MqttConnectOptions conOpt = setUpConnectionOptions(USERNAME, PASSWORD);
        this.topic = "EXPM/" + gameRoom;
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
            Object o = parser.parse(msg);
            JSONArray msgArray = (JSONArray) o;
            System.out.println(msgArray.size());
            for(int j=0;j<msgArray.size();j++){
                String msgInArray = msgArray.get(j).toString();
                JSONObject json = (JSONObject)parser.parse(msgInArray);
                if(isHead && msgArray.size()==1){
                    System.out.println("Reccive MSG");
                    if(json.get("name").equals(this.playerName) && this.playerInLobby == 0){
                        this.playerInLobby += 1;
                        this.isSuccessCreateRoom = true;
                        this.playerNames.add(this.playerName);
                        System.out.println("Success Create Game Room");
                    }
                    else{
                        this.isSuccessCreateRoom = false;
                    }
                    if(!json.get("name").equals("") && !json.get("name").equals(this.playerName) && this.playerInLobby > 0){
                        for(int k=0;k<this.playerInLobby;k++){
                            if(!json.get("name").equals(this.playerNames.get(k))){
                                this.playerInLobby += 1;
                                this.playerNames.add(json.get("name").toString());
                                System.out.println(this.playerNames);
                                JSONArray playerNamesArray = new JSONArray();
                                for(int i=0;i<this.playerInLobby;i++){
                                    JSONObject replyMsg = new JSONObject();
                                    replyMsg.put("name", this.playerNames.get(i));
                                    playerNamesArray.add(replyMsg);
                                }
                                System.out.println(playerNamesArray.toJSONString());
                                this.sendMessage(playerNamesArray.toJSONString());
                            }
                            
                            if(playerInLobby==2){
                                startGame();
                            }
                        }
                    }
                }
                if(!isHead){
                    for(int i=0;i<this.playerInLobby;i++){
                        if(!json.get("name").equals("") && !json.get("name").equals(this.playerName) && !json.get("name").equals(this.playerNames.get(i))){
                            this.playerInLobby += 1;
                            this.playerNames.add(json.get("name").toString());
                        }
                            System.out.println(this.playerNames);    
                    }
                    if(json.get("isStart").equals("true")){
                        this.startGame();
                    }
                }
            }
             
        }
        catch(ParseException pe){	
            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        }
    }
    public boolean createGameRoom() throws MqttException{
        String gameRoom = "";
        for (int i = 0; i < 6; i++) {
            gameRoom += getRandomNumberInRange(0, 9);
        }
        this.topic = "EXPM/" + gameRoom ;
        this.gameRoom = gameRoom;
        System.out.println("game room : "+gameRoom); 
        
        return true;
    }
    
    public boolean joinGame() throws MqttException{ 
        JSONObject msg = new JSONObject();
        JSONArray msgArray = new JSONArray();
        msg.put("name", this.playerName);
        msgArray.add(msg);
        sendMessage(msgArray.toJSONString());
        return true;
    }
    
    private static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
    
}
