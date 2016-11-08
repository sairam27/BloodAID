package ravis.bloodaid.activity;

import ravis.bloodaid.MainActivity;
import ravis.bloodaid.R;
import ravis.bloodaid.app.AppConfig;
import ravis.bloodaid.app.AppController;
import ravis.bloodaid.fragment.HomeFragment;
import ravis.bloodaid.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static ravis.bloodaid.R.id.view;

public class Outview1 extends Activity {

    private static String TAG = Outview1.class.getSimpleName();

    // Progress dialog
    private ProgressDialog pDialog;
    private SQLiteHandler db;

    private ListView lv;
    ArrayList<HashMap<String, String>> contactList;

    @Override
    public void onBackPressed() {
            Intent i = new Intent(getApplicationContext(),
                    MainActivity.class);
            startActivity(i);
            finish();
        super.onBackPressed();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outview1);
        db = new SQLiteHandler(getApplicationContext());
        contactList = new ArrayList<>();


        lv = (ListView) findViewById(R.id.list);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

       String spinner = HomeFragment.bloodgroup();

       makeJsonObjectRequest(spinner);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String email= getemail(position);
                Location(email);

                notification(email,position);

            }
        });
    }


    public void Location(final String email){
        String tag_string_req = "req_Location";

        pDialog.setMessage("Getting Location ...");
        showpDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_Location, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        JSONObject user = jObj.getJSONObject("user");
                        String latitude = user.getString("latitude");
                        String longitude = user.getString("longitude");
                        db.storelocation(latitude,longitude);
                        Intent intent = new Intent(Outview1.this, MapsActivity2.class);
                        startActivity(intent);
                        finish();

                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public String getemail(int position) {
        View childView = lv.getChildAt(position);
        TextView text = (TextView) childView.findViewById(R.id.email);
        String email = text.getText().toString().trim();
        return email;
    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    private void makeJsonObjectRequest(final String spinner) {
        String tag_string_req = "req_register";

        showpDialog();
        //making call to url
        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_SEARCH, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hidepDialog();
                try {
                    JSONArray array = new JSONArray(response);
                        for(int i=0;i<array.length();i++) {

                            JSONObject user = array.getJSONObject(i);
                            String name = user.getString("name");
                            String email = user.getString("email");
                            String mobile = user.getString("mobile");
                            String spinner = user.getString("spinner");

                            HashMap<String, String> contact = new HashMap<>();

                            // adding each child node to HashMap key => value

                            contact.put("name", name);
                            contact.put("email", email);
                            contact.put("mobile", mobile);
                            contact.put("spinner", spinner);

                            // adding contact to contact list
                            contactList.add(contact);
                            ListAdapter adapter = new SimpleAdapter(
                                    Outview1.this, contactList,
                                    R.layout.list_item, new String[]{"name", "email",
                                    "mobile", "spinner"}, new int[]{R.id.name,
                                    R.id.email, R.id.mobile, R.id.spinner});

                            lv.setAdapter(adapter);
                            Toast.makeText(getApplicationContext(),
                                    "Click on entity for getting location", Toast.LENGTH_SHORT).show();
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("spinner", spinner);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


        /**
         * Updating parsed JSON data into ListView
         * */

    }


    public String getspinner(int paramInt)
    {
        return ((TextView)this.lv.getChildAt(paramInt).findViewById(R.id.spinner)).getText().toString().trim();
    }

    public void notification(final String paramString, int paramInt)
    {
        String tag_string_req = "snd..notification";
        final String str1 = getspinner(paramInt);
        final Object localObject = this.db.getUserDetails();
        final String str2 = (String)((HashMap)localObject).get("name");
        final String str3 = (String)((HashMap)localObject).get("email");
        final String str4 = (String)((HashMap)localObject).get("mobile");
        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_NOTIFICATION, new Response.Listener<String>()
        {
            public void onResponse(String response)
            {
                Log.d(Outview1.TAG, "Login Response: " + response.toString());
                try
                {
                    JSONObject jObj = new JSONObject(response);
                    if (!jObj.getBoolean("error"))
                    {
                        Toast.makeText(Outview1.this.getApplicationContext(), "notification sent via email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    response = jObj.getString("error_msg");
                    Toast.makeText(Outview1.this.getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    return;
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Toast.makeText(Outview1.this.getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener()
    {
        public void onErrorResponse(VolleyError paramAnonymousVolleyError)
        {
            Log.e(Outview1.TAG, "Login Error: " + paramAnonymousVolleyError.getMessage());
            Toast.makeText(Outview1.this.getApplicationContext(), paramAnonymousVolleyError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    })
        {
            protected Map<String, String> getParams()
            {
                HashMap localHashMap = new HashMap();
                localHashMap.put("email", paramString);
                localHashMap.put("spinner", str1);
                localHashMap.put("username", str2);
                localHashMap.put("useremail", str3);
                localHashMap.put("usermobile", str4);
                return localHashMap;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req );
    }
}