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

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ReferenceToManyTestCase extends AbstractToManyTestCase<TOTMR_A_2, TOTMR_B_2> {

  protected Class<TOTMR_A_2> getOneSideClass() {
    return TOTMR_A_2.class;
  }

  protected Class<TOTMR_B_2> getManySideClass() {
    return TOTMR_B_2.class;
  }

  protected TOTMR_A_2 getOne(TOTMR_B_2 many) {
    return many.getA();
  }

  protected void setOne(TOTMR_B_2 many, TOTMR_A_2 one) {
    many.setA(one);
  }

  protected String getOneNodeType() {
    return "totmr_a";
  }

  protected String getManyNodeType() {
    return "totmr_b";
  }

  protected void createLink(Node referent, String propertyName, Node referenced) throws RepositoryException {
    referent.setProperty(propertyName, referenced);
  }
}
