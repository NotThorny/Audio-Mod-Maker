package dev.thorny.user;

import java.io.File;
import java.util.Objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// An audio file that has been added by the user to be displayed in the app
public class UserAudio {

    private StringProperty name = new SimpleStringProperty();
    private File file;

    public UserAudio(String fileName, File file) {
        this.file = file;
        setName(fileName);
    }

    public StringProperty nameProperty() {
        return this.name;
    }

    public void setName(String fileName) {
        this.nameProperty().set(fileName);
    }

    public String getName() {
        return this.nameProperty().getName();
    }

    public File getFile() {
        return this.file;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.file);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UserAudio other = (UserAudio) obj;
        return Objects.equals(this.file.getName(), other.file.getName());
    }
    
}
