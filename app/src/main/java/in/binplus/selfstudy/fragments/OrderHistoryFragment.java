package in.binplus.selfstudy.fragments;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.binplus.selfstudy.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.binplus.selfstudy.adapters.TransactionAdapter;
import in.binplus.selfstudy.models.TransactionModel;
import in.binplus.selfstudy.utils.ApiResources;
import in.binplus.selfstudy.utils.ToastMsg;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderHistoryFragment extends Fragment {

    LinearLayout lin_no_data;
    RecyclerView rec_trans;
    TransactionAdapter transactionAdapter;
    ProgressDialog dialog;
    String id=null;
    List<TransactionModel> list;
    String url="";
    boolean isDark;
    private ApiResources apiResources;
    public OrderHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate( R.layout.fragment_order_history, container, false);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please wait");
        dialog.setCancelable(false);
        apiResources=new ApiResources();
        list=new ArrayList<>();
        rec_trans=(RecyclerView)view.findViewById(R.id.rec_trans);
        lin_no_data=(LinearLayout) view.findViewById(R.id.lin_no_data);
        SharedPreferences preferences=getActivity().getSharedPreferences("user",MODE_PRIVATE);
        id = preferences.getString("id","0");

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("push", MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean("dark", false);
//        isDark=false;


//Toast.makeText( getActivity(),"Dark" +isDark,Toast.LENGTH_LONG ).show();
        getData(apiResources.getUser_payment_details(),id);


        return view;
    }

    private void getData(String url, String id) {

        dialog.show();
        String fullUrl = url+"&user_id="+id;
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, fullUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    Log.e("trans-data",response.toString());
                    String status=response.getString("status");
                    if(status.equals("success"))
                    {
                        JSONArray array=response.getJSONArray("data");
                        for(int i=0; i<array.length();i++)
                        {
                            TransactionModel model=new TransactionModel();
                            JSONObject object=array.getJSONObject(i);
                            model.setId(object.getString("id"));
                            model.setTrans_id(object.getString("trans_id"));
                            model.setTrans_image(object.getString("trans_image"));
                            model.setStatus(object.getString("status"));
                            model.setTrans_date(object.getString("trans_date"));
                            model.setTrans_amount(object.getString("trans_amount"));
                            list.add(model);
                        }
                        rec_trans.setVisibility(View.VISIBLE);
                        transactionAdapter=new TransactionAdapter(getActivity(),list,isDark);
                        rec_trans.setLayoutManager(new LinearLayoutManager(getActivity()));
                        rec_trans.setAdapter(transactionAdapter);
                        transactionAdapter.notifyDataSetChanged();

                    }
                    else if(status.equals("failed"))
                    {
                        lin_no_data.setVisibility(View.VISIBLE);
                        new ToastMsg(getActivity()).toastIconError(response.getString("message").toString());
                    }
                    else if(status.equals("error"))
                    {
                        new ToastMsg(getActivity()).toastIconError(response.getString("message").toString());
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    new ToastMsg(getActivity()).toastIconError(ex.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                new ToastMsg(getActivity()).toastIconError(error.getMessage());

            }
        });
        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }


}
