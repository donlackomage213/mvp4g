/*
 * Copyright (c) 2009 - 2017 - Pierre-Laurent Coirer, Frank Hossfeld
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.mvp4g.client.event;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.mvp4g.client.Mvp4gException;
import com.mvp4g.client.history.DefaultHistoryProxy;
import com.mvp4g.client.history.HistoryProxyProvider;
import com.mvp4g.client.history.NavigationConfirmationInterface;
import com.mvp4g.client.history.NavigationEventCommand;
import com.mvp4g.client.test_tools.EventFilterStub;
import com.mvp4g.client.test_tools.Mvp4gModuleStub;
import com.mvp4g.rebind.test_tools.annotation.Presenters;
import com.mvp4g.rebind.test_tools.annotation.handlers.SimpleEventHandler01;
import com.mvp4g.rebind.test_tools.annotation.presenters.SimplePresenter01;
import com.mvp4g.rebind.test_tools.annotation.views.SimpleInjectedView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BaseEventBusTest {

  private BaseEventBus bus = null;
  private Mvp4gModuleStub module;
  private EventFilterStub filter;

  @Before
  public void setUp() {
    bus = new BaseEventBus() {

      @SuppressWarnings("unchecked")
      @Override
      protected <T extends EventHandlerInterface<?>> T createHandler(Class<T> handlerClass) {
        return (T) ((SimplePresenter01.class.equals(handlerClass)) ?
                    new SimplePresenter01() :
                    null);
      }

      public void setApplicationHistoryStored(boolean historyStored) {

      }

      public void setNavigationConfirmation(NavigationConfirmationInterface navigationConfirmation) {

      }

      public void confirmNavigation(NavigationEventCommand event) {

      }

    };
    module = new Mvp4gModuleStub(bus);
    filter = new EventFilterStub();
  }

  @Test
  public void testSetAndGetHistoryStored() {
    assertTrue(bus.isHistoryStored());
    bus.setHistoryStored(false);
    assertFalse(bus.isHistoryStored());
    bus.setHistoryStored(true);
    assertTrue(bus.isHistoryStored());
  }

  @Test
  public void testSetAndGetHistoryStoredForNextOne() {
    bus.setHistoryStored(false);
    bus.setHistoryStoredForNextOne(true);
    assertTrue(bus.isHistoryStored());
    bus.place(module,
              "",
              "",
              false);
    assertFalse(bus.isHistoryStored());

    bus.setHistoryStored(true);
    bus.setHistoryStoredForNextOne(false);
    assertFalse(bus.isHistoryStored());
    bus.place(module,
              "",
              "",
              false);
    assertTrue(bus.isHistoryStored());

    bus.setHistoryStored(true);
    bus.setHistoryStoredForNextOne(true);
    assertTrue(bus.isHistoryStored());
    bus.place(module,
              "",
              "",
              false);
    assertTrue(bus.isHistoryStored());

    bus.setHistoryStored(false);
    bus.setHistoryStoredForNextOne(false);
    assertFalse(bus.isHistoryStored());
    bus.place(module,
              "",
              "",
              false);
    assertFalse(bus.isHistoryStored());

  }

  @Test
  public void testClearHistory() {
    String test = "test";
    String form = "form";
    module.place(test,
                 form,
                 false);
    assertEquals(module.getEventType(),
                 test);
    assertEquals(module.getForm(),
                 form);

    bus.clearHistory(module);
    assertNull(module.getEventType());
    assertNull(module.getForm());

    module.place(test,
                 form,
                 false);
    assertEquals(module.getEventType(),
                 test);
    assertEquals(module.getForm(),
                 form);

    bus.setHistoryStored(false);
    bus.clearHistory(module);
    assertEquals(module.getEventType(),
                 test);
    assertEquals(module.getForm(),
                 form);

    bus.setHistoryStored(true);
    bus.clearHistory(module);
    assertNull(module.getEventType());
    assertNull(module.getForm());
  }

  @Test
  public void testPlace() {

    String eventType = "eventType";
    String form      = "form";

    bus.setHistoryStored(false);
    String token = bus.place(module,
                             eventType,
                             form,
                             false);

    assertNull(module.getEventType());
    assertNull(module.getForm());
    assertNull(token);

    bus.setHistoryStored(true);
    token = bus.place(module,
                      eventType,
                      form,
                      false);

    assertEquals(module.getEventType(),
                 eventType);
    assertEquals(module.getForm(),
                 form);
    assertEquals(module.TOKEN,
                 token);

    bus.setHistoryStored(false);
    bus.tokenMode = true;
    token = bus.place(module,
                      eventType,
                      form,
                      false);
    assertEquals(module.getEventType(),
                 eventType);
    assertEquals(module.getForm(),
                 form);
    assertEquals(module.TOKEN,
                 token);
    assertFalse(bus.isHistoryStored());
    assertFalse(bus.tokenMode);

    bus.setHistoryStored(true);
    bus.setHistoryStoredForNextOne(false);
    bus.tokenMode = true;
    token = bus.place(module,
                      eventType,
                      form,
                      false);
    assertFalse(bus.isHistoryStored());

  }

  @Test
  public void testSetterGetterFilterEnabled() {
    assertTrue(bus.isFilteringEnabled());
    bus.setFilteringEnabled(false);
    assertFalse(bus.isFilteringEnabled());
    bus.setFilteringEnabled(true);
    assertTrue(bus.isFilteringEnabled());
  }

  @Test
  public void testFilter() {
    assertTrue(bus.filterEvent("test"));
    filter.setFilter(false);
    bus.addEventFilter(filter);
    assertFalse(bus.filterEvent("test"));
    bus.removeEventFilter(filter);
    assertTrue(bus.filterEvent("test"));
  }

  @Test
  public void testMultipleFilters() {
    assertTrue(bus.filterEvent("test"));
    EventFilterStub filter1 = new EventFilterStub();
    EventFilterStub filter2 = new EventFilterStub();
    bus.addEventFilter(filter1);
    bus.addEventFilter(filter2);

    filter1.setFilter(true);
    filter2.setFilter(true);
    assertTrue(bus.filterEvent("test"));

    filter1.setFilter(true);
    filter2.setFilter(false);
    assertFalse(bus.filterEvent("test"));

    filter1.setFilter(false);
    filter2.setFilter(true);
    assertFalse(bus.filterEvent("test"));

    filter1.setFilter(false);
    filter2.setFilter(false);
    assertFalse(bus.filterEvent("test"));
  }

  @Test
  public void testSetFilterEnabledForNextOne() {
    assertTrue(bus.filterEvent("test"));
    filter.setFilter(false);
    bus.addEventFilter(filter);
    assertFalse(bus.filterEvent("test"));

    bus.setFilteringEnabledForNextOne(false);
    assertTrue(bus.filterEvent("test"));
    assertFalse(bus.filterEvent("test"));

    bus.setFilteringEnabled(false);
    bus.setFilteringEnabledForNextOne(false);
    assertTrue(bus.filterEvent("test"));
    assertTrue(bus.filterEvent("test"));

    bus.setFilteringEnabled(false);
    bus.setFilteringEnabledForNextOne(true);
    assertFalse(bus.filterEvent("test"));
    assertTrue(bus.filterEvent("test"));
  }

  @Test
  public void testAddRemoveHandler() {
    List<SimplePresenter01> list = bus.getHandlers(SimplePresenter01.class);
    assertNull(list);

    SimplePresenter01 p = bus.addHandler(SimplePresenter01.class);
    list = bus.getHandlers(SimplePresenter01.class);
    assertTrue(list.size() == 1);
    assertEquals(list.get(0),
                 p);

    bus.removeHandler(p);
    list = bus.getHandlers(SimplePresenter01.class);
    assertTrue(list.size() == 0);

    List<SimplePresenter01> list2 = bus.getHandlers(SimplePresenter01.class);
    assertNotSame(list,
                  list2);
    assertEquals(list,
                 list2);
  }

  @Test
  public void testDefaultAddHandler() {
    List<SimplePresenter01> list = bus.getHandlers(SimplePresenter01.class);

    SimplePresenter01 p = bus.addHandler(SimplePresenter01.class);
    list = bus.getHandlers(SimplePresenter01.class);
    assertTrue(list.size() == 1);
    assertEquals(list.get(0),
                 p);
    assertTrue(p.isBindCalled());

    p = bus.addHandler(SimplePresenter01.class,
                       true);
    list = bus.getHandlers(SimplePresenter01.class);
    assertTrue(list.size() == 2);
    assertEquals(list.get(1),
                 p);
    assertTrue(p.isBindCalled());

    p = bus.addHandler(SimplePresenter01.class,
                       false);
    list = bus.getHandlers(SimplePresenter01.class);
    assertTrue(list.size() == 3);
    assertEquals(list.get(2),
                 p);
    assertFalse(p.isBindCalled());

  }

  @Test
  public void testAddUnknownHandler() {
    try {
      bus.addHandler(Presenters.PresenterNoParameter.class);
      fail();
    } catch (Mvp4gException e) {
      assertEquals("Handler with type " +
                   Presenters.PresenterNoParameter.class.getName() +
                   " couldn't be created by the Mvp4g. Have you forgotten to set multiple attribute to true for this handler or are you trying to create an handler that belongs to another module (another type of event bus injected in this handler) or have you set a splitter for this handler?",
                   e.getMessage());
    }
  }

  @Test
  public void testHistoryProxy() {
    // TODO update test
    HistoryProxyProvider.INSTANCE.set(new DefaultHistoryProxy());
    assertSame(HistoryProxyProvider.INSTANCE.get(),
               bus.getHistory());
  }

  @Test
  public void testSetPresenter() {
    SimplePresenter01  presenter = new SimplePresenter01();
    SimpleInjectedView view      = new SimpleInjectedView();
    SimplePresenter01 presenter2 = BaseEventBus.setPresenter(false,
                                                             presenter,
                                                             view,
                                                             bus);
    assertSame(presenter,
               presenter2);
    assertSame(presenter.getEventBus(),
               bus);
    assertSame(presenter.getView(),
               view);

    presenter = new SimplePresenter01();
    view = new SimpleInjectedView();
    presenter2 = BaseEventBus.setPresenter(true,
                                           presenter,
                                           view,
                                           bus);
    assertSame(presenter,
               presenter2);
    assertSame(presenter.getEventBus(),
               bus);
    assertSame(presenter.getView(),
               view);
    assertSame(presenter,
               view.getPresenter());
  }

  @Test
  public void testSetEventHandler() {
    SimpleEventHandler01 eventHandler = new SimpleEventHandler01();
    SimpleEventHandler01 eventHandler2 = BaseEventBus.setEventHandler(eventHandler,
                                                                      bus);
    assertSame(eventHandler,
               eventHandler2);
    assertSame(eventHandler.getEventBus(),
               bus);
  }
}
