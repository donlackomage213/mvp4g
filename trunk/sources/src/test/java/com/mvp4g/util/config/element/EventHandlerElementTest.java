package com.mvp4g.util.config.element;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mvp4g.util.exception.element.DuplicatePropertyNameException;

public class EventHandlerElementTest extends Mvp4gWithServicesElementTest {

	private static final String[] properties = SimpleMvp4gElementTest.addProperties( new String[] { "multiple" } );

	@Override
	protected String[] getProperties() {
		return properties;
	}

	@Override
	protected String getTag() {
		return "eventHandler";
	}

	@Override
	protected SimpleMvp4gElement newElement() {
		return new EventHandlerElement();
	}

	@Test
	public void testMultiple() throws DuplicatePropertyNameException {
		EventHandlerElement element = new EventHandlerElement();
		assertFalse( element.isMultiple() );
		element.setMultiple( "true" );
		assertTrue( element.isMultiple() );

		element = new EventHandlerElement();
		element.setMultiple( "false" );
		assertFalse( element.isMultiple() );
	}

}