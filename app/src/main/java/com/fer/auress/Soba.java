package com.fer.auress;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

class Odgovor {
    public String value;
    public int color;

    public Odgovor(String val, int c){
        value=val;
        color=c;
    }
}

public class Soba extends AppCompatActivity {

    private final String Answers = "Answers:";
    private String text = "";
    private int brojOdgovora;
    private HashMap<Integer,Odgovor> slova;
    private String roomIdString = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soba);
        slova = slova = new HashMap<Integer,Odgovor>();

        slova.put(0,new Odgovor("A", getResources().getColor(R.color.A)));
        slova.put(1,new Odgovor("B", getResources().getColor(R.color.B)));
        slova.put(2,new Odgovor("C", getResources().getColor(R.color.C)));
        slova.put(3,new Odgovor("D", getResources().getColor(R.color.D)));
        slova.put(4,new Odgovor("E", getResources().getColor(R.color.E)));
        slova.put(5,new Odgovor("F", getResources().getColor(R.color.F)));
        slova.put(6,new Odgovor("G", getResources().getColor(R.color.G)));
        String osoba =null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            brojOdgovora = extras.getInt("brojOdgovora");
            roomIdString = extras.getString("roomId");
            osoba = extras.getString("korisnik");
        } else {
            Toast.makeText(getApplicationContext(), "Greška", Toast.LENGTH_SHORT).show();
        }
        dodajSucelje();
        if(osoba!=null){
            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            DodajOdgovore(osoba,today.hour+":"+today.hour+":"+today.second);
        }
    }

    private void provjeriBrojOdogoora(String resultHtml){
        int odgovori = Utils.count(resultHtml,"\"odgovor\"");

        if(odgovori==0){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        if(brojOdgovora!=odgovori){
            brojOdgovora=odgovori;
            dodajSucelje();
        }
    }

    private void dodajSucelje() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.OuterLayout);
        ll.removeAllViews();
        ll.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10,10,10,10);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params2.setMargins(10,10,10,10);


        TextView roomId = new TextView(getApplicationContext());
        roomId.setTextSize(24);
        roomId.setPadding(100,0,0,0);
        roomId.setTextColor(Color.WHITE);
        roomId.setGravity(View.TEXT_ALIGNMENT_CENTER);
        roomId.setText("Soba: "+roomIdString);
        roomId.setLayoutParams(params);

        ll.addView(roomId);

        int brojRedaka = brojOdgovora/2 + brojOdgovora%2;
        LinearLayout[] hll = new LinearLayout[brojRedaka];

        for(int i = 0; i<brojRedaka;i++){
            LinearLayout hl = new LinearLayout(getApplicationContext());
            hl.setOrientation(LinearLayout.HORIZONTAL);
            hll[i] = hl;
            ll.addView(hl);
        }

        int j = 0;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        for (int i = 0; i < brojOdgovora; i++) { ;
            Button btn = new Button(getApplicationContext());
            btn.setHeight(50);
            btn.setWidth(width/2);
            btn.setText(slova.get(i).value);
            btn.setBackgroundColor(slova.get(i).color);
            btn.setLayoutParams(params2);
            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Button b = (Button)v;
                    final String buttonText = b.getText().toString();
                    Web.getInstance(getApplicationContext()).glasaj(buttonText, new VolleyCallback() {
                        @Override
                        public void onSuccessResponse(String result) {
                            Time today = new Time(Time.getCurrentTimezone());
                            today.setToNow();
                            provjeriBrojOdogoora(result);
                            DodajOdgovore(buttonText,today.hour+":"+today.hour+":"+today.second);
                        }
                    });
                }

            });
            hll[j].addView(btn);
            if(i%2==1){j++;}
        }

        final EditText tv = new EditText(getApplicationContext());
        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        tv.setTextColor(Color.WHITE);
        ll.addView(tv);
        Button btn = new Button(getApplicationContext());
        btn.setText("Send");
        btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Web.getInstance(getApplicationContext()).posaljiText(tv.getText().toString(), new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String result) {
                        Time today = new Time(Time.getCurrentTimezone());
                        today.setToNow();
                        provjeriBrojOdogoora(result);
                        DodajOdgovore(tv.getText().toString(),today.hour+":"+today.hour+":"+today.second);
                        tv.setText("");
                        tv.clearFocus();
                    }
                });
            }
        });
        ll.addView(btn);

        TextView tvOdgovori = new TextView(getApplicationContext());
        tvOdgovori.setLayoutParams(params);
        tvOdgovori.setId(R.id.Odgovori);
        tvOdgovori.setTextColor(Color.BLACK);
        tvOdgovori.setTextSize(13);
        tvOdgovori.setText(Answers+text);
        tvOdgovori.setBackgroundColor(Color.GRAY);
        ll.addView(tvOdgovori);
    }

    private void DodajOdgovore(String odgovor, String time){
        text += " "+odgovor+" ("+time+")";
        LinearLayout ll = (LinearLayout) findViewById(R.id.OuterLayout);
        TextView tv = (TextView) ll.findViewById(R.id.Odgovori);
        tv.setText(Answers+text);
    }
}
