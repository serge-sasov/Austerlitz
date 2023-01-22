package ru.serge.austerlitz.combat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.serge.austerlitz.R;

public class CombatOutcomeView extends LinearLayout {

    public CombatOutcomeView(Context context) {
        super(context);
        initializeViews(context);
    }

    public CombatOutcomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public CombatOutcomeView(Context context,
                             AttributeSet attrs,
                             int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.combat_outcome_view, this);
    }

    public void displayShockResult(String side, CombatOutcome combatOutcome){
        //Set the view category title
        TextView sideView = (TextView) findViewById(R.id.side);
        sideView.setText(side);


        if (combatOutcome != null && combatOutcome.qft() == true) {
            //set QFT data
            TextView qftValue = (TextView) findViewById(R.id.qftValue);
            String message = "QFT";
            if (combatOutcome.qftAddValue() > 0) {
                message = message + " + " + combatOutcome.qftAddValue();
            }
            qftValue.setText(message);
        } else {
            //hide the structure qftValue
            LinearLayout qft = (LinearLayout) findViewById(R.id.qft);
            qft.setVisibility(View.INVISIBLE);
        }

        if (combatOutcome == null || combatOutcome.reduced() == false) {
            //hide the structure reduceOneStep
            LinearLayout reduceOneStep = (LinearLayout) findViewById(R.id.reduceStockingUnits);
            reduceOneStep.setVisibility(View.INVISIBLE);
        }

        if (combatOutcome == null || combatOutcome.eliminated() == false) {
            //hide the structure reduceOneStep
            TextView eliminated = (TextView) findViewById(R.id.eliminated);
            eliminated.setVisibility(View.INVISIBLE);
        }

        if (combatOutcome == null || combatOutcome.stepLose() == false) {
            //hide the structure reduceOneStep
            LinearLayout stepLose = (LinearLayout) findViewById(R.id.stepLose);
            stepLose.setVisibility(View.INVISIBLE);
        }else{
            TextView stepLoseValue = (TextView) findViewById(R.id.stepLoseValue);
            stepLoseValue.setText(combatOutcome.stepLoseValue() + getContext().getString(R.string._step_s_));
        }
    }
}