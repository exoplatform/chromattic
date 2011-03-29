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
@GroovyTestGeneration(chromatticClasses = {A1.class, B1.class})
public class PathOneToTestCase extends AbstractOneToTestCase<A1, B1> {

  protected Class<A1> getOneSideClass() {
    return A1.class;
  }

  protected Class<B1> getManySideClass() {
    return B1.class;
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

  protected Collection<B1> getMany(A1 one) {
    return one.getBs();
  }
}