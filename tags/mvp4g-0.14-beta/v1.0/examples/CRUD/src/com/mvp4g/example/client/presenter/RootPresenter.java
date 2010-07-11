package com.mvp4g.example.client.presenter;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.mvp4g.client.presenter.Presenter;
import com.mvp4g.example.client.presenter.view_interface.RootViewInterface;


public class RootPresenter extends Presenter<RootViewInterface>{
	
	public void onChangeBody(Widget newPage){
		Panel body = view.getBody();
		body.clear();
		body.add(newPage);		
	}
	
	public void onDisplayMessage(String message){
		view.getMessage().setValue(message);
	}
	
	public void onStart(){
		view.getMessage().setValue("");
	}

}