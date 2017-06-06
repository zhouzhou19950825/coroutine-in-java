package com.upic.coroutine;


public abstract class AbstractUpicContinuation implements UpicContinuation{
	
	public UpicContinuationContext upicContinuationContext;

	@Override
	public UpicContinuationContext getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resume(Object t) {
		
	}

	@Override
	public void resumeWithException(Throwable exception) {
		
	}
	
	
}
