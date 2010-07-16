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

package org.chromattic.metamodel.typegen.attribute;

import org.chromattic.metamodel.mapping.AttributeMapping;
import org.chromattic.metamodel.mapping.BeanMapping;
import org.chromattic.metamodel.mapping.NodeAttributeType;
import org.chromattic.metamodel.typegen.AbstractMappingTestCase;

import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class MappingTestCase extends AbstractMappingTestCase {

  public void testA1() throws Exception { testA1(A1.class); }
  public void testA2() throws Exception { testA2(A2.class); }
  public void testA3() throws Exception { testA3(A3.class); }

  protected void testA1(Class<?> clazz) throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(clazz);
    BeanMapping mapping = mappings.get(clazz);
    AttributeMapping stringMapping = mapping.getPropertyMapping("path", AttributeMapping.class);
    assertEquals("path", stringMapping.getName());
    assertEquals(NodeAttributeType.PATH, stringMapping.getType());
  }

  protected void testA2(Class<?> clazz) throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(clazz);
    BeanMapping mapping = mappings.get(clazz);
    AttributeMapping stringMapping = mapping.getPropertyMapping("name", AttributeMapping.class);
    assertEquals("name", stringMapping.getName());
    assertEquals(NodeAttributeType.NAME, stringMapping.getType());
  }

  protected void testA3(Class<?> clazz) throws Exception {
    Map<Class<?>, BeanMapping> mappings = assertValid(clazz);
    BeanMapping mapping = mappings.get(clazz);
    AttributeMapping stringMapping = mapping.getPropertyMapping("workspaceName", AttributeMapping.class);
    assertEquals("workspaceName", stringMapping.getName());
    assertEquals(NodeAttributeType.WORKSPACE_NAME, stringMapping.getType());
  }
}