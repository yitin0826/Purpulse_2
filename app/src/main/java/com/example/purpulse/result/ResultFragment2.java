package com.example.purpulse.result;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.purpulse.Note;
import com.example.purpulse.PulseActivity;
import com.example.purpulse.R;
import com.example.purpulse.record.RecordActivity;

public class ResultFragment2 extends Fragment{

    ImageView tired,sad;
    ImageButton btn_down1,btn_down2;

    /** PopupWindow **/
    PopupWindow pop_tired,pop_sad;
    TextView txt_level,txt_suggest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result2, container, false);
        tired = view.findViewById(R.id.tired_img);
        sad = view.findViewById(R.id.sad_img);
        btn_down1 = view.findViewById(R.id.btn_down1);
        btn_down2 = view.findViewById(R.id.btn_down2);
        btn_down1.setOnClickListener(lis);
        btn_down2.setOnClickListener(lis);
        popup_tired();
        popup_sad();
        return view;
    }

    View.OnClickListener lis = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_down1:{
                    pop_tired.showAsDropDown(tired);
                    break;
                }
                case R.id.btn_down2:{
                    pop_sad.showAsDropDown(sad);
                    break;
                }
                case R.id.btn_restart:{
                    Note.RRi.clear();
                    startActivity(new Intent(getActivity(), PulseActivity.class));
                    break;
                }
                case R.id.btn_sure:{
                    Note.RRi.clear();
                    startActivity(new Intent(getActivity(), RecordActivity.class));
                    break;
                }
            }
        }
    };

    public void popup_tired(){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.popwindow_tired,null);
        pop_tired = new PopupWindow(view);
        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        pop_tired.setWidth(width*5/7);
        pop_tired.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop_tired.setFocusable(true);
        txt_level = view.findViewById(R.id.txt_level);
        txt_suggest = view.findViewById(R.id.txt_suggest);
        txt_level.setText(R.string.tired_good);
        String status = "good";
        setText(txt_level.getText().toString(),status);
        txt_suggest.setText(R.string.tired_good_suggest);
    }

    public void popup_sad(){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.popwindow_tired,null);
        pop_sad = new PopupWindow(view);
        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        pop_sad.setWidth(width*5/7);
        pop_sad.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop_sad.setFocusable(true);
        txt_level = view.findViewById(R.id.txt_level);
        txt_suggest = view.findViewById(R.id.txt_suggest);
        txt_level.setText(R.string.sad_good);
        String status = "good";
        setText(txt_level.getText().toString(),status);
        txt_suggest.setText(R.string.sad_good_suggest);
    }

    public void setText(String str,String status){
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        ForegroundColorSpan greenSpan = new ForegroundColorSpan(getResources().getColor(R.color.green));
        ForegroundColorSpan yellowSpan = new ForegroundColorSpan(getResources().getColor(R.color.yellow));
        ForegroundColorSpan redSpan = new ForegroundColorSpan(getResources().getColor(R.color.red));
        if (status.equals("great")){
            builder.setSpan(greenSpan,4,6, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }else if (status.equals("good")){
            builder.setSpan(yellowSpan,4,6, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }else{
            builder.setSpan(redSpan,4,6, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        txt_level.setText(builder);
    }
}