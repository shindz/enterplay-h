package acc.healthapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import acc.healthapp.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainDashActivityFragment extends Fragment {

    public MainDashActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_dash, container, false);
    }
}
