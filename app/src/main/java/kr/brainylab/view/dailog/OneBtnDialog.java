package kr.brainylab.view.dailog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import kr.brainylab.R;
import kr.brainylab.databinding.DialogOneBtnBinding;

/**
 * 1개 버튼을 가진 팝업
 */
public class OneBtnDialog extends BaseDialog implements View.OnClickListener {

    private DialogOneBtnBinding binding;
    private OneBtnDialog.OnClickListener mListener;

    private String mContent = "";
    private String mButtonText = "";

    public interface OnClickListener {
        void onConfirm();
    }

    OneBtnDialog(Context context) {
        super(context);
    }

    public static OneBtnDialog init(Context context, String content, String buttonText, OneBtnDialog.OnClickListener listener) {
        OneBtnDialog dialog = new OneBtnDialog(context);
        dialog.mListener = listener;
        dialog.mContent = content;
        dialog.mButtonText = buttonText;
        dialog.setData();
        return dialog;
    }

    @Override
    public void initUI() {
        this.binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_one_btn, null, false);
        this.binding.setDialog(this);
        setContentView(binding.getRoot());

        binding.rlyBackground.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
    }

    private void setData() {
        binding.tvContent.setText(mContent);
        binding.btnConfirm.setText(mButtonText);
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                dismiss();
                mListener.onConfirm();
                break;

        }
    }
}
