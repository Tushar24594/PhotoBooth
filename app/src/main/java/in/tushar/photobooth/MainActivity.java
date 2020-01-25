package in.tushar.photobooth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    TextView nameTxt, phoneTxt,emailTxt;
    EditText nameEditTxt, phoneEditTxt,emailEditTxt;
    Button submit;
    Typeface bold,regular;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        bold = Typeface.createFromAsset(getAssets(),"font/Teko-Bold.ttf");
        regular = Typeface.createFromAsset(getAssets(),"font/Teko-Medium.ttf");
        nameTxt = findViewById(R.id.nameTxt);
        phoneTxt = findViewById(R.id.phoneTxt);
        emailTxt = findViewById(R.id.emailTxt);
        nameTxt.setTypeface(regular);
        phoneTxt.setTypeface(regular);
        emailTxt.setTypeface(regular);
        nameEditTxt = findViewById(R.id.nameEditTxt);
        phoneEditTxt = findViewById(R.id.phoneEditTxt);
        emailEditTxt = findViewById(R.id.emailEditTxt);
        nameEditTxt.setTypeface(bold);
        phoneEditTxt.setTypeface(bold);
        emailEditTxt.setTypeface(bold);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit.setScaleX((float) 0.9);
                submit.setScaleY((float) 0.9);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        submit.setScaleX((float) 1.0);
                        submit.setScaleY((float) 1.0);
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(intent);
//                        finish();
                    }
                }, 300);
            }
        });
    }

    public void sendMail(){

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
