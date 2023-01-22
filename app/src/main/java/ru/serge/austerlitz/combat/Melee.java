package ru.serge.austerlitz.combat;

import java.util.HashMap;

import ru.serge.austerlitz.game.Play;
import ru.serge.austerlitz.tables.MeleeTable;
import ru.serge.austerlitz.tables.SipRatio;

public class Melee {
    private MeleeTable mMeleeTable;
    private SipRatio mSipRatio;

    public Melee(Play play){
        mMeleeTable = play.meleeTable();
        mSipRatio = play.sipRatio();
    }

    public HashMap<String, CombatOutcome> calculate(int diceRollValue, int attackerSize, int defenderSize, int modifiersValue){
        //derive final modifiers impact
        modifiersValue = modifiersValue + mSipRatio.calculate(attackerSize, defenderSize);
        if(modifiersValue > 5){
            modifiersValue = 5;
        }else if(modifiersValue < -5){
            modifiersValue = -5;
        }
        return mMeleeTable.calculate(diceRollValue + modifiersValue);
    }
}
