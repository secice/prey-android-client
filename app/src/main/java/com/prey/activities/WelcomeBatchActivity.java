/*******************************************************************************
 * Created by Carlos Yaconi
 * Copyright 2015 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import android.os.AsyncTask;


import com.prey.PreyAccountData;
import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.PreyUtils;
import com.prey.PreyVerify;
import com.prey.R;
import com.prey.backwardcompatibility.FroyoSupport;
import com.prey.exceptions.PreyException;
import com.prey.net.PreyWebServices;

public class WelcomeBatchActivity extends FragmentActivity {

    private String error = null;

    @Override
    public void onResume() {
        PreyLogger.i("onResume of WelcomeBatchActivity");
        super.onResume();

    }

    @Override
    public void onPause() {
        PreyLogger.i("onPause of WelcomeBatchActivity");
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.welcomebatch);

        menu();
        installBatch();


    }

    public void menu() {
        PreyLogger.i("menu ready:" + PreyConfig.getPreyConfig(this).getProtectReady());

        String email = PreyConfig.getPreyConfig(this).getEmail();
        if (email == null || "".equals(email)) {
            PreyConfig.getPreyConfig(this).setProtectReady(false);
            PreyConfig.getPreyConfig(this).setProtectAccount(false);
            PreyConfig.getPreyConfig(this).setProtectTour(false);
        }
    }

    private void installBatch() {
        error=null;
        PreyConfig config=PreyConfig.getPreyConfig(this);
        new AddDeviceToApiKeyBatch().execute(config.getApiKeyBatch(),config.getEmailBatch(), PreyUtils.getDeviceType(this));
    }

    private class AddDeviceToApiKeyBatch extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... data) {
            try {
                error = null;
                Context ctx=getApplicationContext();

                if(!PreyConfig.getPreyConfig(ctx).isThisDeviceAlreadyRegisteredWithPrey()) {
                    PreyAccountData accountData = PreyWebServices.getInstance().registerNewDeviceWithApiKeyEmail(ctx, data[0], data[1], data[2]);
                    PreyConfig.getPreyConfig(ctx).saveAccount(accountData);
                }

            } catch (PreyException e) {
                error = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (error == null) {
                String message = getString(R.string.device_added_congratulations_text);
                Bundle bundle = new Bundle();
                bundle.putString("message", message);
                PreyConfig.getPreyConfig(WelcomeBatchActivity.this).setCamouflageSet(true);
                Intent intent = new Intent(WelcomeBatchActivity.this, PermissionInformationActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

                finish();
            }
        }
    }


}
