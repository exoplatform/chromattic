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

import org.chromattic.api.annotations.Create;
import org.chromattic.api.annotations.NodeMapping;
import org.chromattic.api.annotations.OneToMany;

import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@NodeMapping(name = "nt:folder")
public abstract class NTFolder extends NTHierarchyNode implements Iterable {

  @Create
  protected abstract NTFile create();

  @OneToMany
  public abstract Map<String, NTHierarchyNode> getChildren();

  public NTFile createFile(String fileName) {
    return createFile(fileName, null);
  }

  public NTFile createFile(String fileName, Resource contentResource) {
    if (fileName == null) {
      throw new NullPointerException();
    }
    Map<String, NTHierarchyNode> children = getChildren();
    if (children.containsKey(fileName)) {
      throw new IllegalStateException("File " + fileName + " already exists");
    }
    NTFile file = create();
    children.put(fileName, file);
    if (contentResource != null)
    file.setContentResource(contentResource);
    return file;
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

  public void addChild(NTHierarchyNode child) {
    if (child == null) {
      throw new NullPointerException();
    }
    addChild(child.getName(), child);
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

}
