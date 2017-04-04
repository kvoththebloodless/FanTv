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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;


public class FanTvJobService extends JobService {

    public static final String ACTION_SYNC_COMPLETE = "synccomplete";
    JobParameters jobParameters;
    private BroadcastReceiver downloadFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            jobFinished(jobParameters, false);
        }
    };

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        this.jobParameters = jobParameters;
        IntentFilter filter = new IntentFilter(ACTION_SYNC_COMPLETE);
        LocalBroadcastManager.getInstance(this).registerReceiver(downloadFinishedReceiver, filter);
        Context context = getApplicationContext();
        Intent intenttosync = new Intent(context, FanTvSyncIntentService.class);
        intenttosync.putExtra("tag", FanTvUtils.PERIODIC_SYNC);
        context.startService(intenttosync);


        return true;
    }


    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        return true;
    }
}