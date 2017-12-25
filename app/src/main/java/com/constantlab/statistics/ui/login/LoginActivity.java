package com.constantlab.statistics.ui.login;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.constantlab.statistics.R;
import com.constantlab.statistics.ui.base.BaseActivity;

/**
 * Created by Sunny Kinger on 04-12-2017.
 */

public class LoginActivity extends BaseActivity {
    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    @Override
    protected int getTaskFragmentContainerId() {
        return 0;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_fragment);
        showFragment(LoginFragment.newInstance(), false);
    }
}
