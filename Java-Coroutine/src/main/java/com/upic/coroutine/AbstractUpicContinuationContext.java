package com.upic.coroutine;

import com.upic.coroutine.enums.StateEnum;

/**
 * 
 * @author DTZ
 *
 */
public abstract class AbstractUpicContinuationContext implements Element {
	
	//标识符(也可以用AtomicInteger 生成序列)
	private Key key;

	private Element element;
	private UpicContinuationContext left;
	//运行状态 默认为run
	private StateEnum state=StateEnum.RUN;
	
	public AbstractUpicContinuationContext(UpicContinuationContext left, Element element) {
		this.element = element;
		this.left = left;
	}
	public AbstractUpicContinuationContext(UpicContinuationContext left) {
		super();
		this.left = left;
	}
	
	public AbstractUpicContinuationContext(Key key) {
		super();
		this.key=key;
	}
	
	@Override
	public Element get(Key key) {
		AbstractUpicContinuationContext cur = this;
		Element element2 = cur.element.get(key);
		if (element2 == null) {
			return null;
		}
		UpicContinuationContext next = cur.left;
		if (next instanceof AbstractUpicContinuationContext) {
			cur = (AbstractUpicContinuationContext) next;
		} else {
			return next.get(key);
		}
		return cur;
	}

	@Override
	public Object fold(Object obj) {
		return null;
	}

	@Override
	public UpicContinuationContext plus(UpicContinuationContext coroutinecontext1) {
		return coroutinecontext1;
	}

	@Override
	public UpicContinuationContext minusKey(Key key) {
		return null;
	}

	@Override
	public Key getKey() {
		return key;
	}
	public StateEnum state() {
		return state;
	}
	public void setState(StateEnum state) {
		this.state = state;
	}

}
