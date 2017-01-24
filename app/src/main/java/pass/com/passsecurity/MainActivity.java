package pass.com.passsecurity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    private Button FETCHING;
    private FirebaseAuth mAuth;
    private Button SCAN;
    private Button SIGN_OUT;
    private EditText Pass;
    private AlertDialog alertDialog;
    private DatabaseReference Mstorageref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();


        FETCHING=(Button)findViewById(R.id.Submit);
        Pass=(EditText)findViewById(R.id.PASS_NUMBER);
        SIGN_OUT=(Button)findViewById(R.id.Sign_out);

        if(mAuth.getCurrentUser()==null || mAuth.getCurrentUser().getEmail().equals("admin@admin.com"))
        {
            Intent SIGN_IN=new Intent(MainActivity.this,sign_In_Activity.class);
            finish();
            startActivity(SIGN_IN);
        }
        SCAN=(Button)findViewById(R.id.scan);

        Mstorageref= FirebaseDatabase.getInstance().getReference().child("Applications");
        SCAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new IntentIntegrator(MainActivity.this).initiateScan();


            }
        });

        SIGN_OUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MainActivity.this);

                alertDialogBuilder.setTitle("Confirm sign out");
                alertDialogBuilder.setMessage("Are you sure you want to sign out?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        Intent NEW_SIGN= new Intent(getApplicationContext(),sign_In_Activity.class);
                        finish();
                        startActivity(NEW_SIGN);
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

        FETCHING.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!Pass.getText().toString().trim().isEmpty())
                {

                    Intent I=new Intent(MainActivity.this,Display.class);
                    I.putExtra("Pass",Pass.getText().toString().trim());

                    startActivity(I);
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
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                Intent I=new Intent(MainActivity.this,Display.class);
                I.putExtra("Pass",result.getContents());

                startActivity(I);


                }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
