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

package org.chromattic.testgenerator.builder;

import japa.parser.ast.CompilationUnit;
import org.chromattic.testgenerator.visitor.renderer.GroovyCompatibilityFactory;
import org.chromattic.testgenerator.visitor.transformer.UnitChromatticVisitor;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class GroovyFromJavaSourceChromatticBuilder {
  private CompilationUnit compilationUnit;
  private StringBuilder sb = new StringBuilder();

  public GroovyFromJavaSourceChromatticBuilder(CompilationUnit compilationUnit) {
    this.compilationUnit = compilationUnit;
  }

  public void build() {
    UnitChromatticVisitor unitChromatticVisitor = new UnitChromatticVisitor();
    compilationUnit.getPackage().getName().setName(compilationUnit.getPackage().getName().getName() + ".groovy");
    unitChromatticVisitor.visit(compilationUnit, null);
    sb.append(compilationUnit.toString(new GroovyCompatibilityFactory()));
  }

  @Override
  public String toString() {
    return sb.toString();
  }
}