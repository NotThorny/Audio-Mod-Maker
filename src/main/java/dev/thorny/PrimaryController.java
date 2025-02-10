package dev.thorny;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.io.FilenameUtils;

import dev.thorny.data.AudioDetails;
import dev.thorny.data.Avatar;
import dev.thorny.user.Audio;
import dev.thorny.user.UserAudio;
import dev.thorny.utils.AudioConverter;
import dev.thorny.utils.FileIO;
import dev.thorny.utils.PckHandler;
import dev.thorny.utils.WWiseHandler;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

public class PrimaryController implements Initializable {

    @FXML
    private Hyperlink gamebanana;

    @FXML
    private Hyperlink github;

    @FXML
    private ListView<UserAudio> userFileList;

    @FXML
    private Button makeModButton;

    @FXML
    private ListView<Audio> replacementList;

    private ObservableList<String> userFiles = FXCollections.observableArrayList();

    @FXML
    private TreeTableView<Audio> avatarTree = new TreeTableView<>();

    @FXML
    private TreeTableColumn<Audio, String> treeSourceName;

    @FXML
    private TreeTableColumn<Audio, String> treeVoiceContent;

    @FXML
    private TreeTableColumn<Audio, String> treeReplacing;

    @FXML
    private TreeTableColumn<Audio, String> treeHash;

    @FXML
    private TextFlow updateTextFlow;

    @FXML
    private Text updateText;

    @FXML
    private Text updateTextBody;

    @FXML
    private CheckBox saveSeparatelyCheckbox;

    @FXML
    private CheckBox applyModCheckbox;

    private TreeItem<Audio> avatarRootTreeItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Init root
        avatarRootTreeItem = new TreeItem<>(
                new Audio(new AudioDetails("", "", "", "Name"), new UserAudio("File name", new File("."))));

        // Add default option to mute
        userFiles.add("Mute sound");
        App.getUser().getSounds().put("Mute sound", new File("./wems/Windows/mute.wem"));
        userFiles.add("Clear selection");

        loadSoundsList();

        avatarTree.setEditable(true);
        treeReplacing.setEditable(true);

        updateText.setText("Progress: \n");
        updateText.setFont(Font.font("Tahoma", 24.0));
        updateText.setFill(Paint.valueOf("#ea6962"));
        updateText.setUnderline(true);

        updateTextBody.setText("Creating mod...");
        updateTextBody.setFill(Paint.valueOf("#E9E9DE"));
        updateTextBody.setFont(Font.font("Tahoma", 14.0));

        initFactoriesListeners();
    }

    private void initFactoriesListeners() {
        /*
         * Set up cell factories and event listeners.
         */
        userFileList.setCellFactory(_ -> new ListCell<UserAudio>() {
            @Override
            protected void updateItem(UserAudio audio, boolean empty) {
                super.updateItem(audio, empty);

                if (empty || audio == null || audio.getName() == null) {
                    setText(null);
                } else {
                    var name = audio.getFile().getName();
                    setText(name);

                    // Create context menu
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem removeSound = new MenuItem();
                    removeSound.textProperty().bind(Bindings.format("Remove \"%s\"", name));
                    removeSound.setOnAction(_ -> {
                        userFileList.getItems().remove(getItem());
                        userFiles.remove(name);
                    });

                    contextMenu.getItems().add(removeSound);
                    emptyProperty().addListener((_, _, newVal) -> {
                        if (newVal) {
                            setContextMenu(null);
                        } else {
                            setContextMenu(contextMenu);
                        }
                    });

                    setContextMenu(contextMenu);
                }
            }
        });

        userFileList.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.ANY);
                event.consume();
            }
        });

        userFileList.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                var items = (ArrayList<File>)event.getDragboard().getContent(DataFormat.FILES);
                var array = items.stream().filter(f -> f.getName().matches(".*\\.(flac|m(?:4a|p3|ov|p4)|ogg|wav|aac)$")).toList();
                App.getUser().addUserSound(array);
                addUserFileIfNotPresent(array);
                onAppStarted();
                event.setDropCompleted(true);
                event.consume();
            }});

        replacementList.setCellFactory(_ -> new ListCell<Audio>() {
            @Override
            protected void updateItem(Audio audio, boolean empty) {
                super.updateItem(audio, empty);

                if (empty || audio == null || audio.getDetails() == null) {
                    setText(null);
                } else {
                    setText(audio.getUserAudio().getFile().getName() + " -> " + audio.getDetails().getSourceName().getValueSafe());

                    // Create context menu
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem removeSound = new MenuItem();
                    removeSound.textProperty().bind(Bindings.format("Remove \"%s\"", audio.getDetails().getSourceName().getValueSafe()));
                    removeSound.setOnAction(e -> {
                        removeSoundReplacement(audio);
                    });

                    contextMenu.getItems().add(removeSound);
                    emptyProperty().addListener((_, _, newVal) -> {
                        if (newVal) {
                            setContextMenu(null);
                        } else {
                            setContextMenu(contextMenu);
                        }
                    });

                    setContextMenu(contextMenu);
                }
            }
        });

        treeSourceName.setCellValueFactory(param -> {
            // Replace internal names with common names
            String name = "";
            // This is unnecessary but shortens the junk at the beginning of the source.
            String fullSource = param.getValue().getValue().getDetails().getSourceName().getValueSafe().toLowerCase();
            if (fullSource.length() >= 15) {
                String shorterSource = fullSource.substring(3, fullSource.length()-4);
                if (!(shorterSource.indexOf("_") == -1)) {
                    return new SimpleStringProperty(shorterSource.substring(shorterSource.indexOf("_") + 1));
                }
            }

            final String paramName = param.getValue().getValue().getDetails().getSourceName().getValueSafe().toLowerCase();
            switch (paramName) {
                case "hero" -> name = "aether";
                case "heroine" -> name = "lumine";
                case "ambor" -> name = "amber";
                case "qin" -> name = "jean";
                default -> {
                    return param.getValue().getValue().getDetails().getSourceName();
                }
            }

            return new SimpleStringProperty(name);
        });

        treeVoiceContent.setCellValueFactory(param -> param.getValue().getValue().getDetails().getVoiceContent());
        treeReplacing.setCellFactory(ComboBoxTreeTableCell.forTreeTableColumn(userFiles));

        treeReplacing.setCellValueFactory(data -> {
            if (replacementList.getItems().isEmpty()) {
                data.getValue().getValue().getUserAudio().setName("");
            }
            if (!replacementList.getItems().contains(data.getValue().getValue())) {
                data.getValue().getValue().getUserAudio().setName("");
            }
            return data.getValue().getValue().getUserAudio().nameProperty();
        });

        treeReplacing.setOnEditCommit(event -> userSelectedReplacement(event));
        treeHash.setCellValueFactory(param -> param.getValue().getValue().getDetails().getHash());

        avatarTree.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {

                /*
                 * Validation of selection is necessary. Without this,
                 * clicking on text will result in the text being taken
                 * instead of the node it belongs to.
                 */
                Node target = (Node) e.getTarget();

                // Until the proper node, or can no longer go up the chain any further
                while (target != null && !(target.getClass().isInstance(new TreeTableRow<>()))) {
                    target = target.getParent();
                }

                TreeTableRow<Audio> row = (TreeTableRow) target;

                // Sanity
                if (row == null) {
                    return;
                }

                avatarTree.edit(row.getIndex(), treeReplacing);

                TreeItem<Audio> item = avatarTree.getTreeItem(row.getIndex());
                item.setExpanded(!item.isExpanded());
            }
        });

        makeModButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (App.getUser().getModifiedSounds().isEmpty()) {
                    App.displayInfo("No replacements selected! Please select at least one replacement to the left to make a mod.");
                    return;
                }

                makeModButton.setDisable(true);
                makeModButton.setText("Working");
                changeTextFlowVisibility();

                if (!FileIO.backupExists()) {
                    if (App.displayConfirmation(
                            "Would you like to make a backup of your original game files? (Approx. 3gb) \n\n" +
                                    "This is HIGHLY RECOMMENDED. \n\n" +
                                    "This is required to be able to restore the original audio without repairing the game.")) {
                        addTextToFlow("Creating backup of game audio.");
                        PckHandler.makeBackup();
                    }
                }

                // Poor man's field updater
                avatarRootTreeItem.getChildren().forEach(node -> {
                    if (node.isExpanded()) {
                        node.setExpanded(false);
                    }
                });

                makeMod();
                event.consume();
            }
        });
    }

    // Loads any existing sounds
    private void loadSoundsList() {
        if (!App.getUser().getSounds().isEmpty()) {
            addUserFileIfNotPresent(new ArrayList<File>(App.getUser().getSounds().values()));
        }
    }

    @FXML
    private void initiateBackupRestore() {
        if (!FileIO.backupExists()) {
            App.displayInfo("No backup exists! You will have the opportunity to make one the next time you create a mod.");
            return;
        }

        if (App.displayConfirmation("Would you like to initiate a restore? \n\nThis will return your audio files to normal.")) {
            PckHandler.restoreBackup();
        }
    }

    /**
     * Gets the event from selecting a new sound and adds it to the current list.
     * If a sound already exists on the list, it will be replaced.
     * 
     * @param event The edit event
     */
    private void userSelectedReplacement(TreeTableColumn.CellEditEvent<Audio,String> event) {
        Audio selectedAudio = event.getRowValue().getValue();
        var selectedSound = event.getNewValue().strip();

        if (selectedSound.equals("Clear selection")) {
            removeSoundReplacement(selectedAudio);
            event.getRowValue().getValue().getUserAudio().setName("");
            return;
        }

        setSoundReplacement(selectedAudio.getDetails(), App.getUser().getSounds().get(selectedSound));
        event.getRowValue().getValue().getUserAudio().setName(selectedSound);
    }

    @FXML
    // Adding files from left pane with the "Add Files" button
    private void addFiles() {
        // Startup is complete if user is adding files
        onAppStarted();
        final var files = getAudioFromChooser();

        if (files.isEmpty()) {
            return;
        }

        App.getUser().addUserSound(files);
    }

    private List<File> getAudioFromChooser() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter
                = new FileChooser.ExtensionFilter("Audio files",
                    "*.wav", "*.mp3", "*.aac", "*.flac", "*.m4a", "*.ogg", "*.mp4", "*.mov", "*.dash");
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setTitle("Select audio...");

        final var files = fileChooser.showOpenMultipleDialog(App.getStage());

        // Sanity check
        if (files == null) {
            return List.of();
        }

        // Add the files to the list
        addUserFileIfNotPresent(files);

        return files;
    }

    private void addUserFileIfNotPresent(final List<File> files) {
        files.forEach(file -> {
            if (!userFileList.getItems().contains(new UserAudio(file.getName(), file)) && !file.getName().equals("mute.wem")) {
                userFileList.getItems().add(new UserAudio(file.getName(), file));
                userFiles.add(file.getName());
            }
        });
    }

    @FXML
    private void dragAddFiles() {
        // Handled in drag event listener.
        // Due to running as admin for default game path access, drag/drop will not work.
    }

    @FXML
    private void removeAllUserSounds() {
        if (App.displayConfirmation("Remove all added sounds? Replacements using those sounds are unaffected.")) { 
            userFileList.getItems().clear();
            userFiles.clear();
        }
    }

    public void setSoundReplacement(AudioDetails detail, File file) {
        App.getUser().getModifiedSounds().put(detail, file);

        Audio addingAudio = new Audio(detail, new UserAudio(file.getName(), file));

        if (replacementList.getItems().contains(addingAudio)) {
            replacementList.getItems().remove(addingAudio);
        }

        replacementList.getItems().add(addingAudio);
    }

    public void removeSoundReplacement(Audio audio) {
        App.getUser().getModifiedSounds().remove(audio.getDetails());
        replacementList.getItems().remove(audio);
    }

    @FXML
    private void removeAllSoundReplacements() {
        if (App.displayConfirmation("Remove all replacements?")) {
            App.getUser().getModifiedSounds().clear();
            replacementList.getItems().clear();
        }
    }

    private void makeMod() {
        String destination = "./mods/all/";

        if (saveSeparatelyCheckbox.isSelected()) {
            var getNameDialog = new TextInputDialog();
            getNameDialog.setHeaderText("Pick name");
            getNameDialog.setContentText("Select a name for your mod:");
            destination = "./mods/" + getNameDialog.showAndWait().orElse("all") + "/";
        }

        final String dest = destination;

        // Create new thread
        new Thread(() -> {
            
            final HashMap<AudioDetails, File> convertedFiles = new HashMap<>();
            final HashMap<AudioDetails, File> convertedWemFiles = new HashMap<>();

            addTextToFlow("Beginning wav conversion...");
            App.getUser().getModifiedSounds().forEach((sound, file) -> {
                String fileName = file.getName();
                String ext = FilenameUtils.getExtension(fileName);

                // No need to convert to wav when the file already is
                if (!ext.equals("wav")) {
                    // Check if file exists in wav folder already
                    if (!new File("./wavs/" + FilenameUtils.getBaseName(fileName) + ".wav").isFile()) {
                        // File has not already been converted
                        addTextToFlow(String.format("Converting %s to wav...", fileName));

                        // When passing here do not set any name so that the same sound can be reused for many hashes
                        file = AudioConverter.convertToWav(file, "");
                        addTextToFlow("Converted file to wav.");
                    }
                } else {
                    addTextToFlow(String.format("Moving existing wav: %s", fileName));

                    // Copy existing wavs to wavs folder
                    FileIO.copyFile(file.toPath(), "./wavs/" + fileName);
                    file = new File("./wavs/" + fileName);
                }

                // Add to converted files map
                convertedFiles.put(sound, file);

                // Add the equivalent wem path to the list for use after wem conversion
                File convertedWem = new File("./wems/Windows/" + FilenameUtils.getBaseName(fileName) + ".wem");
                convertedWemFiles.put(sound, convertedWem);
            });
            addTextToFlow("Finished converting files to wav.");

            // Replace user files with converted files
            App.getUser().getModifiedSounds().putAll(convertedFiles);

            addTextToFlow("Creating required conversion file...");
            // Create necessary conversion file
            WWiseHandler.createWwiseExternalFile();

            addTextToFlow("Converting wavs to wem...");
            // Convert all selected files to wems
            WWiseHandler.convertToWEM();
            addTextToFlow("Finished converting wavs to wem.");

            App.getUser().getModifiedSounds().putAll(convertedWemFiles);

            addTextToFlow("Moving wems to appropriate directories...");
            App.getUser().getModifiedSounds().forEach((sound, file) -> {
                var directory = sound.getDirectory().getValueSafe();
                FileIO.copyAndRename(file, new File(dest + directory + "/" + sound.getHash().getValueSafe() + ".wem"));
                addTextToFlow(String.format("Moved %s .wem", sound.getHash().getValueSafe()));
            });

            // Apply mods
            if (applyModCheckbox.isSelected()) {
                addTextToFlow("Applying mods to game...");
                PckHandler.applyMods(dest);
            }

            Platform.runLater(() -> {
                // Clear the list
                replacementList.getItems().clear();
                App.getUser().getModifiedSounds().clear();

                App.displayInfo("Done! Your mod can be found in the mods folder (In the top left -> File -> Open mods folder)." +
                    "\n\n If you selected for it to be applied to the game, it is already applied.");

                // Re-enable the button
                makeModButton.setDisable(false);
                makeModButton.setText("Make Mod");

                FileIO.cleanUpTempFiles();
            });

            changeTextFlowVisibility();
            clearTextFlow();
        }).start();
    }

    @FXML
    void openLink(ActionEvent event) throws IOException, URISyntaxException {
        StringBuilder url = new StringBuilder();
        var source = (Hyperlink) event.getSource();
        if (source.getId().equals("github")) {
            url.append("https://www.github.com/NotThorny/Audio-Mod-Maker");
        } else if (source.getId().equals("gamebanana")) {
            url.append("https://gamebanana.com/members/3210319");
        }
        Desktop.getDesktop().browse(new URI(url.toString()));
    }

    @FXML
    void switchToLangSelection(ActionEvent event) throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    void exit(ActionEvent event) {
        App.getStage().close();
    }

    public void onAppStarted() {
        // Sanity check
        if (avatarTree.getRoot() == null) {
            addAllAvatarsToList(App.getUser().getLoadedAvatars().values());
        }
    }

    private void addAllAvatarsToList(Iterable<Avatar> avatars) {
        var iterator = avatars.iterator();
        while (iterator.hasNext()) {
            var avatar = iterator.next();

            // Empty audio with avatar name for display
            Audio audio = new Audio(new AudioDetails("", "", "", avatar.getName()), new UserAudio("", new File(".")));

            var treeAudioItem = new TreeItem<Audio>(audio);

            avatar.getAllHashes().forEach((hash, details) -> {
                var tempAudio = new Audio(details, new UserAudio("", new File(".")));
                var tempTreeItem = new TreeItem<Audio>(tempAudio);
                tempTreeItem.setGraphic(null);
                
                treeAudioItem.getChildren().add(tempTreeItem);
            });

            avatarRootTreeItem.getChildren().add(treeAudioItem);
        }

        avatarTree.setRoot(avatarRootTreeItem);
        avatarTree.setShowRoot(false);
    }

    @FXML
    private void openModsFolder() {
        FileIO.createDirIfNotExists("./mods/");
        try {
            Desktop.getDesktop().open(new File("./mods/"));
        } catch (IOException e) {
            e.printStackTrace();
            App.displayError("Unable to open mods folder!");
        }
    }

    private void changeTextFlowVisibility() {
        updateTextFlow.setVisible((updateTextFlow.isVisible() ? false : true));
    }

    private void addTextToFlow(String message) {
        var currentText = updateTextBody.getText();
        updateTextBody.setText(currentText + "\n" + message);
    }

    private void clearTextFlow() {
        updateTextBody.setText("Creating mod...");
    }
}
