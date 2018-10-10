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
import com.luckynineapps.financialchecker.adapter.SpendingAdapter;
import com.luckynineapps.financialchecker.model.Spending;
import com.luckynineapps.financialchecker.utils.CurrencyEditText;
import com.luckynineapps.financialchecker.utils.LocalizationUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpendingActivity extends AppCompatActivity {

    @BindView(R.id.txt_jumlah_transaksi)
    TextView txtJumlahTransaksi;
    @BindView(R.id.txt_total_biaya)
    TextView txtTotalBiaya;

    @BindView(R.id.ad_bottom)
    AdView bottomAds;

    private RecyclerView mRecyclerView;
    private SpendingAdapter mAdapter;

    List<Spending> listSpending = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spending);
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
        listSpending = new Select().from(Spending.class).orderBy("id ASC").execute();

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_spending);
        mAdapter = new SpendingAdapter(SpendingActivity.this, listSpending);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        long totalSpending = 0;
        for (int i = 0; i< listSpending.size(); i++) {
            Spending mCurrent = listSpending.get(i);
            totalSpending = totalSpending + Long.parseLong(mCurrent.nominal);
        }

        txtJumlahTransaksi.setText(listSpending.size()+" "+getString(R.string.transaksi));
        txtTotalBiaya.setText(CurrencyEditText.currencyFormat(totalSpending));
    }

    @OnClick(R.id.btn_add)
    public void addData(){
        LayoutInflater layoutInflater = LayoutInflater.from(SpendingActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SpendingActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText etKeterangan = (EditText) promptView.findViewById(R.id.et_keterangan);
        final EditText etNominal = (EditText) promptView.findViewById(R.id.et_nominal);

        new CurrencyEditText(etNominal);

        alertDialogBuilder.setCancelable(true)
                .setPositiveButton(getString(R.string.simpan), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Spending spending = new Spending();
                        spending.description = etKeterangan.getText().toString();
                        spending.nominal = etNominal.getText().toString().replace(".", "");
                        spending.save();
                        recreate();

                        listSpending.add(spending);
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
        new AlertDialog.Builder(SpendingActivity.this)
                .setTitle(getString(R.string.yakin_ingin_mereset))
                .setPositiveButton(getString(R.string.ya), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete all data
                        new Delete().from(Spending.class).execute();

                        //init new data
                        listSpending.add(new Spending( "Eating Outside's House", "0"));
                        listSpending.add(new Spending( "Luxurious Buying", "0"));
                        listSpending.add(new Spending( "Picnic", "0"));

                        //set data
                        ActiveAndroid.beginTransaction();
                        try {
                            for (int i = 0; i < listSpending.size(); i++) {
                                Spending item = listSpending.get(i);
                                item.save();
                            }
                            ActiveAndroid.setTransactionSuccessful();
                        }
                        finally {
                            ActiveAndroid.endTransaction();
                        }

                        finish();
                        startActivity(new Intent(SpendingActivity.this, SpendingActivity.class));

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
                Intent i = new Intent(SpendingActivity.this, SpendingMonthActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            }
            case R.id.btn_next : {
                Intent i = new Intent(SpendingActivity.this, PassiveIncomeActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            }
        }
    }

    public void toolbar(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.spending));
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
