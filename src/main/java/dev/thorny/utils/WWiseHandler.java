package dev.thorny.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import dev.thorny.App;
import javafx.application.Platform;

public class WWiseHandler {
    /**
     * Convert all wav files listed in list.wsources at
     * the path [root]/wems to wem format.
     * No renaming is done, so files require further processing
     * to be used as intended.
     * 
     * @returns True if successful, false otherwise
     */
    public static boolean convertToWEM() {
        // Get the root and replace slashes
        final var ROOT_DIR = FileIO.getRootDir().replace("\\", "/");

        // Start the proc with args to the project included to convert to wem
        try {
            // WwiseConsole.exe operation [arguments] [--option1 [parameters]] [--option2 [parameters]] ...
            final var wwiseInstallPath = new File("./resources/WWIse").getAbsolutePath().replace("\\", "/");
            new ProcessBuilder(
                wwiseInstallPath + "/Authoring/x64/Release/bin/WwiseConsole.exe",
                "convert-external-source",
                "\"" + ROOT_DIR + "/resources/WAVtoWEM/WAVtoWEM.wproj\"",
                "--source-file",
                "\"" + ROOT_DIR + "/wems/list.wsources\"",
                "--output",
                "\"" + ROOT_DIR + "/wems/\"")
                .inheritIO().start().waitFor();
        } catch (Exception e) {
            Platform.runLater(() -> {
                App.displayError("Error 5003: Failed to convert file with WWise. " + e.getMessage() + " " + e.getCause());
            });
            return false;
        }
        return true;
    }

    // Returns whether or not the WWise folder exists
    public static boolean isWwiseInstalled() {
        try {
            return new File("./resources/WWIse/Authoring/x64/Release/bin/WwiseConsole.exe").isFile();
        } catch (Exception e) {
            Platform.runLater(() -> {
                App.displayError("Security exception in getting checking wwise install \n\n " + e.getMessage() + "  " + e.getCause());
            });

            return false;
        }
    }

    /**
     * Creates a new file for use with wwise console.
     * The following is the proper layout for the file:
     * 
     * <?xml version="1.0" encoding="UTF-8"?>
     * <ExternalSourcesList SchemaVersion="1" Root="(root dir for sounds)">
     *   <Source Path="(filename)" Conversion="Vorbis High Quality"/>cmd
     * </ExternalSourcesList>
     */
    public static void createWwiseExternalFile() {
        StringBuilder stringContent = new StringBuilder();
        var sounds = App.getUser().getModifiedSounds().values().stream().distinct().filter(f -> f.getName().endsWith(".wav")).toList();

        // Create the list.wsources file layout
        stringContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        stringContent.append("<ExternalSourcesList SchemaVersion=\"1\" Root=\"" + FileIO.ROOT_DIR + "\\wavs" + "\">\n");

        // Add each file specified
        sounds.forEach(file -> {
            stringContent.append("\t<Source Path=\"" + file.getName() + "\" Conversion=\"Vorbis Quality High\"/>\n");
        });

        stringContent.append("</ExternalSourcesList>");

        // Write the file to system
        try {
            File wsources = new File(FileIO.ROOT_DIR + "\\wems\\list.wsources");
            if (!wsources.exists()) {
                wsources.createNewFile();
            }
            Files.writeString(wsources.toPath(), stringContent.toString());
        } catch (IOException e) {
            Platform.runLater(() -> {
                App.displayError("Error 5002: Failed to write to list.wsources file.");
            });

            return;
        }
    }
}
