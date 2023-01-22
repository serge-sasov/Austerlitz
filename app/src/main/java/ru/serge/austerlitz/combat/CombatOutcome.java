package ru.serge.austerlitz.combat;

//hold the outcome of the melee combat for the given side - attacker or defender
public class CombatOutcome {
    private  Boolean mQFT = false; //quality formation test
    private int mQFTAddValue = 0;
    private Boolean mReduced = false;
    private Boolean mEliminated = false;
    private Boolean mStepLose = false;
    private int mStepLoseValue = 0;

    public CombatOutcome(Boolean qft, int qftAddValue, Boolean reduced, Boolean eliminated){
        mQFT = qft;
        mQFTAddValue = qftAddValue;
        mReduced = reduced;
        mEliminated = eliminated;
    }

    public CombatOutcome(Boolean qft, int qftAddValue, Boolean stepLose, int stepLoseValue){
        mQFT = qft;
        mQFTAddValue = qftAddValue;
        mStepLose = stepLose;
        mStepLoseValue = stepLoseValue;
    }

    public Boolean qft(){
        return mQFT;
    }

    public int qftAddValue(){
        return mQFTAddValue;
    }

    public Boolean reduced(){
        return mReduced;
    }

    public Boolean eliminated(){
        return mEliminated;
    }

    public Boolean stepLose(){
        return mStepLose;
    }

    public int stepLoseValue(){
        return mStepLoseValue;
    }
}
