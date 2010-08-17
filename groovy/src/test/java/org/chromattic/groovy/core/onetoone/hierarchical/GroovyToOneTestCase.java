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

package org.chromattic.groovy.core.onetoone.hierarchical;

import org.chromattic.test.onetoone.hierarchical.TOTO_A_2;
import org.chromattic.test.onetoone.hierarchical.TOTO_B_2;
import org.chromattic.test.onetoone.hierarchical.ToOneTestCase;
import org.chromattic.testgenerator.UniversalTest;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
@UniversalTest(
  sourceClass = ToOneTestCase.class,
  baseDir = "core",
  suffix = "CoreTest",
  chromatticClasses = {TOTO_A_2.class, TOTO_B_2.class})
public class GroovyToOneTestCase {
}
