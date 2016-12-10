package pass.com.passsecurity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Display extends AppCompatActivity {

    private ImageView scan_id2;
    private TextView Name2;
    private TextView Address2;
    private Button Vehicle;
    private Button generatebarcode;
    private TextView Mobile2;
    private TextView Dateofbirth2;
    private TextView Dateofjourney2;
    private TextView Transaction_Id2;
    private TextView ID_No2;
    private TextView Purpose2;
    private TextView Scan_id2;
    private ImageView Profile2;
    private  String pass;
    private TextView Application_status2;
    private DatabaseReference ApplicationRef2;
    private DatabaseReference UsersRef;
    private FirebaseAuth mAuth;
    private  Application app;
    private boolean Check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        scan_id2=(ImageView)findViewById(R.id.SCAN_PIC) ;
        Name2=(TextView)findViewById(R.id.SCAN_NAME);
        Address2=(TextView)findViewById(R.id.SCAN_ADDRESS);
        Mobile2=(TextView)findViewById(R.id.SCAN_MOBILE);
        ID_No2=(TextView)findViewById(R.id.SCAN_ID);
        mAuth=FirebaseAuth.getInstance();
        Dateofbirth2=(TextView)findViewById(R.id.SCAN_DOB);
        Dateofjourney2=(TextView)findViewById(R.id.SCAN_DOJ);
        Purpose2=(TextView)findViewById(R.id.SCAN_REASON);
        Profile2=(ImageView)findViewById(R.id.SCAN_PROFILE);
        Application_status2=(TextView)findViewById(R.id.SCAN_STATUS);
        ApplicationRef2= FirebaseDatabase.getInstance().getReference().child("Applications");//Points to the root directory of the Database
        UsersRef=FirebaseDatabase.getInstance().getReference();

        Intent i=getIntent();
        Bundle extras=i.getExtras();
        pass=extras.getString("Pass");

        Log.v("Hello","Hello1");


        ApplicationRef2.child(pass).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                app=dataSnapshot.getValue(Application.class);  //App crasehs due to this line

                Name2.setText(app.Name);
                Address2.setText(app.Address);
                Mobile2.setText(app.Mobile);
                ID_No2.setText(app.ID_No);
                Dateofbirth2.setText(app.DateOfBirth);
                Dateofjourney2.setText(app.DateOfJourney);
                Purpose2.setText(app.Purpose);
                Application_status2.setText(app.ApplicationStatus.toUpperCase());



                Glide.with(getApplicationContext())
                        .load(app.ApplicantPhoto)
                        .into(Profile2);


                Glide.with(getApplicationContext())
                        .load(app.ApplicantScanId)
                        .into(scan_id2);


                Log.v("Maina5",app.ApplicationStatus);

                Check=app.ApplicationStatus.contains("Applied");

                Log.v("Maina6",String.valueOf(Check));

                if(app.ApplicationStatus.contains("Applied"))
                {
                    UsersRef.child("Guards").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid()))
                            {

                                UsersRef.child("Guards").child(mAuth.getCurrentUser().getUid()).child("Name").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshots) {

                                        Time today = new Time(Time.getCurrentTimezone());
                                        int Month=today.month+1;
                                       // ApplicationRef2.child(pass).child("ApplicationStatus").setValue("Checked by"+dataSnapshot.getValue(String.class)+"On"+today.monthDay + "-"+(String.valueOf(Month)) + "-"+today.year+" at "+);

                                        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                                        ApplicationRef2.child(pass).child("ApplicationStatus").setValue("Checked by "+dataSnapshots.getValue(String.class)+" on "+mydate);
                                        UsersRef.child("Users").child(app.Uid).child("Applications").child(pass).setValue("Checked by "+dataSnapshots.getValue(String.class)+" on "+mydate);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Application isn't verified ",Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                  //  ApplicationRef2.child(pass).child("ApplicationStatus");
                }
                else if(app.ApplicationStatus.contains("Checked by"))
                {
                    Toast.makeText(getApplicationContext(),"Application verified",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Application wasn't checked by guards ",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
}