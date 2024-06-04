package com.robomi.robomifront;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RegistInterface {
    @Multipart
    @POST("api/manager/addManager")
    Call<Void> addManager(@Part MultipartBody.Part file, @Part("name")RequestBody name);

    @Multipart
    @POST("api/object/addObject")
    Call<Void> addObject(@Part MultipartBody.Part file, @Part("name")RequestBody name);
}
