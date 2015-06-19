package com.pioneer.aaron.servermonitor;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.florent37.materialviewpager.HeaderDesign;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.pioneer.aaron.servermonitor.Fragments.CPUFragment;
import com.pioneer.aaron.servermonitor.Fragments.DiskFragment;
import com.pioneer.aaron.servermonitor.Fragments.MemoryFragment;
import com.pioneer.aaron.servermonitor.Fragments.NetworkFragment;
import com.pioneer.aaron.servermonitor.Helper.NetworkStatusUtil;


public class MainActivity extends AppCompatActivity {

    private MaterialViewPager mViewPager;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        checkNetworkState();
    }

    private void init() {
        mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);
        toolbar = mViewPager.getToolbar();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayUseLogoEnabled(false);
                actionBar.setHomeButtonEnabled(true);
            }
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return CPUFragment.newInstance();
                    case 1:
                        return MemoryFragment.newInstance();
                    case 2:
                        return NetworkFragment.newInstance();
                    case 3:
                        return DiskFragment.newInstance();
                }
                return null;
            }

            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return getResources().getString(R.string.CPU_title);
                    case 1:
                        return getResources().getString(R.string.Memory_title);
                    case 2:
                        return getResources().getString(R.string.Network_title);
                    case 3:
                        return getResources().getString(R.string.Disk_title);
                }
                return "";
            }
        });

        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.MaterialViewPagerListener() {
            @Override
            public HeaderDesign getHeaderDesign(int i) {
                switch (i) {
                    case 0:
                        return HeaderDesign.fromColorAndDrawable(
                                R.color.blue,
                                getResources().getDrawable(R.drawable.header_1));
                    case 1:
                        return HeaderDesign.fromColorAndDrawable(
                                R.color.green,
                                getResources().getDrawable(R.drawable.header_2));
                    case 2:
                        return HeaderDesign.fromColorAndDrawable(
                                R.color.cyan,
                                getResources().getDrawable(R.drawable.header_3));
                    case 3:
                        return HeaderDesign.fromColorAndDrawable(
                                R.color.red,
                                getResources().getDrawable(R.drawable.header_4));
                }
                return null;
            }
        });

        mViewPager.getViewPager().setOffscreenPageLimit(4);
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());
        mViewPager.getViewPager().setCurrentItem(0);

    }

    private void checkNetworkState() {
        boolean isAvailable;
        isAvailable = new NetworkStatusUtil(this).isAvailable();
        if (!isAvailable) {
            setNetwork();
        }
    }

    /**
     * if network is unavailable, call this method
     * */
    private void setNetwork() {
        new android.app.AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_launcher)
                .setTitle("网络提示信息")
                .setMessage("网络不可用，请先设置网络")
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = null;
//                        call different Setting by different SDK
                        if (Build.VERSION.SDK_INT > 10) {
                            intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
//                            intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        } else {
                            intent = new Intent();
                            ComponentName componentName = new ComponentName(
                                    "com.android.settings",
                                    "com.android.settings.WirelessSettings"
                            );
                            intent.setComponent(componentName);
                            intent.setAction("android.intent.action.VIEW");
                        }

                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        null
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle("About us")
                    .setMessage("这是一个实时服务器监视的软件，通过 HTTP 获取服务器(Linux)端的运行状态，包含 CPU、内存、网络、磁盘、处理器的状态信息。\n"
                        +"APP 端开发: 冯文瀚\n"+"Server 端开发: 彭璟文")
                    .setPositiveButton("OK", null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mDrawerToggle.syncState();
    }
}
