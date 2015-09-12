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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import com.holocron.etal.R;
import com.evo.eventus.activity.SessionDetailActivity;
import com.evo.eventus.adapter.ScheduleAdapter;
import com.evo.eventus.entity.Session;
import com.evo.eventus.entity.SessionDate;
import com.evo.eventus.repository.ScheduleRepository;
import com.evo.eventus.repository.ScheduleRepositoryImpl;
import com.evo.eventus.views.DividerItemDecoration;
import java.util.Date;
import org.parceler.Parcels;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ScheduleFragment extends BaseFragment implements ScheduleAdapter.OnClickStarredListener {

  public static final String STARRED_DATE_ARG = "starred_date_arg";

  @Bind(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
  @Bind(R.id.rvList) RecyclerView mScheduleRecyclerView;
  @Bind(R.id.tvEmpty) TextView mEmptyList;
  @Bind(R.id.btnError) Button mErrorBtn;
  @Bind(R.id.conectionError) LinearLayout mConnectionErrorView;


  private ScheduleRepository mScheduleRepository;
  private ScheduleAdapter mScheduleAdapter;
  private Date mDay;

  public static ScheduleFragment newInstance(Date day) {
    Bundle bundle = new Bundle();
    bundle.putSerializable(STARRED_DATE_ARG, day);
    ScheduleFragment fragment = new ScheduleFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_sessions, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (getArguments() != null) {
      mDay = (Date) getArguments().getSerializable(STARRED_DATE_ARG);
    }
    mScheduleRepository = new ScheduleRepositoryImpl(getActivity());
    mEmptyList.setText(R.string.empty_msg_list_schedule);
    setupButtonError();
    setupRecyclerView();
    setupSwipeRefreshLayout();
  }

  @Override public void onStart() {
    super.onStart();
    setupSessionAdapter();
  }

  @Override public void onItemStarredClcik(Session session) {
    Intent intent = new Intent(getActivity(), SessionDetailActivity.class);
    intent.putExtra(SessionDetailActivity.SESSION_ARG, Parcels.wrap(session));
    getActivity().startActivity(intent);
  }

  private void setupButtonError() {
    mErrorBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        setupSessionAdapter();
      }
    });
  }

  private void setupSwipeRefreshLayout() {
    mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
    mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
  }

  private void setupRecyclerView() {
    mScheduleAdapter = new ScheduleAdapter(getActivity(), this, mDay);
    mScheduleRecyclerView.setAdapter(mScheduleAdapter);
    mScheduleRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    mScheduleRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
  }

  private void setupSessionAdapter() {
    mEmptyList.setVisibility(View.GONE);

    mConnectionErrorView.setVisibility(View.GONE);

    setEnableSwipeRefreshLayout(true);

    mScheduleAdapter.clear();

    mScheduleRepository.starredList()
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .filter(new Func1<Session, Boolean>() {
          @Override public Boolean call(Session session) {
            return filterByDay(session, mDay);
          }
        })
        .subscribe(new Observer<Session>() {
          @Override public void onCompleted() {
            setEnableSwipeRefreshLayout(false);
            if (mScheduleAdapter.isEmpty()) {
              mEmptyList.setVisibility(View.VISIBLE);
            }
          }

          @Override public void onError(Throwable e) {
            setEnableSwipeRefreshLayout(false);
            mConnectionErrorView.setVisibility(View.VISIBLE);
          }

          @Override public void onNext(Session session) {
            mScheduleAdapter.add(session);
          }
        });
  }

  private boolean filterByDay(Session session, Date day) {
    for (SessionDate sessionDate : session.getDateList()) {
      if (sessionDate.getStart().getDay() == day.getDay()) {
        return true;
      }
    }
    return false;
  }

  private void setEnableSwipeRefreshLayout(boolean refresh) {
    if (mSwipeRefreshLayout != null) {
      mSwipeRefreshLayout.setEnabled(false);
      mSwipeRefreshLayout.setRefreshing(refresh);
    }
  }
}
