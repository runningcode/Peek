package com.osacky.peek;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class PeekActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peek);

        ParseAnalytics.trackAppOpened(getIntent());

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // user is already logged in
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new RequestListFragment())
                        .commit();
            }
            ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
            parseInstallation.put("username", currentUser.getUsername());
            parseInstallation.saveEventually();
        } else {
            ParseUser user = new ParseUser();
            final String username = Utils.getUserPhoneNumber(getApplicationContext());
            try {
                ParseUser.logIn(username, "aaaa");
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new RequestListFragment())
                        .commit();
            } catch (ParseException e) {
                e.printStackTrace();
                user.setUsername(username);
                user.setPassword("aaaa");
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // Hooray! Let them use the app now.
                            getSupportFragmentManager().beginTransaction()
                                    .add(R.id.container, new RequestListFragment())
                                    .commit();
                            ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
                            parseInstallation.put("username", username);
                            parseInstallation.saveEventually();
                        } else {
                            // Sign up failed
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.peek, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
