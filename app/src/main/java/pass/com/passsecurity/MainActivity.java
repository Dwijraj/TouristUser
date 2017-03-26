package pass.com.passsecurity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import mohitbadwal.rxconnect.RxConnect;
import pass.com.passsecurity.Constants.Constants;

public class MainActivity extends AppCompatActivity {

    private Button FETCHING;
    private Button SCAN;
  //  private Button SIGN_OUT;
    private EditText Pass;
    private RxConnect rxConnect;
    private SharedPreferences REGISTERED_SECURITY;
    private String REGISTERED_MOBILE_NUBER;
    private String REGISTERED_GAURD_NAME;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        REGISTERED_SECURITY=getSharedPreferences(Constants.USER,MODE_PRIVATE);
        REGISTERED_MOBILE_NUBER=REGISTERED_SECURITY.getString(Constants.SHARED_PREF_KEY,"DEFAULT");
        REGISTERED_GAURD_NAME=REGISTERED_SECURITY.getString(Constants.SHARED_PREF_KEY_NAME,"DEFAULT");

        rxConnect=new RxConnect(this);
        rxConnect.setCachingEnabled(false);

        FETCHING=(Button)findViewById(R.id.Submit);
        Pass=(EditText)findViewById(R.id.PASS_NUMBER);
       // SIGN_OUT=(Button)findViewById(R.id.Sign_out);
        SCAN=(Button)findViewById(R.id.scan);

        SCAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new IntentIntegrator(MainActivity.this).initiateScan();


            }
        });



        FETCHING.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!Pass.getText().toString().trim().isEmpty())
                {
                    getDetails(Pass.getText().toString().trim());

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

               getDetails(result.getContents());


                }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void getDetails(String PassNumber)
    {
        rxConnect.setParam("token_id",PassNumber);
        rxConnect.setParam("gaurd_name",REGISTERED_GAURD_NAME);
        rxConnect.setParam("user_mobile",REGISTERED_MOBILE_NUBER);
        rxConnect.setParam("user","security");

        rxConnect.execute(Constants.PASS_RETREIVE_URL, RxConnect.POST, new RxConnect.RxResultHelper() {
            @Override
            public void onResult(String result) {

                try {
                    Log.v("ResponseViewPass",result);

                    JSONObject jsonObject=new JSONObject(result);

                    if(jsonObject.getString("response_status").equals("1"))///*&&(jsonObject.getString("mobile").equals(REGISTERED_MOBILE_NUBER))||jsonObject.getString("mobile").equals(REGISTERED_MOBILE_NUBER)*/)
                    {

                        Intent I=new Intent(MainActivity.this,Display.class);
                        I.putExtra("Pass",Pass.getText().toString().trim());
                        I.putExtra("PassDetails",result);
                        startActivity(I);
                    }
                    else if(jsonObject.getString("response_status").equals("3"))
                    {
                        Toast.makeText(getApplicationContext(),"No Such application exists",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"You are not authorized to view this pass",Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
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
