package in.tushar.photobooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.mashape.unirest.http.exceptions.UnirestException;


public class MainActivity extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    TextView nameTxt, phoneTxt, emailTxt;
    EditText nameEditTxt, phoneEditTxt, emailEditTxt;
    Button submit;
    Typeface bold, regular;
    String userName, userMobile, userEmail;
    public static final String TAG = "--Main Activity--";
    String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    String image;
    ImageView sent;
    RelativeLayout innerLayout;
    Mail mail = new Mail();
    CSV csv = new CSV();
    Boolean saved = false;
    String mailStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
        }
        image = getIntent().getStringExtra("Overlay");
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        innerLayout = findViewById(R.id.innerLayout);
        sent = findViewById(R.id.sentPopUp);
        sent.setVisibility(View.GONE);
        bold = Typeface.createFromAsset(getAssets(), "font/Teko-Bold.ttf");
        regular = Typeface.createFromAsset(getAssets(), "font/Teko-Medium.ttf");
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
                        userName = nameEditTxt.getText().toString();
                        userMobile = phoneEditTxt.getText().toString();
                        userEmail = emailEditTxt.getText().toString();
                        if (userName.isEmpty()) {
                            nameEditTxt.setError("Please enter your name");
                            nameEditTxt.requestFocus();
                        } else if (userMobile.isEmpty() || userMobile.length() < 10) {
                            phoneEditTxt.setError("Please enter your phone number");
                            phoneEditTxt.requestFocus();
                        } else if (userEmail.isEmpty() || !userEmail.matches(emailPattern)) {
                            emailEditTxt.setError("Please enter your email");
                            emailEditTxt.requestFocus();
                        } else if (!userEmail.isEmpty() || !userName.isEmpty() || !userMobile.isEmpty()) {
                            InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            innerLayout.setAlpha((float) 0.1);
                            sent.setVisibility(View.VISIBLE);
                            dataSaved();
                        }
                    }
                }, 300);


            }
        });
    }

    public void dataSaved(){
        saved = csv.saveDatatoCSV(userName, userMobile, userEmail);
        if(saved){
            Log.e(TAG," Database Saved..."+saved);
            try {
                mailStatus = String.valueOf(mail.sendComplexMessage(image, userEmail));
                Log.e(TAG, "Return : " + mailStatus);
                if (!mailStatus.isEmpty()) {
                    Log.e(TAG, "Sent....");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(), startScreen.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 4000);
                } else {
                    Log.e(TAG, "Not Sent....");
                }
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }else{
            Log.e(TAG," Database Not Saved..."+saved);
        }
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
