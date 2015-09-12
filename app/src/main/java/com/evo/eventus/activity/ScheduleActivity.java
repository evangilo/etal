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

package com.evo.eventus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import butterknife.Bind;
import com.holocron.etal.R;
import com.evo.eventus.adapter.ScheduleFragmentPagerAdapter;
import com.evo.eventus.fragment.ScheduleFragment;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduleActivity extends BaseActivity {

  @Bind(R.id.viewpager) ViewPager mViewPager;
  @Bind(R.id.tabs) TabLayout mTabLayout;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my_schedule);
    setDisplayHomeAsUpEnabled(true);
    setupViewPager();
    setupTabLayout();
  }

  @Override protected void onStart() {
    super.onStart();
    setNavigationViewCurrent(1);
  }

  private void setupViewPager() {
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM");
      Date dayOne = dateFormat.parse("26 ago");
      Date dayTwo = dateFormat.parse("27 ago");
      Date dayThree = dateFormat.parse("28 ago");

      ScheduleFragmentPagerAdapter adapter = new ScheduleFragmentPagerAdapter(getSupportFragmentManager());

      adapter.addFragment(ScheduleFragment.newInstance(dayOne), "26 ago");
      adapter.addFragment(ScheduleFragment.newInstance(dayTwo), "27 ago");
      adapter.addFragment(ScheduleFragment.newInstance(dayThree), "28 ago");
      mViewPager.setAdapter(adapter);
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  private void setupTabLayout() {
    mTabLayout.setupWithViewPager(mViewPager);
  }

  @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
    switch (menuItem.getItemId()) {
      case R.id.nav_home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
      case R.id.nav_my_schedule:
        mDrawerLayout.closeDrawers();
        return true;
      case R.id.nav_app_about:
        startActivity(new Intent(this, AboutActivity.class));
        return true;
      default:
        return false;
    }
  }
}
