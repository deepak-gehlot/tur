package tutorialance.widevision.com.tutorialance.activity;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by mercury-five on 28/12/15.
 */
public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
