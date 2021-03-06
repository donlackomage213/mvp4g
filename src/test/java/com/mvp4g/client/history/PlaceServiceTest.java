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

package com.mvp4g.client.history;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.mvp4g.client.Mvp4gEventPasser;
import com.mvp4g.client.test_tools.EventBusWithLookUpStub;
import com.mvp4g.client.test_tools.HistoryProxyStub;
import com.mvp4g.client.test_tools.Mvp4gModuleStub;
import com.mvp4g.client.test_tools.ValueChangeEventStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * TODO: find a way to test this class without using GWT History.
 *
 * @author PL
 */
public class PlaceServiceTest {

  PlaceService           placeServiceDefault = null;
  EventBusWithLookUpStub eventBus            = null;
  HistoryProxyStub history;
  HistoryProxyStub historySeparator;
  HistoryProxyStub historySeparatorAdd;
  Mvp4gModuleStub module = null;

  @Before
  public void setUp() {
    history = new HistoryProxyStub();
    historySeparator = new HistoryProxyStub();
    historySeparatorAdd = new HistoryProxyStub();

    eventBus = new EventBusWithLookUpStub();
    module = new Mvp4gModuleStub(eventBus);
    placeServiceDefault = new PlaceService(history);
    placeServiceDefault.setModule(module);
  }

  @Test
  public void testConstructor() {
    assertEquals(history.getHandler(),
                 placeServiceDefault);
  }

  @Test
  public void testPlaceNoParam() {
    String historyName = "historyName";
    placeServiceDefault.addConverter(historyName,
                                     buildHistoryConverter(false));
    String token = placeServiceDefault.place(historyName,
                                             null,
                                             false);
    assertEquals(historyName,
                 history.getToken());
    assertFalse(history.isIssueEvent());
    assertEquals(historyName,
                 token);
  }

  private HistoryConverter<EventBusWithLookUpStub> buildHistoryConverter(final boolean crawlable) {
    return new HistoryConverter<EventBusWithLookUpStub>() {

      public void convertFromToken(String eventType,
                                   String form,
                                   EventBusWithLookUpStub eventBus) {
        eventBus.dispatch(eventType,
                          form);
      }

      public boolean isCrawlable() {
        return crawlable;
      }

    };
  }

  @Test
  public void testPlaceWithParam() {
    String form        = "form";
    String historyName = "historyName";
    placeServiceDefault.addConverter(historyName,
                                     buildHistoryConverter(false));
    String token = placeServiceDefault.place(historyName,
                                             form,
                                             false);
    assertEquals(historyName + "?" + form,
                 history.getToken());
    assertFalse(history.isIssueEvent());
    assertEquals(historyName + "?" + form,
                 token);
  }

  @Test
  public void testPlaceTokenOnly() {
    String form        = "form";
    String historyName = "historyName";
    placeServiceDefault.addConverter(historyName,
                                     buildHistoryConverter(false));
    String token = placeServiceDefault.place(historyName,
                                             form,
                                             true);
    assertEquals(historyName + "?" + form,
                 token);
    assertNull(history.getToken());
  }

  @Test
  public void testEmptyToken() {
    ValueChangeEvent<String> event = new ValueChangeEventStub<String>("");
    placeServiceDefault.onValueChange(event);
    assertTrue(module.isHistoryInit());
  }

  @Test
  public void testWrongToken() {
    ValueChangeEvent<String> event = new ValueChangeEventStub<String>("wrongEventType");
    placeServiceDefault.onValueChange(event);
    assertTrue(module.isHistoryNotFound());
  }

  @Test
  public void testConverterNoParameter() {
    String                   historyName = "historyName";
    ValueChangeEvent<String> event       = new ValueChangeEventStub<String>(historyName);

    placeServiceDefault.addConverter(historyName,
                                     buildHistoryConverter(false));
    placeServiceDefault.onValueChange(event);
    eventBus.assertEvent(historyName,
                         new Object[] { null });
  }

  @Test
  public void testConverterWithParameters() {
    String form        = "form";
    String historyName = "historyName";
    placeServiceDefault.addConverter(historyName,
                                     buildHistoryConverter(false));
    ValueChangeEvent<String> event = new ValueChangeEventStub<String>(historyName + "?" + form);
    placeServiceDefault.onValueChange(event);
    eventBus.assertEvent(historyName,
                         new Object[] { form });
  }

  @Test
  public void testConverterForChildModuleNoParameter() {
    String                   historyName = "child/historyName";
    EventBusWithLookUpStub   childBus    = new EventBusWithLookUpStub();
    Mvp4gModuleStub          childModule = new Mvp4gModuleStub(childBus);
    ValueChangeEvent<String> event       = new ValueChangeEventStub<String>(historyName);
    placeServiceDefault.addConverter(historyName,
                                     buildHistoryConverter(false));
    placeServiceDefault.onValueChange(event);

    assertEquals(historyName,
                 module.getEventType());
    Mvp4gEventPasser passer = module.getPasser();
    passer.setEventObject(false);
    passer.pass(childModule);
    assertTrue(module.isHistoryNotFound());

    passer.setEventObject(true);
    passer.pass(childModule);
    childBus.assertEvent("historyName",
                         new Object[] { null });

  }

  @Test
  public void testConverterForChildModuleNoConverter() {
    String                   historyName = "child/historyName";
    EventBusWithLookUpStub   childBus    = new EventBusWithLookUpStub();
    Mvp4gModuleStub          childModule = new Mvp4gModuleStub(childBus);
    ValueChangeEvent<String> event       = new ValueChangeEventStub<String>(historyName);

    placeServiceDefault.onValueChange(event);
    assertEquals(historyName,
                 module.getEventType());
    Mvp4gEventPasser passer = module.getPasser();
    passer.setEventObject(true);
    passer.pass(childModule);
    assertTrue(module.isHistoryNotFound());

  }

  @Test
  public void testConverterForChildModuleWithParameter() {
    String                 historyName = "child/eventType";
    String                 form        = "form";
    EventBusWithLookUpStub childBus    = new EventBusWithLookUpStub();
    Mvp4gModuleStub        childModule = new Mvp4gModuleStub(childBus);
    placeServiceDefault.addConverter(historyName,
                                     buildHistoryConverter(false));
    ValueChangeEvent<String> event = new ValueChangeEventStub<String>(historyName + "?" + form);
    placeServiceDefault.onValueChange(event);

    assertEquals(historyName,
                 module.getEventType());
    Mvp4gEventPasser passer = module.getPasser();
    passer.setEventObject(true);
    passer.pass(childModule);
    childBus.assertEvent("eventType",
                         new Object[] { form });

  }

  @Test
  public void testClearHistory() {
    String historyName = "eventType";
    placeServiceDefault.addConverter(historyName,
                                     new ClearHistory());
    placeServiceDefault.place(historyName,
                              null,
                              false);
    assertEquals(historyName,
                 history.getToken());

    placeServiceDefault.clearHistory();
    assertEquals("",
                 history.getToken());
  }

  @Test(expected = RuntimeException.class)
  public void testClearHistoryConvertFromToken() {
    ClearHistory clearHistory = new ClearHistory();
    clearHistory.convertFromToken(null,
                                  null,
                                  null);
  }

  @Test
  public void testPlaceCrawlable() {
    String historyName = "historyName";
    placeServiceDefault.addConverter(historyName,
                                     buildHistoryConverter(true));
    placeServiceDefault.place(historyName,
                              null,
                              false);
    assertEquals("!" + historyName,
                 history.getToken());
    assertFalse(history.isIssueEvent());
  }

  @Test
  public void testConverterCrawlable() {
    String historyName = "historyName";
    placeServiceDefault.addConverter(historyName,
                                     buildHistoryConverter(true));
    ValueChangeEvent<String> event = new ValueChangeEventStub<String>("!" + historyName);
    placeServiceDefault.onValueChange(event);
    eventBus.assertEvent(historyName,
                         new Object[] { null });
  }

  @Test
  public void testNavigationConfirmationForHistory() {
    String historyName = "historyName";
    placeServiceDefault.addConverter(historyName,
                                     buildHistoryConverter(true));

    MyTestNavigationConfirmation navConf = new MyTestNavigationConfirmation();
    placeServiceDefault.setNavigationConfirmation(navConf);

    ValueChangeEvent<String> event = new ValueChangeEventStub<String>(historyName);
    placeServiceDefault.onValueChange(event);
    eventBus.assertEvent(null,
                         null);

    navConf.getEvent()
           .execute();
    eventBus.assertEvent(historyName,
                         new Object[] { null });

  }

  @Test
  public void testNoNavigationConfirmationForEvent() {
    final String eventType = "eventType";
    final String form      = "form";

    NavigationEventCommand event = new NavigationEventCommand(eventBus) {

      public void execute() {
        eventBus.dispatch(eventType,
                          form);
      }
    };

    placeServiceDefault.confirmEvent(event);
    eventBus.assertEvent(eventType,
                         new Object[] { form });

  }

  @Test
  public void testNavigationConfirmationForEvent() {
    final String eventType = "eventType";
    final String form      = "form";

    NavigationEventCommand event = new NavigationEventCommand(eventBus) {

      public void execute() {
        eventBus.dispatch(eventType,
                          form);
      }
    };

    MyTestNavigationConfirmation navConf = new MyTestNavigationConfirmation();
    placeServiceDefault.setNavigationConfirmation(navConf);

    placeServiceDefault.confirmEvent(event);

    eventBus.assertEvent(null,
                         null);

    navConf.getEvent()
           .execute();
    eventBus.assertEvent(eventType,
                         new Object[] { form });

  }

  @Test
  public void testEnabled() {
    String form        = "form";
    String historyName = "historyName";
    placeServiceDefault.addConverter(historyName,
                                     buildHistoryConverter(false));

    placeServiceDefault.setEnabled(false);
    String token = placeServiceDefault.place(historyName,
                                             form,
                                             true);
    assertEquals(historyName + "?" + form,
                 token);
    assertNull(history.getToken());

    token = placeServiceDefault.place(historyName,
                                      form,
                                      false);
    assertNull(token);
    assertNull(history.getToken());

    placeServiceDefault.setEnabled(true);
    token = placeServiceDefault.place(historyName,
                                      form,
                                      false);
    assertEquals(historyName + "?" + form,
                 token);
    assertEquals(historyName + "?" + form,
                 history.getToken());
  }

  private class MyTestNavigationConfirmation
    implements NavigationConfirmationInterface {

    private NavigationEventCommand event;

    public void confirm(NavigationEventCommand event) {
      this.event = event;
    }

    public NavigationEventCommand getEvent() {
      return event;
    }

  }

}