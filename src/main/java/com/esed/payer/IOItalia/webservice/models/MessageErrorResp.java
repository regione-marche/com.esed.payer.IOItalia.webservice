package com.esed.payer.IOItalia.webservice.models;

public class MessageErrorResp {

	private String title;
	private String status;
	private String detail;
	
	public MessageErrorResp() {
	}
	
	public MessageErrorResp(String title, String status, String detail) {
		super();
		this.title = title;
		this.status = status;
		this.detail = detail;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	
	
}
