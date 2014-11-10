package me.dehoog.trakr.models;

import com.orm.SugarRecord;

/**
 * Created by jordon on 2014-11-09.
 */
public class Account extends SugarRecord<Account> {

    public int id;
    public int number;
    public String name;
    public String description;
    public int branch;
    //public List<Transaction> transactions;

}
