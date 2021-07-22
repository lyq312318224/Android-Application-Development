package com.example.bigproject.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessageListResponse {
    @SerializedName("feeds")
    public List<VideoReturnMessage> feeds;
    @SerializedName("success")
    public boolean success;
}
