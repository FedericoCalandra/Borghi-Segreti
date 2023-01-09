package it.units.adventuremaps;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class BottomSheetFragment extends BottomSheetDialogFragment {

    private Experience experience;

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    public BottomSheetFragment(Experience experience) {
        this.experience = experience;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView title = getView().findViewById(R.id.experience_title);
        TextView description = getView().findViewById(R.id.experience_description);
        Button setObjButton = getView().findViewById(R.id.set_objective_button);

        return inflater.inflate(R.layout.bottom_sheet_dialog_layout, container, false);
    }
}
