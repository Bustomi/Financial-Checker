package com.luckynineapps.financialchecker.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.luckynineapps.financialchecker.R;
import com.luckynineapps.financialchecker.model.ActiveIncome;
import com.luckynineapps.financialchecker.model.PassiveIncome;
import com.luckynineapps.financialchecker.model.Spending;
import com.luckynineapps.financialchecker.model.SpendingMonth;
import com.luckynineapps.financialchecker.utils.CurrencyEditText;
import com.luckynineapps.financialchecker.utils.LocalizationUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResultActivity extends AppCompatActivity {

    @BindView(R.id.img_financial_condition)
    ImageView imageCondition;
    @BindView(R.id.txt_financial_condition)
    TextView txtCondition;
    @BindView(R.id.txt_total_monthly_spending)
    TextView txtMonthlySpending;
    @BindView(R.id.txt_total_spending)
    TextView txtSpending;
    @BindView(R.id.txt_total_passive_income)
    TextView txtPassiveIncome;
    @BindView(R.id.txt_total_active_income)
    TextView txtActiveIncome;

    @BindView(R.id.ad_top)
    AdView topAds;
    @BindView(R.id.ad_bottom)
    AdView bottomAds;
    private InterstitialAd interstitialAd;

    int imageSource;

    List<SpendingMonth> listSpendingMonth = new ArrayList<>();
    List<Spending> listSpending = new ArrayList<>();
    List<PassiveIncome> listPassiveIncome = new ArrayList<>();
    List<ActiveIncome> listActiveIncome = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        SharedPreferences pref = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        LocalizationUtils.setLocale(pref.getString("language", ""), getBaseContext());
        ButterKnife.bind(this);
        toolbar();
        initAd();
    }

    private void initAd() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        topAds.loadAd(adRequest);
        bottomAds.loadAd(adRequest);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.ad_id_interstitial));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        listSpendingMonth = new Select().from(SpendingMonth.class).orderBy("id ASC").execute();
        listSpending = new Select().from(Spending.class).orderBy("id ASC").execute();
        listPassiveIncome = new Select().from(PassiveIncome.class).orderBy("id ASC").execute();
        listActiveIncome = new Select().from(ActiveIncome.class).orderBy("id ASC").execute();

        int i;
        long totalSpendingMonth = 0;
        for (i=0; i<listSpendingMonth.size(); i++) {
            SpendingMonth mCurrent = listSpendingMonth.get(i);
            totalSpendingMonth = totalSpendingMonth + Long.parseLong(mCurrent.nominal);
        }
        long totalSpending = 0;
        for (i=0; i<listSpending.size(); i++) {
            Spending mCurrent = listSpending.get(i);
            totalSpending = totalSpending + Long.parseLong(mCurrent.nominal);
        }
        long totalPassive = 0;
        for (i=0; i<listPassiveIncome.size(); i++) {
            PassiveIncome mCurrent = listPassiveIncome.get(i);
            totalPassive = totalPassive + Long.parseLong(mCurrent.nominal);
        }
        long totalActive = 0;
        for (i=0; i<listActiveIncome.size(); i++) {
            ActiveIncome mCurrent = listActiveIncome.get(i);
            totalActive = totalActive + Long.parseLong(mCurrent.nominal);
        }

        txtMonthlySpending.setText(CurrencyEditText.currencyFormat(totalSpendingMonth));
        txtSpending.setText(CurrencyEditText.currencyFormat(totalSpending));
        txtPassiveIncome.setText(CurrencyEditText.currencyFormat(totalPassive));
        txtActiveIncome.setText(CurrencyEditText.currencyFormat(totalActive));

        //pengecekan
        String kondisi = "";
        if(totalPassive==0 && totalActive==0 && totalSpending==0 && totalSpendingMonth==0){
            kondisi = "-";
        }
        else if(totalSpendingMonth + totalSpending > totalPassive + totalActive){
            kondisi = getString(R.string.not_good);
            imageSource = R.drawable.ic_notgood;
        }
        else if(totalSpendingMonth + totalSpending == totalPassive + totalActive){
            kondisi = getString(R.string.good);
            imageSource = R.drawable.ic_good;
        }
        else if(totalSpendingMonth + totalSpending < totalPassive + totalActive){
            kondisi = getString(R.string.very_good);
            imageSource = R.drawable.ic_verygood;
        }
        else if(totalPassive > totalSpendingMonth + totalSpending){
            kondisi = getString(R.string.financial_independent);
            imageSource = R.drawable.ic_fin_ind;
        }

        imageCondition.setImageResource(imageSource);
        txtCondition.setText(kondisi);
    }

    @OnClick({R.id.btn_home_result, R.id.btn_edit_spending_month, R.id.btn_edit_spending, R.id.btn_edit_passive_income, R.id.btn_edit_active_income})
    public void onViewClicked(View v) {
        switch (v.getId()){
            case R.id.btn_home_result : {
                finish();
                break;
            }
            case R.id.btn_edit_spending_month : {
                Intent i = new Intent(this, SpendingMonthActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            }
            case R.id.btn_edit_spending : {
                Intent i = new Intent(this, SpendingActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            }
            case R.id.btn_edit_passive_income : {
                Intent i = new Intent(this, PassiveIncomeActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            }
            case R.id.btn_edit_active_income : {
                Intent i = new Intent(this, IncomeActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            }
        }
    }

    public void toolbar(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.button_result));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (interstitialAd != null && interstitialAd.isLoaded()) {
                interstitialAd.show();
            }
            else {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
