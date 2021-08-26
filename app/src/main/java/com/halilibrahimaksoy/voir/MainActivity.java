package com.halilibrahimaksoy.voir;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.halilibrahimaksoy.voir.Activity.LoginActivity;
import com.halilibrahimaksoy.voir.Adapter.ViewPagerAdapter;
import com.halilibrahimaksoy.voir.Fragment.Global;
import com.halilibrahimaksoy.voir.Fragment.HeadLine;
import com.halilibrahimaksoy.voir.Fragment.Trending;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private Toolbar tbMain;
    private TabLayout tabLayout;
    private NonSwipeableViewPager viewPager;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseUser loginUser = ParseUser.getCurrentUser();
        if (loginUser == null)
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));

        tbMain = (Toolbar) findViewById(R.id.tbMain);

        tbMain.setLogo(R.mipmap.ic_launcher);

        setSupportActionBar(tbMain);


        viewPager = (NonSwipeableViewPager) findViewById(R.id.viewpager);
        handleIntent(getIntent());

        if (query != null) {
            tbMain.setTitle(query);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else
            tbMain.setTitle("Voir");

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                if (query != null)
                    tbMain.setTitle(query);
                else
                    switch (tab.getPosition()) {
                        case 0:
                            tbMain.setTitle(" Global Videos");
                            break;
                        case 1:
                            tbMain.setTitle(" HeadLine Videos");
                            break;
                        case 2:
                            tbMain.setTitle("User Trend List");
                            break;
                        case 3:
                            tbMain.setTitle("Profile");
                            break;
                        default:
                            tbMain.setTitle("Voir");
                    }
            }
        });


    }

    private void setupTabIcons() {
        // tabLayout.setBackgroundResource(R.drawable.tablayout_color);
        tabLayout.getTabAt(0).setIcon(R.drawable.global_icon_selector);
        tabLayout.getTabAt(1).setIcon(R.drawable.headline_icon_selector);
        tabLayout.getTabAt(2).setIcon(R.drawable.trending_icon_selector);
        //   tabLayout.getTabAt(3).setIcon(R.drawable.account_icon_selector);


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new Global(), "");
        adapter.addFrag(new HeadLine(), "");
        adapter.addFrag(new Trending(), "");
        viewPager.setAdapter(adapter);
    }

    private void setupViewPager(ViewPager viewPager, String query) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new Global(query), "");
        adapter.addFrag(new HeadLine(query), "");
        adapter.addFrag(new Trending(query), "");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }


    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //      Toast.makeText(getApplicationContext(), query, Toast.LENGTH_LONG).show();
            this.query = query;
            setupViewPager(viewPager, query);
        } else {
            setupViewPager(viewPager);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_log_out:
                LogOut();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void LogOut() {
        ParseUser.logOut();
        System.gc();
        finish();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));

    }
}
