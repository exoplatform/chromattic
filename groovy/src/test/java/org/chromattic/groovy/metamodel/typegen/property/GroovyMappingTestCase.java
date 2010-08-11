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

package org.chromattic.groovy.metamodel.typegen.property;

import org.chromattic.metamodel.typegen.property.A1;
import org.chromattic.metamodel.typegen.property.A2;
import org.chromattic.metamodel.typegen.property.B1;
import org.chromattic.metamodel.typegen.property.B2;
import org.chromattic.metamodel.typegen.property.C1;
import org.chromattic.metamodel.typegen.property.C2;
import org.chromattic.metamodel.typegen.property.D1;
import org.chromattic.metamodel.typegen.property.D2;
import org.chromattic.metamodel.typegen.property.E;
import org.chromattic.metamodel.typegen.property.MappingTestCase;
import org.chromattic.testgenerator.UniversalTest;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
@UniversalTest(
  sourceClass = MappingTestCase.class,
  chromatticClasses = {A1.class, A2.class, B1.class, B2.class, C1.class, C2.class, D1.class, D2.class, E.class})
public class GroovyMappingTestCase extends MappingTestCase {}