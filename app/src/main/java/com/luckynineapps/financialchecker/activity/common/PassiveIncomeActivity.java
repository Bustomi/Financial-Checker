package com.luckynineapps.financialchecker.activity.common;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.luckynineapps.financialchecker.R;
import com.luckynineapps.financialchecker.adapter.PassiveIncomeAdapter;
import com.luckynineapps.financialchecker.model.PassiveIncome;
import com.luckynineapps.financialchecker.utils.CurrencyEditText;
import com.luckynineapps.financialchecker.utils.LocalizationUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PassiveIncomeActivity extends AppCompatActivity {

    @BindView(R.id.txt_jumlah_transaksi)
    TextView txtJumlahTransaksi;
    @BindView(R.id.txt_total_biaya)
    TextView txtTotalBiaya;

    @BindView(R.id.ad_bottom)
    AdView bottomAds;

    private RecyclerView mRecyclerView;
    private PassiveIncomeAdapter mAdapter;

    List<PassiveIncome> listPassiveIncome = new ArrayList<>();

    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passive_income);
        SharedPreferences pref = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        LocalizationUtils.setLocale(pref.getString("language", ""), getBaseContext());
        ButterKnife.bind(this);
        toolbar();
        initAd();
    }

    private void initAd() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        bottomAds.loadAd(adRequest);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.ad_id_interstitial));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Intent i = new Intent(PassiveIncomeActivity.this, SpendingActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        listPassiveIncome = new Select().from(PassiveIncome.class).orderBy("id ASC").execute();

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_passive_income);
        mAdapter = new PassiveIncomeAdapter(PassiveIncomeActivity.this, listPassiveIncome);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        long totalPassiveIncome = 0;
        for (int i = 0; i< listPassiveIncome.size(); i++) {
            PassiveIncome mCurrent = listPassiveIncome.get(i);
            totalPassiveIncome = totalPassiveIncome + Long.parseLong(mCurrent.nominal);
        }

        txtJumlahTransaksi.setText(listPassiveIncome.size()+" "+getString(R.string.transaksi));
        txtTotalBiaya.setText(CurrencyEditText.currencyFormat(totalPassiveIncome));
    }

    @OnClick(R.id.btn_add)
    public void addData(){
        LayoutInflater layoutInflater = LayoutInflater.from(PassiveIncomeActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PassiveIncomeActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText etKeterangan = (EditText) promptView.findViewById(R.id.et_keterangan);
        final EditText etNominal = (EditText) promptView.findViewById(R.id.et_nominal);

        new CurrencyEditText(etNominal);

        alertDialogBuilder.setCancelable(true)
                .setPositiveButton(getString(R.string.simpan), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PassiveIncome passiveIncome = new PassiveIncome();
                        passiveIncome.description = etKeterangan.getText().toString();
                        passiveIncome.nominal = etNominal.getText().toString().replace(".", "");
                        passiveIncome.save();
                        recreate();

                        listPassiveIncome.add(passiveIncome);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(getString(R.string.batal), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @OnClick(R.id.btn_clear)
    public void clearData(){
        new AlertDialog.Builder(PassiveIncomeActivity.this)
                .setTitle(getString(R.string.yakin_ingin_mereset))
                .setPositiveButton(getString(R.string.ya), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete all data
                        new Delete().from(PassiveIncome.class).execute();

                        //init new data
                        listPassiveIncome.add(new PassiveIncome( "House's rent / Kost", "0"));
                        listPassiveIncome.add(new PassiveIncome( "Business", "0"));
                        listPassiveIncome.add(new PassiveIncome( "Deposit / MutualFund", "0"));
                        listPassiveIncome.add(new PassiveIncome( "Book Royalties", "0"));
                        listPassiveIncome.add(new PassiveIncome( "Cassete Royalties", "0"));
                        listPassiveIncome.add(new PassiveIncome( "Royaties' System", "0"));

                        //set data
                        ActiveAndroid.beginTransaction();
                        try {
                            for (int i = 0; i < listPassiveIncome.size(); i++) {
                                PassiveIncome item = listPassiveIncome.get(i);
                                item.save();
                            }
                            ActiveAndroid.setTransactionSuccessful();
                        }
                        finally {
                            ActiveAndroid.endTransaction();
                        }

                        finish();
                        startActivity(new Intent(PassiveIncomeActivity.this, PassiveIncomeActivity.class));

                    }
                })
                .setNegativeButton(getString(R.string.tidak), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false)
                .show();
    }

    @OnClick({R.id.btn_next, R.id.btn_back})
    public void onViewClicked(View v){
        switch (v.getId()){
            case R.id.btn_back : {
                if (interstitialAd != null && interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
                else {
                    Intent i = new Intent(PassiveIncomeActivity.this, SpendingActivity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                }
                break;
            }
            case R.id.btn_next : {
                Intent i = new Intent(PassiveIncomeActivity.this, IncomeActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            }
        }
    }

    public void toolbar(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.passive_income));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }
}
