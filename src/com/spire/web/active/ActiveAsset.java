package com.spire.web.active;

public abstract class ActiveAsset
{
	protected final byte[] assetData;
	protected final boolean assetContainer;
	
	protected ActiveAsset(String AssetName)
	{
		this(AssetName, false);
	}
	protected ActiveAsset(String AssetName, boolean IsContainer)
	{
		assetContainer = IsContainer;
		assetData = AssetName.toLowerCase().getBytes();
	}
	
	protected abstract Object doProcess(Object WebPage);
}