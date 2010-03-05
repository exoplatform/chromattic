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

package org.chromattic.common.xml;

import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class Emitter {

  /** . */
  private static final int INITIAL = 0;

  /** . */
  private static final int OPEN = 1;

  /** . */
  private static final int CLOSED = 2;

  /** . */
  private int status;

  /** . */
  private Emitter currentChild;

  /** . */
  protected final Handler handler;

  public Emitter(Handler handler) {
    this.status = INITIAL;
    this.currentChild = null;
    this.handler = handler;
  }

  protected final void checkInitial() {
    if (status != INITIAL) {
      throw new IllegalStateException();
    }
  }

  protected void emmitBeginning() throws SAXException {
    //
  }

  protected void emmitEnd() throws SAXException {
    //
  }

  protected final void emitChild(Emitter child) throws SAXException {
    switch (status) {
      case INITIAL:
        emmitBeginning();
        status = OPEN;
        break;
      case OPEN:
        if (currentChild != null) {
          currentChild.close();
        }
        break;
      default:
        throw new IllegalStateException();
    }
    currentChild = child;
  }

  public final void close() throws SAXException {
    switch (status) {
      case INITIAL:
        emmitBeginning();
        emmitEnd();
        status = CLOSED;
        break;
      case OPEN:
        if (currentChild != null) {
          currentChild.close();
        }
        emmitEnd();
        status = CLOSED;
        break;
      default:
        throw new IllegalStateException();
    }
  }
}
