package ru.serge.austerlitz.utility;

import android.content.res.Resources;

import java.io.InputStream;
import java.util.HashMap;

import ru.serge.austerlitz.R;

public class LoadResourceTables {
    HashMap<String,InputStream> mRowResourceTables; //set of basic tables

    public LoadResourceTables(Resources resources){
        mRowResourceTables = new HashMap<String, InputStream>();

        //Load tables into hash map array
        mRowResourceTables.put("fire_attack",resources.openRawResource(R.raw.fire_attack));
        mRowResourceTables.put("melee_attack",resources.openRawResource(R.raw.melee_attack));
        mRowResourceTables.put("sip_ratio",resources.openRawResource(R.raw.sip_ratio));
        mRowResourceTables.put("special_events",resources.openRawResource(R.raw.special_events));

    }

    /*
     * Load a config files into tables and save it to global variable
     * */
    public HashMap<String,InputStream> getRowResourceTable(){
        return mRowResourceTables;
    }

    public InputStream getRowResourceTable(String title){
        if(mRowResourceTables != null) {
            return mRowResourceTables.get(title);
        }

        return null;
    }
}
