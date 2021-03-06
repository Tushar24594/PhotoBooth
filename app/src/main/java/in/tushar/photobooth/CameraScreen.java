package in.tushar.photobooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.hardware.Camera.Parameters;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.hardware.Camera;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Policy;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.media.ExifInterface.ORIENTATION_ROTATE_180;
import static android.media.ExifInterface.ORIENTATION_ROTATE_270;
import static android.media.ExifInterface.ORIENTATION_ROTATE_90;

public class CameraScreen extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PictureCallback {
    public static final String TAG = "----Camera Activity----";
    public static final int REQUEST_CODE = 100;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    Typeface bold, regular;
    private Camera camera;
    private String[] neededPermissions = new String[]{CAMERA, WRITE_EXTERNAL_STORAGE};
    boolean result;
    MediaPlayer mediaPlayer;
    Integer time = 3;
    TextView timer;
    private Camera.Size mPreviewSize;
    private List<Camera.Size> mSupportedPreviewSizes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_camera);
        bold = Typeface.createFromAsset(getAssets(), "fonts/RENAULTLIFE-BOLD.TTF");
        regular = Typeface.createFromAsset(getAssets(), "fonts/RENAULTLIFE-REGULAR.TTF");
        surfaceView = findViewById(R.id.surfaceView);
        timer = findViewById(R.id.timer);
        timer.setTypeface(bold);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.beep);
        if (surfaceView != null) {
            result = checkPermission();
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CameraScreen.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
            }
            if (result) setupSurfaceHolder();
        }
    }

    private boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            ArrayList<String> permissionsNotGranted = new ArrayList<>();
            for (String permission : neededPermissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsNotGranted.add(permission);
                }
            }
            if (permissionsNotGranted.size() > 0) {
                boolean shouldShowAlert = false;
                for (String permission : permissionsNotGranted) {
                    shouldShowAlert = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                }
                if (shouldShowAlert) {
                    showPermissionAlert(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]));
                } else {
                    requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]));
                }
                return false;
            }
        }
        return true;
    }

    private void showPermissionAlert(final String[] permissions) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(R.string.permission_required);
        alertBuilder.setMessage(R.string.permission_message);
        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(permissions);
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void requestPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(CameraScreen.this, permissions, REQUEST_CODE);
    }

    private void setViewVisibility(int id, int visibility) {
        View view = findViewById(id);
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    private void setupSurfaceHolder() {
        setViewVisibility(R.id.startBtn, View.VISIBLE);
        setViewVisibility(R.id.surfaceView, View.VISIBLE);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        setBtnClick();
    }

    private void setBtnClick() {
        final ImageButton startBtn = findViewById(R.id.startBtn);
        if (startBtn != null) {
            startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startBtn.setScaleX((float) 0.9);
                    startBtn.setScaleY((float) 0.9);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startBtn.setScaleX((float) 1.0);
                            startBtn.setScaleY((float) 1.0);
                            Log.e(TAG, "Timer : " + time);
                            timer.setVisibility(View.VISIBLE);
                            new CountDownTimer(3000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    timer.setText(String.valueOf(time));
                                    time--;
                                }

                                public void onFinish() {
                                    timer.setVisibility(View.GONE);
                                    timer.setText(String.valueOf(3));
                                    time = 3;
                                }
                            }.start();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    captureImage();
                                }
                            }, 3000);
                        }
                    }, 300);

                }
            });
        }
    }

    public void captureImage() {
        if (camera != null) {
            Log.e(TAG, "Camera taking picture.....");
            camera.takePicture(null, null, this);
            mediaPlayer.start();
        } else {
            Log.e(TAG, "Camera Cannot taking picture.....");
        }


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startCamera();
    }

    private void startCamera() {

        try {
            int cameraCount = 0;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras();
            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    try {
                        camera = Camera.open(camIdx);
                        camera.setAutoFocusMoveCallback(null);
                        camera.setDisplayOrientation(90);
                        mSupportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
                        for (Camera.Size str : mSupportedPreviewSizes)
                            Log.e(TAG, "Size--" + str.width + "/" + str.height);
                        Camera.Parameters parameters = camera.getParameters();
                        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                        camera.setParameters(parameters);
                        camera.setDisplayOrientation(90);
                        camera.setPreviewDisplay(surfaceHolder);
                        camera.startPreview();
                    } catch (RuntimeException e) {
                        Log.e(TAG, "Failed to open: " + e.getLocalizedMessage());
                    }
                }
            }
//            Camera.Parameters parameters = camera.getParameters();
//            parameters.setPreviewSize(mPreviewSize.width,mPreviewSize.height);
//            camera.setParameters(parameters);
//            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        resetCamera();
    }

    public void resetCamera() {
        if (surfaceHolder.getSurface() == null) {
            // Return if preview surface does not exist
            return;
        }

        if (camera != null) {
            // Stop if preview surface is already running.
            camera.stopPreview();
            try {
                // Set preview display
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Start the camera preview...
            camera.startPreview();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Toast.makeText(this, "Boolean :" + hasCapture, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        saveImage(data);
        resetCamera();
    }

    private void saveImage(byte[] bytes) {
        FileOutputStream outStream;
        try {
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
            File myDir = new File(root + "/PhotoBooth");
            myDir.mkdirs();
            String fileName = "Image_" + System.currentTimeMillis() + ".PNG";
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "/PhotoBooth/" + fileName);

            outStream = new FileOutputStream(file);
            outStream.write(bytes);
            outStream.close();
            Intent galleryIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(Environment.getExternalStorageDirectory(), fileName);
            Uri picUri = Uri.fromFile(f);
            galleryIntent.setData(picUri);
            this.sendBroadcast(galleryIntent);
            Intent intent = new Intent(getApplicationContext(), CaptureScreen.class);
            intent.putExtra("Image", fileName);
            startActivity(intent);
            finish();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
