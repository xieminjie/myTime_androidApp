package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * Created by Jizhizi Li on 15/09/2015.
 */
public class PhotoFilters extends AppCompatActivity {
    private ImageView filterImage;
    private Bitmap resultBitmap;
    private Bitmap originalBitmap;
    private Button laststep;
    private Button nextstep;
    private ImageButton filterInvert;
    private ImageButton filterGray;
    private ImageButton filterOld;
    private ImageButton filterOriginal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_filters);
        filterImage = (ImageView) findViewById(R.id.filterimage);
        filterInvert = (ImageButton) findViewById(R.id.invert);
        filterGray = (ImageButton) findViewById(R.id.gray);
        filterOld = (ImageButton) findViewById(R.id.old);
        laststep = (Button) findViewById(R.id.laststep);
        nextstep = (Button) findViewById(R.id.nextstep);
        filterOriginal = (ImageButton)findViewById(R.id.original);




        //<------------------------Redeving image from last intent-------------------------->
        if (getIntent().hasExtra("byteArray")) {

            Bitmap b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"), 0, getIntent().getByteArrayExtra("byteArray").length);
            filterImage.setImageBitmap(b);
            resultBitmap = b;
            originalBitmap = b;
        }


        BitmapDrawable invertBitmap = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(changeToInvert(originalBitmap),100,100,false));
        filterInvert.setBackgroundDrawable(invertBitmap);
        BitmapDrawable grayBitmap = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(GrayFilter(originalBitmap),100,100,false));
        filterGray.setBackgroundDrawable(grayBitmap);
        BitmapDrawable oldBitmap = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(changeToOld(originalBitmap),100,100,false));
        filterOld.setBackgroundDrawable(oldBitmap);
        BitmapDrawable original = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(originalBitmap,100,100,false));
        filterOriginal.setBackgroundDrawable(original);




        //<------------------------Button NEXTSTEP-------------------------->
        nextstep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhotoFilters.this, PhotoUpload.class);
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                resultBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                intent.putExtra("byteArray", bs.toByteArray());
                startActivity(intent);
            }
        });
        //<------------------------Button FILTER_INVERT-------------------------->
        filterInvert.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                filterImage.setImageBitmap(changeToInvert(originalBitmap));
                resultBitmap = changeToInvert(originalBitmap);
            }


        });
        //<------------------------Button FILTER_GRAY-------------------------->
        filterGray.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                filterImage.setImageBitmap(GrayFilter(originalBitmap));
                resultBitmap = GrayFilter(originalBitmap);


            }


        });
        //<------------------------Button FILTER_OLD-------------------------->
        filterOld.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                filterImage.setImageBitmap(changeToOld(originalBitmap));
                resultBitmap = changeToOld(originalBitmap);
            }


        });

        //<------------------------Button FILTER_OLD-------------------------->
        filterOriginal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                filterImage.setImageBitmap(originalBitmap);
                resultBitmap = originalBitmap;
            }


        });


    }










    //<------------------------Filter_1_INVERT(Method)-------------------------->
    public static Bitmap changeToInvert(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap returnBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        int colorArray[] = new int[width * height];
        int r, g, b;
        bitmap.getPixels(colorArray, 0, width, 0, 0, width, height);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                r = 255 - Color.red(colorArray[y * width + x]);
                g = 255 - Color.green(colorArray[y * width + x]);
                b = 255 - Color.blue(colorArray[y * width + x]);

                colorArray[y * width + x] = Color.rgb(r, g, b);
                returnBitmap.setPixel(x, y, colorArray[y * width + x]);
            }
        }

        return returnBitmap;

    }


    //<------------------------Filter_2_GRAY(Method)-------------------------->
    public static Bitmap GrayFilter(Bitmap bitmap) {

        int width, height;
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);

        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return grayBitmap;


    }

    //<------------------------Filter_3_OLD(Method)-------------------------->
    public static Bitmap changeToOld(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();


        int pixColor = 0;
        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        int newR = 0;
        int newG = 0;
        int newB = 0;
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < height; i++) {
            for (int k = 0; k < width; k++) {
                pixColor = pixels[width * i + k];
                pixR = Color.red(pixColor);
                pixG = Color.green(pixColor);
                pixB = Color.blue(pixColor);
                newR = (int) (0.393 * pixR + 0.769 * pixG + 0.189 * pixB);
                newG = (int) (0.349 * pixR + 0.686 * pixG + 0.168 * pixB);
                newB = (int) (0.272 * pixR + 0.534 * pixG + 0.131 * pixB);
                int newColor = Color.argb(255, newR > 255 ? 255 : newR, newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);
                pixels[width * i + k] = newColor;
            }
        }
        Bitmap returnBitmap = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
        return returnBitmap;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_filters, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
