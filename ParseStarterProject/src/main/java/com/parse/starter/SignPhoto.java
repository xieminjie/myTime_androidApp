package com.parse.starter;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;

public class SignPhoto extends AppCompatActivity {
    private Toolbar toolbar;
    private Button choose;
    private ImageView imageview;
    static final int REQUEST_IMAGE_CAPTURE=1;
    static final int CHOOSE_FROM_GALLERY =2;
    private Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_photo);


        toolbar = (Toolbar)findViewById(R.id.login_bar);
        imageview = (ImageView)findViewById(R.id.imageView);

        choose = (Button)findViewById(R.id.choose);


        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);
        Button signPhoto = (Button)findViewById(R.id.signPhoto_Btn);


        //<----------button choose---------->

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFromGallery(v);
            }

        });


        //<----------button signphoto---------->
        signPhoto.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){



//upload photo
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] image = stream.toByteArray();

                        ParseFile file = new ParseFile("photo", image);

                        ParseUser user = new ParseUser();

                        user = ParseUser.getCurrentUser();
                        user.put("photo",file);
                        file.saveInBackground();
                        user.saveInBackground();

//to new intent
                        intentToLogin();
                    }
                }
        );
    }
    private void intentToLogin(){
        Intent intent = new Intent(SignPhoto.this,Login.class);
        startActivity(intent);
    }

    public void chooseFromGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, CHOOSE_FROM_GALLERY);
    }


    @Override
    protected void onActivityResult(int requestcode,int resultCode, Intent data){

        if (requestcode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {

            //Take the photo by system's camera, which is not used in this project.
            Bundle extras = data.getExtras();
            photo = (Bitmap) extras.get("data");

        } else if (requestcode == CHOOSE_FROM_GALLERY) {
            //choose from gallery
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            photo = (BitmapFactory.decodeFile(picturePath));
imageview.setImageBitmap(Bitmap.createScaledBitmap(photo,300,300,false));


        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_photo, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if(id==android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
