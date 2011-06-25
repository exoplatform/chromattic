/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
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
package org.chromattic.test.core;

import org.chromattic.api.Status;
import org.chromattic.core.DomainSession;
import org.chromattic.core.EntityContext;
import org.chromattic.core.ObjectContext;
import org.chromattic.core.api.ChromatticSessionImpl;
import org.chromattic.test.AbstractTestCase;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 */
public class CreateTestCase extends AbstractTestCase {

  @Override
  protected void createDomain() {
    addClass(A.class);
  }

  public void testCreate() {
    DomainSession session = domainLogin();
    ObjectContext ctx = session.create(A.class, null);
    assertEquals(EntityContext.class, ctx.getClass());
    assertEquals(Status.TRANSIENT, ctx.getStatus());
    assertEquals("foo", ctx.getMapper().getNodeTypeName());
    assertTrue(ctx.getObject() instanceof A);
    assertNull(ctx.getTypeInfo());
    assertSame(session, ctx.getSession());
  }

  public void testCreateNamed() {
    DomainSession session = domainLogin();
    ObjectContext ctx = session.create(A.class, "a");
    assertEquals(EntityContext.class, ctx.getClass());
    assertEquals(Status.TRANSIENT, ctx.getStatus());
    assertEquals("foo", ctx.getMapper().getNodeTypeName());
    assertNull(ctx.getTypeInfo());
    assertSame(session, ctx.getSession());
  }

  public void testCreateThrowsNPE() {
    DomainSession session = domainLogin();
    try {
      session.create(null, "a");
      fail();
    } catch (NullPointerException ignore) {
    }
  }

  private DomainSession domainLogin() {
    ChromatticSessionImpl session = login();
    return session.getDomainSession();
  }
}
