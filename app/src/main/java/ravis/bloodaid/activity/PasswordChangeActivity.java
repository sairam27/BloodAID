package ravis.bloodaid.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ravis.bloodaid.MainActivity;
import ravis.bloodaid.R;
import ravis.bloodaid.app.AppConfig;
import ravis.bloodaid.app.AppController;
import ravis.bloodaid.fragment.HomeFragment;
import ravis.bloodaid.fragment.SettingsFragment;
import ravis.bloodaid.helper.SQLiteHandler;
import ravis.bloodaid.helper.SessionManager;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class PasswordChangeActivity extends Activity {
    private EditText fipass;
    private EditText sepass;
    private EditText trpass;
    private ProgressDialog pDialog;
    private Button btnsave;

    private SQLiteHandler db;
    private CheckBox mCbShowPwd;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_change);
        fipass = (EditText) findViewById(R.id.name);
        sepass = (EditText) findViewById(R.id.email);
        trpass = (EditText) findViewById(R.id.mobile);
        btnsave=(Button)findViewById(R.id.btnsave);


        db = new SQLiteHandler(getApplicationContext());

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        mCbShowPwd = (CheckBox) findViewById(R.id.cbShowPwd);

        mCbShowPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox status is changed from uncheck to checked.
                if (!isChecked) {
                    // show password
                    fipass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    sepass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    trpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    fipass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    sepass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    trpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String prepass = fipass.getText().toString().trim();
                String newpass = sepass.getText().toString().trim();
                String cpass = trpass.getText().toString().trim();
                HashMap<String, String> user = db.getUserDetails();
                String preemail = user.get("email");
                String mobile = user.get("mobile");

                if(!prepass.isEmpty() && !newpass.isEmpty() &&!cpass.isEmpty()){
                    if(newpass.length()>=8 && newpass.length()<=20) {
                        if(isPasswordMatching(prepass,newpass)) {
                            if (isPasswordMatching(newpass, cpass)) {
                                if (!isPasswordMatch(newpass, mobile)) {
                                    changeUserpass(preemail, prepass, newpass);
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Ur password is ur mobile number dude! COME ON...!", Toast.LENGTH_LONG)
                                            .show();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "password and confirm password are different!", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),
                                    "Previous password and new are same dude..!", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),
                                "Password length should be 8 characters", Toast.LENGTH_LONG)
                                .show();
                    }

                }else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }

            public boolean isPasswordMatching(String password, String confirmPassword) {
                Pattern pattern = Pattern.compile(password, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(confirmPassword);

                if (!matcher.matches()) {
                    // do your Toast("passwords are not matching");
                    return false;
                }
                return true;
            }
            public boolean isPasswordMatch(String password, String mobile) {
                Pattern pattern = Pattern.compile(password, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(mobile);

                if (!matcher.matches()) {
                    // do your Toast("passwords are not matching");
                    return false;
                }
                return true;
            }


        });

    }

    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();


    }

    private void changeUserpass(final String preemail, final String prepass, final String newpass) {
        // Tag used to cancel the request
        String tag_string_req = "req_Password_update";

        pDialog.setMessage("Updating Password...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_Updatepassword, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
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

                        if(db.addUser(name, email, mobile, uid, created_at)) {
                            Toast.makeText(getApplicationContext(), "successfully Updated Password!", Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), "Login again", Toast.LENGTH_LONG).show();
                            db.deleteUsers();
                            HomeFragment.session(false);
                            Intent i = new Intent(getApplicationContext(),
                                    LoginActivity.class);
                            startActivity(i);
                            finish();
                        }

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
                Map<String, String> params = new HashMap<String, String>();
                params.put("preemail",preemail);
                params.put("prepass", prepass);
                params.put("newpass", newpass);
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