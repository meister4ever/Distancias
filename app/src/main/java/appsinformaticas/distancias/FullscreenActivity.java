package appsinformaticas.distancias;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.graphics.Color;

import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static appsinformaticas.distancias.R.id.camera;
import static appsinformaticas.distancias.R.id.imageView;

 /**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button btnCapturePicture;
    private String mImageFileLocation = "";
    ImageView result;
    ImageView result2;
    TextView text;
    private int imageCount;
     private int amountOfSamples = 1000;
    private int X=0,Y=0;
    private int X2=0,Y2=0;
    private float[] base;
    private float focalLength;
    private float p=1;
    private float[] inputArray;
     static double PI = 3.1415926535897932384626433832795;
     static double gravity = 9806.65;

     double Accel2mms(double accel, double freq){
         double result = 0;
         result = (gravity*accel)/(2*PI*freq);
         return result;
     }

     private float resultingArray(){
         for(int i = 0; i < amountOfSamples; i++){
             if(inputArray[i] < 0){
                 inputArray[i] = inputArray[i] - (inputArray[i]*2);
             }
         }
         int sum = 0;
         for (float d : inputArray) sum += d;
         return sum / inputArray.length;
     }

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        base = new float[]{150, 1, 1};
        imageCount = 0;
        btnCapturePicture = (Button)findViewById(camera);
        result = (ImageView)findViewById(imageView);
        result2= (ImageView)findViewById(R.id.imageView2);
        text = (TextView)findViewById(R.id.textView);
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);

    }

    public float getCameraFocalLength(final int cameraId) {
        Context context = getApplicationContext();
        try {
            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String[] cameraIds = manager.getCameraIdList();
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIds[cameraId]);
            return characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)[0];
        } catch (CameraAccessException e) {
            // TODO handle error properly or pass it on
            return 0;
        }
    }


    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        /*
        File photoFile = null;
        try{
            photoFile = createImageFile();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        */
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

     public String getDistance(){

        float W,Z,W2,Z2;

         float focalLength = 10*getCameraFocalLength(1);
        /*
         float t1 = (base[2] * W / p - base[1]) * focalLength;

         float t2 = (- base[2] * W / p + base[0]) * focalLength;

         float t3 = (base[1] * W - base[0] * Z) * focalLength / p;

         float param = (base[0] + base[2] * t3 / t1 + base[1] * t2 / t1) / (W + t3 * focalLength / t1 + t2 / t1 * Z);

         double distancia = param * focalLength * Math.sqrt((W/p) * (W/p) + (Z/p) * (Z/p) + 1);
        */
         /*
         float[] eventXY = new float[] {X, Y};

         Matrix invertMatrix = new Matrix();
         result.getImageMatrix().invert(invertMatrix);

         invertMatrix.mapPoints(eventXY);

         int x = Integer.valueOf((int)eventXY[0]);
         int y = Integer.valueOf((int)eventXY[1]);

         float[] event2XY = new float[] {X2, Y2};
         Matrix invertMatrix2 = new Matrix();
         result2.getImageMatrix().invert(invertMatrix2);

         invertMatrix2.mapPoints(event2XY);

         //int x2 = Integer.valueOf((int)event2XY[0]);
         //int y2 = Integer.valueOf((int)event2XY[1]);
*/
         W = X - result.getWidth()/2;
         //Z = Y - result.getHeight()/2;
         W2 =  X2 - result2.getWidth()/2;
         //Z2 = Y2 - result2.getHeight()/2;

         double D = pxToMm(Math.abs(W),getApplicationContext()) + pxToMm(Math.abs(W2),getApplicationContext());
         double distancia = (focalLength * base[0])/D;
         return String.valueOf(distancia);
     }



     public void visualTextComplete(View view){
         String aString = getDistance();
         text.setText(aString);
     }

     public static float pxToMm(final float px, final Context context)
     {
         final DisplayMetrics dm = context.getResources().getDisplayMetrics();
         return px / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, dm);
     }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if(imageCount==0) {
                imageCount++;
                Bundle extras = data.getExtras();
                Bitmap photoCapturedBitmap = (Bitmap) extras.get("data");
                //Bitmap photoCapturedBitmap = BitmapFactory.decodeFile(mImageFileLocation);
                result.setImageBitmap(photoCapturedBitmap);

                result.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN){

                            //  textView.setText("Touch coordinates : " +String.valueOf(event.getX()) + "x" + String.valueOf(event.getY()));
                            X = Integer.valueOf((int)event.getX());
                            Y = Integer.valueOf((int)event.getY());

                            result.buildDrawingCache();
                            Bitmap newBmp = result.getDrawingCache();
                            newBmp.setPixel(X, Y, Color.rgb(45, 127, 0));
                            result.setImageBitmap(newBmp);
                        }
                        return true;
                    }
                });
            }
            else{
                Bundle extras = data.getExtras();
                Bitmap photoCapturedBitmap = (Bitmap) extras.get("data");
                //Bitmap photoCapturedBitmap = BitmapFactory.decodeFile(mImageFileLocation);
                result2.setImageBitmap(photoCapturedBitmap);

                result2.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN){

                            //  textView.setText("Touch coordinates : " +String.valueOf(event.getX()) + "x" + String.valueOf(event.getY()));
                            X2 = Integer.valueOf((int)event.getX());
                            Y2 = Integer.valueOf((int)event.getY());
                            Bitmap bitmap = ((BitmapDrawable)result2.getDrawable()).getBitmap();

                            result2.buildDrawingCache();
                            Bitmap newBmp = result2.getDrawingCache();
                            newBmp.setPixel(X2, Y2, Color.rgb(45, 127, 0));
                            result2.setImageBitmap(newBmp);
                        }
                        return true;
                    }
                });

            }
        }
    }

    File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";
        File image = File.createTempFile(imageFileName,".jpg",getApplicationContext().getCacheDir());
        mImageFileLocation = image.getAbsolutePath();
        return image;
    }

}
