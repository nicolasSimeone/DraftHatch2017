package nupa.drafthatch;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReputationFragment extends Fragment {
    private String username;
    static String userDistanceSetting;
    static String userCategoriesSetting;
    private int User_Id;

    public ReputationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        username= getActivity().getIntent().getExtras().getString("username");
        userDistanceSetting=getActivity().getIntent().getExtras().getString("distanceSetting");
        userCategoriesSetting = getActivity().getIntent().getExtras().getString("categories");
        User_Id=getActivity().getIntent().getIntExtra("User_Id", 0);

        return inflater.inflate(R.layout.fragment_reputation, container, false);

    }

}
