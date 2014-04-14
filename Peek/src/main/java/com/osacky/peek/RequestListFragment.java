package com.osacky.peek;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.osacky.peek.Models.Contact;

import java.util.List;

public class RequestListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Contact>> {

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
    public Loader<List<Contact>> onCreateLoader(int i, Bundle bundle) {
        return new ContactListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Contact>> listLoader, List<Contact> contacts) {
        contactsListAdapter.setData(contacts);
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!contactsListAdapter.isEmpty()) {
            setListShown(true);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String phone = contactsListAdapter.getItem(position).getPerson().getPhone();
        contactsListAdapter.addSentTime(phone, v, position);
        Log.i("onLIstItemClick", "position: " + position + " rowid " + id);
    }

    @Override
    public void onLoaderReset(Loader<List<Contact>> listLoader) {
    }

}
