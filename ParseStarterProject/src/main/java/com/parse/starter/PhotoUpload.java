package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;

/**
 * Created by Jizhizi Li on 15/09/2015.
 */
public class PhotoUpload extends AppCompatActivity {
    private ImageView uploadImage;
    private Bitmap bitmap;
    private Button upload;
    private EditText captionText;
    private EditText locationText;

    private Button shareBT;
    private Button home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload);
        //<------------------------Matching With ID-------------------------->
        uploadImage = (ImageView) findViewById(R.id.uploadimage);

        locationText = (EditText)findViewById(R.id.location);
        captionText = (EditText) findViewById(R.id.caption);
        upload = (Button) findViewById(R.id.upload);
        shareBT = (Button)findViewById(R.id.shareBT);
        home = (Button)findViewById(R.id.home);

        //<------------------------Receiving image from last intent-------------------------->
        if (getIntent().hasExtra("byteArray")) {

            Bitmap b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"), 0, getIntent().getByteArrayExtra("byteArray").length);
            uploadImage.setImageBitmap(Bitmap.createScaledBitmap(b, 100, 100, false));
            bitmap = b;
        }
        //<------------------------Button UPLOAD-------------------------->
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] image = stream.toByteArray();

                ParseFile file = new ParseFile("postimage", image);
                ParseObject post = new ParseObject("Post");
                post.put("location", locationText.getText().toString());
                post.put("caption", captionText.getText().toString());
                post.put("createdBy", ParseUser.getCurrentUser());
                post.put("photo", file);
                post.saveInBackground();

                Toast.makeText(getApplicationContext(),"upload success",Toast.LENGTH_SHORT).show();


            }
        });

        //<------------------------Button ShareBT-------------------------->
        shareBT.setOnClickListener(new View.OnClickListener(){
            @Override
        public void onClick(View v){
                Intent bt = new Intent(PhotoUpload.this,BlueTooth.class);

                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bs);
                bt.putExtra("byteArray", bs.toByteArray());


                startActivity(bt);

            }


        });

        //<------------------------Button Home------------------------->
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhotoUpload.this, MainActivity.class);


                startActivity(intent);

            }


        });





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_upload, menu);
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
