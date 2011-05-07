package com.avior.yayinakisi.model;

public class Program implements Comparable<Program> {

	private Long id;
	
	private String time;
	
	private String name;
	
	private String category;
	
	private Channel channel;
	
	public Program(String time, String name, String category){
		this.time=time;
		this.name=name;
		this.category=category;
	}

	@Override
	public int compareTo(Program p) {
		return time.compareTo(p.time);
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public String toString(){
		return time + " " + name;
	}
	
}
