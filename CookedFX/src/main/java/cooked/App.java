package cooked;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException; 

public class App extends Application {

    private static Scene scene;
    private static Stage mainStage; 
    
    
    public static String usernameProfil; 
    public static Resep resepDetail;     

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        scene = new Scene(loadFXML("Home")); 
        stage.setScene(scene);
        stage.setTitle("Let Me Cook App");
        stage.setMaximized(true); 
        stage.show();
    }

    
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        if (mainStage != null) {
            mainStage.setMaximized(true);
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}