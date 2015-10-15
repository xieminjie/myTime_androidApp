package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by xieminjie on 15/09/2015.
 */
public class main_tab extends Fragment {
    public static final String TAG = "myActivity";
    public static ParseUser myUser;
    //ArrayList<Tweet> arrayList = new ArrayList<>();
    //private TweetParseAdapter tweetParseAdapter;
    //private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.main_tab, container, false);
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
        final LinearLayout tweetsLayout = (LinearLayout) view.findViewById(R.id.tweetsLayout);
        tweetDate(tweetsLayout);
        Switch switch1 = (Switch) view.findViewById(R.id.tweet_switch);
        switch1.setChecked(true);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getActivity(),"Ordered by Time",Toast.LENGTH_SHORT ).show();
                    tweetsLayout.removeAllViews();
                    tweetDate(tweetsLayout);
                } else {
                    Toast.makeText(getActivity(),"Ordered by Location",Toast.LENGTH_SHORT ).show();
                    tweetsLayout.removeAllViews();
                    tweetLocation(tweetsLayout);
                }
            }
        });
        return  view;
    }



    public LinearLayout tweetLocation(final LinearLayout tweetsLayout){
        ParseQuery<ParseObject> query8 = ParseQuery.getQuery("Post");
        query8.orderByAscending("location");
        query8.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> posts, ParseException e) {
                if (e == null) {
                    Log.d("post", "Retrieved " + posts.size() + " posts in total");

                    ParseRelation relation = ParseUser.getCurrentUser().getRelation("following");
                    ParseQuery query = relation.getQuery();
                    query.findInBackground(new FindCallback<ParseUser>() {
                        public void done(final List<ParseUser> objects, ParseException e) {
                            if (e == null) {
                                // The query was successful.
                                if (objects.size() != 0) {
                                    Log.d(TAG, "following " + objects.size() + " people");
                                    //Log.d(TAG, objects.get(0).getString("name"));
                                    for (final ParseObject aPost : posts) {
                                        //System.out.println(aPost.getString("location"));

                                        aPost.getParseObject("createdBy").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                            public void done(ParseObject aUser, ParseException e) {
                                                if (e == null) {

                                                    if (objects.contains(aUser)) {
                                                        int idcount=0;
                                                        final Activity activity = getActivity();
                                                        final LinearLayout myll = createll(activity);
                                                        //username of the post
                                                        String author = aUser.getString("username");
                                                        Log.d(TAG, "created by " + author);
                                                        TextView t1 = newText( author, activity);
                                                        myll.addView(t1);

                                                        //location of the post
                                                        System.out.println("post is " + aPost.getString("location"));
                                                        TextView t2 = newText("Location: " + aPost.getString("location"), activity);
                                                        myll.addView(t2);


                                                        //post time
                                                        Date date = aPost.getCreatedAt();
                                                        System.out.println("post created at" + date.toString());
                                                        TextView t3 = newText("Created at: " + date.toString(), activity);
                                                        myll.addView(t3);
                                                        //ParseImageView parseImageView = new ParseImageView(activity);

                                                        ImageView image = newiv(activity);
                                                        image.setId(idcount);
                                                        final int idnum=idcount;
                                                        idcount++;
                                                        myll.addView(image);
                                                        // post picture
                                                        ParseFile photo = aPost.getParseFile("photo");
                                                        photo.getDataInBackground(new GetDataCallback() {
                                                            public void done(byte[] data, ParseException e) {
                                                                if (e == null) {
                                                                    // data has the bytes for the resume
                                                                    System.out.println("photo fetching successful");
                                                                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

                                                                    ImageView image = (ImageView) myll.findViewById(idnum);
                                                                    image.setImageBitmap(bmp);
                                                                    /*ImageView image = newiv(activity);
                                                                    image.setImageBitmap(bmp);
                                                                    myll.addView(image);*/

                                                                } else {
                                                                    // something went wrong
                                                                }
                                                            }
                                                        });



                                                        TextView t4=newText("\n", activity);
                                                        t4.setId(idcount);
                                                        final int idnum4=idcount;
                                                        idcount++;
                                                        myll.addView(t4);


                                                        //get comments and authors
                                                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comment");
                                                        query.whereEqualTo("commentedOn", aPost);
                                                        query.findInBackground(new FindCallback<ParseObject>() {
                                                            public void done(List<ParseObject> objects, ParseException e) {
                                                                if (e == null) {
                                                                    if (objects.size() != 0) {
                                                                        // The query was successful.

                                                                        Log.d(TAG, "commented by " + objects.size() + " people");


                                                                        for (int i = 0; i < objects.size(); i++) {
                                                                            Log.d(TAG, objects.get(i).getString("content"));
                                                                            TextView t4=(TextView)myll.findViewById(idnum4);

                                                                            t4.append(objects.get(i).getString("content")+"\n");
                                                                            /*

                                                                            TextView t3 = newText("comments: " + objects.get(i).getString("content"), activity);
                                                                            myll.addView(t3);*/
                                                                            objects.get(i).getParseObject("createdBy")
                                                                                    .fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                                                                        public void done(ParseObject aUser, ParseException e) {
                                                                                            String author = aUser.getString("name");
                                                                                            Log.d(TAG, "created by " + author);
                                                                                            TextView t4 = (TextView) myll.findViewById(idnum4);
                                                                                            t4.append("Created By " + author+"\n");
                                                                                            /*
                                                                                            TextView t4 = newText("comment createdBy" + author, activity);
                                                                                            myll.addView(t4);*/

                                                                                        }
                                                                                    });
                                                                        }

                                                                    } else {
                                                                        Log.d(TAG, "not commented on by anyone");
                                                                    }

                                                                } else {
                                                                    // Something went wrong.
                                                                    Log.d(TAG, " something went wrong" + e.getMessage());
                                                                }
                                                            }
                                                        });


                                                        TextView t5=newText(" ", activity);
                                                        t5.setId(idcount);
                                                        final int idnum5=idcount;
                                                        idcount++;
                                                        myll.addView(t5);
                                                        // get  likeBy usernames
                                                        ParseRelation relation = aPost.getRelation("likedBy");
                                                        ParseQuery query2 = relation.getQuery();
                                                        query2.findInBackground(new FindCallback<ParseUser>() {
                                                            public void done(List<ParseUser> objects, ParseException e) {
                                                                if (e == null) {
                                                                    if (objects.size() != 0) {
                                                                        // The query was successful.

                                                                        Log.d(TAG, "likedBy " + objects.size() + " people");
                                                                        TextView t5 =(TextView)myll.findViewById(idnum5);
                                                                        t5.setText(objects.size() + " likes: ");
                                                                        /*
                                                                        TextView t5 = newText(objects.size() + " likes", activity);
                                                                        myll.addView(t5);*/

                                                                        for (int i = 0; i < objects.size(); i++) {
                                                                            Log.d(TAG, objects.get(i).getString("username"));
                                                                            t5.append(objects.get(i).getString("username")+" ");

                                                                            /*
                                                                            TextView t6 = newText("likes: " + objects.get(i).getString("username"), activity);
                                                                            myll.addView(t6);*/
                                                                        }
                                                                    } else {
                                                                        Log.d(TAG, "not liked by anyone");
                                                                        TextView t5 =(TextView)myll.findViewById(idnum5);
                                                                        t5.setText(objects.size() + " likes");
                                                                        /*TextView t5 = newText(objects.size() + " likes", activity);
                                                                        myll.addView(t5);*/
                                                                    }

                                                                } else {
                                                                    // Something went wrong.
                                                                    Log.d(TAG, " something went wrong" + e.getMessage());
                                                                }
                                                            }
                                                        });
                                                        final EditText commentEditText = createcommentText(activity);
                                                        Button commentBtn = createCommentBtn(activity);
                                                        commentBtn.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Toast.makeText(activity, "comment", Toast.LENGTH_SHORT).show();
                                                                String userComment = commentEditText.getText().toString();
                                                                ParseObject comment = new ParseObject("Comment");
                                                                comment.put("content", userComment);
                                                                comment.put("commentedOn", aPost);
                                                                comment.put("createdBy", ParseUser.getCurrentUser());
                                                                comment.saveInBackground();
                                                            }
                                                        });

                                                        Button likeBtn = createLikeBtn(activity);
                                                        likeBtn.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Toast.makeText(activity, "like", Toast.LENGTH_SHORT).show();
                                                                ParseRelation<ParseObject> likeRelation = aPost.getRelation("likedBy");
                                                                likeRelation.add(ParseUser.getCurrentUser());
                                                                aPost.saveInBackground();
                                                                Log.d(TAG, "liked a post");
                                                                ParseObject activity = new ParseObject("Activity");
                                                                activity.put("content", "liked");
                                                                activity.put("fromUser", ParseUser.getCurrentUser().getUsername());
                                                                activity.put("toUser", aPost.getParseObject("createdBy").getString("username"));
                                                                activity.saveInBackground();
                                                            }
                                                        });
                                                        myll.addView(commentBtn);
                                                        myll.addView(likeBtn);
                                                        myll.addView(commentEditText);
                                                        tweetsLayout.addView(myll);

                                                    }

                                                }
                                                // Do something with your new title variable
                                            }
                                        });

                                    }
                                } else {
                                    Log.d(TAG, "the user is not following any people");
                                }


                            } else

                            {
                                // Something went wrong.
                                Log.d(TAG, " something went wrong" + e.getMessage());
                            }
                        }
                    });


                } else

                {
                    Log.d("post", "Error: " + e.getMessage());
                }
            }
        });
        return tweetsLayout;
    }










    public LinearLayout tweetDate(final LinearLayout tweetsLayout){
        ParseQuery<ParseObject> query8 = ParseQuery.getQuery("Post");
        // sort by date
        // query8.orderByDescending(search);
        // sort by location
        query8.orderByDescending("createdAt");
        query8.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> posts, ParseException e) {
                if (e == null) {
                    Log.d("post", "Retrieved " + posts.size() + " posts in total");

                    ParseRelation relation = ParseUser.getCurrentUser().getRelation("following");
                    ParseQuery query = relation.getQuery();
                    query.findInBackground(new FindCallback<ParseUser>() {
                        public void done(final List<ParseUser> objects, ParseException e) {
                            if (e == null) {
                                // The query was successful.
                                if (objects.size() != 0) {
                                    Log.d(TAG, "following " + objects.size() + " people");
                                    //Log.d(TAG, objects.get(0).getString("name"));
                                    for (final ParseObject aPost : posts) {
                                        //System.out.println(aPost.getString("location"));

                                        aPost.getParseObject("createdBy").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                            public void done(ParseObject aUser, ParseException e) {
                                                if (e == null) {

                                                    if (objects.contains(aUser)) {
                                                        int idcount=0;
                                                        final Activity activity = getActivity();
                                                        final LinearLayout myll = createll(activity);
                                                        //username of the post
                                                        String author = aUser.getString("username");
                                                        Log.d(TAG, "created by " + author);
                                                        TextView t1 = newText( author, activity);
                                                        myll.addView(t1);

                                                        //location of the post
                                                        System.out.println("post is " + aPost.getString("location"));
                                                        TextView t2 = newText("Location: " + aPost.getString("location"), activity);
                                                        myll.addView(t2);


                                                        //post time
                                                        Date date = aPost.getCreatedAt();
                                                        System.out.println("post created at" + date.toString());
                                                        TextView t3 = newText("Created at: " + date.toString(), activity);
                                                        myll.addView(t3);
                                                        //ParseImageView parseImageView = new ParseImageView(activity);

                                                        ImageView image = newiv(activity);
                                                        image.setId(idcount);
                                                        final int idnum=idcount;
                                                        idcount++;
                                                        myll.addView(image);
                                                        // post picture
                                                        ParseFile photo = aPost.getParseFile("photo");
                                                        photo.getDataInBackground(new GetDataCallback() {
                                                            public void done(byte[] data, ParseException e) {
                                                                if (e == null) {
                                                                    // data has the bytes for the resume
                                                                    System.out.println("photo fetching successful");
                                                                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

                                                                    ImageView image = (ImageView) myll.findViewById(idnum);
                                                                    image.setImageBitmap(bmp);
                                                                    /*ImageView image = newiv(activity);
                                                                    image.setImageBitmap(bmp);
                                                                    myll.addView(image);*/

                                                                } else {
                                                                    // something went wrong
                                                                }
                                                            }
                                                        });



                                                        TextView t4=newText("\n", activity);
                                                        t4.setId(idcount);
                                                        final int idnum4=idcount;
                                                        idcount++;
                                                        myll.addView(t4);


                                                        //get comments and authors
                                                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comment");
                                                        query.whereEqualTo("commentedOn", aPost);
                                                        query.findInBackground(new FindCallback<ParseObject>() {
                                                            public void done(List<ParseObject> objects, ParseException e) {
                                                                if (e == null) {
                                                                    if (objects.size() != 0) {
                                                                        // The query was successful.

                                                                        Log.d(TAG, "commented by " + objects.size() + " people");


                                                                        for (int i = 0; i < objects.size(); i++) {
                                                                            Log.d(TAG, objects.get(i).getString("content"));
                                                                            TextView t4=(TextView)myll.findViewById(idnum4);

                                                                            t4.append(objects.get(i).getString("content")+"\n");
                                                                            /*

                                                                            TextView t3 = newText("comments: " + objects.get(i).getString("content"), activity);
                                                                            myll.addView(t3);*/
                                                                            objects.get(i).getParseObject("createdBy")
                                                                                    .fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                                                                                        public void done(ParseObject aUser, ParseException e) {
                                                                                            String author = aUser.getString("name");
                                                                                            Log.d(TAG, "created by " + author);
                                                                                            TextView t4 = (TextView) myll.findViewById(idnum4);
                                                                                            t4.append("Created By " + author+"\n");
                                                                                            /*
                                                                                            TextView t4 = newText("comment createdBy" + author, activity);
                                                                                            myll.addView(t4);*/

                                                                                        }
                                                                                    });
                                                                        }

                                                                    } else {
                                                                        Log.d(TAG, "not commented on by anyone");
                                                                    }

                                                                } else {
                                                                    // Something went wrong.
                                                                    Log.d(TAG, " something went wrong" + e.getMessage());
                                                                }
                                                            }
                                                        });


                                                        TextView t5=newText(" ", activity);
                                                        t5.setId(idcount);
                                                        final int idnum5=idcount;
                                                        idcount++;
                                                        myll.addView(t5);
                                                        // get  likeBy usernames
                                                        ParseRelation relation = aPost.getRelation("likedBy");
                                                        ParseQuery query2 = relation.getQuery();
                                                        query2.findInBackground(new FindCallback<ParseUser>() {
                                                            public void done(List<ParseUser> objects, ParseException e) {
                                                                if (e == null) {
                                                                    if (objects.size() != 0) {
                                                                        // The query was successful.

                                                                        Log.d(TAG, "likedBy " + objects.size() + " people");
                                                                        TextView t5 =(TextView)myll.findViewById(idnum5);
                                                                        t5.setText(objects.size() + " likes: ");
                                                                        /*
                                                                        TextView t5 = newText(objects.size() + " likes", activity);
                                                                        myll.addView(t5);*/

                                                                        for (int i = 0; i < objects.size(); i++) {
                                                                            Log.d(TAG, objects.get(i).getString("username"));
                                                                            t5.append(objects.get(i).getString("username")+" ");

                                                                            /*
                                                                            TextView t6 = newText("likes: " + objects.get(i).getString("username"), activity);
                                                                            myll.addView(t6);*/
                                                                        }
                                                                    } else {
                                                                        Log.d(TAG, "not liked by anyone");
                                                                        TextView t5 =(TextView)myll.findViewById(idnum5);
                                                                        t5.setText(objects.size() + " likes");
                                                                        /*TextView t5 = newText(objects.size() + " likes", activity);
                                                                        myll.addView(t5);*/
                                                                    }

                                                                } else {
                                                                    // Something went wrong.
                                                                    Log.d(TAG, " something went wrong" + e.getMessage());
                                                                }
                                                            }
                                                        });
                                                        final EditText commentEditText = createcommentText(activity);
                                                        Button commentBtn = createCommentBtn(activity);
                                                        commentBtn.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Toast.makeText(activity, "comment", Toast.LENGTH_SHORT).show();
                                                                String userComment = commentEditText.getText().toString();
                                                                ParseObject comment = new ParseObject("Comment");
                                                                comment.put("content", userComment);
                                                                comment.put("commentedOn", aPost);
                                                                comment.put("createdBy", ParseUser.getCurrentUser());
                                                                comment.saveInBackground();
                                                            }
                                                        });

                                                        Button likeBtn = createLikeBtn(activity);
                                                        likeBtn.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Toast.makeText(activity, "like", Toast.LENGTH_SHORT).show();
                                                                ParseRelation<ParseObject> likeRelation = aPost.getRelation("likedBy");
                                                                likeRelation.add(ParseUser.getCurrentUser());
                                                                aPost.saveInBackground();
                                                                Log.d(TAG, "liked a post");
                                                                ParseObject activity = new ParseObject("Activity");
                                                                activity.put("content", "liked");
                                                                activity.put("fromUser", ParseUser.getCurrentUser().getUsername());
                                                                activity.put("toUser", aPost.getParseObject("createdBy").getString("username"));
                                                                activity.saveInBackground();
                                                            }
                                                        });
                                                        myll.addView(commentBtn);
                                                        myll.addView(likeBtn);
                                                        myll.addView(commentEditText);
                                                        tweetsLayout.addView(myll);

                                                    }

                                                }
                                                // Do something with your new title variable
                                            }
                                        });

                                    }
                                } else {
                                    Log.d(TAG, "the user is not following any people");
                                }


                            } else

                            {
                                // Something went wrong.
                                Log.d(TAG, " something went wrong" + e.getMessage());
                            }
                        }
                    });


                } else

                {
                    Log.d("post", "Error: " + e.getMessage());
                }
            }
        });
        return tweetsLayout;
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
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setPadding(0, 0, 0, 0);
        return textLayout;
    }
    public ImageView newiv(Activity activity){
        ImageView image=new ImageView(activity);
        return image;
    }
    public Button createCommentBtn(Activity activity){
        Button commentBtn = new Button(activity);
        commentBtn.setText("comment");
        return commentBtn;
    }
    public Button createLikeBtn(Activity activity){
        Button likeBtn = new Button(activity);
        likeBtn.setText("like");
        return likeBtn;
    }
    public EditText createcommentText(Activity activity){
        EditText commentBtn = new EditText(activity);
        commentBtn.setText("enter comment");
        return commentBtn;
    }

}
