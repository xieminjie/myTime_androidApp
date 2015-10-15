package com.parse.starter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class you extends AppCompatActivity {
    public static final String TAG = "you";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you);
        Toolbar toolbar = (Toolbar)findViewById(R.id.activity_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
        toolbar.setTitleTextColor(Color.WHITE);
        final LinearLayout linearLayout = (LinearLayout)findViewById(R.id.you_linearLayout);
        youtweet(linearLayout);
    }
    public LinearLayout youtweet(final LinearLayout linearLayout){
        //activity feed corresponding to instagram activity you
        ParseQuery<ParseObject> query12 = ParseQuery.getQuery("Activity");
        query12.whereEqualTo("toUser", ParseUser.getCurrentUser().getUsername());
        query12.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> activities, ParseException e) {
                if (e == null) {
                    if (activities.size() != 0) {
                        int count=0;
                        final LinearLayout myll = createll();
                        for (ParseObject oneActivity : activities) {
                            count++;
                            if (!oneActivity.getString("content").equals("liked")) {
                                String str = oneActivity.getString("fromUser") + " started following you";
                                Log.d(TAG, str + " you");
                                TextView textView = newText(str);
                                myll.addView(textView);
                                String fromUserName = oneActivity.getString("fromUser");

                                ImageView imageView=getImage();
                                imageView.setId(count);
                                final int c=count;
                                myll.addView(imageView);


                                //fetch the profile photo
                                ParseQuery<ParseUser> query4 = ParseUser.getQuery();
                                query4.whereEqualTo("username", fromUserName);
                                query4.findInBackground(new FindCallback<ParseUser>() {
                                    public void done(List<ParseUser> objects, ParseException e) {
                                        if (objects != null) {
                                            if (objects.size() != 0) {
                                                // The query was successful.
                                                ParseUser suser;
                                                suser = objects.get(0);

                                                //proxfile photo
                                                ParseFile photo = (ParseFile) suser.get("photo");
                                                photo.getDataInBackground(new GetDataCallback() {
                                                    public void done(byte[] data, ParseException e) {
                                                        if (e == null) {
                                                            // the profile photo for the user
                                                            System.out.println("profile photo fetching successful2");
                                                            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                            ImageView image=(ImageView)myll.findViewById(c);
                                                            image.setImageBitmap(bmp);

                                                        } else {
                                                            // something went wrong
                                                        }
                                                    }
                                                });

                                            } else {
                                                Log.d(TAG, "user not found");
                                            }
                                        } else {
                                            // Something went wrong.
                                            Log.d(TAG, " something went wrong" + e.getMessage());
                                        }
                                    }
                                });


                            }
                        }
                        linearLayout.addView(myll);
                    }
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }
        });


        return  linearLayout;
    }
    public ImageView getImage(){
        ImageView currentImageView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
        currentImageView.setLayoutParams(layoutParams);
        currentImageView.setBackgroundColor(Color.BLACK);
        return currentImageView;
    }
    public LinearLayout createll(){
        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setPadding(0, 0, 0, 0);
        return textLayout;
    }
    public TextView newText(String a){
        TextView nameTextView = new TextView(this);
        nameTextView.setText(a);
        nameTextView.setPadding(10, 0, 0, 0);
        nameTextView.setTypeface(null, Typeface.BOLD);
        return nameTextView;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_you, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
