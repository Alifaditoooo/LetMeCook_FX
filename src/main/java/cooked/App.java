package cooked;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;
    public static User currentUser; 
    public static Resep resepDetail; 

    @Override
    public void start(Stage stage) throws Exception {
        scene = new Scene(loadFXML("Home"), 1366, 768);
        stage.setScene(scene);
        stage.setTitle("Let Me Cook App");
        stage.setMaximized(true);
        stage.show();
    }

    private static Parent loadFXML(String fxml) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void setRoot(String fxml) throws Exception {
        scene.setRoot(loadFXML(fxml));
    }

    public static void main(String[] args) {
        launch();
    }
}