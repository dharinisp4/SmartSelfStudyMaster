package in.binplus.selfstudy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.binplus.selfstudy.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.binplus.selfstudy.Remote.FileUtil;
import in.binplus.selfstudy.Remote.IUpoladAPI;
import in.binplus.selfstudy.Remote.ProgressRequestBody;
import in.binplus.selfstudy.Remote.RetrofitClient;
import in.binplus.selfstudy.Remote.UploadCallBacks;
import in.binplus.selfstudy.network.apis.PaymentApi;
import in.binplus.selfstudy.utils.ApiResources;
import in.binplus.selfstudy.utils.ToastMsg;
import in.binplus.selfstudy.utils.VolleySingleton;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;


public class SubscriptionActivity extends AppCompatActivity implements View.OnClickListener, UploadCallBacks {
    boolean isDark;
    ProgressDialog dialog,loadingBar;
    TextView tv_acc_no,tv_ifsc,tv_holder_name,tv_bank_name,tv_tot_amt,tv_pending,tx_desc,tx_terms;
    ImageView img_qr,img_screenshot;
    EditText edt_trans_id,edt_amount;
    String qr_image;
    private ApiResources apiResources;
    Button btn_choose,btn_cancel,btn_save;
    //for images upload
    Activity activity=SubscriptionActivity.this;
    String id="";
    String trans_image_name="";
    String email="";
    IUpoladAPI mService;
    public static final int REQUEST_PERMISSION=1000;
    private static final int PICK_FILE_REQUEST=1001;
    Uri selectedFileUri;


    private IUpoladAPI getUPIUpload()
    {
        return RetrofitClient.getClient(Config.API_SERVER_URL).create(IUpoladAPI.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean("dark", false);

        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (!isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Payment Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait");
        dialog.setCancelable(false);

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Please wait");
        loadingBar.setCancelable(false);
        initView();
        //check Permissions
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            },REQUEST_PERMISSION);
        }
        mService=getUPIUpload();
        getSubscriptionData();
        getBankDetails();
    }

    private void initView() {
        apiResources=new ApiResources();
        tv_acc_no=(TextView)findViewById(R.id.tv_acc_no);
        tv_ifsc=(TextView)findViewById(R.id.tv_ifsc);
        tv_holder_name=(TextView)findViewById(R.id.tv_holder_name);
        tv_bank_name=(TextView)findViewById(R.id.tv_bank_name);
        tx_desc=(TextView)findViewById(R.id.tx_desc);
        tx_terms=(TextView)findViewById(R.id.tx_terms);
        tv_pending=(TextView)findViewById(R.id.tv_pending);
        tv_tot_amt=(TextView)findViewById(R.id.tv_tot_amt);
        img_qr=(ImageView) findViewById(R.id.img_qr);
        img_screenshot=(ImageView) findViewById(R.id.img_screenshot);
        edt_trans_id=(EditText)findViewById(R.id.edt_trans_id);
        edt_amount=(EditText)findViewById(R.id.edt_amount);
        btn_choose=(Button) findViewById(R.id.btn_choose);
        btn_cancel=(Button) findViewById(R.id.btn_cancel);
        btn_save=(Button) findViewById(R.id.btn_save);
        SharedPreferences preferences=getSharedPreferences("user",MODE_PRIVATE);
        id = preferences.getString("id","0");
        email = preferences.getString("email","0");

        btn_cancel.setOnClickListener(this);
        btn_choose.setOnClickListener(this);
        btn_save.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id=view.getId();

        if(id == R.id.btn_cancel)
        {
            setCancel(img_screenshot,trans_image_name,btn_choose);
        }
        else if( id == R.id.btn_choose)
        {
            String pan_txt=btn_choose.getText().toString();
            if(pan_txt.equalsIgnoreCase("Choose"))
            {
                chooseFile();
            } else if(pan_txt.equalsIgnoreCase("Uploaded"))
            {
                new ToastMsg(SubscriptionActivity.this).toastIconSuccess(getResources().getString(R.string.already_uploaded));
                // Toast.makeText(SubscriptionActivity.this,getResources().getString(R.string.already_uploaded),Toast.LENGTH_SHORT).show();
            }
            else //if(pan_txt.equalsIgnoreCase("Upload"))
            {
                uploadFile();
            }
        }
        else if(id == R.id.btn_save)
        {
            attemptPaymentDetails();
        }
    }

    private void attemptPaymentDetails() {

        int error_flag=0;
        String trans_id=edt_trans_id.getText().toString();
        String trans_amt=edt_amount.getText().toString();

        if(trans_id.equals("") && trans_image_name.equals(""))
        {
            new ToastMsg(SubscriptionActivity.this).toastIconError(getResources().getString(R.string.required_trans));
        }
        else if(trans_amt.equals("") && trans_amt.isEmpty())
        {
            new ToastMsg(SubscriptionActivity.this).toastIconError(getResources().getString(R.string.required_trans_amt));
        }
        else
        {
            if(!trans_id.equals(""))
            {
                if(trans_id.length()>10)
                {
                    makePayementDetailsRequest(id,trans_id,trans_image_name,trans_amt);
                }
                else
                {
                    edt_trans_id.setError(getResources().getString(R.string.invalid_trans_id));
                    edt_trans_id.requestFocus();

                }
            }
            else
            {
                makePayementDetailsRequest(id,trans_id,trans_image_name,trans_amt);
            }


        }



    }

    private void makePayementDetailsRequest(String id, String trans_id, String trans_image_name,String trans_amt) {

        loadingBar.show();

        Retrofit retrofit = in.binplus.selfstudy.network.RetrofitClient.getRetrofitInstance();

        PaymentApi paymentApi = retrofit.create(PaymentApi.class);
        Call<ResponseBody> call=paymentApi.payment_details(Config.API_KEY,id,trans_id,trans_image_name,trans_amt);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                //User user = response.body();
                loadingBar.dismiss();
                try {

                    JSONObject object=new JSONObject(response.body().string().toString());
                    if(object.getString("status").equals("success"))
                    {
                        new ToastMsg(SubscriptionActivity.this).toastIconSuccess(object.getString("message").toString());
                        Intent intent = new Intent(SubscriptionActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        finish();
                    }
                    else if(object.getString("status").equals("error"))
                    {
                        new ToastMsg(SubscriptionActivity.this).toastIconError(object.getString("message").toString());
                    }
                    // Toast.makeText(SignUpActivity.this,""+object.toString(),Toast.LENGTH_SHORT).show();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    // Toast.makeText(SignUpActivity.this,""+ex.getMessage(),Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(EnquiryActivity.this,""+response.body(),Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loadingBar.dismiss();
                new ToastMsg(SubscriptionActivity.this).toastIconError("Something went wrong."+ t.getMessage());
                t.printStackTrace();

            }
        });
    }


    private void getBankDetails() {
        dialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( Request.Method.GET, apiResources.getBank_details(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dialog.cancel();
                    Log.d("banner_image",response.toString());
                    if (response.getString("status").equals("success")) {
                        JSONArray array=response.getJSONArray("data");
                        JSONObject object=array.getJSONObject(0);
                        tv_acc_no.setText(object.getString("acc_no"));
                        tv_ifsc.setText(object.getString("ifsc"));
                        tv_holder_name.setText(object.getString("name"));
                        qr_image=object.getString("qr_image");
                        tv_bank_name.setText(object.getString("bank_name"));
                        Picasso.get().load(qr_image).placeholder(R.drawable.logo).into(img_qr);

                    } else if (response.getString("status").equals("error")) {
                        new ToastMsg(SubscriptionActivity.this).toastIconError(response.getString("message"));
                    }
                } catch (JSONException e) {
                    dialog.cancel();
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.cancel();
                new ToastMsg(SubscriptionActivity.this).toastIconError(getString(R.string.error_toast));
            }
        });
        new VolleySingleton(SubscriptionActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    private void chooseFile() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_FILE_REQUEST);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == PICK_FILE_REQUEST)
            {
                if(data != null)
                {
                    selectedFileUri=data.getData();
                    if(selectedFileUri !=null && !selectedFileUri.getPath().isEmpty())
                    {
                        img_screenshot.setImageURI(selectedFileUri);
                        btn_choose.setText(getResources().getString(R.string.btn_upload));
                    }
                    else
                    {
                        Toast.makeText(SubscriptionActivity.this,"Please select any image",Toast.LENGTH_SHORT).show();
                    }


                }
                else
                {


                    //   }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case REQUEST_PERMISSION :
            {


                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    new ToastMsg(SubscriptionActivity.this).toastIconSuccess("Permission Granted");
                else
                    new ToastMsg(SubscriptionActivity.this).toastIconError("Permission denied");

            }
        }
    }

    private void uploadFile() {

        if(selectedFileUri !=null)
        {
            dialog=new ProgressDialog(SubscriptionActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMessage("loading...");
            dialog.setIndeterminate(false);
            dialog.setMax(100);
            dialog.setCancelable(false);
            dialog.show();
            String file_nm="";
            File file=null;
            try {
                file= FileUtil.from(SubscriptionActivity.this,selectedFileUri);
                file_nm=id+getRandom()+"_trans"+"."+getFileExtension(file);
                trans_image_name=file_nm;

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressRequestBody requestFile=new ProgressRequestBody(file,this);


            //   Toast.makeText(DocUploadActivity.this,""+file.getName()+"\n "+getFileExtension(file),Toast.LENGTH_SHORT).show();
            final MultipartBody.Part body= MultipartBody.Part.createFormData("uploaded_file",file_nm,requestFile);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    mService.uploadFile(body)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, retrofit2.Response<String> response) {

                                    dialog.dismiss();

                                    new ToastMsg(SubscriptionActivity.this).toastIconSuccess("File Uploaded Successfully..".toString());
                                    btn_choose.setText(getResources().getString(R.string.btn_uploaded));
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    new ToastMsg(SubscriptionActivity.this).toastIconError(t.getMessage().toString());

                                }
                            });
                }
            }).start();
        }

    }

    public String getRandom()
    {
        Date date=new Date();
        SimpleDateFormat smdf=new SimpleDateFormat("ddMMyyhhmmss");
        String dt=smdf.format(date);
        return dt;
    }

    public String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    @Override
    public void onProgressUpdate(int percentage) {
        dialog.setProgress(percentage);
    }

    public void setCancel(ImageView img,String name,Button btn)
    {
        name="";
        img.setImageDrawable(getResources().getDrawable(R.drawable.upload_docs));
        btn.setText(getResources().getString(R.string.btn_choose));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dialog.cancel();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getSubscriptionData() {

        SharedPreferences preferences=activity.getSharedPreferences("user",MODE_PRIVATE);
        id = preferences.getString("id","0");

        String url=apiResources.getSubscription()+"&user_id="+id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("subs-data",response.toString());
                    if (response.getString("status").equals("success")) {
                        JSONArray array=response.getJSONArray("data");
                        JSONObject object=array.getJSONObject(0);
                        double pending_amount=Double.parseDouble(object.getString("pending_pay"));
                        double tot_amount=Double.parseDouble(object.getString("amount"));

                        tv_tot_amt.setText(getResources().getString(R.string.currency)+" "+tot_amount);


                        if(pending_amount<=0)
                        {
                            tx_desc.setTextColor(getResources().getColor(R.color.green_500));
                            tx_desc.setText("Congratulations ! You already subscribed all lectures");
                            tv_pending.setText(getResources().getString(R.string.currency)+" "+"0.0");
                            tx_terms.setVisibility(View.GONE);
                        }
                        else
                        {
                            tx_desc.setText("Please pay "+getResources().getString(R.string.currency)+" "+pending_amount+" to get access all pending lectures.");
                            tx_terms.setTextColor(getResources().getColor(R.color.red_400));
                            tv_pending.setText(getResources().getString(R.string.currency)+" "+pending_amount);
                            tx_terms.setText("*(You can pay in installments, According to paid amount lectures will be assigned to you)");
                        }

                    } else if (response.getString("status").equals("error")) {
                        new ToastMsg(activity).toastIconError(response.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new ToastMsg(activity).toastIconError(getString(R.string.error_toast));
            }
        });
        new VolleySingleton(activity).addToRequestQueue(jsonObjectRequest);
    }


}
