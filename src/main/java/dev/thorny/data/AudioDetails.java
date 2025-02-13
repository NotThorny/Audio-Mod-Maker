package dev.thorny.data;

import com.google.gson.annotations.SerializedName;

import javafx.beans.property.SimpleStringProperty;

public class AudioDetails {

    @SerializedName("hash")
    private SimpleStringProperty hash;
    @SerializedName("directory")
    private SimpleStringProperty directory;
    @SerializedName("voiceContent")
    private SimpleStringProperty voiceContent;
    @SerializedName("sourceName")
    private SimpleStringProperty sourceName;

    public AudioDetails(String hash, String directory, String voiceContent, String sourceName) {
        this.hash = new SimpleStringProperty(hash);
        this.directory = new SimpleStringProperty(directory);
        this.voiceContent = new SimpleStringProperty(voiceContent);
        this.sourceName = new SimpleStringProperty(sourceName);
    }

    public SimpleStringProperty getHash() {
        return this.hash;
    }

    public SimpleStringProperty getDirectory() {
        return this.directory;
    }

    public SimpleStringProperty getVoiceContent() {
        return this.voiceContent;
    }

    public SimpleStringProperty getSourceName() {
        return this.sourceName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hash == null) ? 0 : hash.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AudioDetails other = (AudioDetails) obj;
        if (hash == null) {
            if (other.hash != null)
                return false;
        } else if (!hash.getValueSafe().equals(other.hash.getValueSafe()))
            return false;
        return true;
    }
}
