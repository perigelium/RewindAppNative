package developer.alexangan.ru.rewindapp.Models;

import android.util.ArrayMap;

public class ClientsSearchCriteries
{
    private ArrayMap <String, String> mapSearchCriteries;

    public  ClientsSearchCriteries()
    {
        mapSearchCriteries = new ArrayMap<>();

        mapSearchCriteries.put("ragione_sociale", "RAGIONE SOCIALE");
        mapSearchCriteries.put("partita_iva", "PARTITA IVA");
        mapSearchCriteries.put("phone", "TELEFONO");
        mapSearchCriteries.put("location", "LOCALITÀ");
        mapSearchCriteries.put("mobile_phone", "CELULLARE");
        mapSearchCriteries.put("province", "PROVINCIA");
    }

    public ArrayMap<String, String> getMapSearchCriteries()
    {
        return mapSearchCriteries;
    }
}
