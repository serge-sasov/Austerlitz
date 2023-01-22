package ru.serge.austerlitz.game;

import android.content.res.Resources;

import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import ru.serge.austerlitz.counters.UnitView;
import ru.serge.austerlitz.tables.FireTable;
import ru.serge.austerlitz.tables.MeleeTable;
import ru.serge.austerlitz.tables.SipRatio;
import ru.serge.austerlitz.utility.LoadResourceTables;
import ru.serge.austerlitz.utility.LoadTable;

//aggregator for the overall application data
public class Play {
    private HashMap<String, InputStream> mRowResourceTables;
    private FireTable mFireTable;
    private MeleeTable mMeleeTable;
    private SipRatio mSipRatio;

    //temp object to handle moving the units into activation field
    private UnitView mMovingUnitView;

    public Play(Resources resources){
        LoadResourceTables loadResTables = new LoadResourceTables(resources);
        mRowResourceTables = loadResTables.getRowResourceTable();

        LoadTable rawFireTable = null,
                rawRatioTbl = null,
                rawMeleeTable = null;
        try {
            //prepare fire resolution table
            rawFireTable = new LoadTable(loadResTables.getRowResourceTable("fire_attack"));
            mFireTable = new FireTable(rawFireTable);

            //prepare sip resolution table
            rawRatioTbl = new LoadTable(loadResTables.getRowResourceTable("sip_ratio"));
            mSipRatio = new SipRatio(rawRatioTbl);

            //prepare melee resolution table
            rawMeleeTable = new LoadTable(loadResTables.getRowResourceTable("melee_attack"));
            mMeleeTable = new MeleeTable(rawMeleeTable);

        } catch (CsvException | IOException e) {
            e.printStackTrace();
        }
    }

    public void movingUnit(UnitView unitView){
        mMovingUnitView = unitView;
    }

    public UnitView movingUnit(){
        return mMovingUnitView;
    }

    public MeleeTable meleeTable(){
        return mMeleeTable;
    }

    public FireTable fireTable(){
        return mFireTable;
    }

    public SipRatio sipRatio(){
        return mSipRatio;
    }
}
