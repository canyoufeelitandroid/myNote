package com.thread.manyclass;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.EditText;

import java.util.List;

/**
 * Created by 64088 on 2017/2/28.
 */

public class AddPictoEdit {
    private Context mContext;
    public AddPictoEdit(Context context){
        this.mContext=context;
    }
    //转换URI字符串部分为显示图片(添加新图片时)
    public SpannableString insertPic(Bitmap bitmap,String bitString){
        ImageSpan imageSpan=new ImageSpan(mContext,bitmap);
        SpannableString spannableString=new SpannableString("//pic:"+bitString+"//pic:");
        spannableString.setSpan(imageSpan,0,bitString.length()+12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
    //转换URI字符串部分为显示图片(阅读已存数据时)
    public void readPic(String noteString, EditText editText, List<String> list){
        String text[]=noteString.split("//pic:");
        editText.append(text[0]);
        for(int i=1;i<text.length;i++){
            Log.i("data",text[i].toString());
            if(text[i].length()<25){
                editText.append(text[i]);
                continue;
            }
            if(!"".equals(text[i])){
                try{
                    Uri uri=Uri.parse(text[i].toString());
                    ContentResolver cr=mContext.getContentResolver();
                    Bitmap bitmap= BitmapFactory.decodeStream(cr.openInputStream(uri));
                    ImageSpan imageSpan=new ImageSpan(mContext,bitmap);
                    SpannableString spannableString=new SpannableString(text[i]);
                    spannableString.setSpan(imageSpan,0,text[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    Log.i("data","secondload");
                    list.add(text[i]);
                    editText.append(spannableString);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }

}
