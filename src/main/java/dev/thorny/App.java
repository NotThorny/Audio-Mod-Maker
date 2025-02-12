package dev.thorny;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import dev.thorny.data.Languages;
import dev.thorny.user.Preferences;
import dev.thorny.user.User;
import dev.thorny.utils.FileIO;
import dev.thorny.utils.WWiseHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * HAMM - Hoyo Audio Mod Maker
 * This is an app that takes a given audio file (mp3, wav, ogg, flac, etc)
 * and converts it into an audio mod ready to be used. Can apply
 * the mod immediately on completion.
 */
public class App extends Application {

    private static Scene scene;
    private static Stage stage;
    private static User user;
    private static Preferences prefs;

    @Override
    public void start(Stage passedStage) throws IOException {
        // Init user and prefs
        user = new User();
        prefs = FileIO.readPreferences();

        // Check if language needs to be set and show scene
        if (prefs.isFirstStartup()) {
            scene = new Scene(loadFXML("secondary"), 1100, 700);
            prefs.setFirstStartup(false);
            FileIO.savePreferences(prefs);
        } else {
            scene = new Scene(loadFXML("primary"), 1100, 700);
        }

        // Show the app
        scene.getStylesheets().add(getClass().getResource("/dev/thorny/style.css").toExternalForm());
        passedStage.setScene(scene);
        passedStage.getIcons().add(new Image(getClass().getResourceAsStream("/dev/thorny/icon.png")));
        passedStage.setTitle("Hoyo Audio Mod Maker");
        passedStage.show();
        stage = passedStage;

        // Set unresizable
        stage.resizableProperty().setValue(false);

        // Read hash files after to prevent "lag" in displaying if io is delayed
        FileIO.readLanguageFile(prefs.getLanguage());

        if(!WWiseHandler.isWwiseInstalled()) {
            displayWwiseConfirmation();
        }

        // Generate new hashes
        // FileIO.generateCombatHashesInIndexRoots();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static Scene getScene() {
        return scene;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setCurrentLang(Languages lang) {
        prefs.setLanguage(lang);
        FileIO.readLanguageFile(prefs.getLanguage());
    }

    public static Languages getCurrentLang() {
        return prefs.getLanguage();
    }

    public static User getUser() {
        return user;
    }

    public static Preferences getPrefs() {
        return prefs;
    }

    public static boolean displayConfirmation(String message) {
        return displayAlert(message, AlertType.CONFIRMATION);
    }

    public static void displayInfo(String message) {
        displayAlert(message, AlertType.INFORMATION);
    }

    public static void displayError(String error) {
        displayAlert(error, AlertType.ERROR);
    }

    // Display an alert with a given message
    public static boolean displayAlert(String message, AlertType type) {
        Alert alert = new Alert(type, message);

        // Size it to fit the text
        alert.setResizable(true);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        if (type.equals(AlertType.CONFIRMATION)) {
            return alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .isPresent();
        }

        // Display
        alert.show();
        return true;
    }

    // Alert the user that WWise is not installed, but is required
    public static void displayWwiseConfirmation() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("WWise Installation Required");
        alert.setHeaderText("Required: WWise");
        alert.setContentText(
                "It appears you do not have WWise installed! It is required to convert audio.\nWould you like to download it now?");

        alert.setResizable(true);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(_ -> {
                    // Run in new thread
                    new Thread(() -> {
                        if (FileIO.downloadFile("https://gitlab.com/ytnshio/ebi/-/raw/main/WWIse.zip")) {
                            if (FileIO.unzipFile(new File("./resources/WWIse.zip"))) {
                                //FileIO.updateEnv("WWISEROOT", new File("resources/WWIse/").getAbsolutePath());
                                FileIO.deleteFile("./resources/WWIse.zip");
                            }
                        }
                    }).start();
                });  
    }

    public static void openURL(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url.toString()));
        } catch (IOException | URISyntaxException e) {
            displayAlert("Unable to open URL! Please report this issue with context on what you were doing.", AlertType.ERROR);
        }
    }

    public static void setGamePathInPrefs(String path) {
        prefs.setGamePath(path);
        FileIO.savePreferences(prefs);
    }

    public static void displayErrorOnFXThread(String message) {
        Platform.runLater(() -> {
            displayError(message);
        });
    }

}
