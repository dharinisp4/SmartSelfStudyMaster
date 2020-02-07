package in.binplus.selfstudy.network.apis;

import in.binplus.selfstudy.network.model.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SignUpApi {

    @GET("signup")
    Call<User> signUp(@Query("api_secret_key") String key,
                      @Query("email") String email, @Query("password") String password, @Query("name") String name,
                      @Query("mobile") String mobile, @Query("dob") String dob,@Query("gen") String gen, @Query("standard") String standard,@Query("school") String school,
                      @Query("address") String address, @Query("pincode") String pincode, @Query("city") String city,@Query("state") String state);

}
