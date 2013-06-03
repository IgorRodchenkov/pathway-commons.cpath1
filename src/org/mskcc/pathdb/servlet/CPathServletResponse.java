package org.mskcc.pathdb.servlet;

import java.util.Locale;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CPathServletResponse implements HttpServletResponse {

	private PrintWriter printWriter;
	private HttpServletResponse response;

	public CPathServletResponse(HttpServletResponse response) {
		this.response = response;
		this.printWriter = new PrintWriter(new ByteArrayOutputStream(), false);
	}
	public void flushBuffer() throws IOException {}
	public PrintWriter getWriter() throws IOException { return printWriter; }
	public int getBufferSize() { return response.getBufferSize(); }
	public String getCharacterEncoding() { return response.getCharacterEncoding(); }
	public Locale getLocale() { return response.getLocale(); }
	public ServletOutputStream getOutputStream() throws IOException { return response.getOutputStream(); }
	public boolean isCommitted() { return response.isCommitted(); }
	public void reset() { response.reset(); }
	public void resetBuffer() { response.resetBuffer(); }
	public void setBufferSize(int size) { response.setBufferSize(size); }
	public void setContentLength(int len) { response.setContentLength(len); }
	public void setContentType(String type) { response.setContentType(type); }
	public void setLocale(Locale loc) { response.setLocale(loc); }
	public void setStatus(int code, String status) { response.setStatus(code, status); }
	public void setStatus(int code) { response.setStatus(code); }
	public void addIntHeader(String header, int value) { response.addIntHeader(header, value); }
	public void setIntHeader(String header, int value) { response.setIntHeader(header, value); }
	public void addHeader(String header, String value) { response.addHeader(header, value); }
	public void setHeader(String header, String value) { response.setHeader(header, value); }
	public void addDateHeader(String header, long value) { response.addDateHeader(header, value); }
	public void setDateHeader(String header, long value) { response.setDateHeader(header, value); }
	public void sendRedirect(String redirect) throws IOException { response.sendRedirect(redirect); }
	public void sendError(int error) throws IOException { response.sendError(error); }
	public void sendError(int errorValue, String errorStr) throws IOException { response.sendError(errorValue, errorStr); }
	public String encodeRedirectUrl(String url) { return response.encodeRedirectUrl(url); }
	public String encodeUrl(String url) { return response.encodeUrl(url); }
	public String encodeURL(String url) { return response.encodeURL(url); }
	public String encodeRedirectURL(String url) { return response.encodeRedirectURL(url); }
	public boolean containsHeader(String header) { return response.containsHeader(header); }
	public void addCookie(Cookie cookie) { response.addCookie(cookie); }
}