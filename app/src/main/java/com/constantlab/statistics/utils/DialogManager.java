package com.constantlab.statistics.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.constantlab.statistics.R;

/**
 * Created by Hayk on 22/02/2018.
 */

public class DialogManager {

    private static DialogManager _instance;

    private DialogManager() {
    }

    public static DialogManager getInstance() {
        if (_instance == null) {
            _instance = new DialogManager();
        }
        return _instance;
    }

    public AlertDialog showServerInfoDialog(Activity context, View.OnClickListener onSubmit) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_server_info, null);
        dialog.setView(view);
        TextView tvIp = view.findViewById(R.id.et_ip);
        TextView tvPort = view.findViewById(R.id.et_port);
        tvIp.setText(SharedPreferencesManager.getInstance().getServerIP(context));
        tvPort.setText(SharedPreferencesManager.getInstance().getServerPort(context));
        view.findViewById(R.id.btnSave).setOnClickListener(onSubmit);
        dialog.show();
        return dialog;
    }
}
