package fblogin.akki.android.com.facebookloginsample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.widget.ShareDialog;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    TextView nameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle inBundle = getIntent().getExtras();
        Log.d(TAG, "onCreate(): " +inBundle);

        if(inBundle != null) {
            if(inBundle.get("authToken") != null) {
                String authToken = inBundle.get("authToken").toString();
                TextView nameView = (TextView)findViewById(R.id.nameAndSurname);
                nameView.setText("" +authToken);

                Button getAuthBtn = (Button)findViewById(R.id.get_auth_token);
                getAuthBtn.setVisibility(View.INVISIBLE);
            } else {
                nameView = (TextView)findViewById(R.id.nameAndSurname);
                nameView.setText("No AuthToken..Skipped!");

                nameView = (TextView)findViewById(R.id.nameAndSurname);
                nameView.setText("Login Skipped!");

                Button getAuthBtn = (Button)findViewById(R.id.get_auth_token);
                getAuthBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LoginManager.getInstance().logOut();
                        Intent intent = new Intent(MainActivity.this, SplashScreen.class);
                        intent.putExtra("nextClassName", "MainActivity");
                        startActivity(intent);
                        //finish();
                    }
                });
            }
        }
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
        finish();
    }
}
