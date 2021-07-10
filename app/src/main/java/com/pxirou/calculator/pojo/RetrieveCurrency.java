package com.pxirou.calculator.pojo;

import java.io.Serializable;

//import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RetrieveCurrency implements Serializable {

    @SerializedName("result")
    @Expose
    public String result;
    @SerializedName("documentation")
    @Expose
    public String documentation;
    @SerializedName("terms_of_use")
    @Expose
    public String termsOfUse;
    @SerializedName("time_last_update_unix")
    @Expose
    public Integer timeLastUpdateUnix;
    @SerializedName("time_last_update_utc")
    @Expose
    public String timeLastUpdateUtc;
    @SerializedName("time_next_update_unix")
    @Expose
    public Integer timeNextUpdateUnix;
    @SerializedName("time_next_update_utc")
    @Expose
    public String timeNextUpdateUtc;
    @SerializedName("base_code")
    @Expose
    public String baseCode;
    @SerializedName("target_code")
    @Expose
    public String targetCode;
    @SerializedName("conversion_rate")
    @Expose
    public Float conversionRate;
    @SerializedName("conversion_result")
    @Expose
    public Double conversionResult;
}
