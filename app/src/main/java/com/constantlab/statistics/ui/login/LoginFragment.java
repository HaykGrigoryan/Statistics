package com.constantlab.statistics.ui.login;

import android.app.AlertDialog;
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
import com.constantlab.statistics.app.RealmManager;
import com.constantlab.statistics.models.User;
import com.constantlab.statistics.network.RTService;
import com.constantlab.statistics.network.ServiceGenerator;
import com.constantlab.statistics.network.model.BasicSingleDataResponse;
import com.constantlab.statistics.network.model.GetKeyRequest;
import com.constantlab.statistics.network.model.LoginKey;
import com.constantlab.statistics.ui.MainActivity;
import com.constantlab.statistics.ui.base.BaseFragment;
import com.constantlab.statistics.utils.DialogManager;
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
        User user = SharedPreferencesManager.getInstance().getUser(getContext());
        if (user != null) {
            if (RealmManager.getInstance().getUser(user.getUsername(), user.getPassword()) != null) {
                goToMain();
            } else {
                SharedPreferencesManager.getInstance().removeUser(getContext());
            }
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
//
//        etUsername.setText("dummy1");
//        etPassword.setText("qwe123qwe");
    }

    private void loading(boolean show) {
        pbLogin.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    AlertDialog mServerInfoDialog;

    @OnClick(R.id.btn_server_info)
    public void serverInfo() {
        mServerInfoDialog = DialogManager.getInstance().showServerInfoDialog(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverIp = ((EditText) mServerInfoDialog.findViewById(R.id.et_ip)).getText().toString();
                String serverPort = ((EditText) mServerInfoDialog.findViewById(R.id.et_port)).getText().toString();
                SharedPreferencesManager.getInstance().setServerIP(getContext(), serverIp);
                SharedPreferencesManager.getInstance().setServerPort(getContext(), serverPort);

                if (mServerInfoDialog != null) {
                    mServerInfoDialog.dismiss();
                }
            }
        });
    }

    @OnClick(R.id.btn_login)
    public void login() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        User user = RealmManager.getInstance().getUser(username, password);
        if (user != null) {
            SharedPreferencesManager.getInstance().setUser(getContext(), user);
            goToMain();
        } else {
            RTService rtService = ServiceGenerator.createService(RTService.class, getContext(), false);
            Call<BasicSingleDataResponse<String>> call = rtService.getKey(new GetKeyRequest(username, password));
            loading(true);
            call.enqueue(new Callback<BasicSingleDataResponse<String>>() {
                @Override
                public void onResponse(Call<BasicSingleDataResponse<String>> call, Response<BasicSingleDataResponse<String>> response) {
                    if (response.code() == 200 && response.body().isSuccessNestedStatus()) {
                        User u = User.createUser(getContext(), username, password, response.body().getData());
                        RealmManager.getInstance().addUser(u);
                        SharedPreferencesManager.getInstance().setUser(getContext(), RealmManager.getInstance().getUser(username, password));
                        goToMain();
                    } else {
                        Toast.makeText(getContext(), getContext().getString(R.string.message_wrong_credentials), Toast.LENGTH_SHORT).show();
                    }
                    loading(false);
                }

                @Override
                public void onFailure(Call<BasicSingleDataResponse<String>> call, Throwable t) {
                    Toast.makeText(getContext(), getContext().getString(R.string.message_connection_problem), Toast.LENGTH_SHORT).show();
                    loading(false);
                }
            });
        }

    }

    private void goToMain() {
        startActivity(new Intent(getContext(), MainActivity.class));
        getActivity().finish();
    }
}
