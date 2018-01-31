package com.constantlab.statistics.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.constantlab.statistics.R;
import com.constantlab.statistics.network.RTService;
import com.constantlab.statistics.network.ServiceGenerator;
import com.constantlab.statistics.network.model.BasicSingleDataResponse;
import com.constantlab.statistics.network.model.LoginKey;
import com.constantlab.statistics.ui.MainActivity;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.utils.SharedPreferencesManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sunny Kinger on 04-12-2017.
 */

public class LoginFragment extends BaseFragment {

    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.pb_login)
    ProgressBar pbLogin;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreferencesManager.getInstance().getKey(getContext()) != null) {
            goToMain();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pbLogin.setIndeterminate(true);

//        etUsername.setText("Indira_123");
//        etPassword.setText("Qwerty123");
    }

    private void loading(boolean show) {
        pbLogin.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.btn_login)
    public void login() {
        String user = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        RTService rtService = ServiceGenerator.createService(RTService.class);
        Call<BasicSingleDataResponse<LoginKey>> call = rtService.getKey(user, password);
        loading(true);
        call.enqueue(new Callback<BasicSingleDataResponse<LoginKey>>() {
            @Override
            public void onResponse(Call<BasicSingleDataResponse<LoginKey>> call, Response<BasicSingleDataResponse<LoginKey>> response) {
                if (response.body().isSuccess()) {
                    SharedPreferencesManager.getInstance().setKey(getContext(), response.body().getData().getKey());
                    goToMain();
                } else {
                    Toast.makeText(getContext(), getContext().getString(R.string.message_wrong_credentials), Toast.LENGTH_SHORT).show();
                }
                loading(false);
            }

            @Override
            public void onFailure(Call<BasicSingleDataResponse<LoginKey>> call, Throwable t) {
                Toast.makeText(getContext(), getContext().getString(R.string.message_connection_problem), Toast.LENGTH_SHORT).show();
                loading(false);
            }
        });
    }

    private void goToMain() {
        startActivity(new Intent(getContext(), MainActivity.class));
        getActivity().finish();
    }
}
