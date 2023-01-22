package ru.serge.austerlitz.combat;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import ru.serge.austerlitz.R;

public class FireDialogue extends DialogFragment {
    CombatOutcome mCombatOutcome;

    public FireDialogue(CombatOutcome combatOutcome){
        mCombatOutcome = combatOutcome;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // All the rest of the code goes here
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater =  getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.fire_result_dialogue, null);

        CombatOutcomeView fireResultView = (CombatOutcomeView) dialogView.findViewById(R.id.fireResultView);
        TextView missText = (TextView) dialogView.findViewById(R.id.missText);
        if(mCombatOutcome != null) {
            fireResultView.displayShockResult("", mCombatOutcome);
            missText.setVisibility(View.GONE);
        }else{
            fireResultView.setVisibility(View.GONE);
        }
        //Add buttons to control
        Button btnOK = (Button) dialogView.findViewById(R.id.btnOK);
        btnOK.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(dialogView).setMessage(R.string.fire_skirmish_outcome);

        return builder.create();
    }
}
