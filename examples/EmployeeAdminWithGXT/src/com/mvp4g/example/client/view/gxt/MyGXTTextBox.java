package com.mvp4g.example.client.view.gxt;

import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.mvp4g.example.client.presenter.view_interface.widget_interface.MyTextBoxInterface;

public class MyGXTTextBox extends TextField<String> implements MyTextBoxInterface {

	public MyGXTTextBox( String fieldLabel ) {
		super();
		setFieldLabel( fieldLabel );
	}

	public void setValue( String value, boolean fireEvents ) {
		setValue( value );
	}

	public HandlerRegistration addValueChangeHandler( ValueChangeHandler<String> handler ) {
		return addHandler( handler, ValueChangeEvent.getType() );
	}

	public HandlerRegistration addKeyUpHandler( KeyUpHandler handler ) {
		return addHandler( handler, KeyUpEvent.getType() );
	}

}