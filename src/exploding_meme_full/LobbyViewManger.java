package exploding_meme_full;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import exploding_meme_full.InfoLabel;
import java.util.jar.Attributes;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.SpaceRunnerButton;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import exploding_meme_full.Textuse;
import java.util.concurrent.TimeUnit;
import javafx.event.ActionEvent;

public class LobbyViewManger {

    private Boolean host;

    private ImageView[] picture_player = new ImageView[6];

    private ImageView[] picture_backplayer = new ImageView[6];

    private int[] number_cardplayer = {0, 0, 0, 0, 0, 0};

    private InfoLabel chooseShipLabel1;

    private InfoLabel gameRoomLabel;

    private Textuse[] text_numbercared = new Textuse[6];

    private InfoLabel[] textplayer_name = new InfoLabel[6];

    private String[] player_name = {"", "", "", "", "", ""};

    private Deck deck_player;

    private int number_people = 0;

    private SpaceRunnerButton CreateButtonWait = new SpaceRunnerButton("WAIT", 49, 190);
    private SpaceRunnerButton CreateButtonSTART = new SpaceRunnerButton("START", 49, 190);

    private SpaceRunnerButton Card_1;
    private SpaceRunnerButton Card_2;
    private SpaceRunnerButton Card_3;
    private SpaceRunnerButton Card_5;
    private SpaceRunnerButton draw_end;

    private Boolean gamestart = false;

    private AnchorPane gamePane;
    private Scene gameScene;
    private Stage gameStage;

    private AnimationTimer TimerLoop;

    private static final int GAME_WIDTH = 1920;
    private static final int GAME_HIGHT = 1080;
    private final static String BACKGROUND_IMAGE = "exploding_meme_full/resource/pokertable.png";

    private GridPane gridPane;

    private Stage menuStage;
    private String playerName;
    private String gameRoom;

    Lobby lobby;

    public LobbyViewManger(String name) {
        this.playerName = name;
        try {
            lobby = new Lobby(name);
        } catch (MqttException ex) {
            Logger.getLogger(ViewManger.class.getName()).log(Level.SEVERE, null, ex);
        }
        initializeStage();
    }

    public LobbyViewManger(String name, String code) {
        this.playerName = name;
        try {
            lobby = new Lobby(name, code);
        } catch (MqttException ex) {
            Logger.getLogger(ViewManger.class.getName()).log(Level.SEVERE, null, ex);
        }
        initializeStage();

    }

    private void initializeStage() {
        gamePane = new AnchorPane();
        gameScene = new Scene(gamePane, GAME_WIDTH, GAME_HIGHT);
        gameStage = new Stage();
        gameStage.setScene(gameScene);
    }

    public void create(Stage menuStage, Boolean host) throws FileNotFoundException {
        this.host = host;
        this.menuStage = menuStage;
        this.menuStage.hide();
        this.gameRoom = lobby.gameRoom;
        createBackground();

        if (host == true) {
            setButtonSTART(1700, 900);
            gamePane.getChildren().add(CreateButtonSTART);
            gamestartnow();
        } else {
            setButtonWait(1700, 900);
            gamePane.getChildren().add(CreateButtonWait);
            gamestartnow();
        }

        gameRoomLabel = new InfoLabel(String.format("Room : %s", gameRoom), 36);
        gameRoomLabel.setLayoutX(40);
        gameRoomLabel.setLayoutY(25);
        gamePane.getChildren().add(gameRoomLabel);

        chooseShipLabel1 = new InfoLabel(String.format("%d / 6", number_people));
        chooseShipLabel1.setLayoutX(1500);
        chooseShipLabel1.setLayoutY(25);
        gamePane.getChildren().add(chooseShipLabel1);

        for (int i = 0; i < text_numbercared.length; i++) {
            text_numbercared[i] = new Textuse(String.valueOf(number_cardplayer[i]));
            gamePane.getChildren().add(text_numbercared[i]);
            text_numbercared[i].setLayoutX(-200);
            text_numbercared[i].setLayoutY(0);
        }

        createGameLoop();
        gameStage.show();
    }

    private void createBackground() {
        gridPane = new GridPane();

        ImageView backgroImageView1 = new ImageView(BACKGROUND_IMAGE);
        gridPane.getChildren().add(backgroImageView1);

        gridPane.setLayoutX(280);
        gridPane.setLayoutY(50);

        gamePane.getChildren().add(gridPane);
    }

    private void setButtonWait(int x, int y) {
        CreateButtonWait.setLayoutX(x);
        CreateButtonWait.setLayoutY(y);

        CreateButtonWait.setOnAction((event)
                -> {

        });

    }

    private void setButtonSTART(int x, int y) {
        CreateButtonSTART.setLayoutX(x);
        CreateButtonSTART.setLayoutY(y);

        CreateButtonSTART.setOnAction((event)
                -> {
            gamestart = true;
            try {
                this.startGame();
            } catch (MqttException | InterruptedException ex) {
                Logger.getLogger(LobbyViewManger.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

    private void createGameLoop() {
        TimerLoop = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (!gamestart) {
                    try {
                        checkgamestart();
                    } catch (MqttException | InterruptedException ex) {
                        Logger.getLogger(LobbyViewManger.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                number_people = getPlayerInLobby();
                update_picture_textpeople();
                checkmouseposition();
                updatePlayerInSeat();
                if (Game.isStart) {
                    updateIsmyTurn();
                    Deckgamestart();
                    CreateimageCard(deck_player);
                    updatePlayerHand();
                }
            }

        };
        TimerLoop.start();
    }

    private void checkgamestart() throws MqttException, InterruptedException {
        if (host == true) {
            if (gamestart == true) {
                setButtonSTART(-100, -100);
                this.startGame();
            }
        } else {
            if (this.checkIsStart()) {
                setButtonWait(-100, -100);
                lobby.startGame();
            }
        }
    }

    private void update_picture_textpeople() {
        chooseShipLabel1.setText(String.format("%d / 6", number_people));
        for (int i = 0; i < lobby.playerNames.size(); i++) {
            if (player_name[i].equals("")) {

            } else {
                text_numbercared[i].setText(String.valueOf(number_cardplayer[i]));

                textplayer_name[i] = new InfoLabel(player_name[i]);
                textplayer_name[i].setText(player_name[i]);
                gamePane.getChildren().add(textplayer_name[i]);

                picture_player[i] = new ImageView("exploding_meme_full/resource/player.png");
                gamePane.getChildren().add(picture_player[i]);

                Image image = new Image("exploding_meme_full/resource/Back.png", 100, 100, true, true);
                picture_backplayer[i] = new ImageView(image);
                gamePane.getChildren().add(picture_backplayer[i]);

                switch (i) {
                    case 0:
                        picture_player[0].setLayoutX(905);
                        picture_player[0].setLayoutY(760);
                        textplayer_name[0].setLayoutX(750);
                        textplayer_name[0].setLayoutY(693);
                        picture_backplayer[0].setLayoutX(877);
                        picture_backplayer[0].setLayoutY(916);

                        text_numbercared[0].setLayoutX(960);
                        text_numbercared[0].setLayoutY(956);

                        break;
                    case 1:
                        picture_player[1].setLayoutX(335);
                        picture_player[1].setLayoutY(691);
                        textplayer_name[1].setLayoutX(400);
                        textplayer_name[1].setLayoutY(600);
                        picture_backplayer[1].setLayoutX(257);
                        picture_backplayer[1].setLayoutY(883);

                        text_numbercared[1].setLayoutX(342);
                        text_numbercared[1].setLayoutY(916);

                        break;
                    case 2:
                        picture_player[2].setLayoutX(384);
                        picture_player[2].setLayoutY(109);
                        textplayer_name[2].setLayoutX(350);
                        textplayer_name[2].setLayoutY(246);
                        picture_backplayer[2].setLayoutX(552);
                        picture_backplayer[2].setLayoutY(45);

                        text_numbercared[2].setLayoutX(645);
                        text_numbercared[2].setLayoutY(70);

                        break;
                    case 3:
                        picture_player[3].setLayoutX(905);
                        picture_player[3].setLayoutY(38);
                        textplayer_name[3].setLayoutX(750);
                        textplayer_name[3].setLayoutY(180);
                        picture_backplayer[3].setLayoutX(1100);
                        picture_backplayer[3].setLayoutY(45);

                        text_numbercared[3].setLayoutX(1180);
                        text_numbercared[3].setLayoutY(70);

                        break;
                    case 4:
                        picture_player[4].setLayoutX(1420);
                        picture_player[4].setLayoutY(120);
                        textplayer_name[4].setLayoutX(1150);
                        textplayer_name[4].setLayoutY(246);
                        picture_backplayer[4].setLayoutX(1618);
                        picture_backplayer[4].setLayoutY(132);

                        text_numbercared[4].setLayoutX(1695);
                        text_numbercared[4].setLayoutY(166);

                        break;
                    case 5:
                        picture_player[5].setLayoutX(1495);
                        picture_player[5].setLayoutY(683);
                        textplayer_name[5].setLayoutX(1150);
                        textplayer_name[5].setLayoutY(600);
                        picture_backplayer[5].setLayoutX(1686);
                        picture_backplayer[5].setLayoutY(724);

                        text_numbercared[5].setLayoutX(1767);
                        text_numbercared[5].setLayoutY(763);

                        break;
                    default:
                        break;
                }
            }
        }

    }

    private void startGame() throws MqttException, InterruptedException {
        JSONObject msg = new JSONObject();
        msg.put("typeUpdate", "isStart");
        msg.put("status", "true");
        lobby.sendMessage(msg.toJSONString());
        lobby.startGame();
    }

    private boolean checkIsStart() {
        host = lobby.isIsStart();
        return lobby.isIsStart();
    }

    private void stopConnection() throws MqttException {
        lobby.disconnect();
    }

    private int getPlayerInLobby() {
        return lobby.getPlayerInLobby();
    }

    private void checkmouseposition() {
        gameScene.setOnMouseMoved((event)
                -> {
            String msg
                    = "(x: " + event.getX() + ", y: " + event.getY() + ") -- "
                    + "(sceneX: " + event.getSceneX() + ", sceneY: " + event.getSceneY() + ") -- "
                    + "(screenX: " + event.getScreenX() + ", screenY: " + event.getScreenY() + ")";

            //System.out.println(msg);
        });
    }

    private void updatePlayerInSeat() {
        for (int i = 0; i < lobby.playerNames.size(); i++) {
            player_name[i] = lobby.playerNames.get(i);
        }
    }
    
    private void updateIsmyTurn() {
        if (Game.turnList.get(0).equals(Game.playerName)){
            Card_1.setLayoutX(1100);
            Card_1.setLayoutY(900);
            Card_2.setLayoutX(1300);
            Card_2.setLayoutY(900);
            Card_3.setLayoutX(1500);
            Card_3.setLayoutY(900);
            Card_5.setLayoutX(1100);
            Card_5.setLayoutY(950);
            draw_end.setLayoutX(1300);
            draw_end.setLayoutY(950);
        }
        else {
            Card_1.setLayoutX(0);
            Card_1.setLayoutY(-200);
            Card_2.setLayoutX(0);
            Card_2.setLayoutY(-200);
            Card_3.setLayoutX(0);
            Card_3.setLayoutY(-200);
            Card_5.setLayoutX(0);
            Card_5.setLayoutY(-200);
            draw_end.setLayoutX(0);
            draw_end.setLayoutY(-200);
        }
    }

    private void updatePlayerHand() {
        for (int i = 0; i < Game.players.size(); i++) {
            number_cardplayer[i] = Game.players.get(i).getHand().cards.size();
        }
    }

    private void Deckgamestart() {
        for (int i = 0; i < Game.players.size(); i++) {
            if (Game.playerName.equals(Game.players.get(i).getPlayerName())) {
                deck_player = Game.players.get(i).getHand();
            }
        }
    }

    private void CreateimageCard(Deck deck) {
        int j = 0;
        for (int i = 0; i < deck.cards.size(); i++) {
            ImageView picture_card = new ImageView(deck.cards.get(i).getImage());
            picture_card.setLayoutX(450 + i * 150);

            if (i >= 7) {
                picture_card.setLayoutY(300 + 150);
                picture_card.setLayoutX(450 + j * 150);
                j++;
            } else {
                picture_card.setLayoutY(300);
            }
            gamePane.getChildren().add(picture_card);
        }
    }

    private void gamestartnow() {
        createButton();
    }

    private void createButton() {
        Card_1 = new SpaceRunnerButton("1 CARD", 49, 190);
        Card_2 = new SpaceRunnerButton("2 CARD", 49, 190);
        Card_3 = new SpaceRunnerButton("3 CARD", 49, 190);
        Card_5 = new SpaceRunnerButton("5 CARD", 49, 190);
        draw_end = new SpaceRunnerButton("DRAW AND END", 49, 250);

        Card_1.setLayoutX(0);
        Card_1.setLayoutY(-200);
        Card_1.setOnAction((event)
                -> {

        });

        Card_2.setLayoutX(0);
        Card_2.setLayoutY(-200);
        Card_2.setOnAction((event)
                -> {

        });

        Card_3.setLayoutX(0);
        Card_3.setLayoutY(-200);
        Card_3.setOnAction((event)
                -> {

        });

        Card_5.setLayoutX(0);
        Card_5.setLayoutY(-200);
        Card_5.setOnAction((event)
                -> {

        });

        draw_end.setLayoutX(0);
        draw_end.setLayoutY(-200);
        draw_end.setOnAction((ActionEvent event)
                -> {
            try {
                lobby.game.drawCard();
                TimeUnit.SECONDS.sleep(1);
                lobby.game.turnHaveEnd();
            } catch (MqttException | InterruptedException ex) {
                Logger.getLogger(LobbyViewManger.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        gamePane.getChildren().add(draw_end);
        gamePane.getChildren().add(Card_5);
        gamePane.getChildren().add(Card_3);
        gamePane.getChildren().add(Card_2);
        gamePane.getChildren().add(Card_1);
    }
}
