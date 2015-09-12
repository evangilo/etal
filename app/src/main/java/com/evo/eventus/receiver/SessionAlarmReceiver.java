/*
 * Copyright (C) 2015 Evangilo Morais
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evo.eventus.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import com.holocron.etal.R;
import com.evo.eventus.activity.SessionDetailActivity;
import com.evo.eventus.entity.Session;
import org.parceler.Parcels;

public class SessionAlarmReceiver extends BroadcastReceiver {

  public static final String SESSION_ALARM_ARG = "session_alarm_arg";

  @Override public void onReceive(Context context, Intent intent) {
    Session session = Parcels.unwrap(intent.getParcelableExtra(SESSION_ALARM_ARG));

    Intent resultIntent = new Intent(context, SessionDetailActivity.class);
    resultIntent.putExtra(SessionDetailActivity.SESSION_ARG, Parcels.wrap(session));

    PendingIntent resultPendingIntent = TaskStackBuilder.create(context)
        .addParentStack(SessionDetailActivity.class)
        .addNextIntent(resultIntent)
        .getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
        .setSmallIcon(R.drawable.ic_notification)
        .setColor(context.getResources().getColor(R.color.notification_icon_background))
        .setContentTitle(session.getTitle())
        .setContentText(context.getString(R.string.notification_msg))
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setAutoCancel(true)
        .setContentIntent(resultPendingIntent);

    NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.notify((int) session.getId(), builder.build());
  }

}
