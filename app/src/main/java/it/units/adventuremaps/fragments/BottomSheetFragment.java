package it.units.adventuremaps.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import it.units.adventuremaps.interfaces.Database;
import it.units.adventuremaps.models.Experience;
import it.units.adventuremaps.R;


public class BottomSheetFragment extends BottomSheetDialogFragment {

    private Experience experience;
    private Database database;
    private Button setObjButton;

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    public BottomSheetFragment(Experience experience, Database database) {
        this.experience = experience;
        this.database = database;
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

        TextView title = view.findViewById(R.id.experience_title);
        TextView description = view.findViewById(R.id.experience_description);
        setObjButton = view.findViewById(R.id.set_objective_button);

        title.setText(experience.getName());
        description.setText(experience.getDescription());
        if (experience.getIsCompletedByUser()) {
            setObjButton.setVisibility(View.INVISIBLE);
            TextView completedText = new TextView(getActivity());
            completedText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            completedText.setText("COMPLETED!");
            LinearLayout bottomSheetLayout = view.findViewById(R.id.bottom_sheet_layout);
            bottomSheetLayout.addView(completedText);
        }
        if (experience.getIsTheObjective()) {
            setObjButton.setText(R.string.removeObjective_buttonText);
        }

        setObjButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (experience.getIsTheObjective()) {
                    database.setObjectiveExperienceOfUser(null);
                    setObjButton.setText(R.string.setObjective_buttonText);
                } else {
                    database.setObjectiveExperienceOfUser(experience);
                    setObjButton.setText(R.string.removeObjective_buttonText);
                }
            }
        });
        return view;
    }
}