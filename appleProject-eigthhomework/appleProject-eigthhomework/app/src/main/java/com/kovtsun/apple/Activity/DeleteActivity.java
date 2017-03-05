package com.kovtsun.apple.Activity;

import android.content.Intent;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.kovtsun.apple.DBHelper.ContactDBHelper;
import com.kovtsun.apple.DBHelper.HelperFactory;
import com.kovtsun.apple.DBTables.Contact;
import com.kovtsun.apple.R;

public class DeleteActivity extends AppCompatActivity {

    private static String messageId = null;
    private ContactDBHelper contactDBHelperDelete = null;
    private int idDelete = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        Intent intent = getIntent();
        messageId = intent.getStringExtra("mIdDelete");
        if (messageId != null){
            idDelete = Integer.parseInt(messageId);
        }
        HelperFactory.setContactDBHelper(getApplicationContext());
    }

    public void onClickCancelDeleteContact(View view){
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void onClickDeleteContact(View view){
        if (messageId != null){
            try {
                Dao<Contact, Integer> contactDao = getHelperDelete().getDao();
                DeleteBuilder<Contact, Integer> deleteBuilder = contactDao.deleteBuilder();
                deleteBuilder.where().eq("contact_id", idDelete);
                deleteBuilder.delete();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
            CharSequence txt = getString(R.string.txt_delete_was_complite);
            int duration = Toast.LENGTH_SHORT;
            Toast toast;
            toast = Toast.makeText(this, txt, duration);
            toast.show();
            Intent intent = new Intent(this, ContactsActivity.class);
            startActivity(intent);
            this.finish();
        }

    }

    private ContactDBHelper getHelperDelete(){
        if (contactDBHelperDelete == null){
            contactDBHelperDelete = OpenHelperManager.getHelper(this, ContactDBHelper.class);
        }
        return contactDBHelperDelete;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (contactDBHelperDelete != null){
            OpenHelperManager.releaseHelper();
            contactDBHelperDelete = null;
        }
    }
}
