package com.fer.auress;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static boolean enteringRoom = false;
    private String lastJMBAG = "";
    private String lastRoomId = "";
    private ArrayList<String> Jmbags;
    private ArrayList<String> rooms;
    ArrayAdapter<String> roomIdAdapter;
    ArrayAdapter<String> JmbagAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    protected void onResume() {
        super.onResume();

        GetLastData();
        Jmbags = GetJmbags();
        rooms = GetRooms();

        JmbagAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, Jmbags);
        AutoCompleteTextView JmbagTextView = (AutoCompleteTextView)
                findViewById(R.id.JMBAG);
        JmbagTextView.setAdapter(JmbagAdapter);

        roomIdAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, rooms);

        AutoCompleteTextView RoomIdTextView = (AutoCompleteTextView)
                findViewById(R.id.RoomId);
        RoomIdTextView.setAdapter(roomIdAdapter);

        ((AutoCompleteTextView) findViewById(R.id.RoomId)).setText(lastRoomId);
        ((AutoCompleteTextView) findViewById(R.id.JMBAG)).setText(lastJMBAG);

    }

    public void EnterRoom(View v){
        CookieHandler.setDefault(new CookieManager());
        final String RoomId = ((EditText) findViewById(R.id.RoomId)).getText().toString();
       if(!enteringRoom){
            enteringRoom = true;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    enteringRoom = false;
                }
            },2000);
            Web.getInstance(getApplicationContext()).UdiUSobu(RoomId, new VolleyCallback() {
                @Override
                public void onSuccessResponse(String result) {
                    EnterRoomSuccess(Utils.count(result, "\"odgovor\""), result);
                }
            });
        }
    }

    public void EnterRoomSuccess(final int brojOdgovora, final String responseHtml){
        final String JMBAG = ((EditText) findViewById(R.id.JMBAG)).getText().toString();
        final String RoomId = ((EditText) findViewById(R.id.RoomId)).getText().toString();

        AddLastData(RoomId,JMBAG);
        AddRoomId(RoomId);
        if(!TextUtils.isEmpty(JMBAG)) {
            AddJmbag(JMBAG);
            Web.getInstance(getApplicationContext()).posaljiText("JMBAG=" + JMBAG, new VolleyCallback() {
                @Override
                public void onSuccessResponse(String result) {
                    UdiUSobu(responseHtml,result,JMBAG,brojOdgovora,RoomId);
                }
            });
        }else {
            UdiUSobu(responseHtml, "", "", brojOdgovora,RoomId);
        }
    }

    private void UdiUSobu(String roomIdHtml,String JMBAGHtml,String JMBAG, int brojOdgovora, String RoomId){
        boolean validanRoomId = ProvjeriRoomId(brojOdgovora,roomIdHtml);
        boolean validanJmbag = ProvjeriJmbag(JMBAGHtml);

        if(!validanRoomId){
            Toast.makeText(getApplicationContext(), "Krivi ID sobe", Toast.LENGTH_LONG).show();
            return;
        }

        if(!validanJmbag && !TextUtils.isEmpty(JMBAG)){
            Toast.makeText(getApplicationContext(), "Krivi JMBAG", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, Soba.class);
        intent.putExtra("brojOdgovora",brojOdgovora);
        intent.putExtra("roomId",RoomId);
        if(!TextUtils.isEmpty(JMBAG)) {
            String ImePrezime = GetImePrezime(JMBAGHtml);
            intent.putExtra("korisnik", JMBAG + " " + ImePrezime);
        }
        startActivity(intent);
    }


    private boolean ProvjeriRoomId(int brojOdgovora, String html){

        return brojOdgovora>0 && html.toLowerCase().contains("posaljiporuku");
    }
    //Odredi verziju web app
    //ako podrzava validaciju provjeri jeli student validiran
    private boolean ProvjeriJmbag(String html){
        if(html.contains("User unidentified")) {
            return false;
        }else if(html.contains("Verified as")){
            return  true;
        }else{
            return  true;
        }
    }
    //zgrabi ime i prezime iz htmla
    private String GetImePrezime(String html){
        String htmlLower = html.toLowerCase();
        int start = htmlLower.indexOf("verified as:");
        if(start<0) return "";
        int end = htmlLower.substring(start).indexOf("</div>");
        if(end<0) return "";
        if(start+7<(end+start)) {
            return html.substring(start + 7, end+start);
        }else {
            return "";
        }
    }
    private void AddJmbag(String jmbag){

        if(!Jmbags.contains(jmbag)) {
            Jmbags.add(jmbag);
            JmbagAdapter.add(jmbag);
            JmbagAdapter.notifyDataSetChanged();

            File jmbagsFile = new File(getApplicationContext().getFilesDir(), "JMBAG");

            try {
                if(!jmbagsFile.exists()){
                    jmbagsFile.createNewFile();
                }

                FileWriter fw = new FileWriter(jmbagsFile);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(jmbag);
                bw.newLine();
                bw.close();
                fw.close();
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "JMBAG autocomplete greška", Toast.LENGTH_SHORT);
            }
        }
    }

    private void AddRoomId(String roomId){
        if(!rooms.contains(roomId)) {
            rooms.add(roomId);
            roomIdAdapter.add(roomId);
            roomIdAdapter.notifyDataSetChanged();

            File roomsFile = new File(getApplicationContext().getFilesDir(), "rooms");

            try {
                if(!roomsFile.exists()){
                    roomsFile.createNewFile();
                }

                FileWriter fw = new FileWriter(roomsFile);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(roomId);
                bw.newLine();
                bw.close();
                fw.close();
            } catch (Exception ex) {
                Log.d("Error",ex.getMessage());
                Toast.makeText(getApplicationContext(), "Greška autocompleta sobe", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ArrayList<String> GetJmbags(){
        ArrayList<String> Jmbags = new ArrayList<>();
        File jmbagsFile = new File(getApplicationContext().getFilesDir(), "JMBAG");
        if(!jmbagsFile.exists()){
            return new ArrayList<>();
        }

        try{
            FileReader fr = new FileReader(jmbagsFile);
            BufferedReader br = new BufferedReader(fr);
            String line= br.readLine();
            while (line!=null){
                Jmbags.add(line);
                line= br.readLine();
            }
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(),"Greška autocompleta JMBAGa",Toast.LENGTH_SHORT).show();
        }
        return Jmbags;
    }

    private ArrayList<String> GetRooms(){
        File roomsFile = new File(getApplicationContext().getFilesDir(), "rooms");
        Log.d("postojanje", roomsFile.exists()?"da":"ne");
        if(!roomsFile.exists()){
            return new ArrayList<>();
        }

        ArrayList<String> rooms = new ArrayList<>();
        try{
            FileReader fr = new FileReader(roomsFile);
            BufferedReader br = new BufferedReader(fr);
            String line= br.readLine();

            while (line!=null){
                rooms.add(line);
                line= br.readLine();
            }
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(),"Greška autocompleta JMBAGa",Toast.LENGTH_SHORT).show();
        }
        return rooms;
    }

    private void GetLastData(){
        File LastDataFile = new File(getApplicationContext().getFilesDir(), "LastData");

        if(!LastDataFile.exists()){
            return;
        }

        try{
            FileReader fr = new FileReader(LastDataFile);
            BufferedReader br = new BufferedReader(fr);
            String jmbag= br.readLine();
            String roomId = br.readLine();
            if(jmbag!=null){
                lastJMBAG = jmbag;
            }
            if(roomId!=null){
                lastRoomId = roomId;
            }
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(),"Autocomplete greška",Toast.LENGTH_SHORT);
        }
    }

    private void AddLastData(String roomId, String jmbag){

        File LastDataFile = new File(getApplicationContext().getFilesDir(), "LastData");

        try {

            if(!LastDataFile.exists()){
                LastDataFile.createNewFile();
            }

            FileWriter fw = new FileWriter(LastDataFile);
            fw.write("");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(jmbag);
            bw.newLine();
            bw.write(roomId);
            bw.newLine();
            bw.close();
            fw.close();
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(),"Autocomplete greška" +ex.getMessage(),Toast.LENGTH_SHORT);
        }
    }
}
