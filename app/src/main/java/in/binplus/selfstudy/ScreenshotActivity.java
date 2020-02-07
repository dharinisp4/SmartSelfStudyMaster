package in.binplus.selfstudy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.binplus.selfstudy.R;
import com.squareup.picasso.Picasso;

public class ScreenshotActivity extends AppCompatActivity {
    TextView tv_back;
    ImageView img_screenshot;
    String img_name="";
    ProgressDialog loadingBar ;
    public static final String image_url="http://smartselftstudy.com/uploads/docs/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_screenshot);
        tv_back=(TextView)findViewById(R.id.tv_back);
        img_screenshot=(ImageView) findViewById(R.id.img_screenshot);
        loadingBar = new ProgressDialog( ScreenshotActivity.this );
        loadingBar.setCanceledOnTouchOutside( false );
        img_name=getIntent().getStringExtra("screenshot");

        String url=image_url+img_name;
        Picasso.get().load(url).placeholder(R.drawable.logo).into(img_screenshot);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

    }
}
