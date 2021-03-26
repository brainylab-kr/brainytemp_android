package kr.brainylab.view.dailog;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import kr.brainylab.R;
import kr.brainylab.common.Common;
import kr.brainylab.databinding.DialogReceiveUserBinding;
import kr.brainylab.model.AlarmListInfo;
import kr.brainylab.utils.Util;

/**
 * 하트 팝업
 */
public class ReceiveUserDialog extends BaseDialog implements View.OnClickListener {

    private DialogReceiveUserBinding binding;
    private ReceiveUserDialog.OnClickListener mListener;

    private AlarmListInfo mInfo = null;
    private String mKind = "";
    public interface OnClickListener {

        void onConfirm(String type, String content);

        void onCancel();
    }

    ReceiveUserDialog(Context context) {
        super(context);
    }

    public static ReceiveUserDialog init(Context context, AlarmListInfo info, ReceiveUserDialog.OnClickListener listener) {
        ReceiveUserDialog dialog = new ReceiveUserDialog(context);
        dialog.mListener = listener;
        dialog.mInfo = info;
        dialog.setData();
        return dialog;
    }

    @Override
    public void initUI() {
        this.binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_receive_user, null, false);
        this.binding.setDialog(this);
        setContentView(binding.getRoot());
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        binding.btnCancel.setOnClickListener(this);
        binding.rlyBackground.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.rlyWeb.setOnClickListener(this);
        //binding.rlyAlarm.setOnClickListener(this);
    }

    private void setData() {
        mKind = Common.ALARM_CLOUD_SMS;
        if (mInfo == null) {
            //추가
            binding.tvTitle.setText(getContext().getString(R.string.receive_user_add));
            binding.edtContent.setText("");
        } else {
            //편집
            mKind = mInfo.getType();
            binding.tvTitle.setText(getContext().getString(R.string.receive_user_edit));
            binding.edtContent.setText(mInfo.getPhone());
        }
        changeKind();
    }

    private void changeKind() {
        binding.ivWeb.setBackground(context.getDrawable(R.drawable.ic_option_off1));
        //binding.ivAlarm.setBackground(context.getDrawable(R.drawable.ic_option_off1));

        if (mKind.equals(Common.ALARM_CLOUD_SMS)) {
            binding.ivWeb.setBackground(context.getDrawable(R.drawable.ic_option_on1));
        } else {
            //binding.ivAlarm.setBackground(context.getDrawable(R.drawable.ic_option_on1));
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
                Util.hideKeyboard(binding.edtContent);
                break;
            case R.id.rly_web:
                mKind = Common.ALARM_CLOUD_SMS;
                changeKind();
                break;
            case R.id.rly_alarm:
                mKind = Common.ALARM_ALIMTALK;
                changeKind();
                break;
            case R.id.btn_cancel:
                dismiss();
                mListener.onCancel();
                break;
            case R.id.btn_confirm:
                String content = binding.edtContent.getText().toString();

                if (content.equals("")) {
                    Util.showToast(context, context.getString(R.string.empty_content));
                    return;
                }

                if (content.length() != 11) {
                    Util.showToast(context, context.getString(R.string.incorrect_phone));
                    return;
                }

                if (Util.isExistAlarm(content)) {
                    Util.showToast(context, context.getString(R.string.exist_phone));
                    return;
                }

                dismiss();
                mListener.onConfirm(mKind, content);
                break;

        }
    }
}
