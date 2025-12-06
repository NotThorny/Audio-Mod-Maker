package dev.thorny.utils;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FilenameUtils;

import dev.thorny.App;
import javafx.application.Platform;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

public class AudioConverter {
    /**
     * Converts a given audio file to wav.
     * Converted file will be placed in the
     * default output directory.
     * 
     * @parameter source The originating file
     * @parameter newName The new name for the file after conversion
     * @returns File The converted file 
     */
    public static File convertToWav(File source, String newName) {
        try {

            // For updating old wems, convert to wav then back to wem
            if (source.getName().endsWith(".wem")) {
                final String sourceName = source.getAbsolutePath();
                String[] command = {
                    new File("resources/vgmstream").getAbsolutePath() + "/vgmstream-cli.exe", "-o", FilenameUtils.getBaseName(source.toString()) + ".wav", sourceName
                };
                ProcessBuilder b = new ProcessBuilder(command);
                b = b.directory(new File("resources/vgmstream/"));
                try {
                    Process p = b.start();
                    p.waitFor();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

                source = new File("resources/vgmstream/" + FilenameUtils.getBaseName(sourceName).replace(".wem", "") + ".wav");
            }

            // If no name has been defined, use name of existing file
            if (newName.isBlank()) {
                int extIndex = source.getName().lastIndexOf('.');
                newName = (extIndex == -1) ? "default" : source.getName().substring(0, extIndex);
            }

            // Name and location of converted file
            File target = new File("./wavs/" + newName + ".wav");

            // For if the file already exists
            // if (target.exists()) {
            //     // File already exists, handle or skip.
            // }

            AudioAttributes audio = new AudioAttributes();
            audio.setCodec("pcm_s16le");
            audio.setBitRate(128);
            audio.setChannels(2);
            audio.setSamplingRate(48000);

            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setOutputFormat("wav");
            attrs.setAudioAttributes(audio);

            Encoder encoder = new Encoder();
            encoder.encode(new MultimediaObject(source), target, attrs);

            return target;

        } catch (EncoderException e) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    App.displayError("Failed to convert file! If reporting this issue, please include the following:\n\n"
                    + e.getMessage());
                }
            });
            return null;
        }
    }
}
