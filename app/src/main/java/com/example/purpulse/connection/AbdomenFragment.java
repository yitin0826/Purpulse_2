package com.example.purpulse.connection;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.purpulse.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AbdomenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AbdomenFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView txt_abteach;

    public AbdomenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConnectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AbdomenFragment newInstance(String param1, String param2) {
        AbdomenFragment fragment = new AbdomenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_abdomen, container,false);
        txt_abteach = view.findViewById(R.id.txt_abteach);
        txt_abteach.setText(R.string.abdomen);
        ForegroundColorSpan greenSpan = new ForegroundColorSpan(getResources().getColor(R.color.green));
        SpannableStringBuilder builder = new SpannableStringBuilder(txt_abteach.getText().toString());
        builder.setSpan(greenSpan,32,34,Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        txt_abteach.setText(builder);
        return view;
    }
}