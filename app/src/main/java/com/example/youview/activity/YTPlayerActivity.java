package com.example.youview.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.example.youview.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayerView;

import com.example.youview.network.YoutubeAPI;
import com.example.youview.utils.UploadEvent;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

public class YTPlayerActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {

    protected YouTubePlayerView ytPlayer;
    protected YouTubePlayer player;
    protected String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ytplayer);

        Intent data = getIntent();
        videoId = data.getStringExtra("video_id");

        ytPlayer = findViewById(R.id.yt_player);

        ytPlayer.initialize(YoutubeAPI.KEY, this);
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {

        player = youTubePlayer;

        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);
        youTubePlayer.setOnFullscreenListener(onFullScreenListener);

        if(!wasRestored) {
            youTubePlayer.loadVideo(videoId);
        }

    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {
            if (MainActivity.getLastEvent() == "pause") {
                uploadEvent("play");
            }
            MainActivity.setLastEvent("play");
        }

        @Override
        public void onPaused() {
            if (MainActivity.getLastEvent() != "pause" && MainActivity.getLastUploaded() != "pause") {
                uploadEvent("pause");
            }
            MainActivity.setLastEvent("pause");
        }

        @Override
        public void onStopped() {

        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {
            if (MainActivity.getLastEvent() != "video started") {
                uploadEvent("seek");
            }
            MainActivity.setLastEvent("seek");
        }
    };

    YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {
            player.play();
        }

        @Override
        public void onAdStarted() {
            uploadEvent("ad");
            MainActivity.setLastEvent("add");
        }

        @Override
        public void onVideoStarted() {

            if (MainActivity.getLastUploaded() != "enter fullscreen" && MainActivity.getLastUploaded() != "exit fullscreen") {
                int videoNumber = MainActivity.getVideoNumber();
                videoNumber++;
                MainActivity.setVideoNumber(videoNumber);

                SharedPreferences sharedPreferences = getSharedPreferences("com.example.youview", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putInt("videoNumber", videoNumber);
                myEdit.apply();

                uploadEvent("video started");
            }
            MainActivity.setLastEvent("video started");
        }

        @Override
        public void onVideoEnded() {
            uploadEvent("video ended");
            MainActivity.setLastEvent("video ended");

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {

        }
    };

    YouTubePlayer.OnFullscreenListener onFullScreenListener = new YouTubePlayer.OnFullscreenListener() {
        @Override
        public void onFullscreen(boolean b) {
            if (b == true && MainActivity.getLastUploaded() != "enter fullscreen") {
                uploadEvent("enter fullscreen");
                MainActivity.setLastEvent("enter fullscreen");
            }

            if (b == false) {
                uploadEvent("exit fullscreen");
                MainActivity.setLastEvent("exit fullscreen");
            }
        }

    };

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Failed to Initialize Youtube Player", Toast.LENGTH_LONG).show();
    }

    private void uploadEvent(String event){
        MainActivity.setLastUploaded(event);
        UploadEvent upload = new UploadEvent();
        int video = MainActivity.getVideoNumber();
        String connection = getConnection(getApplicationContext());
        String id = MainActivity.getAndroidId();

        upload.doInBackground(id, event, connection, String.valueOf(video));
    }

    private String getConnection(Context context){
        NetworkInfo info = getNetworkInfo(context);
        if(info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return "WIFI";
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                int subType = info.getSubtype();
                String type = "MOBILE ";
                switch (subType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:     // api< 8: replace by 11
                    case TelephonyManager.NETWORK_TYPE_GSM:      // api<25: replace by 16
                        type += "2G";
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:   // api< 9: replace by 12
                    case TelephonyManager.NETWORK_TYPE_EHRPD:    // api<11: replace by 14
                    case TelephonyManager.NETWORK_TYPE_HSPAP:    // api<13: replace by 15
                    case TelephonyManager.NETWORK_TYPE_TD_SCDMA: // api<25: replace by 17
                        type += "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:      // api<11: replace by 13
                    case TelephonyManager.NETWORK_TYPE_IWLAN:    // api<25: replace by 18
                    case 19: // LTE_CA
                        type += "4G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_NR:       // api<29: replace by 20
                        type += "5G";
                        break;
                }
                return type;
            }else return "UNKNOWN";
        }else return "NOT CONNECTED";
    }

    private static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }
}
