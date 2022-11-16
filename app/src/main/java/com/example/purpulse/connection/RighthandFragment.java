package com.example.purpulse.connection;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.purpulse.PulseActivity;
import com.example.purpulse.R;

public class RighthandFragment extends Fragment {

    private String deviceAddress;
    private TextView txt_rightteach;
    private Button btn_ecgnext,btn_yes,btn_no;
    private Spinner spinner;
    Dialog dialog;
    View dialogView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_righthand, container,false);
        txt_rightteach = view.findViewById(R.id.txt_rightteach);
        btn_ecgnext = view.findViewById(R.id.btn_ecgnext);
        btn_ecgnext.setOnClickListener(lis);
        txt_rightteach.setText(R.string.righthand);
        ForegroundColorSpan redSpan = new ForegroundColorSpan(getResources().getColor(R.color.red));
        SpannableStringBuilder builder = new SpannableStringBuilder(txt_rightteach.getText().toString());
        builder.setSpan(redSpan,32,34, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        txt_rightteach.setText(builder);
        return view;
    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bundle args = new Bundle();
            args.putString("device",deviceAddress);
            Intent intent = new Intent();
            intent.setClass(getActivity(), PulseActivity.class);
            intent.putExtra("device",deviceAddress);
            startActivity(intent);
        }
    };
}