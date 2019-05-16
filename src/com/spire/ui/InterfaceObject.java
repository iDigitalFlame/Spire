package com.spire.ui;

import java.io.IOException;

import javax.swing.AbstractButton;
import javax.swing.JComponent;

import com.spire.ex.NullException;
import com.spire.ex.NumberException;
import com.spire.ex.ObjectIDException;
import com.spire.io.Item;
import com.spire.io.ItemControllerObject;
import com.spire.io.Streamable;
import com.spire.log.Reporter;
import com.spire.util.ItemList;

public abstract class InterfaceObject extends Item implements ItemControllerObject
{
	public static final byte ITEM_CLASS_ID = 15;
	
	final ItemList<InterfaceAction> objectActions;
	
	byte objectID;
	int objectWidth;
	int objectHeight;
	String objectName;
	String objectLayout;
	byte objectLayoutType;
	int objectBackgroundColor;
	int objectForegroundColor;
	
	protected String objectCanvas;
	protected Object objectComponent;
	protected boolean objectAnimated;
	
	public final void setBackground(int BackgroundColor)
	{
		
	}
	public final void setSize(int Width, int Height) throws NumberException
	{
		if(Width < 0) throw new NumberException("Width", Width, false);
		if(Height < 0) throw new NumberException("Height", Height, false);
		objectWidth = Width;
		objectHeight = Height;
		getObjectComponent().setSize(Width, Height);
	}
	
	public final boolean isAnimated()
	{
		return objectAnimated;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof InterfaceObject && CompareObject.hashCode() == hashCode();
	}
	
	public final int hashCode()
	{
		return objectWidth + objectHeight + objectBackgroundColor + objectForegroundColor + objectLayoutType + (objectCanvas != null ? objectCanvas.hashCode() : 0) +
			   objectName.hashCode() + (objectLayout != null ? objectLayout.hashCode() : 0) + objectActions.hashCode();
	}
	public final int getItemSubID()
	{
		return objectID;
	}
	
	public abstract String getText();
	
	public final JComponent getObjectPart()
	{
		return getObjectComponentPart();
	}
	
	public abstract Object getValue();
	
	protected InterfaceObject(int ObjectID) throws ObjectIDException
	{
		super(ITEM_CLASS_ID);
		objectID = (byte)ObjectID;
		objectActions = new ItemList<InterfaceAction>();
		Item.addItemObjectMapping(ITEM_CLASS_ID, ObjectID, getClass());
	}

	protected void readItemFailure()
	{
		objectID = -1;
		objectWidth = 0;
		objectHeight = 0;
		objectName = "null";
		objectLayoutType = 0;
		objectCanvas = null;
		objectBackgroundColor = 0;
		objectForegroundColor = 0;
	}
	protected void setTextLimit(int TextLimit)
	{
		if(objectID + TextLimit > 0) { }
	}
	protected void setUneditable(boolean CanEdit)
	{
		if(objectID > 0 && CanEdit) { }
	}
	protected abstract void setText(String TextValue);
	protected abstract void setValue(Object ObjectValue);
	protected abstract void appendText(String AddTextValue);
	
	protected final void refreshAllActions(Interface InterfaceWindow)
	{
		if(!objectActions.isEmpty() && getObjectComponent() instanceof AbstractButton) 
			for(int a = 0; a < objectActions.size(); a++)
			{
				objectActions.get(a).registerWindow(InterfaceWindow);
				((AbstractButton)getObjectComponent()).addActionListener(objectActions.get(a));
			}
	}
	protected final void readItem(Streamable InStream) throws IOException
	{
		
	}
	protected final void writeItem(Streamable OutStream) throws IOException
	{
		
	}
	protected final void removeAction(int ActionIndex) throws NumberException
	{
		if(ActionIndex < 0) throw new NumberException("ActionIndex", ActionIndex, false);
		if(ActionIndex > objectActions.size()) throw new NumberException("ActionIndex", ActionIndex, 0, objectActions.size());
		Reporter.info(Reporter.REPORTER_GUI, "InterfaceAction \"" + objectActions.remove(ActionIndex).getClass().getSimpleName() + "\" was removed from object \"" + objectName + "\"!");
	} 
	protected final void addAction(InterfaceAction InterfaceAction) throws NullException
	{
		if(InterfaceAction == null) throw new NullException("InterfaceAction");
		objectActions.add(InterfaceAction);
		Reporter.info(Reporter.REPORTER_GUI, "InterfaceAction \"" + InterfaceAction.getClass().getSimpleName() + "\" was added to object \"" + objectName + "\"!");
	}
	
	protected final String getName()
	{
		return objectName;
	}
	
	protected JComponent getObjectComponent()
	{
		return (JComponent)objectComponent;
	}
	protected final JComponent getObjectComponentPart()
	{
		return (JComponent)objectComponent;
	}
	
	protected final InterfaceObject getCopy()
	{
		return null;
	}
}