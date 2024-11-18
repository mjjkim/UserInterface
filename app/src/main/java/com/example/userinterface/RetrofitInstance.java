package com.example.userinterface;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.moshi.Moshi;

import org.checkerframework.checker.units.qual.A;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RetrofitInstance {
    // 알라딘 API 요청 URL - open api 메뉴얼 참고
    private static final String BASE_URL = "https://www.aladin.co.kr/ttb/api/";
    private static Retrofit retrofit = null;

    static Gson gson = new GsonBuilder().setLenient().create(); //json 직렬/역직렬화.

    private RetrofitInstance() {}
    public static Retrofit getClient(){
        if(retrofit==null) {

            // 필수 아님
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            // 필수 아님
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging); // 로깅 인터셉터 추가


            retrofit = new Retrofit.Builder() // 레트로핏 빌더
                    .baseUrl(BASE_URL) //
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
