package com.spire.io;

import com.spire.log.Reporter;
import com.spire.ex.IDException;
import com.spire.ex.ItemException;
import com.spire.ex.NullException;
import com.spire.ex.CloneException;
import java.lang.reflect.Constructor;
import com.spire.ex.ObjectIDException;

final class ItemController
{
	protected final ControllerSlot[] controllerMappings;
	protected final Class<? extends Item> controllerBase;
	
	public final void registerObjectClass(int ObjectID, Class<? extends ItemControllerObject> ObjectClass) throws ObjectIDException, NullException, ItemException
	{
		if(ObjectClass == null) throw new NullException("ObjectClass");
		if(ObjectID <= 0) throw new ObjectIDException("ObjectIDs must be greater than zero!");
		if(ObjectID > 255) throw new ObjectIDException("ObjectIDs cannot be greater than 255!");
		if(!Item.class.isAssignableFrom(ObjectClass))
			throw new ItemException("This class must be an Item subclass!");
		if(controllerMappings[ObjectID] != null && !controllerMappings[ObjectID].slotClass.isAssignableFrom(ObjectClass))
			throw new ItemException("ObjectID " + ObjectID + " is already in use by \"" + controllerMappings[ObjectID].slotClass.getName() + "\"!");
		if(controllerMappings[ObjectID] == null)
		{
			try
			{
				controllerMappings[ObjectID] = new ControllerSlot(ObjectClass);
				Reporter.debug(Reporter.REPORTER_GLOBAL, "ItemObject subclass \"" + ObjectClass.getName() + "\" was registered and added to the ItemObjects(" + controllerBase.getSimpleName() + ") listing!");
			}
			catch (NoSuchMethodException Exception)
			{
				throw new ItemException("ItemObject subclass " + ObjectClass.getName() + " does not have an avalible no args constructor!");
			}
			catch (SecurityException Exception)
			{
				throw new ItemException(Exception);
			}
		}
	}
	
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof ItemController && ((ItemController)CompareObject).controllerBase.equals(controllerBase);
	}
	
	public final int hashCode()
	{
		return controllerBase.hashCode();
	}
	
	public final String toString()
	{
		return "ItemController(ISC) for " + controllerBase.getName();
	}
	
	public final ItemController clone() throws CloneException
	{
		throw new CloneException("Cannot clone ItemControllers!");
	}
	
	protected ItemController(Class<? extends Item> ItemClass) throws IDException
	{
		if(!ItemControllerObject.class.isAssignableFrom(ItemClass))
			throw new IDException("ItemClass must implement the \"ItemControllerObject\" interface to support ControllerObjects!");
		controllerBase = ItemClass;
		controllerMappings = new ControllerSlot[Item.MAX_ITEMS];
	}
	
	protected static final class ControllerSlot
	{
		protected final Class<? extends ItemControllerObject> slotClass;
		protected final Constructor<? extends ItemControllerObject> slotConstructor;
		
		private ControllerSlot(Class<? extends ItemControllerObject> ControllerClass) throws NoSuchMethodException, SecurityException
		{
			slotClass = ControllerClass;
			slotConstructor = getConstructor(ControllerClass);
		}
		
		private static final Constructor<? extends ItemControllerObject> getConstructor(Class<? extends ItemControllerObject> ControllerClass) throws NoSuchMethodException, SecurityException
		{
			try
			{
				Constructor<? extends ItemControllerObject> a = ControllerClass.getDeclaredConstructor();
				a.setAccessible(true);
				return a;
			}
			catch (SecurityException Exception)
			{
				Constructor<? extends ItemControllerObject> a = ControllerClass.getConstructor();
				a.setAccessible(true);
				return a;
			}
		}
	}
}