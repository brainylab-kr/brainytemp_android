package kr.brainylab.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
import kr.brainylab.common.Common;
import kr.brainylab.model.SensorInfo;
import kr.brainylab.view.activity.DetailActivity;
import kr.brainylab.view.activity.MainActivity;

public class SensorListAdapter extends ArrayAdapter<SensorInfo> {
    private LayoutInflater mInflater;
    private Context mContext;
    private Fragment mFrag;

    public SensorListAdapter(Context context, Fragment frag, ArrayList<SensorInfo> values) {
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
            holder.rlyHumi = convertView.findViewById(R.id.rly_humi);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvDevice = convertView.findViewById(R.id.tv_device);
            holder.tvTemp = convertView.findViewById(R.id.tv_temp);
            holder.tvHumi = convertView.findViewById(R.id.tv_humi);
            holder.tvDate = convertView.findViewById(R.id.tv_date);
            holder.rlyBackground = convertView.findViewById(R.id.rly_background);
            holder.ivTemperature = convertView.findViewById(R.id.iv_temperature);
            holder.ivHumi = convertView.findViewById(R.id.iv_humi);
            holder.ivWifiSignal = convertView.findViewById(R.id.iv_wifi_signal);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final SensorInfo info = getItem(position);

        String name = BrainyTempApp.getSensorName(info.getAddress());

        holder.tvName.setText(name);
        holder.tvDevice.setText(info.getAddress());
        holder.tvDate.setText(info.getDate());
        holder.tvTemp.setText(Double.toString(info.getTemp()) + "°C");
        holder.tvHumi.setText(Integer.toString(info.getHumi()) + "%");

        double maxTemp = BrainyTempApp.getMaxTemp(info.getAddress());
        double minTemp = BrainyTempApp.getMinTemp(info.getAddress());
        if (info.getTemp() < minTemp || info.getTemp() > maxTemp) {
            holder.ivTemperature.setBackground(mContext.getDrawable(R.drawable.vd_temperature_red));
            holder.tvTemp.setTextColor(mContext.getResources().getColor(R.color.color_c2185b));

        } else {
            holder.ivTemperature.setBackground(mContext.getDrawable(R.drawable.vd_temperature));
            holder.tvTemp.setTextColor(mContext.getResources().getColor(R.color.color_171717));
        }

        if(info.getType().equals(Common.SENSOR_TYPE_TH)) {
            holder.rlyHumi.setVisibility(View.VISIBLE);
            holder.ivHumi.setVisibility(View.VISIBLE);
            holder.tvHumi.setVisibility(View.VISIBLE);

            int maxHumi = BrainyTempApp.getMaxHumi(info.getAddress());
            int minHumi = BrainyTempApp.getMinHumi(info.getAddress());

            if (info.getHumi() < minHumi || info.getHumi() > maxHumi) {
                holder.ivHumi.setBackground(mContext.getDrawable(R.drawable.vd_humidity_red));
                holder.tvHumi.setTextColor(mContext.getResources().getColor(R.color.color_c2185b));
            } else {
                holder.ivHumi.setBackground(mContext.getDrawable(R.drawable.vd_humidity));
                holder.tvHumi.setTextColor(mContext.getResources().getColor(R.color.color_171717));
            }
        }
        else {
            holder.rlyHumi.setVisibility(View.GONE);
            holder.ivHumi.setVisibility(View.GONE);
            holder.tvHumi.setVisibility(View.GONE);
        }

        int nRssi =  info.getRssi();
        if (nRssi > -50) {
            holder.ivWifiSignal.setImageResource(R.drawable.vd_signal_wifi_4);
        } else if (nRssi <= -50 && nRssi > -70) {
            holder.ivWifiSignal.setImageResource(R.drawable.vd_signal_wifi_3);
        } else if (nRssi <= -70 && nRssi > -80) {
            holder.ivWifiSignal.setImageResource(R.drawable.vd_signal_wifi_2);
        } else if (nRssi <= -80 && nRssi > -100) {
            holder.ivWifiSignal.setImageResource(R.drawable.vd_signal_wifi_1);
        } else if (nRssi <= -100) {
            holder.ivWifiSignal.setImageResource(R.drawable.vd_signal_wifi_off);
        }

        holder.rlyBackground.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Common.gSelDevice = info.getAddress();
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
                intent.putExtra("device", info.getAddress());
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
        private TextView tvName, tvDevice, tvTemp, tvHumi, tvDate;
        private RelativeLayout rlyBackground;
        private LinearLayout rlyHumi;
        private ImageView ivTemperature;
        private ImageView ivHumi;
        private ImageView ivWifiSignal;
    }
}
