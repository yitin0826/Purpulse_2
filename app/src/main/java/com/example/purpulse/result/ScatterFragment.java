package com.example.purpulse.result;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.purpulse.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScatterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScatterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView txt_scatter;
    private Button btn_scatter;

    public ScatterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScatterFragment newInstance(String param1, String param2) {
        ScatterFragment fragment = new ScatterFragment();
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
        View view = inflater.inflate(R.layout.fragment_scatter, container,false);
        txt_scatter = view.findViewById(R.id.txt_scatter);
        btn_scatter = view.findViewById(R.id.btn_scatter);
        btn_scatter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ScatterActivity.class));
            }
        });
        btn_scatter.setPaintFlags(btn_scatter.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        SpannableStringBuilder span = new SpannableStringBuilder("縮排"+txt_scatter.getText());
        span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        txt_scatter.setText(span);
        return view;
    }
}