package com.jasonbutwell.simplenotebook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Request code for our intent
    public static final int EditTextIntentRequestCode = 1;

    // array list for the notes
    private ArrayList<String> notes;
    private ArrayList<String> noteTitles;

    private ArrayAdapter arrayAdapter;

    // shared preferences object
    SharedPreferences sharedPreferences;

    // values for shortening the title
    private int maxTitleLength = 36;
    private String shortenedTitlePostfix = "...";

    // used to delete a note entry
    private void deleteEntry( int pos ) {

        // Check there is something to remove before attempting to remove it.
        if ( notes.get(pos) != null && noteTitles.get(pos) != null ) {
            notes.remove(pos);                      // remove element from the notes array
            noteTitles.remove(pos);                 // remove the title too
            arrayAdapter.notifyDataSetChanged();    // notify the adapter's data has been changed
            saveData();                             // save the data
        }
    }

    // Alert Dialog that gets called when a long press occurs

    private void doAlert( int pos) {

        final int position = pos;   // store the location of the element within the list that was long pressed on

        // build the dialog
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        // set title and message
        alertDialog.setTitle("Caution: You are about to delete this entry!");
        alertDialog.setMessage("Are you absolutely sure that you want to do this?");

        // set up what happens when we select no (nothing)
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // set up what happens when we select yes ( deleteEntry() is called )
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEntry( position );
                        dialog.dismiss();
                    }
                });

        // show the dialog to the user
        alertDialog.show();
    }

    // save the data to shared preferences
    private void saveData() {

        // clear all original values
        sharedPreferences.edit().clear().commit();

        // loop for all notes in the array list
        for (int i=0; i < notes.size(); i++ ) {

            // put the string into the shared preferences object, using the loop index as the key
            // and obtaining the arraylist element by index as a string

            sharedPreferences.edit().putString(String.valueOf(i), notes.get(i)).apply();

            //Log.i("prefs", String.valueOf(i) + " : " + notes.get(i).toString());
        }
    }

    // load the data back from shared preferences to the array list
    private void loadData() {

        // index loop counter
        int i = 0;

        // set this to something other than "" for now
        String note = " ";

        // loop until we reach """, meaning empty string
        while ( note != "" ) {
            note = sharedPreferences.getString( String.valueOf(i++), "" );
            // sets note to the preference we obtained. the index is used as the key

            // if the last note was not "" (empty string) then add it to the array list
            if ( note != "") {
                notes.add(note);    // add the preference to the notes array
                noteTitles.add(getTitleFromNote(note)); // add a shortened version as a title to the note title array
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialise
        sharedPreferences = this.getSharedPreferences("com.jasonbutwell.simplenotebook", Context.MODE_PRIVATE);

        notes = new ArrayList<String>();
        noteTitles = new ArrayList<String>();

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, noteTitles);

        loadData(); // load data from shared preferences

        // Get the id of the list view and set up the array adapter
        ListView listView = (ListView) findViewById(R.id.noteListView);
        listView.setAdapter(arrayAdapter);

        // set the listener for the list

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // start an intent for result
                Intent intent = new Intent(getApplicationContext(), EditTextActivity.class);

                // stores the note from that given array list position
                intent.putExtra( "notes", notes.get(position) );

                // / stores the location for later
                intent.putExtra("location", position );

                // Start the intent
                startActivityForResult(intent, EditTextIntentRequestCode);
            }
        });

        // Long click listener for list view.
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {

                doAlert(pos);

                //Log.i("pos",""+pos);

                return true;    // consumes the click
            }
        });

    }

    // Called at run time to create the options menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Called when we click an item on the Action bar

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // grab the item id of the item clicked

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        // Check to see if the first Options menu item was selected.
        if (id == R.id.action_settings) {

            // call intent when + is clicked
            Intent intent = new Intent(getApplicationContext(),EditTextActivity.class);

            // Call our intent to open the Activity to edit the text

            startActivityForResult(intent, EditTextIntentRequestCode);

            return true;    // consumes the click
        }

        return super.onOptionsItemSelected(item);
    }

    // Called as a call back to get the results from our intent we called

    private String getTitleFromNote( String note ) {
        String title = new String( note );  // create a new string based on the note

        // check the length to see if it does exceed the maximum amount of chars we can show
        if ( title.length() > maxTitleLength-shortenedTitlePostfix.length() ) {

            // modifty the title accordingly
            title = title.substring(0,(maxTitleLength-shortenedTitlePostfix.length())).concat(shortenedTitlePostfix);
        }

        return title;   // pass back the title - if the length was okay, then the title will be the same as the actual note
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        // Make sure the request was successful
        if (requestCode == EditTextIntentRequestCode && resultCode == RESULT_OK && data != null) {

            String title = "";

            // Grab the data we got back from the Intent and display it to the Log
            String returnString = data.getStringExtra("notes");

            // grab the index location back, -1 is a default value if we get no value from location
            int indexLocation = data.getIntExtra("location", -1);

            // If something came back then add it to the list
            // check that the return string wasn't null and that the length is greater than 0

            if ( returnString != null && returnString.length() > 0 ) {
                // if index location is -1 then we are adding and not amending
                if ( indexLocation == -1 ) {

                    notes.add(returnString);  // just append to end of array list
                    noteTitles.add(getTitleFromNote(returnString));
                }
                else {
                    // if there is an index we update that entry to amend the item in the list
                    notes.set(indexLocation, returnString);
                    noteTitles.set(indexLocation, getTitleFromNote(returnString));
                }
                // Notify to the adapter that our list has been changed
                arrayAdapter.notifyDataSetChanged();

                saveData();
            }
        }
    }
}
