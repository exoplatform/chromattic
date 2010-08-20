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
public class PropertyInterfaceInheritanceTestCase extends AbstractPropertyInheritanceTestCase<AImpl> {

  protected Class<AImpl> getType() {
    return AImpl.class;
  }

  protected String getString1(AImpl b) {
    return b.getString1();
  }

  protected void setString1(AImpl b, String s) {
    b.setString1(s);
  }

  protected void setString2(AImpl b, String s) {
    b.setString2(s);
  }

  protected String getString2(AImpl b) {
    return b.getString2();
  }

  @Override
  protected Object getStrings1(AImpl b) {
    return b.getStrings1();
  }

  @Override
  protected void setStrings1(AImpl b, Object s) {
    b.setStrings1((String[])s);
  }

  @Override
  protected Object getStrings2(AImpl b) {
    return b.getStrings2();
  }

  @Override
  protected void setStrings2(AImpl b, Object s) {
    b.setStrings2((String[])s);
  }
}