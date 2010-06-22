/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.chromattic.metamodel.bean;

import org.reflext.api.ClassTypeInfo;

import java.util.Collections;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ReadWritePropertyTestCase extends AbstractBeanTestCase {

  public class ConsistentGetterAndSetter {
    public String getA() { throw new UnsupportedOperationException(); }
    public void setA(String a) { throw new UnsupportedOperationException(); }
  }

  public void testConsistentGetterAndSetter() {
    ClassTypeInfo typeInfo = (ClassTypeInfo)domain.resolve(ConsistentGetterAndSetter.class);
    BeanInfo beanInfo = beanInfo(typeInfo);
    assertEquals(Collections.singleton("a"), beanInfo.getProperties().keySet());
    assertProperty(beanInfo.getProperty("a"), "a", String.class, AccessMode.READ_WRITE);
  }

  public class GetterAndSetterWithDifferentTypes {
    public String getA() { throw new UnsupportedOperationException(); }
    public void setA(Exception a) { throw new UnsupportedOperationException(); }
  }

  public void testGetterAndSetterWithDifferentTypes() {
    ClassTypeInfo typeInfo = (ClassTypeInfo)domain.resolve(GetterAndSetterWithDifferentTypes.class);
    BeanInfo beanInfo = beanInfo(typeInfo);
    assertEquals(Collections.singleton("a"), beanInfo.getProperties().keySet());
    assertProperty(beanInfo.getProperty("a"), "a", String.class, AccessMode.READ_ONLY);
  }
}
