package ru.serge.austerlitz;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import ru.serge.austerlitz.fragments.CombatAdapter;
import ru.serge.austerlitz.game.Play;

public class MainActivity extends AppCompatActivity {
    private Play mPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CombatAdapter combatAdapter = new CombatAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(combatAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        //instantiate a new play
        mPlay = new Play(getResources());
    }

    public Play play(){
        return mPlay;
    }
}