package com.example.purpulse;

public class NameMapping {

    //太極圖說明
    private String mitemInfo;

    //圖片資源id
    private int mitemImg;

    //建構式2
    public NameMapping(String itemInfo, int itemImg){
        mitemInfo = itemInfo;
        mitemImg = itemImg;
    }

    //方法2.傳回中文名
    public String getItemInfo(){
        return mitemInfo;
    }

    //方法3.傳回圖片id
    public int getImage(){
        return mitemImg;
    }
}
