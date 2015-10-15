package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by xieminjie on 1/10/2015.
 */
public class discovery_tab extends Fragment implements SearchView.OnQueryTextListener{
    public static final String TAG = "discovery";
    private SearchView userSearchView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.discovery_tab, container, false);
        userSearchView = (SearchView)view.findViewById(R.id.discovery_SearchView);
        userSearchView.setOnQueryTextListener(this);
        final LinearLayout tweetsLayout = (LinearLayout) view.findViewById(R.id.discovery_linearLayout);
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
        final ParseUser currentUser = ParseUser.getCurrentUser();
        // recommend user
        ParseRelation relation=ParseUser.getCurrentUser().getRelation("following");
        ParseQuery query10=relation.getQuery();

        query10.findInBackground(new FindCallback<ParseUser>() {
            public void done(final List<ParseUser> following, ParseException e) {
                if (e == null) {
                    // The query was successful.
                    if(following.size()!=0) {
                        Log.d(TAG, "following " + following.size() + " people");


                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        query.findInBackground(new FindCallback<ParseUser>() {
                            public void done(List<ParseUser> allUsers, ParseException e) {
                                if (allUsers != null) {
                                    if (allUsers.size() != 0) {
                                        // The query was successful.
                                        for (final ParseUser aUser:allUsers){

                                            ParseQuery<ParseUser> query = ParseUser.getQuery();
                                            query.whereEqualTo("following", aUser);

                                            query.findInBackground(new FindCallback<ParseUser>() {
                                                public void done(List<ParseUser> followers, ParseException e) {
                                                    final Activity activity = getActivity();
                                                    final LinearLayout myll = createll(activity);
                                                    if (e == null) {
                                                        // The query was successful.
                                                        if(followers.size()!=0) {

                                                            int count=0;

                                                            for(ParseUser user1:followers){
                                                                if(following.contains(user1)&&!aUser.equals(ParseUser.getCurrentUser())&&!following.contains(aUser))
                                                                    count++;
                                                            }

                                                            if(count>=1) {
                                                                if((!aUser.equals(ParseUser.getCurrentUser()))&&(!following.contains(aUser))){

                                                                //recommend a user
                                                                final String username = aUser.getString("username");
                                                                Log.d(TAG, "recommand " + username);
                                                                final TextView nameTextView = newText(username, activity);
                                                                final Button followButton = createFollowedButton(activity);
                                                                followButton.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                       // Toast.makeText(activity, "followed", Toast.LENGTH_SHORT).show();
                                                                        Intent intent = new Intent();
                                                                        intent.setClass(getActivity(), SearchUser.class);
                                                                        intent.putExtra("name",username);
                                                                        startActivity(intent);

                                                                        // add following relationship
                                                                       /* ParseQuery<ParseUser> query4 = ParseUser.getQuery();
                                                                        Log.d(TAG, "query " + username);
                                                                        query4.whereEqualTo("username", username);
                                                                        query4.findInBackground(new FindCallback<ParseUser>() {
                                                                            public void done(List<ParseUser> objects, ParseException e) {
                                                                                if (objects != null) {
                                                                                    if (objects.size() != 0) {
                                                                                        // The query was successful.
                                                                                        ParseRelation<ParseObject> relation = currentUser.getRelation("following");
                                                                                        relation.add(objects.get(0));
                                                                                        currentUser.saveInBackground();
                                                                                        followButton.setText("followed");
                                                                                        Log.d(TAG, "followed");
                                                                                        //Must include the activity if you follow someone, so that we can keep track of who follows whom
                                                                                        ParseObject activity = new ParseObject("Activity");
                                                                                        activity.put("content", "started following");
                                                                                        activity.put("fromUser", ParseUser.getCurrentUser().getUsername());
                                                                                        activity.put("toUser", objects.get(0).getString("username"));
                                                                                        activity.saveInBackground();
                                                                                    } else {
                                                                                        Log.d(TAG, "user not found");
                                                                                    }
                                                                                } else {
                                                                                    // Something went wrong.
                                                                                    Log.d(TAG, " something went wrong" + e.getMessage());
                                                                                }
                                                                            }
                                                                        });*/
                                                                    }
                                                                });
                                                                myll.addView(followButton);
                                                                myll.addView(nameTextView);
                                                            }
                                                            }
                                                            tweetsLayout.addView(myll);
                                                        }
                                                        else{
                                                            Log.d(TAG,"the current user is not followed by any people");
                                                        }

                                                    } else {
                                                        // Something went wrong.
                                                        Log.d(TAG, " something went wrong" + e.getMessage());
                                                    }
                                                }
                                            });

                                        }

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
                    else{
                        Log.d(TAG,"the user is not following any people");
                    }


                } else {
                    // Something went wrong.
                    Log.d(TAG, " something went wrong" + e.getMessage());
                }
            }
        });
        return view;
    }
    public Button createFollowedButton(Activity activity){
        Button likeBtn = new Button(activity);
        likeBtn.setText("Profile");
        return likeBtn;
    }
    public TextView newText(String a,Activity activity){
        TextView nameTextView = new TextView(activity);
        nameTextView.setText(a);
        nameTextView.setPadding(10, 0, 0, 0);
        nameTextView.setTypeface(null, Typeface.BOLD);
        return nameTextView;
    }
    public LinearLayout createll(Activity activity){
        LinearLayout textLayout = new LinearLayout(activity);
        textLayout.setOrientation(LinearLayout.HORIZONTAL);
        textLayout.setPadding(0, 0, 0, 0);
        return textLayout;
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        System.out.println(query);
        Intent intent = new Intent();
        intent.setClass(getActivity(), SearchUser.class);
        intent.putExtra("name", query);
        startActivity(intent);
        userSearchView.clearFocus();
        return false;
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
