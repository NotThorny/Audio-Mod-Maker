package dev.thorny.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.PrefixFileFilter;

import dev.thorny.App;

public class PckHandler {

    private static final PrefixFileFilter externalFilter = new PrefixFileFilter("External");

    public static void makeBackup() {
        var gamePath = App.getPrefs().getGamePath();
        String gameName = FilenameUtils.getBaseName(gamePath);

        // Sanity check
        if (gamePath.isBlank()) {
            // No game set
            return;
        }

        var source = getRootAudioFolder();

        try {
            FileUtils.copyDirectory(source, new File("./resources/backup/" + gameName + "/"), externalFilter);
        } catch (IOException e) {
            e.printStackTrace();
            App.displayError("Failed to create backup! Is a valid game set? \n" + e.getMessage() + "\n" + e.getCause());
        }
    }

    public static void restoreBackup() {
        // Sanity check
        if (!FileIO.backupExists()) {
            return;
        }

        File backup = FileIO.getBackup();

        // Set files to be writable
        try {
            var dirs = backup.listFiles();
            for (File file : dirs) {
                var gameFile = new File(getRootAudioFolder() + "/" + file.getName());
                gameFile.setWritable(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Copy all backup files into the game audio folder
        try {
            FileUtils.copyDirectory(backup, getRootAudioFolder(), externalFilter, false, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            App.displayError("Unable to restore backup!");
        }
    }

    private static File getRootAudioFolder() {
        var gamePath = App.getPrefs().getGamePath();
        String gameName = FilenameUtils.getBaseName(gamePath);
        String rootAudioFolder = FilenameUtils.getFullPath(gamePath) + "/" + gameName + "_Data/Persistent/AudioAssets/" + App.getCurrentLang().getName() + "/";

        return new File(rootAudioFolder);
    }

    /**
     * Applies the files listed in the repacking/wem/ folder to the set game folder.
     * 
     * @param name The name of the mod folder.
     */
    public static void applyMods(String name) {
        // Create folders
        FileIO.createDirIfNotExists("./resources/repacking/output_pck");
        FileIO.createDirIfNotExists("./resources/repacking/input_pck");

        // Destinations
        // File inputDest = new File("./resources/repacking/input_pck/");
        File wemDest = new File("./resources/repacking/wem/");
        File outputDest = new File("./resources/repacking/output_pck/");

        copyWemsToRepack(name, wemDest);

        movePcksToRepack(wemDest);

        repackWemsProcess();

        movePcksToGameFolder(outputDest);
    }

    private static void repackWemsProcess() {
        String location = new File("resources/repacking/repack.exe").getAbsolutePath().replace("\\", "/");
        try {
            var proc = new ProcessBuilder(location).directory(new File("resources/repacking/"));
            proc.inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            App.displayError("Failed to run or finish running repack");
        }
    }

    // Move the specific pcks needed for making the desired mod
    private static void movePcksToRepack(File wemDest) {
        try {
            // FileUtils.copyDirectory(getRootAudioFolder(), inputDest, externalFilter, false, StandardCopyOption.REPLACE_EXISTING);
            var dirs = wemDest.listFiles();
            for (File file : dirs) {
                Files.move(
                        new File(getRootAudioFolder() + "/" + file.getName() + ".pck").toPath(),
                        Path.of(new File("resources/repacking/input_pck" + "/" + file.getName() + ".pck").getAbsolutePath()),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Move to game folder
    private static void movePcksToGameFolder(File outputDest) {
        try {
            FileUtils.copyDirectory(outputDest, getRootAudioFolder(), externalFilter, false, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            App.displayError("Failed to copy output pcks to game folder.");
        }
    }

    // Copy wems to wem folder
    private static void copyWemsToRepack(String name, File wemDest) {
        try {
            FileUtils.copyDirectory(new File(name), wemDest);
        } catch (IOException e) {
            e.printStackTrace();
            App.displayError("Failed to copy wems to packing folder.");
        }
    }
}
