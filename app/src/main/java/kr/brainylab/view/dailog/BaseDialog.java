package kr.brainylab.view.dailog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import kr.brainylab.R;


public abstract class BaseDialog extends Dialog {

    public Context context;

    public abstract void initUI();

    public BaseDialog(Context context) {
        super(context, R.style.DialogCustomTheme);
        this.context = context;

        initUI();
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

        this.context = context;

        initUI();
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);

        this.context = context;

        initUI();
    }
}
