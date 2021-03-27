package kr.brainylab.view.dailog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
import kr.brainylab.databinding.DialogDeviceAlarmRepeatBinding;

/**
 * 경보음 반복주기
 */
public class DeviceAlarmRepeatDialog extends BaseDialog implements View.OnClickListener {

    private DialogDeviceAlarmRepeatBinding binding;
    private DeviceAlarmRepeatDialog.OnClickListener mListener;

    private static int mMinute = 0;

    public interface OnClickListener {

        void onConfirm(int minute);
    }

    DeviceAlarmRepeatDialog(Context context) {
        super(context);
    }

    public static DeviceAlarmRepeatDialog init(Context context, DeviceAlarmRepeatDialog.OnClickListener listener) {
        DeviceAlarmRepeatDialog dialog = new DeviceAlarmRepeatDialog(context);
        dialog.mListener = listener;
        return dialog;
    }

    @Override
    public void initUI() {
        this.binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_device_alarm_repeat, null, false);
        this.binding.setDialog(this);
        setContentView(binding.getRoot());

        binding.btnCancel.setOnClickListener(this);
        binding.rlyBackground.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.lly0Minute.setOnClickListener(this);
        binding.lly5Minute.setOnClickListener(this);
        binding.lly15Minute.setOnClickListener(this);
        binding.lly30Minute.setOnClickListener(this);

        mMinute = BrainyTempApp.getAlarmRepeatCycle();
        changeKind();
    }

    private void changeKind() {
        binding.iv0Min.setBackground(context.getDrawable(R.drawable.ic_option_off1));
        binding.iv5Min.setBackground(context.getDrawable(R.drawable.ic_option_off1));
        binding.iv15Min.setBackground(context.getDrawable(R.drawable.ic_option_off1));
        binding.iv30Min.setBackground(context.getDrawable(R.drawable.ic_option_off1));

        if (mMinute == 0) {
            binding.iv0Min.setBackground(context.getDrawable(R.drawable.ic_option_on1));
        } else if (mMinute == 5) {
            binding.iv5Min.setBackground(context.getDrawable(R.drawable.ic_option_on1));
        } else if (mMinute == 15) {
            binding.iv15Min.setBackground(context.getDrawable(R.drawable.ic_option_on1));
        } else {
            binding.iv30Min.setBackground(context.getDrawable(R.drawable.ic_option_on1));
        }
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lly_0_minute:
                mMinute = 0;
                changeKind();
                break;
            case R.id.lly_5_minute:
                mMinute = 5;
                changeKind();
                break;
            case R.id.lly_15_minute:
                mMinute = 15;
                changeKind();
                break;
            case R.id.lly_30_minute:
                mMinute = 30;
                changeKind();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_confirm:

                dismiss();
                mListener.onConfirm(mMinute);
                break;

        }
    }
}
