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

package org.chromattic.metamodel.typegen;

import junit.framework.TestCase;
import org.chromattic.metamodel.mapping.InvalidMappingException;
import org.reflext.api.ClassTypeInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class AbstractSchemaTestCase extends TestCase {
  
  protected final Map<Class<?>, NodeType> assertValid(Class<?>... classTypes) {
    TypeGen gen = new TypeGen();
    ClassTypeInfo[] ctis = new ClassTypeInfo[classTypes.length];
    for (int i = 0;i < classTypes.length;i++) {
      ctis[i] = gen.addType(classTypes[i]);
    }
    gen.generate();
    Map<Class<?>, NodeType> types = new HashMap<Class<?>, NodeType>();
    for (int i = 0; i < ctis.length; i++) {
      types.put(classTypes[i], gen.getNodeType(ctis[i]));
    }
    return types;
  }

  protected final void assertInvalid(Class<?> classType) {
    TypeGen gen = new TypeGen();
    gen.addType(classType);
    try {
      gen.generate();
      fail();
    }
    catch (InvalidMappingException ignore) {
    }
  }

}
