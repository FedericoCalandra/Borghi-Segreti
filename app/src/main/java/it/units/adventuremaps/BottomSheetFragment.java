package it.units.adventuremaps;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class BottomSheetFragment extends BottomSheetDialogFragment {

    private Experience experience;
    private Marker marker;
    private MapsActivityFragment mapsFragment;
    private TextView title;
    private TextView description;
    private Button setObjButton;

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    public BottomSheetFragment(Experience experience, Marker marker, MapsActivityFragment mapsFragment) {
        this.experience = experience;
        this.marker = marker;
        this.mapsFragment = mapsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog_layout, container, false);

        title = view.findViewById(R.id.experience_title);
        description = view.findViewById(R.id.experience_description);
        setObjButton = view.findViewById(R.id.set_objective_button);

        title.setText(experience.getName());
        description.setText(experience.getDescription());
        if (experience.getIsTheObjective()) {
            setObjButton.setText(R.string.removeObjective_buttonText);
        }

        setObjButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (experience.getIsTheObjective()) {
                    setObjectiveOnDatabase(null);
                    experience.setIsTheObjective(false);
                    setObjButton.setText(R.string.setObjective_buttonText);
                } else {
                    setObjectiveOnDatabase(experience);
                    experience.setIsTheObjective(true);
                    setObjButton.setText(R.string.removeObjective_buttonText);

                    for (Experience exp : mapsFragment.getExperiences()) {
                        if (exp.getIsTheObjective()) {
                            exp.setIsTheObjective(false);
                        }
                    }
                }
                mapsFragment.redrawExperienceMarker(marker);
            }
        });
        return view;
    }

    private void setObjectiveOnDatabase(Experience experience) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uId = "";
        if (user != null) {
            uId = user.getUid();
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://adventuremaps-1205-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference dataRef = database.getReference().child("user_data").child(uId);
        Map<String, String> value = new HashMap<>();

        if (experience != null) {
            value.put("objective", experience.getId());
        } else {
            value.put("objective", null);
        }

        dataRef.setValue(value);
    }
}
