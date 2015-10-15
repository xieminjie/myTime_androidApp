package com.parse.starter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Jizhizi Li on 15/09/2015.
 */
public class TakePhoto extends Activity implements SurfaceHolder.Callback {

    static final int CROP_IMAGE_CAMERA=1;
    static final int PHOTO_WIDTH = 300;
    static final int PHOTO_HEIGHT = 300;
    static final int CROP_WIDTH=300;
    static final int CROP_HEIGHT=300;

    private Camera camera = null;
    private SurfaceView cameraSurfaceView = null;
    private SurfaceHolder cameraSurfaceHolder = null;
    private boolean previewing = false;
    RelativeLayout relativeLayout;
    private Switch flash;
    private boolean setFlash = false;
    private Switch grid;
    private ImageButton captureImage = null;
    private Uri picUri;
    private ImageView gridImage;
    private Paint paint = new Paint();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);


        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_take_photo);

        //<------------------------Matching With ID-------------------------->
        gridImage = (ImageView) findViewById(R.id.gridImage);
        flash = (Switch) findViewById(R.id.flash);
        flash.setChecked(false);
        grid = (Switch) findViewById(R.id.grid);
        grid.setChecked(true);
        captureImage = (ImageButton) findViewById(R.id.captureImage);

        relativeLayout = (RelativeLayout) findViewById(R.id.containerImg);
        relativeLayout.setDrawingCacheEnabled(true);
        cameraSurfaceView = (SurfaceView)
                findViewById(R.id.surfaceView1);
        cameraSurfaceHolder = cameraSurfaceView.getHolder();

        //<-------set the will not draw to false------->
        cameraSurfaceView.setWillNotDraw(false);


        //<--------set end------------->



        cameraSurfaceHolder.addCallback(this);


        //<------------------------Switch FLASH-------------------------->

        flash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    System.out.println("flash is on");
                    setFlash = true;
                } else {
                    System.out.println("flash is off");
                    setFlash = false;
                }
            }
        });

        //<------------------------Switch GRID-------------------------->
        grid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gridImage.setVisibility(View.VISIBLE);

                } else {
                    gridImage.setVisibility(View.INVISIBLE);
                }
            }


        });
        //<------------------------Button CAPTUREIMAGE-------------------------->
        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (setFlash == true) {
                    //with Flash On when Taking Picutre
                    Camera.Parameters p = camera.getParameters();
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(p);
                }
                camera.takePicture(cameraShutterCallback,
                        cameraPictureCallbackRaw,
                        cameraPictureCallbackJpeg);


            }
        });
    }

    Camera.ShutterCallback cameraShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            // TODO Auto-generated method stub
        }
    };

    Camera.PictureCallback cameraPictureCallbackRaw = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
        }
    };

    Camera.PictureCallback cameraPictureCallbackJpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Bitmap cameraBitmap = BitmapFactory.decodeByteArray
                    (data, 0, data.length);
            int wid = cameraBitmap.getWidth();
            int hgt = cameraBitmap.getHeight();

            Bitmap newImage = Bitmap.createBitmap
                    (wid, hgt, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(newImage);

            canvas.drawBitmap(cameraBitmap, 0f, 0f, null);

//<-----------------try to use ondraw------------->


          //  onDraw(canvas);

            //<----------------try end ------------------>


            //Rotate picture
            Matrix matrix = new Matrix();

            matrix.postRotate(90);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(newImage, PHOTO_WIDTH, PHOTO_HEIGHT, true);

            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

            newImage = rotatedBitmap;

            //<------------------------Store Picture in External Storage-------------------------->

            File storagePath = new File(Environment.
                    getExternalStorageDirectory() + "/MyTime/");
            storagePath.mkdirs();

            File myImage = new File(storagePath,
                    Long.toString(System.currentTimeMillis()) + ".jpg");
            //convert it to uri
            picUri = Uri.fromFile(myImage);

            try {
                FileOutputStream out = new FileOutputStream(myImage);
                newImage.compress(Bitmap.CompressFormat.JPEG, 80, out);
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                Log.d("In Saving File", e + "");
            } catch (IOException e) {
                Log.d("In Saving File", e + "");
            }
            //<------------------------Crop Image-------------------------->
            performCrop();
            newImage.recycle();
            newImage = null;
        }
    };

    @Override
    public void surfaceChanged(SurfaceHolder holder,
                               int format, int width, int height) {
        // TODO Auto-generated method stub

        if (previewing) {
            camera.stopPreview();
            previewing = false;
        }
        try {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(640, 480);
            parameters.setPictureSize(640, 480);
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                camera.setDisplayOrientation(90);

            }

            // parameters.setRotation(90);
            camera.setParameters(parameters);

            camera.setPreviewDisplay(cameraSurfaceHolder);
            camera.startPreview();
            previewing = true;



        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        try {

            //<-----------try to ondraw----------->
//            Canvas canvas = holder.lockCanvas();
//onDraw(canvas);
          //  holder.unlockCanvasAndPost(canvas);
            //<----------end of try-------->
            camera = Camera.open();

        } catch (RuntimeException e) {
            Toast.makeText(getApplicationContext(), "Device camera  is not working properly, please try after sometime.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }

    //<------------------------Crop Method-------------------------->
    private void performCrop() {
        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", CROP_WIDTH);
            cropIntent.putExtra("outputY", CROP_HEIGHT);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);

            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_IMAGE_CAMERA);

        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    @Override
    protected void onActivityResult(int requestcode, int resultCode, Intent data) {

        if (requestcode == CROP_IMAGE_CAMERA) {
            Bundle extras = data.getExtras();
            Bitmap thePic = extras.getParcelable("data");

            //<-------------------PASS TO NEW INTENT-------------------->
            Intent intent = new Intent(this, PhotoEdit.class);
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            thePic.compress(Bitmap.CompressFormat.PNG, 50, bs);
            intent.putExtra("byteArray", bs.toByteArray());
            startActivity(intent);

        }
    }






    protected void onDraw(Canvas canvas)
    {
        // if(grid){
        //  Find Screen size first
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = (int) (metrics.heightPixels*0.9);

        //  Set paint options
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);

        canvas.drawLine((screenWidth/3)*2,0,(screenWidth/3)*2,screenHeight,paint);
        canvas.drawLine((screenWidth/3),0,(screenWidth/3),screenHeight,paint);
        canvas.drawLine(0,(screenHeight/3)*2,screenWidth,(screenHeight/3)*2,paint);
        canvas.drawLine(0,(screenHeight/3),screenWidth,(screenHeight/3),paint);
        //}
    }

}
