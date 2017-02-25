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

package com.mvp4g.rebind.config.element;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class Mvp4gWithServicesElementTest
  extends SimpleMvp4gElementTest {

  @Test
  public void testSetServices() {
    Mvp4gWithServicesElement e        = (Mvp4gWithServicesElement) element;
    List<InjectedElement>    services = e.getInjectedServices();
    assertEquals(0,
                 services.size());
    e.setValues("services",
                new String[] { "service" });
    assertEquals(1,
                 services.size());
    assertEquals("service",
                 services.get(0)
                         .getElementName());
    assertEquals("setService",
                 services.get(0)
                         .getSetterName());
  }

  @Test
  public void testSetValues() {
    Mvp4gWithServicesElement e        = (Mvp4gWithServicesElement) element;
    List<InjectedElement>    services = e.getInjectedServices();
    String[]                 tab      = new String[] { "service" };
    assertEquals(0,
                 services.size());
    e.setValues("test",
                tab);
    assertEquals(0,
                 services.size());
    assertArrayEquals(tab,
                      e.getValues("test"));
  }

  @Override
  protected SimpleMvp4gElement newElement() {
    return new Mvp4gWithServicesElement();
  }

  @Override
  protected String getTag() {
    return "withServices";
  }

}
