package com.spire.web;

import java.io.IOException;
import java.util.ArrayList;
import com.spire.log.Reporter;
import com.spire.sec.Security;
import com.spire.util.HashKey;
import com.spire.util.HashList;
import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import com.spire.ex.StringException;
import com.spire.ex.PermissionException;

public final class WebLogin
{
	private static final String LOGIN_KEY = "spire_login_active";
	private static final WebPage DEFAULT_PAGE = new LoginDefault();
	
	private final ArrayList<WebVerifier> pageVerifiers;
	private final HashList<String, LoginPage> pageMappings;

	private WebPage pageLogin;
	
	public WebLogin() throws PermissionException
	{
		Security.check("io.web.login");
		pageLogin = DEFAULT_PAGE;
		pageVerifiers = new ArrayList<WebVerifier>();
		pageMappings = new HashList<String, LoginPage>();
	}
	public WebLogin(WebPage LoginPage) throws NullException, PermissionException
	{
		this();
		setLoginPage(LoginPage);
	}
	
	public final void clearVerfiers() throws PermissionException
	{
		Security.check("io.web.login.verrall");
		pageVerifiers.clear();
	}
	public final void removeAllPages() throws PermissionException
	{
		Security.check("io.web.login.rall");
		pageMappings.clear();
	}
	public final void setLoginPage(WebPage LoginPage) throws NullException, PermissionException
	{
		if(LoginPage == null) throw new NullException("LoginPage");
		Security.check("io.web.login.page", LoginPage.getClass());
		pageLogin = LoginPage;		
	}
	public final void addVefifier(WebVerifier Verifier) throws NullException, PermissionException
	{
		if(Verifier == null) throw new NullException("Verifier");
		Security.check("io.web.login.ver", Verifier.getClass());
		pageVerifiers.add(Verifier);
	}
	public final void removeVefifier(WebVerifier Verifier) throws NullException, PermissionException
	{
		if(Verifier == null) throw new NullException("Verifier");
		Security.check("io.web.login.vera", Verifier.getClass());
		pageVerifiers.remove(Verifier);
	}
	public final void addPagesToServer(WebServer WebServer) throws NullException, PermissionException
	{
		Security.check("io.web.login.adds");
		if(WebServer == null) throw new NullException("WebServer");
		if(pageMappings.size() > 0) for(int a = 0; a < pageMappings.size(); a++)
				WebServer.addPage(pageMappings.get(a).getKey(), pageMappings.get(a));
	}
	public final void removePage(String WebPagePath) throws NullException, StringException, PermissionException
	{
		Security.check("io.web.login.rem");
		if(WebPagePath == null) throw new NullException("WebPagePath");
		if(WebPagePath.isEmpty()) throw new StringException("WebPagePath");
		pageMappings.remove(WebPagePath);
	}
	public final void removeVefifier(int VerifierIndex) throws NullException, PermissionException, NumberException
	{
		if(VerifierIndex < 0) throw new NumberException("VerifierIndex", VerifierIndex, false);
		if(VerifierIndex > pageVerifiers.size()) throw new NumberException("VerifierIndex", VerifierIndex, 0, pageVerifiers.size());
		WebVerifier a = pageVerifiers.get(VerifierIndex);
		if(a != null)
		{
			Security.check("io.web.login.verr", a.getClass());
			pageVerifiers.remove(VerifierIndex);
			a = null;
		}
	}
	public final void addPage(String WebPagePath, WebPage Page) throws NullException, StringException, PermissionException
	{
		Security.check("io.web.login.add", WebPagePath);
		if(Page == null) throw new NullException("Page");
		if(WebPagePath == null) throw new NullException("WebPagePath");
		if(WebPagePath.isEmpty()) throw new StringException("WebPagePath");
		pageMappings.putElement(WebPagePath, new LoginPage(this, WebPagePath, Page));
	}
	
	public final boolean containsPages()
	{
		return !pageMappings.isEmpty();
	}
	public final boolean containsVerifiers()
	{
		return !pageVerifiers.isEmpty();
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof WebLogin && ((WebLogin)CompareObject).pageLogin.equals(pageLogin) && ((WebLogin)CompareObject).pageMappings.equals(pageMappings) && ((WebLogin)CompareObject).pageVerifiers.equals(pageVerifiers);
	}
	
	public final int hashCode()
	{
		return pageVerifiers.hashCode() + pageMappings.hashCode() + pageLogin.hashCode();
	}
	public final int getPageCount()
	{
		return pageMappings.size();
	}
	public final int getVerifierCount()
	{
		return pageVerifiers.size();
	}
	
	public final String toString()
	{
		return "WebLogin(SF) [V:" + pageVerifiers.size() + ";M:" + pageMappings.size() + "]";
	}
	
	public final WebVerifier[] getVerifiers()
	{
		return pageVerifiers.toArray(new WebVerifier[pageVerifiers.size()]);
	}
	
	public final WebLogin clone()
	{
		WebLogin a = new WebLogin();
		a.pageLogin = pageLogin;
		a.pageMappings.addAll(pageMappings);
		a.pageVerifiers.addAll(pageVerifiers);
		return a;
	}
	
	public static final void clearLogin(WebState PageState) throws NullException, PermissionException
	{
		Security.check("io.web.login.logoff");
		if(PageState == null) throw new NullException("PageState");
		Object a = PageState.getSession(LOGIN_KEY);
		if(a instanceof WebIdentity)
		{
			Reporter.debug(Reporter.REPORTER_WEB, "Removing login for identity \"" + ((WebIdentity)a).getName() + "\"!");
			PageState.removeSession(LOGIN_KEY);
		}
	}
	
	private final void pageLogin(WebPage RequestPage, WebState PageState) throws IOException
	{
		WebIdentity a = null;
		Reporter.debug(Reporter.REPORTER_WEB, "Page requested is secured by login, trying to verify");
		for(int b = 0; b < pageVerifiers.size(); b++)
		{
			a = pageVerifiers.get(b).verifyIdentity(PageState);
			if(a != null)
			{
				PageState.setSession(LOGIN_KEY, a);
				Reporter.debug(Reporter.REPORTER_WEB, "Verify passed, identity \"" + a.getName() + "\" was logged in!");
				if(PageState.isPost()) RequestPage.onPagePost(PageState);
				else RequestPage.onPageGet(PageState);
				return;
			}
		}
		if(PageState.isPost()) pageLogin.onPagePost(PageState);
		else pageLogin.onPageGet(PageState);
	}
	
	private static final class LoginDefault extends WebSpirePage
	{
		protected final void getPage(WebState PageState) throws IOException
		{
			StringBuilder a = new StringBuilder();
			a.append("<div style=\"text-align:center;\">Please login to proceed to \"");
			a.append(PageState.statePage);
			a.append("\"<br/><hr/><form method=\"post\"><table style=\"margin-left:auto;margin-right:auto;\"><tr><td>Username:</td><td><input type=\"text\" name=\"spire_usern\" /></td></tr>");
			a.append("<tr><td>Password:</td><td><input type=\"password\" name=\"spire_userp\" /></td></tr></table><input type=\"submit\" value=\"Login\" name=\"spire_userl\" /></form></div>");
			PageState.appendText(a);
		}

		protected final String getPageTitle()
		{
			return "Login";
		}
		
		private LoginDefault() { }
	}
	private static final class LoginPage implements HashKey<String>, WebPage
	{
		private final String pagePath;
		private final WebLogin pageHost;
		private final WebPage pageInstance;
		
		public final void onPageGet(WebState PageState) throws IOException
		{
			if(PageState.getSession(LOGIN_KEY) instanceof WebIdentity)
				pageInstance.onPageGet(PageState);
			else pageHost.pageLogin(pageInstance, PageState);
		}
		public final void onPagePost(WebState PageState) throws IOException
		{
			if(PageState.getSession(LOGIN_KEY) instanceof WebIdentity)
				pageInstance.onPagePost(PageState);
			else pageHost.pageLogin(pageInstance, PageState);
		}
		
		public final String getKey()
		{
			return pagePath;
		}
		
		private LoginPage(WebLogin Host, String Path, WebPage Page)
		{
			pagePath = Path;
			pageHost = Host;
			pageInstance = Page;
		}

	}
}