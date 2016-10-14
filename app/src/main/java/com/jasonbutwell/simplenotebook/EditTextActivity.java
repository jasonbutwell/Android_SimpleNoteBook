package com.jasonbutwell.simplenotebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;

public class EditTextActivity extends AppCompatActivity {

    // used to temporarily store our location variable for later
    private int location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);

        // set up back button on the action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // obtain the intent passed in
        Intent intent = getIntent();

        // grab the notes for that location if it exists
        String notes = intent.getStringExtra( "notes" );

        // grab the location id and store it if it exists
        location = intent.getIntExtra("location",-1);

        // if there are notes, set the textview to the note so we can then edit it

        if ( notes != null ) {
            EditText editText = (EditText)findViewById(R.id.editNoteTextView);
            editText.setText( notes );
        }
    }

    // Called if back button is pressed.

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Log.i("select","back button pressed");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // the home button on the action bar was pressed
            case android.R.id.home:

                //Log.i("select","home button pressed");

                // set up and intent to pass data back
                Intent outputIntent = new Intent();
                EditText textField = (EditText)findViewById(R.id.editNoteTextView);

                // grab the contents of the textfield and store in the intent
                outputIntent.putExtra("notes", textField.getText().toString() );

                // store the location to pass back
                outputIntent.putExtra("location", location);
                setResult( RESULT_OK, outputIntent );

                this.finish();      // If home button closed then finish this activity to pass the data back.
                return true;

            default:
                return super.onOptionsItemSelected(item);   // default
        }
    }
}
