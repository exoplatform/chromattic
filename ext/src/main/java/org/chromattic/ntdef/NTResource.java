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
package org.chromattic.ntdef;

import org.chromattic.api.annotations.PrimaryType;
import org.chromattic.api.annotations.Property;

import java.io.InputStream;
import java.util.Date;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@PrimaryType(name = "nt:resource")
public abstract class NTResource {

  @Property(name = "jcr:encoding")
  public abstract String getEncoding();

  public abstract void setEncoding(String encoding);

  @Property(name = "jcr:mimeType")
  public abstract String getMimeType();

  public abstract void setMimeType(String mimeType);

  @Property(name = "jcr:data")
  public abstract InputStream getData();

  public abstract void setData(InputStream data);

  @Property(name = "jcr:lastModified")
  public abstract Date getLastModified();

  public abstract void setLastModified(Date date);

}
