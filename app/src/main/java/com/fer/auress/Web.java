package com.fer.auress;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public final class Web {

    private static Web web;
    private static Context context;
    RequestQueue requestQueue;
    String url ="https://www.auress.org/s/index.php";


    private Web(Context context) {
        Web.context = context;
        requestQueue = getRequestQueue();
    }


    public static synchronized Web getInstance(Context context) {
        if (web == null) {
            web = new Web(context);
        }
        return web;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public boolean UdiUSobu(final String idSobe, final VolleyCallback callback){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            callback.onSuccessResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String,String> getHeaders(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("DNT","1");
                params.put("Host","www.auress.org");
                params.put("User-Agent","Mozilla/5.0 (X11; Ubuntu; Linu…) Gecko/20100101 Firefox/63.0");
                params.put("Accept","text/html,application/xhtml+xm…plication/xml;q=0.9,*/*;q=0.8");
                params.put("Accept-Encoding","UTF-8, deflate, br");
                params.put("Accept-Language","en-GB,en;q=0.5");
                params.put("Upgrade-Insecure-Requests","1");
                params.put("Connection","keep-alive");
                return params;
            }

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("idSobe", idSobe);
                params.put("udiUSobu", "Start");

                return params;
            }
        };

        requestQueue.add(stringRequest);
        return true;
    }

    public boolean glasaj(final String odgovor, final VolleyCallback callback){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccessResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText(error.networkResponse.statusCode+" "/*+error.networkResponse.allHeaders.get(1)+error.networkResponse.allHeaders.get(2)+error.networkResponse.allHeaders.get(3)+error.networkResponse.allHeaders.get(4)*/);
            }
        }){
            @Override
            public Map<String,String> getHeaders(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("DNT","1");
                params.put("Host","www.auress.org");
                params.put("User-Agent","Mozilla/5.0 (X11; Ubuntu; Linu…) Gecko/20100101 Firefox/63.0");
                params.put("Accept","text/html,application/xhtml+xm…plication/xml;q=0.9,*/*;q=0.8");
                params.put("Accept-Encoding","UTF-8, deflate, br");
                params.put("Accept-Language","en-GB,en;q=0.5");
                params.put("Upgrade-Insecure-Requests","1");
                params.put("Connection","keep-alive");
                return params;
            }

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("odgovor", odgovor);
                params.put("browser", "Firefox");
                params.put("device", "Linux");
                return params;
            }
        };

        requestQueue.add(stringRequest);
        return true;
    }

    public boolean posaljiText(final String poruka, final VolleyCallback callback ){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccessResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText(error.networkResponse.statusCode+" "/*+error.networkResponse.allHeaders.get(1)+error.networkResponse.allHeaders.get(2)+error.networkResponse.allHeaders.get(3)+error.networkResponse.allHeaders.get(4)*/);
            }
        }){
            @Override
            public Map<String,String> getHeaders(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("DNT","1");
                params.put("Host","www.auress.org");
                params.put("User-Agent","Mozilla/5.0 (X11; Ubuntu; Linu…) Gecko/20100101 Firefox/63.0");
                params.put("Accept","text/html,application/xhtml+xm…plication/xml;q=0.9,*/*;q=0.8");
                params.put("Accept-Encoding","UTF-8, deflate, br");
                params.put("Accept-Language","en-GB,en;q=0.5");
                params.put("Upgrade-Insecure-Requests","1");
                params.put("Connection","keep-alive");
                return params;
            }

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("porukaStudenta", poruka);
                params.put("posaljiPoruku", "Send+text");
                return params;
            }
        };

        requestQueue.add(stringRequest);
        return true;
    }
}
