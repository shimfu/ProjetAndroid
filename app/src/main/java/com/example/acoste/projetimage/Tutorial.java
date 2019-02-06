package com.example.acoste.projetimage;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Tutorial extends AppCompatActivity {

    private Button button_main = null;
    private String test_tuto_string = null;
    private TextView test_tuto_textview = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);

        Resources res = getResources();

        Intent ii = getIntent();
        Bundle b = ii.getExtras();


        if (b != null) {
            test_tuto_string = (String) b.get("code_string_main");
        }

        button_main = (Button) findViewById(R.id.menu_tutorial);
        button_main.setOnClickListener(listener_main);

        test_tuto_textview = (TextView) findViewById(R.id.test_tuto);
        test_tuto_textview.setText(test_tuto_string);
    }

    private View.OnClickListener listener_main = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(Tutorial.this, Main.class);

            startActivity(intent);
        }
    };
}
