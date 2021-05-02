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
import model.SpaceRunnerButton;

public class LobbyViewManger 
{
    private Boolean host;

    private ImageView[] picture_player = new ImageView[6];
  
  
        
    private InfoLabel chooseShipLabel1;
    
    
    private InfoLabel[] textplayer_name = new InfoLabel[6];
    
    private String[] player_name ={"1","2","3","4","5","6"} ;
    
    
    
    private int number_people = 0;

    private SpaceRunnerButton CreateButtonWait = new SpaceRunnerButton("WAIT",49,190);;
    private SpaceRunnerButton CreateButtonSTART = new SpaceRunnerButton("START",49,190);;
    
    private Boolean gamestart = false;

    private AnchorPane gamePane;
    private Scene gameScene;
    private Stage gameStage;


    private AnimationTimer TimerLoop;


    private static final int GAME_WIDTH =  1920 ;
    private static final int GAME_HIGHT =1080;
    private final static String BACKGROUND_IMAGE = "exploding_meme_full/resource/pokertable.png";

    private GridPane gridPane;

    private Stage menuStage;

    public LobbyViewManger()
    {
        initializeStage();

    }

    private void initializeStage()
    {
        gamePane = new AnchorPane();
        gameScene = new Scene(gamePane,GAME_WIDTH,GAME_HIGHT);
        gameStage = new Stage();
        gameStage.setScene(gameScene);
    }

    public void create(Stage menuStage,Boolean host) throws FileNotFoundException
    {
        this.host = host;
        this.menuStage = menuStage;
        this.menuStage.hide();
        createBackground();

        if(host == true)
        {
            setButtonSTART(1700,900);
            gamePane.getChildren().add(CreateButtonSTART);
        }
        else 
        {
            setButtonWait(1700,900);
            gamePane.getChildren().add(CreateButtonWait);
        }

        chooseShipLabel1 = new InfoLabel(String.format("%d / 6",number_people));
        chooseShipLabel1.setLayoutX(1500);
        chooseShipLabel1.setLayoutY(25);
        gamePane.getChildren().add(chooseShipLabel1);

        for(int i =0;i<6;i++)
        {
            textplayer_name[i] = new InfoLabel(player_name[i]);
            gamePane.getChildren().add(textplayer_name[i]);
            
            picture_player[i] = new ImageView("exploding_meme_full/resource/playe.png");
            gamePane.getChildren().add(picture_player[i]);
        }
      
        textplayer_name[0].setLayoutX(350);
        textplayer_name[0].setLayoutY(246);
        
        textplayer_name[1].setLayoutX(750);
        textplayer_name[1].setLayoutY(180);
        
        textplayer_name[2].setLayoutX(1150);
        textplayer_name[2].setLayoutY(246);
        
        textplayer_name[3].setLayoutX(1150);
        textplayer_name[3].setLayoutY(600);
        
        textplayer_name[4].setLayoutX(750);
        textplayer_name[4].setLayoutY(693);
        
        textplayer_name[5].setLayoutX(400);
        textplayer_name[5].setLayoutY(600);
        
        picture_player[0].setLayoutX(384);
        picture_player[0].setLayoutY(109);

        


        createGameLoop();
        gameStage.show();
    }

    private void createBackground()
    {
        gridPane = new GridPane();

        ImageView backgroImageView1 = new ImageView(BACKGROUND_IMAGE);
        gridPane.getChildren().add(backgroImageView1);

        
        gridPane.setLayoutX(280);
        gridPane.setLayoutY(50);

        gamePane.getChildren().add(gridPane);
    }

    private void setButtonWait(int x,int y)
    {
        CreateButtonWait.setLayoutX(x); 
        CreateButtonWait.setLayoutY(y);

        CreateButtonWait.setOnAction((event) -> 
        {
           
        });
        
    }

    private void setButtonSTART(int x,int y)
    {
        CreateButtonSTART.setLayoutX(x); 
        CreateButtonSTART.setLayoutY(y);

        CreateButtonSTART.setOnAction((event) -> 
        {
           gamestart = true;
        });
        
    }

    private void createGameLoop()
    {
        TimerLoop = new AnimationTimer(){

            @Override
            public void handle(long now) 
            {    
                number_people++;
                checkgamestart();
                textpeople();
                checkmouseposition();
            }
            
        };
        TimerLoop.start();
    }

    private void checkgamestart()
    {
        if(host == true)
        {
            if(gamestart == true)
            {
                setButtonSTART(-100,-100);
            } 
        }
        else 
        {
            if(gamestart == true)
            {
                setButtonWait(-100,-100);
            } 
        }
    }


    private void textpeople()
    {
        chooseShipLabel1.setText(String.format("%d / 6",number_people));
        for(int i =0;i<6;i++)
        {
            textplayer_name[i].setText(player_name[i]);
        }
        
    }
    
    
    
    
     private void checkmouseposition()
    {
        gameScene.setOnMouseMoved((event) -> 
        {
            String msg =
            "(x: "       + event.getX()      + ", y: "       + event.getY()       + ") -- " +
            "(sceneX: "  + event.getSceneX() + ", sceneY: "  + event.getSceneY()  + ") -- " +
            "(screenX: " + event.getScreenX()+ ", screenY: " + event.getScreenY() + ")";

           System.out.println(msg);
        });
    }
     
     
}