package ru.serge.austerlitz.tables;

import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import ru.serge.austerlitz.combat.CombatOutcome;
import ru.serge.austerlitz.utility.LoadTable;
import ru.serge.austerlitz.utility.ParseTable;

public class MeleeTable extends ParseTable {

    /*
    @input:
    rawFireTbl - basic raw input value for fire resolution
    diceRoll - dice roll value, given either via manual roll or generated automatically
    artillery - true for artillery, false for infantry
  */
    public MeleeTable(LoadTable rawFireTbl) throws CsvException, IOException {
        super(rawFireTbl);
    }

    /*
    * @input: diceRoll - final modified dice roll value
      @output: a calculated combat outcome for attacker and defender
     */
    public HashMap<String, CombatOutcome> calculate(int diceRoll){
        Boolean eliminated = false;

        //populate the array list
        ArrayList<String> sides = new ArrayList<String>();
        sides.add("Attacker");
        sides.add("Defender");

        //the melee combat outcome
        HashMap<String, CombatOutcome> outcome = new HashMap<String, CombatOutcome>();

        if(diceRoll < 0){
            //set eliminated for defending stock
            eliminated = true;
        }

        //Convert the input value to the given boundaries 2 .. 12
        if(diceRoll < 2){
            diceRoll = 2;
        }else if(diceRoll > 12){
            diceRoll = 12;
        }

        String diceRollStr = "" + diceRoll;

        for(String side: sides) {
            String result = getTableValueByGivenNames(side, diceRollStr);

            //parse out the result value
            if(side.equals("Defender")) {
                outcome.put(side, meleeResult(result, eliminated));
            }else{
                outcome.put(side, meleeResult(result, false));
            }
        }
        return outcome;
    }

    /*
     * parse out melee result for given side
     * @input: rawEffect - raw result data
     * @output: MeleeResult - parsed out melee outcome object
     * */
    private CombatOutcome meleeResult(String rawEffect, Boolean eliminated){
        CombatOutcome combatOutcome = null;

        Boolean qft = false; //quality formation test
        int qftAddValue = 0;
        Boolean reduced = false;

        if(rawEffect != null) {
            String[] effectArray = rawEffect.split("/");
            Boolean changed = false; //shows whether some effect has influenced the outcome result
            for (String effectString : effectArray) {
                switch (effectString) {
                    case "-":
                        break;

                    case "QFT":
                        qft = true;
                        changed = true;
                        break;

                    case "QFT1":
                        qft = true;
                        qftAddValue = 1;
                        changed = true;
                        break;

                    case "QFT2":
                        qft = true;
                        qftAddValue = 2;
                        changed = true;;
                        break;

                    case "R":
                        reduced = true;
                        changed = true;
                        break;

                    default:
                        break;
                }
            }
            if(changed == true) {
                combatOutcome = new CombatOutcome(qft, qftAddValue, reduced, eliminated);
            }
        }

        return combatOutcome;
    }
}