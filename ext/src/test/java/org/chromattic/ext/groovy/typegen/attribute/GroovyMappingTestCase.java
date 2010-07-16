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

package org.chromattic.ext.groovy.typegen.attribute;

import groovy.lang.GroovyShell;
import org.chromattic.metamodel.typegen.attribute.MappingTestCase;

import java.util.Map;

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

  public void testA3() throws Exception {
    testA3((Class<?>) new GroovyShell(new A3ClassLoader()).evaluate("A3.class"));
  }
  
}