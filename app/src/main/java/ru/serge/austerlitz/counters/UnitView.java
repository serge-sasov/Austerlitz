package ru.serge.austerlitz.counters;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.HashMap;

import ru.serge.austerlitz.R;

public class UnitView extends ConstraintLayout {
    private String mNation;
    private String mType;
    private int mSize;

    //populate a list of sign resources
    private static HashMap<String, HashMap<String, Integer>> mUnitTypeSign = unitTypeSign();

    public UnitView(Context context) {
        super(context);
        initializeViews(context);
    }

    public UnitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public UnitView(Context context,
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
        inflater.inflate(R.layout.unit, this);
    }

    /*
     * @input:
     *  - nation: french, russion, etc
     *  - type: cavalry, infantry
     *   - size: unit size
     */
    public void set(String nation, String type, int size){
        mNation = nation;
        mType = type;
        mSize = size;

        rebuild();
    }

    //clone the view using the reference object
    public void clone(UnitView  unitView){
        mNation = unitView.nation();
        mType = unitView.type();
        mSize = unitView.size();

        rebuild();
    }

    public String nation(){
        return mNation;
    }

    public String type(){
        return mType;
    }

    public int size(){
        return mSize;
    }

    private static HashMap<String, HashMap<String, Integer>> unitTypeSign(){
        HashMap<String, HashMap<String, Integer>> unitTypeSigns = new HashMap<String, HashMap<String, Integer>>();

        HashMap<String, Integer> french_units = new HashMap<String, Integer>();
        french_units.put("Artillery", R.drawable.french_artillery);
        french_units.put("Mounted Artillery", R.drawable.french_mounted_artillery);
        french_units.put("Infantry", R.drawable.french_infantry);
        french_units.put("Light Infantry", R.drawable.french_light_infantry);
        french_units.put("Cavalry", R.drawable.french_cavalry);

        HashMap<String, Integer> russia_units = new HashMap<String, Integer>();
        russia_units.put("Artillery", R.drawable.russia_artillery);
        russia_units.put("Mounted Artillery", R.drawable.russia_mounted_artillery);
        russia_units.put("Infantry", R.drawable.russia_infantry);
        russia_units.put("Light Infantry", R.drawable.russia_light_infantry);
        russia_units.put("Cavalry", R.drawable.russia_cavalry);

        ArrayList<String> nations = new ArrayList<String>();
        nations.add("France");
        nations.add("Russia");

        unitTypeSigns.put("France", french_units);
        unitTypeSigns.put("Russia", russia_units);

        return unitTypeSigns;
    }

    //Retrieve a resource id using the input attributes nation & warrior type.
    private Integer unitTypeSign(String nation, String type){
        if(mUnitTypeSign.get(nation) != null && mUnitTypeSign.get(nation).get(type) != null) {
            return mUnitTypeSign.get(nation).get(type);
        }

        return null;
    }

    //rebuild view outlook
    private void rebuild(){
        //setup size
        TextView sizeView = (TextView) findViewById(R.id.size);
        sizeView.setText("" + mSize);

        //setup unit type & flag
        TextView unitSignView = (TextView) findViewById(R.id.unitSign);
        unitSignView.setBackgroundResource(mUnitTypeSign.get(mNation).get(mType));
    }

    //shows whether the unit is artillery type
    public Boolean artillery(){
        Boolean artillery = false;
        if(type().equals("Artillery") || type().equals("Mounted Artillery")){
            artillery = true;
        }

        return artillery;
    }
}