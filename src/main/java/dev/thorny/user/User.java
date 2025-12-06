package dev.thorny.user;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.thorny.App;
import dev.thorny.data.AudioDetails;
import dev.thorny.data.Avatar;

/*
 * Object that contains all current user data.
 * Tracks what sounds have been added and what sounds are to be modified.
 */
public class User {

    private final Map<String, File> sounds;
    private final Map<AudioDetails, File> modifiedSounds;
    private final Map<String, Avatar> loadedAvatars;
    private final Map<String, AudioDetails> allHashes;

    public User() {
        this.sounds = new HashMap<>();
        this.modifiedSounds = new HashMap<>();
        this.loadedAvatars = new HashMap<>();
        this.allHashes = new HashMap<>();
    }

    public void addUserSound(List<File> soundList) {
        soundList.forEach((userSound) -> {
            this.sounds.put(userSound.getName(), userSound);
        });
    }

    public void addModifiedSound(AudioDetails details, File userSound) {
        this.modifiedSounds.put(details, userSound);
    }

    public Map<String, File> getSounds() {
        return this.sounds;
    }

    public Map<AudioDetails, File> getModifiedSounds() {
        return this.modifiedSounds;
    }

    public void addLoadedAvatar(Avatar avatar) {
        this.loadedAvatars.put(avatar.getName(), avatar);
    }

    public Map<String, Avatar> getLoadedAvatars() {
        return this.loadedAvatars;
    }

    public void addAllHashes() {
        this.allHashes.clear();
        for (Avatar a : getLoadedAvatars().values()) {
            allHashes.putAll(a.getAllHashes());
        }
    }

    public Map<String, AudioDetails> getAllHashes() {
        return this.allHashes;
    }

}
