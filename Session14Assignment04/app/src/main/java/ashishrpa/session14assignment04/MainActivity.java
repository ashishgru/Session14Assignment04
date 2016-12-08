package ashishrpa.session14assignment04;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_WRITE_CONTACTS = 100;
    ContentResolver contentResolver;
    Button buttonAdd, buttonViewAll;
    Intent intent;
    EditText ev_diag_contactName,ev_diag_contactNumber;
    String diag_contactName,diag_contactNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contentResolver = getContentResolver();
        buttonAdd = (Button) findViewById(R.id.buttonADD);
        buttonViewAll = (Button) findViewById(R.id.buttonViewAll);

        buttonAdd.setOnClickListener(this);
        buttonViewAll.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonADD:
                addItem();
                Toast.makeText(MainActivity.this,"buttonADD Clicked",Toast.LENGTH_LONG).show();
                break;
            case R.id.buttonViewAll:
                intent = new Intent(MainActivity.this,SecondActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this,"buttonViewAll Clicked",Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    public void addItem(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        alertDialog.setView(inflater.inflate(R.layout.dialog_contact, null))
                // Add action buttons
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog diag = (Dialog) dialog;
                        ev_diag_contactName = (EditText) diag.findViewById(R.id.diag_contact_name);
                        ev_diag_contactNumber = (EditText) diag.findViewById(R.id.diag_phone_number);


                        diag_contactName = ev_diag_contactName.getText().toString();
                        diag_contactNumber = ev_diag_contactNumber.getText().toString();

                        Log.e("addItem ",diag_contactName);
                        writeContacts();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }


    private void writeContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, PERMISSIONS_REQUEST_WRITE_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            insertContact(diag_contactName, diag_contactNumber);
        }
    }

    private void insertContact(String contactName,String contactNumber) {
        ContactHelper.insertContact(contentResolver,contactName,contactNumber);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_WRITE_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                insertContact(diag_contactName, diag_contactNumber);
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
