package ravis.bloodaid.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import ravis.bloodaid.R;
import ravis.bloodaid.app.AppConfig;
import ravis.bloodaid.app.AppController;
import ravis.bloodaid.helper.SQLiteHandler;
import ravis.bloodaid.helper.SessionManager;

public class ForgotpassActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btngetpass;
    private Button btnLinkToLoginScreen;
    private EditText inputEmail;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpass);

        inputEmail = (EditText) findViewById(R.id.email);
        btngetpass = (Button) findViewById(R.id.btngetpass);
        btnLinkToLoginScreen = (Button) findViewById(R.id.btnLinkToLoginScreen);


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        btngetpass.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                if (!email.isEmpty()) {
                    if(isValidEmail(email)) {
                        sendPassword(email);
                    }else{
                        Toast.makeText(getApplicationContext(),
                                "Please enter email in correct format!", Toast.LENGTH_LONG)
                                .show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }
            private boolean isValidEmail(CharSequence email) {
                return Patterns.EMAIL_ADDRESS.matcher(email).matches();
            }
        });
        btnLinkToLoginScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(),
                LoginActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();
    }

    private void sendPassword(final String email) {
        String tag_string_req = "req_login";
        pDialog.setMessage("Sending Password to registered email...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_FORGETPASS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        Intent intent = new Intent(ForgotpassActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}

