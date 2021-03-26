package kr.brainylab.view.dailog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import kr.brainylab.R;
import kr.brainylab.databinding.DialogNameEditBinding;
import kr.brainylab.databinding.DialogSensorDelBinding;
import kr.brainylab.utils.Util;

/**
 * 센서 이름 변경 팝업
 */
public class SensorDelDialog extends BaseDialog implements View.OnClickListener {

    private DialogSensorDelBinding binding;
    private SensorDelDialog.OnClickListener mListener;

    private int mKind = 1;

    public interface OnClickListener {

        void onConfirm();
        void onCancel();
    }

    SensorDelDialog(Context context) {
        super(context);
    }

    public static SensorDelDialog init(Context context, SensorDelDialog.OnClickListener listener) {
        SensorDelDialog dialog = new SensorDelDialog(context);
        dialog.mListener = listener;
        return dialog;
    }

    @Override
    public void initUI() {
        this.binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_sensor_del, null, false);
        this.binding.setDialog(this);
        setContentView(binding.getRoot());

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
