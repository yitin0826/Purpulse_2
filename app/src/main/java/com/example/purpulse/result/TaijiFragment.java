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

public class TaijiFragment extends Fragment {

    private TextView txt_taiji;
    private Button btn_taiji;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_taiji, container,false);
        txt_taiji = view.findViewById(R.id.txt_taiji);
        btn_taiji = view.findViewById(R.id.btn_taiji);
        btn_taiji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), TaijiActivity.class));
            }
        });
        btn_taiji.setPaintFlags(btn_taiji.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        SpannableStringBuilder span = new SpannableStringBuilder("縮排"+txt_taiji.getText());
        span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        txt_taiji.setText(span);
        return view;
    }
}