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
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.Bind;
import com.holocron.etal.R;
import com.evo.eventus.fragment.SessionFilterFragment;
import com.evo.eventus.fragment.SessionsFragment;

public class SessionsActivity extends BaseActivity {

  @Bind(R.id.nav_filter_list) NavigationView mFilterListNavigation;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setDisplayHomeAsUpEnabled(true);

    if (savedInstanceState == null) {
      SessionFilterFragment filterFragment = new SessionFilterFragment();
      getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.fragment_content_session_filter, filterFragment)
          .commit();

      SessionsFragment fragment = new SessionsFragment();
      getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.content_session_list, fragment)
          .commit();
    }

  }

  @Override protected void onStart() {
    super.onStart();
    setNavigationViewCurrent(0);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    switch (id)  {
      case R.id.action_filter_list:
        mDrawerLayout.openDrawer(GravityCompat.END);
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
    switch (menuItem.getItemId()) {
      case R.id.nav_home:
        mDrawerLayout.closeDrawers();
        return true;
      case R.id.nav_my_schedule:
        startActivity(new Intent(this, ScheduleActivity.class));
        return true;
      case R.id.nav_app_about:
        startActivity(new Intent(this, AboutActivity.class));
        return true;
      default:
        return false;
    }
  }
}
