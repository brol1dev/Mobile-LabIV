package com.computomovil.labIV.bean;

public class User {

	private String sim;
	private String name;
	
	public String getSim() {
		return sim;
	}
	
	public void setSim(String sim) {
		this.sim = sim;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "User: [sim: " + sim + ", name: " + name + "]";
	}
}
