package com.parse.starter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

/**
 * Created by xieminjie on 24/09/2015.
 */
public class TweetParseAdapter extends ParseQueryAdapter<ParseObject> {
    public TweetParseAdapter(Context context){
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                Log.d("myActivity", "Adapter running" );
                ParseQuery query = new ParseQuery("TestObject");
                query.whereEqualTo("foo","a");
                return query;
            }
        });

    }
    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.main_custom_row, null);
        }
        super.getItemView(object, v, parent);
        // Add and download the image
        ParseImageView todoImage = (ParseImageView) v.findViewById(R.id.tweet_userPhoto);
        ParseFile imageFile = object.getParseFile("userPhoto");
        if (imageFile != null) {
            todoImage.setParseFile(imageFile);
            todoImage.loadInBackground();
        }
        ParseImageView image2 = (ParseImageView) v.findViewById(R.id.tweet_photo);
        ParseFile imageFile2 = object.getParseFile("Photo");
        if (imageFile2 != null) {
            image2.setParseFile(imageFile2);
            image2.loadInBackground();
        }
        // Add the title view
        TextView nameTextView = (TextView) v.findViewById(R.id.tweet_username);
        nameTextView.setText(object.getString("Name"));

        TextView timeTextView = (TextView) v.findViewById(R.id.tweet_time);
        timeTextView.setText(object.getString("time"));

        TextView likeTextView = (TextView) v.findViewById(R.id.tweet_likenum);
        Log.d("myActivity","likenum "+object.getInt("likeNum"));
        likeTextView.setText(object.getInt("likeNum")+"");

        TextView commmentTextView = (TextView) v.findViewById(R.id.tweet_commentnum);
        commmentTextView.setText(object.getInt("commentNum")+"");
        return v;
    }
}
