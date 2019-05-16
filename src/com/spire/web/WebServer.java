package com.spire.web;

import java.io.File;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import com.spire.io.Stream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import com.spire.log.Reporter;
import com.spire.sec.Security;
import com.spire.io.DataStream;
import com.spire.util.HashList;
import com.spire.util.Constants;
import com.spire.ex.NullException;
import com.spire.ex.CloneException;
import com.spire.ex.NumberException;
import com.spire.ex.StringException;
import com.spire.io.SecurityProvider;
import com.spire.ex.InternalException;
import java.net.SocketTimeoutException;
import com.spire.ex.PermissionException;

public final class WebServer
{
	public static final short HTTP = 80;
	public static final short HTTPS = 443;
	
	private static final WebPage FNF_PAGE = new WebServerDefaultFNFError();
	private static final WebPage SERVER_ERR_PAGE = new WebServerDefaultError();
	
	private final WebServerThread serverThread;
	private final SecurityProvider serverProvider;
	private final HashMap<String, WebPage> serverPages;
	private final HashMap<String, Boolean> serverServePages;
	
	protected final HashList<String, WebSession> serverSessions;

	protected boolean serverNoCookies;
	protected short serverSessionTime;
	
	private boolean serverRules;
	private boolean serverAlive;
	private File serverDirectory;
	private WebPage serverErrorPage;
	private WebPage serverNotFoundPage;
	private ServerSocket serverWebSocket;
	private WebServerCThread[] serverThreads;
	
	public WebServer(int ServerPort) throws IOException, PermissionException
	{
		this(ServerPort, null, null);
	}
	public WebServer(int ServerPort, InetAddress LocalAddress) throws IOException, PermissionException
	{
		this(ServerPort, LocalAddress, null);
	}
	public WebServer(int ServerPort, SecurityProvider ServerProvider) throws IOException, PermissionException
	{
		this(ServerPort, null, ServerProvider);
	}
	public WebServer(int ServerPort, InetAddress LocalAddress, SecurityProvider ServerProvider) throws IOException, PermissionException
	{
		if(ServerPort <= 0) throw new NumberException("ServerPort", ServerPort, true);
		if(ServerPort > Constants.MAX_USHORT_SIZE) throw new NumberException("ServerPort", ServerPort, 0, Constants.MAX_USHORT_SIZE);
		Security.check("io.web.server", Integer.valueOf(ServerPort));
		serverAlive = true;
		serverSessionTime = 20;
		serverNotFoundPage = FNF_PAGE;
		serverErrorPage = SERVER_ERR_PAGE;
		serverProvider = ServerProvider;
		serverPages = new HashMap<String, WebPage>();
		serverServePages = new HashMap<String, Boolean>();
		serverSessions = new HashList<String, WebSession>();
		serverWebSocket = serverProvider != null ? serverProvider.createServerSocket(ServerPort, 500, LocalAddress) : new ServerSocket(ServerPort, 500, LocalAddress);
		serverWebSocket.setSoTimeout(60000);
		serverThread = new WebServerThread(this);
		if(serverProvider != null) Reporter.debug(Reporter.REPORTER_SECURITY, "WebServer bound with a \"" + serverProvider.getProviderName() + "\" SecurityProvider!");
		Reporter.info(Reporter.REPORTER_WEB, "A WebServer on port " + ServerPort + " was opened");
	}
	
	public final void stopWebServer() throws PermissionException
	{
		Security.check("io.web.server.stop", serverThread.getName());
		serverAlive = false;
		serverThread.interrupt();
		try
		{
			serverWebSocket.close();
		}
		catch (IOException Exception) { }
		serverWebSocket = null;
	}
	public final void removeAllPages() throws PermissionException
	{
		Security.check("io.web.server.rall", serverThread.getName());
		serverPages.clear();
	}
	public final void setAllowOnly(boolean AllowOnly) throws PermissionException
	{
		Security.check("io.web.sever.aext", serverThread.getName());
		serverRules = AllowOnly;
	}
	public final void setNoCookies(boolean NoCookies) throws PermissionException
	{
		Security.check("io.web.sever.nocookie", serverThread.getName());
		serverNoCookies = NoCookies;
	}
	public final void setErrorPage(WebPage Page) throws NullException, PermissionException
	{
		Security.check("io.web.server.err", serverThread.getName());
		if(Page == null) throw new NullException("Page");
		serverErrorPage = Page;
	}
	public final void setNotFoundPage(WebPage Page) throws NullException, PermissionException
	{
		Security.check("io.web.server.fnf", serverThread.getName());
		if(Page == null) throw new NullException("Page");
		serverNotFoundPage = Page;
	}
	public final void setServerPort(int ServerPort) throws NumberException, PermissionException
	{
		Security.check("io.web.server", Integer.valueOf(ServerPort));
		if(ServerPort < 0) throw new NumberException("ServerPort", ServerPort, false);
		if(Constants.MAX_USHORT_SIZE > ServerPort) throw new NumberException("ServerPort", ServerPort, 0, Constants.MAX_USHORT_SIZE);
		try
		{
			InetAddress a = serverWebSocket.getInetAddress();
			serverWebSocket.close();
			serverWebSocket = serverProvider != null ? serverProvider.createServerSocket(ServerPort, 500, a) : new ServerSocket(ServerPort, 500, a);
			a = null;
		}
		catch (IOException Excepion)
		{
			serverAlive = false;
		}
	}
	public final void setServerThreadCount(int ThreadCount) throws NumberException, PermissionException
	{
		Security.check("io.web.server.threads", serverThread.getName());
		if(ThreadCount < 0) throw new NumberException("ThreadCount", ThreadCount, false);
		if(ThreadCount > Short.MAX_VALUE) throw new NumberException("ThreadCount", ThreadCount, 0, Short.MAX_VALUE);
		if(ThreadCount == 0 && serverThreads != null)
		{
			for(short a = 0; a < serverThreads.length; a++)
				if(serverThreads[a] != null)
				{
					serverThreads[a].threadAlive = false;
					serverThreads[a] = null;
				}
			serverThreads = null;
			serverThread.setPriority(3);
		}
		else if(serverThreads == null && ThreadCount > 0)
		{
			serverThread.setPriority(2);
			serverThreads = new WebServerCThread[ThreadCount];
		}
		else if(serverThreads != null)
		{
			if(serverThreads.length > ThreadCount)
			{
				for(short c = (byte)ThreadCount; c < serverThreads.length; c++)
					if(serverThreads[c] != null)
					{
						serverThreads[c].threadAlive = false;
						serverThreads[c] = null;
					}
				serverThreads = Arrays.copyOf(serverThreads, ThreadCount);
			}
			else if(ThreadCount > serverThreads.length)
				serverThreads = Arrays.copyOf(serverThreads, ThreadCount);
		}
	}
	public final void setServerSessionTimeout(int ServerTimeout) throws NumberException, PermissionException
	{
		Security.check("io.web.server.tout", Integer.valueOf(ServerTimeout));
		if(ServerTimeout < 0) throw new NumberException("ServerTimeout", ServerTimeout, false);
		if(ServerTimeout > Short.MAX_VALUE) throw new NumberException("ServerTimeout", ServerTimeout, 0, Short.MAX_VALUE);
		serverSessionTime = (short)ServerTimeout;
	}
	public final void removePage(String WebPagePath) throws NullException, StringException, PermissionException
	{
		Security.check("io.web.server.rem", serverThread.getName());
		if(WebPagePath == null) throw new NullException("WebPagePath");
		if(WebPagePath.isEmpty()) throw new StringException("WebPagePath");
		serverPages.remove(WebPagePath);
	}
	public final void setAllowedFile(String HandledFileExt) throws NullException, StringException, PermissionException
	{
		if(HandledFileExt == null) throw new NullException("HandledFileExt");
		if(HandledFileExt.isEmpty()) throw new StringException("HandledFileExt");
		Security.check("io.web.server.aext", HandledFileExt);
		serverServePages.put(HandledFileExt.toLowerCase(), Boolean.TRUE);
	}
	public final void removeFileHandled(String HandledFileExt) throws NullException, StringException, PermissionException
	{
		if(HandledFileExt == null) throw new NullException("HandledFileExt");
		if(HandledFileExt.isEmpty()) throw new StringException("HandledFileExt");
		Security.check("io.web.server.aext", HandledFileExt);
		serverServePages.remove(HandledFileExt.toLowerCase());
	}
	public final void addPage(String WebPagePath, WebPage Page) throws NullException, StringException, PermissionException
	{
		Security.check("io.web.server.addp", WebPagePath);
		Security.check("io.web.server.add", serverThread.getName());
		if(Page == null) throw new NullException("Page");
		if(WebPagePath == null) throw new NullException("WebPagePath");
		if(WebPagePath.isEmpty()) throw new StringException("WebPagePath");
		serverPages.put(WebPagePath, Page);
	}
	public final void setDisallowedFile(String DisallowedFileExt) throws NullException, StringException, PermissionException
	{
		if(DisallowedFileExt == null) throw new NullException("DisallowedFileExt");
		if(DisallowedFileExt.isEmpty()) throw new StringException("DisallowedFileExt");
		Security.check("io.web.server.dext", DisallowedFileExt);
		serverServePages.put(DisallowedFileExt.toLowerCase(), Boolean.FALSE);
	}
	public final void setWebDirectory(String DirectoryPath) throws NullException, StringException, PermissionException, InternalException
	{
		Security.check("io.web.server.dir", serverThread.getName());
		if(DirectoryPath == null) throw new NullException("DirectoryPath");
		if(DirectoryPath.isEmpty()) throw new StringException("DirectoryPath");
		File a = new File(DirectoryPath);
		if(!a.isDirectory()) throw new InternalException("This is not a directory!");
		serverDirectory = a;
	}
	
	public final boolean isAlive()
	{
		return serverAlive;
	}
	public final boolean isSecure()
	{
		return serverProvider != null;
	}
	public final boolean handlesFile(String FileExt)
	{
		return FileExt != null && !FileExt.isEmpty() && serverServePages.containsKey(FileExt) && serverServePages.get(FileExt).booleanValue();
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof WebServer && ((WebServer)CompareObject).getPort() == serverWebSocket.getLocalPort();
	}
	public final boolean disallowsFile(String FileExt)
	{
		return FileExt != null && !FileExt.isEmpty() && serverServePages.containsKey(FileExt) && !serverServePages.get(FileExt).booleanValue();
	}
	public final boolean containsPage(String WebPagePath)
	{
		return WebPagePath != null && serverPages.containsKey(WebPagePath);
	}
	
	public final int getPort()
	{
		return serverWebSocket.getLocalPort();
	}
	public final int hashCode()
	{
		return serverWebSocket.getLocalPort() + serverSessionTime + serverWebSocket.hashCode() + serverPages.hashCode() + serverThread.hashCode();
	}
	public final int getPagesCount()
	{
		return serverPages.size();
	}
	public final int getSessionTimeout()
	{
		return serverSessionTime;
	}
	public final int getServerThreadCount()
	{
		return serverThreads != null ? serverThreads.length : 0;
	}
	
	public final String toString()
	{
		return "WebServer(WC) " + serverWebSocket.getLocalPort() + " / " + serverPages.size() + " / " + serverSessions.size();
	}
	
	public final SecurityProvider getSecurityProvider()
	{
		return serverProvider;
	}
	
	protected final WebServer clone() throws CloneException
	{
		throw new CloneException("Cannot clone WebServer instances!");
	}
	
	private final void doProcessing()
	{
		try
		{
			Socket a = serverWebSocket.accept();
			if(serverThreads != null)
			{
				for(short b = 0; b < serverThreads.length; b++)
				{
					if(serverThreads[b] == null)
					{
						serverThreads[b] = new WebServerCThread(this);
						serverThreads[b].threadSocket = a;
						break;
					}
					else if(!serverThreads[b].hasSocket())
					{
						serverThreads[b].threadSocket = a;
						break;
					}
					if((b + 1) >= serverThreads.length)
					{
						doServerProcess(a);
						break;
					}
				}
			}
			else
				doServerProcess(a);
		}
		catch (SocketTimeoutException Exception)
		{
			return;
		}
		catch (IOException Exception)
		{
			Reporter.error(Reporter.REPORTER_WEB, "Cannot establish a connection to host!", Exception);
			return;
		}
	}
	private final void doServerProcess(Socket ClientSocket)
	{
		InputStream a = null;
		OutputStream b= null;
		WebState c = null;
		try
		{
			a = ClientSocket.getInputStream();
			b = ClientSocket.getOutputStream();
		}
		catch (IOException Exception)
		{
			Reporter.error(Reporter.REPORTER_WEB, "Cannot establish a connection to host!", Exception);
			try
			{
				ClientSocket.close();
			}
			catch (IOException Exception1) { }
			return;
		}
		try
		{
			c = WebState.generateWebState(this, ClientSocket.getInetAddress(), a);//WebState.generateWebState(this, Host.getInetAddress(), a);
			if(c != null)
			{
				Reporter.debug(Reporter.REPORTER_WEB, "Client from \"" + ClientSocket.getInetAddress().getHostAddress() + "\" connected requesting page \"" + c.statePage + "\"");
				WebPage d = null;
				if(serverDirectory != null)
				{
					File e = new File(serverDirectory, c.statePage.replace("%20", " "));
					if(!e.isDirectory() && e.exists())
					{
						int f = c.statePage.lastIndexOf('/'), g = c.statePage.lastIndexOf('.');
						if(f >= 0 && g >= 0)// && (f > g || f == 0))
						{
							Boolean h = serverServePages.get(c.statePage.substring(g + 1).toLowerCase());
							if((!serverRules && (h == null || h.booleanValue())) || (serverRules && (h != null && h.booleanValue())))
								d = new WebContent(e.getAbsolutePath());
							else
								d = serverPages.get(c.statePage);
						}	
						else
							d = serverPages.get(c.statePage);
					}
					else
						d = serverPages.get(c.statePage);
					e = null;
				}
				else
					d = serverPages.get(c.statePage);
				if(d == null) d = serverNotFoundPage;
				if(c.statePost)
					d.onPagePost(c);
				else
					d.onPageGet(c);
				c.writeOutHeadersAndPage(b);
				d = null;
				c.invalidate();
				c = null;
			}
			a.close();
			b.close();
			ClientSocket.close();
		}
		catch (WebException Exception)
		{
			c = new WebState(false, null, a, this, ClientSocket.getInetAddress());
			try
			{
				c.setResponseMessage(Exception.exceptionMessage);
				serverErrorPage.onPageGet(c);
				c.writeOutHeadersAndPage(b);
				b.flush();
				ClientSocket.close();
			}
			catch (IOException Exception2)
			{
				Reporter.error(Reporter.REPORTER_WEB, "Failed to transfer data to host!", Exception2);
				try
				{
					ClientSocket.close();
				}
				catch (IOException Exception3)
				{
					a = null;
				}
			}
			c.invalidate();
			c = null;
		}
		catch (Throwable Exception)
		{
			System.out.println("!!!");
			Exception.printStackTrace();
			Reporter.error(Reporter.REPORTER_WEB, "There was an exception thrown when processing page, sent 500!", Exception);
			if(c == null)
				c = new WebState(false, null, a, this, ClientSocket.getInetAddress());
			else try
			{
				c.clearContent();
			}
			catch (IOException Exception1) { }
			try
			{
				serverErrorPage.onPageGet(c);
				c.writeOutHeadersAndPage(b);
				b.flush();
				ClientSocket.close();
			}
			catch (IOException Exception2)
			{
				Reporter.error(Reporter.REPORTER_WEB, "Failed to transfer data to host!", Exception2);
				try
				{
					ClientSocket.close();
				}
				catch (IOException Exception3)
				{
					a = null;
				}
			}
			c.invalidate();
			c = null;
		}
	}
	
	private static final class WebServerThread extends Thread
	{
		private final WebServer threadServer;
		
		public final void run()
		{
			long a = 0;
			while(threadServer.serverAlive)
			{
				if(!threadServer.serverSessions.isEmpty())
				{
					a = System.currentTimeMillis();
					boolean b = false;
					for(int c = 0; c < threadServer.serverSessions.size(); c++)
						if(a > threadServer.serverSessions.get(c).sessionExpires)
						{
							b = true;
							threadServer.serverSessions.remove(c);
						}
					if(b)
						System.gc();
				}
				threadServer.doProcessing();
			}
		}
		
		private WebServerThread(WebServer Server)
		{
			threadServer = Server;
			setName("WServer " + Server.getPort() + " ServerThread");
			setPriority(3);
			start();
		}
	}
	private static final class WebServerCThread extends Thread
	{
		private final WebServer threadServer;
		
		private Socket threadSocket;
		private boolean threadAlive;
		
		public final void run()
		{
			while(threadAlive && threadServer.serverAlive)
			{
				if(threadSocket != null)
				{
					threadServer.doServerProcess(threadSocket);
					threadSocket = null;
				}
				try
				{
					Thread.sleep(250);
				}
				catch (InterruptedException Exception) { }
			}
		}
		
		private final boolean hasSocket()
		{
			return threadSocket != null;
		}
		
		private WebServerCThread(WebServer Server)
		{
			threadAlive = true;
			threadServer = Server;
			setName("WServer " + Server.getPort() + " ContentThread");
			setPriority(2);
			start();
		}
	}
	private static final class WebServerDefaultError extends WebSpirePage
	{	
		protected final void getPage(WebState PageState) throws IOException
		{
			PageState.appendText("<div style=\"text-align: center;\"><h1 style=\"color: red;\">Internal Server Error (Error ");
			PageState.appendText(String.valueOf(PageState.getMessage().messageID));
			PageState.appendText(")</h1><div>There was an unknown error that occured that prevented the server from displaying this page properly.  <a href=\"#\" onClick=\"location.reload(true);");
			PageState.appendText("\">Refreshing this page</a> or trying again later may solve the problem.  If this error continues, please contact the server owner.</div></div>");
			PageState.setResponseMessage(WebMessage.SERVER_ERROR);
		}
		
		protected final String getPageTitle()
		{
			return "Internal Server Error";
		}
	
		private WebServerDefaultError() { }
	}
	private static final class WebServerDefaultFNFError extends WebSpirePage
	{	
		protected final void getPage(WebState PageState) throws IOException
		{
			PageState.appendText("<div style=\"text-align: center\"><h1 style=\"color:red\">File Not Found</h1><div>");
			PageState.appendText("The file you requested, \"");
			PageState.appendText(PageState.statePage);
			PageState.appendText("\" could not be found or you do not have permission to access this file. If this error continues, please contact the server owner.</div></div>");
			PageState.setResponseMessage(WebMessage.NOT_FOUND);
		}
		
		protected final String getPageTitle()
		{
			return "File Not Found";
		}
		
		private WebServerDefaultFNFError() { }
	}
}
