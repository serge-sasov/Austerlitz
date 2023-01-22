package ru.serge.austerlitz.combat;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import java.util.HashMap;

import ru.serge.austerlitz.R;

public class MeleeDialogue extends DialogFragment {
    HashMap<String, CombatOutcome> mCombatOutcome;

    public MeleeDialogue(HashMap<String, CombatOutcome> combatOutcome){
        mCombatOutcome = combatOutcome;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // All the rest of the code goes here
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater =  getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.melee_result_dialogue, null);

        CombatOutcomeView attackerCombatOutcomeView = (CombatOutcomeView) dialogView.findViewById(R.id.attackerResultView);
        if(mCombatOutcome.get("Attacker") != null) {
            attackerCombatOutcomeView.displayShockResult(getString(R.string.attacker), mCombatOutcome.get("Attacker"));
        }else{
            attackerCombatOutcomeView.setVisibility(View.GONE);
        }

        CombatOutcomeView defenderCombatOutcomeView = (CombatOutcomeView) dialogView.findViewById(R.id.defenderResultView);
        if(mCombatOutcome.get("Defender") != null){
            defenderCombatOutcomeView.displayShockResult(getString(R.string.defender), mCombatOutcome.get("Defender"));
        }else{
            defenderCombatOutcomeView.setVisibility(View.GONE);
        }

        //Add buttons to control
        Button btnOK = (Button) dialogView.findViewById(R.id.btnOK);
        btnOK.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(dialogView).setMessage(R.string.combat_result);

        return builder.create();
    }
}
