package exploding_meme_full;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class HowtoplayViewManger {
    private AnchorPane gamePane;
    private Scene gameScene;
    private Stage gameStage;

    private static final int GAME_WIDTH =  1920 ;
    private static final int GAME_HIGHT =1080;

    private int ggg;
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
    }
    
}
