package in.tushar.photobooth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class startScreen extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    ImageButton imageButton;
    TextView txt;
    Typeface bold, regular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_start_screen);
        bold = Typeface.createFromAsset(getAssets(), "font/RENAULTLIFE-BOLD.TTF");
        regular = Typeface.createFromAsset(getAssets(), "font/RENAULTLIFE-REGULAR.TTF");
        txt = findViewById(R.id.txt);
        txt.setTypeface(bold);
        imageButton = findViewById(R.id.start);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButton.setScaleX((float) 0.9);
                imageButton.setScaleY((float) 0.9);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageButton.setScaleX((float) 1.0);
                        imageButton.setScaleY((float) 1.0);
                        Intent intent = new Intent(getApplicationContext(), CameraScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }, 300);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


}
