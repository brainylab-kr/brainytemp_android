package kr.brainylab.view.dailog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
import kr.brainylab.databinding.DialogReceiveUserBinding;
import kr.brainylab.databinding.DialogTempLimitBinding;
import kr.brainylab.utils.Util;
import kr.brainylab.view.activity.IntroActivity;

/**
 * 온도 제한
 */
public class TempLimitDialog extends BaseDialog implements View.OnClickListener {

    private DialogTempLimitBinding binding;
    private TempLimitDialog.OnClickListener mListener;

    private int mType = 0;
    private String mDevice = "";

    public interface OnClickListener {
        void onConfirm(double temp);
    }

    TempLimitDialog(Context context) {
        super(context);
    }

    public static TempLimitDialog init(Context context, int type, String device, TempLimitDialog.OnClickListener listener) {
        TempLimitDialog dialog = new TempLimitDialog(context);
        dialog.mListener = listener;
        dialog.mType = type;
        dialog.mDevice = device;
        dialog.setData();
        return dialog;
    }

    @Override
    public void initUI() {
        this.binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_temp_limit, null, false);
        this.binding.setDialog(this);
        setContentView(binding.getRoot());
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        binding.btnCancel.setOnClickListener(this);
        binding.rlyBackground.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
    }

    private void setData() {
        if (mType == 0) { //max
            binding.tvSubTitle.setText(context.getString(R.string.max_temp));
            binding.tvAlert.setText(context.getString(R.string.temp_limit_hint_max));

            double maxTemp = BrainyTempApp.getMaxTemp(mDevice);
            binding.edtTemp.setText(String.valueOf(maxTemp));
        } else {
            binding.tvSubTitle.setText(context.getString(R.string.min_temp));
            binding.tvAlert.setText(context.getString(R.string.temp_limit_hint_min));

            double minTemp = BrainyTempApp.getMinTemp(mDevice);
            binding.edtTemp.setText(String.valueOf(minTemp));
        }
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rly_background:
                Util.hideKeyboard(binding.edtTemp);
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_confirm:
                String content = binding.edtTemp.getText().toString();

                if (content.equals("")) {
                    Util.showToast(context, context.getString(R.string.empty_content));
                    return;
                }

                try {
                    double fTemp = Double.parseDouble(content);
                    if (mType == 0) { //max
                        if (fTemp < -35 || fTemp > 70) {
                            Util.showToast(context, context.getString(R.string.temp_limit_hint_max));
                            return;
                        }
                    } else {
                        if (fTemp < -35 || fTemp > 70) {
                            Util.showToast(context, context.getString(R.string.temp_limit_hint_min));
                            return;
                        }
                    }

                    dismiss();
                    mListener.onConfirm(fTemp);
                } catch (NumberFormatException e) {
                    Util.showToast(context, context.getString(R.string.temp_limit_hint_incorrect));
                }

                break;

        }
    }
}
