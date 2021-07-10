package com.pxirou.calculator.networking;

import com.pxirou.calculator.pojo.RetrieveCurrency;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface GetDataService {
    @GET
    Call<RetrieveCurrency> applyConversion(@Url String url);
}
