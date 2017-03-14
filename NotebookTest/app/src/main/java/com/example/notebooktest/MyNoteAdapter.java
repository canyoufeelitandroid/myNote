package com.example.notebooktest;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thread.manyclass.AvatarImageView;

import java.util.List;

public class MyNoteAdapter extends BaseAdapter{
    private List<MyNote> ls;
    private Context mContext;
    final int VIEW_TYPE = 2;
    final int TYPE_1 = 0;
    final int TYPE_2 = 1;
    private LayoutInflater inflater;


//    private MainActivity.OnMyLongClickListener onMyLongClickListener;
//    private void setOnMyLongClickListener(MainActivity.OnMyLongClickListener myLongClickListener){
//        this.onMyLongClickListener=myLongClickListener;
//    }

    public MyNoteAdapter(Context context, List<MyNote> list) {

        ls=list;
        mContext=context;

    }

    @Override
    public MyNote getItem(int i) {
        return ls.get(i);
    }

    @Override
    public int getCount() {
        return ls.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        MyNote myNote=getItem(position);
        String note_text=myNote.getmyNote();
        String text[]=note_text.split("//pic:");
        for(int i=0;i<text.length;i++){
            if(text[i].contains("content://media/external/images/media/")){
                return TYPE_2;
            }
        }
        return TYPE_1;

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyNote mynote=getItem(position);

        ViewHolder1 viewHolder1=null;
        ViewHolder2 viewHolder2=null;
        int type=getItemViewType(position);
        if(convertView==null){
            inflater=LayoutInflater.from(mContext);
            switch (type){
                case TYPE_1:
                    convertView=inflater.inflate(R.layout.noteface,null);
                    viewHolder1=new ViewHolder1();
                    viewHolder1.dtext1=(TextView) convertView.findViewById(R.id.dtext);
                    viewHolder1.ttext1=(TextView) convertView.findViewById(R.id.ttext);
                    viewHolder1.ntext1=(TextView) convertView.findViewById(R.id.ntext);
                    viewHolder1.itext1=(TextView) convertView.findViewById(R.id.itext);
                    viewHolder1.iv1=(ImageView)convertView.findViewById(R.id.istop);
                    viewHolder1.mainLayout1=(RelativeLayout) convertView.findViewById(R.id.face_note) ;
                    convertView.setTag(viewHolder1);
                    break;
                case TYPE_2:
                    convertView=inflater.inflate(R.layout.noteface_wp,null);
                    viewHolder2=new ViewHolder2();
                    viewHolder2.dtext2=(TextView) convertView.findViewById(R.id.dtext_1);
                    viewHolder2.ttext2=(TextView) convertView.findViewById(R.id.ttext_1);
                    viewHolder2.ntext2=(TextView) convertView.findViewById(R.id.ntext_1);
                    viewHolder2.itext2=(TextView) convertView.findViewById(R.id.itext_1);
                    viewHolder2.iv2=(ImageView)convertView.findViewById(R.id.istop_1);
                    viewHolder2.imageView2=(AvatarImageView)convertView.findViewById(R.id.note_pic);
                    viewHolder2.mainLayout2=(RelativeLayout) convertView.findViewById(R.id.face_note_1) ;
                    convertView.setTag(viewHolder2);
                    break;
                default:
                    break;
            }

        }else{
            switch (type){
                case TYPE_1:
                    viewHolder1=(ViewHolder1) convertView.getTag();
                    break;
                case TYPE_2:
                    viewHolder2=(ViewHolder2)convertView.getTag();
                    break;
                default:
                    break;
            }
//            view=convertView;
//            viewHolder=(ViewHolder)view.getTag();
        }

        switch(type){
            case TYPE_1:
                viewHolder1.dtext1.setText(mynote.getmyDate());
                viewHolder1.ttext1.setText(mynote.getmyTime().toString());
                viewHolder1.itext1.setText(String.valueOf(mynote.getId()));
                final String note_tag=mynote.getmyNote().concat(String.valueOf(mynote.getId()));
                viewHolder1.iv1.setTag(note_tag);
                viewHolder1.iv1.setImageResource(R.drawable.more);
                if(mynote.getTop_id()==1) {
                    ImageView imageView=(ImageView)convertView.findViewWithTag(note_tag);
                    imageView.setImageResource(R.drawable.pushpin);
                    //imageView.setTag("");
                }
                viewHolder1.ntext1.setText(mynote.getmyNote());
                break;
            case TYPE_2:
                viewHolder2.dtext2.setText(mynote.getmyDate());
                viewHolder2.ttext2.setText(mynote.getmyTime().toString());
                viewHolder2.itext2.setText(String.valueOf(mynote.getId()));
                final String note_tag2=mynote.getmyNote().concat(String.valueOf(mynote.getId()));
                viewHolder2.iv2.setTag(note_tag2);
                viewHolder2.iv2.setImageResource(R.drawable.more);
                if(mynote.getTop_id()==1) {
                    ImageView imageView=(ImageView)convertView.findViewWithTag(note_tag2);
                    imageView.setImageResource(R.drawable.pushpin);
                    //imageView.setTag("");
                }
                //ÅÐ¶ÏÊÇ·ñ°üº¬Í¼Æ¬²¢ÏÔÊ¾Ê£ÓàÎÄ×ÖÄÚÈÝ
                String myNoteText=mynote.getmyNote();
                String text[]=myNoteText.split("//pic:");
                StringBuilder mNote=new StringBuilder();
                int node=1;
                for(int i=0;i<text.length;i++){
                    //text[i].contains("")||content://com.android.providers
                    if(!text[i].contains("content://media/external/images/media/")){
                        mNote.append(text[i]+" ");
                    }else{
                        if(node==1){
                            Uri uri = Uri.parse(text[i]);
                            ContentResolver cr = mContext.getContentResolver();
                            try {
                                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                                viewHolder2.imageView2.setImageBitmap(bitmap);
                                node = 2;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
                viewHolder2.ntext2.setText(mNote);
                break;
            default:
                break;
        }
        return convertView;
    }


    class ViewHolder1 {
        TextView dtext1;
        TextView ttext1;
        TextView ntext1;
        TextView itext1;
        ImageView iv1;
        RelativeLayout mainLayout1;

    }
    class ViewHolder2 {
        TextView dtext2;
        TextView ttext2;
        TextView ntext2;
        TextView itext2;
        ImageView iv2;
        RelativeLayout mainLayout2;
        AvatarImageView imageView2;

    }

}
