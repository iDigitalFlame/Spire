package com.spire.web.mocha;

import java.util.Arrays;
import com.spire.web.WebState;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

final class MochaCall
{
	private static final Class<?>[] REFLECT_PARAMS = new Class<?>[] { com.spire.web.WebState.class };
	
	private final byte callParams;
	private final Method callMethod;
	
	protected MochaCall(MochaPage Page, String Paramaters)
	{
		callParams = 0;
		callMethod = createMethod(Page.getClass(), Paramaters, callParams);
	}
	protected MochaCall(MochaPage Page, String[] Paramaters)
	{
		callParams = (byte)(Paramaters.length - 1);
		callMethod = createMethod(Page.getClass(), Paramaters[0], callParams);
	}
	
	protected final boolean isValid()
	{
		return callMethod != null;
	}
	
	protected final Object callMethod(MochaPage Page, WebState Current, String[] Paramaters)
	{
		if(callParams > 0 && (Paramaters == null || (Paramaters.length - 1) != callParams)) return null;
		Object[] a = new Object[1 + callParams];
		a[0] = Current;
		if(callParams > 0) for(byte b = 0; b < callParams; b++) a[1 + b] = Paramaters[1 + b];
		try
		{
			return callMethod.invoke(Page, a);
		}
		catch (IllegalAccessException Exception) { }
		catch (IllegalArgumentException Exception) { }
		catch (InvocationTargetException Exception) { }
		return null;
	}
	
	private static final Method createMethod(Class<? extends MochaPage> PageClass, String MethodName, byte ParamAmount)
	{
		Class<?>[] a = ParamAmount > 0 ? Arrays.copyOf(REFLECT_PARAMS, 1 + ParamAmount) : REFLECT_PARAMS;
		if(ParamAmount > 0) for(byte b = 0; b < ParamAmount; b++) a[b + 1] = String.class;
		try
		{
			try
			{
				Method c = PageClass.getDeclaredMethod(MethodName, a);
				c.setAccessible(true);
				return c;
			}
			catch (SecurityException Exception)
			{
				return PageClass.getMethod(MethodName, a);
			}
		}
		catch (SecurityException Exception) { }
		catch (NoSuchMethodException Exception) { }
		return null;
	}
}