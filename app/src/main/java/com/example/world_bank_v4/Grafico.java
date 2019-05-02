package com.example.world_bank_v4;

public class Grafico {

    /*private Indicatore indicator;
    private Paese country;*/
    private String date = null;
    private Float value = null;
    private double decimal = 0;


    public Grafico(){}                /*serve per il Gson*/


    public Grafico(String date, Float value, double decimal/*, Indicatore indicator,
                   Paese country*/){
        /*this.indicator = indicator;
        this.country = country;*/
        this.date = date;
        this.value = value;
        this.decimal = decimal;
    }



    public void resetValue(){
        this.value = Float.valueOf(0) ;
    }


    @Override
    public String toString() {
        return /*" [indicator = " + indicator + " country " + country + " date = " + date +
                " value = " + String.valueOf(value) +
                                " decimal = " + String.valueOf(decimal) + " ]";*/
        "[Elemento grafico: date " + date + " value " + value + " ]";
    }


    public String getDate(){
        return date;
    }

    public Float getvalue(){
        return value;
    }


   /* public Indicatore getIndicator() { return indicator; }

    public Paese getCountry(){
        return country;
    }*/



}
