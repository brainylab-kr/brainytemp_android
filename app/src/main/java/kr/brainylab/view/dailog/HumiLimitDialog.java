package kr.brainylab.view.dailog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
import kr.brainylab.databinding.DialogHumiLimitBinding;
import kr.brainylab.utils.Util;

/**
 * 습도 제한
 */
public class HumiLimitDialog extends BaseDialog implements View.OnClickListener {

    private DialogHumiLimitBinding binding;
    private HumiLimitDialog.OnClickListener mListener;

    private int mType = 0;
    private String mDevice = "";

    public interface OnClickListener {
        void onConfirm(int humi);
    }

    HumiLimitDialog(Context context) {
        super(context);
    }

    public static HumiLimitDialog init(Context context, int type, String device, HumiLimitDialog.OnClickListener listener) {
        HumiLimitDialog dialog = new HumiLimitDialog(context);
        dialog.mListener = listener;
        dialog.mType = type;
        dialog.mDevice = device;
        dialog.setData();
        return dialog;
    }

    @Override
    public void initUI() {
        this.binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_humi_limit, null, false);
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
            binding.tvAlert.setText(context.getString(R.string.humi_limit_hint));

            int maxHumi = BrainyTempApp.getMaxHumi(mDevice);
            binding.edtHumi.setText(String.valueOf(maxHumi));
        } else {
            binding.tvSubTitle.setText(context.getString(R.string.min_temp));
            binding.tvAlert.setText(context.getString(R.string.humi_limit_hint));

            int minHumi = BrainyTempApp.getMinHumi(mDevice);
            binding.edtHumi.setText(String.valueOf(minHumi));
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
                Util.hideKeyboard(binding.edtHumi);
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_confirm:
                String content = binding.edtHumi.getText().toString();

                if (content.equals("")) {
                    Util.showToast(context, context.getString(R.string.empty_content));
                    return;
                }

                try {
                    int humi = Integer.parseInt(content);
                    if (mType == 0) { //max
                        if (humi < 0 || humi > 100) {
                            Util.showToast(context, context.getString(R.string.humi_limit_hint));
                            return;
                        }
                    } else {
                        if (humi < 0 || humi > 100) {
                            Util.showToast(context, context.getString(R.string.humi_limit_hint));
                            return;
                        }
                    }

                    dismiss();
                    mListener.onConfirm(humi);
                } catch (NumberFormatException e) {
                    Util.showToast(context, context.getString(R.string.humi_limit_hint_incorrect));
                }

                break;

        }
    }
}
