package com.kovtsun.apple.Activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.kovtsun.apple.DBHelper.ContactDBHelper;
import com.kovtsun.apple.DBHelper.HelperFactory;
import com.kovtsun.apple.DBTables.Contact;
import com.kovtsun.apple.Interfaces.LongClickListener;
import com.kovtsun.apple.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView recyclerView;
    private LinearLayoutManager verticalLinearLayoutManager;
    private LinearLayoutManager horizontalLinearLayoutManager;
    private RecyclerAdapter adapter;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private boolean is_in_action_mode = false;
    private TextView counter_text_view;
    private static String nameEditIntent = null;
    private static String numberEditIntent = null;
    private static String idEditIntent = null;
    private static String idDeleteIntent = null;
    private ContactDBHelper contactDBHelper = null;
    private ContactDBHelper contactDBHelperDelete = null;
    private List<Contact> cList;
    private String loginPrefActive = "", passwordPrefActive = "";
    private NavigationView navigationView = null;
    private int find;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (contactDBHelper != null) {
            HelperFactory.releaseHelper();
        }
        if (contactDBHelperDelete != null) {
            HelperFactory.releaseHelper();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        mAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedPrefActiveNow = getSharedPreferences("userInfoActive", Context.MODE_PRIVATE);
        loginPrefActive = sharedPrefActiveNow.getString("username", "");
        passwordPrefActive = sharedPrefActiveNow.getString("password", "");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if ((firebaseAuth.getCurrentUser() == null)&&(loginPrefActive =="")&&(passwordPrefActive =="")){
                    startActivity(new Intent(ContactsActivity.this, MainActivity.class));
                    finish();
                }
            }
        };

        contactDBHelper = OpenHelperManager.getHelper(this, ContactDBHelper.class);
        RuntimeExceptionDao<Contact, Integer> contactDao = contactDBHelper.getFruitRuntimeExceptionDao();

        cList = contactDao.queryForAll();
        if (cList.size() == 0){
            showContacts();
            cList.clear();
            cList = contactDao.queryForAll();
        }
        OpenHelperManager.releaseHelper();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ContactsActivity.this, AddNewContactActivity.class));
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        verticalLinearLayoutManager = new LinearLayoutManager(this);
        horizontalLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(verticalLinearLayoutManager);
        counter_text_view = (TextView) findViewById(R.id.counter_text);
        counter_text_view.setVisibility(View.GONE);
        adapter = new RecyclerAdapter(cList, ContactsActivity.this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        MenuItem menuItem = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        counter_text_view.setVisibility(View.GONE);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            recyclerView.setLayoutManager(horizontalLinearLayoutManager);
        }else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(verticalLinearLayoutManager);
        }
    }

    private ContactDBHelper getHelper(){
        if (contactDBHelper == null){
            contactDBHelper = OpenHelperManager.getHelper(this, ContactDBHelper.class);
        }
        return contactDBHelper;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        adapter.getItemSelected(item);
        if (item.getItemId()== 0){
            for (Contact c : cList){
                if (c.contactId == find){
                    String number = c.getContactNumber();
                    Call(number);
                    break;
                }
            }
            Log.i("TAG", String.valueOf(find));
        }
        if (item.getItemId() == 2){
            for (Contact c : cList){
                if (c.contactId == find){
                    idDeleteIntent = String.valueOf(c.contactId);
                    break;
                }
            }
            Intent intentD = new Intent(this, DeleteContactActivity.class);
            intentD.putExtra("mIdDelete", idDeleteIntent);
            startActivity(intentD);
            this.finish();
        }
        if (item.getItemId() == 1){
            for (Contact c : cList){
                if (c.contactId == find){
                    nameEditIntent = c.getContactName();
                    numberEditIntent = c.getContactNumber();
                    idEditIntent = String.valueOf(c.contactId);
                    break;
                }
            }
            Intent intentE = new Intent(this, EditContactActivity.class);
            intentE.putExtra("mEditBeforeName", nameEditIntent);
            intentE.putExtra("mEditBeforeNumber", numberEditIntent);
            intentE.putExtra("mIdEdit", idEditIntent);
            startActivity(intentE);
            this.finish();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (is_in_action_mode){
            adapter = new RecyclerAdapter(cList, ContactsActivity.this);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<Contact> newList = new ArrayList<>();
        for (Contact contact : cList){
            String name = contact.contactName.toLowerCase();
            String number = contact.contactNumber.toLowerCase();
            if ((name.contains(newText))||(number.contains(newText))){
                newList.add(contact);
            }
        }
        adapter.setFilter(newList);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.weather_id){
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            this.finish();
        }
        if (id==R.id.map_id){
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
            this.finish();
        }
        if (id==R.id.logout_id){
            if ((loginPrefActive != "")&&(passwordPrefActive != "")){
                SharedPreferences sharedPrefActive = getSharedPreferences("userInfoActive", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorActive = sharedPrefActive.edit();
                editorActive.putString("username", "");
                editorActive.putString("password", "");
                editorActive.apply();

                Toast.makeText(this, R.string.logOut, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();
            }
            else {
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                this.finish();
            }
        }
        return true;
    }

    private void Call(String phone){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showContacts(){
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            Contact contact = new Contact();
            String phoneNumber = "null";
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            contact.contactName = name;
            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
            while (phoneCursor.moveToNext()) {
                phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                break;
            }
            contact.contactNumber = phoneNumber;
            if (contact.contactNumber != "null") {
                try {
                    final Dao<Contact, Integer> contactDaoAdd = getHelper().getDao();
                    contactDaoAdd.create(contact);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        public ArrayList<Contact> listDB = new ArrayList<>();
        ContactsActivity contactActivity;
        Context ctx;
        String name;
        String number;
        int id;

        public ArrayList<Contact> getItems() {return listDB;}

        public RecyclerAdapter(List<Contact> listDB, Context ctx) {
            this.listDB = (ArrayList<Contact>) listDB;
            this.ctx = ctx;
            contactActivity = (ContactsActivity) ctx;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cards, parent, false);
            RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view, contactActivity);
            return  recyclerViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            holder.bind(listDB.get(position));
            holder.name.setText(listDB.get(position).contactName.toString());
            holder.number.setText(listDB.get(position).contactNumber.toString());
            holder.setItemLongClick(new LongClickListener() {
                @Override
                public void onItemLongClick(int pos) {
                    name = listDB.get(pos).getContactName();
                    number = listDB.get(pos).getContactNumber();
                    id = listDB.get(pos).getContactId();
                    find = id;
                    Log.i("TAG", String.valueOf(id));
                    Log.i("TAG", String.valueOf(find));
                }
            });
        }

        @Override
        public int getItemCount() {
            return listDB.size();
        }

        public void getItemSelected(MenuItem item){
            Log.i("TAG", name + ":" + item.getItemId() );
        }

        public void updateAdapter(ArrayList<Contact> list){
            for (Contact contact : list){
                listDB.remove(contact);
                notifyDataSetChanged();
            }
        }

        public  void  setFilter(ArrayList<Contact> newList){
            listDB = new ArrayList<>();
            listDB.addAll(newList);
            notifyDataSetChanged();
        }
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnCreateContextMenuListener {
        private TextView name;
        private TextView number;
        private ContactsActivity contactsActivityActivity;
        private CardView cardView;
        LongClickListener longClickListener;

        public RecyclerViewHolder(View itemView, ContactsActivity contactsActivityActivity) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.title);
            number = (TextView) itemView.findViewById(R.id.number);
            this.contactsActivityActivity = contactsActivityActivity;
            cardView = (CardView) itemView.findViewById(R.id.card);
            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        public void setItemLongClick(LongClickListener lc){
            this.longClickListener = lc;
        }

        public void bind(Contact contactItem) {
            name.setText(contactItem.contactName.toString());
            number.setText(contactItem.contactNumber.toString());
        }

        @Override
        public boolean onLongClick(View v) {
            this.longClickListener.onItemLongClick(getLayoutPosition());
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            menu.add(0,0,0,"Call");
            menu.add(0,1,0,"Edit contact");
            menu.add(0,2,0,"Delete contact");

        }
    }
}
