package com.spire.sec;

import java.util.HashMap;
import java.net.InetAddress;
import java.io.FileDescriptor;
import java.io.FilePermission;
import com.spire.log.Reporter;
import java.security.Permission;
/*import com.spire.sec.per.InetRule;
import com.spire.sec.per.ClassRule;
import com.spire.sec.per.ShortRule;*/
import com.spire.sec.per.StringRule;
import com.spire.sec.per.BooleanRule;
/*import com.spire.sec.per.IntegerRule;*/
import com.spire.ex.PermissionException;
import java.security.AccessControlContext;

public final class Security extends SecurityManager
{
	public static final Security SECURITY_MANAGER = new Security();
	
	private final /*Concurrent*/HashMap<String, SecurityItem<?>> securityPermissions;
	
	public final void checkSetFactory() throws PermissionException
	{
		if(!permissionValid("java.factory", permissionType("java.factory", StringRule.class) ? Thread.currentThread().getName() : Thread.currentThread().getClass()))
			throw new PermissionException("java.factory");
	}
	public final void checkPrintJobAccess() throws PermissionException
	{
		if(!permissionValid("java.print", null)) throw new PermissionException("java.print");
	}
	public final void checkPropertiesAccess() throws PermissionException
	{
		if(!permissionValid("java.properties", null)) throw new PermissionException("java.properties");
	}
	public final void checkExit(int ExitCode) throws PermissionException
	{
		if(!permissionValid("java.exit", Integer.valueOf(ExitCode))) throw new PermissionException("java.exit");
	}
	public final void checkCreateClassLoader() throws PermissionException
	{
		if(!permissionValid("java.classloader", permissionType("java.classloader", StringRule.class) ? Thread.currentThread().getName() : Thread.currentThread().getClass()))
			throw new PermissionException("java.classloader");
	}
	public final void checkRead(String FilePath) throws PermissionException
	{
		if(FilePath.contains("Java") || FilePath.contains("spire")) return;
		if(!permissionValid("io.file.read", FilePath)) throw new PermissionException("io.file.read");
	}
	public final void checkAwtEventQueueAccess() throws PermissionException
	{
		if(!permissionValid("gui.access", permissionType("gui.access", StringRule.class) ? Thread.currentThread().getName() : Thread.currentThread().getClass()))
			throw new PermissionException("gui.access");
	}
	public final void checkExec(String FilePath) throws PermissionException
	{
		if(!permissionValid("io.file.execute", FilePath)) throw new PermissionException("io.file.execute");
	}
	public final void checkWrite(String FilePath) throws PermissionException
	{
		if(!permissionValid("io.file.write", FilePath)) throw new PermissionException("io.file.write");
	}
	public final void checkSystemClipboardAccess() throws PermissionException
	{
		if(!permissionValid("java.clipboard", permissionType("java.clipboard", StringRule.class) ? Thread.currentThread().getName() : Thread.currentThread().getClass()))
			throw new PermissionException("java.clipboard");
	}
	public final void checkDelete(String FilePath) throws PermissionException
	{
		if(!permissionValid("io.file.delete", FilePath)) throw new PermissionException("io.file.delete");
	}
	public final void checkListen(int ConnectPort) throws PermissionException
	{
		if(!permissionValid("io.net.port", Integer.valueOf(ConnectPort))) throw new PermissionException("io.net.port");
	}
	public final void checkLink(String LibraryPath) throws PermissionException
	{
		if(!permissionValid("java.library", LibraryPath)) throw new PermissionException("java.library");
		if(!permissionValid("io.file.execute", LibraryPath)) throw new PermissionException("io.file.execute");
	}
	public final void checkAccess(Thread CheckThread) throws PermissionException
	{
		if(!permissionValid("java.thread.access", permissionType("java.thread.access", StringRule.class) ? CheckThread.getName() : CheckThread.getClass()))
			throw new PermissionException("java.thread.access");
	}
	public final void checkRead(FileDescriptor FilePath) throws PermissionException
	{
		if(!permissionValid("io.file.read", null)) throw new PermissionException("io.file.read");
	}
	public final void checkWrite(FileDescriptor FilePath) throws PermissionException
	{
		if(!permissionValid("io.file.write", null)) throw new PermissionException("io.file.write");
	}
	public final void checkPermission(Permission Permission) throws PermissionException
	{
		//if(Permission.getName().equals("setSecurityManager")) throw new PermissionException("Cannot overide the Spire SecurityManager!");
		if(!permissionValid("java.permission", Permission.getName().toLowerCase())) throw new PermissionException("java.permission");
	}
	public final void checkPackageAccess(String PackagePath) throws PermissionException
	{
		if(!permissionValid("java.package", PackagePath)) throw new PermissionException("java.package");
		//if(!permissionValid("io.file.execute", PackagePath)) throw new PermissionException("io.file.execute");
	}
	public final void checkPropertyAccess(String PropertyName) throws PermissionException
	{
		if(!permissionValid("java.properties.key", PropertyName)) throw new PermissionException("java.properties.key");
	}
	public final void checkAccess(ThreadGroup CheckThreadGroup) throws PermissionException
	{
		if(!permissionValid("java.thread.group.access", permissionType("java.thread.group.access", StringRule.class) ? CheckThreadGroup.getName() :
												        CheckThreadGroup.getClass())) throw new PermissionException("java.thread.group.access");
	}
	public final void checkPackageDefinition(String PackagePath) throws PermissionException
	{
		if(!permissionValid("java.package.define", PackagePath)) throw new PermissionException("java.package.define");
	}
	public final void checkRead(String FilePath, Object Context) throws PermissionException
	{
		if(Context instanceof AccessControlContext)
			((AccessControlContext)Context).checkPermission(new FilePermission(FilePath, "read"));
		else throw new PermissionException("io.file.read");
		if(!permissionValid("io.file.read", FilePath)) throw new PermissionException("io.file.read");
	}
	public final void checkSecurityAccess(String SecurityTarget) throws PermissionException
	{
		if(!permissionValid("java.security", SecurityTarget)) throw new PermissionException("java.security");
	}
	public final void checkMulticast(InetAddress MulticastAddress) throws PermissionException
	{
		if(!permissionValid("io.net.multicast", permissionType("io.net.multicast", StringRule.class) ? MulticastAddress.getHostAddress() : MulticastAddress))
			throw new PermissionException("io.net.multicast");
	}
	public final void checkAccept(String ConnectHost, int ConnectPort) throws PermissionException
	{
		if(!permissionValid("io.net.host", ConnectHost)) throw new PermissionException("io.net.host");
		if(!permissionValid("io.net.port", Integer.valueOf(ConnectPort))) throw new PermissionException("io.net.port");
	}
	public final void checkConnect(String ConnectHost, int ConnectPort) throws PermissionException
	{
		if(!permissionValid("io.net.host", ConnectHost)) throw new PermissionException("io.net.host");
		if(!permissionValid("io.net.port", Integer.valueOf(ConnectPort))) throw new PermissionException("io.net.port");
	}
	public final void checkPermission(Permission Permission, Object Context) throws PermissionException
	{
		if(Context instanceof AccessControlContext)
			((AccessControlContext)Context).checkPermission(Permission);
		else throw new PermissionException("java.permission");
		checkPermission(Permission);
	}
	public final void checkMemberAccess(Class<?> AccessClass, int StackDepth) throws PermissionException
	{
		if(!permissionValid("java.access", AccessClass)) throw new PermissionException("java.access");
		if(!permissionValid("java.access.depth", Integer.valueOf(StackDepth))) throw new PermissionException("java.access.depth");
	}
	public final void checkMulticast(InetAddress MulticastAddress, byte TimeToLive) throws PermissionException
	{
		checkMulticast(MulticastAddress);
	}
	public final void checkConnect(String ConnectHost, int ConnectPort, Object Context) throws PermissionException
	{
		if(Context == null) throw new PermissionException("io.net");
		checkConnect(ConnectHost, ConnectPort);
	}
	
	public final void resetSecuritySettings() throws PermissionException
	{
		resetSecuritySettings(null);
	}
	public final void resetSecuritySettings(Object Permissions) throws PermissionException
	{
		// On Domains need Runtime Permission, Need Admin / Spire Admin
	}
	
	public final boolean equals(Object CompareObject)
	{
		return false;
	}
	public final boolean checkTopLevelWindow(Object TopWindow)
	{
		return TopWindow != null && permissionValid("gui.show.toplevel", TopWindow.getClass());
	}
	
	public final int hashCode()
	{
		return -1;
	}
	
	public final String toString()
	{
		return "Spire Security Manager";
	}
	
	public static final void check(final String PermissionName) throws PermissionException
	{
		if(!SECURITY_MANAGER.permissionValid(PermissionName, null)) throw new PermissionException(PermissionName);
	}
	public static final void check(final String PermissionName, Object PermissionObject) throws PermissionException
	{
		if(!SECURITY_MANAGER.permissionValid(PermissionName, PermissionObject)) throw new PermissionException(PermissionName);
	}
	
	public static final boolean valid(final String PermissionName) throws PermissionException
	{
		return SECURITY_MANAGER.permissionValid(PermissionName, null);
	}
	public static final boolean valid(final String PermissionName, Object PermissionObject) throws PermissionException
	{
		return SECURITY_MANAGER.permissionValid(PermissionName, PermissionObject);
	}
	
	public static final SecurityItem<?> getPermission(final String PermissionName) throws PermissionException
	{
		if(PermissionName == null) return null;
		check("sec.get", PermissionName.toLowerCase());
		return SECURITY_MANAGER.securityPermissions.get(PermissionName);
	}
	
	protected final Security clone() throws PermissionException
	{
		throw new PermissionException(false, "Cannot Clone the Security Manager!");
	}
	
	private Security()
	{
		securityPermissions = new /*Concurrent*/HashMap<String, SecurityItem<?>>();
		//setDefaults(securityPermissions);
		try
		{
			System.setSecurityManager(this);
		}
		catch (SecurityException Exception)
		{
			Reporter.warning(Reporter.REPORTER_GLOBAL, "There was a problem setting the Spire Security Manager!  Custom security settings might not work!", Exception);
		}
	}
	
	private final boolean permissionValid(final String PermissionName, Object PermissionData) throws PermissionException
	{
		if(PermissionName == null || PermissionName.isEmpty()) return true;
		if(PermissionName.indexOf('.') > 0)
		{
			String a = null;
			String[] b = PermissionName.split("\\.", 5);
			StringBuilder c = new StringBuilder(PermissionName.length());
			for(byte d = 0; d < b.length - 1; d++)
			{
				if(d > 0) c.append('.');
				c.append(b[d]);
				a = c.toString();
				if(securityPermissions.containsKey(a) && securityPermissions.get(a) instanceof BooleanRule && !securityPermissions.get(a).isValid(PermissionData)) return false;
			}
		}
		if(securityPermissions.containsKey(PermissionName)) 
			return securityPermissions.get(PermissionName).isValid(PermissionData);
		return true;
	}
	/*
	private static final void setDefaults(/*Concurrent*//*HashMap<String, SecurityItem<?>> PermissionsMap)
	{
		PermissionsMap.put("io", new BooleanRule(true));
		PermissionsMap.put("io.net", new BooleanRule(true));
		PermissionsMap.put("io.net.post", new ShortRule());
		PermissionsMap.put("io.net.host", new StringRule());
		PermissionsMap.put("io.stream", new BooleanRule(true));
		PermissionsMap.put("io.stream.data", new BooleanRule(true));
		PermissionsMap.put("io.stream.secure", new BooleanRule(true));
		PermissionsMap.put("java.package", new StringRule());
		PermissionsMap.put("java.security", new StringRule());
		PermissionsMap.put("java.factory", new ClassRule());
		PermissionsMap.put("java.clipboard", new ClassRule());
		PermissionsMap.put("java.package.define", new StringRule());
		PermissionsMap.put("java.permission", new StringRule());
		PermissionsMap.put("java.print", new BooleanRule(true));
		PermissionsMap.put("java.properties", new BooleanRule(true));
		PermissionsMap.put("java.properties.key", new StringRule());
		PermissionsMap.put("io.net.multicast", new InetRule());
		PermissionsMap.put("io.file", new BooleanRule(true));
		PermissionsMap.put("io.file.read", new StringRule());
		PermissionsMap.put("io.file.delete", new StringRule());
		PermissionsMap.put("io.file.execute", new StringRule());
		PermissionsMap.put("io.net", new BooleanRule(true));
		PermissionsMap.put("java", new BooleanRule(true));
		PermissionsMap.put("java.thread", new BooleanRule(true));
		PermissionsMap.put("java.exit", new ShortRule());
		PermissionsMap.put("java.library", new StringRule());
		PermissionsMap.put("java.access", new ClassRule());
		PermissionsMap.put("java.access.depth", new ShortRule());
		PermissionsMap.put("java.thread.access", new ClassRule());
		PermissionsMap.put("java.thread.group", new BooleanRule(true));
		PermissionsMap.put("java.thread.group.access", new ClassRule());
		PermissionsMap.put("java.classloader", new ClassRule());
		PermissionsMap.put("gui", new BooleanRule(true));
		PermissionsMap.put("gui.access", new ClassRule());
		PermissionsMap.put("gui.show", new StringRule());
		PermissionsMap.put("gui.show.toplevel", new ClassRule());
		PermissionsMap.put("log", new BooleanRule(true));
		PermissionsMap.put("log.handles", new StringRule());
		PermissionsMap.put("log.clear", new BooleanRule(true));
		PermissionsMap.put("log.add", new ClassRule());
		PermissionsMap.put("log.remove", new ClassRule());
		PermissionsMap.put("log.create", new StringRule());
		PermissionsMap.put("sec", new BooleanRule(true));
		PermissionsMap.put("sec.get", new StringRule());
		
		PermissionsMap.put("io.web.server", new IntegerRule());
		PermissionsMap.put("io.web.server.aext", new StringRule());
	}*/

	private static final boolean permissionType(final String PermissionName, Class<? extends SecurityItem<?>> PermissionType)
	{
		return SECURITY_MANAGER.securityPermissions.containsKey(PermissionName) && SECURITY_MANAGER.securityPermissions.get(PermissionName).getClass().isAssignableFrom(PermissionType);
	}
}