package in.binplus.selfstudy.network.apis;

import in.binplus.selfstudy.network.model.SearchModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 06,February,2020
 */
public interface PaymentApi {

    @GET("payment_details")
    Call<ResponseBody> payment_details(@Query("api_secret_key") String apiSecreteKey,
                                       @Query("id") String id, @Query("trans_id") String password, @Query("trans_image_name") String trans_image_name, @Query("trans_amt") String trans_amt);
}
