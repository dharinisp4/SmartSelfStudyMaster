package in.binplus.selfstudy;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.location.SettingInjectorService;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.binplus.selfstudy.R;

import java.util.Calendar;

import in.binplus.selfstudy.network.RetrofitClient;
import in.binplus.selfstudy.network.apis.SignUpApi;
import in.binplus.selfstudy.network.model.User;
import in.binplus.selfstudy.utils.ToastMsg;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignUpActivity extends AppCompatActivity {

    private EditText etName,etEmail,etPass,et_mobile,et_dob,et_standard,et_school,et_address,et_pincode,et_city,et_state;
    RadioButton rd_male,rd_female;
    private Button btnSignup;
    private ProgressDialog dialog;
    private View backgorundView;
    private int mYear, mMonth, mDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        boolean isDark = sharedPreferences.getBoolean("dark", false);

        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (!isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SignUp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //---analytics-----------
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "sign_up_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait");
        dialog.setCancelable(false);

        etName=findViewById(R.id.name);
        etEmail=findViewById(R.id.email);
        etPass=findViewById(R.id.password);
        et_mobile=findViewById( R.id.et_mob);
        et_dob=findViewById( R.id.et_dob);
        et_standard=findViewById( R.id.et_standard);
        et_school=findViewById( R.id.et_school);
        et_address=findViewById( R.id.et_address);
        et_pincode=findViewById( R.id.et_pincode);
        et_city=findViewById( R.id.et_city);
        et_state=findViewById( R.id.et_state);
        rd_male=(RadioButton)findViewById( R.id.rd_male ) ;
        rd_female=(RadioButton)findViewById( R.id.rd_female ) ;
        btnSignup=findViewById(R.id.signup);
        backgorundView=findViewById(R.id.background_view);
        if (isDark) {
            backgorundView.setBackgroundColor(getResources().getColor(R.color.nav_head_bg));
            btnSignup.setBackground(getResources().getDrawable(R.drawable.btn_rounded_dark));
        }

        et_dob.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                             et_dob.setText(getFormatedDateOrMOnth( dayOfMonth )+"-"+getFormatedDateOrMOnth(monthOfYear+1)+"-"+year  );

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();


            }
        } );

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String gen="";
                if(rd_male.isChecked())
                {
                    gen="M";
                }
                else if(rd_female.isChecked())
                {
                    gen="F";
                }

                if (!isValidEmailAddress(etEmail.getText().toString())){
                    new ToastMsg(SignUpActivity.this).toastIconError("please enter valid email");
                }else if(etPass.getText().toString().equals("")){
                    new ToastMsg(SignUpActivity.this).toastIconError("please enter password");
                }else if (etName.getText().toString().equals("")){
                    new ToastMsg(SignUpActivity.this).toastIconError("please enter name");
                }else if (et_mobile.getText().toString().equals("")){
                    new ToastMsg(SignUpActivity.this).toastIconError("please enter mobile number");
                }else if (et_standard.getText().toString().equals("")){
                    new ToastMsg(SignUpActivity.this).toastIconError("please enter standard");
                }else if (et_school.getText().toString().equals("")){
                    new ToastMsg(SignUpActivity.this).toastIconError("please enter school/college name");
                }else if (et_address.getText().toString().equals("")){
                    new ToastMsg(SignUpActivity.this).toastIconError("please enter address");
                }else if (et_pincode.getText().toString().equals("")){
                    new ToastMsg(SignUpActivity.this).toastIconError("please enter pincode");
                }else if (et_city.getText().toString().equals("")){
                    new ToastMsg(SignUpActivity.this).toastIconError("please enter city");
                }else if (et_state.getText().toString().equals("")){
                    new ToastMsg(SignUpActivity.this).toastIconError("please enter state");
                }else if (et_mobile.getText().toString().length()!=10){
                    new ToastMsg(SignUpActivity.this).toastIconError("invalid mobile number");
                }else if (et_pincode.getText().toString().length()!=6){
                    new ToastMsg(SignUpActivity.this).toastIconError("invalid pin code");
                }else if (gen.toString().equals( "" )){
                    new ToastMsg(SignUpActivity.this).toastIconError("please select gender");
                }else if (et_dob.getText().toString().equals( "" )){
                    new ToastMsg(SignUpActivity.this).toastIconError("please select date of birth");
                }else {
                    String email = etEmail.getText().toString();
                    String pass = etPass.getText().toString();
                    String name = etName.getText().toString();
                    String mobile=et_mobile.getText().toString();
                    String dob=et_dob.getText().toString();
                    String standard=et_standard.getText().toString();
                    String school=et_school.getText().toString();
                    String address=et_address.getText().toString();
                    String pincode=et_pincode.getText().toString();
                    String city=et_city.getText().toString();
                    String state=et_state.getText().toString();

                    //new ToastMsg( SignUpActivity.this ).toastIconError( et_mobile.getText().toString() );
                    signUp(email, pass, name,mobile,dob,gen,standard,school,address,pincode,city,state);
                }
            }
        });
    }

    private void signUp(String email, String pass, String name,String mobile,String dob,String gen,String standard,String school,String address,String pincode,String city,String state){
        dialog.show();

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();

        SignUpApi signUpApi = retrofit.create(SignUpApi.class);

        Call<User> call = signUpApi.signUp(Config.API_KEY, email, pass, name, mobile, dob, gen,standard, school, address,pincode,city,state);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();

                if (user.getStatus().equals("success")) {
                    new ToastMsg(SignUpActivity.this).toastIconSuccess("Successfully registered");

                    // save user info to sharedPref
//                    saveUserInfo(user.getName(), etEmail.getText().toString(),
//                            user.getUserId());

                    Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    startActivity(intent);
                    finish();
                } else if (user.getStatus().equals("error")){
                    new ToastMsg(SignUpActivity.this).toastIconError(user.getData());
                }
                dialog.cancel();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                new ToastMsg(SignUpActivity.this).toastIconError("Something went wrong."+ t.getMessage());
                t.printStackTrace();
                dialog.cancel();

            }
        });
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

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public void saveUserInfo(String name, String email, String id) {
        SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("id", id);
        editor.putBoolean("status",true);
        editor.apply();
    }

    public String getFormatedDateOrMOnth(int dt)
    {
        String r="";
        String d=String.valueOf( dt );
        if(d.length() <2)
        {
            r="0"+d;
        }
        else
        {
            r=d;
        }
        return r;
    }


}
