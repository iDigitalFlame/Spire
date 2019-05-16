package com.spire.web.mocha;

import java.io.IOException;
import java.util.ArrayList;

import com.spire.web.WebContent;
import com.spire.web.WebState;
import com.spire.ex.NullException;
import com.spire.ex.SizeException;
import com.spire.ex.NumberException;
import com.spire.ex.StringException;

public abstract class MochaRegister
{
	private static final byte LIST_SPACE = 32;
	private static final byte LIST_SEPERATOR = 44;
	private static final String LIST_BOOLEAN = "1";
	private static final ArrayList<MochaRegister> REGISTER_LIST = new ArrayList<MochaRegister>();
	
	public static final byte ON_LOAD = 0;
	public static final byte ON_POSTBACK = 1;
	public static final byte ON_VALIDATION = 3;
	
	protected final byte registerTrigger;
	
	private final byte[] registerLower;
	private final byte[] registerUpper;
	
	public static final void addRegister(MochaRegister Register) throws NullException
	{
		if(Register == null) throw new NullException("Register");
		REGISTER_LIST.add(Register);
	}
	
	protected MochaRegister(byte TriggerType, String RegisterName) throws NullException, StringException, NumberException
	{
		if(RegisterName == null) throw new NullException("RegisterName");
		if(RegisterName.isEmpty()) throw new StringException("RegisterName");
		if(TriggerType < 0) throw new NumberException("TriggerType", TriggerType, false);
		if(TriggerType > 3) throw new NumberException("TriggerType", TriggerType, 0, 3);
		registerTrigger = TriggerType;
		registerLower = RegisterName.toLowerCase().getBytes();
		registerUpper = RegisterName.toUpperCase().getBytes();
	}
	protected MochaRegister(byte TriggerType, byte[] UpperBytes, byte[] LowerBytes) throws NullException, SizeException, NumberException
	{
		if(UpperBytes == null) throw new NullException("UpperBytes");
		if(UpperBytes.length == 0) throw new SizeException("UpperBytes", true, 0, 0, Byte.MAX_VALUE);
		if(UpperBytes.length > Byte.MAX_VALUE) throw new SizeException("UpperBytes", true, UpperBytes.length, 0, Byte.MAX_VALUE);
		if(LowerBytes == null) throw new NullException("LowerBytes");
		if(LowerBytes.length == 0) throw new SizeException("LowerBytes", true, 0, 0, Byte.MAX_VALUE);
		if(LowerBytes.length > Byte.MAX_VALUE) throw new SizeException("LowerBytes", true, LowerBytes.length, 0, Byte.MAX_VALUE);
		if(TriggerType < 0) throw new NumberException("TriggerType", TriggerType, false);
		if(TriggerType > 3) throw new NumberException("TriggerType", TriggerType, 0, 3);
		registerTrigger = TriggerType;
		registerLower = LowerBytes;
		registerUpper = UpperBytes;
	}
	
	protected final boolean isRegister(byte[] Source, short StartIndex)
	{
		if(Source[StartIndex] == registerLower[0] || Source[StartIndex] == registerUpper[0])
		{
			for(byte a = 1; a < registerLower.length; a++)
				if(Source[StartIndex + a] != registerLower[a] && Source[StartIndex + a] != registerUpper[a])
					return false;
			return true;
		}
		return false;
	}
	
	protected final byte getSize()
	{
		return (byte)registerLower.length;
	}
	
	protected MochaPart createPart(MochaPage CurrentPage, WebState Current, String Paramter)
	{
		return null;
	}
	protected MochaPart createPart(MochaPage CurrentPage, WebState Current, String[] ParamterList)
	{
		return null;
	}
	
	protected abstract Object processEvent(MochaPage Page, WebState Current, String Paramater);
	protected abstract Object processEvent(MochaPage Page, WebState Current, String[] Paramaters);
	protected Object processEvent(MochaPage Page, WebState Current, MochaInclude Include, String Paramater)
	{
		return processEvent(Page, Current, Paramater);
	}
	protected final Object getResult(MochaPage Page, WebState Current, MochaInclude Include, String Paramater)
	{
		if(Paramater.indexOf(LIST_SEPERATOR) > -1)
		{
			String[] a = Paramater.split(String.valueOf((char)LIST_SEPERATOR));
			if(a != null && a.length < Byte.MAX_VALUE)
			{
				for(byte b = 0; b < a.length; b++)
					a[b] = a[b].trim();
				return processEvent(Page, Current, Include, a);
			}
		}
		return processEvent(Page, Current, Include, Paramater);
	}
	protected Object processEvent(MochaPage Page, WebState Current, MochaInclude Include, String[] Paramaters)
	{
		return processEvent(Page, Current, Paramaters);
	}

	protected static final MochaRegister getRegister(byte[] Source, short StartIndex)
	{
		if(!REGISTER_LIST.isEmpty())
			for(int a = 0; a < REGISTER_LIST.size(); a++)
				if(REGISTER_LIST.get(a).isRegister(Source, StartIndex)) return REGISTER_LIST.get(a);
		return null;
	}

	private static class MochaGetRegister extends MochaRegister
	{
		private MochaGetRegister()
		{
			super(ON_LOAD, new byte[] { 71, 69, 84 }, new byte[] { 103, 101, 116 });
		}
		private MochaGetRegister(byte TriggerType, byte[] UpperBytes, byte[] LowerBytes)
		{
			super(TriggerType, UpperBytes, LowerBytes);
		}

		protected final Object processEvent(MochaPage Page, WebState Current, String Paramater)
		{
			return Page.getPageObject(Paramater);
		}
		protected final Object processEvent(MochaPage Page, WebState Current, String[] Paramaters)
		{
			return Page.getPageObject(Paramaters[0]);
		}
	}
	private static class MochaCallRegister extends MochaRegister
	{
		private MochaCallRegister()
		{
			super(ON_LOAD, new byte[] { 67, 65, 76, 76 }, new byte[] { 99, 97, 108, 108 });
		}
		private MochaCallRegister(byte TriggerType, byte[] UpperBytes, byte[] LowerBytes)
		{
			super(TriggerType, UpperBytes, LowerBytes);
		}

		protected final Object processEvent(MochaPage Page, WebState Current, String Paramater)
		{
			if(Page.pageCalls.containsKey(Paramater))
				return Page.pageCalls.get(Paramater).isValid() ? Page.pageCalls.get(Paramater).callMethod(Page, Current, null) : null;
			MochaCall a = new MochaCall(Page, Paramater);
			Page.pageCalls.put(Paramater, a);
			return a.isValid() ? a.callMethod(Page, Current, null) : null;
		}
		protected final Object processEvent(MochaPage Page, WebState Current, String[] Paramaters)
		{
			if(Page.pageCalls.containsKey(Paramaters[0]))
				return Page.pageCalls.get(Paramaters[0]).isValid() ? Page.pageCalls.get(Paramaters[0]).callMethod(Page, Current, Paramaters) : null;
			MochaCall a = new MochaCall(Page, Paramaters);
			Page.pageCalls.put(Paramaters[0], a);
			return a.isValid() ? a.callMethod(Page, Current, Paramaters) : null;
		}
	}
	private static final class MochaIfRegister extends MochaRegister
	{
		private MochaIfRegister()
		{
			super(ON_LOAD, new byte[] { 73, 70 }, new byte[] { 105, 102 });
		}
		
		protected final Object processEvent(MochaPage Page, WebState Current, String Paramater)
		{
			Object a = Page.getPageObject(Paramater);
			if(a == null)
			{
				byte b = (byte)Paramater.indexOf(LIST_SPACE);
				if(b >= 0)
				{
					MochaRegister c = getRegister(Paramater.substring(0, b).getBytes(), (short)0);
					if(c != null)
						a = c.processEvent(Page, Current, Paramater.substring(b + 1));
				}
			}
			return Boolean.valueOf(a != null && ((a instanceof Boolean && ((Boolean)a).booleanValue()) || Boolean.valueOf(a.toString()).booleanValue() || a.toString().equals(LIST_BOOLEAN)));
		}
		protected final Object processEvent(MochaPage Page, WebState Current, String[] Paramaters)
		{
			Object a = Page.getPageObject(Paramaters[0]);
			if(a == null)
			{
				byte b = (byte)Paramaters[0].indexOf(LIST_SPACE);
				if(b >= 0)
				{
					MochaRegister c = getRegister(Paramaters[0].substring(0, b).getBytes(), (short)0);
					if(c != null)
						a = c.processEvent(Page, Current, Paramaters[0].substring(b + 1));
				}
			}
			boolean b = a != null && ((a instanceof Boolean && ((Boolean)a).booleanValue()) || Boolean.valueOf(a.toString()).booleanValue() || a.toString().equals(LIST_BOOLEAN));
			if(b) return Paramaters[1];
			return Paramaters.length >= 2 ? Paramaters[2]: null;
		}
	}
	private static final class MochaFormRegister extends MochaRegister
	{
		private MochaFormRegister()
		{
			super(ON_POSTBACK, new byte[] { 70, 79, 82, 77 }, new byte[] { 102, 111, 114, 109 });
		}

		protected final Object processEvent(MochaPage Page, WebState Current, String Paramater)
		{
			return Current.getFormValue(Paramater);
		}
		protected final Object processEvent(MochaPage Page, WebState Current, String[] Paramaters)
		{
			return processEvent(Page, Current, Paramaters[0]);
		}
	}
	private static final class MochaRequestRegister extends MochaRegister
	{
		private MochaRequestRegister()
		{
			super(ON_LOAD, new byte[] { 82, 69, 81, 85, 69, 83, 84 }, new byte[] { 114, 101, 113, 117, 101, 115, 116 });
		}

		protected final Object processEvent(MochaPage Page, WebState Current, String Paramater)
		{
			return Current.getRequest(Paramater);
		}
		protected final Object processEvent(MochaPage Page, WebState Current, String[] Paramaters)
		{
			return processEvent(Page, Current, Paramaters[0]);
		}
	}
	private static final class MochaPostGetRegister extends MochaGetRegister
	{
		private MochaPostGetRegister()
		{
			super(ON_POSTBACK, new byte[] { 80, 79, 83, 84, 32, 71, 69, 84 }, new byte[] { 112, 111, 115, 116, 32, 103, 101, 116 });
		}
	}
	private static final class MochaPostCallRegister extends MochaCallRegister
	{
		private MochaPostCallRegister()
		{
			super(ON_POSTBACK, new byte[] { 80, 79, 83, 84, 32, 67, 65, 76, 76 }, new byte[] { 112, 111, 115, 116, 32, 99, 97, 108, 108 });
		}
	}
	
	private static final class MochaPageRegister extends MochaRegister
	{
		private MochaPageRegister()
		{
			super(ON_LOAD, new byte[] { 80, 65, 71, 69 }, new byte[] { 112, 97, 103, 101 });
		}
		
		protected final Object processEvent(MochaPage Page, WebState Current, String Paramater)
		{
			return null;
		}
		protected final Object processEvent(MochaPage Page, WebState Current, String[] Paramaters)
		{
			return null;
		}
		protected final Object processEvent(MochaPage Page, WebState Current, MochaInclude Include, String Paramater)
		{
			try
			{
				if(Include.includePage == null)
					Include.includePage = new MochaContentPage(Paramater);
				Include.includePage.onPageGet(Current);
			}
			catch (IOException Exception) { }
			return null;
		}
		protected final Object processEvent(MochaPage Page, WebState Current, MochaInclude Include, String[] Paramaters)
		{
			try
			{
				if(Include.includePage == null)
					Include.includePage = (Paramaters[1].equals(LIST_BOOLEAN) || Boolean.valueOf(Paramaters[1]).booleanValue()) ?
							new MochaContentPage(Paramaters[0]) : new WebContent(Current.getType(), Paramaters[0]);
				Include.includePage.onPageGet(Current);
			}
			catch (IOException Exception) { }
			return null;
		}
	}
	
	
	static
	{
		REGISTER_LIST.add(new MochaPageRegister());
		
		REGISTER_LIST.add(new MochaIfRegister());
		REGISTER_LIST.add(new MochaGetRegister());
		REGISTER_LIST.add(new MochaCallRegister());
		REGISTER_LIST.add(new MochaFormRegister());
		REGISTER_LIST.add(new MochaRequestRegister());
		REGISTER_LIST.add(new MochaPostGetRegister());
		REGISTER_LIST.add(new MochaPostCallRegister());
	}
}