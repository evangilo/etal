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

package com.evo.eventus.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import com.evo.eventus.EventusApplication;
import com.holocron.etal.R;
import com.evo.eventus.adapter.SessionFilterAdapter;
import com.evo.eventus.entity.filters.SessionFilter;
import com.evo.eventus.entity.filters.SessionItemFilter;
import com.evo.eventus.entity.filters.SessionDateFilter;
import com.evo.eventus.entity.filters.SessionTypeFilter;
import com.squareup.otto.Bus;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.evo.eventus.entity.SessionType.ROUNDTABLE;
import static com.evo.eventus.entity.SessionType.TALK;
import static com.evo.eventus.entity.SessionType.WORKSHOP;

public class SessionFilterFragment extends BaseFragment implements
    SessionFilterAdapter.OnSessionFilterListener {

  @Bind(R.id.rvFilter) RecyclerView mFilterRecyclerView;

  private Bus mBus;

  @Nullable @Override public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_session_filter, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupFilter();
    mBus = EventusApplication.getBus();
  }

  @Override public void onChecked(SessionItemFilter filter) {
    mBus.post(filter);
  }

  private void setupFilter() {
    SessionFilterAdapter adapter = new SessionFilterAdapter(getActivity(), filters(), this);
    mFilterRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    mFilterRecyclerView.setAdapter(adapter);
  }

  private List<SessionItemFilter> filters() {
    List<SessionItemFilter> filters = new ArrayList<>();
    filters.addAll(getSessionTypeFilters());
    filters.addAll(getDateFilters());
    return filters;
  }

  private List<SessionItemFilter> getSessionTypeFilters() {
    SessionTypeFilter talkFilter = new SessionTypeFilter(TALK);
    SessionTypeFilter workshopFilter = new SessionTypeFilter(WORKSHOP);
    SessionTypeFilter roundtableFilter = new SessionTypeFilter(ROUNDTABLE);

    SessionItemFilter talk = new SessionItemFilter("Palestra", talkFilter);
    SessionItemFilter wokshop = new SessionItemFilter("Minicurso", workshopFilter);
    SessionItemFilter roundtable = new SessionItemFilter("Mesa-redonda", roundtableFilter);

    return Arrays.asList(talk, wokshop, roundtable);
  }

  private List<SessionItemFilter> getDateFilters() {
    String[] dates = {"26 de ago", "27 de ago", "28 de ago"};
    String format ="d 'de' MMM";
    List<SessionItemFilter> filters = new ArrayList<>();
    for (String date: dates) {
      SessionFilter filter = new SessionDateFilter(getDate(date, format));
      filters.add(new SessionItemFilter(date, filter));
    }
    return filters;
  }

  private Date getDate(String date, String format) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(format);
    try {
      return dateFormat.parse(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

}
