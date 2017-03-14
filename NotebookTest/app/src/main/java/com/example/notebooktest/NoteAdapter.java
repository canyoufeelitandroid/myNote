package com.example.notebooktest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class NoteAdapter extends ArrayAdapter<MyNote> {
	
	private int resourceId;
	private Context mContext;


	public NoteAdapter(Context context, int resource, List<MyNote> objects) {
		super(context, resource, objects);
		resourceId=resource;
		mContext=context;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyNote mynote=getItem(position);
		View view;
		final ViewHolder viewHolder;
		if(convertView==null){
			view=LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder=new ViewHolder();
			viewHolder.dtext=(TextView) view.findViewById(R.id.dtext);
			viewHolder.ttext=(TextView) view.findViewById(R.id.ttext);
			viewHolder.ntext=(TextView) view.findViewById(R.id.ntext);
			viewHolder.itext=(TextView) view.findViewById(R.id.itext);
			viewHolder.iv=(ImageView)view.findViewById(R.id.istop);
			viewHolder.mainLayout=(RelativeLayout) view.findViewById(R.id.face_note) ;
			view.setTag(viewHolder);
		}else{
			view=convertView;
			viewHolder=(ViewHolder)view.getTag();
		}

		viewHolder.dtext.setText(mynote.getmyDate());
		viewHolder.ttext.setText(mynote.getmyTime().toString());
		viewHolder.itext.setText(String.valueOf(mynote.getId()));
		final String note_tag=mynote.getmyNote().concat(String.valueOf(mynote.getId()));
		viewHolder.iv.setTag(note_tag);
		viewHolder.iv.setImageResource(R.drawable.more);
		if(mynote.getTop_id()==1) {
			ImageView imageView=(ImageView)view.findViewWithTag(note_tag);
			imageView.setImageResource(R.drawable.pushpin);
			//imageView.setTag("");
		}
		//≈–∂œ «∑Ò∞¸∫¨Õº∆¨≤¢œ‘ æ £”‡Œƒ◊÷ƒ⁄»›
		String myNoteText=mynote.getmyNote();
		String text[]=myNoteText.split("//pic:");
		StringBuilder mNote=new StringBuilder();
		for(int i=0;i<text.length;i++){
			if(!text[i].contains("content://media/external/images/media/")){
				mNote.append(text[i]+" ");
			}
		}
		viewHolder.ntext.setText(mNote);
		return view;
	}

	class ViewHolder{
		TextView dtext;
		TextView ttext;
		TextView ntext;
		TextView itext;
		ImageView iv;
		RelativeLayout mainLayout;
	}
}
