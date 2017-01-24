package pass.com.passsecurity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class sign_In_Activity extends AppCompatActivity {




    private EditText GATE_ID_NUMBER;
    private EditText GATE_PASSWORD;
    private TextView Phone;
    private TextView Name;
    private String gate_id;
    private Button ID_SCAN_;
    private String gate_password;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthlistener;
    private Button buttons;
    private TelephonyManager telephonyManager;                          //TelephonyManager Object to help fetch IMEI of the mobile
    private ProgressDialog prog;
    private DatabaseReference mDatabaseref;
    private DatabaseReference mGateRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign__in_);

        ID_SCAN_=(Button)findViewById(R.id.ID_SCAN);
        mGateRef=FirebaseDatabase.getInstance().getReference().child("Gate");
        GATE_ID_NUMBER=(EditText)findViewById(R.id.GATE_ID);
        GATE_PASSWORD=(EditText)findViewById(R.id.GATE_PASSWORD);
        Name=(TextView)findViewById(R.id.editText4);
        Phone=(TextView)findViewById(R.id.editText3);
        prog=new ProgressDialog(this);
        buttons=(Button)findViewById(R.id.button);
        mDatabaseref= FirebaseDatabase.getInstance().getReference();   //Points to the Users child  of the root parent

        // telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);  //Telephony manager object is initiated
        mAuth=FirebaseAuth.getInstance();                           //Firebase Auth instance
        mAuthlistener=new FirebaseAuth.AuthStateListener() {        //Firebase Auth Listener that checks if the user if logged in
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth!=null)
                {
                    buttons.setVisibility(View.VISIBLE);
                    ID_SCAN_.setVisibility(View.VISIBLE);
                }

            }
        };

        mAuth.signInWithEmailAndPassword("admin@admin.com","Admin123").addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {


                buttons.setVisibility(View.VISIBLE);
                ID_SCAN_.setVisibility(View.VISIBLE);

            }
        });



        ID_SCAN_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new IntentIntegrator(sign_In_Activity.this).initiateScan();

            }
        });




        buttons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!(Phone.getText().toString().isEmpty() && Name.getText().toString().isEmpty()&&GATE_ID_NUMBER.getText().toString().isEmpty()
                        &&GATE_PASSWORD.getText().toString().isEmpty())) {            //makes sure that user enter his/her phone number
                    prog.setMessage("Signing you..");



                    Log.v("Testing1","Test");

                    final String IMEI = Phone.getText().toString().trim();
                    //  final String IMEI = "455567888344432";
                    final String EMAIL = IMEI + "@" + IMEI + ".com";
                    final String PASSWORD = IMEI;
                    gate_id=GATE_ID_NUMBER.getText().toString().trim();
                    gate_password=GATE_PASSWORD.getText().toString().trim();
                    //   Toast.makeText(getApplicationContext(),IMEI,Toast.LENGTH_SHORT).show();


                    Log.v("Testing1","Test1");

                     prog.show();


                    mAuth.signInWithEmailAndPassword(EMAIL, PASSWORD).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {




                            mGateRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {



                                    if(dataSnapshot.hasChild(gate_id))
                                    {


                                        Log.v("Testing1","Test2");
                                        mGateRef.child(gate_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                String  PASSWORD_GATE=dataSnapshot.getValue(String.class);

                                                if(PASSWORD_GATE.equals(gate_password))
                                                {

                                                    Log.v("Testing1","Test3");
                                                    //  prog.show();

                                                    prog.dismiss();

                                                    Toast.makeText(getApplicationContext(), "Signed in successful", Toast.LENGTH_SHORT).show();

                                                    Intent Apply = new Intent(sign_In_Activity.this, MainActivity.class);
                                                    Apply.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    finish();
                                                    startActivity(Apply);




                                                }
                                                else
                                                {
                                                    prog.dismiss();
                                                    Toast.makeText(getApplicationContext(),"Wrong Gate Password",Toast.LENGTH_SHORT).show();
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                    else
                                    {

                                        prog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Wrong Gate Id",Toast.LENGTH_SHORT).show();
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                                /*On successfully signing into the account the user is prompted to the apply pass activity*/



                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            prog.dismiss();
                            Toast.makeText(getApplicationContext(),"Couldn't sign you up please check internet settings", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Enter Phone number...",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
       /* if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {

                final String GAURD_ID=result.getContents();

                mDatabaseref.child("Guards").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(GAURD_ID))
                        {
                            mDatabaseref.child("Guards").child(GAURD_ID).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    Gaurd_Details GD=dataSnapshot.getValue(Gaurd_Details.class);

                                    Name.setText(GD.Name);
                                    Phone.setText(GD.Contact);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }*/


        if(result != null) {
            final String GAURD_ID="8hu9uJK5pqS3OwUqjZqGbbz8ESr2";

            mDatabaseref.child("Guards").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(GAURD_ID))
                    {
                        mDatabaseref.child("Guards").child(GAURD_ID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Gaurd_Details GD=dataSnapshot.getValue(Gaurd_Details.class);

                                Name.setText(GD.Name);
                                Phone.setText(GD.Contact);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
