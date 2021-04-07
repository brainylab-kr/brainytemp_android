package kr.brainylab.view.dailog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
import kr.brainylab.databinding.DialogSensingRepeatBinding;

/**
 * 알림 반복주기
 */
public class SensingRepeatDialog extends BaseDialog implements View.OnClickListener {

    private DialogSensingRepeatBinding binding;
    private SensingRepeatDialog.OnClickListener mListener;

    private static int mMinute = 0;

    public interface OnClickListener {

        void onConfirm(int minute);
    }

    SensingRepeatDialog(Context context) {
        super(context);
    }

    public static SensingRepeatDialog init(Context context, SensingRepeatDialog.OnClickListener listener) {
        SensingRepeatDialog dialog = new SensingRepeatDialog(context);
        dialog.mListener = listener;
        return dialog;
    }

    @Override
    public void initUI() {
        this.binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_sensing_repeat, null, false);
        this.binding.setDialog(this);
        setContentView(binding.getRoot());

        binding.btnCancel.setOnClickListener(this);
        binding.rlyBackground.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.lly1Minute.setOnClickListener(this);
        binding.lly5Minute.setOnClickListener(this);
        binding.lly15Minute.setOnClickListener(this);
        binding.lly30Minute.setOnClickListener(this);
        binding.lly60Minute.setOnClickListener(this);

        mMinute = BrainyTempApp.getSensingRepeatCycle();
        changeKind();
    }

    private void changeKind() {
        binding.iv1Min.setBackground(context.getDrawable(R.drawable.ic_option_off1));
        binding.iv5Min.setBackground(context.getDrawable(R.drawable.ic_option_off1));
        binding.iv15Min.setBackground(context.getDrawable(R.drawable.ic_option_off1));
        binding.iv30Min.setBackground(context.getDrawable(R.drawable.ic_option_off1));
        binding.iv60Min.setBackground(context.getDrawable(R.drawable.ic_option_off1));

        if (mMinute == 1) {
            binding.iv1Min.setBackground(context.getDrawable(R.drawable.ic_option_on1));
        } else if (mMinute == 5) {
            binding.iv5Min.setBackground(context.getDrawable(R.drawable.ic_option_on1));
        } else if (mMinute == 15) {
            binding.iv15Min.setBackground(context.getDrawable(R.drawable.ic_option_on1));
        } else if (mMinute == 30) {
            binding.iv30Min.setBackground(context.getDrawable(R.drawable.ic_option_on1));
        } else {
            binding.iv60Min.setBackground(context.getDrawable(R.drawable.ic_option_on1));
        }
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lly_1_minute:
                mMinute = 1;
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
            case R.id.lly_60_minute:
                mMinute = 60;
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
