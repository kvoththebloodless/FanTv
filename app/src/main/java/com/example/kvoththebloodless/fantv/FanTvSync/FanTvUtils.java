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

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.kvoththebloodless.fantv.Misc.Utility;
import com.example.kvoththebloodless.fantv.R;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class FanTvUtils {


    public static final int
            PERIODIC_SYNC = 100,
            FIRST_SYNC = 104,
            SEARCH = 101,
            IMMIDIATE_EP_RETRIEVAL = 102,
            NEWSFEED = 103;
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;
    private static final String FAN_SYNC_TAG = "FANTASYNC";

    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);


        Job syncSunshineJob = dispatcher.newJobBuilder()
                .setService(FanTvJobService.class)
                .setTag(FAN_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        30,
                        60))
                .setReplaceCurrent(true)
                .build();
        Log.i("dispatched", "yesss");
        dispatcher.schedule(syncSunshineJob);
    }

    synchronized public static void initialize(@NonNull final Context context) {
        if (Utility.checkForString(context, R.string.initsync))
            return;
        scheduleFirebaseJobDispatcherSync(context);
        startImmediateSync(context);
        Log.i("immidiatesync", "yay");
        Utility.editPref(context, R.string.initsync, true);


    }

    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, FanTvSyncIntentService.class);
        intentToSyncImmediately.putExtra("tag", FIRST_SYNC);
        context.startService(intentToSyncImmediately);
    }


}