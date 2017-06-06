package com.upic.uploadTest;

import com.upic.coroutine.AbstractUpicContinuationContext;

public  class UploadPath extends AbstractUpicContinuationContext
{
	private  String path;
	public static final Key Key = new ThisKey();

	public  String getPath()
	{
		return path;
	}

	public UploadPath(String path)
	{
		super(Key);
		this.path = path;
	}
	public static class ThisKey implements Key {

		public ThisKey() {
		}
	}
}