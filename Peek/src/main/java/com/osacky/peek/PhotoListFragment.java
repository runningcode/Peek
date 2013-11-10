package com.osacky.peek;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;

import com.osacky.peek.Models.Photo;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class PhotoListFragment extends ListFragment {

    private ParseQueryAdapter<Photo> photoAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ParseQueryAdapter.QueryFactory<ParseObject> factory =
                new ParseQueryAdapter.QueryFactory<ParseObject>() {

                    @Override
                    public ParseQuery<ParseObject> create() {
                        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Photo");
                        query.whereEqualTo("receiver", Utils.getUserPhoneNumber(getActivity()));
                        query.addDescendingOrder("time");
                        return query;
                    }
                };

        photoAdapter = new ParsePhotoAdapter(getActivity(), factory);
        photoAdapter.setTextKey("sender");
        photoAdapter.setImageKey("photo");

        setListAdapter(photoAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }
}
