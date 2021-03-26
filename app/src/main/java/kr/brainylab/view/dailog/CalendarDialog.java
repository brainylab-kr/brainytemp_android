package kr.brainylab.view.dailog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.math.BigInteger;
import java.util.Calendar;

import kr.brainylab.R;
import kr.brainylab.common.Common;
import kr.brainylab.databinding.DialogCalendarBinding;
import kr.brainylab.databinding.DialogSensorDelBinding;

/**
 * 달력 팝업
 */
public class CalendarDialog extends BaseDialog implements View.OnClickListener {

    private DialogCalendarBinding binding;
    private CalendarDialog.OnClickListener mListener;

    public interface OnClickListener {

        void onConfirm(String content);
    }

    CalendarDialog(Context context) {
        super(context);
    }

    public static CalendarDialog init(Context context, CalendarDialog.OnClickListener listener) {
        CalendarDialog dialog = new CalendarDialog(context);
        dialog.mListener = listener;
        return dialog;
    }

    @Override
    public void initUI() {
        this.binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_calendar, null, false);
        this.binding.setDialog(this);
        setContentView(binding.getRoot());
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        binding.btnCancel.setOnClickListener(this);
        binding.rlyBackground.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);

        String title = "28:2C:02:40:32:56" + context.getString(R.string.calendar_title);
        binding.tvTitle.setText(title);

        Calendar c = Calendar.getInstance();
        binding.calendarView.setDateSelected(c, true);
        showDate(c);

        binding.calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                showDate(date.getCalendar());
            }
        });
    }

    private void showDate(Calendar c) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int week = c.get(Calendar.DAY_OF_WEEK);
        binding.tvYear.setText(String.valueOf(year));
        String date = String.format("%d월 %d일 (%s)", month, day, Common.WEEKS[week]);
        binding.tvDate.setText(date);
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
                break;
            case R.id.btn_confirm:

                dismiss();
                mListener.onConfirm("2021-02-25");
                break;

        }
    }
}
