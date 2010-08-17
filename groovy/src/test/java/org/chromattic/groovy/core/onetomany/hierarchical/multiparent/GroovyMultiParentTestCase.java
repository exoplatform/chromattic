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

package org.chromattic.groovy.core.onetomany.hierarchical.multiparent;

import org.chromattic.test.onetomany.hierarchical.multiparent.A;
import org.chromattic.test.onetomany.hierarchical.multiparent.B;
import org.chromattic.test.onetomany.hierarchical.multiparent.C;
import org.chromattic.test.onetomany.hierarchical.multiparent.MultiParentTestCase;
import org.chromattic.testgenerator.UniversalTest;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
@UniversalTest(
  sourceClass = MultiParentTestCase.class,
  baseDir = "core",
  suffix = "CoreTest",
  chromatticClasses = {A.class, B.class, C.class})
public class GroovyMultiParentTestCase {
}
