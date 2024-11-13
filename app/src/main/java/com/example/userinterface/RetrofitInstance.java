package com.example.userinterface;

import com.squareup.moshi.Moshi;

import org.checkerframework.checker.units.qual.A;

import retrofit2.Retrofit;
import retrofit2.converter.jaxb.JaxbConverterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class RetrofitInstance {
    private static final String BASE_URL = "http://www.aladin.co.kr/ttb/api/";
    private static Retrofit retrofit;

    public static AladinApiSevice getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(JaxbConverterFactory.create())
                    .build();
        }
        return retrofit.create(AladinApiSevice.class);
    }
}
