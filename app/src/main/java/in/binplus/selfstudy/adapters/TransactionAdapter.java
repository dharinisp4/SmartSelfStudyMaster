package in.binplus.selfstudy.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.binplus.selfstudy.R;

import java.util.List;


import in.binplus.selfstudy.ScreenshotActivity;
import in.binplus.selfstudy.models.TransactionModel;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 29,January,2020
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    Activity activity;
    List<TransactionModel> list;
    boolean isDark;
    View view ;

    public TransactionAdapter(Activity activity, List<TransactionModel> list, boolean isDark) {
        this.activity = activity;
        this.list = list;
        this.isDark = isDark;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isDark)
        {
             view = LayoutInflater.from( activity ).inflate( R.layout.row_transaction_dark, null );
        }
        else
            {
            view = LayoutInflater.from( activity ).inflate( R.layout.row_order_history_layout, null );
        }
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final TransactionModel model=list.get(position);

        if(model.getTrans_id().equals(""))
        {
            holder.tv_trans_id.setText("Transaction Id : Not Available ");

        }
        else
        {
            holder.tv_trans_id.setText("Transaction Id : #"+model.getTrans_id());

        }
        String t_date=model.getTrans_date();
        String[] dt_arr=t_date.split(" ");
        holder.tv_date.setText("Date : "+dt_arr[0].toString());
        holder.tv_time.setText("Time : "+dt_arr[1].toString());

        String status=model.getStatus().toString();

        if(status.equals("0"))
        {
            holder.rel_pending.setVisibility(View.VISIBLE);
        }
        else
        {
           holder.rel_confirm.setVisibility(View.VISIBLE);
                    }
        if(model.getTrans_image().equals(""))
        {
            if(holder.rel_screenshot.getVisibility() == View.VISIBLE)
            {
                holder.rel_screenshot.setVisibility(View.GONE);
            }

        }
        else
        {
            holder.rel_screenshot.setVisibility(View.VISIBLE);
        }
   holder.tv_amount.setText(activity.getResources().getString(R.string.currency)+" "+model.getTrans_amount());

        holder.rel_screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(activity, ScreenshotActivity.class);
                intent.putExtra("screenshot",model.getTrans_image());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_trans_id,tv_date,tv_time,tv_amount;
        RelativeLayout rel_screenshot,rel_pending,rel_confirm;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_trans_id=(TextView)itemView.findViewById(R.id.tv_trans_id);
            tv_date=(TextView)itemView.findViewById(R.id.tv_date);
            tv_time=(TextView)itemView.findViewById(R.id.tv_time);
            tv_amount=(TextView)itemView.findViewById(R.id.tv_amount);

            rel_screenshot=(RelativeLayout)itemView.findViewById(R.id.rel_screenshot);
            rel_pending=(RelativeLayout)itemView.findViewById(R.id.rel_pending);
            rel_confirm=(RelativeLayout)itemView.findViewById(R.id.rel_confirm);

        }
    }
}
