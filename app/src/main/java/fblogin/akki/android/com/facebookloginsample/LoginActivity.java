package fblogin.akki.android.com.facebookloginsample;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.Arrays;


public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_login);

        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "fblogin.akki.android.com.facebookloginsample",
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d(TAG, Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/


        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
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
                nextActivity(newProfile, null);
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();


        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Login_success");
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                String authToken = accessToken.getToken();
                nextActivity(profile, authToken);
                Toast.makeText(getApplicationContext(), "Login in...", Toast.LENGTH_SHORT).show();
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
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        LoginManager.getInstance().logOut();
        //Facebook login
        Profile profile = Profile.getCurrentProfile();
        nextActivity(profile, null);
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
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
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
            Intent main = new Intent(LoginActivity.this, MainActivity.class);
            main.putExtra("name", profile.getFirstName());
            main.putExtra("surname", profile.getLastName());
            main.putExtra("imageUrl", profile.getProfilePictureUri(200,200).toString());
            main.putExtra("authToken", authToken);
            startActivity(main);
        }
    }
}