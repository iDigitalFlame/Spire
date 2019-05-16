package com.spire.io;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;

public abstract class SSLTrustManager implements X509TrustManager 
{
	public final void checkClientTrusted(X509Certificate[] TrustChain, String TrustAuth) throws CertificateException
	{
		for(int a = 0; a < TrustChain.length; a++)
			if(!canTrustCertificate(TrustChain[a], TrustAuth, false))
				throw new CertificateException("Certificate(s) are not trusted!");
	}
	public final void checkServerTrusted(X509Certificate[] TrustChain, String TrustAuth) throws CertificateException
	{
		for(int a = 0; a < TrustChain.length; a++)
			if(!canTrustCertificate(TrustChain[a], TrustAuth, true))
				throw new CertificateException("Certificate(s) are not trusted!");
	}

	public final X509Certificate[] getAcceptedIssuers()
	{
		return null;
	}

	protected abstract boolean canTrustCertificate(X509Certificate Certificate, String CertificateAuth, boolean IsServer);
}