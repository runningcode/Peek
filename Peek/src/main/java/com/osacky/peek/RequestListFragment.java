package com.osacky.peek;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.osacky.peek.Models.Contact;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Map<String, String>> {

    private ContactsListAdapter contactsListAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setRetainInstance(true);
        contactsListAdapter = new ContactsListAdapter(getActivity());
        setListAdapter(contactsListAdapter);
        getActivity().getSupportLoaderManager().initLoader(0, null, this).forceLoad();
        setListShown(false);
        setEmptyText("No friends are on Peek");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Map<String, String>> onCreateLoader(int i, Bundle bundle) {
        return new ContactListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Map<String, String>> listLoader, final Map<String, String> contacts) {
        if (contacts != null && !contacts.isEmpty()) {
            List<ParseQuery<ParseUser>> queries = new ArrayList<ParseQuery<ParseUser>>();
            for (Map.Entry<String, String> entry : contacts.entrySet()) {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("username", entry.getKey());
                queries.add(query);
            }
            ParseQuery<ParseUser> mainQuery = ParseQuery.or(queries);
            //mainQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
            mainQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> parseUsers, ParseException e) {
                    if (parseUsers == null) {
                        Log.i("TAG", String.valueOf(e.getCode()));
                        e.printStackTrace();
                    } else {
                        contactsListAdapter.clear();
                        for (ParseUser parseUser : parseUsers) {
                            String phone = parseUser.getUsername();
                            String name = contacts.get(parseUser.getUsername());
                            Contact contact = new Contact(name, phone, parseUser);
                            contactsListAdapter.add(contact);
                        }
                        if (isResumed()) {
                            setListShown(true);
                        } else {
                            setListShownNoAnimation(true);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Map<String, String>> listLoader) {

    }
}
