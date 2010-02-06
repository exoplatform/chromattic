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

package org.chromattic.test.common.jcr;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import org.chromattic.common.jcr.Path;
import org.chromattic.common.jcr.PathException;
import org.chromattic.common.jcr.PathVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TestVisitor implements PathVisitor {

  /** . */
  private final List<String> atoms = new ArrayList<String>();

  public void onPrefixPathSegment(String s, int prefixStart, int prefixEnd, int start, int end, Integer number) {
    String atom = "_" + s.substring(prefixStart, prefixEnd) + ":" + s.substring(start, end);
    if (number != null) {
      atom += "[" + number + "]";
    }
    atoms.add(atom);
  }

  public void onURIPathSegment(String s, int uriStart, int uriEnd, int start, int end, Integer number) {
    String atom = "_{" + s.substring(uriStart, uriEnd) + "}" + s.substring(start, end);
    if (number != null) {
      atom += "[" + number + "]";
    }
    atoms.add(atom);
  }

  public void onPathSegment(String s, int start, int end, Integer number) {
    String atom = "_" + s.substring(start, end);
    if (number != null) {
      atom += "[" + number + "]";
    }
    atoms.add(atom);
  }

  public void onSelf() {
    atoms.add(".");
  }

  public void onParent() {
    atoms.add("..");
  }

  public void assertPathSegment(String pathSegment, String... expectedAtoms) {
    atoms.clear();
    try {
      Path.parseRelativePath(this, pathSegment);
    }
    catch (PathException e) {
      AssertionFailedError afe = new AssertionFailedError();
      afe.initCause(e);
      throw afe;
    }
    Assert.assertEquals(Arrays.asList(expectedAtoms), atoms);
  }

  public void assertPathSegmentFailure(String pathSegment) {
    try {
      Path.parsePathSegment(pathSegment, this);
      Assert.fail("Was expecting path segment " + pathSegment + " to fail");
    }
    catch (PathException ignore) {
    }
  }

  public void assertAbsolurePath(String absolutePath, String... expectedAtoms) {
    atoms.clear();
    try {
      Path.parseAbsolutePath(absolutePath, this);
    }
    catch (PathException e) {
      AssertionFailedError afe = new AssertionFailedError();
      afe.initCause(e);
      throw afe;
    }
    Assert.assertEquals(Arrays.asList(expectedAtoms), atoms);

    //
    assertPath(absolutePath, expectedAtoms);
  }

  public void assertAbsolutePathFailure(String absolutePath) {
    try {
      Path.parseAbsolutePath(absolutePath, this);
      Assert.fail("Was expecting absolute path " + absolutePath + " to fail");
    }
    catch (PathException ignore) {
    }
  }

  public void assertRelativePath(String path, String... expectedAtoms) {
    atoms.clear();
    try {
      Path.parseRelativePath(this, path);
    }
    catch (PathException e) {
      AssertionFailedError afe = new AssertionFailedError();
      afe.initCause(e);
      throw afe;
    }
    Assert.assertEquals(Arrays.asList(expectedAtoms), atoms);

    //
    assertPath(path, expectedAtoms);
  }

  public void assertRelativePathFailure(String relativePath) {
    try {
      Path.parseRelativePath(this, relativePath);
      Assert.fail("Was expecting relative path " + relativePath + " to fail");
    }
    catch (PathException ignore) {
    }
  }

  public void assertPath(String path, String... expectedAtoms) {
    atoms.clear();
    try {
      Path.parsePath(path, this);
    }
    catch (PathException e) {
      AssertionFailedError afe = new AssertionFailedError();
      afe.initCause(e);
      throw afe;
    }
    Assert.assertEquals(Arrays.asList(expectedAtoms), atoms);
  }

  public void assertPathFailure(String path) {
    try {
      Path.parseRelativePath(this, path);
      Assert.fail("Was expecting path segment " + path + " to fail");
    }
    catch (PathException ignore) {
    }
  }
}
