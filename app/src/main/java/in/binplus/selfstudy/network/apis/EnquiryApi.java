package in.binplus.selfstudy.network.apis;

import in.binplus.selfstudy.network.model.Enquiry;
import in.binplus.selfstudy.network.model.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EnquiryApi
{
    @GET("enquiry")
    Call<Enquiry> enquiry(@Query("api_secret_key") String key,
                          @Query("email") String email, @Query("message") String message);
}
