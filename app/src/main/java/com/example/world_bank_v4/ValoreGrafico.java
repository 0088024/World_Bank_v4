package com.example.world_bank_v4;


public class ValoreGrafico {

    private String date = null;
    private Float value = null;
    private double decimal = 0;


    public ValoreGrafico(){}                /*serve per il Gson*/


    public ValoreGrafico(String date, Float value, double decimal){

        this.date = date;
        this.value = value;
        this.decimal = decimal;
    }



    public void resetValue(){
        this.value = Float.valueOf(0) ;
    }


    @Override
    public String toString() {
        return  "[Elemento grafico: date " + date + " value " + value + " ]";
    }


    public String getDate(){
        return date;
    }

    public Float getvalue(){
        return value;
    }




}
