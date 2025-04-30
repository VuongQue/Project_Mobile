package com.example.project_mobile.dto;

import com.google.gson.annotations.SerializedName;

public class VietQRResponse {
    @SerializedName("code")
    private String code;

    @SerializedName("desc")
    private String desc;

    @SerializedName("data")
    private Data data;

    public String getCode() {
        return code;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("qrDataURL")
        private String qrDataURL;

        public String getQrDataURL() {
            return qrDataURL;
        }
    }
}
