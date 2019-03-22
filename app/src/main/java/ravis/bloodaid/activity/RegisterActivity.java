package ravis.bloodaid.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.widget.ArrayAdapter;
import android.widget.Spinner;


import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ravis.bloodaid.*;
import ravis.bloodaid.app.AppConfig;
import ravis.bloodaid.app.AppController;
import ravis.bloodaid.helper.SQLiteHandler;
import ravis.bloodaid.helper.SessionManager;

public class RegisterActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputmobile;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    private Spinner inputspinner;
    private EditText incpassword;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        incpassword = findViewById(R.id.cpassword);
        inputspinner = findViewById(R.id.spinner);
        inputFullName = findViewById(R.id.name);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        inputmobile = findViewById(R.id.mobile);
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnLinkToLogin = findViewById(R.id.btnLinkToLoginScreen);

        inputspinner.setOnItemSelectedListener(this);



        final List<String> categories = new ArrayList<>();
        categories.add("O+ve");
        categories.add("A+ve");
        categories.add("B+ve");
        categories.add("AB+ve");
        categories.add("0-ve");
        categories.add("A-ve");
        categories.add("B-ve");
        categories.add("AB-ve");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);


        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        inputspinner.setAdapter(dataAdapter);


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        SessionManager session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }



        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String mobile = inputmobile.getText().toString().trim();
                String spinner = inputspinner.getSelectedItem().toString();
                String cpassword = incpassword.getText().toString().trim();

                email=email.toLowerCase();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !mobile.isEmpty() && !spinner.isEmpty() && !cpassword.isEmpty()) {
                    if(isValidEmail(email)) {
                        if(password.length()>=8 && password.length()<=20) {
                            if (isPasswordMatching(password, cpassword)) {
                                if(mobile.length()>=10 && mobile.length()<=12) {
                                    if (mobile.matches("^[789]\\d{9}$")) {
                                        if (isPasswordMatch(password, mobile)) {
                                            registerUser(name, email, password, mobile, spinner);
                                        } else {
                                            Toast.makeText(getApplicationContext(),
                                                    "Ur password is ur mobile number dude! COME ON...!", Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "enter a correct mobile number", Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(),
                                            "mobile number should be 10 digits", Toast.LENGTH_LONG)
                                            .show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "password and confirm password are different!", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),
                                    "Password length should be 8 characters", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),
                                "Please enter your email correctly", Toast.LENGTH_LONG)
                                .show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
            private boolean isValidEmail(CharSequence email) {
                return Patterns.EMAIL_ADDRESS.matcher(email).matches();
            }
            boolean isPasswordMatching(String password, String confirmPassword) {
                Pattern pattern = Pattern.compile(password, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(confirmPassword);

                return matcher.matches();
            }
            boolean isPasswordMatch(String password, String mobile) {
                Pattern pattern = Pattern.compile(password, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(mobile);

                return !matcher.matches();
            }


        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

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

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String name, final String email,
                              final String password, final String mobile, final String spinner) {
        // Tag used to cancel the request
    String tag_string_req = "req_register";

    pDialog.setMessage("Registering ...");
    showDialog();

    StringRequest strReq = new StringRequest(Method.POST,
            AppConfig.URL_REGISTER, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.d(TAG, "Register Response: " + response);
            hideDialog();

            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    // User successfully stored in MySQL
                    // Now store the user in sqlite
                    String uid = jObj.getString("uid");

                    JSONObject user = jObj.getJSONObject("user");
                    String name = user.getString("name");
                    String email = user.getString("email");
                    String created_at = user
                            .getString("created_at");
                    String mobile =user.getString("mobile");

                    // Inserting row in users table
                    db.addUser(name, email, mobile, uid, created_at);

                    Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                    // Launch login activity
                    Intent intent = new Intent(
                            RegisterActivity.this,
                            LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    // Error occurred in registration. Get the error
                    // message
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
            Toast.makeText(getApplicationContext(),
                    error.getMessage(), Toast.LENGTH_LONG).show();
            hideDialog();
        }
    }) {

        @Override
        protected Map<String, String> getParams() {
            // Posting params to register url
            Map<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("email", email);
            params.put("password", password);
            params.put("mobile", mobile);
            params.put("spinner",spinner);

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