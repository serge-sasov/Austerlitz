package ru.serge.austerlitz.tables;

import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import ru.serge.austerlitz.utility.LoadTable;
import ru.serge.austerlitz.utility.ParseTable;

public class SipRatio extends ParseTable {
    private HashMap<Float, Integer> mRatioMap;
    private ArrayList<Float> mRatioValues;

    public SipRatio(LoadTable rawSipTbl) throws CsvException, IOException {
        super(rawSipTbl);

        //ratio to result mapping
        mRatioMap = new HashMap<Float, Integer>();
        mRatioValues = new ArrayList<Float>();

        //get the full string along with the row title
        String [] rowRatioValues = getRowByRowTitle("Ratio");

        //starts from 1 as the first item is the row title
        for(int index = 1; index < rowRatioValues.length; index++){
            String[] rawRatioItems = rowRatioValues[index].split("/");

            float rawRatioItemAtt = Integer.valueOf(rawRatioItems[0]);
            float rawRatioItemDef = Integer.valueOf(rawRatioItems[1]);

            float ratio = rawRatioItemAtt/rawRatioItemDef;

            //populate the ratio mapping
            Integer value = Integer.valueOf(getTableValueByGivenNames(rowRatioValues[index],"DRM"));

            int test = 1;

            mRatioMap.put(ratio, value);
            mRatioValues.add(ratio);
        }
    }

    //derive the value based on the input parameters
    public Integer calculate(float attackerSize, float defenderSize){
        float ratio = attackerSize/defenderSize;

        if(ratio >= mRatioValues.get(0)){
            //return the highest attacker DRM value
            return mRatioMap.get(mRatioValues.get(0));
        }else if(ratio < mRatioValues.get(mRatioValues.size() - 1)){
            //return the highest defender DRM value
            return mRatioMap.get(mRatioValues.get(mRatioValues.size() - 1));
        }

        for(int index = 0; index < mRatioValues.size()-1; index++){
            float firstValue = mRatioValues.get(index);
            float secondValue = mRatioValues.get(index + 1);

            if(firstValue > ratio &&  ratio >= secondValue){
                return mRatioMap.get(secondValue);
            }
        }

        //return null in any other case
        return null;
    }
}
