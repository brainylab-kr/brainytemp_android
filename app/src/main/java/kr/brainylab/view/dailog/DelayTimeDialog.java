package kr.brainylab.view.dailog;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import kr.brainylab.BrainyTempApp;
import kr.brainylab.R;
import kr.brainylab.databinding.DialogDelayTimeBinding;
import kr.brainylab.utils.Util;

/**
 * 온도 제한
 */
public class DelayTimeDialog extends BaseDialog implements View.OnClickListener {

    private DialogDelayTimeBinding binding;
    private DelayTimeDialog.OnClickListener mListener;

    private String mDevice = "";

    public interface OnClickListener {
        void onConfirm(int minute);
    }

    DelayTimeDialog(Context context) {
        super(context);
    }

    public static DelayTimeDialog init(Context context, String device, DelayTimeDialog.OnClickListener listener) {
        DelayTimeDialog dialog = new DelayTimeDialog(context);
        dialog.mListener = listener;
        dialog.mDevice = device;
        dialog.setData();
        return dialog;
    }

    @Override
    public void initUI() {
        this.binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_delay_time, null, false);
        this.binding.setDialog(this);
        setContentView(binding.getRoot());
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        binding.btnCancel.setOnClickListener(this);
        binding.rlyBackground.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.tvAlert.setVisibility(View.GONE);

        binding.edtMinute.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                binding.tvAlert.setVisibility(View.GONE);
            }
        });
    }

    private void setData() {
        int delayTime = BrainyTempApp.getDelayTime(mDevice);
        binding.edtMinute.setText(String.valueOf(delayTime));
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rly_background:
                Util.hideKeyboard(binding.edtMinute);
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_confirm:
                String content = binding.edtMinute.getText().toString();

                if (content.equals("")) {
                    Util.showToast(context, context.getString(R.string.empty_content));
                    return;
                }

                int min = Integer.parseInt(content);
                if (min < 1 || min > 60) {
                    binding.tvAlert.setVisibility(View.VISIBLE);
                    return;
                }

                dismiss();
                mListener.onConfirm(min);
                break;

        }
    }
}
