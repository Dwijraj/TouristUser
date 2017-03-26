package pass.com.passsecurity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import mohitbadwal.rxconnect.RxConnect;
import pass.com.passsecurity.Constants.Constants;
import pass.com.passsecurity.Utils.JSONParser;

public class sign_In_Activity extends AppCompatActivity {




    private Button Login,Scan;
    private TextView GaurdName,GaurdPhone;
    private EditText GateNumber,GatePassword;
    private RxConnect rxConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign__in_);
        rxConnect=new RxConnect(this);
        rxConnect.setCachingEnabled(false);


        Login=(Button) findViewById(R.id.login);
        Login.setEnabled(false);
        Scan=(Button) findViewById(R.id.ID_SCAN);

        GaurdName=(TextView) findViewById(R.id.GaurdName);
        GaurdPhone=(TextView) findViewById(R.id.PhoneNumber);

        GateNumber=(EditText) findViewById(R.id.GATE_ID);
        GatePassword=(EditText) findViewById(R.id.GATE_PASSWORD);


        Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(sign_In_Activity.this).initiateScan();

            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String GateNumberVal=GateNumber.getText().toString().trim();
                String GatePasswordVal=GatePassword.getText().toString().trim();
                if(!(TextUtils.isEmpty(GateNumberVal)&&TextUtils.isEmpty(GatePasswordVal)))
                {
                    rxConnect.setParam("name",GaurdName.getText().toString().trim());
                    rxConnect.setParam("phone_number",GaurdPhone.getText().toString().trim());
                    rxConnect.setParam("gate_number",GateNumberVal);
                    rxConnect.setParam("gate_password",GatePasswordVal);
                    rxConnect.execute(Constants.GAURD_LOGIN_URL, RxConnect.POST, new RxConnect.RxResultHelper() {
                        @Override
                        public void onResult(String result) {

                            try
                            {
                                JSONObject jsonObject=new JSONObject(result);

                                if(jsonObject.getString("response_status").equals("1"))///*&&(jsonObject.getString("mobile").equals(REGISTERED_MOBILE_NUBER))||jsonObject.getString("mobile").equals(REGISTERED_MOBILE_NUBER)*/)
                                {
                                    SharedPreferences.Editor Editor=getSharedPreferences(Constants.USER,MODE_PRIVATE).edit();
                                    Editor.putString(Constants.SHARED_PREF_KEY,GaurdPhone.getText().toString().trim());
                                    Editor.putString(Constants.SHARED_PREF_KEY_NAME,GaurdName.getText().toString().trim());
                                    Editor.commit();

                                    Intent i=new Intent(sign_In_Activity.this,MainActivity.class);
                                    finish();
                                    startActivity(i);

                                }
                                else if(jsonObject.getString("response_status").equals("3"))
                                {
                                    Toast.makeText(getApplicationContext(),"Gaurd Not Registered",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    //   Toast.makeText(getApplicationContext(),"You are not authorized to view this pass",Toast.LENGTH_SHORT).show();
                                }

                            }catch (JSONException e)
                            {

                            }

                        }

                        @Override
                        public void onNoResult() {

                        }

                        @Override
                        public void onError(Throwable throwable) {

                        }
                    });

                }

            }
        });



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                // Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                // Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                String RESULT=result.getContents();
                rxConnect.setParam("Security_Gaurd_Id",RESULT);
                rxConnect.execute(Constants.GAURD_DETAILS_URL, RxConnect.POST, new RxConnect.RxResultHelper() {
                    @Override
                    public void onResult(String result) {

                        try
                        {
                            JSONObject jsonObject=new JSONObject(result);

                            if(jsonObject.getString("response_status").equals("1"))///*&&(jsonObject.getString("mobile").equals(REGISTERED_MOBILE_NUBER))||jsonObject.getString("mobile").equals(REGISTERED_MOBILE_NUBER)*/)
                            {
                                JSONObject jsonObject1=jsonObject.getJSONObject("gaurd_info");

                                GaurdName.setText(JSONParser.JSONValue(jsonObject1,"name"));
                                GaurdPhone.setText(JSONParser.JSONValue(jsonObject1,"phone_number"));

                                Login.setEnabled(true);

                            }
                            else if(jsonObject.getString("response_status").equals("3"))
                            {
                                Toast.makeText(getApplicationContext(),"Gaurd Not Registered",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                             //   Toast.makeText(getApplicationContext(),"You are not authorized to view this pass",Toast.LENGTH_SHORT).show();
                            }

                        }catch (JSONException e)
                        {

                        }
                    }

                    @Override
                    public void onNoResult() {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });



            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
