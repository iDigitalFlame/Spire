package com.spire.web;

import java.util.HashMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.spire.io.Stream;

import java.io.PrintWriter;
import java.net.InetAddress;

import com.spire.io.DataStream;
import com.spire.io.Streamable;
import com.spire.util.HashList;
import com.spire.util.Constants;
import com.spire.ex.NullException;
import com.spire.ex.CloneException;
import com.spire.ex.StringException;

import java.io.ByteArrayOutputStream;

public final class WebState
{
	private static final byte[] CRLF = new byte[] { 13, 10 };
	private static final byte[] WEB_BOUNDRY = "boundry=".getBytes();
	
	protected static final char WEB_SEPERATOR ='/';
	
	private final WebServer stateServer;
	
	protected byte[] stateBoundry;
	
	//protected int stateLen2;
	//protected int stateLength;
	
	protected final boolean statePost;
	protected /*final*/ WebMethod stateMethod;
	protected final InputStream stateStream;
	protected final InetAddress stateAddress;
	protected final HashMap<String, String> stateKeys;
	protected final HashMap<String, Object> stateForms;
	protected final HashList<String, WebHeader> stateHeaders;
	
	protected int stateLength;
	protected String statePage;
	protected String stateType;
	protected String stateLang;
	protected boolean stateNCS;
	protected String stateEncoding;
	protected String stateRedirect;
	protected Streamable stateOutput;
	protected WebBrowser stateClient;
	protected WebMessage stateMessage;
	protected WebSession stateSession;
	
	public final void clearSessions()
	{
		if(stateSession != null) stateSession.sessionObjects.clear();
	}
	public final void clearContent() throws IOException
	{
		stateOutput.flush();
		stateOutput.close();
		stateOutput = null;
		stateOutput = new DataStream(new ByteArrayOutputStream());
	}
	public final void setRedirect(String Location) throws NullException
	{
		setRedirect(Location, null);
	}
	public final void setLanguage(String Language) throws StringException
	{
		if(Language != null && Language.isEmpty()) throw new StringException("Language");
		stateLang = Language;
	}
	public final void setResponseMessage(WebMessage Message) throws NullException
	{
		if(Message == null) throw new NullException("Message");
		stateMessage = Message;
		if(stateRedirect != null && Message != WebMessage.TEMPORARY_REDIRECT && Message != WebMessage.REDIRECT && Message != WebMessage.PERMANENT_REDIRECT) stateRedirect = null;
	}
	public final void appendText(String StringValue) throws NullException, IOException
	{
		if(StringValue == null) throw new NullException("StringValue");
		stateOutput.writeBytes(StringValue);
	}
	public final void setMIMEType(String DataType) throws NullException, StringException
	{
		if(DataType == null) throw new NullException("DataType");
		if(DataType.isEmpty()) throw new StringException("DataType");
		stateType = DataType;
	}
	public final void setRedirect(String Location, boolean Temporary) throws NullException
	{
		setRedirect(Location, Temporary ? WebMessage.TEMPORARY_REDIRECT : WebMessage.REDIRECT);
	}
	public final void setRedirect(String Location, WebMessage Response) throws NullException
	{
		if(Location == null) throw new NullException("Location");
		if(Location.isEmpty()) throw new StringException("Location");
		stateRedirect = Location;
		stateMessage = Response == null ? WebMessage.TEMPORARY_REDIRECT : Response;
	}
	public final void removeSession(String SessionKey) throws NullException, StringException
	{
		if(SessionKey == null) throw new NullException("SessionKey");
		if(SessionKey.isEmpty()) throw new StringException("SessionKey");
		if(stateSession != null) stateSession.sessionObjects.remove(SessionKey);
	}
	public final void setEncoding(String EncodingName) throws NullException, StringException
	{
		if(EncodingName == null) throw new NullException("EncodingName");
		if(EncodingName.isEmpty()) throw new StringException("EncodingName");
		stateEncoding = EncodingName;
	}
	public final void appendText(StringBuilder StringBuilder) throws NullException, IOException
	{
		if(StringBuilder == null) throw new NullException("StringBuilder");
		stateOutput.writeBytes(StringBuilder.toString());
	}
	public final void setHeader(String HeaderName, String HeaderData) throws NullException, StringException
	{
		if(HeaderName == null) throw new NullException("HeaderName");
		if(HeaderData == null) throw new NullException("HeaderData");
		if(HeaderName.isEmpty()) throw new StringException("HeaderName");
		if(HeaderData.isEmpty()) throw new StringException("HeaderData");
		WebHeader a = stateHeaders.get(HeaderName.toLowerCase());
		if(a != null)
		{
			a.headerData = HeaderData;
			a.headerOutgoing = true;
		}
		else
		{
			a = new WebHeader(HeaderName, HeaderData, true);
			stateHeaders.putElement(a.headerKey, a);
		}
		a = null;
	}
	public final void setSession(String SessionKey, Object SessionObject) throws NullException, StringException
	{
		if(SessionKey == null) throw new NullException("SessionKey");
		if(SessionKey.isEmpty()) throw new StringException("SessionKey");
		if(stateSession == null)
		{
			String a = Stream.createRandomName(128), b = Integer.toHexString(stateClient.headerData.hashCode());
			stateSession = new WebSession(a, b, stateAddress, System.currentTimeMillis() + (60000 * stateServer.serverSessionTime));
			stateServer.serverSessions.putElement(a, stateSession);
			if(stateServer.serverNoCookies) setRedirect("(" + a + b + ")" + statePage);
			else stateHeaders.putElement("Set-Cookie", new WebHeader("Set-Cookie", "WSID=" + a + b + "; path=/; expires=0", true));
		}
		stateSession.sessionObjects.put(SessionKey, SessionObject);
	}
	
	public final boolean isPost()
	{
		return statePost;
	}
	public final boolean isSecure()
	{
		return stateServer.isSecure();
	}
	public final boolean containsSession()
	{
		return stateSession != null;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof WebState && CompareObject.hashCode() == hashCode();
	}
	public final boolean containsSession(String SessionKey)
	{
		return stateSession != null && SessionKey != null && stateSession.sessionObjects.get(SessionKey) != null;
	}
	public final boolean containsRequest(String RequestKey)
	{
		return RequestKey != null && stateKeys.containsKey(RequestKey);
	}
	public final boolean containsFormValue(String FormElementName)
	{
		return FormElementName != null && stateForms.containsKey(FormElementName);
	}
	
	public final int hashCode()
	{
		return stateAddress.hashCode() + stateEncoding.hashCode() + stateLang.hashCode() + statePage.hashCode() + stateType.hashCode() + stateClient.hashCode() + 
				stateHeaders.hashCode() + stateForms.hashCode() + stateKeys.hashCode();
	}
	public final int getHeaderCount()
	{
		return stateHeaders.size();
	}
	
	public final String getType()
	{
		return stateType;
	}
	public final String toString()
	{
		return "WebState(IE) " + (statePost ? 'P' : 'G') + statePage + ":" + stateLang + "; " + stateMessage.messageID;
	}
	public final String getEncoding()
	{
		return stateEncoding;
	}
	public final String getLanguage()
	{
		return stateLang;
	}
	public final String getLocation()
	{
		return statePage;
	}
	public final String convertFullPath(String FullPath) throws NullException
	{
		if(FullPath == null) throw new NullException("FullPath");
		if(statePage.isEmpty() || (statePage.length() == 1 && statePage.charAt(0) == WEB_SEPERATOR))
		{
			if(stateServer.serverNoCookies && stateSession != null)
				return "/(" + stateSession.sessionKey + stateSession.sessionCheck + ")" + FullPath;
			return FullPath;
		}
		String[] a = statePage.split(String.valueOf(WEB_SEPERATOR));
		if(a.length > Short.MAX_VALUE)
		{
			if(stateServer.serverNoCookies && stateSession != null)
				return "/(" + stateSession.sessionKey + stateSession.sessionCheck + ")" + FullPath;
			return FullPath;
		}
		short b = (short)(a.length - 1);
		if(statePage.charAt(0) == WEB_SEPERATOR) b--;
		if(statePage.charAt(statePage.length() - 1) != WEB_SEPERATOR) b--;
		StringBuilder c = new StringBuilder();
		if(stateServer.serverNoCookies && stateSession != null)
		{
			c.append("/(");
			c.append(stateSession.sessionKey);
			c.append(stateSession.sessionCheck);
			c.append(")");
		}
		for(short d = 0; d <= b; d++)
		{
			c.append("..");
			if(d != b || FullPath.charAt(0) != WEB_SEPERATOR)
				c.append(WEB_SEPERATOR);
		}
		c.append(FullPath);
		return c.toString();
	}
	public final String getHeader(String HeaderName) throws NullException, StringException
	{
		if(HeaderName == null) throw new NullException("HeaderName");
		if(HeaderName.isEmpty()) throw new StringException("HeaderName");
		WebHeader a = stateHeaders.get(HeaderName);
		return a != null ? a.headerData : null;
	}
	public final String getRequest(String RequestKey) throws NullException, StringException
	{
		if(RequestKey == null) throw new NullException("RequestKey");
		if(RequestKey.isEmpty()) throw new StringException("RequestKey");
		return stateKeys.get(RequestKey);
	}
	public final String getFormValue(String FormElementName) throws NullException, StringException
	{
		if(FormElementName == null) throw new NullException("FormElementName");
		if(FormElementName.isEmpty()) throw new StringException("FormElementName");
		Object a = stateForms.get(FormElementName);
		if(a instanceof WebFile || a == null) return null;
		return a instanceof String ? (String)a : a.toString();
	}
	
	public final InetAddress getClientAddress()
	{
		return stateAddress;
	}
	
	public final InputStream getInputStream()
	{
		return stateStream;
	}
	
	public final Streamable getOutputStream()
	{
		return stateOutput;
	}
	
	public final WebFile getFile(String FormElementName) throws NullException, StringException
	{
		if(FormElementName == null) throw new NullException("FormElementName");
		if(FormElementName.isEmpty()) throw new StringException("FormElementName");
		Object a = stateForms.get(FormElementName);
		if(a == null || !(a instanceof WebFile)) return null;
		return (WebFile)a;
	}
	
	public final WebMessage getMessage()
	{
		return stateMessage;
	}
	
	public final WebBrowser getClientBrowser()
	{
		return stateClient;
	}
	
	public final WebServer getPageServer()
	{
		return stateServer;
	}
	
	public final PrintWriter getWriter()
	{
		return ((DataStream)stateOutput).getWriter();
	}
	
	public final Object getSession(String SessionKey) throws NullException, StringException
	{
		if(stateSession == null) return null;
		if(SessionKey == null) throw new NullException("SessionKey");
		if(SessionKey.isEmpty()) throw new StringException("SessionKey");
		return stateSession.sessionObjects.get(SessionKey);
	}
	
	public final WebState clone() throws CloneException
	{
		throw new CloneException("Cannot clone WebStates!");
	}
	
	protected WebState(boolean IsPost, String WebPage, InputStream WebStream, WebServer WebServer, InetAddress WebHost)
	{
		statePost = IsPost;
		statePage = WebPage;
		stateAddress = WebHost;
		stateServer = WebServer;
		stateType = "text/html";
		stateEncoding = "UTF-8";
		stateStream = WebStream;
		stateMessage = WebMessage.OK;
		stateKeys = new HashMap<String, String>();
		stateForms = new HashMap<String, Object>();
		stateHeaders = new HashList<String, WebHeader>();
		stateOutput = new DataStream(new ByteArrayOutputStream());
	}

	protected final void invalidate()
	{
		stateClient = null;
		stateEncoding = null;
		stateForms.clear();
		stateHeaders.clear();
		stateKeys.clear();
		stateLang = null;
		stateMessage = null;
		stateOutput = null;
		statePage = null;
		stateRedirect = null;
		stateSession = null;
		stateType = null;
	}
	protected final void writeOutHeadersAndPage(OutputStream OutStream) throws IOException
	{
		stateOutput.flush();
		stateOutput.close();
		StringBuilder a = new StringBuilder(350);
		if(stateSession == null && stateServer.serverNoCookies && stateNCS)
		{
			stateRedirect = getCorrectPath(this, stateServer);
			stateMessage = WebMessage.REDIRECT;
		}
		if(stateSession != null && stateSession.sessionObjects.isEmpty())
		{
			stateServer.serverSessions.remove(stateSession.getKey());
			if(stateServer.serverNoCookies)
			{
				stateRedirect = getCorrectPath(this, stateServer);
				stateMessage = WebMessage.REDIRECT;
			}
			else
			{
				if(stateHeaders.containsKey("cookie")) stateHeaders.removeElement("cookie");
				if(stateHeaders.containsKey("Set-Cookie")) stateHeaders.removeElement("Set-Cookie");
			}
			stateSession = null;
		}
		a.append("HTTP/1.1 ");
		a.append(String.valueOf(stateMessage.messageID) + " " + stateMessage.messageText);
		byte[] b = null;
		if(stateRedirect != null)
		{
			a.append("\r\nLocation: " + stateRedirect);
			b = new byte[0];
		}	
		else b = ((ByteArrayOutputStream)stateOutput.getStreamOutput()).toByteArray();
		a.append("\r\nDate: " + Constants.getNow().toString() + "\r\nServer: " + Constants.LOCAL.getName() + "\r\nConnection: close\r\nX-Powered-By: SpireAPI");
		if(stateLang != null) a.append("\r\nContent-Language: " + stateLang);
		a.append("\r\nLast-Modified: " + Constants.getNow().toString() + "\r\nPragma: no-cache\r\nCache-Control: no-cache\r\nContent-Type: " + stateType + "; charset=" + stateEncoding);
		if(stateHeaders.size() > 0)
		{
			for(int c = 0; c < stateHeaders.size(); c++)
				if(stateHeaders.get(c).headerOutgoing)
					a.append("\r\n" + stateHeaders.get(c).headerKey + ": " + stateHeaders.get(c).headerData);
			a.append("\r\n");
		}
		a.append("Vary: Accept-Encoding, Cookie, User-Agent\r\nContent-Length: " + b.length + "\r\n\r\n");
		OutStream.write(a.toString().getBytes());
		OutStream.write(b);
		a.delete(0, a.length());
		a = null;
		b = null;
	}	

	protected static final WebState generateWebState(WebServer StateServer, InetAddress StateClient, InputStream StateStream) throws IOException, WebException
	{
		/*byte[] a = new byte[4024];
		int cc = a.length;
		for(int b = 0; (b = StateStream.read(a, 0, cc)) >= 0; b = StateStream.read(a, 0, cc))
		{
			System.out.print(new String(a, 0, b));
			//if(StateStream.available() < cc && StateStream.available() > 0)
			//	cc = StateStream.available();
			if(StateStream.available() <= 0){
				StateStream.read();
				if(StateStream.available() <= 0) break;
			}
		}
		System.out.println();*/
		return WebReader.readWebState(StateServer, StateClient, StateStream);
	}
	protected static final WebState generateWebState(WebServer Server, InetAddress WebClient, Stream WebStream) throws IOException
	{
		boolean Az = true;
		if(Az)
		{
			try
			{
				WebState Cz = WebReader.readWebState(Server, WebClient, WebStream);
				//WebState Cz = new WebState(false, "page", WebStream, Server, WebClient);
				return Cz;
			}
			catch (Throwable Exception)
			{
				Exception.printStackTrace();
				WebState Cz = new WebState(false, "page", WebStream, Server, WebClient);
				return Cz;
			}
		}
		byte[] a = new byte[500];
		StringBuilder b = new StringBuilder(650);
		for(short c = 0; (c = (short)WebStream.readDirectByteArray(a, 0, a.length)) >= 0; )
		{
			//System.out.println(java.util.Arrays.toString(a));
			b.append(new String(a, 0, c));
			if(WebStream.getAvailable() <= 0) break;
		}
		String d = b.toString(), e = null, f = null, g = null;
		System.out.println(d);
		b.delete(0, b.length());
		b = null;
		int h = d.indexOf("HTTP/"), i = 0, j = 0, k = 0;
		WebSession l = null;
		if(d.isEmpty()) return null;
		boolean m = d.charAt(0) == 'P';
		e = d.substring(d.indexOf(WEB_SEPERATOR), h - 1);
		i = e.indexOf('?');
		if(i >= 0)
			j = e.indexOf('&', i);
		WebState n = new WebState(m, i >= 0 ? e.substring(0, i) : e, WebStream, Server, WebClient);
		if(j >= 0)
		{
			String[] o = e.substring(i + 1).split("&");
			for(int p = 0, q = 0; p < o.length; p++)
			{
				q = o[p].indexOf('=');
				if(q >= 0)
					n.stateKeys.put(o[p].substring(0, q), o[p].substring(q + 1).replace("%20", " " ));
			}
			o = null;
		}
		else
		{
			j = e.indexOf('=', i);
			if(j >= 0)
				n.stateKeys.put(e.substring(i + 1, j), e.substring(j + 1).replace("%20", " " ));
		}
		j = d.indexOf("\r\n\r\n");
		String[] r = (j >= 0 ? d.substring(0, j) : d).split("\r\n");
		for(int s = 1, t = 0; s < r.length; s++)
		{
			if(!m && s > 0 && r[s - 1].isEmpty() && r[s].isEmpty())
				break;
			t = r[s].indexOf(':');
			if(t >= 0)
			{
				f = r[s].substring(0, t);
				if(f.equalsIgnoreCase("user-agent"))
				{
					n.stateClient = new WebBrowser(f.toLowerCase(), r[s].substring(t + 2), false);
					n.stateHeaders.putElement(n.stateClient.headerKey, n.stateClient);
				}
				else if(f.equalsIgnoreCase("cookie"))
					n.stateHeaders.putElement(f.toLowerCase(), new WebHeader(f.toLowerCase(), r[s].substring(t + 2), true));					
				else
					n.stateHeaders.putElement(f, new WebHeader(f, r[s].substring(t + 2), false));
				f = null;
			}
			if(m && (s + 1) == r.length)
			{
				j = r[s].indexOf('&');
				if(j >= 0)
				{
					String[] v = r[s].split("&");
					for(int w = 0, x = 0; w < v.length; w++)
					{
						x = v[w].indexOf('=');
						if(x >= 0)
						{
							//System.out.println(v[w].substring(0, x).trim() + " ,, " +  v[w].substring(x + 1).replace('+', ' '));
							n.stateForms.put(v[w].substring(0, x).trim(), v[w].substring(x + 1).replace('+', ' '));
						}
					}
					v = null;
				}
				else
				{
					j = r[s].indexOf('=');
					if(j >= 0)
					{
						//System.out.println(r[s].substring(0, j).trim() + " ,,, " + r[s].substring(j + 1).replace('+', ' '));
						n.stateForms.put(r[s].substring(0, j).trim(), r[s].substring(j + 1).replace('+', ' '));
					}
				}
			}
		}
		
		
		
		r = null;
		if(Server.serverNoCookies) checkNoSession(n, Server);
		g = n.stateHeaders.contains("cookie") ? n.stateHeaders.get("cookie").headerData : null;
		if(g != null && g.length() > 134 && !Server.serverNoCookies)
		{
			k = g.indexOf('=');
			l = Server.serverSessions.get(g.substring(k + 1, k + 129));
			String y = g.length() > (k + 129) ? g.substring(k + 129) : null;
			if(k >= 0 && l != null && y != null && g.substring(0, k).equals("WSID") && l.sessionHost.equals(WebClient))
			{
				if(l.sessionCheck.equals(y) && y.equals(Integer.toHexString(n.stateClient.headerData.hashCode())))
				{
					n.stateSession = l;
					n.stateSession.sessionExpires = System.currentTimeMillis() + (60000 * Server.serverSessionTime);
				}
				else
				{
					n.stateHeaders.remove("cookie");
					Server.serverSessions.remove(l.sessionKey);
				}
			}
			else
			{
				n.stateHeaders.remove("cookie");
				if(l != null) Server.serverSessions.remove(l.sessionKey);
			}
		}
		return n;
	}

	private static final void checkNoSession(WebState State, WebServer Server)
	{
		if(!Server.serverNoCookies) return;
		short a = (short)State.statePage.indexOf('('), b = (short)State.statePage.indexOf(')');
		if(a < 0 || b <= 128) return;
		String c = State.statePage.substring(a + 1, b), d = null;
		if(c.length() > 128)
		{
			d = c.substring(128);
			c = c.substring(0, 128);
			WebSession e = Server.serverSessions.get(c);
			if(e != null && e.sessionCheck.equals(d) && d.equals(Integer.toHexString(State.stateClient.headerData.hashCode())))
			{
				State.stateSession = e;
				State.stateSession.sessionExpires = System.currentTimeMillis() + (60000 * Server.serverSessionTime);
			}
			else State.stateNCS = true;
			State.statePage = State.statePage.substring(b + 1);
			e = null;
		}
	}
	
	private static final String getCorrectPath(WebState State, WebServer Server)
	{
		if(!Server.serverNoCookies) return State.statePage;
		short a = (short)State.statePage.indexOf('('), b = (short)State.statePage.indexOf(')');
		if(a < 0 || b <= 128) return State.statePage;
		return State.statePage.substring(b + 1);
	}
}