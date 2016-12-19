package appsinformaticas.distancias;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.graphics.Color;

import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static appsinformaticas.distancias.R.id.camera;
import static appsinformaticas.distancias.R.id.imageView;

public class FullscreenActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button btnCapturePicture;
    ImageView result;
    ImageView result2;
    TextView text;
    private int imageCount;
    private int X=0,Y=0;
    private int X2=0,Y2=0;
    private float[] base;

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
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

     public String getDistance(){
        float W,W2;

        float focalLength = 10*getCameraFocalLength(1);
        W = X - result.getWidth()/2;
        W2 =  X2 - result2.getWidth()/2;

        double D = pxToMm(Math.abs(W),getApplicationContext()) + pxToMm(Math.abs(W2),getApplicationContext());
        double distancia = (1.5*focalLength * base[0])/D;
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
                result.setImageBitmap(photoCapturedBitmap);

                result.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN){

                            X = (int)event.getX();
                            Y = (int)event.getY();

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
                result2.setImageBitmap(photoCapturedBitmap);

                result2.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN){

                            X2 =(int)event.getX();
                            Y2 = (int)event.getY();

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
}
