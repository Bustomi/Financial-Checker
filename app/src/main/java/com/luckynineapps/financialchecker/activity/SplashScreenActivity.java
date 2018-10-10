package com.luckynineapps.financialchecker.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.luckynineapps.financialchecker.R;
import com.luckynineapps.financialchecker.model.ActiveIncome;
import com.luckynineapps.financialchecker.model.PassiveIncome;
import com.luckynineapps.financialchecker.model.Spending;
import com.luckynineapps.financialchecker.model.SpendingMonth;
import com.luckynineapps.financialchecker.utils.LocalizationUtils;

import java.util.ArrayList;
import java.util.List;

public class SplashScreenActivity extends AppCompatActivity {

    final int SPLASH_TIME_OUT = 2000;

    List<SpendingMonth> listSpendingMonth = new ArrayList<>();
    List<Spending> listSpending = new ArrayList<>();
    List<PassiveIncome> listPassiveIncome = new ArrayList<>();
    List<ActiveIncome> listActiveIncome = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        String lang;
        if(pref != null){
            lang = pref.getString("language", "");
        }
        else{
            lang = "en";
        }
        LocalizationUtils.setLocale(lang, getBaseContext());

        setContentView(R.layout.activity_splash_screen);

        listSpendingMonth = new Select().from(SpendingMonth.class).orderBy("id ASC").execute();
        listSpending = new Select().from(Spending.class).orderBy("id ASC").execute();
        listPassiveIncome = new Select().from(PassiveIncome.class).orderBy("id ASC").execute();
        listActiveIncome = new Select().from(ActiveIncome.class).orderBy("id ASC").execute();

        Log.e("COUNT", ""+listSpendingMonth.size());

        if (listSpendingMonth.size()==0 && listSpending.size()==0 && listPassiveIncome.size()==0 && listActiveIncome.size()==0) {
            initData();
            int i;
            ActiveAndroid.beginTransaction();
            try {
                for (i = 0; i < listSpendingMonth.size(); i++) {
                    SpendingMonth item = listSpendingMonth.get(i);
                    item.save();
                }
                for (i = 0; i < listSpending.size(); i++) {
                    Spending item = listSpending.get(i);
                    item.save();
                }
                for (i = 0; i < listPassiveIncome.size(); i++) {
                    PassiveIncome item = listPassiveIncome.get(i);
                    item.save();
                }
                for (i = 0; i < listActiveIncome.size(); i++) {
                    ActiveIncome item = listActiveIncome.get(i);
                    item.save();
                }
                ActiveAndroid.setTransactionSuccessful();
            }
            finally {
                ActiveAndroid.endTransaction();
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void initData() {
        listSpendingMonth.add(new SpendingMonth( "Eating at home", "0"));
        listSpendingMonth.add(new SpendingMonth( "Electricity, Gas, Water", "0"));
        listSpendingMonth.add(new SpendingMonth( "House's Phone", "0"));
        listSpendingMonth.add(new SpendingMonth( "Phone, mobile phone", "0"));
        listSpendingMonth.add(new SpendingMonth( "School / Children's course", "0"));
        listSpendingMonth.add(new SpendingMonth( "House's Instalment Debt", "0"));
        listSpendingMonth.add(new SpendingMonth( "Transportation's Instalment", "0"));
        listSpendingMonth.add(new SpendingMonth( "Credit Card's Instalment", "0"));
        listSpendingMonth.add(new SpendingMonth( "Insurance", "0"));
        listSpendingMonth.add(new SpendingMonth( "Servant", "0"));
        listSpendingMonth.add(new SpendingMonth( "Car (Maintenance and Gasoline)", "0"));
        listSpendingMonth.add(new SpendingMonth( "Clothes", "0"));

        listSpending.add(new Spending( "Eating Outside's House", "0"));
        listSpending.add(new Spending( "Luxurious Buying", "0"));
        listSpending.add(new Spending( "Picnic", "0"));

        listPassiveIncome.add(new PassiveIncome( "House's rent / Kost", "0"));
        listPassiveIncome.add(new PassiveIncome( "Business", "0"));
        listPassiveIncome.add(new PassiveIncome( "Deposit / MutualFund", "0"));
        listPassiveIncome.add(new PassiveIncome( "Book Royalties", "0"));
        listPassiveIncome.add(new PassiveIncome( "Cassete Royalties", "0"));
        listPassiveIncome.add(new PassiveIncome( "Royaties' System", "0"));

        listActiveIncome.add(new ActiveIncome( "Occupation", "0"));
        listActiveIncome.add(new ActiveIncome( "Trading", "0"));
    }
}