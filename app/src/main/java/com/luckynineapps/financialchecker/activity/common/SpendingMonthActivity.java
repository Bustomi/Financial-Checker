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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.luckynineapps.financialchecker.R;
import com.luckynineapps.financialchecker.adapter.MonthlySpendingAdapter;
import com.luckynineapps.financialchecker.model.SpendingMonth;
import com.luckynineapps.financialchecker.utils.CurrencyEditText;
import com.luckynineapps.financialchecker.utils.LocalizationUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpendingMonthActivity extends AppCompatActivity {

    @BindView(R.id.txt_jumlah_transaksi)
    TextView txtJumlahTransaksi;
    @BindView(R.id.txt_total_biaya)
    TextView txtTotalBiaya;

    @BindView(R.id.ad_bottom)
    AdView bottomAds;

    private RecyclerView mRecyclerView;
    private MonthlySpendingAdapter mAdapter;

    List<SpendingMonth> listSpendingMonth = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spending_month);
        SharedPreferences pref = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        LocalizationUtils.setLocale(pref.getString("language", ""), getBaseContext());
        ButterKnife.bind(this);
        toolbar();

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        bottomAds.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listSpendingMonth = new Select().from(SpendingMonth.class).orderBy("id ASC").execute();

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_month_spending);
        mAdapter = new MonthlySpendingAdapter(SpendingMonthActivity.this, listSpendingMonth);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        long totalSpendingMonth = 0;
        for (int i=0; i<listSpendingMonth.size(); i++) {
            SpendingMonth mCurrent = listSpendingMonth.get(i);
            totalSpendingMonth = totalSpendingMonth + Long.parseLong(mCurrent.nominal);
        }

        txtJumlahTransaksi.setText(listSpendingMonth.size()+" "+getString(R.string.transaksi));
        txtTotalBiaya.setText(CurrencyEditText.currencyFormat(totalSpendingMonth));
    }

    @OnClick(R.id.btn_add)
    public void addData(){
        LayoutInflater layoutInflater = LayoutInflater.from(SpendingMonthActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SpendingMonthActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText etKeterangan = (EditText) promptView.findViewById(R.id.et_keterangan);
        final EditText etNominal = (EditText) promptView.findViewById(R.id.et_nominal);

        new CurrencyEditText(etNominal);

        alertDialogBuilder.setCancelable(true)
                .setPositiveButton(getString(R.string.simpan), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SpendingMonth spendingMonth = new SpendingMonth();
                        spendingMonth.description = etKeterangan.getText().toString();
                        spendingMonth.nominal = etNominal.getText().toString().replace(".", "");
                        spendingMonth.save();
                        recreate();

                        listSpendingMonth.add(spendingMonth);
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
        new AlertDialog.Builder(SpendingMonthActivity.this)
                .setTitle(getString(R.string.yakin_ingin_mereset))
                .setPositiveButton(getString(R.string.ya), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete all data
                        new Delete().from(SpendingMonth.class).execute();

                        //init new data
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

                        //set data
                        ActiveAndroid.beginTransaction();
                        try {
                            for (int i = 0; i < listSpendingMonth.size(); i++) {
                                SpendingMonth item = listSpendingMonth.get(i);
                                item.save();
                            }
                            ActiveAndroid.setTransactionSuccessful();
                        }
                        finally {
                            ActiveAndroid.endTransaction();
                        }

                        finish();
                        startActivity(new Intent(SpendingMonthActivity.this, SpendingMonthActivity.class));

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
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            }
            case R.id.btn_next : {
                Intent i = new Intent(SpendingMonthActivity.this, SpendingActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            }
        }
    }

    public void toolbar(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.monthly_spending));
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
