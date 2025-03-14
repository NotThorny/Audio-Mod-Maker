package dev.thorny.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.hildan.fxgson.FxGson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import dev.thorny.App;
import dev.thorny.data.Avatar;
import dev.thorny.data.Languages;
import dev.thorny.user.Audio;
import dev.thorny.user.Preferences;
import dev.thorny.user.User;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class FileIO {

    private static final Gson fxGson = FxGson.coreBuilder().setPrettyPrinting()
            .registerTypeAdapter(File.class, new FileSerializer())
            .registerTypeAdapter(File.class, new FileDeserializer())
            .create();
    static final String ROOT_DIR = System.getProperty("user.dir");

    /**
     * Reads hash info for avatars of the given langauge
     * 
     * @param lang The language to read
     */
    public static void readLanguageFile(Languages lang) {
        String path = "./resources/indexes/" + lang.name() + "/";
        User user = App.getUser();

        var filesInDir = Stream.of(new File(path).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());

        // Read each file in the directory of the current language
        for (String fromDir : filesInDir) {
            // Get character name from file name
            String name = fromDir.replace("_" + App.getCurrentLang() + ".json", "");

            // Create full path to file
            StringBuilder sb = new StringBuilder();
            sb.append(path).append("/").append(fromDir);
            fromDir = sb.toString();

            try (FileReader file = new FileReader(fromDir, StandardCharsets.UTF_8)) {
                Avatar temp = fxGson.fromJson(file, Avatar.class);
                temp.setName(name);
                user.addLoadedAvatar(temp);
            } catch (Exception e) {
                e.printStackTrace();
                App.displayError(
                        "Error 5001: Unable to read language indexes. You may be missing files or lacking permissions.\n\n"
                                +
                                "Ensure the indexes folder exists, and try running the application with admin privileges from the right-click context menu.");
                return;
            }
        }
    }

    /**
     * Reads preferences from the prefs file
     * 
     * @return Preferences The contents of the file
     */
    public static Preferences readPreferences() {
        String path = "./prefs.json";
        File prefs = new File(path);

        try (FileReader file = new FileReader(prefs)) {
            return fxGson.fromJson(file, Preferences.class);
        } catch (Exception e) {
            Preferences defaultPref = new Preferences();
            savePreferences(defaultPref);
            return defaultPref;
        }
    }

    /**
     * Lists all folders in a given relative directory
     * 
     * @param directory The relative directory pathname
     * @return A list of all directories in the given path
     * @throws IOException
     */
    public static List<Path> listFoldersInRelativeDir(String directory) throws IOException {
        var file = new File("./" + directory);
        if (file.isDirectory()) {
            return Files.list(file.toPath()).filter(Files::isDirectory).toList();
        } else {
            Files.createDirectories(file.toPath());
            return new ArrayList<>();
        }
    }

    /**
     * Gets the name of the directory at the given path
     * @param path The path to the file/directory
     * @return The name of the directory
     */
    public static String getNameFromPath(Path path) {
        return new File(path.toString()).getName();
    }

    /**
     * Writes the given mod to the list of all mods.
     * @param audio The mod to be added
     * @return True if successful, false otherwise
     */
    public static boolean saveModToList(String name, ArrayList<Audio> audio) {
        String path = "./modsList.json";
        File modsList = new File(path);

        var existingFile = readModsFromList();

        // Sanity
        if (existingFile == null) {
            existingFile = new HashMap<>();
        }

        existingFile.put(name, audio);

        try (FileWriter file = new FileWriter(modsList)) {
            // Sanity check
            if (audio == null) {
                return false;
            } else {
                file.write(fxGson.toJson(existingFile, new TypeToken<HashMap<String, ArrayList<Audio>>>(){}.getType()));
            }
        } catch (Exception e) {
            App.displayError("Unable to save mods to list! \n\n" + e.getMessage() + " " + e.getCause());
            return false;
        }

        return true;
    }

    /**
     * Reads the file with saved mods
     * @return The contents of the file as a list of Audio entries.
     */
    public static HashMap<String, ArrayList<Audio>> readModsFromList() {
        File modsFile = new File("./modsList.json");
        HashMap<String, ArrayList<Audio>> modsList = new HashMap<>();
        var type = new TypeToken<HashMap<String, ArrayList<Audio>>>(){}.getType();
        
        try (FileReader file = new FileReader(modsFile)) {
            return fxGson.fromJson(file, type);
        } catch (Exception e) {
            return modsList;
        }
    }

    public static ArrayList<File> listFilesInAudioMap(HashMap<String, ArrayList<Audio>> map) {
        ArrayList<File> files = new ArrayList<>();
        map.values().forEach(val -> {
            for (var audio : val) {
                files.add(audio.getUserAudio().getFile());
            }
        });

        return files;
    }

    public static ArrayList<File> listFilesInAudioArray(ArrayList<Audio> list) {
        ArrayList<File> files = new ArrayList<>();
        for (var audio : list) {
            files.add(audio.getUserAudio().getFile());
        }

        return files;
    }

    /**
     * Saves preferences to file
     * 
     * @param pref The preferences file
     * @return True if successful, otherwise false
     */
    public static boolean savePreferences(Preferences pref) {
        String path = "./prefs.json";
        File prefs = new File(path);

        try (FileWriter file = new FileWriter(prefs)) {
            if (pref == null) {
                file.write(fxGson.toJson(new Preferences()));
            } else {
                file.write(fxGson.toJson(pref));
            }
        } catch (Exception e) {
            App.displayError("Unable to save preferences!");
            return false;
        }

        return true;
    }

    /**
     * Copies a file to a specified location
     *
     * @param source      The path to the original file
     * @param destination The path to copy the file to
     * @return True if the file was copied, otherwise false
     */
    public static boolean copyFile(File source, String destination) {
        try {
            FileUtils.copyFile(source, new File(destination));
            return true;
        } catch (Exception e) {
                App.displayErrorOnFXThread(
                    new StringBuilder("Unable to copy file ")
                            .append(source).append(" to ").append(destination)
                            .append("\n\n").append(e.getMessage())
                            .toString());
            return false;
        }
    }

    public static boolean createFile(String appRelativePath) throws IOException {
        return new File(appRelativePath).createNewFile();
    }

    public static String getRootDir() {
        return ROOT_DIR;
    }

    public static void copyAndRename(File file, File dest) {
        try {
            FileUtils.copyFile(file, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            App.displayErrorOnFXThread(String.format("Error copying file %s to %s \n\n %s", file.toPath(), dest, e.getCause()));
        }
    }

    // Delete a folder and all files inside it
    public static void deleteFolder(String folder) {
        if (!new File(folder).isDirectory()) {
            return;
        }
        try {
            FileUtils.deleteDirectory(new File(folder));
        } catch (IOException e) {
            e.printStackTrace();
            App.displayErrorOnFXThread("Failed to delete folder: " + folder);
        }
    }

    // Delete a specified file
    public static boolean deleteFile(String path) {
        try {
            FileUtils.delete(new File(path));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            App.displayErrorOnFXThread(String.format("Failed to delete file %s", path));
            return false;
        }
    }

    // Check if a backup file exists
    public static boolean backupExists() {
        var gameName = FilenameUtils.getBaseName(App.getPrefs().getGamePath());

        return (new File("./resources/backup/" + gameName + "/" + App.getCurrentLang().getName() + "/External0.pck")).isFile();
    }

    // Get backup folder
    public static File getBackup() {
        var gameName = FilenameUtils.getBaseName(App.getPrefs().getGamePath());

        return new File("./resources/backup/" + gameName + "/" + App.getCurrentLang().getName());
    }

    public static File getBackupDirectory() {
        var gameName = FilenameUtils.getBaseName(App.getPrefs().getGamePath());

        return new File("./resources/backup/" + gameName);
    }

    /**
     * Removes temp files from repack folder: input_pcks, output_pcks, wem, and wavs from conversion.
     */
    public static void cleanUpTempFiles() {
        deleteFolder("./wavs/");
        deleteFolder("./resources/repacking/input_pck/");
        deleteFolder("./resources/repacking/output_pck/");
        deleteFolder("./resources/repacking/wem/");
    }

    // Create a directory
    public static void createDirIfNotExists(String dir) {
        try {
            FileUtils.forceMkdir(new File(dir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Downloads a file from the specified URI to ./resources/
     * 
     * @param uri The uri of the file
     * @return True if the download was successful, false otherwise.
     */
    public static boolean downloadFile(String uri) {
        var name = FilenameUtils.getName(uri);
        try {
            FileUtils.copyURLToFile(new URI(uri).toURL(), new File("./resources/" + name), 300000, 600000);
            return true;
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            App.displayErrorOnFXThread(String.format("Failed to download file! %s :: %s", e.getCause(), e.getMessage()));
            return false;
        }
    }

    /**
     * Extracts a file to the ./resources/ folder
     * 
     * @param file The file to unzip
     * @return True if unzipping was successful, false otherwise.
     */
    public static boolean unzipFile(File file) {
        try {
            new ZipFile(file).extractAll("./resources/");
            return true;
        } catch (ZipException e) {
            e.printStackTrace();
            App.displayErrorOnFXThread(String.format("Error extracting zip file: %s \n\n %s \n %s", file.getName(), e.getMessage(), e.getCause()));
            return false;
        }
    }
}
