package com.parse.starter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
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
import com.parse.ParseRelation;
import com.parse.ParseUser;
import java.util.List;

public class following extends AppCompatActivity {
    public static final String TAG = "following";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        Toolbar toolbar = (Toolbar)findViewById(R.id.activity_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        toolbar.setTitleTextColor(Color.WHITE);

        final LinearLayout linearLayout = (LinearLayout)findViewById(R.id.following_linearLayout);

        ParseRelation relation= ParseUser.getCurrentUser().getRelation("following");
        ParseQuery query=relation.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, com.parse.ParseException e) {
                if (e == null) {
                    if (objects.size() != 0) {
                        Log.d(TAG, "following " + objects.size() + " people");
                        for (ParseUser aUser : objects) {
                            final LinearLayout myll = createll();
                            String name = aUser.getString("username");
                            Log.d(TAG, aUser.getString("username"));
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Activity");
                            query.whereEqualTo("fromUser", name);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> activities, com.parse.ParseException e) {
                                    if (e == null) {
                                        if (activities.size() != 0) {
                                            int count=0;
                                            for (ParseObject oneActivity : activities) {
                                                count++;
                                                String a = oneActivity.getString("content");
                                                if (a.equals("liked")) {
                                                    String str = oneActivity.getString("fromUser") + " liked " + oneActivity.getString("toUser") + "'s post";
                                                    Log.d(TAG, str);
                                                    TextView textView = newText(str);
                                                    myll.addView(textView);
                                                } else {
                                                    Log.d(TAG, oneActivity.getString("fromUser") + " started following " +
                                                            oneActivity.getString("toUser"));
                                                    String str = oneActivity.getString("fromUser") + " liked " + oneActivity.getString("toUser") + "'s post";
                                                    Log.d(TAG, str);
                                                    TextView textView = newText(str);
                                                    myll.addView(textView);
                                                }
                                                String fromUserName=oneActivity.getString("fromUser");



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
                                                                suser=objects.get(0);
                                                                //profile photo
                                                                ParseFile photo = (ParseFile)suser.get("photo");
                                                                photo.getDataInBackground(new GetDataCallback() {
                                                                    public void done(byte[] data, ParseException e) {
                                                                        if (e == null) {
                                                                            // the profile photo for the user
                                                                            System.out.println("profile photo fetching successful");
                                                                            Log.d(TAG, "photo");
                                                                            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                                            ImageView image=(ImageView)myll.findViewById(c);
                                                                            image.setImageBitmap(bmp);
                                                                            /*ImageView imageView = getImage();
                                                                            imageView.setImageBitmap(bmp);
                                                                            myll.addView(imageView);*/



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
                                                if(count>=10) break;

                                            }
                                        } else {
                                            Log.d(TAG, "no activity found");
                                        }
                                        linearLayout.addView(myll);
                                    } else {
                                        Log.d(TAG, "Error: " + e.getMessage());
                                    }
                                }
                            });
                        }
                    } else {
                        Log.d(TAG, "the user is not following any people");
                    }
                } else {
                    Log.d(TAG, " something went wrong" + e.getMessage());
                }
            }
        });

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
