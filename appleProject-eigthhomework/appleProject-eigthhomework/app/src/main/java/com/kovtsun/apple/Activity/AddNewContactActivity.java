package com.kovtsun.apple.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.kovtsun.apple.DBHelper.ContactDBHelper;
import com.kovtsun.apple.DBTables.Contact;
import com.kovtsun.apple.R;

import java.sql.SQLException;

public class AddNewContactActivity extends AppCompatActivity {

    private ContactDBHelper contactDBHelper = null;
    private EditText contact_name;
    private EditText contact_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);
        contact_name = (EditText) findViewById(R.id.editNewContactName);
        contact_number = (EditText) findViewById(R.id.editAddNewContatcNumber);
    }

    private ContactDBHelper getHelper(){
        if (contactDBHelper == null){
            contactDBHelper = OpenHelperManager.getHelper(this, ContactDBHelper.class);
        }
        return contactDBHelper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (contactDBHelper != null){
            OpenHelperManager.releaseHelper();
            contactDBHelper = null;
        }
    }

    private void reset(){
        contact_name.setText("");
        contact_number.setText("");
    }

    public void onClickAddNewContact(View view){
        if ((contact_name.getText().toString().trim().length() > 0)&&(contact_number.getText().toString().trim().length() > 0)){
            final Contact contact = new Contact();
            contact.contactName = contact_name.getText().toString();
            contact.contactNumber = contact_number.getText().toString();
            try{
                final Dao<Contact, Integer> fruitDao = getHelper().getDao();
                fruitDao.create(contact);
                reset();
            }catch (SQLException e){
                e.printStackTrace();
            }
            CharSequence txt = getString(R.string.newContactAdd);
            int duration = Toast.LENGTH_SHORT;
            Toast toast;
            toast = Toast.makeText(this, txt, duration);
            toast.show();
            Intent intent = new Intent(this, ContactsActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

    public void onClickCancelAddNewContact(View view){
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
        this.finish();
    }
}
