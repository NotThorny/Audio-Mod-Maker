package dev.thorny.data;

import java.util.HashMap;

import com.google.gson.annotations.SerializedName;

import javafx.beans.property.SimpleStringProperty;

/**
 *   Avatar entity which consists of several sets of hashmaps.
 *   An avatar is read from file, where AudioDetails are contained.
 *   Each avatar is a separate file when reading, so name is not contained in the file.
 */
public class Avatar {

    private SimpleStringProperty name;

    private HashMap<String, AudioDetails> allHashes = new HashMap<>();

    @SerializedName("Teapot")
    private final HashMap<String, AudioDetails> teapotHashes = new HashMap<>();
    @SerializedName("Quests")
    private final HashMap<String, AudioDetails> questHashes = new HashMap<>();
    @SerializedName("Gameplay")
    private final HashMap<String, AudioDetails> gameplayHashes = new HashMap<>();

    public Avatar(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public String getName() {
        return this.name.getValueSafe();
    }

    public void setName(String newName) {
        if (this.name == null) {
            this.name = new SimpleStringProperty(newName);
        } else {
            this.name.set(newName);
        }
    }

    public HashMap<String, AudioDetails> getAllHashes() {
        if (this.allHashes == null) {
            this.allHashes = new HashMap<>();
            return combineHashes();
        }
        return this.allHashes;
    }

    public HashMap<String, AudioDetails> getTeapotHashes() {
        return this.teapotHashes;
    }

    public HashMap<String, AudioDetails> getQuestHashes() {
        return this.questHashes;
    }

    public HashMap<String, AudioDetails> getGameplayHashes() {
        return this.gameplayHashes;
    }

    public HashMap<String, AudioDetails> combineHashes() {
        this.allHashes.putAll(gameplayHashes);
        this.allHashes.putAll(questHashes);
        this.allHashes.putAll(teapotHashes);

        return this.allHashes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Avatar other = (Avatar) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.getValue().equals(other.name.getValue()))
            return false;
        return true;
    }

}
