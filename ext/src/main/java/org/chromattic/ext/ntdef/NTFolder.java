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

import org.chromattic.api.annotations.Create;
import org.chromattic.api.annotations.PrimaryType;
import org.chromattic.api.annotations.OneToMany;

import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@PrimaryType(name = "nt:folder")
public abstract class NTFolder extends NTHierarchyNode implements Iterable<NTHierarchyNode> {

  @Create
  protected abstract NTFile createFile();

  @Create
  protected abstract NTFolder createFolder();

  @OneToMany
  public abstract Map<String, NTHierarchyNode> getChildren();

  public Iterator<NTHierarchyNode> iterator() {
    return getChildren().values().iterator();
  }

  public NTFolder createFolder(String folderName) {
    if (folderName == null) {
      throw new NullPointerException();
    }
    NTFolder folder = createFolder();
    addChild(folderName, folder);
    return folder;
  }

  public NTFile createFile(String fileName, Resource contentResource) {
    if (fileName == null) {
      throw new NullPointerException();
    }
    NTFile file = createFile();
    addChild(fileName, file);
    if (contentResource != null) {
      file.setContentResource(contentResource);
    }
    return file;
  }

  public void addChild(NTHierarchyNode child) {
    if (child == null) {
      throw new NullPointerException();
    }
    addChild(child.getName(), child);
  }

  public void addChild(String childName, NTHierarchyNode child) {
    if (childName == null) {
      throw new NullPointerException();
    }
    if (child == null) {
      throw new NullPointerException();
    }
    Map<String, NTHierarchyNode> children = getChildren();
    if (children.containsKey(childName)) {
      throw new IllegalStateException();
    }
    children.put(childName, child);
  }

  public NTHierarchyNode getChild(String childName) {
    if (childName == null) {
      throw new NullPointerException();
    }
    Map<String, NTHierarchyNode> children = getChildren();
    return children.get(childName);
  }

  public NTFile getFile(String fileName) {
    NTHierarchyNode hierarchyNode = getChild(fileName);
    if (hierarchyNode instanceof NTFile) {
      return (NTFile)hierarchyNode;
    } else {
      throw new IllegalStateException();
    }
  }

  public NTFolder getFolder(String fileName) {
    NTHierarchyNode hierarchyNode = getChild(fileName);
    if (hierarchyNode instanceof NTFolder) {
      return (NTFolder)hierarchyNode;
    } else {
      throw new IllegalStateException();
    }
  }
}
