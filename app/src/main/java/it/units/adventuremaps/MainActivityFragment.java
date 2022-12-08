package it.units.adventuremaps;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

public class MainActivityFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        AppCompatImageButton experienceBtn = view.findViewById(R.id.experienceButton);
        experienceBtn.setOnClickListener(experienceBtnClickListener);

        AppCompatImageButton userProfileBtn = view.findViewById(R.id.userButton);
        userProfileBtn.setOnClickListener(userBtnClickListener);

        return view;
    }

    private final OnClickListener experienceBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent mapsIntent = new Intent(getActivity(), MapsActivityFragment.class);
            startActivity(mapsIntent);
        }
    };

    private final OnClickListener userBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent mapsIntent = new Intent(getActivity(), UserProfileActivity.class);
            startActivity(mapsIntent);
        }
    };

}
