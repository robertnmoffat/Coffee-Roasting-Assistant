package Utilities;

import android.content.Context;
import android.provider.ContactsContract;

public class GlobalSettings {
    public enum Language{
        english
    };

    private boolean metric = false;
    private String username = "User";
    private Language language = Language.english;
    private float cameraBrightness = 0.75f;

    private static GlobalSettings settings;

    private GlobalSettings(){

    }

    public static GlobalSettings getSettings(Context context){
        if(settings==null) {
            settings = new GlobalSettings();
            DataSaver.loadSettings(settings, context);
        }

        return settings;
    }

    public boolean isMetric() {
        return metric;
    }

    public void setMetric(boolean metric, Context context) {
        this.metric = metric;
        DataSaver.saveSettings(this, context);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username, Context context) {
        this.username = username;
        DataSaver.saveSettings(this, context);
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language, Context context) {
        this.language = language;
        DataSaver.saveSettings(this, context);
    }

    public float getCameraBrightness() {
        return cameraBrightness;
    }

    public void setCameraBrightness(float cameraBrightness, Context context) {
        this.cameraBrightness = cameraBrightness;
        DataSaver.saveSettings(this, context);
    }
}
