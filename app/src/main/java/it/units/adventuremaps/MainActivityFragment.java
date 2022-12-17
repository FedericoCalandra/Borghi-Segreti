package it.units.adventuremaps;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivityFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        AppCompatImageButton experienceBtn = view.findViewById(R.id.experienceButton);
        experienceBtn.setOnClickListener(experienceBtnClickListener);

        AppCompatImageButton userProfileBtn = view.findViewById(R.id.userButton);
        userProfileBtn.setOnClickListener(userBtnClickListener);

        AppCompatImageButton logOutBtn = view.findViewById(R.id.logOutButton);
        logOutBtn.setOnClickListener(logOutBtnClickListener);

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

    private final OnClickListener logOutBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(R.string.logout_confirmation)
                    .setTitle(R.string.dialog_title);

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.signOut();
                    Toast.makeText(getActivity(), "User signed out", Toast.LENGTH_SHORT).show();
                    Intent firstIntent = new Intent(getActivity(), ActivityFirst.class);
                    Handler handler = new Handler();
                    handler.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(firstIntent);
                                }
                            }
                            , 200);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {}
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }
    };

}
