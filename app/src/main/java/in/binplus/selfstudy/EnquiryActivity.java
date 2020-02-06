package in.binplus.selfstudy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.binplus.selfstudy.R;

public class EnquiryActivity extends AppCompatActivity {
    Button btn_save ;
    EditText et_email ,et_message ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_enquiry );

    }
}
