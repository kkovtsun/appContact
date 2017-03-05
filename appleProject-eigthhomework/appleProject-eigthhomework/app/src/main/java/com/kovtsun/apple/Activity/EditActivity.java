package com.kovtsun.apple.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.kovtsun.apple.Activity.ContactsActivity;
import com.kovtsun.apple.DBHelper.ContactDBHelper;
import com.kovtsun.apple.DBHelper.HelperFactory;
import com.kovtsun.apple.DBTables.Contact;
import com.kovtsun.apple.R;

import java.sql.SQLException;
import java.util.List;

public class EditActivity extends AppCompatActivity {

    private EditText contact_name_edit;
    private EditText contact_number_edit;
    private static String messageName = null, messageNumber = null, messageId = null;
    private ContactDBHelper contactDBHelper = null;
    private int idEdit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        contact_name_edit = (EditText) findViewById(R.id.editEditContactName);
        contact_number_edit = (EditText) findViewById(R.id.editEditContactNumber);

        Intent intent = getIntent();

        messageName = intent.getStringExtra("mEditBeforeName");
        messageNumber = intent.getStringExtra("mEditBeforeNumber");
        messageId = intent.getStringExtra("mIdEdit");
        if (messageName != null){
            contact_name_edit.setText(messageName);
        }
        if (messageNumber != null){
            contact_number_edit.setText(messageNumber);
        }
        if (messageId != null){
            idEdit = Integer.parseInt(messageId);
        }

        HelperFactory.setContactDBHelper(getApplicationContext());
    }

    public void onClickEditContact(View view){
        if ((messageName != null)&&(messageNumber != null)) {
            final Contact contact = new Contact();
            contact.contactId = idEdit;
            contact.contactName = messageName;
            contact.contactNumber = messageNumber;

            String newName = contact_name_edit.getText().toString();
            String newNumber = contact_number_edit.getText().toString();
            try{
                final Dao<Contact, Integer> contactDao = HelperFactory.getContactDBHelper().getDao();
                UpdateBuilder<Contact, Integer> updateBuilder = contactDao.updateBuilder();
                updateBuilder.updateColumnValue("contact_name", newName);
                updateBuilder.updateColumnValue("contact_number", newNumber);
                updateBuilder.where().eq("contact_id", contact.contactId);
                updateBuilder.update();
                reset();
            }catch (SQLException e){
                e.printStackTrace();
            }
            CharSequence txt = getString(R.string.editContact);
            int duration = Toast.LENGTH_SHORT;
            Toast toast;
            toast = Toast.makeText(this, txt, duration);
            toast.show();
            Intent intent = new Intent(this, ContactsActivity.class);
            startActivity(intent);
            this.finish();
        }
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
        contact_name_edit.setText("");
        contact_number_edit.setText("");
    }

    public void onClickCancelEditContact(View view){
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
        this.finish();
    }
}
