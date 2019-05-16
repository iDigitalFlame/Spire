package com.spire.web.mocha;

import com.spire.web.WebPage;
import com.spire.web.WebState;
import com.spire.log.Reporter;
import com.spire.util.Constants;

class MochaInclude
{
	protected final int includeRound;
	protected final short includeEnd;
	protected final short includeStart;
	protected final String includeName;
	protected final MochaRegister includeRegister;
	
	protected WebPage includePage;
	
	protected MochaInclude(int IncludeRound, short IncludeStart, short IncludeEnd, String IncludeName, MochaRegister Register)
	{
		includeEnd = IncludeEnd;
		includeName = IncludeName;
		includeRound = IncludeRound;
		includeStart = IncludeStart;
		includeRegister = Register;
	}
	
	protected final String getResult(MochaPage Page, WebState Current)
	{
		if((includeRegister.registerTrigger == MochaRegister.ON_POSTBACK && Current.isPost()) || includeRegister.registerTrigger != MochaRegister.ON_POSTBACK) try
		{
			Object a = includeRegister.getResult(Page, Current, this, includeName);
			if(a != null) return String.valueOf(a);
		}
		catch (Throwable Exception)
		{
			Reporter.error(Reporter.REPORTER_WEB, Exception);
			return Constants.EMPTY_STRING;
		}
		return Constants.EMPTY_STRING;
	}
}