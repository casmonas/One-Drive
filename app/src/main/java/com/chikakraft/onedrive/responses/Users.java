package com.chikakraft.onedrive.responses;

import com.google.gson.annotations.SerializedName;

public class Users {



    @SerializedName("response")
    private String Response;

    public String getResponse() {
        return Response;
    }
}
