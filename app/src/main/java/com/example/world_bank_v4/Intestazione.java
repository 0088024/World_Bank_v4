package com.example.world_bank_v4;

public class Intestazione {

    private int page;
    private int pages;
    private int per_page;
    private int total;
    private String sourceid;
    private String lastupdated;


    public Intestazione(){}

    public Intestazione(int page, int pages, int per_page, String sourceid,
                        String lastupdated){
        this.page = page;
        this.pages = pages;
        this.per_page = per_page;
        this.total = total;
        this.sourceid = sourceid;
        this.lastupdated = lastupdated;

    }


    @Override
    public String toString(){
        return "[Intestazione: page = " + page + " pages = " + pages + " per_page = " + per_page
                + " total = " + total + " sourceid = " + sourceid + " lastupdated = " +
                lastupdated + "]";
    }


    public String getLastUpdated(){
        return lastupdated;
    }

}
