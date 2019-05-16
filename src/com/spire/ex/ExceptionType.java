package com.spire.ex;

public final class ExceptionType
{
	public static final ExceptionType IO = new ExceptionType(5, "IO");
	public static final ExceptionType NONE = new ExceptionType(0, "DEBUG");
	public static final ExceptionType FORMAT = new ExceptionType(2, "GLOBAL");
	public static final ExceptionType INTERFACE = new ExceptionType(4, "GUI");
	public static final ExceptionType GENERAL = new ExceptionType(3, "GLOBAL");
	public static final ExceptionType NETWORK = new ExceptionType(4, "NETWORK");
	public static final ExceptionType SECURITY = new ExceptionType(5, "SECURITY");
	
	protected static final String UI_TITLE = "UI_ERROR";
	protected static final String ID_TITLE = "INVALID_ID";
	protected static final String STRING_TITLE = "STRING";
	protected static final String FORMAT_TITLE = "FORMAT";
	protected static final String LOAD_TITLE = "READ_ERROR";
	protected static final String ITEM_TITLE = "INVALID_ITEM";
	protected static final String INTERNAL_TITLE = "INTERNAL";
	protected static final String NULL_TITLE = "NULL_POINTER";
	protected static final String CLONE_TITLE = "CANNOT_CLONE";
	protected static final String NUMBER_TITLE = "NUMBER_RANGE";
	protected static final String REFERENCE_TITLE = "BAD_REFERENCE";
	protected static final String REFLECT_TITLE = "REFLECTION_ERROR";
	protected static final String PERMISSION_TITLE = "FAILED_PERMISSION";
	
	protected final byte typeLevel;
	protected final String typeLog;
	
	public ExceptionType(int TypeLevel, String TypeLog)
	{
		typeLog = TypeLog;
		typeLevel = (byte)TypeLevel;
	}
}