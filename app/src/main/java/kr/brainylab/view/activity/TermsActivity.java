package kr.brainylab.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import kr.brainylab.R;
import kr.brainylab.common.Common;
import kr.brainylab.databinding.ActivityTermsBinding;

/**
 * 센서 상세
 */
public class TermsActivity extends BaseActivity implements View.OnClickListener {

    ActivityTermsBinding binding;

    public String pageType = "";  //페이지 번호

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        this.binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_terms, null, false);
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        pageType = intent.getExtras().getString("page", "");

        LoadLayout();
    }

    private void LoadLayout() {
        binding.rlyBack.setOnClickListener(this);

        if (pageType.equals(Common.PAGE_POLICY)) {
            binding.tvTitle.setText(getResources().getString(R.string.policy));
        } else if (pageType.equals(Common.PAGE_OPEN_SOURCE)) {
            binding.tvTitle.setText(getResources().getString(R.string.opensource));
        } else {
            binding.tvTitle.setText(getResources().getString(R.string.terms));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    /**
     * Click Events
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rly_back:
                finish();
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                break;

        }
    }
}