package com.luckynineapps.financialchecker.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.luckynineapps.financialchecker.R;
import com.luckynineapps.financialchecker.activity.common.IncomeActivity;
import com.luckynineapps.financialchecker.activity.common.PassiveIncomeActivity;
import com.luckynineapps.financialchecker.activity.common.SpendingActivity;
import com.luckynineapps.financialchecker.activity.common.SpendingMonthActivity;
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

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.cv_active_income)
    CardView cdIncome;
    @BindView(R.id.cv_spending)
    CardView cdSpending;
    @BindView(R.id.cv_month_spending)
    CardView cdMonthSpending;
    @BindView(R.id.cv_passive_income)
    CardView cdPassiveIncome;
    @BindView(R.id.txt_financial_condition)
    TextView txtFinancialCondition;
    @BindView(R.id.txt_financial_total)
    TextView txtFinancialTotal;

    List<SpendingMonth> listSpendingMonth = new ArrayList<>();
    List<Spending> listSpending = new ArrayList<>();
    List<PassiveIncome> listPassiveIncome = new ArrayList<>();
    List<ActiveIncome> listActiveIncome = new ArrayList<>();

    @BindView(R.id.ad_bottom)
    AdView bottomAds;
    private InterstitialAd intersLangIndo;
    private InterstitialAd intersLangEng;
    private InterstitialAd intersAbout;

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        LocalizationUtils.setLocale(pref.getString("language", ""), getBaseContext());

        ButterKnife.bind(this);

        initAd();
        actionClicked();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initListData();
    }

    private void initListData() {
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

        //pengecekan
        String kondisi = "";
        int txtColor = R.color.white;
        if(totalPassive==0 && totalActive==0 && totalSpending==0 && totalSpendingMonth==0){
            kondisi = "-";
        }
        else if(totalSpendingMonth + totalSpending > totalPassive + totalActive){
            kondisi = getString(R.string.not_good);
            txtColor = R.color.red;

        }
        else if(totalSpendingMonth + totalSpending == totalPassive + totalActive){
            kondisi = getString(R.string.good);
            txtColor = R.color.txt_green;
        }
        else if(totalSpendingMonth + totalSpending < totalPassive + totalActive){
            kondisi = getString(R.string.very_good);
            txtColor = R.color.txt_green;
        }
        else if(totalPassive > totalSpendingMonth + totalSpending){
            kondisi = getString(R.string.financial_independent);
        }

        txtFinancialCondition.setText(kondisi);
        txtFinancialCondition.setTextColor(getResources().getColor(txtColor));
        txtFinancialTotal.setText(CurrencyEditText.currencyFormat(totalPassive + totalActive - totalSpendingMonth - totalSpending));
    }

    private void initAd() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        bottomAds.loadAd(adRequest);

        final Intent i = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        intersLangIndo = new InterstitialAd(this);
        intersLangIndo.setAdUnitId(getString(R.string.ad_id_interstitial));
        intersLangIndo.loadAd(new AdRequest.Builder().build());
        intersLangIndo.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                LocalizationUtils.setLocale("in", getBaseContext());
                setLangPref("in");
                startActivity(i);
            }
        });

        intersLangEng = new InterstitialAd(this);
        intersLangEng.setAdUnitId(getString(R.string.ad_id_interstitial));
        intersLangEng.loadAd(new AdRequest.Builder().build());
        intersLangEng.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                LocalizationUtils.setLocale("en", getBaseContext());
                setLangPref("en");
                startActivity(i);
            }
        });

        intersAbout = new InterstitialAd(this);
        intersAbout.setAdUnitId(getString(R.string.ad_id_interstitial));
        intersAbout.loadAd(new AdRequest.Builder().build());
        intersAbout.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
        });
    }

    public void actionClicked(){
        cdIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,IncomeActivity.class);
                startActivity(i);
            }
        });

        cdPassiveIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,PassiveIncomeActivity.class);
                startActivity(i);
            }
        });

        cdSpending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SpendingActivity.class);
                startActivity(i);
            }
        });

        cdMonthSpending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SpendingMonthActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_home, menu);

        return true;
    }

    public void setLangPref(String lang){
        SharedPreferences.Editor editor = getSharedPreferences("setting", MODE_PRIVATE).edit();
        editor.putString("language", lang);
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        switch(item.getItemId())
        {
            case R.id.nav_indonesia:
                if (intersLangIndo != null && intersLangIndo.isLoaded()) {
                    intersLangIndo.show();
                }
                else {
                    LocalizationUtils.setLocale("in", getBaseContext());
                    setLangPref("in");
                    startActivity(i);
                }
                break;
            case R.id.nav_inggris:
                if (intersLangEng != null && intersLangEng.isLoaded()) {
                    intersLangEng.show();
                }
                else {
                    LocalizationUtils.setLocale("en", getBaseContext());
                    setLangPref("en");
                    startActivity(i);
                }
                break;
            case R.id.nav_tentang:
                if (intersAbout != null && intersAbout.isLoaded()) {
                    intersAbout.show();
                }
                else {
                    startActivity(new Intent(this, AboutActivity.class));
                }
                break;
            case R.id.nav_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyText = "";
                if(pref.getString("language", "").equalsIgnoreCase("en")){
                    shareBodyText = "Do you want to check your private financial security ? \n" +
                            "Let's download this app right now : \n" +
                            "http://play.google.com/store/apps/details?id="+ getApplicationContext().getPackageName();
                }
                else{
                    shareBodyText = "Mau periksa seberapa sehat Keuangan Pribadi anda ?\n" +
                            "Silahkan download aplikasi ini sekarang juga : \n" +
                            "http://play.google.com/store/apps/details?id="+ getApplicationContext().getPackageName();
                }

                sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Financial Checker");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Financial Checker"));
                break;
            case R.id.nav_suka:
                Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                }
                catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                }
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
