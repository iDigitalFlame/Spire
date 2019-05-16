package com.spire.util;

import com.spire.io.Item;
import java.io.IOException;
import com.spire.log.Reporter;
import com.spire.io.DataObject;
import com.spire.io.Streamable;
import java.lang.reflect.Method;
import com.spire.ex.NullException;
import com.spire.ex.StringException;
import com.spire.ex.FormatException;
import com.spire.ex.InternalException;
import java.lang.reflect.InvocationTargetException;

public final class Reflect extends Item
{
	public static final byte ITEM_CLASS_ID = 4;
	
	private Method reflectMethod;
	private Object reflectObject;
	private Class<?>[] reflectParams;
	private boolean reflectWriteObject;
	
	public Reflect(String TargetMethod, Object TargetObject) throws NullException, InternalException, StringException
	{
		this(TargetObject.getClass(), TargetMethod, TargetObject, (Class<?>[])null);
	}
	public Reflect(String TargetClass, String TargetMethod) throws NullException, InternalException, StringException
	{
		this(classFromString(TargetClass), TargetMethod, null, (Class<?>[])null);
	}
	public Reflect(Class<?> TargetClass, String TargetMethod) throws NullException, InternalException, StringException
	{
		this(TargetClass, TargetMethod, null, (Class<?>[])null);
	}
	public Reflect(String TargetClass, String TargetMethod, Class<?>... TargetParams) throws NullException, InternalException, StringException
	{
		this(classFromString(TargetClass), TargetMethod, null, TargetParams);
	}
	public Reflect(String TargetMethod, Object TargetObject, Class<?>... TargetParams) throws NullException, InternalException, StringException
	{
		this(TargetObject.getClass(), TargetMethod, TargetObject, TargetParams);
	}
	public Reflect(Class<?> TargetClass, String TargetMethod, Class<?>... TargetParams) throws NullException, InternalException, StringException
	{
		this(TargetClass, TargetMethod, null, TargetParams);
	}
	public Reflect(String TargetClass, String TargetMethod, Object TargetObject, Class<?>... TargetParams) throws NullException, InternalException, StringException
	{
		this(classFromString(TargetClass), TargetMethod, TargetObject, TargetParams);
	}
	public Reflect(Class<?> TargetClass, String TargetMethod, Object TargetObject, Class<?>... TargetParams) throws NullException, InternalException, StringException
	{
		super(ITEM_CLASS_ID);
		if(TargetClass == null) throw new NullException("TargetClass");
		if(TargetMethod == null) throw new NullException("TargetMethod");
		if(TargetMethod.isEmpty()) throw new StringException("TargetMethod");
		try
		{
			reflectObject = TargetObject;
			reflectParams = TargetParams;
			reflectMethod = TargetClass.getDeclaredMethod(TargetMethod, TargetParams);
		}
		catch (SecurityException Exception)
		{
			throw new InternalException("SecurityException blocked creation! " + Exception.toString());
		}
		catch (NoSuchMethodException Exception)
		{
			throw new InternalException("The method \"" + TargetMethod + "\" was not found in class \"" + TargetClass.getName() + "\"!");
		}
	}
	
	public final void writeReflectObject(boolean WriteObject)
	{
		reflectWriteObject = WriteObject;
	}
	public final void setReflectObject(Object ReflectObject) throws FormatException
	{
		if(ReflectObject == null)
			reflectObject = null;
		else
		{
			if(reflectMethod.getDeclaringClass().isAssignableFrom(ReflectObject.getClass()))
				reflectObject = ReflectObject;
			else throw new FormatException("This Object is not a class or subclass of \"" + reflectMethod.getDeclaringClass().getName() + "\"!");
		}
	}
	
	public final boolean hasParamaters()
	{
		return reflectParams != null;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof Reflect && super.equals(CompareObject) && (reflectParams == null && ((Reflect)CompareObject).reflectParams == null) ||
			   reflectParams.equals(((Reflect)CompareObject).reflectParams);
	}
	public final boolean hasParamaters(Class<?>... Paramaters)
	{
		if(reflectParams == null || Paramaters == null) return false;
		for(byte a = 0; a < reflectParams.length; a++)
			if(!reflectParams[a].isAssignableFrom(Paramaters[a])) return false;
		return true;
	}
	
	public final int hashCode()
	{
		return reflectMethod.hashCode() + (reflectObject != null ? reflectObject.hashCode() : 0);
	}
	
	public final String toString()
	{
		return "Reflect(" + getItemID() + ") " + getReflectClass().getName() + "@" + getMethodName();
	}
	public final String getMethodName()
	{
		return reflectMethod.getName();
	}
	
	public final Class<?> getReturnClass()
	{
		return reflectMethod.getReturnType();
	}
	public final Class<?> getReflectClass()
	{
		return reflectMethod.getDeclaringClass();
	}
	
	public final Object inokeReflect() throws InternalException
	{
		return inokeReflect(reflectObject, (Object[])null);
	}
	public final Object inokeReflect(Object TargetObject) throws InternalException
	{
		return inokeReflect(TargetObject, (Object[])null);
	}
	public final Object inokeReflect(Object[] TargetParamaters) throws InternalException
	{
		return inokeReflect(reflectObject, TargetParamaters);
	}
	public final Object inokeReflect(Object TargetObject, Object[] TargetParamaters) throws InternalException
	{
		try
		{
			return reflectMethod.invoke(TargetObject, TargetParamaters);
		}
		catch (IllegalAccessException Exception)
		{
			throw new InternalException(Exception);
		}
		catch (IllegalArgumentException Exception)
		{
			throw new InternalException(Exception);
		}
		catch (InvocationTargetException Exception)
		{
			throw new InternalException(Exception);
		}
	}
	
	public static final Object[] makeParams(Object... TargetParamaters)
	{
		return TargetParamaters;
	}
	
	protected Reflect()
	{
		super(ITEM_CLASS_ID);
	}
	
	protected final void readItemFailure()
	{
		reflectMethod = null;
		reflectObject = null;
	}
	protected final void readItem(Streamable InStream) throws IOException
	{
		String a = itemEncoder.readString(InStream), b = itemEncoder.readString(InStream);
		if(a == null || a.isEmpty() || b == null || b.isEmpty())
			throw new InternalException("Cannot create a Reflect from a null or empty name/class!");
		reflectObject = Item.getNextItemByID(InStream, 2);
		if(reflectObject != null) reflectObject = ((DataObject)reflectObject).getObject();
		reflectParams = new Class<?>[itemEncoder.readUnsignedByte(InStream)];	
		if(reflectParams.length > 0) for(byte c = 0; c < reflectParams.length; c++)
			reflectParams[c] = classFromString(itemEncoder.readString(InStream));
		try
		{
			Class<?> e = classFromString(b);
			if(e == null) throw new InternalException("Cannot find the class \"" + b + "\"!");
			reflectMethod = e.getDeclaredMethod(a, reflectParams);
		}
		catch (SecurityException Exception)
		{
			throw new InternalException("SecurityException blocked creation! " + Exception.toString());
		}
		catch (NoSuchMethodException Exception)
		{
			throw new InternalException("The method \"" + b + "\" was not found in class \"" + a + "\"!");
		}
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{
		itemEncoder.writeString(OutStream, reflectMethod.getName());
		itemEncoder.writeString(OutStream, reflectMethod.getDeclaringClass().getName());
		if(DataObject.supportsObjectType(reflectObject) && reflectWriteObject)
			DataObject.createFromObject(reflectObject).writeStream(OutStream);
		else Item.writeNullItem(null, OutStream);
		if(reflectParams != null && reflectParams.length > 0)
		{
			itemEncoder.writeByte(OutStream, reflectParams.length);
			for(byte a = 0; a < reflectParams.length; a++)
				itemEncoder.writeString(OutStream, reflectParams[a].getName());
		}
		else
			itemEncoder.writeByte(OutStream, 0);
	}
	
	protected final Reflect getCopy()
	{
		Reflect a = new Reflect();
		a.reflectMethod = reflectMethod;
		a.reflectObject = reflectObject;
		a.reflectParams = reflectParams;
		a.reflectWriteObject = reflectWriteObject;
		return a;
	}
	
	private static final Class<?> classFromString(String ClassName)
	{
		if(ClassName == null || ClassName.isEmpty()) return null;
		try
		{
			return Class.forName(ClassName);
		}
		catch (ClassNotFoundException Excption)
		{
			Reporter.warning(Reporter.REPORTER_GLOBAL, "Could not find a class from the name \"" + ClassName + "\"!");
		}
		return null;
	}
}