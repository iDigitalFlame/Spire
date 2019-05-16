package com.spire.net;

import java.io.IOException;
import com.spire.io.Streamable;
import com.spire.ex.NullException;

public interface FileHolder
{
	void readFileFromStream(Streamable InStream) throws IOException, NullException;
	void writeFileToStream(Streamable OutStream) throws IOException, NullException;
}