package in.binplus.selfstudy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.binplus.selfstudy.R;

import in.binplus.selfstudy.network.RetrofitClient;
import in.binplus.selfstudy.network.apis.EnquiryApi;

import in.binplus.selfstudy.network.model.Enquiry;

import in.binplus.selfstudy.utils.ApiResources;
import in.binplus.selfstudy.utils.ToastMsg;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EnquiryActivity extends AppCompatActivity implements View.OnClickListener {
   private Button btn_save ;
   private EditText et_email ,et_message ;
    private ProgressDialog dialog;
    private View backgroundView;
    String id , email ;
    ApiResources apiResources ;
    boolean isDark ;
    ImageView back ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
         isDark = sharedPreferences.getBoolean("dark", false);

        if (isDark) {
            setTheme(R.style.AppThemeDark);

        } else {
            setTheme(R.style.AppThemeLight);
        }


        setContentView( R.layout.activity_enquiry );

        Toolbar toolbar = findViewById(R.id.toolbar);

        if (!isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Contact Us");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait");
        dialog.setCancelable(false);

//        loadingBar = new ProgressDialog(this);
//        loadingBar.setMessage("Please wait");
//        loadingBar.setCancelable(false);

        initView();


    }
    private void initView() {
        apiResources=new ApiResources();
       et_email = (EditText)findViewById( R.id.et_email );
       et_message =(EditText)findViewById( R.id.et_message );
        btn_save=(Button) findViewById(R.id.btnSave);
        back = findViewById( R.id.img_back );
        SharedPreferences preferences=getSharedPreferences("user",MODE_PRIVATE);
        id = preferences.getString("id","0");
        email = preferences.getString("email","0");
        btn_save.setOnClickListener(this);
        et_email.setText(email);
        et_email.setEnabled( false );

        if (isDark)
        {
            et_message.setBackgroundColor( getResources().getColor( R.color.dark ) );
        }
        else
        {

        }

    }

    @Override
    public void onClick(View view) {
        int id  = view.getId();
        if (id== R.id.btnSave)
        {
            String getemail = et_email.getText().toString();
            String getmsg = et_message.getText().toString();

            if (getmsg.isEmpty())
            {
                et_message.setError( "Enter Message" );
                et_message.requestFocus();
            }
            else
            {
              sendEnquiry( getemail,getmsg );
            }
        }



    }

    private void sendEnquiry(String email, String msg) {
        dialog.show();

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();

       EnquiryApi enquiryApi = retrofit.create( EnquiryApi.class );

        Call<Enquiry> call = enquiryApi.enquiry( Config.API_KEY, email,msg );
        call.enqueue( new Callback<Enquiry>() {
            @Override
            public void onResponse(Call<Enquiry> call, Response<Enquiry> response) {
               Enquiry enq = response.body();

                if (enq.getStatus().equals( "success" )) {
                    new ToastMsg( EnquiryActivity.this ).toastIconSuccess( enq.getMessage() );

                    // save user info to sharedPref
//                    saveUserInfo(user.getName(), etEmail.getText().toString(),
//                            user.getUserId());

                    Intent intent = new Intent( EnquiryActivity.this, MainActivity.class );
                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
                    intent.addFlags( Intent.FLAG_ACTIVITY_NO_ANIMATION );

                    startActivity( intent );
                    finish();
                } else if (enq.getStatus().equals( "error" )) {
                    new ToastMsg( EnquiryActivity.this ).toastIconError( enq.getMessage() );
                }
                dialog.cancel();
            }

            @Override
            public void onFailure(Call<Enquiry> call, Throwable t) {
                new ToastMsg( EnquiryActivity.this ).toastIconError( "Something went wrong." + t.getMessage() );
                t.printStackTrace();
                dialog.cancel();
            }

        } );
    }
}
