package com.example.world_bank_v4;

public class Grafico {

    private String date = null;
    private double value = 0;
    private double decimal = 0;

    public Grafico(){}                /*serve per il Gson*/

    public Grafico(String date, double value, double decimal){

        this.date = date;
        this.value = value;
        this.decimal = decimal;
    }

    @Override
    public String toString() {
        return " [date = " + date + " value = " + String.valueOf(value) +
                                " decimal = " + String.valueOf(decimal) + " ]";
    }


}
