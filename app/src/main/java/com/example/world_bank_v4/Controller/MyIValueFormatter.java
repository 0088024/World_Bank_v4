package com.example.world_bank_v4.Controller;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;


public class MyIValueFormatter implements IValueFormatter {

    private boolean writed;
    DataSet dataSet;

    public  MyIValueFormatter(DataSet dataSet){
        writed = false;
        this.dataSet = dataSet;
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler
            viewPortHandler) {
            /*per evitare confusione grafica, il valore max o min lo disegno 1 sola volta*/
            if(writed) return "";
            /*disegna solo il valore massimo e/o il valore minimo. Entrambi 1 sola volta*/
            if ((value == dataSet.getYMax() || value == dataSet.getYMin())
                    && value != 0) {
                    writed = true;
                    return (String.valueOf(value));
            }

            return "";
    }


}
