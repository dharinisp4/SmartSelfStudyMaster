package in.binplus.selfstudy.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.binplus.selfstudy.R;
import com.glide.slider.library.SliderLayout;
import com.google.android.gms.common.internal.constants.ListAppsActivityContract;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.binplus.selfstudy.SubscriptionActivity;
import in.binplus.selfstudy.models.PagesModel;
import in.binplus.selfstudy.utils.ApiResources;
import in.binplus.selfstudy.utils.ToastMsg;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUsFragment extends Fragment {
    ApiResources apiResources ;
    TextView txt_content ;
    ArrayList<PagesModel> page_list ;
    ProgressDialog loadingBar ;

    public AboutUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated( view, savedInstanceState );
        getActivity().setTitle(getResources().getString(R.string.title_about));
        loadingBar = new ProgressDialog(getContext());
        loadingBar.setMessage("Please wait");
        loadingBar.setCancelable(false);
        initComponent(view);
        getData();
    }

    private void initComponent(View view) {

        apiResources=new ApiResources();
        txt_content = view.findViewById( R.id.txt_content );


    }
    public  void getData()
    {
        loadingBar.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( Request.Method.GET, apiResources.getPages(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString( "status" ).equals( "success" ))
                            {
                                loadingBar.dismiss();
                                JSONArray array = response.getJSONArray( "pages" );
                                for (int i=0 ; i<array.length() ; i++)
                                {
                                   JSONObject jsonObject=array.getJSONObject(i);


                                    if (jsonObject.getString( "title" ).equalsIgnoreCase( "About Us" ))
                                    {
                                        txt_content.setText( jsonObject.getString( "content" ) );

//                                        Toast.makeText( getActivity(),"About Us" ,Toast.LENGTH_LONG ).show();
                                    }


                                }
                            }
                            else
                            {
//                                new ToastMsg(getActivity()).toastIconError(response.getString( "" ));
                            }
                        }
                        catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingBar.dismiss();
                        new ToastMsg( getActivity()).toastIconError(error.getMessage());

                    }
                } );
        Volley.newRequestQueue( getContext()).add( jsonObjectRequest );
    }
}
