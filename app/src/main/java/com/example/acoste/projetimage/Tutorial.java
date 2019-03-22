package com.example.acoste.projetimage;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Tutorial extends AppCompatActivity {

    private Button button_main = null;
    private Button button_next = null;
    private Button button_previous = null;
    private String test_tuto_string = null;
    private TextView intro_tuto_textview = null;
    private TextView indication_tuto_textview = null;
    private ImageView imgView_left = null;
    private ImageView imgView_right = null;
    private int index = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);

        Intent ii = getIntent();
        Bundle b = ii.getExtras();

        if (b != null) {
            test_tuto_string = (String) b.get("code_string_main");
        }

        intro_tuto_textview = (TextView)findViewById(R.id.intro_tutorial);
        intro_tuto_textview.setText(getString(R.string.intro_tutorial));

        button_main = (Button) findViewById(R.id.menu_tutorial);
        button_main.setOnClickListener(listener_main);

        imgView_left = (ImageView)findViewById(R.id.imageView_left);
        imgView_left.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.test5));

        imgView_right = (ImageView)findViewById(R.id.imageView_right);
        imgView_right.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.legumesgrey));

        indication_tuto_textview = (TextView)findViewById(R.id.indication_tutorial);
        indication_tuto_textview.setText("Effet : Mise en niveau de gris (grey)");

        button_next = (Button) findViewById(R.id.next);
        button_next.setOnClickListener(listener_next);

        button_previous = (Button) findViewById(R.id.previous);
        button_previous.setOnClickListener(listener_previous);
    }

    private View.OnClickListener listener_next = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            index++;
            switch(index){
                case 0:
                    imgView_left.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.test5));
                    imgView_right.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.legumesgrey));
                    indication_tuto_textview.setText("Effet : Mise en niveau de gris (grey)");
                    break;
                case 1:
                    imgView_right.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.legumeskeepcolor));
                    indication_tuto_textview.setText("Effet : Mise en niveau de gris et conservation d'une couleur (keepColor)");
                    break;
                case 2:
                    imgView_left.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.test2));
                    imgView_right.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ville));
                    indication_tuto_textview.setText("Effet : Amélioration du contraste");
                    index = -1;
                    break;
                case 3:
                    index = 0;
                    imgView_left.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.test5));
                    imgView_right.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.legumesgrey));
                    indication_tuto_textview.setText("Effet : Mise en niveau de gris (grey)");
                    break;
            }

        }
    };

    private View.OnClickListener listener_previous = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            index--;
            switch(index){
                case -1:
                    index = 2;
                    imgView_left.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.test2));
                    imgView_right.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ville));
                    indication_tuto_textview.setText("Effet : Amélioration du contraste");
                    break;
                case 0:
                    imgView_right.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.legumesgrey));
                    indication_tuto_textview.setText("Effet : Mise en niveau de gris (grey)");
                    break;
                case 1:
                    imgView_left.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.test5));
                    imgView_right.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.legumeskeepcolor));
                    indication_tuto_textview.setText("Effet : Mise en niveau de gris et conservation d'une couleur (keepColor)");
                    break;
                case 2:
                    imgView_left.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.test2));
                    imgView_right.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ville));
                    indication_tuto_textview.setText("Effet : Amélioration du contraste");
                    break;
            }

        }
    };

    private View.OnClickListener listener_main = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(Tutorial.this, Main.class);

            startActivity(intent);
        }
    };
}
