package com.example.youview.network;

import com.example.youview.models.ModelHome;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

public class YoutubeAPI {

    public static final String BASE_URL = "https://www.googleapis.com/youtube/v3/";
    public static final String KEY = "key=AIzaSyBKMUgqfU8OsRIMFzl76335wB65Z-67X7M";
    public static final String sch = "search?";
    public static final String mx = "&maxResults=20";
    public static final String ord = "&order=viewCount";
    public static final String part = "&part=snippet";
    public static final String NPT = "&pageToken=";
    public static final String loc = "&regionCode=HR";
    public static final String pop = "&chart=mostPopular";

    public static final String query = "&q=";
    public static final String allTypes = "&type=video&type=channel&type=playlist";

    public interface Video {
        @GET
        Call<ModelHome> getHomeVideo(@Url String url);
    }

    protected static Video video = null;

    public static Video getVideo(){
        if (video == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            video = retrofit.create(Video.class);
        }
        return video;
    }

}
