
package com.parse.starter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

  public static final String TAG="myActivity";
  private Toolbar toolbar;
  private ViewPager pager;
  private ViewPagerAdapter adapter;
  private SlidingTabLayout tabs;
  private CharSequence Titles[]={"Home","Discovery","Photo","Activity","Profile"};
  private static int[] ICONS = new int[]{
          R.drawable.ic_action_send_now,
          R.drawable.ic_action_not_secure,
          R.drawable.ic_action_mouse,
          R.drawable.ic_action_dock,
          R.drawable.ic_action_gamepad
  };
  private int Numboftabs =5;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
      toolbar = (Toolbar)findViewById(R.id.main_bar);
      setSupportActionBar(toolbar);
      toolbar.setTitleTextColor(Color.WHITE);
      adapter =  new ViewPagerAdapter(getSupportFragmentManager(),ICONS,Numboftabs);
      pager = (ViewPager) findViewById(R.id.pager);
      pager.setAdapter(adapter);
      tabs = (SlidingTabLayout) findViewById(R.id.tabs);
      tabs.setDistributeEvenly(true);
      tabs.setViewPager(pager);
  }
}
