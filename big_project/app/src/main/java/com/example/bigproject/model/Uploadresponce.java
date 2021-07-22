package com.example.bigproject.model;

import com.google.gson.annotations.SerializedName;

public class Uploadresponce {
    @SerializedName("result")
    public VideoReturnMessage message;
    @SerializedName("url")
    public String Url;
    @SerializedName("success")
    public boolean success;

}
