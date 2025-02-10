package dev.thorny;

import java.io.IOException;

import dev.thorny.data.Languages;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class SecondaryController {

    ObservableList<Languages> languageChoiceList = FXCollections.observableArrayList(Languages.EN, Languages.JP, Languages.CHS, Languages.KR);

    @FXML
    private ChoiceBox<Languages> languageChoiceBox;

    @FXML
    private Button confirmButton;

    @FXML
    private TextField gamePathText;

    @FXML
    private void initialize() {
        languageChoiceBox.setValue(App.getCurrentLang());
        languageChoiceBox.setItems(languageChoiceList);
        gamePathText.setText(App.getPrefs().getGamePath());
    }

    @FXML
    private void pickLanguage(ActionEvent event) throws IOException {
        App.setCurrentLang((Languages) languageChoiceBox.getValue());

        switchToPrimary();
    }

    @FXML
    private void pickGameExecutable(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter
                = new FileChooser.ExtensionFilter("Executable file", "*.exe");
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setTitle("Select game...");

        final var game = fileChooser.showOpenDialog(App.getStage());
        gamePathText.setText(game.getAbsolutePath());
        App.setGamePathInPrefs(game.getAbsolutePath());
    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}
