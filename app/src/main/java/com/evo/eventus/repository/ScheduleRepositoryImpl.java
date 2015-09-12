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

package com.evo.eventus.repository;

import android.content.Context;
import android.support.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.evo.eventus.entity.Session;
import com.evo.eventus.sync.RestAdapterFactory;
import com.evo.eventus.utils.SharedPreferencesUtils;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;

public class ScheduleRepositoryImpl implements ScheduleRepository {

  private final String SESSION_KEY = "session_key";

  private Context mContext;
  private SharedPreferencesUtils mSharedPreferencesUtils;

  private List<Long> mSessions;
  private Gson mGson;

  public ScheduleRepositoryImpl(@NonNull Context context) {
    mSharedPreferencesUtils = new SharedPreferencesUtils(context);
    mContext = context;
    mGson = new Gson();
    mSessions = list();
  }

  @Override public void add(Session session) {
    mSessions.add(session.getId());
    mSharedPreferencesUtils.put(SESSION_KEY, mGson.toJson(mSessions));
  }

  @Override public void remove(Session session) {
    if (mSessions.contains(session.getId())) {
      mSessions.remove(session.getId());
      mSharedPreferencesUtils.put(SESSION_KEY, mGson.toJson(mSessions));
    }
  }

  @Override public boolean isStarred(Session session) {
    return mSessions.contains(session.getId());
  }

  @Override public Observable<Session> starredList() {
    final List<Long> scheduleSessions = list();
    return RestAdapterFactory.getRestApi(mContext)
        .sessions()
        .flatMap(new Func1<List<Session>, Observable<Session>>() {
          @Override public Observable<Session> call(List<Session> sessions) {
            return Observable.from(sessions);
          }
        })
        .filter(new Func1<Session, Boolean>() {
          @Override public Boolean call(Session session) {
            return scheduleSessions.contains(session.getId());
          }
        });
  }

  private List<Long> list() {
    String jsonSessionList = mSharedPreferencesUtils.get(SESSION_KEY);
    if (!jsonSessionList.isEmpty()) {
      Type typeToken = new TypeToken<ArrayList<Long>>() {
      }.getType();
      return mGson.fromJson(jsonSessionList, typeToken);
    }
    return new ArrayList<>();
  }
}
