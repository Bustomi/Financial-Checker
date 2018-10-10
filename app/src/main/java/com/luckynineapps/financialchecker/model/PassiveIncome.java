package com.luckynineapps.financialchecker.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "PassiveIncome")
public class PassiveIncome extends Model {

    @Column(name = "Decription")
    public String description;

    @Column(name = "Nominal")
    public String nominal;

    public PassiveIncome(){

    }

    public PassiveIncome(String description, String nominal) {
        this.description = description;
        this.nominal = nominal;
    }

}