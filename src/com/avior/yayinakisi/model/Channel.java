package com.avior.yayinakisi.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.util.Log;

public class Channel {
	private String name;
	
	public List<Program> schedule;
	
	private Integer code;
	
	public Channel(String name, Integer code){
		this.name = name;
		this.code = code;
		this.schedule = new ArrayList<Program>();
	}
	
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public void setSchedule(List<Program> schedule) {
		this.schedule = schedule;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Program> getSchedule(){
		return schedule;
	}
	
	@Override
	public String toString(){
		return name;
	}
	
	public Program getCurrentProgram(){
		if(schedule == null || schedule.size()==0) return null;
		
		Date d = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		
		String day = c.get(Calendar.HOUR_OF_DAY)+"";
		if (day.length()<2) day = "0"+day;
		String minute = c.get(Calendar.MINUTE)+"";
		if (minute.length()<2) minute = "0"+minute;
		String dateString = day + ":" + minute;
		
		Program prev = schedule.get(0);
		for(int i = 1 ; i < schedule.size(); i++){
			if (schedule.get(i).getTime().compareTo(dateString)>0){
				return prev;
			} else {
				prev = schedule.get(i);
			}
		}
		return prev;
	}
}
