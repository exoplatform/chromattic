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

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.chromattic.common.jcr.Path;
import org.chromattic.common.jcr.PathException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PathVisitorTestCase extends TestCase {

  /** . */
  private final TestVisitor visitor = new TestVisitor();

  public void testParseAbsolutePath() {
    visitor.assertPath("/");
    visitor.assertPath("/a", "_a");
    visitor.assertPath("/a:b/{c}d", "_a:b", "_{c}d");

    visitor.assertAbsolutePathFailure("a");
    visitor.assertAbsolutePathFailure(".");
    visitor.assertAbsolutePathFailure("..");
    visitor.assertAbsolutePathFailure("a/");
  }

  public void testParseRelativePath() {
    visitor.assertRelativePath("", "_");
    
    visitor.assertRelativePath("a", "_a");
    visitor.assertRelativePath(".", ".");
    visitor.assertRelativePathFailure("[");
    visitor.assertRelativePathFailure("]");
    visitor.assertRelativePathFailure("|");

    visitor.assertRelativePath("a/", "_a");
    visitor.assertRelativePath("..", "..");
    visitor.assertRelativePath(".a", "_.a");
    visitor.assertRelativePath("a.", "_a.");
    visitor.assertRelativePath("ab", "_ab");
    visitor.assertRelativePath("./", ".");
    visitor.assertRelativePath(".:", "_.:");
    visitor.assertRelativePath(":a", "_:a");
    visitor.assertRelativePath("a:", "_a:");
    visitor.assertRelativePathFailure("//");
    visitor.assertRelativePathFailure("/:");
    visitor.assertRelativePathFailure("/[");
    visitor.assertRelativePathFailure("/]");
    visitor.assertRelativePathFailure("/*");
    visitor.assertRelativePathFailure("/'");
    visitor.assertRelativePathFailure("/'");
    visitor.assertRelativePathFailure("/\"");
    visitor.assertRelativePathFailure("/|");
    visitor.assertRelativePathFailure(".[");
    visitor.assertRelativePathFailure(".]");
    visitor.assertRelativePathFailure(".*");
    visitor.assertRelativePathFailure(".|");

    visitor.assertRelativePath("a/b", "_a", "_b");
    visitor.assertRelativePath("./b", ".", "_b");
    visitor.assertRelativePath("a/.", "_a", ".");
    visitor.assertRelativePath("./.", ".", ".");
    visitor.assertRelativePath("../", "..");

    visitor.assertRelativePath("../.", "..", ".");
    visitor.assertRelativePath("./..", ".", "..");
    visitor.assertRelativePath("../a", "..", "_a");
    visitor.assertRelativePath("a/..", "_a", "..");
    visitor.assertRelativePath("a[0]", "_a[0]");

    visitor.assertRelativePath("../..", "..", "..");
  }

  public void testParsePathSegment() {
    visitor.assertPathSegmentFailure("{");
    visitor.assertPathSegmentFailure("{a");
    visitor.assertPathSegmentFailure("/");
    visitor.assertPathSegmentFailure("[");
    visitor.assertPathSegmentFailure("]");
    visitor.assertPathSegmentFailure("*");
    visitor.assertPathSegmentFailure("|");
    visitor.assertPathSegmentFailure("::");
    visitor.assertPathSegment("", "_");
    visitor.assertPathSegment(".", ".");
    visitor.assertPathSegment("..", "..");
    visitor.assertPathSegment(":", "_:");
    visitor.assertPathSegment(":a", "_:a");
    visitor.assertPathSegment("{a}a", "_{a}a");
    visitor.assertPathSegment("a:a", "_a:a");
    visitor.assertPathSegment("a:", "_a:");
  }

  public void testNormalizePath() {
    assertNormalizablePath("/", "/");
    assertNormalizablePath("/a", "/a");
    assertNormalizablePath("/a/", "/a");
    assertNormalizablePath("/a/.", "/a");
    assertNormalizablePath("/a/./", "/a");
    assertNormalizablePath("/a/..", "/");
    assertNormalizablePath("/a/../", "/");
    assertNormalizablePath("/a/b/..", "/a");
    assertNormalizablePath("/a/b/../", "/a");
  }

  private void assertNormalizablePath(String path, String expectedNormalizedPath) {
    try {
      String normalizedPath = Path.normalizeAbsolutePath(path);
      assertEquals(expectedNormalizedPath, normalizedPath);
    }
    catch (PathException e) {
      AssertionFailedError afe = new AssertionFailedError();
      afe.initCause(e);
      throw afe;
    }
  }

  private void assertInvalidNormalizablePath(String path) {
    try {
      Path.normalizeAbsolutePath(path);
      fail();
    }
    catch (PathException ignore) {
    }
  }
}
