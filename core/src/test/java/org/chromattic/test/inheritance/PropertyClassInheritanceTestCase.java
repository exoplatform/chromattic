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

package org.chromattic.test.inheritance;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PropertyClassInheritanceTestCase extends AbstractPropertyInheritanceTestCase<BImpl> {

  protected Class<BImpl> getType() {
    return BImpl.class;
  }

  protected String getString1(BImpl b) {
    return b.getString1();
  }

  protected void setString1(BImpl b, String s) {
    b.setString1(s);
  }

  protected void setString2(BImpl b, String s) {
    b.setString2(s);
  }

  protected String getString2(BImpl b) {
    return b.getString2();
  }

  protected String[] getStrings1(BImpl b) {
    return b.getStrings1();
  }

  protected void setStrings1(BImpl b, String... s) {
    b.setStrings1(s);
  }

  protected String[] getStrings2(BImpl b) {
    return b.getStrings2();
  }

  protected void setStrings2(BImpl b, String... s) {
    b.setStrings2(s);
  }
}