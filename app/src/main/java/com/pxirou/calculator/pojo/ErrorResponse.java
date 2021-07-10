package com.pxirou.calculator.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ErrorResponse implements Serializable {
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("error-type")
    @Expose
    private String errorType;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

}
