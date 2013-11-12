package com.osacky.peek;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;

import com.osacky.peek.Models.Photo;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.List;

public class PhotoListFragment extends ListFragment {

    private ParseQueryAdapter<Photo> photoAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText("No Peeks yet, try requesting some!");
        setListShown(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ParseQueryAdapter.QueryFactory<Photo> factory =
                new ParseQueryAdapter.QueryFactory<Photo>() {

                    @Override
                    public ParseQuery<Photo> create() {
                        ParseQuery<Photo> query = ParseQuery.getQuery(Photo.class);
                        query.whereEqualTo("receiver", Utils.getUserPhoneNumber(getActivity()));
                        query.addDescendingOrder("time");
                        return query;
                    }
                };

        photoAdapter = new ParsePhotoAdapter(getActivity(), factory);
        photoAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<Photo>() {
            @Override
            public void onLoading() {
                setListShown(false);
            }

            @Override
            public void onLoaded(List<Photo> photos, Exception e) {
                setListShown(true);
            }
        });
        setListAdapter(photoAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}
