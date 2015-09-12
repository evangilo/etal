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

package com.evo.eventus.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.evo.eventus.entity.Session;
import com.evo.eventus.receiver.SessionAlarmReceiver;
import java.util.Calendar;
import org.parceler.Parcels;

public class SessionAlarmService {

  private Context mContext;
  private AlarmManager mAlarmManager;

  public SessionAlarmService(Context context) {
    mContext = context;
    mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
  }

  public void addAlarm(Session session) {
    final int sessionId = (int) session.getId();
    Intent intent = new Intent(mContext, SessionAlarmReceiver.class);
    intent.putExtra(SessionAlarmReceiver.SESSION_ALARM_ARG, Parcels.wrap(session));
    intent.setAction("session:" + session.getId());

    PendingIntent alarmSender = PendingIntent.getBroadcast(mContext, sessionId, intent, 0);

    Calendar c = Calendar.getInstance();
    c.setTime(session.getDateList().get(0).getStart());
    c.add(Calendar.MINUTE, -30);

    mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), alarmSender);
  }

  public void removeAlarm(Session session) {
    final int id = (int) session.getId();
    Intent intent = new Intent(mContext, SessionAlarmReceiver.class);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id, intent, 0);
    mAlarmManager.cancel(pendingIntent);
  }
}
