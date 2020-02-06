package in.binplus.selfstudy;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.binplus.selfstudy.R;

import in.binplus.selfstudy.utils.Constants;


public class SplashscreenActivity extends AppCompatActivity {

    private int SPLASH_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_splashscreen);

        //Toast.makeText(SplashscreenActivity.this, "login:"+ isLogedIn(), Toast.LENGTH_SHORT).show();
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_TIME);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (isLogedIn()) {
                        startActivity(new Intent(SplashscreenActivity.this,MainActivity.class));
                        finish();
                    } else {
                        if (!Constants.IS_LOGIN_MANDATORY) {
                            Intent intent = new Intent(SplashscreenActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);

                        } else {
                            startActivity(new Intent(SplashscreenActivity.this,LoginActivity.class));
                        }
                        finish();
                    }

                }
            }
        };
        timer.start();

    }

    public boolean isLogedIn() {
        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
        return preferences.getBoolean("status", false);

    }

}
