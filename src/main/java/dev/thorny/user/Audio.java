package dev.thorny.user;

import dev.thorny.data.AudioDetails;

public class Audio {
    private AudioDetails details;
    private UserAudio userAudio;

    public Audio(AudioDetails details, UserAudio userAudio) {
        this.details = details;
        this.userAudio = userAudio;
    }

    public AudioDetails getDetails() {
        return details;
    }

    public UserAudio getUserAudio() {
        return userAudio;
    }

    public void setDetails(AudioDetails details) {
        this.details = details;
    }

    public void setUserAudio(UserAudio userAudio) {
        this.userAudio = userAudio;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((details == null) ? 0 : details.hashCode());
        result = prime * result + ((userAudio == null) ? 0 : userAudio.hashCode());
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
        Audio other = (Audio) obj;
        if (details == null) {
            if (other.details != null)
                return false;
        } else if (!details.equals(other.details))
            return false;
        return true;
    }
}
