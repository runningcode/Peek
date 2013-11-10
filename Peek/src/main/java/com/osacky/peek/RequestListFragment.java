package com.osacky.peek;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.osacky.peek.Models.Contact;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class RequestListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Contact>> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setListShownNoAnimation(false);
        setEmptyText("No friends are on Peek");
        getActivity().getSupportLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public Loader<List<Contact>> onCreateLoader(int i, Bundle bundle) {
        return new ContactListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Contact>> listLoader, List<Contact> contacts) {
        for (Contact contact : contacts) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            //query.whereEqualTo(ParseUser.fetchAll())

        }

    }

    @Override
    public void onLoaderReset(Loader<List<Contact>> listLoader) {

    }
}
