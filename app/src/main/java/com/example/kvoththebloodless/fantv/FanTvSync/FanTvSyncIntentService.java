/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.kvoththebloodless.fantv.FanTvSync;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import com.example.kvoththebloodless.fantv.Misc.Utility;
import com.example.kvoththebloodless.fantv.PoplarWidgetProvider;
import com.example.kvoththebloodless.fantv.R;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class FanTvSyncIntentService extends IntentService {

    public FanTvSyncIntentService() {
        super("FanTvSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!Utility.isConnected(getApplicationContext())) {
            Toast.makeText(this, R.string.internet_loss, Toast.LENGTH_SHORT).show();
            return;
        }
        FanTvSyncTask.startSync(this, intent.getExtras().getInt("tag"));

        Intent dataUpdatedIntent = new Intent(PoplarWidgetProvider.ACTION_DATA_UPDATED)
                .setPackage(getPackageName());
        sendBroadcast(dataUpdatedIntent);

    }

    @Override
    public void onDestroy() {
        Intent finishedIntent = new Intent(FanTvJobService.ACTION_SYNC_COMPLETE);
        super.onDestroy();
    }
}