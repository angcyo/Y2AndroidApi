package com.angcyo.y2androidapi;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.angcyo.y2androidapi.control.AndroidApiMgr;
import com.angcyo.y2androidapi.control.DataPool;
import com.angcyo.y2androidapi.control.SummaryPool;
import com.angcyo.y2androidapi.moudle.AndroidClassLinkBean;
import com.angcyo.y2androidapi.moudle.AndroidSummaryBean;
import com.angcyo.y2androidapi.util.FileUtil;
import com.angcyo.y2androidapi.util.Logger;
import com.angcyo.y2androidapi.view.ExitEvent;
import com.angcyo.y2androidapi.view.fragment.MenuFragment;
import com.angcyo.y2androidapi.view.fragment.ProgressFragment;
import com.angcyo.y2androidapi.view.fragment.SparseExitFragment;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.rootView)
    CoordinatorLayout rootView;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.tabs)
    TabLayout mTabLayout;

    @Bind(R.id.recyclerView)
    RecyclerView mSummaryRecyclerView;

    @Bind(R.id.refresher)
    SwipeRefreshLayout mRefreshLayout;

    private ActionBarDrawerToggle mDrawerToggle;
    private SearchView searchView;

    private ProgressFragment progressFragment;
    private MenuFragment mMenuFragment;
    private SparseExitFragment sparseExitFragment;

    private SummaryAdapter summaryAdapter;

    String url = "http://android.xsoftlab.net/reference/packages.html";
    private LinearLayoutManager mSummaryLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initWindow();
        initToolbar();
        initDrawerView();
        initMenuFragment();
        EventBus.getDefault().register(this);


        mSummaryLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        mSummaryRecyclerView.setLayoutManager(mSummaryLayoutManager);
        summaryAdapter = new SummaryAdapter();
        mSummaryRecyclerView.setAdapter(summaryAdapter);

        mRefreshLayout.setColorSchemeResources(R.color.green);
        mRefreshLayout.setOnRefreshListener(this);

//        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//当显示不下的时候,Tab可以滚动

        inflateData(url);
    }

    private void inflateData(String url) {
        Logger.e("请求url-->" + url);
        this.url = url;
        AndroidApiMgr.with(this).load(url)
                .onPreGo(new AndroidApiMgr.OnPreGo() {
                    @Override
                    public void onPreGo(String url) {
                        mRefreshLayout.setRefreshing(true);
                        showProgressFragment("解析数据中...");
                    }
                })
                .onGetSucceed(new AndroidApiMgr.OnGetSucceedListener() {
                    @Override
                    public void onSucceed(DataPool dataPool, SummaryPool summaryPool) {
                        if (mMenuFragment != null) {
                            mMenuFragment.setData(dataPool);
                        }
                        initTabLayout(summaryPool.getSummaryWrap().getSpan());
                        mToolbar.setTitle(summaryPool.title.equalsIgnoreCase("") ? "未知标题" : summaryPool.title);
                        mToolbar.setSubtitle(summaryPool.fullTitle.equalsIgnoreCase("") ? null : summaryPool.fullTitle);
                        if (summaryAdapter != null) {
                            summaryAdapter.setData(summaryPool, summaryPool.getSummaryWrap().getSpanDescr());
                        }

                        hideProgressFragment();
                        mRefreshLayout.setRefreshing(false);
                        showSnackbar("解析成功");
                    }
                })
                .onGetFailed(new AndroidApiMgr.OnGetFailedListener() {
                    @Override
                    public void onFailed() {
                        if (progressFragment != null) {
                            progressFragment.dismiss();
                        }
                        mRefreshLayout.setRefreshing(false);
                        showSnackbar("解析失败");
                    }
                })
                .go();
    }

    private void showSnackbar(String msg) {
        Snackbar snackbar = Snackbar.make(rootView, msg, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundResource(R.color.green);
        snackbar.show();
    }

    private void initTabLayout(List<String> datas) {
        if (datas == null || datas.size() == 0) {
            mTabLayout.setVisibility(View.GONE);
            return;
        } else {
            mTabLayout.setVisibility(View.VISIBLE);
        }
        mTabLayout.removeAllTabs();

        mTabLayout.addTab(mTabLayout.newTab().setText("All"));
        for (String str : datas) {
            mTabLayout.addTab(mTabLayout.newTab().setText(str));
        }
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                mSummaryLayoutManager.scrollToPosition((mSummaryLayoutManager.getItemCount() - 1) < 0 ? 0 : (mSummaryLayoutManager.getItemCount() - 1));
                mTabLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSummaryLayoutManager.scrollToPosition(tab.getText().toString().equalsIgnoreCase("all") ?
                                0 : SummaryPool.getPositionWithSpan(summaryAdapter.getSummaryPool(), tab.getText().toString()));
                    }
                }, 100);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initMenuFragment() {
        mMenuFragment = new MenuFragment();
        mMenuFragment.setOnMenuClick(new MenuFragment.OnMenuItemClickListener() {
            @Override
            public void onItemClick(View parentV, int position, Object values) {
                AndroidClassLinkBean link = (AndroidClassLinkBean) values;
                mDrawerLayout.closeDrawer(GravityCompat.START);
                inflateData(link.getLink());
            }
        });
        replaceFragment(R.id.menu_layout, mMenuFragment);
    }

    private void replaceFragment(@IdRes int layout, Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(layout, fragment).commit();
    }

    private void addFragment(@IdRes int layout, Fragment fragment) {
        getSupportFragmentManager().beginTransaction().add(layout, fragment).commit();
    }

    protected void initToolbar() {
        if (mToolbar != null)
            setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.app_name);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.action_bar_title_color));
        mToolbar.collapseActionView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    private void initDrawerView() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, 0, 0) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
//                mToolbar.setTitle("open");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
//                mToolbar.setTitle("close");
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
//        设置抽屉卷帘颜色,可以理解为剩余空间的颜色
//        mDrawerLayout.setScrimColor(getResources().getColor(R.color.action_bar_title_color));
    }

    @TargetApi(19)
    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//导航栏
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setTintResource(R.color.dark_green);//设置状态栏颜色
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintResource(R.color.dark_green);//设置导航栏颜色
            tintManager.setNavigationBarTintEnabled(false);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        //searchItem.expandActionView();
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        ComponentName componentName = getComponentName();

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(componentName));
        searchView.setQueryHint("搜索内容");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.e("搜索", s);
                return true;
            }
        });
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Log.e("menu expand", "展开");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.e("menu collapse", "关闭");
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.setting:
                Log.e("menu selected", "setting");

                return true;
            case R.id.delete_cache:
                deleteCache();

                return true;
            case R.id.about:
                Log.e("menu selected", "about");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showProgressFragment(String msg) {
        progressFragment = new ProgressFragment();
        Bundle arg = new Bundle();
        arg.putString("msg", msg);
        progressFragment.setArguments(arg);
        progressFragment.show(getSupportFragmentManager(), "progress_fragment");
    }

    private void hideProgressFragment() {
        if (progressFragment != null) {
            progressFragment.dismiss();
            progressFragment = null;
        }
    }

    private void deleteCache() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgressFragment("正在清理缓存...");
            }

            @Override
            protected Void doInBackground(Void... params) {
                FileUtil.cleanAppChache(FileUtil.getAppCachePath(MainActivity.this));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                hideProgressFragment();
                showSnackbar("清理完成");
            }
        }.execute();
    }

    @Override
    public void onRefresh() {
        Log.e("MainActivity", "刷新");
        inflateData(this.url);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            if (mMenuFragment != null && mMenuFragment.isClassifyShow()) {
                mMenuFragment.hideClassifyView();
                return;
            }
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if (sparseExitFragment == null || getSupportFragmentManager().findFragmentByTag("exit_fragment") == null) {
            sparseExitFragment = new SparseExitFragment();
            sparseExitFragment.show(getSupportFragmentManager(), "exit_fragment");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(ExitEvent event) {
        super.onBackPressed();
    }

    private class SummaryAdapter extends RecyclerView.Adapter<SimplerViewHolder> {
        private List<AndroidSummaryBean> datas;
        private SummaryPool summaryPool;

        @Override
        public SimplerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.summary_item, parent, false);
            return new SimplerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimplerViewHolder holder, final int position) {
            holder.txType.setText(datas.get(position).getType());
            holder.txLink.setText(datas.get(position).getLink());
            holder.txDescr.setText(datas.get(position).getDescr());

            for (Integer i : summaryPool.getSummaryWrap().getSpanStartPosition()) {
                if (i == position) {
                    holder.dividerView.setVisibility(View.VISIBLE);
                } else {
                    holder.dividerView.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return datas == null ? 0 : datas.size();
        }

        public void setData(SummaryPool summaryPool, List<AndroidSummaryBean> data) {
            this.datas = data;
            this.summaryPool = summaryPool;
            notifyDataSetChanged();
        }

        public SummaryPool getSummaryPool() {
            return summaryPool;
        }
    }


    public class SimplerViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.summary_type)
        TextView txType;
        @Bind(R.id.summary_link)
        TextView txLink;
        @Bind(R.id.summary_descr)
        TextView txDescr;

        @Bind(R.id.summary_divider)
        View dividerView;

        public SimplerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
