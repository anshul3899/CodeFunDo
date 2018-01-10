package com.harish.eat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.support.v7.app.AppCompatActivity;
import static android.R.attr.button;

/**
 * Created by ANSHUL YADAV on 08-01-2018.
 */

public class GameInstructions extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_instructions);

        TextView textView = (TextView) findViewById(R.id.instructions);
        Typeface arcadeTypeface = Typeface.createFromAsset(getAssets(), "fonts/arcade.ttf");
        textView.setTypeface(arcadeTypeface);
        TextView textView1 = (TextView) findViewById(R.id.instr);
        textView1.setTypeface(arcadeTypeface);


    }
}