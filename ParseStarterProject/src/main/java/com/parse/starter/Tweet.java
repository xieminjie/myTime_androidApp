package com.parse.starter;

import com.parse.ParseFile;
import com.parse.ParseUser;

/**
 * Created by xieminjie on 16/09/2015.
 */
public class Tweet {
    private ParseFile userPhoto;
    private String username;
    private String tweetTime;
    private ParseFile photo;
    private String likeNum;
    private String commentNum;

    public Tweet(){

    }

    public Tweet(ParseFile userPhoto, String username, String tweetTime, ParseFile photo, String likeNum, String commentNum) {
        this.userPhoto = userPhoto;
        this.username = username;
        this.tweetTime = tweetTime;
        this.photo = photo;
        this.likeNum = likeNum;
        this.commentNum = commentNum;
    }

    public ParseFile getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(ParseFile userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTweetTime() {
        return tweetTime;
    }

    public void setTweetTime(String tweetTime) {
        this.tweetTime = tweetTime;
    }

    public ParseFile getPhoto() {
        return photo;
    }

    public void setPhoto(ParseFile photo) {
        this.photo = photo;
    }

    public String getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(String likeNum) {
        this.likeNum = likeNum;
    }

    public String getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
    }
}
