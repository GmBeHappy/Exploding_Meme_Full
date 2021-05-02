package exploding_meme_full;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
// import model.RenewButton;
// import model.ShipPicker;
 import model.SpaceRunnerButton;
// import model.SHIP;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;



public class ViewManger {

    private static final int HEIGHT = 1080;
    private static final int WIDTH =  1920 ;// 1366 768    768    1024  1280 768
    private AnchorPane mainPane;
    private Scene mainScene;
    private Stage mainStage;


     private GameSubScene helpSubScene;
     private GameSubScene startSubScene;

     private GameSubScene JoinSubScene;
     private GameSubScene CreateSubScene;

     private GameSubScene sceneToHide;

     private TextField EnterName_create = new TextField();
     private TextField EnterName_join = new TextField();
     private TextField Enterpass_join = new TextField();

    //List<ShipPicker> shipsList;
  //  private SHIP choosenShip;


    private AnimationTimer TimerLoop;
    private GridPane gridPane1;
    private GridPane gridPane2;
    
    Lobby lobby;

    
    public ViewManger() throws FileNotFoundException 
    {
        mainPane = new AnchorPane();

        mainScene= new Scene(mainPane,WIDTH,HEIGHT);

        mainStage = new Stage();
        mainStage.setScene(mainScene);

        createBackground();
        createButtons();
        createLogo();
        createNameGame();
        createSubscens();
        createGameLoop();

        
    }

    private void showSubScene(GameSubScene subScene)
    {
        if(sceneToHide != null )
        {
            sceneToHide.moveSubScence();
        }
        if(subScene != sceneToHide)
        {
            subScene.moveSubScence();
            sceneToHide = subScene;
        }
        else
        {
            sceneToHide = null;
        }
      
    }


    public void createSubscens() throws FileNotFoundException
    {
        helpSubScene = new GameSubScene();
        mainPane.getChildren().add(helpSubScene);

        JoinSubScene = new GameSubScene();
        mainPane.getChildren().add(JoinSubScene);


        create_CreateSubscene();
        create_JoinSubscene();


      //  createShipChooserSubScene();
        
     }


     private void create_CreateSubscene()  throws FileNotFoundException
    {
        CreateSubScene = new GameSubScene();
        mainPane.getChildren().add(CreateSubScene);

         InfoLabel chooseShipLabel1 = new InfoLabel("START YOUR GAME");
         chooseShipLabel1.setLayoutX(70);
         chooseShipLabel1.setLayoutY(25);
         CreateSubScene.getPane().getChildren().add(chooseShipLabel1);

         
         EnterName_create.setLayoutX(180);
         EnterName_create.setLayoutY(150);
         CreateSubScene.getPane().getChildren().add(EnterName_create);


         CreateSubScene.getPane().getChildren().add(createButtonToCreate());


         Text text_name = new Text();
         text_name.setText("NAME");
         text_name.setFont(Font.loadFont("file:src/exploding_meme_full/resource/kenvector_future.ttf",15) );
         text_name.setX(100);
         text_name.setY(170);
         CreateSubScene.getPane().getChildren().add(text_name);
    }


    private void create_JoinSubscene() throws FileNotFoundException
    {
        JoinSubScene = new GameSubScene();
        mainPane.getChildren().add(JoinSubScene);

         InfoLabel chooseShipLabel1 = new InfoLabel("START YOUR GAME");
         chooseShipLabel1.setLayoutX(70);
         chooseShipLabel1.setLayoutY(25);
         JoinSubScene.getPane().getChildren().add(chooseShipLabel1);

         Text text_name = new Text();
         text_name.setText("NAME");
         text_name.setFont(Font.loadFont("file:src/exploding_meme_full/resource/kenvector_future.ttf",15) );
         text_name.setX(100);
         text_name.setY(170);
         JoinSubScene.getPane().getChildren().add(text_name);


         Text text_pass = new Text();
         text_pass.setText("PASS");
         text_pass.setFont(Font.loadFont("file:src/exploding_meme_full/resource/kenvector_future.ttf",15) );
         text_pass.setX(100);
         text_pass.setY(220);
         JoinSubScene.getPane().getChildren().add(text_pass);


         EnterName_join.setLayoutX(180);
         EnterName_join.setLayoutY(150);
         Enterpass_join.setLayoutX(180);
         Enterpass_join.setLayoutY(200);
         JoinSubScene.getPane().getChildren().add(EnterName_join);
         JoinSubScene.getPane().getChildren().add(Enterpass_join);


         JoinSubScene.getPane().getChildren().add(createButtonToJoin());
    }

    // private void createShipChooserSubScene()
    // {
    //     startSubScene = new GameSubScene();
    //     mainPane.getChildren().add(startSubScene);

    //      InfoLabel chooseShipLabel1 = new InfoLabel("START YOUR GAME");
    //      chooseShipLabel1.setLayoutX(70);
    //      chooseShipLabel1.setLayoutY(25);
    //      startSubScene.getPane().getChildren().add(chooseShipLabel1);
    //     //startSubScene.getPane().getChildren().add(createShipsToChoose());
    //      startSubScene.getPane().getChildren().add(createButtonToCreate());
    //      startSubScene.getPane().getChildren().add(createButtonToJoin());


         
    //      EnterName.setLayoutX(180);
    //      EnterName.setLayoutY(150);
    //      startSubScene.getPane().getChildren().add(EnterName);
    // }

    // private HBox createShipsToChoose()
    // {
    //     HBox box = new HBox();
    //     box.setSpacing(20);
    //     shipsList = new ArrayList<>();
    //     for(SHIP ship : SHIP.values())
    //     {
    //         ShipPicker shipToPick = new ShipPicker(ship);
    //         shipsList.add(shipToPick);
    //         box.getChildren().add(shipToPick);
    //         shipToPick.setOnMouseClicked((event) -> 
    //         {
    //             for(ShipPicker ship2 : shipsList)
    //             {
    //                 ship2.setIsCircleChoosen(false);
    //             }
    //             shipToPick.setIsCircleChoosen(true);
    //             choosenShip = shipToPick.getShip();
    //         });
           
    //     }
    //     box.setLayoutX(300-(118*2));
    //     box.setLayoutY(100);
    //     return box;
    // }

    private SpaceRunnerButton createButtonToCreate()
    {
        SpaceRunnerButton CreateButton = new SpaceRunnerButton("CREATE",49,190);
        CreateButton.setLayoutX(170); //350
        CreateButton.setLayoutY(370);

        

        CreateButton.setOnAction((event) -> 
        {
            String name = EnterName_create.getText();
            if(name.equals(""))
            {   
            }
            else
            {
                System.out.println(name);
                try {
                    lobby = new Lobby(name);
                } catch (MqttException ex) {
                    Logger.getLogger(ViewManger.class.getName()).log(Level.SEVERE, null, ex);
                }
                //lobby.startGame();
                LobbyViewManger lobbyviewManager = new LobbyViewManger();
                try {
                    lobbyviewManager.create(mainStage,true);
                } catch (Exception ex) {
                    Logger.getLogger(ViewManger.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        return CreateButton;
    }

    private SpaceRunnerButton createButtonToJoin()
    {
        SpaceRunnerButton JoinButton = new SpaceRunnerButton("JOIN",49,190);
        JoinButton.setLayoutX(170); //350
        JoinButton.setLayoutY(370);

        

        JoinButton.setOnAction((event) -> 
        {
            String name = EnterName_join.getText();
            String code = Enterpass_join.getText();
            if(name.equals("") && code.equals("") )
            {  

            }
            else
            {
                System.out.println(name);
                System.out.println(code);
                try {
                    lobby = new Lobby(name, code);
                } catch (MqttException ex) {
                    Logger.getLogger(ViewManger.class.getName()).log(Level.SEVERE, null, ex);
                }
                LobbyViewManger lobbyviewManager = new LobbyViewManger();
                try {
                    lobbyviewManager.create(mainStage,false);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ViewManger.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        return JoinButton;
    }




    public Stage getMainStage() {
        return mainStage;
    }

    private void createButtons()
    {
        createCREATEBUTTON();
        createJOINBUTTON();
        createHelpButton();
        createExitButton();
    }
    
    private ImageView createModelButton(String URL,int x,int y)
    {
        ImageView button = new ImageView(URL);
        button.setLayoutX(x);
        button.setLayoutY(y);

        button.setOnMouseEntered((event) -> 
        {
            button.setEffect(new Bloom());
        });
        button.setOnMouseExited((event) -> 
        {
            button.setEffect(null);
        });

        button.setOnMousePressed((event) -> 
        {
            button.setLayoutY(button.getLayoutY()+4);
        });

        button.setOnMouseReleased((event) -> 
        {
            button.setLayoutY(button.getLayoutY()-4);
        });
        mainPane.getChildren().add(button);

        return button;
    }

    private void createCREATEBUTTON()
    {
        createModelButton("exploding_meme_full/resource/CREATEBUTTON.png",880,700).setOnMouseClicked((event) -> 
        {
            showSubScene(CreateSubScene);
        });
    }

    private void createJOINBUTTON()
    {
        createModelButton("exploding_meme_full/resource/JOINBUTTON.png",880,780).setOnMouseClicked((event) -> 
        {
            
            showSubScene(JoinSubScene);
           
        });
    }
   
    private void createHelpButton()
    {
        createModelButton("exploding_meme_full/resource/HELPBUTTON.png",850,860).setOnMouseClicked((event) -> 
        {
           HowtoplayViewManger howtoplayviewManager = new HowtoplayViewManger();
           howtoplayviewManager.create(mainStage);
        });
    }

    private void createExitButton()
    {
        createModelButton("exploding_meme_full/resource/EXITBUTTON.png",850,940).setOnMouseClicked((event) -> 
        {
           mainStage.close();
        });
    }

    private void createBackground()
    {
        gridPane1 = new GridPane();
        gridPane2 = new GridPane();

        for(int i = 0 ;i<100;i++)
        {
            ImageView backgroImageView1 = new ImageView("exploding_meme_full/resource/BlueRenew.jpg");
            ImageView backgroImageView2 = new ImageView("exploding_meme_full/resource/BlueRenew.jpg");
            GridPane.setConstraints(backgroImageView1, i%10,i/10);
            GridPane.setConstraints(backgroImageView2, i%10,i/10);
            gridPane1.getChildren().add(backgroImageView1);
            gridPane2.getChildren().add(backgroImageView2);
        }

        gridPane2.setLayoutY(-1920);

        mainPane.getChildren().addAll(gridPane1,gridPane2);

    }
    
    private void createLogo()
    {
        ImageView logo = new ImageView("exploding_meme_full/resource/logoCatTNT02.png");
        logo.setLayoutX(-10);
        logo.setLayoutY(-20);


        logo.setOnMouseEntered((event) -> 
        {
           logo. setEffect(new Bloom());

          
        });

        logo.setOnMouseExited((event) -> 
        {
           logo.setEffect(null);
        });

        mainPane.getChildren().add(logo);
    }

    private void createNameGame()
    {
        ImageView logo = new ImageView("exploding_meme_full/resource/Level2.png");
        logo.setLayoutX(750);
        logo.setLayoutY(0);


        logo.setOnMouseEntered((event) -> 
        {
           logo.setEffect(new Bloom());

        });

        logo.setOnMouseExited((event) -> 
        {
           logo.setEffect(null);
        });
        mainPane.getChildren().add(logo);
    }

    private void moveBackground()
    {
        gridPane1.setLayoutY(gridPane1.getLayoutY()+0.5);
        gridPane2.setLayoutY(gridPane2.getLayoutY()+0.5);

        if(gridPane1.getLayoutY() >= 1920)
        {
            gridPane1.setLayoutY(-1920);
        }

        if(gridPane2.getLayoutY()>=1920)
        {
            gridPane2.setLayoutY(-1920);
        }
    }


    private void createGameLoop()
    {
        TimerLoop = new AnimationTimer(){

            @Override
            public void handle(long now) {    
                moveBackground();
               // checkmouseposition();
            }
            
        };
        TimerLoop.start();
    }

    private void checkmouseposition()
    {
        mainScene.setOnMouseMoved((event) -> 
        {
            String msg =
            "(x: "       + event.getX()      + ", y: "       + event.getY()       + ") -- " +
            "(sceneX: "  + event.getSceneX() + ", sceneY: "  + event.getSceneY()  + ") -- " +
            "(screenX: " + event.getScreenX()+ ", screenY: " + event.getScreenY() + ")";

           System.out.println(msg);
        });
    }

   
        
    


}
