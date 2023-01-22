package ru.serge.austerlitz.counters;

import android.content.ClipData;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ru.serge.austerlitz.MainActivity;
import ru.serge.austerlitz.R;

public class ActivatedUnitListView extends UnitListView {
    private ArrayList<UnitView> mUnitViews;
    private Boolean mFlankChavalry = false;

    // Step 1 - This interface defines the type of messages I want to communicate to my owner
    public interface OnPlacingUnitListener {
        // implement validation if diferent unit types are placed at the same time into activated unit box
        public boolean onUnitPlaced(ArrayList<UnitView> unitViews, UnitView placingUnitView); //provide item selected to the listener
        public void onUnitRemoved(ArrayList<UnitView> unitViews);
    }

    // Step 2 - This variable represents the listener passed in by the owning object
    // The listener must implement the events interface and passes messages up to the parent.
    private OnPlacingUnitListener mListener;

    // Assign the listener implementing events interface that will receive the events
    public void setOnChangeListener(ActivatedUnitListView.OnPlacingUnitListener listener) {
        mListener = listener;
    }

    public ActivatedUnitListView(Context context) {
        super(context);
        initializeViews(context);
    }

    public ActivatedUnitListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public ActivatedUnitListView(Context context,
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
        inflater.inflate(R.layout.activated_unit_list_view, this);
    }

    //specify default list of units
    public void set(MainActivity activity, String title){
        mPlay = activity.play();
        mTitle = title;

        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(title);

        this.setOnDragListener(new MyDragListener());
    }

    //specify the flank cavalry impact
    public void set(MainActivity activity, String title, Boolean flankCavalry){
        if(flankCavalry == true){
            mFlankChavalry = true;
        }

        set(activity, title);
    }

    private void updateTitle(){
        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(mTitle + " (" + count() + ")");
    }

    //provide lead unit
    public UnitView leadUnit(){
        return mUnitViews.get(0);
    }

    //calculate the total size for activated units
    public int count(){
        int sum = 0;

        if(mUnitViews != null){
            for(UnitView unitView:mUnitViews){
                sum = sum + unitView.size();
            }
        }

        //in case there is a flank
        if(mFlankChavalry == true){
            sum = sum * 2;
        }

        return sum;
    }

    //clean up all placed units
    public void clear(){
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        container.removeAllViews();

        mUnitViews = null;
    }

    class MyDragListener implements OnDragListener {

        @Override
        public boolean onDrag(View view, DragEvent event) {
            int action = event.getAction();
            UnitView movingView = mPlay.movingUnit();
            LinearLayout container = (LinearLayout) findViewById(R.id.container);

            if(mUnitViews == null){
                mUnitViews = new ArrayList<UnitView>();
            }

            Log.d("Drag Event = ", "" + action);
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    mUnitViews.remove(movingView);
                    container.removeView(movingView);

                    if(mListener != null){
                        mListener.onUnitRemoved(mUnitViews);
                    }

                    break;
                case DragEvent.ACTION_DROP:
                    // Check first whether the unit was moved from a hex around

                    ClipData.Item clip = event.getClipData().getItemAt(0);
                    String hashCodes = clip.getText().toString();

                    //handle only a new unit view
                    if(movingView != null && mUnitViews.contains(movingView) != true) {
                        //instantiate a new unit and populate it with the data

                        Boolean check = true;
                        if(mListener != null){
                            check = mListener.onUnitPlaced(mUnitViews, movingView);
                        }

                        if(check == true) {
                            UnitView addingUnitView = new UnitView(getContext());
                            addingUnitView.clone(movingView);

                            //assign a ling click listener
                            setOnLongClickListener(addingUnitView);

                            //add new instance to the array of unit views
                            mUnitViews.add(addingUnitView);

                            //show the view
                            container.addView(addingUnitView);

                            //remove tmp object from the aggregator
                            mPlay.movingUnit(null);
                        }
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    //to define in future
                    break;
                default:
                    break;
            }
            Log.d("Drag Action Id ", "" + action);

            //recalculate the amount
            updateTitle();

            return true;
        }
    }
}
