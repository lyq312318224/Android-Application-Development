package com.example.bigproject.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class VideoReturnMessage {
    @SerializedName("student_id")
    private String studentId;
    @SerializedName("user_name")
    private String username;
    @SerializedName("video_url")
    private String videoUrl;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("image_w")
    private int imageWid;
    @SerializedName("image_h")
    private int imageHei;
    @SerializedName("_id")
    private String videoId;
    @SerializedName("createdAt")
    private Date createdAt;
    @SerializedName("updateAt")
    private Date updatedAt;

    public void setStudentId(String Id){ this.studentId=Id; }
    public String getStudentId(){ return studentId; }

    public void setUsername(String Id){ this.username=Id; }
    public String getUsername(){ return username; }

    public void setVideoUrl(String Url){ this.videoUrl=Url; }
    public String getVideoUrl(){ return videoUrl; }

    public void setImageUrl(String Url){ this.imageUrl=Url; }
    public String getImageUrl(){ return imageUrl; }

    public void setImageWid(int wid){ this.imageWid=wid; }
    public int getImageWid(){ return imageWid; }

    public void setImageHei(int hei){ this.imageHei=hei; }
    public int getImageHei(){ return imageHei; }

    public void setVideoId(String id){ this.videoId=id; }
    public String getVideoId(){ return videoId; }

    public void setCreatedAt(Date createdat) {
        this.createdAt = createdat;
    }
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setUpdatedAtt(Date updatedat) {
        this.updatedAt = updatedat;
    }
    public Date getUpdatedAt() {
        return updatedAt;
    }

}
