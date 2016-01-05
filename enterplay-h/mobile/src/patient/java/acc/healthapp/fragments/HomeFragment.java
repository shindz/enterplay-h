package acc.healthapp.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import acc.healthapp.R;


public class HomeFragment extends Fragment {
    public static HomeFragment getInstance(int position) {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        homeFragment.setArguments(args);
        return homeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_home, container, false);
        return layout;
    }
}
