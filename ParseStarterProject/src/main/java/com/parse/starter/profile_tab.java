package com.parse.starter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xieminjie on 15/09/2015.
 */
public class profile_tab extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ParseUser parseUser = ParseUser.getCurrentUser();
        final View view =inflater.inflate(R.layout.profile_tab, container, false);
        final ParseImageView parseImageView = (ParseImageView)view.findViewById(R.id.profile_imageView);
        final ParseFile userphoto = (ParseFile)parseUser.get("photo");
        final TextView postnumTextView = (TextView)view.findViewById(R.id.post_num);
        final TextView followerTextView = (TextView)view.findViewById(R.id.follower_num);
        final TextView followingTextView = (TextView)view.findViewById(R.id.following_num);
        final Activity activity = getActivity();
        final LinearLayout tweetsLayout = (LinearLayout) view.findViewById(R.id.profile_linear);

        // query for post number
        ParseQuery<ParseObject> query3 = ParseQuery.getQuery("Post");
        query3.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        query3.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    if (objects != null) {
                        Log.d("myActivity", "Retrieved posts " + objects.size());
                        postnumTextView.setText(objects.size() + "");
                    } else {
                        postnumTextView.setText("0");
                    }
                } else {
                    postnumTextView.setText("0");
                }
            }

        });
        //query for userphoto
        userphoto.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, com.parse.ParseException e) {
                if (e == null) {
                    parseImageView.setParseFile(userphoto);
                    parseImageView.loadInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, com.parse.ParseException e) {
                            System.out.println("photo done");
                        }
                    });
                } else {
                    parseImageView.setImageResource(R.drawable.ic_action_dock);
                }
            }
        });
        //query for follower number
        ParseQuery<ParseUser> query2 = ParseUser.getQuery();
        query2.whereEqualTo("following", parseUser);

        query2.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, com.parse.ParseException e) {
                if (e == null) {
                    if (objects.size() != 0) {
                        followerTextView.setText(objects.size() + "");
                    } else {
                        followerTextView.setText("0");
                    }
                } else {
                    followerTextView.setText("0");
                }
            }
        });
        //get number of people current user is following
        ParseRelation relation=parseUser.getRelation("following");
        ParseQuery query=relation.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, com.parse.ParseException e) {
                if (e == null) {
                    if(objects.size()!=0) {
                        followingTextView.setText(objects.size()+"");
                    }
                    else{
                        followerTextView.setText("0");
                    }
                } else {
                    followerTextView.setText("0");
                }
            }
        });

        //Query for user post photo
        ParseQuery<ParseObject> query9 = ParseQuery.getQuery("Post");
        query9.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        query9.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> posts, com.parse.ParseException e) {
                if (e == null) {
                    Log.d("myActivity", "Retrieved posts " + posts.size() );
                    final LinearLayout myll = getLinearLayout(activity);
                    if(posts.size()>0){
                        for(ParseObject aPost:posts){
                            ParseFile imageFile = aPost.getParseFile("photo");
                            imageFile.getDataInBackground(new GetDataCallback() {
                                public void done(byte[] data, com.parse.ParseException e) {
                                    if (e == null) {
                                        // data has the bytes for the resume
                                        System.out.println("photo fetching successful");
                                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        ImageView image = newiv(activity);
                                        image.setImageBitmap(bmp);
                                        myll.addView(image);

                                    } else {
                                        // something went wrong
                                    }
                                }
                            });
                        }
                    }
                    tweetsLayout.addView(myll);
                } else {
                    Log.d("myActivity", "Error: " + e.getMessage());
                }
            }

        });
        return view;
    }
    public ImageView newiv(Activity activity){
        ImageView image=new ImageView(activity);
        return image;
    }
    public LinearLayout getLinearLayout(Activity activity){
        LinearLayout textLayout = new LinearLayout(activity);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setPadding(0, 0, 0, 0);
        return textLayout;
    }
}

