package com.spire.web.mocha;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.io.IOException;
import java.util.ArrayList;

import com.spire.io.Stream;
import com.spire.web.WebPage;
import com.spire.log.Reporter;
import com.spire.web.WebState;
import com.spire.io.Streamable;

import java.io.FileNotFoundException;

public abstract class MochaPage implements WebPage
{
	private static final byte COMMAND_START = 64;
	private static final byte COMMAND_END_CHAR = 41;
	private static final byte COMMAND_START_CHAR = 40;
	private static final short COMMAND_BLOCK_SIZE = 850;
	
	protected final HashMap<String, MochaCall> pageCalls;
	protected final HashMap<String, Object> pageMappings;
	
	private final File pagePath;
	private final ArrayList<MochaInclude> pageIncludes;
	
	private long pageLastEdit;
	
	public final void onPageGet(WebState Current) throws IOException
	{
		onLoad(Current);
		if(pagePath.lastModified() != pageLastEdit)
		{
			pageIncludes.clear();
			pageLastEdit = pagePath.lastModified();
		}
		processPage(Stream.getFileInputStream(pagePath.getAbsolutePath()), Current.getOutputStream(), this, Current);
	}
	public final void onPagePost(WebState Current) throws IOException
	{
		onPageGet(Current);
	}
	
	protected MochaPage(String PagePath) throws IOException
	{
		pagePath = new File(PagePath);
		if(!pagePath.exists())
		{
			Reporter.error(Reporter.REPORTER_IO, "The file \"" + PagePath + "\" does not exist!");
			throw new FileNotFoundException("The file \"" + PagePath + "\" does not exist!");
		}
		pageCalls = new HashMap<String, MochaCall>();
		pageIncludes = new ArrayList<MochaInclude>();
		pageMappings = new HashMap<String, Object>();
		pageLastEdit = pagePath.exists() ? pagePath.lastModified(): 0;
	}
	
	@SuppressWarnings("unused")
	protected void onLoad(WebState Current) throws IOException { }
	
	protected Object getPageObject(String ObjectMapping)
	{
		return pageMappings.get(ObjectMapping);
	}
	
	private static final void processPage(Streamable PageStream, Streamable PageOut, MochaPage Page, WebState Current) throws IOException
	{
		if(!Page.pageIncludes.isEmpty())
		{
			processPageWithIncludes(PageStream, PageOut, Page, Current);
			return;
		}
		byte[] a = new byte[550];
		short b = 0, c = 0, d = 0;
		int e = 0;
		String f = null;
		MochaInclude g = null;
		MochaRegister h = null;
		for(short i = 0; (i = (short)PageStream.readByteArray(a)) > 0; b = 0, e++)
		{
			for(short j = 0; j < i; j++)
			{
				if(a[j] == COMMAND_START && (j + 1) < i && a[j + 1] == COMMAND_START_CHAR)
				{
					c = j;
					h = MochaRegister.getRegister(a, (short)(j + 2));
					if(h != null)
					{
						j += (2 + h.getSize());
						d = -1;
						for(short k = j; k < (j + Byte.MAX_VALUE) && k < i; k++)
							if(a[k] == COMMAND_END_CHAR)
							{
								d = k;
								break;
							}
						if(d != -1 && (d - (j + 1)) > 0)
						{
							j++;
							f = new String(a, j, d - j);
							g = new MochaInclude(e, c, d, f, h);
							Page.pageIncludes.add(g);
							f = g.getResult(Page, Current);
							PageOut.writeByteArray(a, b, (c - b));
							PageOut.writeBytes(f);
							b = j = (short)(d + 1);
							g = null;
							h = null;
						}
					}
				}
			}
			if(b < i)
				PageOut.writeByteArray(a, b, (i - b));
		}
	}
	private static final void processPageWithIncludes(Streamable PageStream, Streamable PageOut, MochaPage Page, WebState Current) throws IOException
	{
		byte[] a = new byte[550];
		int b = 0;
		MochaInclude c = null;
		for(short d = 0, e = 0; (d = (short)PageStream.readByteArray(a)) > 0; b++, e = 0)
		{
			for(int f = 0; f < Page.pageIncludes.size(); f++)
				if(Page.pageIncludes.get(f).includeRound == b)
				{
					c = Page.pageIncludes.get(f);
					PageOut.writeByteArray(a, e, (c.includeStart - e));
					PageOut.writeBytes(c.getResult(Page, Current));
					e = (short)(c.includeEnd + 1);
					c = null;
				}
			if(e < d)
				PageOut.writeByteArray(a, e, (d - e));
		}
	}

	private static final void createPage(Streamable InStream, Streamable OutStream, MochaPage ThisPage, WebState Current) throws IOException
	{
		int a = 0;
		String b = null;
		MochaPart c = null;
		MochaRegister d = null;
		short e = 0, f = 0, g = 0;
		byte[] h = new byte[COMMAND_BLOCK_SIZE];
		for(short i = 0; (i = (short)InStream.readByteArray(h)) > 0; e = 0, a++)
		{
			for(short j = 0; j < i; j++)
			{
				if(h[i] == COMMAND_START)
				{
					e = createPagePart(j, i, h, InStream, OutStream, ThisPage, Current);
					if(e == j)
						e = 0;
					else
						j = e;	
				}
			}
			if(e < i)
				OutStream.writeByteArray(h, e, (i - e));
		}
	}
	
	private static final short createPagePart(short CurrentIndex, short MaxIndex, byte[] Source, Streamable InStream, Streamable OutStream, MochaPage ThisPage, WebState Current) throws IOException
	{
		if((CurrentIndex + 1) >= MaxIndex)
		{
			short a = (short)InStream.readByteArray(Source);
			if(a <= 0)
				return CurrentIndex;
			return createPagePart((short)0, a, Source, InStream, OutStream, ThisPage, Current);
		}
		//if(CurrentIndex
		return 0;
	}
	
	private static final class MochaGhostPart extends MochaPart
	{
		private byte[] partBytes;
		
		protected final Object doPartAction(MochaPage CurrentPage, WebState Current)
		{
			return null;
		}
		
		private MochaGhostPart(byte[] Part, short Start, short End)
		{
			partBytes = Arrays.copyOfRange(Part, Start, End);
		}
	}
}