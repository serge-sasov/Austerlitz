package ru.serge.austerlitz.combat;

import java.util.HashMap;

import ru.serge.austerlitz.game.Play;
import ru.serge.austerlitz.tables.FireTable;

public class Fire {
    private FireTable mFireTable;

    public Fire(Play play){
        mFireTable = play.fireTable();
    }

    public CombatOutcome calculate(int diceRollValue, int modifiersValue, boolean artillery){
        return mFireTable.fire(diceRollValue + modifiersValue, artillery);
    }
}
