package com.luckynineapps.financialchecker.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "ActiveIncome")
public class ActiveIncome extends Model {

    @Column(name = "Decription")
    public String description;

    @Column(name = "Nominal")
    public String nominal;

    public ActiveIncome(){

    }

    public ActiveIncome(String description, String nominal) {
        this.description = description;
        this.nominal = nominal;
    }

}