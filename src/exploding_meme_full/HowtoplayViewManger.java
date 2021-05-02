package exploding_meme_full;


//"file:src/exploding_meme_full/resource/kenvector_future.ttf"

import java.io.FileNotFoundException;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.SpaceRunnerButton;

public class HowtoplayViewManger {
    private AnimationTimer TimerLoop;

    private GridPane gridPane1;
    private GridPane gridPane2;

    private AnchorPane gamePane;
    private Scene gameScene;
    private Stage gameStage;

    private static final int GAME_WIDTH =  1920 ;
    private static final int GAME_HIGHT =1080;

    private Stage menuStage;

    public HowtoplayViewManger()
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

    public void create(Stage menuStage)
    {
        this.menuStage = menuStage;
        this.menuStage.hide();
        gameStage.show();

        createBackground();

        createButtons();


        createGameLoop();
    }
    
    private void createButtons()
    {
        createBACKBUTTON();
        
    }


    private SpaceRunnerButton createBACKBUTTON()
    {
        SpaceRunnerButton CreateButton = new SpaceRunnerButton("CREATE",49,190);
        CreateButton.setLayoutX(170); //350
        CreateButton.setLayoutY(370);

        

        CreateButton.setOnAction((event) -> 
        {
            try {
                ViewManger manger = new ViewManger();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        return CreateButton;
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

    private void createBackground()
    {
        gridPane1 = new GridPane();
        gridPane2 = new GridPane();

        for(int i = 0 ;i<100;i++)
        {
            ImageView backgroImageView1 = new ImageView("file:src/exploding_meme_full/resource/BlueRenew.jpg");
            ImageView backgroImageView2 = new ImageView("file:src/exploding_meme_full/resource/BlueRenew.jpg");
            GridPane.setConstraints(backgroImageView1, i%10,i/10);
            GridPane.setConstraints(backgroImageView2, i%10,i/10);
            gridPane1.getChildren().add(backgroImageView1);
            gridPane2.getChildren().add(backgroImageView2);
        }

        gridPane2.setLayoutY(-1920);

        gamePane.getChildren().addAll(gridPane1,gridPane2);

    }
}
