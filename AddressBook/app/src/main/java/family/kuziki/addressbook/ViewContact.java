package family.kuziki.addressbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Created by Admin on 020 20.03.16.
 */
public class ViewContact extends Activity{
    private long rowID;
    private TextView nameTextView;
    private TextView phoneTextView;
    private TextView emailTextView;
    private TextView streetTextView;
    private TextView cityTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_contact);

        nameTextView = (TextView) findViewById(R.id.nameTextView);
        phoneTextView = (TextView) findViewById(R.id.phoneTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        streetTextView = (TextView) findViewById(R.id.streetTextView);
        cityTextView = (TextView) findViewById(R.id.cityTextView);

        Bundle extras = getIntent().getExtras();
        rowID = extras.getLong("row_id");
    }

    @Override
    protected void onResume(){
        super.onResume();
        new LoadContactTask().execute(rowID);
    }

    private class LoadContactTask extends AsyncTask<Long, Object, Cursor>{

        DatabaseConnector databaseConnector = new DatabaseConnector(ViewContact.this);

        @Override
        protected Cursor doInBackground(Long... params) {
            try {
                databaseConnector.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return databaseConnector.getOneContact(params[0]);
        }

        @Override
        protected void onPostExecute(Cursor result) {
            super.onPostExecute(result);
            result.moveToFirst();

            int nameIndex = result.getColumnIndex("name");
            int phoneIndex = result.getColumnIndex("phone");
            int emailIndex = result.getColumnIndex("email");
            int streetIndex = result.getColumnIndex("street");
            int cityIndex = result.getColumnIndex("city");

            nameTextView.setText(result.getString(nameIndex));
            phoneTextView.setText(result.getString(phoneIndex));
            emailTextView.setText(result.getString(emailIndex));
            streetTextView.setText(result.getString(streetIndex));
            cityTextView.setText(result.getString(cityIndex));

            result.close();
            databaseConnector.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_viewcontact, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()){
            case R.id.editItem:
                Intent addEditContact = new Intent(this, AddEditContact.class);
                addEditContact.putExtra("row_id", rowID);
                addEditContact.putExtra("name", nameTextView.getText());
                addEditContact.putExtra("phone", phoneTextView.getText());
                addEditContact.putExtra("email", emailTextView.getText());
                addEditContact.putExtra("street", streetTextView.getText());
                addEditContact.putExtra("city", cityTextView.getText());
                startActivity(addEditContact);
                return true;
            case R.id.deleteItem:
                deleteContact();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void deleteContact() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewContact.this);
        builder.setTitle(R.string.confirmTitle);
        builder.setMessage(R.string.confirmMessage);
        
        builder.setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                final DatabaseConnector databaseConnector = new DatabaseConnector(ViewContact.this);
                
                AsyncTask<Long, Object, Object> deleteTask = new AsyncTask<Long, Object, Object>() {
                    @Override
                    protected Object doInBackground(Long... params) {
                        try {
                            databaseConnector.deleteContact(params[0]);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Object result) {
                        finish();
                    }
                };
                deleteTask.execute(new Long[] {rowID});
            }
        });
        builder.setNegativeButton(R.string.button_cancel, null);
        builder.show();
    }
}
