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
import android.support.v7.widget.DefaultItemAnimator;
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
import com.evo.eventus.EventusApplication;
import com.holocron.etal.R;
import com.evo.eventus.activity.SessionDetailActivity;
import com.evo.eventus.adapter.SessionAdapter;
import com.evo.eventus.entity.Session;
import com.evo.eventus.entity.filters.SessionFilterComposite;
import com.evo.eventus.entity.filters.SessionItemFilter;
import com.evo.eventus.entity.filters.SessionTypeFilter;
import com.evo.eventus.sync.RestAdapterFactory;
import com.evo.eventus.sync.RestApi;
import com.evo.eventus.views.DividerItemDecoration;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.List;
import org.parceler.Parcels;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SessionsFragment extends BaseFragment implements
    SessionAdapter.OnClickSessionListener {

  @Bind(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
  @Bind(R.id.rvList) RecyclerView mSessionsRecyclerView;
  @Bind(R.id.tvEmpty) TextView mEmptyList;
  @Bind(R.id.btnError) Button mUpdateSessionView;
  @Bind(R.id.conectionError) LinearLayout mConectionErrorView;

  private SessionAdapter mAdapter;

  private RestApi mApi;

  private SessionFilterComposite mSessionTypeFilterComposite;
  private SessionFilterComposite mSessionDateFilterComposite;

  private Bus mBus;

  @Nullable @Override public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_sessions, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mEmptyList.setText(R.string.empty_msg_list_sessions);
    mApi = RestAdapterFactory.getRestApi(getActivity());
    mSessionTypeFilterComposite = new SessionFilterComposite();
    mSessionDateFilterComposite = new SessionFilterComposite();
    mBus = EventusApplication.getBus();
    setupRecyclerView();
    setupSwipeRefreshLayout();
    setupButtonError();
    refreshSessions();
  }

  @Override public void onStart() {
    super.onStart();
    mBus.register(this);
  }

  @Override public void onStop() {
    super.onStop();
    mBus.unregister(this);
  }

  @Override public void onItemSessionClick(Session session) {
    Intent intent = new Intent(getActivity(), SessionDetailActivity.class);
    intent.putExtra(SessionDetailActivity.SESSION_ARG, Parcels.wrap(session));
    getActivity().startActivity(intent);
  }

  // Usando o Bus para notificar.
  @Subscribe
  public void getItemSessionFilter(SessionItemFilter filter) {
    if (filter.getSessionFilter() instanceof SessionTypeFilter) {
      addOrRemoveFilter(mSessionTypeFilterComposite, filter);
    } else {
      addOrRemoveFilter(mSessionDateFilterComposite, filter);
    }
    refreshSessions();
  }

  private void setupButtonError() {
    mUpdateSessionView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        refreshSessions();
      }
    });
  }

  private void addOrRemoveFilter(SessionFilterComposite sessionFilterComposite, SessionItemFilter filter) {
    if (filter.isChecked()) {
      sessionFilterComposite.add(filter.getSessionFilter());
    } else {
      sessionFilterComposite.remove(filter.getSessionFilter());
    }
  }

  private void setupSwipeRefreshLayout() {
    mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
    mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
  }

  private void setupRecyclerView() {
    mAdapter = new SessionAdapter(getActivity(), this);
    mSessionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    mSessionsRecyclerView.setAdapter(mAdapter);
    mSessionsRecyclerView.setHasFixedSize(true);
    mSessionsRecyclerView.setItemAnimator(new DefaultItemAnimator());
    mSessionsRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
  }

  private void refreshSessions() {
    mEmptyList.setVisibility(View.GONE);
    mConectionErrorView.setVisibility(View.GONE);

    mSwipeRefreshLayout.setEnabled(false);
    mSwipeRefreshLayout.setRefreshing(true);
    mAdapter.clear();

    mApi.sessions()
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(new Func1<List<Session>, Observable<Session>>() {
          @Override public Observable<Session> call(List<Session> sessions) {
            return Observable.from(sessions);
          }
        })
        .filter(new Func1<Session, Boolean>() {
          @Override public Boolean call(Session session) {
            boolean type = mSessionTypeFilterComposite.filter(session);
            boolean date = mSessionDateFilterComposite.filter(session);
            return type && date;
          }
        })
        .subscribe(new Observer<Session>() {
          @Override public void onCompleted() {
            mSwipeRefreshLayout.setRefreshing(false);
            if (mAdapter.isEmpty()) {
              mEmptyList.setVisibility(View.VISIBLE);
            }
          }

          @Override public void onError(Throwable e) {
            mSwipeRefreshLayout.setRefreshing(false);
            mConectionErrorView.setVisibility(View.VISIBLE);
          }

          @Override public void onNext(Session session) {
            mSessionsRecyclerView.setVisibility(View.VISIBLE);
            mAdapter.add(session);
          }
        });
  }
}
