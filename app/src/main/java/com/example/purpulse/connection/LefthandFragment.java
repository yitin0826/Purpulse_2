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
 * Use the {@link LefthandFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LefthandFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView txt_leftteach;

    public LefthandFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LefthandFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LefthandFragment newInstance(String param1, String param2) {
        LefthandFragment fragment = new LefthandFragment();
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
        View view = inflater.inflate(R.layout.fragment_lefthand, container,false);
        txt_leftteach = view.findViewById(R.id.txt_leftteach);
        txt_leftteach.setText(R.string.lefthand);
        ForegroundColorSpan yellowSpan = new ForegroundColorSpan(getResources().getColor(R.color.yellow));
        SpannableStringBuilder builder = new SpannableStringBuilder(txt_leftteach.getText().toString());
        builder.setSpan(yellowSpan,32,34, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        txt_leftteach.setText(builder);
        return view;
    }
}