package com.example.world_bank_v4;

public class Grafico {

    private String date = null;
    private Float value = null;
    private double decimal = 0;

    public Grafico(){}                /*serve per il Gson*/

    public Grafico(String date, Float value, double decimal){

        this.date = date;
        this.value = value;
        this.decimal = decimal;
    }

    public void resetValue(){
        this.value = Float.valueOf(0) ;
    }

    @Override
    public String toString() {
        return " [date = " + date + " value = " + String.valueOf(value) +
                                " decimal = " + String.valueOf(decimal) + " ]";
    }

    public String getDate(){
        return date;
    }

    public Float getvalue(){
        return value;
    }


}
