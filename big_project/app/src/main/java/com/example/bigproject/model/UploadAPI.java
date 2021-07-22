package com.example.bigproject.model;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface UploadAPI {
    @Multipart
    @POST("video")
    Call<Uploadresponce> submit(@Query("student_id") String studentId,
                                @Query("user_name") String username,
                                @Query("extra_value") String extravalue,
                                @Part MultipartBody.Part cover_image,
                                @Part MultipartBody.Part video,
                                @Header("token") String token
    );
}
