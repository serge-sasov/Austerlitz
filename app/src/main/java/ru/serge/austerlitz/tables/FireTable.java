package ru.serge.austerlitz.tables;

import com.opencsv.exceptions.CsvException;

import java.io.IOException;

import ru.serge.austerlitz.combat.CombatOutcome;
import ru.serge.austerlitz.utility.LoadTable;
import ru.serge.austerlitz.utility.ParseTable;

public class FireTable extends ParseTable {

    /*
    @input:
    rawFireTbl - basic raw input value for fire resolution
    diceRoll - dice roll value, given either via manual roll or generated automatically
    artillery - true for artillery, false for infantry
  */
    public FireTable(LoadTable rawFireTbl) throws CsvException, IOException {
        super(rawFireTbl);
    }

    public CombatOutcome fire(int diceRoll, boolean artillery){
        //Convert the input value to the given boundaries -3 .. 12
        if(diceRoll < 2){
            diceRoll = 2;
        }else if(diceRoll > 8){
            diceRoll = 8;
        }

        String diceRollStr = "" + diceRoll;
        String result = null;

        if(artillery == true){
            result = getTableValueByGivenNames("Artillery", diceRollStr);
        }else{
            result = getTableValueByGivenNames("Infantry",diceRollStr);
        }

        //parse out the result value
        return calculateFireEffect(result);
    }

    /*
     * derive the parsed out value out of the input raw data
     * */
    private CombatOutcome calculateFireEffect(String rawEffect){
        if(rawEffect != null && rawEffect.equals("-") != true) {
            Boolean qft = false;
            int qftAddValue = 0;
            Boolean stepLose = false;
            int stepLoseValue = 0;

            String[] effectArray = rawEffect.split("/");

            for (String effectString : effectArray) {
                switch (effectString) {
                    case "-":
                        break;

                    case "QFT":
                        qft = true;
                        break;

                    case "QFT1":
                        qft = true;
                        qftAddValue = 1;
                        break;

                    case "QFT2":
                        qft = true;
                        qftAddValue = 2;
                        break;

                    case "1 Step":
                        stepLose = true;
                        stepLoseValue = 1;
                        break;

                    case "2 Steps":
                        stepLose = true;
                        stepLoseValue = 2;
                        break;

                    default:
                        break;
                }
            }
            return new CombatOutcome(qft, qftAddValue, stepLose, stepLoseValue);
        }
        return null;
    }


}
