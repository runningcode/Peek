package com.osacky.peek;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.osacky.peek.Models.Contact;
import com.osacky.peek.Models.Person;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RequestListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Contact>> {

    private ContactsListAdapter contactsListAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        Person person = contactsListAdapter.getItem(position).getPerson();

        ParseQuery<ParseInstallation> parseInstallationQuery = ParseInstallation.getQuery();
        parseInstallationQuery.whereEqualTo("username", person.getPhone());

        JSONObject data = new JSONObject();
        try {
            data.put("action", "com.osacky.peek.create");
            data.put("username", Utils.getUserPhoneNumber(getActivity()));
            data.put("time", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ParsePush push = new ParsePush();
        push.setQuery(parseInstallationQuery);
        push.setData(data);
        push.sendInBackground();

        v.setBackgroundResource(R.color.green);
    }

    @Override
    public void onLoaderReset(Loader<List<Contact>> listLoader) {
    }

}
