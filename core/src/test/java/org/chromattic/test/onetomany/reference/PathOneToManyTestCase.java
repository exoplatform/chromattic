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

package org.chromattic.test.onetomany.reference;

import org.chromattic.testgenerator.GroovyTestGeneration;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFactory;
import javax.jcr.Value;
import javax.jcr.PropertyType;
import java.util.Collection;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
//@GroovyTestGeneration(chromatticClasses = {A3.class, B3.class})
public class PathOneToManyTestCase extends AbstractOneToManyTestCase<A3, B3> {

  protected Class<A3> getOneSideClass() {
    return A3.class;
  }

  protected Class<B3> getManySideClass() {
    return B3.class;
  }

  protected void createLink(Node referent, String propertyName, Node referenced) throws RepositoryException {
    if (referenced != null) {
      String path = referenced.getPath();
      ValueFactory valueFactory = referent.getSession().getValueFactory();
      Value value = valueFactory.createValue(path, PropertyType.PATH);
      referent.setProperty(propertyName, value);
    } else {
      referent.setProperty(propertyName, (String)null);
    }
  }

  protected Collection<B3> getMany(A3 one) {
    return one.getBs();
  }

  protected A3 getOne(B3 many) {
    return many.getA();
  }

  protected void setOne(B3 many, A3 one) {
    many.setA(one);
  }
}