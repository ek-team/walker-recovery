package com.pharos.walker.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.widget.Toast;

public  class InstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (intent != null) { //安装的广播
            final int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS,
                    PackageInstaller.STATUS_FAILURE);
            if (status == PackageInstaller.STATUS_SUCCESS) {
                // success
                Toast.makeText(context,"APP Install Success!",Toast.LENGTH_SHORT).show();
                // InstallAPP.getInstance().sendInstallSucces();
            } else {
                String msg = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE);
                Toast.makeText(context,"Install FAILURE status_massage",Toast.LENGTH_SHORT).show();
                //InstallAPP.getInstance().sendFailure(msg);
            }
        }
    }
}
