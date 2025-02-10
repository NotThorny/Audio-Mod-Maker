package dev.thorny.utils;

import java.io.File;

import dev.thorny.App;
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

            // No reason to convert wem -> wav -> wem
            if (source.getName().endsWith(".wem")) {
                return source;
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
            App.displayError("Failed to convert file! If reporting this issue, please include the following:\n\n"
                    + e.getMessage());
            return null;
        }
    }
}
