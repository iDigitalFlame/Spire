package com.spire.web.mocha;

import com.spire.web.WebState;

public abstract class MochaPart
{
	protected short partEnd;
	protected short partStart;
	protected byte partBlockEnd;
	protected int partBlockStart;
	
	protected abstract Object doPartAction(MochaPage CurrentPage, WebState Current);
}