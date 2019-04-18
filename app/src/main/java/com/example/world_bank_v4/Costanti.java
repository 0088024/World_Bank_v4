package com.example.world_bank_v4;

public final class Costanti {

    private Costanti(){}        /*costruttore privato per prevenire che accidentalmente venga
                                   istanziata tale classe*/

    public static final String NOME_APP = "WorldBank: ";
    public static final String API_TOPIC_LIST = "https://api.worldbank.org/v2/topic?format=json";
    public static final String API_COUNTRY_LIST = "https://api.worldbank.org/v2/country/";
    public static final String API_TOPIC = "https://api.worldbank.org/v2/topic/";

    public static final String KEY_JSON_FILE_COUNTRY = "json_file_country";
    public static final String KEY_JSON_FILE_ARGOMENTI = "json_file_argomenti";
    public static final String KEY_JSON_FILE_INDICATORI_PER_ARGOMENTO =
                                            "json_file_indicatori_per_argomento";
    public static final String KEY_JSON_FILE_INDICATORE_PER_PAESE =
                                            "json_file_indicatore_per_paese";

    public static final String PREFERENCES_FILE_PAESI = "Preferences_Paesi";
    public static final String PREFERENCES_FILE_ARGOMENTI = "Preferences_Argomenti";
    public static final String PREFERENCES_FILE_INDICATORI_PER_ARGOMENTO =
                                            "Preferences_Indicatori_per_argomento";
    public static final String PREFERENCES_FILE_INDICATORE_PER_PAESE =
                                            "Preferences_Indicatore_per_Paese";

    public static final String API_COUNTRY_LIST_PER_PAGE_500 =
                                     "https://api.worldbank.org/v2/country?format=json&per_page=500";


    public static final String ID_PAESE_SELEZIONATO = "idPaeseSelezionato";
    ;




}