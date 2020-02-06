package in.binplus.selfstudy.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 20,January,2020
 */
public class RetrofitClient {

    private static Retrofit retrofitClient=null;

    public static Retrofit getClient(String baseUrl)
    {
        if(retrofitClient ==null)
        {
            retrofitClient=new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofitClient;
    }
}
