package ru.serge.austerlitz.counters;

import android.content.ClipData;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.serge.austerlitz.MainActivity;
import ru.serge.austerlitz.R;
import ru.serge.austerlitz.game.Play;

public class UnitListView extends LinearLayout {
    protected ArrayList<UnitView> mUnitViews;
    protected Play mPlay;
    protected String mTitle; //view title

    public UnitListView(Context context) {
        super(context);
        initializeViews(context);
    }

    public UnitListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public UnitListView(Context context,
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
        inflater.inflate(R.layout.unit_list_view, this);
    }

    //specify default list of units
    public void set(MainActivity activity, String nation, String type){
        mPlay = activity.play(); // get reference to the play aggregator object
        set(nation, type, 12, 1);
    }

    public void set(MainActivity activity, String nation, String type, int max, int min){
        mPlay = activity.play(); // get reference to the play aggregator object
        set(nation, type, max, min);
    }

    public void set(MainActivity activity, String nation, String type, ArrayList<Integer> sizes){
        mPlay = activity.play(); // get reference to the play aggregator object
        set(nation, type, sizes);
    }

    //specify default list of units
    public void set(String nation, String type, ArrayList<Integer> sizes){
        mUnitViews = new ArrayList<UnitView>();
        for(Integer size: sizes){
            UnitView unitView = new UnitView(getContext());
            unitView.set(nation, type, size);

            //assign a ling click listener
            setOnLongClickListener(unitView);

            mUnitViews.add(unitView);
        }

        rebuild(nation + " " + type);
    }

    //specify default list of units
    public void set(String nation, String type, int max, int min){
        mUnitViews = new ArrayList<UnitView>();
        for(int size = max; size >= min; size--){
            UnitView unitView = new UnitView(getContext());
            unitView.set(nation, type, size);

            //assign a ling click listener
            setOnLongClickListener(unitView);

            mUnitViews.add(unitView);
        }

        rebuild(nation + " " + type);
    }

    protected void setOnLongClickListener(UnitView unitView){
        if(unitView != null) {
            unitView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    //put moving unit into temp table
                    mPlay.movingUnit((UnitView) view);

                    JSONObject jo = new JSONObject();
                    // --------- build up json data --------------
                    try {
                        jo.put("unit_view_hashcode", unitView.hashCode());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // --------- build up json data --------------
                    ClipData data = ClipData.newPlainText("hashcodes", jo.toString());
                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

                    view.startDrag(data, //data to be dragged
                            shadowBuilder, //drag shadow
                            view, //local data about the drag and drop operation
                            0   //no needed flags
                    );
                    return true;
                }
            });
        }
    }

    //rebuild representation
    protected void rebuild(String title){
        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(title);

        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        //put warriors to the container
        for(UnitView unitView: mUnitViews){
            container.addView(unitView);
        }
    }
}
