package com.spire.mail;

/**
 * Always passed as thread
 *
 */
public interface EmailFolderListener
{
	void folderUpdated(EmailFolder UpdatedFolder, Email[] MessagesAdded);
	void folderExpunged(EmailFolder UpdatedFolder, Email[] MessagesDeleted);

	boolean runAsThread();
}