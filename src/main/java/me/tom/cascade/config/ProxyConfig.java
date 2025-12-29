package me.tom.cascade.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProxyConfig {
	private short proxyPort;
	private String targetHost;
	private short targetPort;
	
	private int proxyVersionProtocol;
	private String proxyVersionName;
	private String proxyDescription;
	
	private String jwtSecret;
}