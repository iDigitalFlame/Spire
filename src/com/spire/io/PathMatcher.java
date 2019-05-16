package com.spire.io;

import com.spire.util.Constants;
import com.spire.ex.NullException;
import com.spire.ex.StringException;

public final class PathMatcher
{
	private static final byte MATCH_EQUALS = 0;
	private static final byte MATCH_STARTS_W = 1;
	private static final byte MATCH_ENDS_WITH = 2;
	private static final byte MATCH_IN_FOLDER = 3;
	private static final byte MATCH_FILE_EXTEN = 4;
	
	private final byte matchType;
	private final String matchObject;

	public final boolean isMatch(String FilePath)
	{
		if(FilePath == null) return false;
		switch(matchType)
		{
		case MATCH_EQUALS:
			return FilePath.equalsIgnoreCase(matchObject);
		case MATCH_STARTS_W:
			return FilePath.startsWith(matchObject);
		case MATCH_ENDS_WITH:
			int a = FilePath.indexOf(Constants.CURRENT_OS.systemSeperator);
			if(a == -1)
				return FilePath.equalsIgnoreCase(matchObject);
			return FilePath.substring(a).equalsIgnoreCase(matchObject);
		case MATCH_IN_FOLDER:
			String[] b = FilePath.split(String.valueOf(Constants.CURRENT_OS.systemSeperator));
			if(b != null)
			{
				for(int c = 0; c < b.length; c++)
					if(!b[c].equalsIgnoreCase(matchObject)) return false;
				return true;
			}
			break;
		case MATCH_FILE_EXTEN:
			int d = FilePath.indexOf('.');
			if(d >= 0)
				return FilePath.substring(d).equalsIgnoreCase(matchObject);
			break;
		}
		return false;
	}
	public final boolean equals(Object CompareObject)
	{
		return CompareObject instanceof PathMatcher && ((PathMatcher)CompareObject).matchType == matchType && ((PathMatcher)CompareObject).matchObject.equals(matchObject);
	}

	public final int hashCode()
	{
		return (1 + matchType) * matchObject.hashCode();
	}
	
	public final String toString()
	{
		return "PathMatcher(SH) " + matchType + ":" + matchObject;
	}
	
	public static final PathMatcher HasExt(String FileExt) throws NullException, StringException
	{
		if(FileExt == null) throw new NullException("FileExt");
		if(FileExt.isEmpty()) throw new StringException("FileExt");
		return new PathMatcher(MATCH_FILE_EXTEN, FileExt);
	}
	public static final PathMatcher HasName(String FileName) throws NullException, StringException
	{
		if(FileName == null) throw new NullException("FileName");
		if(FileName.isEmpty()) throw new StringException("FileName");
		return new PathMatcher(MATCH_ENDS_WITH, FileName);
	}
	public static final PathMatcher EqualsPath(String FilePath) throws NullException, StringException
	{
		if(FilePath == null) throw new NullException("FilePath");
		if(FilePath.isEmpty()) throw new StringException("FilePath");
		return new PathMatcher(MATCH_EQUALS, FilePath);
	}
	public static final PathMatcher ContainedInDrive(String DrivePath) throws NullException, StringException
	{
		if(DrivePath == null) throw new NullException("DrivePath");
		if(DrivePath.isEmpty()) throw new StringException("DrivePath");
		return new PathMatcher(MATCH_STARTS_W, DrivePath);
	}
	public static final PathMatcher ContainedInFolder(String FolderName) throws NullException, StringException
	{
		if(FolderName == null) throw new NullException("FolderName");
		if(FolderName.isEmpty()) throw new StringException("FolderName");
		return new PathMatcher(MATCH_IN_FOLDER, FolderName);
	}

	protected final PathMatcher clone()
	{
		return new PathMatcher(matchType, matchObject);
	}
	
	private PathMatcher(byte MatchType, String MatchPath)
	{
		matchType = MatchType;
		matchObject = MatchPath;
	}
}