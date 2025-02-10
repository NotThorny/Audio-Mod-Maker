package dev.thorny.user;

import com.google.gson.annotations.SerializedName;

import dev.thorny.data.Languages;
import dev.thorny.utils.FileIO;

public class Preferences {
    @SerializedName("language")
    private Languages language;
    @SerializedName("firstStartup")
    private boolean firstStartup;
    @SerializedName("gamePath")
    private String gamePath;

    public Preferences() {
        this.language = Languages.EN;
        this.firstStartup = true;
        this.gamePath = "C:/Program Files/HoYoPlay/games/Genshin Impact game/GenshinImpact.exe";
    }

    public void setLanguage(Languages lang) {
        this.language = lang;
        FileIO.savePreferences(this);
    }

    public Languages getLanguage() {
        return this.language;
    }

    public boolean isFirstStartup() {
        return firstStartup;
    }

    public void setFirstStartup(boolean firstStartup) {
        this.firstStartup = firstStartup;
    }

    public String getGamePath() {
        return gamePath;
    }

    public void setGamePath(String gamePath) {
        this.gamePath = gamePath;
    }
}
