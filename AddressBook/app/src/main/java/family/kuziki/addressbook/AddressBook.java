package family.kuziki.addressbook;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ListActivity;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.sql.SQLException;
import java.util.Objects;

public class AddressBook extends ListActivity{

    public static final String ROW_ID = "row_id";
    private ListView contactListView;
    private CursorAdapter contactAdapter;
    private ListView listView;
    private CursorAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactListView = getListView(); /// ?????????????????????????????????????????????????
        contactListView.setOnItemClickListener(viewContactListener);

        String[] from = new String[]{"name"};
        int[] to = new int[]{R.id.contactTextView};
        CursorAdapter contactAdapter = new SimpleCursorAdapter(AddressBook.this, R.layout.contact_list_item, null, from, to);
        setListAdapter(contactAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        new GetContactsTask().execute((Object[]) null);
    }

    @Override
    protected void onStop(){
        Cursor cursor = contactAdapter.getCursor();
        if (cursor != null){
            cursor.deactivate();
        }
        contactAdapter.changeCursor(null);
        super.onStop();
    }

    public ListView getListView() {
        return listView;
    }

    public void setListAdapter(CursorAdapter listAdapter) {
        this.listAdapter = listAdapter;
    }

    private class GetContactsTask extends AsyncTask <Object, Object, Cursor>{

        DatabaseConnector databaseConnector = new DatabaseConnector (AddressBook.this);

        @Override
        protected Cursor doInBackground(Object... params) {
            try {
                databaseConnector.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return databaseConnector.getAllContacts();
        }

        @Override
        protected void onPostExecute(Cursor result){
            contactAdapter.changeCursor(result);
            databaseConnector.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_addressbook, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent addNewContact = new Intent(AddressBook.this, AddEditContact.class);
        startActivity(addNewContact);
        return super.onOptionsItemSelected(item);
    }

    OnItemClickListener viewContactListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent viewContact = new Intent(AddressBook.this, ViewContact.class);
            viewContact.putExtra(ROW_ID, id);
            startActivity(viewContact);
        }
    };
}
