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
package org.chromattic.common;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class CopyingInputStream extends InputStream {

  /** . */
  private final ByteArrayOutputStream baos = new ByteArrayOutputStream(100);

  /** . */
  private final InputStream in;

  public CopyingInputStream(InputStream in) {
    this.in = in;
  }

  @Override
  public int read() throws IOException {
    int value = in.read();
    if (value != -1) {
      baos.write(value);
    }
    return value;
  }

  @Override
  public void close() throws IOException {
    in.close();
  }

  public byte[] getBytes() {
    return baos.toByteArray();
  }
}
