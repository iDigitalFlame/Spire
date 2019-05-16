package com.spire.io;

import java.io.IOException;

public interface Storage
{
	public void readStorage(Streamable InStream, Encoder StorageEncoder) throws IOException;
	public void writeStorage(Streamable OutStream, Encoder StorageEncoder) throws IOException;
}