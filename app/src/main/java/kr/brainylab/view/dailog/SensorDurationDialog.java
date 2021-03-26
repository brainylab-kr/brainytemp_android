package kr.brainylab.view.dailog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import kr.brainylab.R;
import kr.brainylab.databinding.DialogSensorDurationBinding;

/**
 * 센서 기간연장
 */
public class SensorDurationDialog extends BaseDialog implements View.OnClickListener {

    private DialogSensorDurationBinding binding;
    private SensorDurationDialog.OnClickListener mListener;

    private int myear = 0;

    public interface OnClickListener {

        void onConfirm(int minute);
    }

    SensorDurationDialog(Context context) {
        super(context);
    }

    public static SensorDurationDialog init(Context context, SensorDurationDialog.OnClickListener listener) {
        SensorDurationDialog dialog = new SensorDurationDialog(context);
        dialog.mListener = listener;
        return dialog;
    }

    @Override
    public void initUI() {
        this.binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_sensor_duration, null, false);
        this.binding.setDialog(this);
        setContentView(binding.getRoot());

        binding.btnCancel.setOnClickListener(this);
        binding.rlyBackground.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.lly1Year.setOnClickListener(this);
        binding.lly2Year.setOnClickListener(this);
        binding.lly3Year.setOnClickListener(this);
        binding.lly0Year.setOnClickListener(this);

        myear = 2;
        changeKind();
    }

    private void changeKind() {
        binding.iv0Year.setBackground(context.getDrawable(R.drawable.ic_option_off1));
        binding.iv1Year.setBackground(context.getDrawable(R.drawable.ic_option_off1));
        binding.iv2Year.setBackground(context.getDrawable(R.drawable.ic_option_off1));
        binding.iv3Year.setBackground(context.getDrawable(R.drawable.ic_option_off1));

        if (myear == 1) {
            binding.iv1Year.setBackground(context.getDrawable(R.drawable.ic_option_on1));
        } else if (myear == 2) {
            binding.iv2Year.setBackground(context.getDrawable(R.drawable.ic_option_on1));
        } else if (myear == 3) {
            binding.iv3Year.setBackground(context.getDrawable(R.drawable.ic_option_on1));
        } else {
            binding.iv0Year.setBackground(context.getDrawable(R.drawable.ic_option_on1));
        }
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lly_1_year:
                myear = 1;
                changeKind();
                break;
            case R.id.lly_2_year:
                myear = 2;
                changeKind();
                break;
            case R.id.lly_3_year:
                myear = 3;
                changeKind();
                break;
            case R.id.lly_0_year:
                myear = 0;
                changeKind();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_confirm:

                dismiss();
                mListener.onConfirm(myear);
                break;

        }
    }
}
