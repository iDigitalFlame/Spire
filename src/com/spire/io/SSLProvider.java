package com.spire.io;

import java.net.Socket;
import java.util.Arrays;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.security.Provider;

import com.spire.log.Reporter;
import com.spire.sec.Security;
import com.spire.util.BoolTag;

import javax.net.ssl.SSLSocket;

import java.io.FileInputStream;

import com.spire.util.Constants;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import com.spire.cred.Credentials;
import com.spire.ex.NullException;
import com.spire.ex.CloneException;
import com.spire.ex.NumberException;
import com.spire.ex.StringException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocketFactory;

import java.security.KeyStoreException;

import javax.net.ssl.KeyManagerFactory;

import com.spire.ex.PermissionException;

import javax.net.ssl.TrustManagerFactory;

import java.security.KeyManagementException;

import javax.net.ssl.SSLServerSocketFactory;

import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public final class SSLProvider implements SecurityProvider
{
	private static final byte OPS_DEFAULT = 1;
	private static final String CRED_USERNAME = "SSL";
	
	public static final String ENCRYPTION_DEFAULT = "TLS";
	public static final String FACTORY_DEFAULT = "SunX509";
	/**
	 * This is the default provider for SSL.<br/><br/>This allows for a connection to a trusted SSL Source such as a SSL-Based Tunnel sources.<br/><b>  
	 * Certificates that are used with the default provider must be trusted on the computer or by Java in order to allow the connection</b>,  Otherwise
	 * the Socket generated will throw an exception.
	 */
	public static final SecurityProvider SSL_DEFAULT = new SSLDefault();
	public static final Provider PROVIDERKEY_DEFAULT = java.security.Security.getProvider("SUN");
	public static final Provider PROVIDER_DEFAULT = java.security.Security.getProvider("SunJSSE");
	public static final String[] PROVIDER_MAX_SECURITY = { "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384", 
														   "TLS_RSA_WITH_AES_256_CBC_SHA256", "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384", "TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384",
														   "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256", "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256", "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",
														   "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", "TLS_RSA_WITH_AES_256_CBC_SHA", "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA", 
														   "TLS_ECDH_RSA_WITH_AES_256_CBC_SHA", "TLS_DHE_RSA_WITH_AES_256_CBC_SHA", "TLS_DHE_DSS_WITH_AES_256_CBC_SHA" };
	
	private final BoolTag providerOps;
	private final String providerType;
	private final String providerCertificate;
	
	private SSLSet providerSet;
	private Provider providerBase;
	private String[] providerTypes;
	private String providerFactory;
	private Provider providerKeyBase;
	private String providerEncryption;
	private TrustManager[] providerTrust;
	private Credentials providerIdentity;
	private SSLSocketFactory providerSoFact;
	private SSLServerSocketFactory providerSSoFact;
	
	public SSLProvider() throws PermissionException, IOException
	{
		this(null, null, (Credentials)null, ENCRYPTION_DEFAULT, FACTORY_DEFAULT, PROVIDER_DEFAULT, PROVIDERKEY_DEFAULT);
	}
	public SSLProvider(String SSLEncryption) throws PermissionException, IOException
	{
		this(null, null, (Credentials)null, SSLEncryption, FACTORY_DEFAULT, PROVIDER_DEFAULT, PROVIDERKEY_DEFAULT);
	}
	public SSLProvider(String SSLEncryption, String SSLFactoryName) throws PermissionException, IOException
	{
		this(null, null, (Credentials)null, SSLEncryption, SSLFactoryName, PROVIDER_DEFAULT, PROVIDERKEY_DEFAULT);
	}
	public SSLProvider(String SSLEncryption, String SSLFactoryName, Provider SSLBaseProvider) throws PermissionException, IOException
	{
		this(null, null, (Credentials)null, SSLEncryption, SSLFactoryName, SSLBaseProvider, PROVIDERKEY_DEFAULT);
	}
	public SSLProvider(String SSLCertificate, String CertificateType, String CertificateIdentity) throws PermissionException, IOException
	{
		this(SSLCertificate, CertificateType, new Credentials(CRED_USERNAME, CertificateIdentity), ENCRYPTION_DEFAULT, FACTORY_DEFAULT, PROVIDER_DEFAULT, PROVIDERKEY_DEFAULT);
	}
	public SSLProvider(String SSLCertificate, String CertificateType, Credentials CertificateIdentity) throws PermissionException, IOException
	{
		this(SSLCertificate, CertificateType, CertificateIdentity, ENCRYPTION_DEFAULT, FACTORY_DEFAULT, PROVIDER_DEFAULT, PROVIDERKEY_DEFAULT);
	}
	public SSLProvider(String SSLCertificate, String CertificateType, String CertificateIdentity, String SSLEncryption) throws PermissionException, IOException
	{
		this(SSLCertificate, CertificateType, new Credentials(CRED_USERNAME, CertificateIdentity), SSLEncryption, FACTORY_DEFAULT, PROVIDER_DEFAULT, PROVIDERKEY_DEFAULT);
	}
	public SSLProvider(String SSLCertificate, String CertificateType, Credentials CertificateIdentity, String SSLEncryption) throws PermissionException, IOException
	{
		this(SSLCertificate, CertificateType, CertificateIdentity, SSLEncryption, FACTORY_DEFAULT, PROVIDER_DEFAULT, PROVIDERKEY_DEFAULT);
	}
	public SSLProvider(String SSLEncryption, String SSLFactoryName, Provider SSLBaseProvider, TrustManager[] SSLTrustManagers) throws PermissionException, NullException, IOException
	{
		this(null, null, (Credentials)null, SSLEncryption, SSLFactoryName, SSLBaseProvider, PROVIDERKEY_DEFAULT);
		setTrustManagers(SSLTrustManagers);
	}
	public SSLProvider(String SSLCertificate, String CertificateType, String CertificateIdentity, String SSLEncryption, String SSLFactoryName) throws PermissionException, IOException
	{
		this(SSLCertificate, CertificateType, new Credentials(CRED_USERNAME, CertificateIdentity), SSLEncryption, SSLFactoryName, PROVIDER_DEFAULT, PROVIDERKEY_DEFAULT);
	}
	public SSLProvider(String SSLCertificate, String CertificateType, Credentials CertificateIdentity, String SSLEncryption, String SSLFactoryName) throws PermissionException, IOException
	{
		this(SSLCertificate, CertificateType, CertificateIdentity, SSLEncryption, SSLFactoryName, PROVIDER_DEFAULT, PROVIDERKEY_DEFAULT);
	}
	public SSLProvider(String SSLCertificate, String CertificateType, String CertificateIdentity, String SSLEncryption, String SSLFactoryName, Provider SSLBaseProvider) throws PermissionException, IOException
	{
		this(SSLCertificate, CertificateType, new Credentials(CRED_USERNAME, CertificateIdentity), SSLEncryption, SSLFactoryName, SSLBaseProvider, PROVIDERKEY_DEFAULT);
	}
	public SSLProvider(String SSLCertificate, String CertificateType, Credentials CertificateIdentity, String SSLEncryption, String SSLFactoryName, Provider SSLBaseProvider) throws PermissionException, IOException
	{
		this(SSLCertificate, CertificateType, CertificateIdentity, SSLEncryption, SSLFactoryName, SSLBaseProvider, PROVIDERKEY_DEFAULT);
	}
	public SSLProvider(String SSLCertificate, String CertificateType, String CertificateIdentity, String SSLEncryption, String SSLFactoryName, Provider SSLBaseProvider, Provider SSLKeyProvider) throws PermissionException, IOException
	{
		this(SSLCertificate, CertificateType, new Credentials(CRED_USERNAME, CertificateIdentity), SSLEncryption, SSLFactoryName, SSLBaseProvider, SSLKeyProvider);
	}
	public SSLProvider(String SSLCertificate, String CertificateType, Credentials CertificateIdentity, String SSLEncryption, String SSLFactoryName, Provider SSLBaseProvider, Provider SSLKeyProvider) throws PermissionException, IOException
	{
		Security.check("io.net.ssl.create");
		if(SSLCertificate != null && !Stream.fileExists(SSLCertificate)) throw new IOException("The Certificate \"" + SSLCertificate + "\" does not exist!");
		providerType = CertificateType;
		providerBase = SSLBaseProvider;
		providerKeyBase = SSLKeyProvider;
		providerFactory = SSLFactoryName;
		providerEncryption = SSLEncryption;
		providerCertificate = SSLCertificate;
		providerOps = new BoolTag(OPS_DEFAULT);
		providerIdentity = CertificateIdentity;
	}
	
	public final void clearSet() throws PermissionException
	{
		Security.check("io.net.ssl.clear.set");
		providerSet = null;
		providerSoFact = null;
		providerSSoFact = null;
	}
	public final void clearCiphers() throws PermissionException
	{
		Security.check("io.net.ssl.clear.cipher");
		providerTypes = null;
	}
	public final void clearTrustManagers() throws PermissionException
	{
		Security.check("io.net.ssl.clear.trust");
		providerTrust = null;
	}
	public final void setWantClientAuth(boolean WantAuth) throws PermissionException
	{
		Security.check("io.net.ssl.set.wca");
		providerOps.setTagC(WantAuth);
	}
	public final void setNeedClientAuth(boolean NeedAuth) throws PermissionException
	{
		Security.check("io.net.ssl.set.nca");
		providerOps.setTagB(NeedAuth);
	}
	public final void setProviderBase(Provider BaseProvider) throws PermissionException
	{
		if(BaseProvider != null) Security.check("io.net.ssl.set.base", BaseProvider.getClass());
		else Security.check("io.net.ssl.set.base");
		providerBase = BaseProvider;
	}
	public final void setProviderKeyBase(Provider BaseKeyProvider) throws PermissionException
	{
		if(BaseKeyProvider != null) Security.check("io.net.ssl.set.kbase", BaseKeyProvider.getClass());
		else Security.check("io.net.ssl.set.kbase");
		providerKeyBase = BaseKeyProvider;
	}
	public final void setEnableSessionCreation(boolean SessionCreation) throws PermissionException
	{
		Security.check("io.net.ssl.set.esc");
		providerOps.setTagA(SessionCreation);
	}
	public final void setProviderFactory(String FactoryName) throws PermissionException, StringException
	{
		if(FactoryName != null && FactoryName.isEmpty()) throw new StringException("FactoryName");
		if(FactoryName != null) Security.check("io.net.ssl.set.fact", FactoryName);
		else Security.check("io.net.ssl.set.fact");
		providerFactory = FactoryName;
	}
	public final void setProviderSecurityType(String SecurityType) throws PermissionException, StringException
	{
		if(SecurityType != null && SecurityType.isEmpty()) throw new StringException("SecurityType");
		if(SecurityType != null) Security.check("io.net.ssl.set.enc", SecurityType);
		else Security.check("io.net.ssl.set.enc");
		providerEncryption = SecurityType;
	}
	public final void setTrustManagers(TrustManager[] TrustManagers) throws PermissionException, NullException
	{
		if(TrustManagers != null)
			for(int a = 0; a < TrustManagers.length; a++)
			{
				if(TrustManagers[a] == null) throw new NullException("TrustManagers[" + a + "]");
				Security.check("io.net.ssl.set.trust", TrustManagers[a].getClass());
			}
		else Security.check("io.net.ssl.set.trust");
		providerTrust = TrustManagers;
	}
	public final void setSupportedCiphers(String[] SupportedCiphers) throws PermissionException, StringException, NullException
	{
		if(SupportedCiphers != null)
			for(int a = 0; a < SupportedCiphers.length; a++)
			{
				if(SupportedCiphers[a] == null) throw new NullException("SupportedCiphers[" + a + "]");
				if(SupportedCiphers[a].isEmpty()) throw new StringException("SupportedCiphers[" + a + "]");
				Security.check("io.net.ssl.set.ciphers", SupportedCiphers[a]);
			}
		else Security.check("io.net.ssl.set.ciphers");
		providerTypes = SupportedCiphers;
	}
	
	public final boolean isIntilized()
	{
		return providerSet != null;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof SSLProvider && ((SSLProvider)CompareObject).providerCertificate.equals(providerCertificate) && ((SSLProvider)CompareObject).providerEncryption.equals(providerEncryption) &&
			   ((SSLProvider)CompareObject).providerType.equals(providerType) && CompareObject.hashCode() == hashCode();
	}
	
	public final int hashCode()
	{
		return providerOps.hashCode() + (providerFactory.hashCode() + providerEncryption.hashCode() * providerIdentity.hashCode()) + providerCertificate.hashCode() + providerType.hashCode() + Arrays.hashCode(providerTrust) + Arrays.hashCode(providerTypes);
	}
	
	public final String toString()
	{
		return "SSLProvider(SP) " + (providerType != null ? providerType : "NT") + ";" + (providerEncryption != null ? providerEncryption : "NENC") + ";" + (providerFactory != null ? providerFactory : "DEF");
	}
	public final String getProviderName()
	{
		return CRED_USERNAME;
	}
	public final String getProviderType()
	{
		return providerType != null ? providerType : "NT";
	}
	public final String getProviderEncryption()
	{
		return providerEncryption != null ? providerEncryption : "NENC";
	}
	public final String getSupportedCipher(int CipherIndex) throws NumberException
	{
		if(providerTypes == null) return null;
		if(CipherIndex < 0) throw new NumberException("CipherIndex", CipherIndex, false);
		if(CipherIndex > providerTypes.length) throw new NumberException("CipherIndex", CipherIndex, 0, providerTypes.length);
		return providerTypes[CipherIndex];
	}
	
	public final TrustManager getTrustManager(int TrustIndex) throws NumberException
	{
		if(providerTrust == null) return null;
		if(TrustIndex < 0) throw new NumberException("TrustIndex", TrustIndex, false);
		if(TrustIndex > providerTrust.length) throw new NumberException("TrustIndex", TrustIndex, 0, providerTrust.length);
		return providerTrust[TrustIndex];
	}
	
	public final String[] getSupportedCiphers()
	{
		return providerTypes;
	}
	
	public final TrustManager[] getTrustManagers()
	{
		return providerTrust;
	}
	
	public final Socket createSocket() throws IOException, PermissionException
	{
		Security.check("io.net.ssl.client");
		prepareSocket();
		Reporter.info(Reporter.REPORTER_SECURITY, "Creating a SSL Socket, type \"" + getProviderType() + "\" enc \"" + getProviderEncryption() + "\"");
		SSLSocket a = (SSLSocket)providerSoFact.createSocket();
		a.setEnabledCipherSuites(providerTypes);
		a.setEnableSessionCreation(providerOps.getTagA());
		a.setNeedClientAuth(providerOps.getTagB());
		a.setWantClientAuth(providerOps.getTagC());
		return a;
	}
	public final Socket createSocket(InetAddress ConnectAddress, int PortNumber) throws IOException, NumberException, NullException, PermissionException
	{
		Security.check("io.net.ssl.client");
		if(ConnectAddress == null) throw new NullException("ConnectAddress");
		if(PortNumber < 0) throw new NumberException("PortNumber", PortNumber, false);
		if(PortNumber > Constants.MAX_USHORT_SIZE) throw new NumberException("PortNumber", PortNumber, 0, Constants.MAX_USHORT_SIZE);
		prepareSocket();
		Reporter.info(Reporter.REPORTER_SECURITY, "Creating a SSL Socket, type \"" + getProviderType() + "\" enc \"" + getProviderEncryption() + "\"");
		SSLSocket a = (SSLSocket)providerSoFact.createSocket(ConnectAddress, PortNumber);
		a.setEnabledCipherSuites(providerTypes);
		a.setEnableSessionCreation(providerOps.getTagA());
		a.setNeedClientAuth(providerOps.getTagB());
		a.setWantClientAuth(providerOps.getTagC());
		return a;
	}
	public final Socket createSocket(String ConnectAddress, int PortNumber) throws IOException, NumberException, NullException, StringException, PermissionException
	{
		Security.check("io.net.ssl.client");
		if(ConnectAddress == null) throw new NullException("ConnectAddress");
		if(ConnectAddress.isEmpty()) throw new StringException("ConnectAddress");
		if(PortNumber < 0) throw new NumberException("PortNumber", PortNumber, false);
		if(PortNumber > Constants.MAX_USHORT_SIZE) throw new NumberException("PortNumber", PortNumber, 0, Constants.MAX_USHORT_SIZE);
		prepareSocket();
		Reporter.info(Reporter.REPORTER_SECURITY, "Creating a SSL Socket, type \"" + getProviderType() + "\" enc \"" + getProviderEncryption() + "\"");
		SSLSocket a = (SSLSocket)providerSoFact.createSocket(ConnectAddress, PortNumber);
		a.setEnabledCipherSuites(providerTypes);
		a.setEnableSessionCreation(providerOps.getTagA());
		a.setNeedClientAuth(providerOps.getTagB());
		a.setWantClientAuth(providerOps.getTagC());
		return a;
	}
	public final Socket createSocket(InetAddress ConnectAddress, int ConnectPort, InetAddress LocalAddress, int LocalPort) throws IOException, NumberException, NullException, PermissionException
	{
		Security.check("io.net.ssl.client");
		if(ConnectAddress == null) throw new NullException("ConnectAddress");
		if(LocalPort < 0) throw new NumberException("LocalPort", LocalPort, false);
		if(ConnectPort < 0) throw new NumberException("ConnectPort", ConnectPort, false);
		if(LocalPort > Constants.MAX_USHORT_SIZE) throw new NumberException("LocalPort", LocalPort, 0, Constants.MAX_USHORT_SIZE);
		if(ConnectPort > Constants.MAX_USHORT_SIZE) throw new NumberException("ConnectPort", ConnectPort, 0, Constants.MAX_USHORT_SIZE);
		prepareSocket();
		Reporter.info(Reporter.REPORTER_SECURITY, "Creating a SSL Socket, type \"" + getProviderType() + "\" enc \"" + getProviderEncryption() + "\"");
		SSLSocket a = (SSLSocket)providerSoFact.createSocket(ConnectAddress, ConnectPort, LocalAddress, LocalPort);
		a.setEnabledCipherSuites(providerTypes);
		a.setEnableSessionCreation(providerOps.getTagA());
		a.setNeedClientAuth(providerOps.getTagB());
		a.setWantClientAuth(providerOps.getTagC());
		return a;
	}
	public final Socket createSocket(String ConnectAddress, int ConnectPort, InetAddress LocalAddress, int LocalPort) throws IOException, NumberException, NullException, StringException, PermissionException
	{
		Security.check("io.net.ssl.client");
		if(ConnectAddress == null) throw new NullException("ConnectAddress");
		if(ConnectAddress.isEmpty()) throw new StringException("Connectddress");
		if(LocalPort < 0) throw new NumberException("LocalPort", LocalPort, false);
		if(ConnectPort < 0) throw new NumberException("ConnectPort", ConnectPort, false);
		if(LocalPort > Constants.MAX_USHORT_SIZE) throw new NumberException("LocalPort", LocalPort, 0, Constants.MAX_USHORT_SIZE);
		if(ConnectPort > Constants.MAX_USHORT_SIZE) throw new NumberException("ConnectPort", ConnectPort, 0, Constants.MAX_USHORT_SIZE);
		prepareSocket();
		Reporter.info(Reporter.REPORTER_SECURITY, "Creating a SSL Socket, type \"" + getProviderType() + "\" enc \"" + getProviderEncryption() + "\"");
		SSLSocket a = (SSLSocket)providerSoFact.createSocket(ConnectAddress, ConnectPort, LocalAddress, LocalPort);
		a.setEnabledCipherSuites(providerTypes);
		a.setEnableSessionCreation(providerOps.getTagA());
		a.setNeedClientAuth(providerOps.getTagB());
		a.setWantClientAuth(providerOps.getTagC());
		return a;
	}
	
	public final ServerSocket createServerSocket() throws IOException, PermissionException
	{
		Security.check("io.net.ssl.server");
		prepareServerSocket();
		Reporter.info(Reporter.REPORTER_SECURITY, "Creating a SSL ServerSocket, type \"" + getProviderType() + "\" enc \"" + getProviderEncryption() + "\"");
		SSLServerSocket a = (SSLServerSocket)providerSSoFact.createServerSocket();
		a.setEnabledCipherSuites(providerTypes);
		a.setEnableSessionCreation(providerOps.getTagA());
		a.setNeedClientAuth(providerOps.getTagB());
		a.setWantClientAuth(providerOps.getTagC());
		return a;
	}
	public final ServerSocket createServerSocket(int PortNumber) throws IOException, NumberException, PermissionException
	{
		Security.check("io.net.ssl.server");
		if(PortNumber < 0) throw new NumberException("PortNumber", PortNumber, false);
		if(PortNumber > Constants.MAX_USHORT_SIZE) throw new NumberException("PortNumber", PortNumber, 0, Constants.MAX_USHORT_SIZE);
		prepareServerSocket();
		Reporter.info(Reporter.REPORTER_SECURITY, "Creating a SSL ServerSocket, type \"" + getProviderType() + "\" enc \"" + getProviderEncryption() + "\"");
		SSLServerSocket a = (SSLServerSocket)providerSSoFact.createServerSocket(PortNumber);
		a.setEnabledCipherSuites(providerTypes);
		a.setEnableSessionCreation(providerOps.getTagA());
		a.setNeedClientAuth(providerOps.getTagB());
		a.setWantClientAuth(providerOps.getTagC());
		return a;
	}
	public final ServerSocket createServerSocket(int PortNumber, int QueueBacklog) throws IOException, NumberException, PermissionException
	{
		Security.check("io.net.ssl.server");
		if(PortNumber < 0) throw new NumberException("PortNumber", PortNumber, false);
		if(QueueBacklog < 0) throw new NumberException("QueueBacklog", QueueBacklog, false);
		if(PortNumber > Constants.MAX_USHORT_SIZE) throw new NumberException("PortNumber", PortNumber, 0, Constants.MAX_USHORT_SIZE);
		prepareServerSocket();
		Reporter.info(Reporter.REPORTER_SECURITY, "Creating a SSL ServerSocket, type \"" + getProviderType() + "\" enc \"" + getProviderEncryption() + "\"");
		SSLServerSocket a = (SSLServerSocket)providerSSoFact.createServerSocket(PortNumber, QueueBacklog);
		a.setEnabledCipherSuites(providerTypes);
		a.setEnableSessionCreation(providerOps.getTagA());
		a.setNeedClientAuth(providerOps.getTagB());
		a.setWantClientAuth(providerOps.getTagC());
		return a;
	}
	public final ServerSocket createServerSocket(int PortNumber, int QueueBacklog, InetAddress LocalAddress) throws IOException, NumberException, NullException, PermissionException
	{
		Security.check("io.net.ssl.server");
		if(PortNumber < 0) throw new NumberException("PortNumber", PortNumber, false);
		if(QueueBacklog < 0) throw new NumberException("QueueBacklog", QueueBacklog, false);
		if(PortNumber > Constants.MAX_USHORT_SIZE) throw new NumberException("PortNumber", PortNumber, 0, Constants.MAX_USHORT_SIZE);
		prepareServerSocket();
		Reporter.info(Reporter.REPORTER_SECURITY, "Creating a SSL ServerSocket, type \"" + getProviderType() + "\" enc \"" + getProviderEncryption() + "\"");
		SSLServerSocket a = (SSLServerSocket)providerSSoFact.createServerSocket(PortNumber, QueueBacklog, LocalAddress);
		a.setEnabledCipherSuites(providerTypes);
		a.setEnableSessionCreation(providerOps.getTagA());
		a.setNeedClientAuth(providerOps.getTagB());
		a.setWantClientAuth(providerOps.getTagC());
		return a;
	}
	
	public final InputStream modifyInputStream(InputStream StreamInput) throws IOException, NullException
	{
		return StreamInput;
	}
	
	public final OutputStream modifyOuputStream(OutputStream StreamOutput) throws IOException, NullException
	{
		return StreamOutput;
	}
	
	public final SSLProvider clone() throws CloneException
	{
		throw new CloneException("Cannot clone an SSL SecurityProvider!");
	}
	
	private final void prepareSet() throws IOException
	{
		try
		{
			Reporter.debug(Reporter.REPORTER_SECURITY, "Preparing SSL socket, type \"" + providerType + "\" enc \"" + providerEncryption + "\"");
			providerSet = new SSLSet();
			if(providerCertificate != null)
			{
				providerSet.setKeys = providerKeyBase != null ? KeyStore.getInstance(providerType, providerKeyBase) : KeyStore.getInstance(providerType);
				FileInputStream a = new FileInputStream(providerCertificate);
				providerSet.setKeys.load(a, providerIdentity.getUserPasswordArray());
				a.close();
				providerSet.setKeyFact = providerBase != null ? KeyManagerFactory.getInstance(providerFactory, providerBase) : KeyManagerFactory.getInstance(providerFactory != null ? providerFactory : KeyManagerFactory.getDefaultAlgorithm());
				providerSet.setKeyFact.init(providerSet.setKeys, providerIdentity.getUserPasswordArray());
				providerSet.setTrustFact = providerBase != null ? TrustManagerFactory.getInstance(providerFactory, providerBase) : TrustManagerFactory.getInstance(providerFactory != null ? providerFactory : TrustManagerFactory.getDefaultAlgorithm());
				providerSet.setTrustFact.init(providerSet.setKeys);
				if(providerTrust == null) providerTrust = providerSet.setTrustFact.getTrustManagers();
			}
			providerSet.setConext = providerBase != null ? SSLContext.getInstance(providerEncryption, providerBase) : SSLContext.getInstance(providerEncryption != null ? providerEncryption : "Default");
			providerSet.setConext.init(providerSet.setKeyFact != null ? providerSet.setKeyFact.getKeyManagers() : null, providerTrust, null);
			Reporter.debug(Reporter.REPORTER_SECURITY, "Finished SSL Prepare");
		}
		catch (KeyStoreException Exception)
		{
			Reporter.error(Reporter.REPORTER_SECURITY, Exception);
			throw new IOException(Exception);
		}
		catch (CertificateException Exception)
		{
			Reporter.error(Reporter.REPORTER_SECURITY, Exception);
			throw new IOException(Exception);
		}
		catch (KeyManagementException Exception)
		{
			Reporter.error(Reporter.REPORTER_SECURITY, Exception);
			throw new IOException(Exception);
		}
		catch (NoSuchAlgorithmException Exception)
		{
			Reporter.error(Reporter.REPORTER_SECURITY, Exception);
			throw new IOException(Exception);
		}
		catch (UnrecoverableKeyException Exception)
		{
			Reporter.error(Reporter.REPORTER_SECURITY, Exception);
			throw new IOException(Exception);
		}
	}
	private final void prepareSocket() throws IOException
	{
		if(providerSet == null) prepareSet();
		if(providerSoFact == null) providerSoFact = providerSet.setConext.getSocketFactory();
		if(providerTypes == null) providerTypes = providerSoFact.getDefaultCipherSuites();
	}
	private final void prepareServerSocket() throws IOException
	{
		if(providerSet == null) prepareSet();
		if(providerSoFact == null) providerSSoFact = providerSet.setConext.getServerSocketFactory();
		if(providerTypes == null) providerTypes = providerSSoFact.getDefaultCipherSuites();
	}
	
	private static final class SSLSet
	{
		private KeyStore setKeys;
		private SSLContext setConext;
		private KeyManagerFactory setKeyFact;
		private TrustManagerFactory setTrustFact;
		
		private SSLSet() { }
	}
	public static final class SSLDefault implements SecurityProvider
	{
		//public TrustManager[] aa;
		
		private SSLContext defaultContext;
		private SSLSocketFactory defaultSoFact;
		private SSLServerSocketFactory defaultSSoFact;
		
		public final String getProviderName()
		{
			return CRED_USERNAME;
		}
		
		public final Socket createSocket() throws IOException
		{
			try
			{
				if(defaultContext == null)
					defaultContext = SSLContext.getDefault();
				if(defaultSoFact == null)
					defaultSoFact = defaultContext.getSocketFactory();
				return defaultSoFact.createSocket();
			}
			catch (NoSuchAlgorithmException Exception)
			{
				Reporter.error(Reporter.REPORTER_SECURITY, Exception);
				throw new IOException(Exception);
			}
		}
		public final Socket createSocket(InetAddress ConnectAddress, int PortNumber) throws IOException, NumberException, NullException
		{
			if(ConnectAddress == null) throw new NullException("ConnectAddress");
			if(PortNumber < 0) throw new NumberException("PortNumber", PortNumber, false);
			if(PortNumber > Constants.MAX_USHORT_SIZE) throw new NumberException("PortNumber", PortNumber, 0, Constants.MAX_USHORT_SIZE);
			try
			{
				if(defaultContext == null)
					defaultContext = SSLContext.getDefault();
				if(defaultSoFact == null)
					defaultSoFact = defaultContext.getSocketFactory();
				return defaultSoFact.createSocket(ConnectAddress, PortNumber);
			}
			catch (NoSuchAlgorithmException Exception)
			{
				Reporter.error(Reporter.REPORTER_SECURITY, Exception);
				throw new IOException(Exception);
			}
		}
		public final Socket createSocket(String ConnectAddress, int PortNumber) throws IOException, NumberException, NullException, StringException
		{
			if(ConnectAddress == null) throw new NullException("ConnectAddress");
			if(ConnectAddress.isEmpty()) throw new StringException("ConnectAddress");
			if(PortNumber < 0) throw new NumberException("PortNumber", PortNumber, false);
			if(PortNumber > Constants.MAX_USHORT_SIZE) throw new NumberException("PortNumber", PortNumber, 0, Constants.MAX_USHORT_SIZE);
			try
			{
				if(defaultContext == null)
					defaultContext = SSLContext.getDefault();
				if(defaultSoFact == null)
					defaultSoFact = defaultContext.getSocketFactory();
				return defaultSoFact.createSocket(ConnectAddress, PortNumber);
			}
			catch (NoSuchAlgorithmException Exception)
			{
				Reporter.error(Reporter.REPORTER_SECURITY, Exception);
				throw new IOException(Exception);
			}
		}
		public final Socket createSocket(InetAddress ConnectAddress, int ConnectPort, InetAddress LocalAddress, int LocalPort) throws IOException, NumberException, NullException
		{
			if(ConnectAddress == null) throw new NullException("ConnectAddress");
			if(LocalPort < 0) throw new NumberException("LocalPort", LocalPort, false);
			if(ConnectPort < 0) throw new NumberException("ConnectPort", ConnectPort, false);
			if(LocalPort > Constants.MAX_USHORT_SIZE) throw new NumberException("LocalPort", LocalPort, 0, Constants.MAX_USHORT_SIZE);
			if(ConnectPort > Constants.MAX_USHORT_SIZE) throw new NumberException("ConnectPort", ConnectPort, 0, Constants.MAX_USHORT_SIZE);
			try
			{
				if(defaultContext == null)
					defaultContext = SSLContext.getDefault();
				if(defaultSoFact == null)
					defaultSoFact = defaultContext.getSocketFactory();
				return defaultSoFact.createSocket(ConnectAddress, ConnectPort, LocalAddress, LocalPort);
			}
			catch (NoSuchAlgorithmException Exception)
			{
				Reporter.error(Reporter.REPORTER_SECURITY, Exception);
				throw new IOException(Exception);
			}
		}
		public final Socket createSocket(String ConnectAddress, int ConnectPort, InetAddress LocalAddress, int LocalPort) throws IOException, NumberException, NullException, StringException
		{
			if(ConnectAddress == null) throw new NullException("ConnectAddress");
			if(ConnectAddress.isEmpty()) throw new StringException("Connectddress");
			if(LocalPort < 0) throw new NumberException("LocalPort", LocalPort, false);
			if(ConnectPort < 0) throw new NumberException("ConnectPort", ConnectPort, false);
			if(LocalPort > Constants.MAX_USHORT_SIZE) throw new NumberException("LocalPort", LocalPort, 0, Constants.MAX_USHORT_SIZE);
			if(ConnectPort > Constants.MAX_USHORT_SIZE) throw new NumberException("ConnectPort", ConnectPort, 0, Constants.MAX_USHORT_SIZE);
			try
			{
				if(defaultContext == null)
					defaultContext = SSLContext.getDefault();
				if(defaultSoFact == null)
					defaultSoFact = defaultContext.getSocketFactory();
				return defaultSoFact.createSocket(ConnectAddress, ConnectPort, LocalAddress, LocalPort);
			}
			catch (NoSuchAlgorithmException Exception)
			{
				Reporter.error(Reporter.REPORTER_SECURITY, Exception);
				throw new IOException(Exception);
			}
		}

		public final ServerSocket createServerSocket() throws IOException
		{
			try
			{
				if(defaultContext == null)
					defaultContext = SSLContext.getDefault();
				if(defaultSSoFact == null)
					defaultSSoFact = defaultContext.getServerSocketFactory();
				return defaultSSoFact.createServerSocket();
			}
			catch (NoSuchAlgorithmException Exception)
			{
				Reporter.error(Reporter.REPORTER_SECURITY, Exception);
				throw new IOException(Exception);
			}
		}
		public final ServerSocket createServerSocket(int PortNumber) throws IOException, NumberException
		{
			if(PortNumber < 0) throw new NumberException("PortNumber", PortNumber, false);
			if(PortNumber > Constants.MAX_USHORT_SIZE) throw new NumberException("PortNumber", PortNumber, 0, Constants.MAX_USHORT_SIZE);
			try
			{
				if(defaultContext == null)
					defaultContext = SSLContext.getDefault();
				if(defaultSSoFact == null)
					defaultSSoFact = defaultContext.getServerSocketFactory();
				return defaultSSoFact.createServerSocket();
			}
			catch (NoSuchAlgorithmException Exception)
			{
				Reporter.error(Reporter.REPORTER_SECURITY, Exception);
				throw new IOException(Exception);
			}
		}
		public final ServerSocket createServerSocket(int PortNumber, int QueueBacklog) throws IOException, NumberException
		{
			if(PortNumber < 0) throw new NumberException("PortNumber", PortNumber, false);
			if(QueueBacklog < 0) throw new NumberException("QueueBacklog", QueueBacklog, false);
			if(PortNumber > Constants.MAX_USHORT_SIZE) throw new NumberException("PortNumber", PortNumber, 0, Constants.MAX_USHORT_SIZE);
			try
			{
				if(defaultContext == null)
					defaultContext = SSLContext.getDefault();
				if(defaultSSoFact == null)
					defaultSSoFact = defaultContext.getServerSocketFactory();
				return defaultSSoFact.createServerSocket();
			}
			catch (NoSuchAlgorithmException Exception)
			{
				Reporter.error(Reporter.REPORTER_SECURITY, Exception);
				throw new IOException(Exception);
			}
		}
		public final ServerSocket createServerSocket(int PortNumber, int QueueBacklog, InetAddress LocalAddress) throws IOException, NumberException, NullException
		{
			if(PortNumber < 0) throw new NumberException("PortNumber", PortNumber, false);
			if(QueueBacklog < 0) throw new NumberException("QueueBacklog", QueueBacklog, false);
			if(PortNumber > Constants.MAX_USHORT_SIZE) throw new NumberException("PortNumber", PortNumber, 0, Constants.MAX_USHORT_SIZE);
			try
			{
				if(defaultContext == null)
					defaultContext = SSLContext.getDefault();
				if(defaultSSoFact == null)
					defaultSSoFact = defaultContext.getServerSocketFactory();
				return defaultSSoFact.createServerSocket();
			}
			catch (NoSuchAlgorithmException Exception)
			{
				Reporter.error(Reporter.REPORTER_SECURITY, Exception);
				throw new IOException(Exception);
			}
		}

		public final InputStream modifyInputStream(InputStream StreamInput) throws IOException, NullException
		{
			return StreamInput;
		}

		public final OutputStream modifyOuputStream(OutputStream StreamOutput) throws IOException, NullException
		{
			return StreamOutput;
		}
		
		private SSLDefault() { }
	}
}