package com.example.purpulse.result;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.purpulse.NameMapping;
import com.example.purpulse.R;

import java.util.ArrayList;

public class TaijiAdapter extends ArrayAdapter<NameMapping> {
    public TaijiAdapter(Activity context, ArrayList<NameMapping> tortoises){
        super(context, 0, tortoises);
    }

    @Override
    //改寫getView()方法
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        //listItemView可能會是空的，例如App剛啟動時，沒有預先儲存的view可使用
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_taiji, parent, false);
        }

        //找到data，並在View上設定正確的data
        NameMapping currentName = getItem(position);

        TextView itemInfo = listItemView.findViewById(R.id.item_info);
        itemInfo.setText(currentName.getItemInfo());

        ImageView itemImg = listItemView.findViewById(R.id.img_taiji);
        itemImg.setImageResource(currentName.getImage());

        return listItemView;
    }
}
