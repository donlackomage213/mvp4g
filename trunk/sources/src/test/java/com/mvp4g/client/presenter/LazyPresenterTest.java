package com.mvp4g.client.presenter;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.ParameterizedType;

import org.junit.Test;

import com.mvp4g.client.event.EventBus;
import com.mvp4g.client.event.EventBusWithLookup;
import com.mvp4g.client.view.LazyView;

public class LazyPresenterTest {

	private class LazyViewImpl implements LazyView {

		private boolean created = false;

		public void createView() {
			created = true;
		}

		public boolean isCreated() {
			return created;
		}

	}

	private class LazyPresenterImpl extends LazyPresenter<LazyViewImpl, EventBus> {

		private boolean created = false;
		private boolean binded = false;

		@Override
		public void createPresenter() {
			created = true;
		}

		@Override
		public void bindView() {
			binded = true;
		}

		public boolean isCreated() {
			return created;
		}

		public boolean isBinded() {
			return binded;
		}
	}

	@Test
	public void testLazyBinding() {
		LazyPresenterImpl presenter = new LazyPresenterImpl();
		LazyViewImpl view = new LazyViewImpl();
		presenter.setView( view );

		assertFalse( view.isCreated() );
		assertFalse( presenter.isCreated() );
		assertFalse( presenter.isBinded() );
		presenter.bind();
		assertTrue( view.isCreated() );
		assertTrue( presenter.isCreated() );
		assertTrue( presenter.isBinded() );

	}
	
	@Test
	public void testLazyXmlPresenter() {
		LazyXmlPresenter<LazyViewImpl> presenter = new LazyXmlPresenter<LazyViewImpl>();
		assertEquals( EventBusWithLookup.class,
				(Class<?>)( (ParameterizedType)presenter.getClass().getGenericSuperclass() ).getActualTypeArguments()[1] );

	}

}
