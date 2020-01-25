package in.tushar.photobooth;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CaptureScreen extends AppCompatActivity {
    public static final String TAG = "----CameraScreen----";
    ImageView imageView;
    String image;
    FrameLayout layout;
    ImageButton back,next;
    Bitmap finalImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_capture_screen);
        imageView = findViewById(R.id.image);
        layout = findViewById(R.id.layout);
        back = findViewById(R.id.backbtn);
        next = findViewById(R.id.next);
        image = getIntent().getStringExtra("Image");

        File imgFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "/PhotoBooth/"+image);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Matrix mat = new Matrix();
            mat.postRotate(-90);
            mat.preScale(0.9f, -0.9f);
            Bitmap bMapRotate = Bitmap.createBitmap(myBitmap, 0, 0,
                    myBitmap.getWidth(), myBitmap.getHeight(), mat, true);
            BitmapDrawable bmd = new BitmapDrawable(bMapRotate);
            Bitmap smallImage = BitmapFactory.decodeResource(getResources(), R.drawable.cam);
            finalImage = createSingleImageFromMultipleImages(bMapRotate, smallImage);
            imageView.setImageBitmap(finalImage);
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setScaleX((float)0.9);
                back.setScaleY((float)0.9);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        back.setScaleX((float) 1.0);
                        back.setScaleY((float) 1.0);
                        Intent intent = new Intent(getApplicationContext(),CameraScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }, 300);

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next.setScaleX((float)0.9);
                next.setScaleY((float)0.9);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        next.setScaleX((float) 1.0);
                        next.setScaleY((float) 1.0);
                        saveBitmapImage(finalImage);
                    }
                }, 300);

            }
        });
    }

    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage) {

        Bitmap result = Bitmap.createBitmap(secondImage.getWidth(), secondImage.getHeight(), secondImage.getConfig());
        Log.e(TAG, String.valueOf(secondImage.getConfig()));
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, -150, -30,null);
        canvas.drawBitmap(secondImage, 0,0, null);
        Log.w(TAG,"C H: "+canvas.getHeight()+"C W :"+canvas.getWidth() );
        return result;
    }
    private void saveBitmapImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/PhotoBoxi");
        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "Shutta_"+ timeStamp +".jpg";

        File file = new File(myDir, fname);
        if (file.exists()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
//            Toast.makeText(this, "Overlay Saved "+fname, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.putExtra("Overlay",fname);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
