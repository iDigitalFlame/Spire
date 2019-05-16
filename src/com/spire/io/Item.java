package com.spire.io;

import java.io.IOException;
import java.io.EOFException;
import com.spire.log.Reporter;
import com.spire.sec.Security;
import com.spire.ex.IDException;
import com.spire.net.Compatible;
import java.lang.reflect.Modifier;
import com.spire.ex.ItemException;
import com.spire.ex.LoadException;
import com.spire.ex.NullException;
import com.spire.ex.CloneException;
import java.lang.reflect.Constructor;
import com.spire.ex.ObjectIDException;
import com.spire.ex.PermissionException;

import java.lang.reflect.InvocationTargetException;

public abstract class Item extends Compatible
{
	public static final short MAX_ITEMS = 256;

	private static final ItemSlot[] ITEM_MAPPINGS = new ItemSlot[MAX_ITEMS];

	protected final byte itemID;
	protected final Encoder itemEncoder;

	public final void writeStream(Streamable OutStream) throws IOException, NullException
	{
		if(OutStream == null) throw new NullException("OutStream");
		if(!OutStream.isStreamOutput())
		{
			Reporter.error(Reporter.REPORTER_IO, "This does not conatin an OutputStream!");
			throw new IOException("This does not conatin an OutputStream!");
		}
		Encoder.ENCODER_LIST.get().getLastEncoder(false).writeByte(OutStream, itemID);
		if(this instanceof ItemControllerObject)
			Encoder.ENCODER_LIST.get().getLastEncoder(false).writeByte(OutStream, ((ItemControllerObject)this).getItemSubID());
		itemEncoder.openOutputStream(OutStream, true);
		writeItem(OutStream);
	}

	public boolean equals(Object CompareObject)
	{
		return CompareObject instanceof Item && ((Item)CompareObject).itemID == itemID && ((Item)CompareObject).itemEncoder.equals(itemEncoder) && CompareObject.hashCode() == hashCode();
	}

	public final int getItemID()
	{
		return itemID & 0xFF;
	}
	public abstract int hashCode();

	public String toString()
	{
		return getClass().getSimpleName() + "(" + (itemID & 0xFF) + ") [" + Math.abs(hashCode()) + "]";
	}

	public final Item clone() throws CloneException
	{
		Item a = getCopy();
		if(a == null) throw new CloneException("The Item subclass \"" + getClass().getName() + "\" cannot be cloned!");
		a.itemEncoder.refreshEncoderABC();
		return a;
	}

	public static final void setReceivingLocal()
	{
		Encoder.ENCODER_LIST.get().generateNewSeed();
	}
	public static final void setReceivingSeed(long ReceivingSeed)
	{
		Encoder.ENCODER_LIST.get().generateNewSeed(ReceivingSeed);
	}
	public static final void setAllThrowReadErrors(boolean ThrowRead) throws PermissionException
	{
		Security.valid("item.setthrow.all");
		for(int a = 0; a < 256; a++) if(ITEM_MAPPINGS[a] != null) ITEM_MAPPINGS[a].slotItemThrows = ThrowRead;
		Reporter.debug(Reporter.REPORTER_GLOBAL, "All Item subclasses were set to " + (ThrowRead ? "throw" : "supress") + " read errors");
	}
	public static final void closeCurrentController(Streamable Stream) throws IOException, NullException
	{
		if(Stream == null) throw new NullException("Stream");
		Encoder.ENCODER_LIST.get().close(Stream);
	}
	public static final void writeNullItem(Item ItemObject, Streamable OutStream) throws IOException, NullException
	{
		if(ItemObject == null) Encoder.ENCODER_LIST.get().getLastEncoder(false).writeByte(OutStream, 0);
		else ItemObject.writeStream(OutStream);
	}
	public static final void addItemMapping(int ItemID, Class<? extends Item> ItemClass) throws NullException, IDException
	{
		if(ItemClass == null) throw new NullException("ItemClass");
		if(ItemID <= 0) throw new IDException("ItemIDs must be greater than zero!");
		if(ItemID > 255) throw new IDException("ItemIDs cannot be greater than 255!");
		if(ITEM_MAPPINGS[ItemID] != null && !ITEM_MAPPINGS[ItemID].slotClass.isAssignableFrom(ItemClass))
			throw new IDException("ItemID " + ItemID + " is already in use by \"" + ITEM_MAPPINGS[ItemID].slotClass.getName() + "\"!");
		if(ITEM_MAPPINGS[ItemID] == null) setItemMapping(ItemID, ItemClass, false);
	}
	public static final void readFinalItem(Item ItemObject, Streamable InStream) throws IOException, NullException, LoadException
	{
		if(ItemObject == null) throw new NullException("ItemObject");
		if(ItemObject.getItemID() == Encoder.ENCODER_LIST.get().readItemByte(InStream))
		{
			if(ItemObject instanceof ItemControllerObject && ((ItemControllerObject)ItemObject).getItemSubID() != Encoder.ENCODER_LIST.get().readItemByte(InStream))
				return;
			ItemObject.itemEncoder.openInputStream(InStream, true);
			ItemObject.readStream(InStream);
		}
	}
	public static final void setThrowReadErrors(Class<? extends Item> ItemClass, boolean ThrowRead) throws PermissionException, NullException
	{
		Security.valid("item.setthrow");
		if(ItemClass == null) throw new NullException("ItemClass");
		for(int a = 0; a < 256; a++) if(ITEM_MAPPINGS[a] != null && ITEM_MAPPINGS[a].slotClass.isAssignableFrom(ItemClass))
		{
			ITEM_MAPPINGS[a].slotItemThrows = ThrowRead;
			Reporter.debug(Reporter.REPORTER_GLOBAL, "Item subclass \"" + ItemClass.getName() + "\" was set to " + (ThrowRead ? "throw" : "supress") + " read errors");
			break;
		}
	}
	public static final void addItemObjectMapping(int ItemID, int ObjectID, Class<? extends ItemControllerObject> ObjectClass) throws NullException, IDException, ObjectIDException, ItemException
	{
		if(ObjectClass == null) throw new NullException("ObjectClass");
		if(ItemID <= 0) throw new IDException("ItemIDs must be greater than zero!");
		if(ItemID > 255) throw new IDException("ItemIDs cannot be greater than 255!");
		if(ObjectID <= 0) throw new ObjectIDException("ObjectIDs must be greater than zero!");
		if(ObjectID > 255) throw new ObjectIDException("ObjectIDs cannot be greater than 255!");
		if(ITEM_MAPPINGS[ItemID] == null) throw new ItemException("There is not an Item registered at ItemID " + ItemID + " !");
		if(ITEM_MAPPINGS[ItemID].slotController == null) throw new ItemException("The Item subclass at ItemID " + ItemID + " does not support ItemObjects!");
		ITEM_MAPPINGS[ItemID].slotController.registerObjectClass(ObjectID, ObjectClass);
	}

	public static final Item getNextItem(Streamable InStream) throws IOException, NullException, LoadException
	{
		return getNextItemByID(InStream, -1);
	}
	public static final Item getNextItemByID(Streamable InStream, int ItemID) throws IOException, NullException, LoadException
	{
		if(InStream == null) throw new NullException("InStream");
		if(!InStream.isStreamInput())
		{
			Reporter.error(Reporter.REPORTER_IO, "This does not contain an InputStream!");
			throw new IOException("This does not contain an InputStream!");
		}
		if(ItemID == 0) return null;
		try
		{
			short a = Encoder.ENCODER_LIST.get().readItemByte(InStream);
			Reporter.debug(Reporter.REPORTER_IO, "Expecting ID: " + ItemID + " received " + a);
			if(a >= 1 && a <= 255 && (ItemID == -1 || ItemID == a) && ITEM_MAPPINGS[a] != null)
			{
				Reporter.debug(Reporter.REPORTER_GLOBAL, "Grabbing Item \"" + ITEM_MAPPINGS[a].slotClass.getName() + "\" from InStream");
				Item b = null;
				if(ITEM_MAPPINGS[a].slotController != null)
				{
					short c = Encoder.ENCODER_LIST.get().readItemByte(InStream);
					if(ITEM_MAPPINGS[a].slotController.controllerMappings[c] != null)
					{
						b = (Item)ITEM_MAPPINGS[a].slotController.controllerMappings[c].slotConstructor.newInstance();
						Reporter.debug(Reporter.REPORTER_GLOBAL, "Grabbing ItemObject \"" + ITEM_MAPPINGS[a].slotController.controllerMappings[c].slotClass.getName() + "\" from InStream");
					}
					else
						return null;
				}
				else
					b = ITEM_MAPPINGS[a].slotConstructor.newInstance();
				b.itemEncoder.openInputStream(InStream, true);
				b.readStream(InStream);
				return b;
			}
		}
		catch (EOFException Exception) { }
		catch (IllegalArgumentException Exception) { }
		catch (SecurityException Exception)
		{
			Reporter.error(Reporter.REPORTER_SECURITY, "ITEM_SECURITY_ERROR", Exception);
		}
		catch (InstantiationException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, "ITEM_REFLECT_ERROR", Exception);
		}
		catch (IllegalAccessException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, "ITEM_REFLECT_ERROR", Exception);
		}
		catch (InvocationTargetException Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, "ITEM_REFLECT_ERROR", Exception.getCause());
		}
		catch (ExceptionInInitializerError Exception)
		{
			Reporter.error(Reporter.REPORTER_IO, "ITEM_REFLECT_ERROR", Exception.getCause());
		}
		return null;
	}

	public static final ItemController getItemObjectController(int ItemID) throws IDException, ItemException
	{
		if(ItemID <= 0) throw new IDException("ItemIDs must be greater than zero!");
		if(ItemID > 255) throw new IDException("ItemIDs cannot be greater than 255!");
		if(ITEM_MAPPINGS[ItemID] == null) throw new ItemException("There is not an Item registered at ItemID " + ItemID + " !");
		if(ITEM_MAPPINGS[ItemID].slotController == null) throw new ItemException("The Item subclass at ItemID " + ItemID + " does not support ItemObjects!");
		return ITEM_MAPPINGS[ItemID].slotController;
	}

	protected Item(int ItemID) throws IDException
	{
		if(ItemID <= 0) throw new IDException("ItemIDs must be greater than zero!");
		if(ItemID > 255) throw new IDException("ItemIDs cannot be greater than 255!");
		if(ITEM_MAPPINGS[ItemID] != null && !ITEM_MAPPINGS[ItemID].slotClass.isAssignableFrom(getClass()))
			throw new IDException("ItemID " + ItemID + " is already in use by \"" + ITEM_MAPPINGS[ItemID].slotClass.getName() + "\"!");
		if(ITEM_MAPPINGS[ItemID] == null) setItemMapping(ItemID, getClass(), false);
		itemID = (byte)ItemID;
		itemEncoder = new Encoder(ItemID);
	}

	protected abstract void readItemFailure();
	protected abstract void readItem(Streamable InStream) throws IOException;
	protected abstract void writeItem(Streamable OutStream) throws IOException;
	protected final void setThrowReadErrors(boolean ThrowRead) throws PermissionException
	{
		Security.valid("item.setthrow");
		ITEM_MAPPINGS[itemID & 0xFF].slotItemThrows = ThrowRead;
		Reporter.debug(Reporter.REPORTER_GLOBAL, "Item subclass \"" + getClass().getName() + "\" was set to " + (ThrowRead ? "throw" : "supress") + " read errors");
	}

	protected Item getCopy()
	{
		try
		{
			return (Item)super.clone();
		}
		catch (CloneNotSupportedException Exception) { }
		return null;
	}

	private final void readStream(Streamable InStream) throws IOException, LoadException
	{
		try
		{
			readItem(InStream);
		}
		catch (IOException Exception)
		{
			throw Exception;
		}
		catch (Throwable Exception)
		{
			Exception.printStackTrace();
			if(ITEM_MAPPINGS[itemID & 0xFF].slotItemThrows)
				throw new LoadException(Exception);
			readItemFailure();
		}
	}

	private static final void setItemMapping(int ItemID, Class<? extends Item> ItemClass, boolean ThrowRead) throws IDException
	{
		if(ItemID <= 0) throw new IDException("ItemIDs must be greater than zero!");
		if(ItemID > 255) throw new IDException("ItemIDs cannot be greater than 255!");
		if(Modifier.isAbstract(ItemClass.getModifiers()))
		{
			if(!ItemControllerObject.class.isAssignableFrom(ItemClass))
				throw new IDException("This Item subclass is abstract but must implement the \"ItemControllerObject\" interface to be added!");
			try
			{
				ITEM_MAPPINGS[ItemID] = new ItemSlot(ItemClass, ThrowRead, true);
			}
			catch (NoSuchMethodException Exception)
			{
				Reporter.error(Reporter.REPORTER_GLOBAL, "ItemObject subclass" + ItemClass.getName() + " did not register correctly!");
			}
		}
		else
		{
			try
			{
				ITEM_MAPPINGS[ItemID] = new ItemSlot(ItemClass, ThrowRead, false);
				Reporter.debug(Reporter.REPORTER_GLOBAL, "Item subclass \"" + ItemClass.getName() + "\" was registered and added to the Items listing!");
			}
			catch (NoSuchMethodException Exception)
			{
				Reporter.error(Reporter.REPORTER_GLOBAL, "Item " + ItemClass.getName() + " does not have an avalible no args constructor!");
			}
			catch (SecurityException Exception)
			{
				Reporter.error(Reporter.REPORTER_SECURITY, Exception);
			}
		}
	}

	static
	{
		setItemMapping(1, com.spire.net.Computer.class, false);
		setItemMapping(2, com.spire.io.DataObject.class, false);
		setItemMapping(3, com.spire.util.SyncedList.class, false);
		setItemMapping(4, com.spire.util.Reflect.class, false);
		setItemMapping(5, com.spire.net.Header.class, false);
		setItemMapping(6, com.spire.net.Packet.class, false);
		setItemMapping(7, com.spire.net.DataPacket.class, false);
		setItemMapping(8, com.spire.net.FilePacket.class, false);
		setItemMapping(9, com.spire.net.FileData.class, false);
		setItemMapping(10, com.spire.exc.Program.class, false);
		setItemMapping(11, com.spire.net.Group.class, false);
		setItemMapping(12, com.spire.util.ItemList.class, false);
		setItemMapping(13, com.spire.util.NamedObjectMap.class, false);
		setItemMapping(14, com.spire.ui.InterfaceAction.class, false);
		setItemMapping(15, com.spire.sql.SQLPacket.class, false);
		setItemMapping(16, com.spire.cred.Credentials.class, false);
		setItemMapping(17, com.spire.mail.EmailSettings.class, false);
		setItemMapping(18, com.spire.mail.EmailAccount.class, false);
		setItemMapping(19, com.spire.mail.Email.class, false);
		setItemMapping(20, com.spire.mail.EmailTerms.class, false);
		setItemMapping(21, com.spire.io.KeyChain.class, false);
		setItemMapping(22, com.spire.mail.EmailFolder.class, false);

		setItemMapping(30, com.spire.sec.per.BooleanRule.class, false);
		setItemMapping(31, com.spire.sec.per.ClassRule.class, false);
		setItemMapping(32, com.spire.sec.per.StringRule.class, false);
		setItemMapping(33, com.spire.sec.per.ByteRule.class, false);
		setItemMapping(34, com.spire.sec.per.ShortRule.class, false);
		setItemMapping(35, com.spire.sec.per.IntegerRule.class, false);
		setItemMapping(36, com.spire.sec.per.InetRule.class, false);
	}

	private static final class ItemSlot
	{
		private final ItemController slotController;
		private final Class<? extends Item> slotClass;
		private final Constructor<? extends Item> slotConstructor;

		private boolean slotItemThrows;

		private ItemSlot(Class<? extends Item> ItemClass, boolean ItemThrows, boolean ItemAbstract) throws NoSuchMethodException, SecurityException
		{
			slotClass = ItemClass;
			slotItemThrows = ItemThrows;
			slotController = ItemAbstract ? new ItemController(ItemClass) : null;
			slotConstructor = ItemAbstract ? null : getConstructor(ItemClass);
		}

		private static final Constructor<? extends Item> getConstructor(Class<? extends Item> ItemClass) throws NoSuchMethodException, SecurityException
		{
			try
			{
				Constructor<? extends Item> a = ItemClass.getDeclaredConstructor();
				a.setAccessible(true);
				return a;
			}
			catch (SecurityException Exception)
			{
				Constructor<? extends Item> a = ItemClass.getConstructor();
				a.setAccessible(true);
				return a;
			}
		}
	}
}