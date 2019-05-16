package com.spire.web;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Arrays;

public final class WebReader
{
	private static final byte AND = (byte)'&';
	private static final byte QUOTE = (byte)'"';
	private static final byte COLON = (byte)':';
	private static final byte MINUS = (byte)'-';
	private static final byte SPACE = (byte)32;
	private static final byte EQUAL = (byte)'=';
	private static final byte SCOLON = (byte)';';
	private static final byte QUESTIONMK = (byte)'?';
	private static final byte[] CRLF = new byte[] { 13, 10 };
	private static final byte[] CRLFCRLF = new byte[] { 13, 10, 13, 10 };
	
	private static final byte[] BOUNDRY = "boundary=".getBytes();
	
	public static final WebState readWebState(WebServer Server, InetAddress WebClient, InputStream WebStream) throws IOException, WebException
	{		
		WebState a = null;
		String b = null, c = null;
		byte[] d = null, e = null;
		int f = 0, g = 0, h = 0, i = 0;
		WebMethod j = null;
		// The magic below
		ByteBuffer k = new ByteBuffer(WebStream, 4096);
		// This magic ^^
	
		/*if(Server.isSecure())
		{
			k.bufferStream.mark(1);
			System.out.println(k.bufferStream.read());
			k.bufferStream.reset();
		}*/
		
		//fx ie Issue
		//
		// >> IE SSL Issues 
		// >> IE does not process reuqests on SSL, reads return -1 as no data seems to be present.
		
		
		
		// Lets start!!
		// Find the first CRLF (13,10) so we can find the verb
		//if(k.bufferStream.getAvailable() <= 0)
		//	throw new IOException();
		f = k.readStop(0, CRLF);
		if(f == -1)
			throw new WebException(WebMessage.BAD_REQUEST, "Cannot read stream! First CRLF not found!"); // Throw web exception!!
		// ok we got the crlf
		// len of line == f, simple right?
		//System.out.println("First Read: " + k.readStop(0, CRLFCRLF));
		
		// lets get the verb!
		j = WebMethod.getMethod(k.getData());
		if(j == null || !j.methodSupported) //No verb!??
			throw new WebException(WebMessage.METHOD_NOT_ALLOWED, "No method given or not supported!"); // throw web
		// Lets find the path now
		g = ByteBuffer.indexOf(j.methodBytes.length + 1, k.getData(), SPACE); //replace 32 with const?
		// lets see if we have a space?
		if(g == -1 || g > f) //Malformed call?
			throw new WebException(WebMessage.BAD_REQUEST, "Cannot determine page requested!");
		// Now lets look for the path complexity
		h = f - ((j.methodBytes.length + 1) + (f - g)); // get size of path
		d = new byte[h];
		System.arraycopy(k.getData(), j.methodBytes.length + 1, d, 0, h); // copy path to seperate array
		h = ByteBuffer.indexOf(0, d, QUESTIONMK); // remind to replace with const, look for keys
		if(h > 0) // no >= cause ? cant be at root
		{
			//we got keys!
			e = new byte[d.length - (h + 1)];
			System.arraycopy(d, h + 1, e, 0, e.length); // copy keys to new array
			d = Arrays.copyOfRange(d, 0, d.length - (e.length + 1)); // resize array to exclude keys
		}
		// create the state
		// Remind me to path check this and to convert to UTF-8 plzzz
		a = new WebState(false, new String(d, 0, d.length), WebStream, Server, WebClient);
		
		// gonna set method
		a.stateMethod = j;
		
		//
		//
		d = null;
		if(e != null)
		{
			h = 0; // null out h to find 
			do
			{
				g = ByteBuffer.indexOf(h, e, EQUAL);
				if(g > 0)
				{
					b = new String(e, h, g - h);
					h = ByteBuffer.indexOf(h, e, AND);
					if(h > 0)
					{
						i = (h - (g + 1));
						if(i > 0)
						{
							c = new String(e, g + 1, i).replace("%20", " ");
							a.stateKeys.put(b, c);
							System.out.println("Keys(" + b + "):[" + c + "]");
						}
					}
					else
					{
						i = e.length - (g + 1);
						if(i > 0)
						{
							c = new String(e, g + 1, i).replace("%20", " ");
							a.stateKeys.put(b, c);
							System.out.println("Keys(" + b + "):[" + c + "]");
						}
					}
					b = null;
					c = null;
					h++;
				}
				else break;
			}
			while(h > 0);
			e = null;
		}
		//lets find our endpoint
		g = k.readStop(f, CRLFCRLF);
		//if(g == -1)	k.bufferStream.read();
		//System.out.println(": " + g);
		h = f + CRLF.length;
		// h start
		// i  end
		for(i = k.readStop(h + 1, CRLF); h <= g && i <= g && i >= 0; h = (i + CRLF.length), i = k.readStop(h + 1, CRLF))
		{
			System.out.println("VAAAL: " + k.bufferStream.available());
			//System.out.println("read params: hs: " + g + " cs(i): " + i + " ce(h): " + h);
			
			f = ByteBuffer.indexOf(h, k.getData(), COLON);
			if(f > i || f < h)
				throw new IOException();
			b = new String(k.getData(), h, (f - h));
			c = new String(k.getData(), f + 2, (i - (f + 2)));
			if(b.equalsIgnoreCase("user-agent"))
			{
				a.stateClient = new WebBrowser(b.toLowerCase(), c, true);
				a.stateHeaders.putElement(a.stateClient.headerKey, a.stateClient);
			}
			else if(b.equalsIgnoreCase("cookie"))
			{
				b = b.toLowerCase();
				a.stateHeaders.putElement(b, new WebHeader(b, c, true));
			}
			else if(b.equalsIgnoreCase("content-length"))
			{
				a.stateHeaders.putElement(b, new WebHeader(b, c, false));
				try
				{
					a.stateLength = Integer.parseInt(c);
				}
				catch (NumberFormatException Exception) { }
			}
			else if(b.equalsIgnoreCase("content-type"))
			{
				h = ByteBuffer.indexOf(f + 2, k.getData(), BOUNDRY);
				if(h < i)
				{
					a.stateBoundry = new byte[2 + i - (h + BOUNDRY.length)];
					a.stateBoundry[0] = MINUS;
					a.stateBoundry[1] = MINUS;
					System.arraycopy(k.getData(), (h + BOUNDRY.length), a.stateBoundry, 2, a.stateBoundry.length - 2);
					//System.out.println("BOUNDRYY ---- > " + new String(a.stateBoundry, 0, a.stateBoundry.length));
				}
				a.stateHeaders.putElement(b, new WebHeader(b, c, false));
			}
			else
				a.stateHeaders.putElement(b, new WebHeader(b, c, false));
			System.out.println(b + ">" + c);
		}
		b = null;
		c = null;
		// h is the end of the crlfcrlf
		// g is the start of the crlfcrlf
		// i might be -1 (depending on content len)
		if(g > h) // should not happen!
			throw new WebException(WebMessage.BAD_REQUEST, "Content Sizing and borders are not correct!");
		// lets first look for mulitpart data
		/*if(Server.isSecure())
		{
			k.bufferStream.mark(1);
			System.out.println(k.bufferStream.read());
			k.bufferStream.reset();
		}*/
		a.stateLength += h;
		System.out.println("h/g " + h + "/" + g);
		System.out.println(a.stateMethod.methodName);
		if(a.stateBoundry != null && (a.stateMethod == WebMethod.GET || a.stateMethod == WebMethod.POST))
		{
			k.readFully(a.stateLength);
			// multipart duh
			// MULTIPART FORMAT
			// .@G is here
			// [BOUNDRY](CRLF)
			// Content-Disposition: form-data; name="(name)"; (filename="vpn-key.txt") (CRLF)
			// (CRLF)
			// [DATA]
			// [BOUNDRY](CRLF)
			
			//lets loop this ^^
			
			// last multipart has "--" at the end (only Chrome??)
			
			//h will be start, i will be end
			
			i = 0;
			f = 0;
			
			
			System.out.println("Start read post data");	
			FieldContent l = null;
			for(i = k.readStop(h, a.stateBoundry, a.stateLength) + a.stateBoundry.length, f = k.readStop(i, a.stateBoundry, a.stateLength); f >= i; i = f + a.stateBoundry.length, f = k.readStop(i + 1, a.stateBoundry, a.stateLength), k.readStop(f + 1, a.stateBoundry, a.stateLength))
			{
				if(!ByteBuffer.equals(k.getData(), i, CRLF, 0) && l != null)
					throw new IOException();
					//l.fieldData.write(k.getData(), (i + 2), ((f - 2) - (i + 2)));
				g = k.readStop(i + 1, CRLF);
				if(g > h)
				{
					h = ByteBuffer.indexOf(i, k.getData(), COLON);
					System.out.println("g : " + g + " h: " + h + " i : " + i);
					if(h < g && h > i)
					{
						g = ByteBuffer.indexOf(h + 1, k.getData(), SCOLON);
						if(g > h)
						{
							if(l != null)
							{
								l.saveField(a);
								l = null;
							}
							l = new FieldContent();
							l.fieldDisposition = new String(k.getData(), h + 2, (g - (h + 2)));
							System.out.println(l.fieldDisposition);
							h = ByteBuffer.indexOf(g + 1, k.getData(), EQUAL);
							if(h > g)
							{
								if(k.getData()[h + 1] == QUOTE) h++;
								l.fieldCRLF = ByteBuffer.indexOf(h + 1, k.getData(), CRLF);
								g = ByteBuffer.indexOf(h + 1, k.getData(), SCOLON);
								if(g < l.fieldCRLF && g > 0)
								{
									System.out.println("File!");
									if(k.getData()[g - 1] == QUOTE) g--;
									l.fieldName = new String(k.getData(), (h + 1), (g - (h + 1)));
									System.out.println(l.fieldName);
									h = ByteBuffer.indexOf(g + 1, k.getData(), EQUAL);
									if(h > g && h < l.fieldCRLF)
									{
										if(k.getData()[h + 1] == QUOTE) h++;
										if(k.getData()[l.fieldCRLF - 1] == QUOTE) l.fieldCRLF--;
										System.out.println("h: " + h + ", ll: " + l.fieldCRLF);
										if((l.fieldCRLF - (h + 1)) > 0)
										{
											l.fieldFName = new String(k.getData(), (h + 1), (l.fieldCRLF - (h + 1)));
											l.fixFilePath();
											System.out.println(l.fieldFName);
										}
										else
											l = null;
									}
									else
										l = null;
								}
								else
								{
									g = l.fieldCRLF;
									if(k.getData()[g - 1] == QUOTE) g--;
									l.fieldName = new String(k.getData(), (h + 1), (g - (h + 1)));
									System.out.println(l.fieldName);
								}
								if(l != null && l.fieldFName == null)
								{
									g += CRLF.length;
									h = ByteBuffer.indexOf(g, k.getData(), CRLF);
									if(h > g)
									{
										if(ByteBuffer.equals(k.getData(), g + 1, CRLF, 0))
										{
											// not a file
											h = f;
											g += (1 + CRLF.length);
											if(ByteBuffer.equals(k.getData(), f - 2, CRLF, 0)) h -= CRLF.length;
											l.fieldData.write(k.getData(), g, (h - g));
										}
										else
											throw new WebException(WebMessage.BAD_REQUEST, "There was no border for a multipart!");
									}
									else
										throw new WebException(WebMessage.BAD_REQUEST, "There was no border listed for a multipart!");
								}
								else if(l != null)
								{
									g = l.fieldCRLF;
									g += (1 + CRLF.length);
									h = ByteBuffer.indexOf(g, k.getData(), CRLF);
									if(h > g)
									{
										g = ByteBuffer.indexOf(g + 1, k.getData(), COLON);
										if(g < h && g > l.fieldCRLF)
										{
											l.fieldFType = new String(k.getData(), (g + 2), (h - (g + 2)));
											h = ByteBuffer.indexOf(g + 1, k.getData(), CRLF);
											if(h > g)
											{
												h += CRLF.length;
												if(ByteBuffer.equals(k.getData(), h, CRLF, 0)) h += CRLF.length;
												g = f;
												if(ByteBuffer.equals(k.getData(), f - 2, CRLF, 0)) g -= CRLF.length;
												l.fieldData.write(k.getData(), h, (g - h));
												//l.write(k.getData(), h, (g - h));
											}
											else
												throw new WebException(WebMessage.BAD_REQUEST, "File multipart border not listed!");
										}
										else
											throw new WebException(WebMessage.BAD_REQUEST, "File multipart border was not listed!");
									}
									else
										throw new WebException(WebMessage.BAD_REQUEST, "File multipart border size was incorrect!");
								}
								//file with no data
							}
							else
								throw new WebException(WebMessage.BAD_REQUEST, "Multipart request not formatted properly!");
						}
						else
							throw new WebException(WebMessage.BAD_REQUEST, "Multipart frquest not formatted properly!");
					}
					else
						throw new WebException(WebMessage.BAD_REQUEST, "Multipart frquest not formatted properly!");
				}
				else
					throw new WebException(WebMessage.BAD_REQUEST, "Multipart frquest not formatted properly!");
			}
			if(l != null)
			{
				l.saveField(a);
				l = null;
			}

		}
		else
		{
			// reg post req
		}
		
		return a;
	}
	
	private static final class FieldContent
	{
		private int fieldCRLF;
		private String fieldName;
		private String fieldFName;
		private String fieldFType;
		private String fieldDisposition;
		private ByteArrayOutputStream fieldData;
		
		int cc = 0;
		
		private FieldContent()
		{
			fieldData = new ByteArrayOutputStream(1024);
		}
		
		private final void fixFilePath()
		{
			if(fieldFName != null)
			{
				int a = fieldFName.indexOf('/'), b = 0;
				if(a >= 0)
				{
					b = fieldFName.lastIndexOf('/');
					if(b >= 0)
					{
						fieldFName = fieldFName.substring(b + 1);
						return;
					}
				}
				a = fieldFName.indexOf('\\');
				if(a >= 0)
				{
					b = fieldFName.lastIndexOf('\\');
					if(b >= 0)
					{
						fieldFName = fieldFName.substring(b + 1);
						return;
					}
				}
			}
		}
		private final void saveField(WebState StateInstance)
		{
			System.out.println("Saved!: " + new String(fieldData.toByteArray()));
			if(fieldFName != null) try
			{
				com.spire.io.Stream a = com.spire.io.Stream.getFileOutputStream(fieldFName, true);
				a.writeByteArray(fieldData.toByteArray(), 0, fieldData.size());
				a.flush();
				a.close();
			}
			catch (Exception E) { E.printStackTrace(); }
		}
	}
	private static final class ByteBuffer extends ByteArrayOutputStream
	{
		private final InputStream bufferStream;
		
		private ByteBuffer(InputStream ReadStream, int BufferSize)
		{
			super(BufferSize);
			bufferStream = new BufferedInputStream(ReadStream);
		}
		
		private final void readFully(int StopLength) throws IOException
		{
			byte[] a = new byte[4096];
			if(bufferStream.available() <= 0 && count > 0 && (StopLength == 0 || count > StopLength)) return;
			for(int b = 0; (b = bufferStream.read(a, 0, a.length)) > 0; b++)
			{
				write(a, 0, b);
				if((count + 2) >= StopLength) return;
			}
		}
		
		private final int readStop(int StartIndex, byte[] StopIndex) throws IOException
		{
			return readStop(StartIndex, StopIndex, 0);
		}
		private final int readStop(int StartIndex, byte[] StopIndex, int StopLength) throws IOException
		{
			//System.out.println(bufferStream.getClass());
			int a = 0;
			if(count > 0 && StartIndex > 0)
			{
				a = indexOf(StartIndex, buf, StopIndex);
				System.out.println("isearech @a : " + a + " : val@" + new String(StopIndex, 0 , StopIndex.length));
				if(a >= 0) return a;
			}
			byte[] b = new byte[4096];
			byte[] c = new byte[b.length + StopIndex.length];
			System.out.println("AVL: " + bufferStream.available() + " SL: " + StopLength + " : " + count);
			if(bufferStream.available() <= 0 && count > 0 && (StopLength == 0 || count > StopLength))
			{
				//bufferStream.mark(1);
				//bufferStream.skip(1);
				//bufferStream.reset();
				//if(a == -1 || bufferStreamjava no.available() == 0)
					return -1;
			}
			for(int d = 0, e = 0; (d = bufferStream.read(b, 0, b.length)) > 0; e++)
			{
				if(count > 0 && count > StopIndex.length)
				{
					System.arraycopy(buf, count - StopIndex.length, c, 0, StopIndex.length);
					System.arraycopy(b, 0, c, StopIndex.length, d);
				}
				else System.arraycopy(b, 0, c, 0, d);
				write(b, 0, d);
				a = indexOf(0, c, StopIndex);
				if(a >= 0)
				{
					System.out.println("derp @ a : " + a + " : " + new String(StopIndex, 0 , StopIndex.length) + " e: " + e + " co: " + count);
					if(e == 0 && count == d) return a;
					if(a > d && d < b.length) return -1;
					//if(a < StopIndex.length) return ((e * b.length) - (StopIndex.length - a));
					return count + (a - StopIndex.length);
					//return ((e * b.length) + (a - StopIndex.length));
				}
				System.out.print("c" + count +";");
			}
			return -1;
		}
		
		private static final boolean equals(byte[] ArrayOne, int StartIndexOne, byte[] ArrayTwo, int StartIndexTwo)
		{
			for(int a = StartIndexOne, b = StartIndexTwo; a < ArrayOne.length && b < ArrayTwo.length; a++, b++)
			{
				System.out.println(ArrayOne[a] + " == " + ArrayTwo[b]);
				if(ArrayOne[a] != ArrayTwo[b]) return false;
			}
			return true;
		}
		
		private static final int indexOf(byte[] ByteArray, byte FindValue)
		{
			return indexOf(0, ByteArray, FindValue);
		}
		private static final int indexOf(byte[] ByteArray, byte[] FindValue)
		{
			return indexOf(0, ByteArray, FindValue);
		}
		private static final int indexOf(int FindStart, byte[] ByteArray, byte FindValue)
		{
			for(int a = FindStart; a < ByteArray.length; a++)
				if(ByteArray[a] == FindValue)
					return a;
			return -1;
		}
		private static final int indexOf(int FindStart, byte[] ByteArray, byte[] FindValue)
		{
			if(FindValue.length == 1)
				return indexOf(FindStart, ByteArray, FindValue[0]);
			boolean a = true;
			for(int b = FindStart; b < ByteArray.length; b++)
				if(ByteArray[b] == FindValue[0])
				{
					a = true;
					for(int c = 1; c < FindValue.length && (b + c) < ByteArray.length; c++)
					{
						if(ByteArray[b + c] != FindValue[c])
						{
							a = false;
							break;
						}
					}
					if(a)
						return b;
				}
			return -1;
		}
		private static final int indexOf(int FindStart, int FindEnd, byte[] ByteArray, byte FindValue)
		{
			for(int a = FindStart; a < ByteArray.length && a < (FindStart + FindEnd); a++)
				if(ByteArray[a] == FindValue)
					return a;
			return -1;
		}
		
		private final byte[] getData()
		{
			return buf;
		}
	}
}