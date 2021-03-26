package kr.brainylab.view.dailog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import kr.brainylab.R;
import kr.brainylab.databinding.DialogNameEditBinding;
import kr.brainylab.databinding.DialogTwoBtnBinding;
import kr.brainylab.utils.Util;

/**
 * 2개 버튼을 가진 팝업
 */
public class TwoBtnDialog extends BaseDialog implements View.OnClickListener {

    private DialogTwoBtnBinding binding;
    private TwoBtnDialog.OnClickListener mListener;

    private String mContent = "";
    private String mLeftTitle = "";
    private String mRightTitle = "";

    public interface OnClickListener {

        void onConfirm();

        void onCancel();
    }

    TwoBtnDialog(Context context) {
        super(context);
    }

    public static TwoBtnDialog init(Context context, String content, String left, String right, TwoBtnDialog.OnClickListener listener) {
        TwoBtnDialog dialog = new TwoBtnDialog(context);
        dialog.mListener = listener;
        dialog.mContent = content;
        dialog.mLeftTitle = left;
        dialog.mRightTitle = right;
        dialog.setData();
        return dialog;
    }

    @Override
    public void initUI() {
        this.binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_two_btn, null, false);
        this.binding.setDialog(this);
        setContentView(binding.getRoot());

        binding.rlyBackground.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
    }

    private void setData() {
        binding.tvContent.setText(mContent);
        binding.btnCancel.setText(mLeftTitle);
        binding.btnConfirm.setText(mRightTitle);
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                mListener.onCancel();
                dismiss();
                break;
            case R.id.btn_confirm:
                dismiss();
                mListener.onConfirm();
                break;

        }
    }
}
