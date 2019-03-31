package nupa.drafthatch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class OtherKarmaFragment extends Fragment {
    private String username;
    static String userDistanceSetting;
    static String userCategoriesSetting;
    private int User_Id;


    public OtherKarmaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        username= getActivity().getIntent().getExtras().getString("username");


        return inflater.inflate(R.layout.fragment_other_karma, container, false);
    }


}
