package com.example.notebooktest;



public class MyNote {

	private String date1;
	private String time1;
	private String note;
	private int id;
	private int top_id;
	
	
	public  MyNote(String date1,String time1 ,String note){
		this.date1=date1;
		this.time1=time1;
		this.note=note;
	}
	public  MyNote(String date1,String time1 ,String note,int id,int top_id){
		this.date1=date1;
		this.time1=time1;
		this.note=note;
		this.id=id;
		this.top_id=top_id;
	}
	public String getmyDate(){
		return date1;
	}
	public String getmyTime(){
		return time1;
	}
	public String getmyNote(){
		return note;
	}

	public int getId(){
		return id;
	}
	public int getTop_id(){
		return top_id;}
}
