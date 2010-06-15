/*
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.chromattic.metamodel.bean2;

import junit.framework.TestCase;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class BeanTestCase extends TestCase {

  protected abstract Collection<PropertyMetaData> buildMetaData(Class<?> beanClass) throws Exception;

  protected final void assertProperties(Class<?> beanClass, PropertyMetaData... descriptors) throws Exception {
    Map<String, PropertyMetaData> actual = new HashMap<String, PropertyMetaData>();
    for (PropertyMetaData propertyMD : buildMetaData(beanClass)) {
      actual.put(propertyMD.name, propertyMD);
    }
    Map<String, PropertyMetaData> expected = new HashMap<String, PropertyMetaData>();
    for (PropertyMetaData propertyMD : descriptors) {
      expected.put(propertyMD.name, propertyMD);
    }
    assertEquals(expected, actual);
  }

  public void testReadOnlyProperty() throws Exception {
    class A {
      public String getA() { return null; }
    }
    assertProperties(A.class, new PropertyMetaData("a", String.class, AccessMode.READ_ONLY));
  }

  public void testWriteOnlyProperty() throws Exception {
    class A {
      public void setA(String a) {}
    }
    assertProperties(A.class, new PropertyMetaData("a", String.class, AccessMode.WRITE_ONLY));
  }

  public void testReadWriteProperty() throws Exception {
    class A {
      public String getA() { return null; }
      public void setA(String a) {}
    }
    assertProperties(A.class, new PropertyMetaData("a", String.class, AccessMode.READ_WRITE));
  }

  /*
   * The getter is inherited.
   */
  public void testGetterInheritance() throws Exception {
    class A {
      public String getA() { return null; }
    }
    class B extends A {
    }
    assertProperties(B.class, new PropertyMetaData("a", String.class, AccessMode.READ_ONLY));
  }

  /*
   * The setter is inherited.
   */
  public void testSetterInheritance() throws Exception {
    class A {
      public void setA(String a) {}
    }
    class B extends A {
    }
    assertProperties(B.class, new PropertyMetaData("a", String.class, AccessMode.WRITE_ONLY));
  }

  /*
   * The getter and setter are inherited.
   */
  public void testGetterSetterInheritance() throws Exception {
    class A {
      public String getA() { return null; }
      public void setA(String a) {}
    }
    class B extends A {
    }
    assertProperties(B.class, new PropertyMetaData("a", String.class, AccessMode.READ_WRITE));
  }

  /*
   * The getter is overriden.
   */
  public void testGetterOverride() throws Exception {
    class A {
      public String getA() { return null; }
    }
    class B extends A {
      public String getA() { return null; }
    }
    assertProperties(B.class, new PropertyMetaData("a", String.class, AccessMode.READ_ONLY));
  }

  /*
   * The setter is overriden.
   */
  public void testSetterOverride() throws Exception {
    class A {
      public void setA(String a) {}
    }
    class B extends A {
      public void setA(String a) {}
    }
    assertProperties(B.class, new PropertyMetaData("a", String.class, AccessMode.WRITE_ONLY));
  }

  /*
   * The getter and setter are overriden.
   */
  public void testGetterSetterOverride() throws Exception {
    class A {
      public String getA() { return null; }
      public void setA(String a) {}
    }
    class B extends A {
      public String getA() { return null; }
      public void setA(String a) {}
    }
    assertProperties(B.class, new PropertyMetaData("a", String.class, AccessMode.READ_WRITE));
  }

  /*
   * Last declared wins part 1.
   */
  public void testSetterOverloading1() throws Exception {
    class A {
      public void setA(String a) {}
      public void setA(Integer a) {}
    }
    assertProperties(A.class, new PropertyMetaData("a", Integer.class, AccessMode.WRITE_ONLY));
  }

  /*
   * Last declared wins part 2.
   */
  public void testSetterOverloading2() throws Exception {
    class A {
      public void setA(Integer a) {}
      public void setA(String a) {}
    }
    assertProperties(A.class, new PropertyMetaData("a", String.class, AccessMode.WRITE_ONLY));
  }

  /*
   * The property becomes read/write as it inherits the getter from its super class.
   */
  public void testReadWriteSubclass1() throws Exception {
    class A {
      public String getA() { return null; }
    }
    class B extends A {
      public void setA(String a) {}
    }
    assertProperties(B.class, new PropertyMetaData("a", String.class, AccessMode.READ_WRITE));
  }

  /*
   * The property becomes read/write as it inherits the getter from its super class.
   */
  public void testReadWriteSubclass2() throws Exception {
    class A {
      public void setA(String a) {}
    }
    class B extends A {
      public String getA() { return null; }
    }
    assertProperties(B.class, new PropertyMetaData("a", String.class, AccessMode.READ_WRITE));
  }

  /*
   * The getter primes over any setter with the same name, which makes sense as setter signature can be
    * overloaded.
   */
  public void testGetterAndSetterWithDifferentType() throws Exception {
    class A {
      public String getA() { return null; }
      public void setA(Integer a) {}
    }
    assertProperties(A.class, new PropertyMetaData("a", String.class, AccessMode.READ_ONLY));
  }
}
