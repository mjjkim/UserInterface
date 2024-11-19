package com.example.userinterface;

import retrofit2.Retrofit;
import retrofit2.converter.jaxb.JaxbConverterFactory;

public class AladinRetrofitInstance {
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
