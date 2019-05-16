package com.spire.web;

import java.io.IOException;
import com.spire.io.Stream;
import java.net.InetAddress;
import java.io.ByteArrayOutputStream;

public final class WebStateReader
{
	private static final short READER_SIZE = 650;
	private static final byte[] READER_CRLF = new byte[] { 13, 10 };
	private static final byte[] READER_VALIDS = new byte[] { (byte)'G', (byte)'P', (byte)'?', (byte)'&', (byte)'=', (byte)':' };
	private static final byte[] READER_TERMINATE = new byte[] { 13, 10, 13, 10 };
	
	private final ByteReader readerOut;
	
	protected WebStateReader(Stream ReaderStream)
	{
		readerOut = new ByteReader(ReaderStream);
	}
	
	protected final WebState createState(WebServer StateServer, InetAddress StateAddress) throws IOException, WebException
	{
		int a = readerOut.readUntil(READER_CRLF), b = 0, c = 0;
		if(a <= -1) throw new WebException(WebMessage.BAD_REQUEST);
		byte[] d = readerOut.getArray();
		if(d[0] != READER_VALIDS[0] &&  d[0] != READER_VALIDS[1]) throw new WebException(WebMessage.BAD_REQUEST);
		WebState e = new WebState(d[0] == READER_VALIDS[1], null, readerOut.readerStream, StateServer, StateAddress);
		byte f = e.isPost() ? (byte)5 : (byte)4;
		b = readerOut.getIndexOf(READER_VALIDS[2], f);
		if(b < a && b != -1)
		{
			e.statePage = new String(d, f, b);
			c = readerOut.getIndexOf(READER_VALIDS[3]);
			if(c > 0)
			{
				for(int g = (short)(b + 1), h = 0; c >= -1; g = (short)(c + 1), c = readerOut.getIndexOf(READER_VALIDS[3], c + 1))
				{
					if(c == -1) c = a;
					h = readerOut.getIndexOf(READER_VALIDS[4], g);
					if(h >= c) break;
					//Work on later
					//g = start 'after ? or &'
					//c = end '&' or crlf
					//h is the '=' or dividor
					e.stateKeys.put(new String(d, g, h - 1), new String(d, h + 1, c));
				}
			}
			else
			{
				c = readerOut.getIndexOf(READER_VALIDS[4]);
				if(c > 0) e.stateKeys.put(new String(d, b + 1, c - 1), new String(d, c + 1, a));
			}
		}
		else e.statePage = new String(d, f, a);
		
		System.out.println("Web >> Page requested: " + e.statePage);
		
		c = readerOut.getIndexOf(READER_CRLF, a);
		b = readerOut.readUntil(READER_TERMINATE);
		d = readerOut.getArray();
		int i = 0;
		a += 4;
		WebHeader j = null;
		while(c != b)
		{
			i = readerOut.getIndexOf(READER_VALIDS[5], a);
			if(i > 0)
			{
				j = new WebHeader(new String(d, a, i - 1).toLowerCase().trim(), new String(d, i + 1, i).trim(), false);
				System.out.println("Web >> Header: " + j.headerKey + ", Data: " + j.headerData);
				if(j.headerKey.equals("user-agent"))
				{
					System.out.println("User agent found!");
				}
				else
				{
					if(j.headerKey.equals("cookie"))
						j.headerOutgoing = true;
					e.stateHeaders.putElement(j.headerKey, j);
				}
			}
			a = c + 4;
			c = readerOut.getIndexOf(READER_CRLF, c);
		}
		//Need to go through the crlfs and stop at the double crlf to read raw
		return e;
	}
	
	public static final class ByteReader extends ByteArrayOutputStream
	{
		private final byte[] readerBlock;
		private final Stream readerStream;
		
		private ByteReader(Stream ReadStream)
		{
			readerStream = ReadStream;
			readerBlock = new byte[READER_SIZE];
		}
		
		private final boolean read() throws IOException
		{
			if(readerStream.getAvailable() <= 0) return false;
			short a = (short)readerStream.readDirectByteArray(readerBlock, 0, readerBlock.length);
			System.out.println("RREAD: " + a);
			if(a <= -1) return false;
			write(readerBlock, 0, a);
			return true;
		}
		
		private final int getIndexOf(byte Value)
		{
			return getIndexOf(Value, 0);
		}
		private final int getIndexOf(byte[] Array)
		{
			return getIndexOf(Array, 0);
		}
		private final int getIndexOf(byte Value, int StartIndex)
		{
			return getArrayIndex(buf, StartIndex, Value);
		}
		private final int getIndexOf(byte[] Array, int StartIndex)
		{
			return getArrayIndex(buf, StartIndex, Array);
		}
		private final int readUntil(byte[] Array) throws IOException
		{
			int a = getArrayIndex(buf, 0, Array);
			System.out.println("WREAD: " + a);
			if(a >= 0) return a;
			for(int b = -1; b == -1 && read();)
			{
				b = getArrayIndex(buf, 0, Array);
				System.out.println("WREAD L: " + a);
				if(b >= 0) return a;
			}
			return -1;
		}
		
		private final byte[] getArray()
		{
			return buf;
		}
	
		private static final int getArrayIndex(byte[] SearchArray, int StartIndex, byte FindValue)
		{
			for(int a = StartIndex; a < SearchArray.length; a++)
				if(SearchArray[a] == FindValue) return a;
			return -1;
		}
		public static final int getArrayIndex(byte[] SearchArray, int StartIndex, byte[] FindArray)
		{
			boolean a = false;
			for(int b = StartIndex; b < SearchArray.length; b++)
			{
				
				if(SearchArray[b] == FindArray[0])
				{
					System.out.println("!!");
					return b;
				}
			}
			return -1;
		}
	}
}