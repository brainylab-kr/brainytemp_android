package kr.brainylab.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import at.robhor.wifisignalstrength.WifiSignalStrengthView;
import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
import kr.brainylab.common.Common;
import kr.brainylab.model.SensorListInfo;
import kr.brainylab.view.activity.DetailActivity;
import kr.brainylab.view.activity.MainActivity;

public class SenserListAdapter extends ArrayAdapter<SensorListInfo> {
    private LayoutInflater mInflater;
    private Context mContext;
    private Fragment mFrag;

    public SenserListAdapter(Context context, Fragment frag, ArrayList<SensorListInfo> values) {
        super(context, R.layout.item_sensor, values);
        mContext = context;
        mFrag = frag;
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final Holder holder;

        if (convertView == null) {
            // Inflate the view since it does not exist
            convertView = mInflater.inflate(R.layout.item_sensor, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            // This must be done for performance reasons
            holder = new Holder();
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvDevice = convertView.findViewById(R.id.tv_device);
            holder.tvTemp = convertView.findViewById(R.id.tv_temp);
            holder.tvDate = convertView.findViewById(R.id.tv_date);
            holder.rlyBackground = convertView.findViewById(R.id.rly_background);
            holder.ivSignal = convertView.findViewById(R.id.iv_signal);
            holder.ivTemperature = convertView.findViewById(R.id.iv_temperature);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final SensorListInfo info = getItem(position);

        String name = BrainyTempApp.getSensorName(info.getDevice());
        holder.tvName.setText(name);
        holder.tvDevice.setText(info.getDevice());
        holder.tvDate.setText(info.getDate());
        holder.tvTemp.setText(String.valueOf(info.getTemp()) + "°C");

        double maxTemp = BrainyTempApp.getMaxTemp(info.getDevice());
        double minTemp = BrainyTempApp.getMinTemp(info.getDevice());
        if (info.getTemp() < minTemp || info.getTemp() > maxTemp) {
            holder.ivTemperature.setBackground(mContext.getDrawable(R.drawable.ic_temperature1));
            holder.tvTemp.setTextColor(mContext.getResources().getColor(R.color.color_c2185b));
        } else {
            holder.ivTemperature.setBackground(mContext.getDrawable(R.drawable.ic_temperature));
            holder.tvTemp.setTextColor(mContext.getResources().getColor(R.color.color_171717));
        }

        int nRssi =  info.getRssi();
        float fRssi = 0;
        if (nRssi > -35) {
            fRssi = 1;
        } else if (nRssi <= -35 && nRssi > -45) {
            fRssi = (float)0.9;
        } else if (nRssi <= -45 && nRssi > -55) {
            fRssi = (float)0.8;
        } else if (nRssi <= -55 && nRssi > -65) {
            fRssi = (float)0.7;
        } else if (nRssi <= -65 && nRssi > -75) {
            fRssi = (float)0.6;
        }else if (nRssi <= -75 && nRssi > -85) {
            fRssi = (float)0.5;
        } else if (nRssi < -85) {
            fRssi = (float)0;
        }

        holder.ivSignal.setLevel(fRssi);

        holder.rlyBackground.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Common.gSelDevice = info.getDevice();
                MainActivity mainActivity = (MainActivity) mContext;
                mainActivity.bEdit = true;
                mainActivity.changeTitle();
                return true;
            }
        });

        holder.rlyBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("device", info.getDevice());
                intent.putExtra("temp", info.getTemp());
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    /**
     * View holder for the views we need access to
     */
    private class Holder {
        private TextView tvName, tvDevice, tvTemp, tvDate;
        private RelativeLayout rlyBackground;
        private WifiSignalStrengthView ivSignal;
        private ImageView ivTemperature;
    }
}
