package kr.brainylab.adapter;

import android.content.Context;
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

import kr.brainylab.R;
import kr.brainylab.model.SensorAddInfo;
import kr.brainylab.view.fragment.SearchFragment;
import pl.efento.sdk.api.scan.Device;

public class SensorAddAdapter extends ArrayAdapter<SensorAddInfo> {
    private LayoutInflater mInflater;
    private Context mContext;
    private Fragment mFrag;

    public SensorAddAdapter(Context context, Fragment frag, ArrayList<SensorAddInfo> values) {
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
            convertView = mInflater.inflate(R.layout.item_sensor_add, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            // This must be done for performance reasons
            holder = new Holder();
            holder.tvDevice = convertView.findViewById(R.id.tv_device);
            holder.rlyBackground = convertView.findViewById(R.id.rly_background);
            holder.ivSignal = convertView.findViewById(R.id.iv_signal);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final SensorAddInfo info = getItem(position);

        holder.tvDevice.setText(info.getDevice());
        int nRssi =  info.getRssi();
        if (nRssi > -50) {
            holder.ivSignal.setImageResource(R.drawable.vd_signal_wifi_4);
        } else if (nRssi <= -50 && nRssi > -70) {
            holder.ivSignal.setImageResource(R.drawable.vd_signal_wifi_3);
        } else if (nRssi <= -70 && nRssi > -80) {
            holder.ivSignal.setImageResource(R.drawable.vd_signal_wifi_2);
        } else if (nRssi <= -80 && nRssi > -100) {
            holder.ivSignal.setImageResource(R.drawable.vd_signal_wifi_1);
        } else if (nRssi <= -100) {
            holder.ivSignal.setImageResource(R.drawable.vd_signal_wifi_off);
        }

        holder.rlyBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SearchFragment) mFrag).addSensor(position);
                /*
                SensorCorrectionDialog.init(mContext, new SensorCorrectionDialog.OnClickListener() {
                    @Override
                    public void onConfirm() {
                        CalendarDialog.init(mContext, new CalendarDialog.OnClickListener() {
                            @Override
                            public void onConfirm(String content) {

                                SensorDurationDialog.init(mContext, new SensorDurationDialog.OnClickListener() {
                                    @Override
                                    public void onConfirm(int minute) {

                                    }
                                }).show();

                            }
                        }).show();
                    }

                    @Override
                    public void onCancel() {
                        //
                    }
                }).show();
                 */
            }
        });
        return convertView;
    }

    /**
     * View holder for the views we need access to
     */
    private class Holder {
        private TextView tvDevice;
        private ImageView ivSignal;
        private RelativeLayout rlyBackground;
    }
}
