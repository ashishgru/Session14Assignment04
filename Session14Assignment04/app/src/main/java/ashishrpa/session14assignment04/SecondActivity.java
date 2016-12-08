package ashishrpa.session14assignment04;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {
    EditText ev_diag_contactName,ev_diag_contactNumber;
    TextView tv_diag_contactName;
    //String diag_contactName,diag_contactNumber;
    String edit_contactName,edit_contactNumber,edit_contactId;
    MyAdapter myAdapter;
    int currentposition;
    boolean wantToDelete=false;
    // The ListView
    private ListView lstNames;
    private ArrayList<String> al_contactName,al_contactNumber,al_contactId;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int PERMISSIONS_REQUEST_UPDATE_CONTACTS = 100;
    ContentResolver contentResolver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        contentResolver = getContentResolver();

        al_contactName = new ArrayList<>();
        al_contactNumber = new ArrayList<>();
        al_contactId = new ArrayList<>();
        showContacts();
        myAdapter = new MyAdapter(getApplicationContext(),al_contactName,al_contactNumber);
        lstNames = (ListView) findViewById(R.id.lstNames);
        lstNames.setAdapter(myAdapter);
        registerForContextMenu(lstNames);
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_context_menu, menu);
        menu.setHeaderTitle("Select The Action");
        Log.e("SecondACTIVITY","ContextMenu is inflated");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        currentposition = info.position;
        Log.e("MI CP : ", String.valueOf(currentposition));
        int selecteditemid = item.getItemId();

        switch (selecteditemid) {
            case R.id.subOptionUpdate:
                Toast.makeText(SecondActivity.this, "Update Selected" +selecteditemid, Toast.LENGTH_LONG).show();
                Log.e("MainActivity", "Update Option Selected");
                updateItem();
                myAdapter.notifyDataSetChanged();
                return true;
            case R.id.subOptionDelete:
                Toast.makeText(SecondActivity.this, "Update Selected" +selecteditemid, Toast.LENGTH_LONG).show();
                Log.e("MainActivity", "Update Option Selected");
                deleteAlert();
                //deleteContact(al_contactNumber.get(currentposition));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deleteContact(String contactNumber) {
        ContactHelper.deleteContact(contentResolver,contactNumber);
        myAdapter.notifyDataSetChanged();
    }
    private void updateContact(String number,String contactNumber, String firstName) {
        ContactHelper.updateContact(contentResolver,number,contactNumber,firstName);
    }


    public void updateItem(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SecondActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        alertDialog.setTitle("Update "+al_contactName.get(currentposition)+" ?");
        alertDialog.setView(inflater.inflate(R.layout.dialog_update, null))
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog diag = (Dialog) dialog;
                        tv_diag_contactName = (TextView) diag.findViewById(R.id.diag_update_contact_name_txt);
                        tv_diag_contactName.setText(al_contactName.get(currentposition));
                        ev_diag_contactNumber = (EditText) diag.findViewById(R.id.diag_update_contact_phone_number);


                        edit_contactName = al_contactName.get(currentposition);
                        edit_contactNumber = ev_diag_contactNumber.getText().toString();

                        Log.e("updateItem ",edit_contactNumber);
                        editContacts();
                        refresh();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public boolean deleteAlert(){
        wantToDelete = false;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SecondActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        alertDialog.setTitle("Delete "+al_contactName.get(currentposition)+" ?");
        alertDialog.setView(inflater.inflate(R.layout.dialog_delete, null))
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        wantToDelete = true;
                        deleteContact(al_contactNumber.get(currentposition));
                        refresh();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        wantToDelete = false;
                        dialog.cancel();
                    }
                });
        alertDialog.show();
        return wantToDelete;
    }

    private void refresh() {
        Intent intent = new Intent(SecondActivity.this, SecondActivity.class);
        SecondActivity.this.startActivity(intent);
        finish();
    }

    private void displayAllContacts(){
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        if(cursor.getCount()>0){
            while (cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))>0){
                    Cursor pCur = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"=?",
                            new String[]{id},null);
                    while (pCur.moveToNext()){
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        al_contactName.add(name);
                        al_contactNumber.add(phoneNo);
                        al_contactId.add(id);
                        Log.e("DAC : ",name+", "+phoneNo+", "+id);
                    }
                }

            }
        }
    }
    /**
     * Show the contacts in the ListView.
     */
    private void showContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        // Android version is lesser than 6.0 or the permission is already granted.
        displayAllContacts();


    }

    private void editContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, PERMISSIONS_REQUEST_UPDATE_CONTACTS);
        }
        // Android version is lesser than 6.0 or the permission is already granted.
        Log.e("EC : ",al_contactNumber.get(currentposition)+", "+edit_contactName+", "+edit_contactNumber);
        updateContact(al_contactNumber.get(currentposition),edit_contactName,edit_contactNumber);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == PERMISSIONS_REQUEST_UPDATE_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                updateContact(al_contactNumber.get(currentposition),edit_contactName,edit_contactNumber);
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

}


