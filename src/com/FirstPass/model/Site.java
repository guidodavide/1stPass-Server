package com.firstpass.model;

import java.io.Serializable;

public class Site implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String url, usr, psw, name;
	
	

	public Site(String url, String usr, String psw, String name) {
		super();
		this.url = url;
		this.usr = usr;
		this.psw = psw;
		this.name = name;
	}
	public Site(){
		super();
	}
	
	

	public void setUrl(String url) {
		this.url = url;
	}
	public void setUsr(String usr) {
		this.usr = usr;
	}
	public void setPsw(String psw) {
		this.psw = psw;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "" + name;
	}



	public String getUrl() {
		return url;
	}

	public String getUsr() {
		return usr;
	}

	public String getPsw() {
		return psw;
	}

	public String getName() {
		return name;
	}
	
	

}
