package com.example.schoolproj;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment subclass for the search screen interface.
 * Primarily used within navigation containers to host search-related UI components.
 */
public class search_screen_fragment extends Fragment {

    /** Argument key for the first parameter. */
    private static final String ARG_PARAM1 = "param1";
    /** Argument key for the second parameter. */
    private static final String ARG_PARAM2 = "param2";

    /** Internal parameter values. */
    private String mParam1;
    private String mParam2;

    /**
     * Default constructor for the search screen fragment.
     */
    public search_screen_fragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment search_screen_fragment.
     */
    public static search_screen_fragment newInstance(String param1, String param2) {
        search_screen_fragment fragment = new search_screen_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is being created.
     * Initializes parameters from fragment arguments.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.search_screen_fragment, container, false);
    }
}