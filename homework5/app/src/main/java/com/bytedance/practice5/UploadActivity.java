package com.bytedance.practice5;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.practice5.model.MessageListResponse;
import com.bytedance.practice5.model.Upload;
import com.bytedance.practice5.model.UploadResponse;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;

public class UploadActivity extends AppCompatActivity {
    private static final String TAG = "chapter5";
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    private static final int REQUEST_CODE_COVER_IMAGE = 101;
    private static final String COVER_IMAGE_TYPE = "image/*";
    private IApi api;
    private Uri coverImageUri;
    private SimpleDraweeView coverSD;
    private EditText toEditText;
    private EditText contentEditText ;
    private FeedAdapter adapter = new FeedAdapter();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initNetwork();
        setContentView(R.layout.activity_upload);
        coverSD = findViewById(R.id.sd_cover);
        toEditText = findViewById(R.id.et_to);
        contentEditText = findViewById(R.id.et_content);
        findViewById(R.id.btn_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_COVER_IMAGE, COVER_IMAGE_TYPE, "选择图片");
            }
        });


        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
                //submitMessageWithURLConnection();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_COVER_IMAGE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                coverImageUri = data.getData();
                coverSD.setImageURI(coverImageUri);

                if (coverImageUri != null) {
                    Log.d(TAG, "pick cover image " + coverImageUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }

            } else {
                Log.d(TAG, "file pick fail");
            }
        }
    }

    private void initNetwork() {
        //TODO 3
        // 创建Retrofit实例
        // 生成api对象
         final Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
         api=retrofit.create(IApi.class);
    }

    private void getFile(int requestCode, String type, String title) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    private void submit() {
        byte[] coverImageData = readDataFromUri(coverImageUri);
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(this, "封面不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        String to = toEditText.getText().toString();
        if (TextUtils.isEmpty(to)) {
            Toast.makeText(this, "请输入TA的名字", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = contentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入想要对TA说的话", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( coverImageData.length >= MAX_FILE_SIZE) {
            Toast.makeText(this, "文件过大", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO 5
        // 使用api.submitMessage()方法提交留言
        // 如果提交成功则关闭activity，否则弹出toast
        Log.d("111","success2");
        MultipartBody.Part from = MultipartBody.Part.createFormData("from", Constants.USER_NAME);
        MultipartBody.Part to2 = MultipartBody.Part.createFormData("to", to);
        MultipartBody.Part content2 = MultipartBody.Part.createFormData("content", content);
        MultipartBody.Part image = MultipartBody.Part.createFormData("image", "cover.png", RequestBody.create(MediaType.parse("multipart/form-data"), coverImageData));

        Call<UploadResponse> response = api.submitMessage(Constants.STUDENT_ID, "",
                from, to2, content2, image, Constants.token);

        response.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(final Call<UploadResponse> call, final Response<UploadResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d("111","success4");
                    System.out.print(response);
                    Toast.makeText(UploadActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("111","success3");
                    finish();
                }
            }

            @Override
            public void onFailure(final Call<UploadResponse> call, final Throwable t) {
                t.printStackTrace();
            }
        });

    }






    // TODO 7 选做 用URLConnection的方式实现提交
    private void submitMessageWithURLConnection(){
        byte[] coverImageData = readDataFromUri(coverImageUri);
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(this, "封面不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        String to = toEditText.getText().toString();
        if (TextUtils.isEmpty(to)) {
            Toast.makeText(this, "请输入TA的名字", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = contentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入想要对TA说的话", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( coverImageData.length >= MAX_FILE_SIZE) {
            Toast.makeText(this, "文件过大", Toast.LENGTH_SHORT).show();
            return;
        }


        String urlStr = String.format(Constants.BASE_URL + "messages?student_id=%s&extra_value=%s", Constants.STUDENT_ID,"");
        Log.d(TAG, "getData: " + urlStr);
        try{
            Upload uploadBody = new Upload();
            uploadBody.setFrom(Constants.USER_NAME);
            uploadBody.setTo(to);
            uploadBody.setContent(content);
            MultipartBody.Part imageBody = MultipartBody.Part.createFormData("image", "cover.png", RequestBody.create(MediaType.parse("multipart/form-data"), coverImageData));
            uploadBody.setImage(imageBody);

            byte[] body = new Gson().toJson(uploadBody).getBytes();

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(6000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept", "application/json");
            conn.setRequestProperty("token", Constants.token);

            conn.getOutputStream().write(body);
            conn.getOutputStream().flush();
            Log.d("123","ittrue");
            if (conn.getResponseCode() == 200) {
                Log.d("123","true");
                finish();
            }
            else {
                Log.i("getData","it's empty: " + conn.getResponseCode());
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    private byte[] readDataFromUri(Uri uri) {
        byte[] data = null;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            data = Util.inputStream2bytes(is);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


}
