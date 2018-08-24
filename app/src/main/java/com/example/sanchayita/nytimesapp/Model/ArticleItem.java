package com.example.sanchayita.nytimesapp.Model;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ArticleItem implements Parcelable {

      String  webUrl;
      String headline;
      String thumbnail;


     public ArticleItem( String  url,  String title,  String img) {

        webUrl = url;
        headline = title;
        thumbnail = img;

     }

    public  ArticleItem(Parcel in ) {

        webUrl = in.readString();
        headline = in.readString();
        thumbnail = in.readString();

        }

    public static final Parcelable.Creator<ArticleItem> CREATOR
            = new Parcelable.Creator<ArticleItem>() {

        public ArticleItem createFromParcel(Parcel in) {
            return new ArticleItem(in);
        }

        public ArticleItem[] newArray(int size) {
            return new ArticleItem [size];
        }
    };




    public String getWebUrl() {
    return webUrl;
    }

    public void setWebUrl( String url) {
    webUrl = url;
    }

    public String getHeadline() {
    return headline;
    }

    public void setHeadline( String headline) {
    this.headline = headline;
    }

    public String getThumbnail(){
        return thumbnail;
        }

    public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
    return 0;
    }

    @Override
    public void writeToParcel( Parcel dest , int flags) {
        dest.writeString(webUrl);
        dest.writeString(headline);
        dest.writeString(thumbnail);

    }

    public ArticleItem(JSONObject jsonObject) {
        try {
            this.webUrl = jsonObject.getString("web_url");
            this.headline = jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");

            if (multimedia.length() > 0) {
                JSONObject multimediaJson = multimedia.getJSONObject(0);
                this.thumbnail = "http://www.nytimes.com/" + multimediaJson.getString("url");
            } else {
                this.thumbnail = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ArticleItem> fromJSONArray(JSONArray array) {
        ArrayList<ArticleItem> results = new ArrayList<>();

        for (int x = 0; x < array.length(); x++) {
            try {
                results.add(new ArticleItem(array.getJSONObject(x)));
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        return results;
    }

}