package com.mvp4g.example.client;

import com.mvp4g.client.annotation.Event;
import com.mvp4g.client.annotation.Events;
import com.mvp4g.client.annotation.Start;
import com.mvp4g.client.event.EventBus;
import com.mvp4g.example.client.data.MailItem;
import com.mvp4g.example.client.presenter.MailDetailPresenter;
import com.mvp4g.example.client.presenter.MailListPresenter;
import com.mvp4g.example.client.presenter.NavBarPresenter;
import com.mvp4g.example.client.presenter.ShortCutsPresenter;
import com.mvp4g.example.client.presenter.TopPresenter;
import com.mvp4g.example.client.view.MailView;

@Events( startView = MailView.class )
public interface MailEventBus extends EventBus {

	@Start
	@Event( handlers = { TopPresenter.class, NavBarPresenter.class, MailListPresenter.class, MailDetailPresenter.class, ShortCutsPresenter.class } )
	public void start();

	@Event( handlers = MailDetailPresenter.class )
	public void itemSelected( MailItem item );

	@Event( handlers = NavBarPresenter.class )
	public void setNavStatus( int startIndex, int endIndex, int numberOfElements );

	@Event( handlers = MailListPresenter.class )
	public void newer();

	@Event( handlers = MailListPresenter.class )
	public void older();

}
