package com.example.world_bank_v4.Model;

public final class Costanti {

    private Costanti(){}        /*costruttore privato per prevenire che accidentalmente venga
                                   istanziata tale classe*/

    public static final String NOME_APP = "WorldBank: ";
    public static final String CHOOSE_BROWSER =
            "Complete the action with one of the following browser: ";
    public static final String WORLDBANK_SITE = "http://www.worldbank.org";
    public static final String IO_ERROR = "Unable to contact: ";
    public static final String MSG_ERRORE_CONNESSIONE = ": Errore connessione internet";


    public static final String API_TOPIC_LIST_FORMAT_JSON =
            "https://api.worldbank.org/v2/topic?format=json";
    public static final String API_COUNTRY_LIST = "https://api.worldbank.org/v2/country/";
    public static final String API_TOPIC_LIST = "https://api.worldbank.org/v2/topic/";
    public static final String API_COUNTRY_LIST_FORMAT_JSON_PER_PAGE_500 =
            "https://api.worldbank.org/v2/country?format=json&per_page=500";

    public static final String NOME_CLASSE_SELEZIONATA = "nome_classe_selezionata";
    public static final String NOME_PAESE_SELEZIONATO = "nome_paese_selezionato";
    public static final String NOME_INDICATORE_SELEZIONATO = "nome_indicatore_selezionato";
    public static final String ID_PAESE_SELEZIONATO = "idPaeseSelezionato";
    public static final String ID_ARGOMENTO_SELEZIONATO = "idArgomentoSelezionato";
    public static final String ID_INDICATORE_SELEZIONATO = "idIndicatoreSelezionato";
    public static final String ID_RECORD_TABELLA = "idRecordTabella";


    public static final String NOME_CHIAVE_FILE_JSON = "nome_chiave_file_json";
    public static final String KEY_JSON_FILE_COUNTRY = "json_file_country";
    public static final String KEY_JSON_FILE_ARGOMENTI = "json_file_argomenti";
    public static final String KEY_JSON_FILE_INDICATORI_PER_ARGOMENTO =
                                            "json_file_indicatori_per_argomento";
    public static final String KEY_JSON_FILE_INDICATORE_PER_PAESE =
                                            "json_file_indicatore_per_paese";
    public static final String KEY_RECORD_ID = "id_record_visualizza_dati";

    public static final String PREFERENCES_FILE_PAESI = "Preferences_Paesi";
    public static final String PREFERENCES_FILE_ARGOMENTI = "Preferences_Argomenti";
    public static final String PREFERENCES_FILE_INDICATORI_PER_ARGOMENTO =
                                            "Preferences_Indicatori_per_argomento";
    public static final String PREFERENCES_FILE_INDICATORE_PER_PAESE =
                                            "Preferences_Indicatore_per_Paese";
    public static final String PREFERENCES_FILE_VISUALIZZA_DATI = "Preferences_Visualizza_Dati";


    public static final String NOME_UNICO_FILE_PNG = "nome_unico_file.png";

    public static final String LANCIATA_DA_PRECEDENTE = "lanciata_da_precedente";

    public static final String ATTIVITÀ_LANCIATA = "attività_lanciata";


    public static final Integer progressBarTime = 10000;

    public static final Integer LISTA_PAESI_CODE = 0;
    public static final Integer LISTA_ARGOMENTI_CODE = 1;
    public static final Integer LISTA_INDICATORI_CODE = 2;
    public static final Integer MOSTRA_GRAFICO_CODE =3;
    public static final Integer MOSTRA_DATABASE_CODE =4;
    public static final Integer NO_DATA =5;
    public static final Integer TIMEOUT =3000;
    public static final Integer RETURN_FROM_NOTIFICATION_ACTIVITY = 99;





}
