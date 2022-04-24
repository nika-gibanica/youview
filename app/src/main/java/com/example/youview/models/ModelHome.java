package com.example.youview.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelHome {
    @SerializedName("nextPageToken")
    @Expose
    protected String nextPageToken;

    @SerializedName("items")
    @Expose
    protected List<VideoYT> items;

    public ModelHome() {
    }

    public ModelHome(String nextPageToken, List<VideoYT> items) {
        this.nextPageToken = nextPageToken;
        this.items = items;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<VideoYT> getItems() {
        return items;
    }

    public void setItems(List<VideoYT> items) {
        this.items = items;
    }
}