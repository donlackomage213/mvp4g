package com.mvp4g.util.config.loader.xml;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.XMLConfiguration;
import org.junit.Test;

import com.mvp4g.util.config.element.HistoryElement;
import com.mvp4g.util.config.loader.xml.HistoryLoader;
import com.mvp4g.util.exception.loader.Mvp4gXmlException;

public class HistoryLoaderTest extends AbstractMvp4gElementLoaderTest<HistoryElement, HistoryLoader> {

	@Test
	public void testEmptyElement() throws Mvp4gXmlException {
		assertEquals( 0, basicLoader.loadElements().size() );
		assertNull( basicLoader.loadElement() );
	}

	@Test
	public void testLoadOk() throws Mvp4gXmlException {
		List<String> attributes = convertToList( basicLoader.getRequiredAttributeNames() );
		attributes.addAll( convertToList( basicLoader.getOptionalAttributeNames() ) );
		List<String> multiValues = convertToList( basicLoader.getMultiValueAttributeNames() );
		multiValues.addAll( convertToList( basicLoader.getOptionalMultiValueAttributeNames() ) );
		List<String> parents = convertToList( basicLoader.getParentAttributeNames() );

		HistoryLoader loader = newLoader( xmlBuilder.getConfigAttribute( attributes, multiValues, parents, 1, false, isSingleNode() ) );
		assertEquals( loader.loadElement(), new ArrayList<HistoryElement>( loader.loadElements() ).get( 0 ) );

	}

	@Override
	protected String getTagName() {
		return "history";
	}

	@Override
	protected boolean isSingleNode() {
		return true;
	}

	@Override
	protected HistoryLoader newLoader( XMLConfiguration xml ) {
		return new HistoryLoader( xml );
	}

}