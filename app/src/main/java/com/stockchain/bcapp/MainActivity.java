package com.stockchain.bcapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.stockchain.cosmos.PreferenceManager;
import com.stockchain.cosmos.StockBankInform;
import com.stockchain.cosmos.StockData;
import com.stockchain.cosmos.StockDataDetailInform;
import com.stockchain.cosmos.StockDataInform;
import com.stockchain.cosmos.StockTransaction;
import com.stockchain.cosmos.StockTransactionInform;
import com.stockchain.cosmos.StockTransactionRecordInform;

import java.io.IOException;
import java.util.ArrayList;

interface OnBackPressedListener{
    void onBackPressed();
}

public class MainActivity extends AppCompatActivity {
    Menu menu;
    String username;
    String address;
    StockDataInform inqueryStockDataInform;
    ArrayList<StockTransactionInform> stockTransactionInformList;
    ArrayList<StockDataInform> stockDataList;
    ArrayList<StockTransactionRecordInform> stockTransactionRecordInformList;
    StockBankInform stockBankInform;

    MainHomeFragment mainHomeFragment;
    MainFeedFragment mainFeedFragment;
    MainRankFragment mainRankFragment;
    MainSettingFragment mainSettingFragment;
    MainSearchFragment mainSearchFragment;
    MainStockInqueryFragment mainStockInqueryFragment;
//    MainStockTransactionFragment mainStockTransactionFragment;

    SearchView searchView;
    BottomNavigationView bottomNavigationView;

    RecyclerView searchRecyclerView;
    SearchAdapter searchAdapter;
    RecyclerView mockRecyclerView;
    MockStockTransactionStatusAdapter mockStatusAdapter;
    RecyclerView mockRecordRecyclerView;
    MockStockTransactionRecordAdapter mockRecordAdapter;
    RecyclerView feedRecyclerView;
    FeedAdapter feedAdapter;
    RecyclerView rankRecyclerView;
    RankAdapter rankAdapter;

    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }

    public SearchView getSearchView() {
        return searchView;
    }

    public void setSearchView(SearchView searchView) {
        this.searchView = searchView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager pm = new PreferenceManager();
        username = pm.getString(getApplicationContext(), "username");
        address = pm.getString(getApplicationContext(), "address");

        try {
            StockTransaction stockTransaction = new StockTransaction(getApplicationContext());
            stockTransactionInformList = stockTransaction.getStockTransactionInformList(address);
            stockTransactionRecordInformList = stockTransaction.getStockTransactionRecord(address);
            stockBankInform = stockTransaction.getStockBankInform(stockTransactionInformList, address);

            StockData stockData = new StockData(getApplicationContext());
            stockDataList = stockData.getStockDataList();
        } catch (IOException e) {
        }
        mainHomeFragment = new MainHomeFragment();
        mainFeedFragment = new MainFeedFragment();
        mainRankFragment = new MainRankFragment();
        mainSettingFragment = new MainSettingFragment();
        mainSearchFragment = new MainSearchFragment();
        mainStockInqueryFragment = new MainStockInqueryFragment();
//        mainStockTransactionFragment = new MainStockTransactionFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, mainHomeFragment).addToBackStack(null).commit();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_bottom_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, mainHomeFragment).commit();
                        return true;
                    case R.id.menu_bottom_mock:
                        MainMockFragment mainMockFragment = new MainMockFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, mainMockFragment).commit();
                        return true;
                    case R.id.menu_bottom_feed:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, mainFeedFragment).commit();
                        return true;
                    case R.id.menu_bottom_rank:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, mainRankFragment).commit();
                        return true;
                    case R.id.menu_bottom_setting:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, mainSettingFragment).commit();
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchView = (SearchView)menu.findItem(R.id.menu_search).getActionView();
        searchView.setQueryHint("주식이름입력");
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnSearchClickListener(new onClickSearchView());
        searchView.setOnQueryTextListener(new onQueryTextSearchView(this));

        return true;
    }

    class onClickSearchView implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, mainSearchFragment).addToBackStack(null).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch (curId){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0 ){
            getSupportFragmentManager().popBackStack();
        }
        else{
            super.onBackPressed();
        }
    }
}

interface OnItemClickListener {
    void onItemClick(View v, int position) ;
}