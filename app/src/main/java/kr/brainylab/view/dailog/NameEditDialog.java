package kr.brainylab.view.dailog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import kr.brainylab.R;
import kr.brainylab.databinding.DialogNameEditBinding;
import kr.brainylab.databinding.DialogReceiveUserBinding;
import kr.brainylab.utils.Util;

/**
 * 센서 이름 변경 팝업
 */
public class NameEditDialog extends BaseDialog implements View.OnClickListener {

    private DialogNameEditBinding binding;
    private NameEditDialog.OnClickListener mListener;

    private String mName = "";

    public interface OnClickListener {

        void onConfirm(String content);
    }

    NameEditDialog(Context context) {
        super(context);
    }

    public static NameEditDialog init(Context context, String name, NameEditDialog.OnClickListener listener) {
        NameEditDialog dialog = new NameEditDialog(context);
        dialog.mListener = listener;
        dialog.mName = name;
        dialog.setData();
        ;
        return dialog;
    }

    @Override
    public void initUI() {
        this.binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_name_edit, null, false);
        this.binding.setDialog(this);
        setContentView(binding.getRoot());
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        binding.btnCancel.setOnClickListener(this);
        binding.rlyBackground.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
    }

    private void setData() {
        binding.edtName.setText(mName);
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rly_background:
                Util.hideKeyboard(binding.edtName);
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_confirm:
                String content = binding.edtName.getText().toString();

                if (content.equals("")) {
                    Util.showToast(context, context.getString(R.string.empty_content));
                    return;
                }

                dismiss();
                mListener.onConfirm(content);
                break;

        }
    }
}
