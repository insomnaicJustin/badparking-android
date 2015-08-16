package ua.in.badparking.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by Igor on 15-08-2015.
 */
public class MapFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View result = inflater.inflate(0, container, false);

        return result;
    }

    public static MapFragment newInstance(final Bundle args) {
        MapFragment fragment = new MapFragment();

        if (args != null) fragment.setArguments(args);
        return fragment;
    }
}
