package kr.brainylab.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import kr.brainylab.R;
import kr.brainylab.common.Common;
import kr.brainylab.model.AlarmListInfo;
import kr.brainylab.utils.Util;
import kr.brainylab.view.activity.MainActivity;
import kr.brainylab.view.fragment.AlarmFragment;
import kr.brainylab.view.fragment.SearchFragment;

public class AlarmListAdapter extends ArrayAdapter<AlarmListInfo> {
    private LayoutInflater mInflater;
    private Context mContext;
    private Fragment mFrag;

    public AlarmListAdapter(Context context, Fragment frag, ArrayList<AlarmListInfo> values) {
        super(context, R.layout.item_alarm, values);
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
            convertView = mInflater.inflate(R.layout.item_alarm, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            // This must be done for performance reasons
            holder = new Holder();
            holder.llyDetail = convertView.findViewById(R.id.lly_detail);
            holder.llyContent = convertView.findViewById(R.id.lly_content);

            holder.llyCheck1 = convertView.findViewById(R.id.lly_check1);
            holder.llyCheck2 = convertView.findViewById(R.id.lly_check2);
            holder.llyCheck3 = convertView.findViewById(R.id.lly_check3);
            holder.llyCheck4 = convertView.findViewById(R.id.lly_check4);

            holder.tvTitle = convertView.findViewById(R.id.tv_title);
            holder.tvContent = convertView.findViewById(R.id.tv_content);
            holder.ivArrow = convertView.findViewById(R.id.iv_arrow);
            holder.ivCheck1 = convertView.findViewById(R.id.iv_check1);
            holder.ivCheck2 = convertView.findViewById(R.id.iv_check2);
            holder.ivCheck3 = convertView.findViewById(R.id.iv_check3);
            holder.ivCheck4 = convertView.findViewById(R.id.iv_check4);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final AlarmListInfo info = getItem(position);

        holder.tvTitle.setText(info.getPhone());

        String content = "";
        if (info.getTemp()) {
            content = content + "," + "온도";
        }

        if (info.getBattery()) {
            content = content + "," + "배터리";
        }

        if (info.getConnect()) {
            content = content + "," + "통신";
        }

        if (info.getError()) {
            content = content + "," + "S/W 오류";
        }

        if (!content.isEmpty()) {
            holder.tvContent.setText(content.substring(1, content.length()));
        } else {
            holder.tvContent.setText(content);
        }

        final int selIndex = ((AlarmFragment) mFrag).mSelIndex;
        if (selIndex == position) {
            holder.llyDetail.setVisibility(View.VISIBLE);
            holder.ivArrow.setBackground(mContext.getDrawable(R.drawable.ic_arrow_up));
        } else {
            holder.llyDetail.setVisibility(View.GONE);
            holder.ivArrow.setBackground(mContext.getDrawable(R.drawable.ic_arrow_down));
        }

        if (info.getTemp()) {
            holder.ivCheck1.setBackground(mContext.getDrawable(R.drawable.ic_checkbox_on));
        } else {
            holder.ivCheck1.setBackground(mContext.getDrawable(R.drawable.ic_checkbox_off));
        }

        if (info.getBattery()) {
            holder.ivCheck2.setBackground(mContext.getDrawable(R.drawable.ic_checkbox_on));
        } else {
            holder.ivCheck2.setBackground(mContext.getDrawable(R.drawable.ic_checkbox_off));
        }

        if (info.getConnect()) {
            holder.ivCheck3.setBackground(mContext.getDrawable(R.drawable.ic_checkbox_on));
        } else {
            holder.ivCheck3.setBackground(mContext.getDrawable(R.drawable.ic_checkbox_off));
        }

        if (info.getError()) {
            holder.ivCheck4.setBackground(mContext.getDrawable(R.drawable.ic_checkbox_on));
        } else {
            holder.ivCheck4.setBackground(mContext.getDrawable(R.drawable.ic_checkbox_off));
        }

        holder.llyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selIndex == position) {
                    ((AlarmFragment) mFrag).mSelIndex = -2;
                } else {
                    ((AlarmFragment) mFrag).mSelIndex = position;
                }
                notifyDataSetChanged();
            }
        });

        holder.llyContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Common.gAlarmInfo = info;
                MainActivity mainActivity = (MainActivity) mContext;
                mainActivity.bEdit = true;
                mainActivity.changeTitle();
                return true;
            }
        });

        holder.llyCheck1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmListInfo item = new AlarmListInfo(info.getPhone(), info.getType(), !info.getTemp(), info.getBattery(), info.getConnect(), info.getError());
                Util.updateAlarm(mContext, info.getPhone(),item);
            }
        });

        holder.llyCheck2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmListInfo item = new AlarmListInfo(info.getPhone(), info.getType(), info.getTemp(), !info.getBattery(), info.getConnect(), info.getError());
                Util.updateAlarm(mContext, info.getPhone(),item);
            }
        });

        holder.llyCheck3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmListInfo item = new AlarmListInfo(info.getPhone(), info.getType(), info.getTemp(), info.getBattery(), !info.getConnect(), info.getError());
                Util.updateAlarm(mContext, info.getPhone(),item);
            }
        });

        holder.llyCheck4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmListInfo item = new AlarmListInfo(info.getPhone(), info.getType(), info.getTemp(), info.getBattery(), info.getConnect(), !info.getError());
                Util.updateAlarm(mContext, info.getPhone(),item);
            }
        });

        return convertView;
    }

    /**
     * View holder for the views we need access to
     */
    private class Holder {
        private LinearLayout llyDetail, llyContent, llyCheck1, llyCheck2, llyCheck3, llyCheck4;
        private TextView tvTitle, tvContent;
        private ImageView ivArrow, ivCheck1, ivCheck2, ivCheck3, ivCheck4, ivCheck5;
    }
}
