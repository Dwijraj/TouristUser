package pass.com.passsecurity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mohitbadwal.rxconnect.RxConnect;
import pass.com.passsecurity.Constants.Constants;
import pass.com.passsecurity.Utils.JSONParser;

public class Display extends AppCompatActivity {

    private ImageView scan_id2;
    private TextView Name2;
    private TextView Address2;
    private TextView Mobile2;
    private TextView CarNumber;
    private TextView DriverName;
    private TextView Dateofbirth2;
    private TextView Dateofjourney2;
    private TextView DESTINATION;
    private TextView ID_No2;
    private TextView Purpose2;
    private TextView Scan_id2;
    private ImageView Profile2;
    private  String pass;
    private TextView ID_Sources;
    private TextView Application_status2;
   private TextView GATE_NUMBER;
    private int WIDTH_SCREEN;
    private String APPLICATION_STATUS;
    private int HEIGHT_SCREEN;
    private boolean Check;
    private RxConnect rxConnect;
    private Button RETRY_BUTTON;
    private String PassDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        RETRY_BUTTON=(Button)findViewById(R.id.RETRY);
        rxConnect=new RxConnect(this);
        rxConnect.setCachingEnabled(false);
        GATE_NUMBER=(TextView) findViewById(R.id.GATE);
        DESTINATION=(TextView)findViewById(R.id.DESTINATION);
        CarNumber=(TextView)findViewById(R.id.car_num);
        DriverName=(TextView)findViewById(R.id.driver_name);
        scan_id2=(ImageView)findViewById(R.id.SCAN_PIC) ;
        Name2=(TextView)findViewById(R.id.SCAN_NAME);
        Address2=(TextView)findViewById(R.id.SCAN_ADDRESS);
        Mobile2=(TextView)findViewById(R.id.SCAN_MOBILE);
        ID_No2=(TextView)findViewById(R.id.SCAN_ID);
        ID_Sources=(TextView)findViewById(R.id.ID_Source);

        Dateofbirth2=(TextView)findViewById(R.id.SCAN_DOB);
        Dateofjourney2=(TextView)findViewById(R.id.SCAN_DOJ);
        Purpose2=(TextView)findViewById(R.id.SCAN_REASON);
        Profile2=(ImageView)findViewById(R.id.SCAN_PROFILE);
        Application_status2=(TextView)findViewById(R.id.SCAN_STATUS);
        Intent i=getIntent();
        Bundle extras=i.getExtras();
        pass=extras.getString("Pass");
        PassDetails=extras.getString("PassDetails");


        WindowManager wm = (WindowManager) Display.this.getSystemService(Context.WINDOW_SERVICE);
        android.view.Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        WIDTH_SCREEN = metrics.widthPixels;
        HEIGHT_SCREEN = metrics.heightPixels;


        android.view.ViewGroup.LayoutParams layoutParams = Profile2.getLayoutParams();
        layoutParams.width = WIDTH_SCREEN/2;
        layoutParams.height = HEIGHT_SCREEN/3;
        Profile2.setLayoutParams(layoutParams);

        android.view.ViewGroup.LayoutParams layoutParamss = scan_id2.getLayoutParams();
        layoutParamss.width = WIDTH_SCREEN/2;
        layoutParamss.height = HEIGHT_SCREEN/3;
        scan_id2.setLayoutParams(layoutParamss);


        try {

            JSONObject jsonObject2=new JSONObject(PassDetails);

            JSONObject jsonObject=jsonObject2.getJSONObject("application_info");



            String Name= JSONParser.JSONValue(jsonObject,"applicant_name");

            String Address= JSONParser.JSONValue(jsonObject,"applicant_address");


            String PlaceOfVisit= JSONParser.JSONValue(jsonObject,"place_visting");


            String Mobile= JSONParser.JSONValue(jsonObject,"application_mobile");


            String IDNumber= JSONParser.JSONValue(jsonObject,"applicant_id_no");


            String IDSource= JSONParser.JSONValue(jsonObject,"applicant_id_source");


            String DateOfBirth= JSONParser.JSONValue(jsonObject,"dob");


            String Purpose= JSONParser.JSONValue(jsonObject,"purpose_visting");


            String DateOfJourney= JSONParser.JSONValue(jsonObject,"date_journey");


            String Res= JSONParser.JSONValue(jsonObject,"Photo");

            String Profile = Res.replaceAll("\"","");


            String ResId= JSONParser.JSONValue(jsonObject,"Scan_id_photo");

            String ScanId=ResId.replaceAll("\"","");


            String ApplicationStatus=JSONParser.JSONValue(jsonObject,"paid_status");
            APPLICATION_STATUS=ApplicationStatus;



            Name2.setText(Name);
            Address2.setText(Address);
            Mobile2.setText(Mobile);
            ID_No2.setText(IDNumber);
            Dateofbirth2.setText(DateOfBirth);
            Dateofjourney2.setText(DateOfJourney);
            Purpose2.setText(Purpose);
            Application_status2.setText(ApplicationStatus.toUpperCase());
            ID_Sources.setText(IDSource);
            DESTINATION.setText(PlaceOfVisit);


            Glide.with(getApplicationContext())
                    .load(Profile)
                    .into(Profile2);


            Glide.with(getApplicationContext())
                    .load(ScanId)
                    .into(scan_id2);

           changeApplicationStatus();

            RETRY_BUTTON.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeApplicationStatus();
                }
            });



            
            

        }catch (JSONException e)
        {

        }




    }
    public void changeApplicationStatus()
    {
        rxConnect.setParam("token_id",pass);
        rxConnect.setParam("security_number",
                getSharedPreferences(Constants.USER,MODE_PRIVATE).getString(Constants.SHARED_PREF_KEY,"DEFAULT"));
        rxConnect.setParam("application_status",APPLICATION_STATUS);
        rxConnect.setParam("security_name",
                getSharedPreferences(Constants.USER,MODE_PRIVATE).getString(Constants.SHARED_PREF_KEY_NAME,"DEFAULT"));
        rxConnect.setParam("check_time",new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis())));
        rxConnect.execute(Constants.PASS_STATUS_CHANGE_URL, RxConnect.POST, new RxConnect.RxResultHelper() {
            @Override
            public void onResult(String result) {

                try {
                    JSONObject jsonObject=new JSONObject(result);
                   if(jsonObject.getString("response_status").equals("1"))
                   {
                       RETRY_BUTTON.setEnabled(false);
                       Toast.makeText(getApplicationContext(),"Application status updated",Toast.LENGTH_SHORT).show();

                   }
                   else if (jsonObject.getString("response_status").equals("2"))
                   {
                       Toast.makeText(getApplicationContext(),"Status couldn't be changed",Toast.LENGTH_SHORT).show();
                   }


                }catch (Exception e)
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
