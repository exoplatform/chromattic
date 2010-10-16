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
package org.chromattic.ext.ntdef;

import org.chromattic.api.annotations.*;
import org.chromattic.common.IO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Date;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@PrimaryType(name = "nt:file")
public abstract class NTFile extends NTHierarchyNode {
  
  @OneToOne
  @Owner
  @MappedBy("jcr:content")
  public abstract Object getContent();

  public abstract void setContent(Object content);

  @Create
  protected abstract NTResource createResource();

  public Date getLastModified() {
    Object content = getContent();
    if (content == null) {
      return null;
    } else {
      if (content instanceof NTResource) {
        NTResource contentResource = (NTResource)content;
        return contentResource.getLastModified();
      } else {
        throw new IllegalStateException();
      }
    }
  }

  public void setContentResource(Resource resource) {
    if (resource != null) {
      Object content = getContent();
      NTResource contentResource;
      if (content instanceof NTResource) {
        contentResource = (NTResource)content;
      } else {
        contentResource = createResource();
        setContent(contentResource);
      }
      contentResource.update(resource);
    } else {
      setContent(null);
    }
  }

  public Resource getContentResource() {
    Object content = getContent();
    if (content == null) {
      return null;
    } else {
      if (content instanceof NTResource) {
        NTResource contentResource = (NTResource)content;
        String encoding = contentResource.getEncoding();
        String mimeType = contentResource.getMimeType();
        InputStream data = contentResource.getData();
        byte[] bytes;
        try {
          bytes = IO.getBytes(data);
        }
        catch (IOException e) {
          throw new UndeclaredThrowableException(e);
        }
        return new Resource(mimeType, encoding, bytes);
      } else {
        throw new IllegalStateException();
      }
    }
    }
}
