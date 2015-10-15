package com.parse.starter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.support.v4.app.ActivityCompat.startActivity;
import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static android.support.v4.content.ContextCompat.startActivities;

/**
 * Created by xieminjie on 16/09/2015.
 */
public class TweetsAdapter extends ArrayAdapter<Tweet>{
    Context context;

    public TweetsAdapter(Context context, ArrayList<Tweet> arrayList) {
        super(context, 0, arrayList);
        this.context = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Tweet tweet = getItem(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.main_custom_row,parent,false);
        ParseImageView userPhotoImageView = (ParseImageView)convertView.findViewById(R.id.tweet_userPhoto);
        TextView usernameTextField = (TextView)convertView.findViewById(R.id.tweet_username);
        TextView timeTextField = (TextView)convertView.findViewById(R.id.tweet_time);
        ParseImageView photoImageView = (ParseImageView)convertView.findViewById(R.id.tweet_photo);
        TextView likenumTextField = (TextView)convertView.findViewById(R.id.tweet_likenum);
        TextView commentnumTextField = (TextView)convertView.findViewById(R.id.tweet_commentnum);

        Button likeBtn = (Button)convertView.findViewById(R.id.tweet_like_btn);
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("test");
            }
        });

        Button commentBtn = (Button)convertView.findViewById(R.id.tweet_commment_btn);
        commentBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                System.out.println("comment");
            /*    System.out.println(tweet.getUsername());
                Intent intent = new Intent();
                intent.setClass(context, SearchUser.class);
                Bundle b = new Bundle();
                intent.putExtra("name","sfa");
                startActivity(intent);*/
            }
        });
        usernameTextField.setText(tweet.getUsername());
        timeTextField.setText(tweet.getTweetTime());
        likenumTextField.setText(tweet.getLikeNum());
        commentnumTextField.setText(tweet.getCommentNum());
        ParseFile userPhoto = tweet.getUserPhoto();
        if (userPhoto != null) {
            userPhotoImageView.setParseFile(userPhoto);
            userPhotoImageView.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    System.out.println("photo done");
                }
            });
        }else{
            userPhotoImageView.setImageResource(R.drawable.ic_action_dock);
        }

        ParseFile photoFile = tweet.getPhoto();
        if (photoFile != null) {
            photoImageView.setParseFile(photoFile);
            photoImageView.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    System.out.println("photo done");
                }
            });
        }else{
            photoImageView.setImageResource(R.drawable.ic_action_dock);
        }

        return convertView;
    }
}
