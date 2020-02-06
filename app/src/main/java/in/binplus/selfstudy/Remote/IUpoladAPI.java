package in.binplus.selfstudy.Remote;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 20,January,2020
 */
public interface IUpoladAPI {
    @Multipart
    @POST("index.php/api/upload_image")
    Call<String> uploadFile(@Part MultipartBody.Part file);
}
