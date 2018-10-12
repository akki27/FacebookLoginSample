package fblogin.akki.android.com.facebookloginsample;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by SadyAkki on 6/3/2017.
 */

public class SocialLoginApp extends Application {

    public static SharedPreferences fbLoginPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        fbLoginPreferences = getSharedPreferences(getPackageName() + "_fbLoginPreferences", MODE_PRIVATE);
    }
}
