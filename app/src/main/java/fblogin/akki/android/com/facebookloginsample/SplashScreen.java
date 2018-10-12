package fblogin.akki.android.com.facebookloginsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.w3c.dom.Text;

import java.util.Arrays;

/**
 * Created by SadyAkki on 5/22/2017.
 */

public class SplashScreen extends Activity {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    private String nextClassName;
    Bundle inBundle;
    TextView skipText;

    private static final String TAG = "SplashScreen";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.d(TAG, "onCreate()");
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.splash_screen);

        fbLogin();


        //Skip Login
        skipText = (TextView) findViewById(R.id.skip_text);
        skipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startNextActivity(MainActivity.class);
                startNextActivity("MainActivity", null);
            }
        });
    }

    public void startNextActivity( String nextClassName, String authToken) {
        Log.d(TAG, "NextActivityClassName: " +nextClassName);
        String className = "fblogin.akki.android.com.facebookloginsample."+nextClassName;
        try{
            Class<?> nextActivityName = Class.forName(className);
            Intent intent = new Intent(SplashScreen.this, nextActivityName);
            intent.putExtra("authToken", authToken);
            startActivity(intent);
            if(authToken != null) {
                finish();
            }
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void fbLogin() {
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                Log.d(TAG, "onCurrentAccessTokenChanged()");
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                Log.d(TAG, "onCurrentProfileChanged()_oldProfile");
                //nextActivity(newProfile, null);
                //startNextActivity(nextClassName);
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Login_success");
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                String authToken = accessToken.getToken();
                Toast.makeText(getApplicationContext(), "Login in...", Toast.LENGTH_SHORT).show();

                SocialLoginApp.fbLoginPreferences.edit().putString(Preferences.FB_LOGIN_PREFERENCE_KEY, authToken).commit();

                startNextActivity(nextClassName, authToken);


            }

            @Override
            public void onCancel() {
                Log.v(TAG, "Facebook login was canceled");
            }

            @Override
            public void onError(FacebookException e) {
                Log.e(TAG, "Facebook login failed: " + e.getMessage());
            }
        });

        LoginButton facebook_button = (LoginButton)findViewById(R.id.login_button);
        facebook_button.setReadPermissions("user_friends", "email", "public_profile");
        facebook_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(SplashScreen.this, Arrays.asList("public_profile"));
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        inBundle = getIntent().getExtras();
        if(inBundle != null) {
            if(inBundle.get("nextClassName") != null) {
                nextClassName = inBundle.get("nextClassName").toString();
                skipText = (TextView) findViewById(R.id.skip_text);
                skipText.setVisibility(View.INVISIBLE);
            } else {
                nextClassName = "MainActivity";
                //Check if FB Access Token exist, if Yes, proceed to HomeActivity
                try {
                    String curAuthToken = SocialLoginApp.fbLoginPreferences.getString(Preferences.FB_LOGIN_PREFERENCE_KEY, Preferences.DEFAULT_AUTH_DATA);
                    Log.d(TAG, "SavedAuthToken: " + curAuthToken);

                    if(!curAuthToken.isEmpty() && !curAuthToken.equalsIgnoreCase(Preferences.DEFAULT_AUTH_DATA)) {
                        startNextActivity(nextClassName, curAuthToken);
                    } else {
                        //
                    }
                }catch (ClassCastException e) {
                    e.printStackTrace();
                    //Move back to the previous calling activity
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");

        //Facebook login
        //accessTokenTracker.stopTracking();
        //profileTracker.stopTracking();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()_called");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed()_called");
        inBundle = null;
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        Log.d(TAG, "onActivityResult()");
        //Facebook login
        callbackManager.onActivityResult(requestCode, responseCode, intent);
    }

    private void nextActivity(Profile profile, String authToken){
        Log.d(TAG, "nextActivity()_profile: " +profile);
        if(profile != null){
            Intent main = new Intent(SplashScreen.this, MainActivity.class);
            main.putExtra("name", profile.getFirstName());
            main.putExtra("surname", profile.getLastName());
            main.putExtra("imageUrl", profile.getProfilePictureUri(200,200).toString());
            main.putExtra("authToken", authToken);
            startActivity(main);
            SplashScreen.this.finish();
        }
    }
}