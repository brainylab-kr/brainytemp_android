
package kr.brainylab.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import kr.brainylab.R;
import kr.brainylab.model.RingtoneListInfo;

public class RingtoneListAdapter extends ArrayAdapter<RingtoneListInfo> {
    private LayoutInflater mInflater;
    private Context mContext;

    public RingtoneListAdapter(Context context, ArrayList<RingtoneListInfo> values) {
        super(context, R.layout.item_ringtone, values);
        mContext = context;
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final Holder holder;

        if (convertView == null) {
            // Inflate the view since it does not exist
            convertView = mInflater.inflate(R.layout.item_ringtone, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            // This must be done for performance reasons
            holder = new Holder();

            holder.tvDevice = convertView.findViewById(R.id.tv_device);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvTemp = convertView.findViewById(R.id.tv_temperature);
            holder.rlyHumi = convertView.findViewById(R.id.rly_humi);
            holder.tvHumi = convertView.findViewById(R.id.tv_humi);
            holder.tvDate = convertView.findViewById(R.id.tv_date);


            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final RingtoneListInfo info = getItem(position);

        holder.tvName.setText(info.getName());
        holder.tvDevice.setText(info.getDevice());
        holder.tvTemp.setText(info.getTemp() + "°C");
        holder.tvHumi.setText(info.getHumi() + "%");
        holder.tvDate.setText(info.getTime());

        if(Integer.parseInt(info.getHumi()) <= 0) {
            holder.rlyHumi.setVisibility(View.GONE);
        }
        return convertView;
    }

    /**
     * View holder for the views we need access to
     */
    private class Holder {
        private TextView tvDevice, tvName, tvTemp, tvHumi, tvDate;
        private LinearLayout rlyHumi;
    }
}
