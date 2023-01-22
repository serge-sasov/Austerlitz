package ru.serge.austerlitz.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.HashMap;

import ru.serge.austerlitz.MainActivity;
import ru.serge.austerlitz.R;
import ru.serge.austerlitz.combat.FireDialogue;
import ru.serge.austerlitz.combat.MeleeDialogue;
import ru.serge.austerlitz.combat.Fire;
import ru.serge.austerlitz.combat.Melee;
import ru.serge.austerlitz.counters.ActivatedUnitListView;
import ru.serge.austerlitz.counters.UnitListView;
import ru.serge.austerlitz.counters.UnitView;
import ru.serge.austerlitz.items_selector.ItemSelector;
import ru.serge.austerlitz.items_selector.ItemSelectorSimpleHorizontal;
import ru.serge.austerlitz.combat.CombatOutcome;

/**
 * A placeholder fragment containing a simple view.
 */
public class CombatFragment extends Fragment implements ActivatedUnitListView.OnPlacingUnitListener {
    private View mRoot;
    private Boolean mHiddenArtilleryModifiers = false;

    private static final String ARG_SECTION_NUMBER = "section_number";
    private int mIndex; //store the index value being displayed

    private PageViewModel pageViewModel;

    public static CombatFragment newInstance(int index) {
        CombatFragment fragment = new CombatFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        mIndex = index;

        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = null;

        switch (mIndex){
            case 1:
                //Melee
                root = prepareMeleeView(inflater, container);
                break;

            case 2:
                //Fire
                root = prepareFireView(inflater, container);
                break;

            case 3:
                //Victory points
                root = null;
                break;

            default:
                break;
        }

        return root;
    }

    private View prepareMeleeView(@NonNull LayoutInflater inflater, ViewGroup container){
        View root = inflater.inflate(R.layout.fragment_melee_main, container, false);

        // specify terrain modifier
        ItemSelector terrainModifier = configureItemSelector(root, R.id.terrain, getString(R.string.terrain_modifier), -1, 3, 0);

        // specify Delta TQ modifier
        ItemSelector deltaTroopQualityModifier = configureItemSelector(root, R.id.troopQuality, getString(R.string.defender_TQ_Attacker_TQ),-8, 8, 0);

        //get input data from main activity
        MainActivity activity = (MainActivity) getActivity();

        //--------------------------------------------------------------
        ActivatedUnitListView activatedAttackerList = (ActivatedUnitListView) root.findViewById(R.id.activatedAttackerList);
        activatedAttackerList.set(activity, getString(R.string.activated_attackers_units));

        UnitListView attackerInfantry = (UnitListView) root.findViewById(R.id.attackerInfantry);
        attackerInfantry.set(activity, "France","Infantry", 10, 1);

        UnitListView attackerCavalry = (UnitListView) root.findViewById(R.id.attackerCavalry);
        attackerCavalry.set(activity, "France","Cavalry", 8, 1);

        ActivatedUnitListView activatedAttackerFlankList = (ActivatedUnitListView) root.findViewById(R.id.activatedAttackerFlankList);
        activatedAttackerFlankList.set(activity, getString(R.string.activated_attackers_flank_cavalry_units), true);

        UnitListView attackerCavalryFlank = (UnitListView) root.findViewById(R.id.attackerCavalryFlank);
        attackerCavalryFlank.set(activity, "France","Cavalry", 8, 1);

        ActivatedUnitListView activatedDefenderList = (ActivatedUnitListView) root.findViewById(R.id.activatedDefenderList);
        activatedDefenderList.set(activity, getString(R.string.activated_defenders_units));

        UnitListView defenderInfantry = (UnitListView) root.findViewById(R.id.defenderInfantry);
        defenderInfantry.set(activity, "Russia","Infantry", 10, 1);

        UnitListView defenderCavalry = (UnitListView) root.findViewById(R.id.defenderCavalry);
        defenderCavalry.set(activity, "Russia","Cavalry", 8, 1);

        //setup dice roll
        ItemSelectorSimpleHorizontal diceRollView = (ItemSelectorSimpleHorizontal) root.findViewById(R.id.diceRoll);
        ArrayList<String> diceRolls = new ArrayList<String>();
        for(int roll = 2; roll <= 12; roll++){
            diceRolls.add("" + roll);
        }
        diceRollView.setValues(diceRolls, getString(R.string.dice_rolls));
        diceRollView.setSelectedValue("6");

        Button calculateBtn = (Button) root.findViewById(R.id.calculateBtn);
        calculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if the necessary fields are populated
                int attackerSize = activatedAttackerList.count() + activatedAttackerFlankList.count();
                int defenderSize = activatedDefenderList.count();

                //validate input data
                if(attackerSize == 0){
                    //Throw warning messages
                    Toast.makeText(getContext(), R.string.no_attackers_are_selected,Toast.LENGTH_LONG).show();
                }else if(defenderSize == 0){
                    Toast.makeText(getContext(), R.string.тo_defenders_are_selected,Toast.LENGTH_LONG).show();
                }else{
                    //validation passed successfully
                    int modifiersValue = terrainModifier.getSelectedIntegerValue() + deltaTroopQualityModifier.getSelectedIntegerValue() + meleeModifiersValue(root);

                    Melee melee = new Melee(activity.play());

                    Integer diceRollResult = diceRollView.getSelectedIntegerValue();

                    if(diceRollResult == null){
                        Toast.makeText(getContext(), R.string.no_dice_roll_value_is_given,Toast.LENGTH_LONG).show();
                    }else {
                        HashMap<String, CombatOutcome> outcome = melee.calculate(diceRollResult, attackerSize, defenderSize, modifiersValue);

                        //Call a dialogue
                        MeleeDialogue dialog = new MeleeDialogue(outcome);

                        dialog.setTargetFragment(CombatFragment.this, 1);
                        dialog.show(getFragmentManager(), dialog.getClass().getName());
                    }

                    int test = 0;
                }
            }
        });

        //setup clear button
        Button clearBtn = (Button) root.findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if the necessary fields are populated
                clearMeleeConfig(root);
            }
        });

        return root;
    }

    //setup item selector configuration
    private ItemSelector configureItemSelector(View view, int id, String title, int minValue, int maxValue, int defValue){
        ArrayList<String> deltaTroopQualityModifierValues = new ArrayList<String>();
        for(int value = minValue; value <= maxValue; value ++) {
            deltaTroopQualityModifierValues.add("" + value);
        }

        ItemSelector itemSelector = (ItemSelector) view.findViewById(id);
        itemSelector.setValues(deltaTroopQualityModifierValues, title);
        itemSelector.setSelectedValue("" + defValue);

        return itemSelector;
    }

    private int meleeModifiersValue(View view){
        int result = 0;

        //cavalry Unprepared Attack
        Switch cavalryUnpreparedAttack = (Switch) view.findViewById(R.id.cavalryUnpreparedAttack);
        if(cavalryUnpreparedAttack.isChecked() == true){
            result = result + 2;
        }

        //fatigued Defending Cavalry
        Switch fatiguedDefendingCavalry = (Switch) view.findViewById(R.id.fatiguedDefendingCavalry);
        if(fatiguedDefendingCavalry.isChecked() == true){
            result = result - 2;
        }

        //Encircled Defender
        Switch encircledDefender = (Switch) view.findViewById(R.id.encircledDefender);
        if(encircledDefender.isChecked() == true){
            result = result - 2;
        }

        //Mixing Attacker Formation Penalty
        Switch mixingAttackerFormationPenalty = (Switch) view.findViewById(R.id.mixingAttackerFormationPenalty);
        if(mixingAttackerFormationPenalty.isChecked() == true){
            result = result +1;
        }

        //Mixing Defender Formation Penalty
        Switch mixingDefenderFormationPenalty = (Switch) view.findViewById(R.id.mixingDefenderFormationPenalty);
        if(mixingDefenderFormationPenalty.isChecked() == true){
            result = result -1;
        }

        //Attack versus a stock containing at least one rooted Unit
        Switch attackVersusRootedUnit = (Switch) view.findViewById(R.id.attackVersusRootedUnit);
        if(attackVersusRootedUnit.isChecked() == true){
            result = result -2;
        }

        //Attack With demorized lead unit
        Switch attackWithDemorizedUnit = (Switch) view.findViewById(R.id.attackWithDemorizedUnit);
        if(attackWithDemorizedUnit.isChecked() == true){
            result = result +1;
        }

        return result;
    }

    private void clearMeleeConfig(View view){

        //disable all tangles
        ArrayList<Switch> toglers = new ArrayList<Switch>();

        toglers.add(view.findViewById(R.id.cavalryUnpreparedAttack));
        toglers.add(view.findViewById(R.id.fatiguedDefendingCavalry));
        toglers.add(view.findViewById(R.id.encircledDefender));
        toglers.add(view.findViewById(R.id.mixingAttackerFormationPenalty));
        toglers.add(view.findViewById(R.id.mixingDefenderFormationPenalty));
        toglers.add(view.findViewById(R.id.attackVersusRootedUnit));
        toglers.add(view.findViewById(R.id.attackWithDemorizedUnit));


        for(Switch taggle : toglers) {
            if (taggle.isChecked() == true) {
                taggle.setChecked(false);
            }
        }

        //set the modifiers to default "0"
        ArrayList<ItemSelector> modifiers = new ArrayList<ItemSelector>();
        modifiers.add(view.findViewById(R.id.terrain));
        modifiers.add(view.findViewById(R.id.troopQuality));

        for(ItemSelector modifier : modifiers) {
            modifier.setSelectedValue("0");
        }

        //set Dice Roll to default "6" all modifiers
        ItemSelectorSimpleHorizontal diceRollView = (ItemSelectorSimpleHorizontal) view.findViewById(R.id.diceRoll);
        diceRollView.setSelectedValue("6");

        //clear out all selected units
        ArrayList<ActivatedUnitListView> activatedUnitLists = new ArrayList<ActivatedUnitListView>();
        activatedUnitLists.add(view.findViewById(R.id.activatedAttackerList));
        activatedUnitLists.add(view.findViewById(R.id.activatedAttackerFlankList));
        activatedUnitLists.add(view.findViewById(R.id.activatedDefenderList));

        for(ActivatedUnitListView activatedUnitList: activatedUnitLists) {
            activatedUnitList.clear();
        }
    }

    private View prepareFireView(@NonNull LayoutInflater inflater, ViewGroup container){
        View root = inflater.inflate(R.layout.fragment_fire_main, container, false);
        mRoot = root;

        // specify terrain modifier
        ItemSelector terrainModifier = configureItemSelector(root, R.id.terrain, getString(R.string.terrain_modifier), -1, 3, 0);

        // specify Delta TQ modifier
        ItemSelector distanceModifier = configureItemSelector(root, R.id.distance, getString(R.string.distance),1, 4, 1);

        //get input data from main activity
        MainActivity activity = (MainActivity) getActivity();

        //--------------------------------------------------------------
        ActivatedUnitListView activatedAttackerList = (ActivatedUnitListView) root.findViewById(R.id.activatedAttackerList);
        activatedAttackerList.set(activity, getString(R.string.activated_attackers_units));
        activatedAttackerList.setOnChangeListener(CombatFragment.this); //set listener to implement validation

        UnitListView infantry = (UnitListView) root.findViewById(R.id.infantry);
        infantry.set(activity, "France","Infantry", 10, 1);

        UnitListView light_infantry = (UnitListView) root.findViewById(R.id.light_infantry);
        light_infantry.set(activity, "France","Light Infantry", 7, 1);

        ArrayList<Integer> artillerySize = new ArrayList<Integer>();
        artillerySize.add(15);
        artillerySize.add(8);
        artillerySize.add(5);
        artillerySize.add(4);
        artillerySize.add(3);
        artillerySize.add(2);
        artillerySize.add(1);

        UnitListView artillery = (UnitListView) root.findViewById(R.id.artillery);
        artillery.set(activity, "France","Artillery", artillerySize);

        UnitListView mountedArtillery = (UnitListView) root.findViewById(R.id.mountedArtillery);
        mountedArtillery.set(activity, "France","Mounted Artillery", 4, 1);

        //setup dice roll
        ItemSelectorSimpleHorizontal diceRollView = (ItemSelectorSimpleHorizontal) root.findViewById(R.id.diceRoll);
        ArrayList<String> diceRolls = new ArrayList<String>();
        for(int roll = 2; roll <= 12; roll++){
            diceRolls.add("" + roll);
        }
        diceRollView.setValues(diceRolls, getString(R.string.dice_rolls));
        diceRollView.setSelectedValue("6");

        Button calculateBtn = (Button) root.findViewById(R.id.calculateBtn);
        calculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if the necessary fields are populated
                int attackerSize = activatedAttackerList.count();

                //validate input data
                if(attackerSize == 0){
                    //Throw warning messages
                    Toast.makeText(getContext(),R.string.no_attackers_are_selected, Toast.LENGTH_LONG).show();
                }else{
                    //validation passed successfully
                    int modifiersValue = terrainModifier.getSelectedIntegerValue() + fireModifiersValue(root) + fireCalculableModifiers(root);

                    Fire fire = new Fire(activity.play());

                    Integer diceRollResult = diceRollView.getSelectedIntegerValue();

                    if(diceRollResult == null){
                        Toast.makeText(getContext(), R.string.no_dice_roll_value_is_given, Toast.LENGTH_LONG).show();
                    }else {
                        //derive lead unit type
                        Boolean artilleryType = false;
                        UnitView leadUnit = activatedAttackerList.leadUnit();

                        if(leadUnit.artillery() == true){
                            artilleryType = true;
                        }

                        CombatOutcome outcome = fire.calculate(diceRollResult, modifiersValue, artilleryType);

                        //Call a dialogue
                        FireDialogue dialog = new FireDialogue(outcome);

                        dialog.setTargetFragment(CombatFragment.this, 1);
                        dialog.show(getFragmentManager(), dialog.getClass().getName());
                    }

                    int test = 0;
                }
            }
        });

        //setup clear button
        Button clearBtn = (Button) root.findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if the necessary fields are populated
                clearFireConfig(root);
            }
        });


        return root;
    }

    private int fireModifiersValue(View view){
        int result = 0;

        //derive distance range
        ItemSelector distanceModifier = (ItemSelector) view.findViewById(R.id.distance);
        int distance = distanceModifier.getSelectedIntegerValue();

        //cavalry Unprepared Attack
        Switch opportunityFire = (Switch) view.findViewById(R.id.opportunityFire);
        if(opportunityFire.isChecked() == true && distance == 1){
            result = result - 1;
        }

        //fatigued Defending Cavalry
        Switch coalitionFiring = (Switch) view.findViewById(R.id.coalitionFiring);
        if(coalitionFiring.isChecked() == true){
            result = result + 1;
        }

        //mud penalty (applicable only if the target located not in adjacent hex)
        Switch storm = (Switch) view.findViewById(R.id.storm);
        if(storm.isChecked() == true && distance > 1){
            result = result + 1;
        }

        //Mixing Attacker Formation Penalty
        Switch unitInDefenceOrder = (Switch) view.findViewById(R.id.unitInDefenceOrder);
        if(unitInDefenceOrder.isChecked() == true){
            result = result - 1;
        }

        //Mud Penalty (applicable only if the target located not in adjacent hex)
        Switch mud = (Switch) view.findViewById(R.id.mud);
        if(mud.isChecked() == true && distance > 1){
            result = result + 1;
        }

        //Attack versus a stock containing at least one rooted Unit
        Switch artilleryAlongInTheHex = (Switch) view.findViewById(R.id.artilleryAlongInTheHex);
        if(artilleryAlongInTheHex.isChecked() == true){
            result = result + 1;
        }

        return result;
    }

    private int fireCalculableModifiers(View view){
        int result = 0;

        //less then for SIP firing
        ActivatedUnitListView activatedAttackerList = (ActivatedUnitListView) view.findViewById(R.id.activatedAttackerList);
        if(activatedAttackerList.count() < 4){
            result = result + 1;
        }

        //Light infantry type lead unit firing
        UnitView leadUnit = activatedAttackerList.leadUnit();
        if(leadUnit.type().equals("Light Infantry") == true){
            result = result - 1;
        }

        //3 or 4 hexes range (artillery)
        ItemSelector distanceModifier = (ItemSelector) view.findViewById(R.id.distance);
        if(distanceModifier.getSelectedIntegerValue() >=3){
            result = result + 1;
        }

        //Adjanced target (artillery)
        if(leadUnit.artillery() == true &&
                distanceModifier.getSelectedIntegerValue() == 1){
            result = result - 1;
        }

        //for each 10Sip add -1 (no more that 15Sip artillery can fire from one hex)
        if(leadUnit.artillery() == true){
            int count = activatedAttackerList.count();

            if(count >= 20){
                result = result - 2;
            }else if(count >= 10){
                result = result - 1;
            }
        }

        return result;
    }

    private void hideFireArtilleryModifiers(View view){
        ArrayList<Switch> togglers = new ArrayList<Switch>();

        togglers.add(view.findViewById(R.id.storm));
        togglers.add(view.findViewById(R.id.unitInDefenceOrder));
        togglers.add(view.findViewById(R.id.mud));
        togglers.add(view.findViewById(R.id.coalitionFiring));

        for (Switch toggle : togglers) {
            toggle.setChecked(false);
            toggle.setVisibility(View.GONE);
        }

        ArrayList<TextView> textViews = new ArrayList<TextView>();
        textViews.add(view.findViewById(R.id.stormText));
        textViews.add(view.findViewById(R.id.unitInDefenceOrderText));
        textViews.add(view.findViewById(R.id.mudText));
        textViews.add(view.findViewById(R.id.coalitionFiringText));
        textViews.add(view.findViewById(R.id.artilleryModifiersText));
        for (TextView textView : textViews) {
            textView.setVisibility(View.GONE);
        }

        //hide distance field
        ItemSelector distance = (ItemSelector) view.findViewById(R.id.distance);
        distance.setSelectedValue("1");
        distance.setVisibility(View.GONE);

        /*
        UnitListView artillery = (UnitListView) view.findViewById(R.id.artillery);
        artillery.setVisibility(View.GONE);

        UnitListView mountedArtillery = (UnitListView) view.findViewById(R.id.mountedArtillery);
        artillery.setVisibility(View.GONE);
         */

        mHiddenArtilleryModifiers = true;
    }

    private void showFireArtilleryModifiers(View view){
        ArrayList<Switch> togglers = new ArrayList<Switch>();

        togglers.add(view.findViewById(R.id.storm));
        togglers.add(view.findViewById(R.id.unitInDefenceOrder));
        togglers.add(view.findViewById(R.id.mud));
        togglers.add(view.findViewById(R.id.coalitionFiring));

        for (Switch toggle : togglers) {
            toggle.setVisibility(View.VISIBLE);
        }

        ArrayList<TextView> textViews = new ArrayList<TextView>();
        textViews.add(view.findViewById(R.id.stormText));
        textViews.add(view.findViewById(R.id.unitInDefenceOrderText));
        textViews.add(view.findViewById(R.id.mudText));
        textViews.add(view.findViewById(R.id.coalitionFiringText));
        textViews.add(view.findViewById(R.id.artilleryModifiersText));
        for (TextView textView : textViews) {
            textView.setVisibility(View.VISIBLE);
        }

        ItemSelector distance = (ItemSelector) view.findViewById(R.id.distance);
        distance.setVisibility(View.VISIBLE);

        /*
        UnitListView artillery = (UnitListView) view.findViewById(R.id.artillery);
        artillery.setVisibility(View.VISIBLE);

        UnitListView mountedArtillery = (UnitListView) view.findViewById(R.id.mountedArtillery);
        artillery.setVisibility(View.VISIBLE);
         */

        mHiddenArtilleryModifiers = false;
    }

    private void clearFireConfig(View view) {
        //disable all tangles
        ArrayList<Switch> toglers = new ArrayList<Switch>();

        toglers.add(view.findViewById(R.id.opportunityFire));
        toglers.add(view.findViewById(R.id.coalitionFiring));
        toglers.add(view.findViewById(R.id.storm));
        toglers.add(view.findViewById(R.id.unitInDefenceOrder));
        toglers.add(view.findViewById(R.id.mud));
        toglers.add(view.findViewById(R.id.artilleryAlongInTheHex));

        for (Switch taggle : toglers) {
            if (taggle.isChecked() == true) {
                taggle.setChecked(false);
            }
        }

        //set the modifiers to default values
        ItemSelector terrain = (ItemSelector) view.findViewById(R.id.terrain);
        terrain.setSelectedValue("0");

        ItemSelector distance = (ItemSelector) view.findViewById(R.id.distance);
        distance.setSelectedValue("1");


        //set Dice Roll to default "6" all modifiers
        ItemSelectorSimpleHorizontal diceRollView = (ItemSelectorSimpleHorizontal) view.findViewById(R.id.diceRoll);
        diceRollView.setSelectedValue("6");

        //clear out all selected units
        ActivatedUnitListView activatedAttackerList = (ActivatedUnitListView) view.findViewById(R.id.activatedAttackerList);
        activatedAttackerList.clear();

        //if artillery toggle are hidden then show them
        if(mHiddenArtilleryModifiers == true){
            showFireArtilleryModifiers(view);
        }
    }

    @Override
    //use to check whether the artillery unit type is used along any other type
    public boolean onUnitPlaced(ArrayList<UnitView> unitViews, UnitView placingUnitView) {
        if(unitViews != null){
            Boolean artyllery = false;
            Boolean noneArtillery = false;

            for(UnitView unitView: unitViews){
                if(unitView.artillery() == true){
                    artyllery = true;
                }else{
                    noneArtillery = true;
                }
            }

            if(placingUnitView.artillery() == true){
                artyllery = true;
            }else{
                noneArtillery = true;
            }

            if(artyllery == true & noneArtillery == true){
                //rise an warning and return false
                Toast.makeText(getContext(), R.string.artillery_and_none_artillery_units_cannot_be_used_together,Toast.LENGTH_LONG).show();
                return false;
            }
        }

        //hide artillery related modifiers
        if(mHiddenArtilleryModifiers == false && placingUnitView.artillery() == false){
            hideFireArtilleryModifiers(mRoot);
        }

        //show artillery related modifiers
        if(mHiddenArtilleryModifiers == true && placingUnitView.artillery() == true){
            showFireArtilleryModifiers(mRoot);
        }

        return true;
    }

    @Override
    public void onUnitRemoved(ArrayList<UnitView> unitViews) {
        if(mHiddenArtilleryModifiers == true &&
                (unitViews == null || unitViews.size() == 0 )){
            showFireArtilleryModifiers(mRoot);
        }
    }
}