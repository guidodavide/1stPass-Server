package com.serverSSL.threads;

import java.util.LinkedHashSet;

public class Monitor {
	private static LinkedHashSet<String> loggedUsers;
	private static Monitor monitor;
	
	private Monitor(){
		super();
		loggedUsers = new LinkedHashSet<String>();
	}
	
	public static Monitor getInstance(){
		if(monitor == null)
			monitor = new Monitor();
		return monitor;
	}
	
	public boolean contains(String s){
		return loggedUsers.contains(s);
	}
	
	public synchronized boolean signInUser (String s){
		return loggedUsers.add(s);
	}
	
	public synchronized boolean signOutUser (String s){
		return loggedUsers.remove(s);
	}
}
