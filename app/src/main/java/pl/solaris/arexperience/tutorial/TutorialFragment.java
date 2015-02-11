package pl.solaris.arexperience.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.solaris.arexperience.R;

public class TutorialFragment extends Fragment {

    public static final String ARGS_POSITION = "TutorialFragment:POSITION";

    public TutorialFragment() {
    }

    public static TutorialFragment newInstance(int position) {
        TutorialFragment fragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tutorial, container, false);
        root.setId(getArguments().getInt(ARGS_POSITION, -1));
        return root;
    }

}
