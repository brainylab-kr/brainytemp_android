package kr.brainylab.view.dailog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import kr.brainylab.R;
import kr.brainylab.databinding.DialogSensorCorrectionBinding;
import kr.brainylab.databinding.DialogSensorDelBinding;

/**
 * 센서 교정 팝업
 */
public class SensorCorrectionDialog extends BaseDialog implements View.OnClickListener {

    private DialogSensorCorrectionBinding binding;
    private SensorCorrectionDialog.OnClickListener mListener;

    private int mKind = 1;

    public interface OnClickListener {

        void onConfirm();

        void onCancel();
    }

    SensorCorrectionDialog(Context context) {
        super(context);
    }

    public static SensorCorrectionDialog init(Context context, SensorCorrectionDialog.OnClickListener listener) {
        SensorCorrectionDialog dialog = new SensorCorrectionDialog(context);
        dialog.mListener = listener;
        return dialog;
    }

    @Override
    public void initUI() {
        this.binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_sensor_correction, null, false);
        this.binding.setDialog(this);
        setContentView(binding.getRoot());
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        binding.btnCancel.setOnClickListener(this);
        binding.rlyBackground.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                dismiss();
                mListener.onCancel();
                break;
            case R.id.btn_confirm:

                dismiss();
                mListener.onConfirm();
                break;

        }
    }
}
