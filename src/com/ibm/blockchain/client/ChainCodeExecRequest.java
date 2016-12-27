package com.ibm.blockchain.client;

public class ChainCodeExecRequest {
	private String jsonrpc="2.0";
	private String method;
	private int id;
	
	public ChainCodeExecRequest(String method)
	{
		this.method=method;
	}
}
