package kr.brainylab.view.dailog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kr.brainylab.R;
import kr.brainylab.common.Common;
import kr.brainylab.databinding.DialogCalendar1Binding;
import kr.brainylab.databinding.DialogCalendarBinding;

/**
 * 달력 팝업
 */
public class Calendar1Dialog extends BaseDialog implements View.OnClickListener {

    private DialogCalendar1Binding binding;
    private Calendar1Dialog.OnClickListener mListener;

    private String mDate = "";

    public interface OnClickListener {

        void onConfirm(String content);
    }

    Calendar1Dialog(Context context) {
        super(context);
    }

    public static Calendar1Dialog init(Context context, String date, Calendar1Dialog.OnClickListener listener) {
        Calendar1Dialog dialog = new Calendar1Dialog(context);
        dialog.mListener = listener;
        dialog.mDate = date;
        dialog.setData();
        return dialog;
    }

    @Override
    public void initUI() {
        this.binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_calendar1, null, false);
        this.binding.setDialog(this);
        setContentView(binding.getRoot());
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        binding.btnCancel.setOnClickListener(this);
        binding.rlyBackground.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
    }

    private void setData() {
        Calendar c = Calendar.getInstance();
        try {
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            Date tempDate = sdFormat.parse(mDate);
            c.setTime(tempDate);
            binding.calendarView.setDateSelected(c, true);
            binding.calendarView.setCurrentDate(c);
            showDate(c);
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
        String date = String.format("%d월 %d일 (%s)", month, day, Common.WEEKS[week - 1]);
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
