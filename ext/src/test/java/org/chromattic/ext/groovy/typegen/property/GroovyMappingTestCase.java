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

package org.chromattic.ext.groovy.typegen.property;

import groovy.lang.GroovyShell;
import org.chromattic.metamodel.typegen.property.MappingTestCase;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class GroovyMappingTestCase extends MappingTestCase {
  
  public void testA1() throws Exception {
    testA1((Class<?>) new GroovyShell(new A1ClassLoader()).evaluate("A1.class"));
  }

  public void testA2() throws Exception {
    testA2((Class<?>) new GroovyShell(new A2ClassLoader()).evaluate("A2.class"));
  }

  public void testB1() throws Exception {
    testB1((Class<?>) new GroovyShell(new B1ClassLoader()).evaluate("B1.class"));
  }

  public void testB2() throws Exception {
    testB2((Class<?>) new GroovyShell(new B2ClassLoader()).evaluate("B2.class"));
  }

  public void testC1() throws Exception {
    testC1((Class<?>) new GroovyShell(new C1ClassLoader()).evaluate("C1.class"));
  }

  public void testC2() throws Exception {
    testC2((Class<?>) new GroovyShell(new C2ClassLoader()).evaluate("C2.class"));
  }

  public void testD1() throws Exception {
    testD1((Class<?>) new GroovyShell(new D1ClassLoader()).evaluate("D1.class"));
  }

  public void testD2() throws Exception {
    testD2((Class<?>) new GroovyShell(new D2ClassLoader()).evaluate("D2.class"));
  }

  public void testE() throws Exception {
    testE((Class<?>) new GroovyShell(new EClassLoader()).evaluate("E.class"));
  }

}
