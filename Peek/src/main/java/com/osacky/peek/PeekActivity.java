package com.osacky.peek;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.osacky.peek.Models.Person;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Locale;

public class PeekActivity extends ActionBarActivity implements LoginFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peek);

        ParseAnalytics.trackAppOpened(getIntent());
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        final String phoneNumber = Utils.getUserPhoneNumber(getApplicationContext());

        if (ParseUser.getCurrentUser() == null) {
            DialogFragment dialogFragment = LoginFragment.newInstance(false);
            dialogFragment.show(getSupportFragmentManager(), "login");
        }

        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
        parseInstallation.put("username", phoneNumber);
        parseInstallation.saveEventually();

        ParseQuery<Person> query = ParseQuery.getQuery(Person.class);
        query.whereEqualTo("phone", phoneNumber);
        query.findInBackground(new FindCallback<Person>() {
            @Override
            public void done(List<Person> persons, ParseException e) {
                if (e == null && persons.isEmpty()) {
                        Person person = new Person();
                        person.setPhone(phoneNumber);
                        person.saveEventually();
                }
            }
        });
    }

    @Override
    public void onFragmentLogin() {
        if (ParseUser.getCurrentUser() == null) {
            DialogFragment loginFragment = LoginFragment.newInstance(false);
            loginFragment.show(getSupportFragmentManager(), "login");
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new RequestListFragment();
                case 1:
                    return new PhotoListFragment();
                default:
                    // this should never happen
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
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
        if (id == R.id.action_add) {
            Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
            intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
