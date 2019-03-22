package ravis.bloodaid.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ravis.bloodaid.R;
import ravis.bloodaid.activity.LoginActivity;
import ravis.bloodaid.activity.PasswordChangeActivity;
import ravis.bloodaid.app.AppConfig;
import ravis.bloodaid.app.AppController;
import ravis.bloodaid.helper.SQLiteHandler;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private EditText ename;
    private EditText eemail;
    private EditText emobile;

    private Button btnsave;
    private SQLiteHandler db;
    private ProgressDialog pDialog;

    private Button temp;


    private OnFragmentInteractionListener mListener;


    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ename= view.findViewById(R.id.name);
        eemail= view.findViewById(R.id.email);
        emobile= view.findViewById(R.id.mobile);
        ename.setEnabled(false);
        eemail.setEnabled(false);
        emobile.setEnabled(false);

        Button btnchangepass = view.findViewById(R.id.passchange);
        Button btnedit = view.findViewById(R.id.btnedit);
        btnsave = view.findViewById(R.id.savechanges);
        pDialog = new ProgressDialog(this.getActivity());
        pDialog.setCancelable(false);
        btnsave.setVisibility(View.GONE);
        temp = view.findViewById(R.id.buttontemp);

        db = new SQLiteHandler(getActivity().getApplicationContext());


        HashMap<String, String> user = db.getUserDetails();

        String prename = user.get("name");
        String preemail = user.get("email");
        String premobile = user.get("mobile");

        // Displaying the user details on the screen
        ename.setText(prename);
        eemail.setText(preemail);
        emobile.setText(premobile);

        btnedit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view1) {
                ename.setEnabled(true);
                eemail.setEnabled(true);
                emobile.setEnabled(true);
                btnsave.setVisibility(View.VISIBLE);
            }
        });
        temp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view1) {
                ename.setEnabled(false);
                eemail.setEnabled(false);
                emobile.setEnabled(false);
                btnsave.setVisibility(View.GONE);
            }
        });


        btnsave.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                HashMap<String, String> user = db.getUserDetails();
                String preemail = user.get("email");
                String prename = user.get("name");
                String premobile = user.get("mobile");
                String name = ename.getText().toString().trim();
                String email = eemail.getText().toString().trim();
                String mobile = emobile.getText().toString().trim();


                email=email.toLowerCase();

                if (!name.isEmpty() && !email.isEmpty() &&!mobile.isEmpty()) {
                    if(!name.equals(prename) || !email.equals(preemail) || !mobile.equals(premobile)) {
                        if (isValidEmail(email)) {
                            if (mobile.length() >= 10 && mobile.length() <= 12) {
                                if (mobile.matches("^[789]\\d{9}$")) {
                                    updateUser(preemail, name, email, mobile, premobile);
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "enter a correct mobile number", Toast.LENGTH_LONG)
                                            .show();
                                }
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "mobile number should be 10 digits", Toast.LENGTH_LONG)
                                        .show();
                            }
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Please enter your email correctly", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(),
                                "No changes were made broooo", Toast.LENGTH_LONG)
                                .show();
                        temp.performClick();
                    }
                }   else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
            private boolean isValidEmail(CharSequence email) {
                return Patterns.EMAIL_ADDRESS.matcher(email).matches();
            }
        });

        btnchangepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getApplicationContext(),PasswordChangeActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        return view;
    }


    private void updateUser(final String preemail, final String name, final String email, final String mobile, final  String premobile) {
        // Tag used to cancel the request
        String tag_string_req = "req_update";

        pDialog.setMessage("Updating ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_Update, new Response.Listener<String>() {
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

                        if(db.addUser(name, email, mobile, uid, created_at)) {
                            Toast.makeText(getActivity().getApplicationContext(), "successfully Updated Thank YOu!", Toast.LENGTH_LONG).show();
                        }
                        if(!preemail.equals(email) || !premobile.equals(mobile)){
                            Toast.makeText(getActivity().getApplicationContext(), "Login again", Toast.LENGTH_LONG).show();
                            db.deleteUsers();
                            HomeFragment.session(false);
                            Intent i = new Intent(getActivity().getApplicationContext(),
                                    LoginActivity.class);
                            startActivity(i);
                            getActivity().finish();
                        }

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity().getApplicationContext(),
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
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("preemail",preemail);
                params.put("name", name);
                params.put("email", email);
                params.put("mobile", mobile);
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


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
