package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

public class SearchUser extends AppCompatActivity {
    public static final String TAG = "SearchUser";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        System.out.println("get " + name);
        Toolbar toolbar = (Toolbar)findViewById(R.id.activity_bar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        Log.d(TAG,name);

        final ParseImageView userphoto = (ParseImageView)findViewById(R.id.searchuserphoto);
        final TextView postnumtextView = (TextView)findViewById(R.id.searchpostnum);
        final TextView searchfollower = (TextView)findViewById(R.id.searchfollower);
        final TextView searchfollowed = (TextView)findViewById(R.id.searchfollowing);
        final Button followBtn = (Button)findViewById(R.id.searchfollowbtn);
        final LinearLayout linearLayout = (LinearLayout)findViewById(R.id.searchuser_linearLayout);

        ParseQuery<ParseUser> query4 = ParseUser.getQuery();
        query4.whereEqualTo("username",name);
        query4.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (objects != null) {
                    if (objects.size() != 0) {
                        // The query was successful.
                        final ParseUser suser;
                        suser = objects.get(0);
                        followBtn.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                ParseRelation<ParseObject> relation = ParseUser.getCurrentUser().getRelation("following");
                                relation.add(suser);
                                followBtn.setText("followed");
                                Toast.makeText(getBaseContext(),"followed",Toast.LENGTH_SHORT).show();
                                ParseUser.getCurrentUser().saveInBackground();
                                //Must include the activity if you follow someone, so that we can keep track of who follows whom
                                ParseObject activity = new ParseObject("Activity");
                                activity.put("content", "started following");
                                activity.put("fromUser", ParseUser.getCurrentUser().getUsername());
                                activity.put("toUser", suser.getString("username"));
                                activity.saveInBackground();
                            }
                        });
                        //follow the searched user




                        //get the number of posts of searched user
                        ParseQuery<ParseObject> query3 = ParseQuery.getQuery("Post");
                        query3.whereEqualTo("createdBy", suser);
                        query3.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> posts, ParseException e) {
                                if (e == null) {
                                    Log.d(TAG, "Retrieved posts " + posts.size());

                                    postnumtextView.setText(posts.size()+"");

                                } else {
                                    Log.d(TAG, "Error: " + e.getMessage());
                                }
                            }
                        });


                        //get number of followers
                        ParseQuery<ParseUser> query2 = ParseUser.getQuery();
                        query2.whereEqualTo("following", suser);

                        query2.findInBackground(new FindCallback<ParseUser>() {
                            public void done(List<ParseUser> objects, ParseException e) {
                                if (e == null) {
                                    // The query was successful.
                                    if (objects.size() != 0) {

                                        Log.d(TAG, "followedBy " + objects.size() + " people");
                                        Log.d(TAG, objects.get(0).getString("name"));
                                        searchfollower.setText(objects.size() + "");
                                    } else {
                                        Log.d(TAG, "the current user is not followed by any people");
                                    }

                                } else {
                                    // Something went wrong.
                                    Log.d(TAG, " something went wrong" + e.getMessage());
                                }
                            }
                        });


                        ParseRelation relation1 = suser.getRelation("following");
                        ParseQuery query = relation1.getQuery();
                        query.findInBackground(new FindCallback<ParseUser>() {
                            public void done(List<ParseUser> objects, ParseException e) {
                                if (e == null) {
                                    // The query was successful.

                                    if (objects.size() != 0) {
                                        Log.d(TAG, "following " + objects.size() + " people");
                                        Log.d(TAG, objects.get(0).getString("name"));
                                        searchfollowed.setText(objects.size() + "");
                                    } else {
                                        Log.d(TAG, "the user is not following any people");
                                    }


                                } else {
                                    // Something went wrong.
                                    Log.d(TAG, " something went wrong" + e.getMessage());
                                }
                            }
                        });

                        //all the photos of the searched user
                        ParseQuery<ParseObject> query9 = ParseQuery.getQuery("Post");
                        query9.whereEqualTo("createdBy", suser);
                        query9.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> posts, ParseException e) {
                                if (e == null) {
                                    Log.d(TAG, "Retrieved posts " + posts.size() );
                                    final LinearLayout myll = getLinearLayout();
                                    if(posts.size()>0){
                                        for(ParseObject aPost:posts){
                                            ParseFile photo = (ParseFile)aPost.get("photo");
                                            photo.getDataInBackground(new GetDataCallback() {
                                                public void done(byte[] data, ParseException e) {
                                                    if (e == null) {
                                                        System.out.println("all the photos fetching successful");
                                                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                        ImageView image = newiv();
                                                        image.setImageBitmap(bmp);
                                                        myll.addView(image);

                                                    } else {
                                                        // something went wrong
                                                    }
                                                }
                                            });


                                        }
                                    }
                                    linearLayout.addView(myll);

                                } else {
                                    Log.d(TAG, "Error: " + e.getMessage());
                                }
                            }
                        });


                        //profile photo
                        final ParseFile photo = (ParseFile) suser.get("photo");
                        photo.getDataInBackground(new GetDataCallback() {
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    // data has the bytes for the resume
                                    userphoto.setParseFile(photo);
                                    userphoto.loadInBackground(new GetDataCallback() {
                                        @Override
                                        public void done(byte[] data, com.parse.ParseException e) {
                                            System.out.println("photo done");
                                        }
                                    });

                                } else {
                                    userphoto.setImageResource(R.drawable.ic_action_dock);
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
    public ImageView newiv(){
        ImageView image=new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
        image.setLayoutParams(layoutParams);
        image.setBackgroundColor(Color.BLACK);
        return image;
    }
    public LinearLayout getLinearLayout(){
        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setPadding(0, 0, 0, 0);
        return textLayout;
    }

}
