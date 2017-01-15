package com.karan.twitterwidget.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by stpl on 1/6/2017.
 */

public class TrendsList implements Serializable {
    ArrayList<String> list ;
    public TrendsList(ArrayList<String> list){
        this.list=new ArrayList<>(list);
    }
    public ArrayList<String> getList(){
        return list;
    }
}
