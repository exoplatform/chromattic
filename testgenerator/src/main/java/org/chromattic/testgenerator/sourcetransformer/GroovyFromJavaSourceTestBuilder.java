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

package org.chromattic.testgenerator.sourcetransformer;

import japa.parser.ast.CompilationUnit;

import java.util.List;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class GroovyFromJavaSourceTestBuilder {
  private CompilationUnit compilationUnit;
  private StringBuilder sb = new StringBuilder();
  private String suffix;

  public GroovyFromJavaSourceTestBuilder(CompilationUnit compilationUnit, String suffix) {
    this.compilationUnit = compilationUnit;
    this.suffix = suffix;
  }

  public void build(TransformationProcessor transformationProcessor, List<String> excludedMethods) {
    UnitTestVisitor unitTestVisitor = new UnitTestVisitor("_" + suffix);
    unitTestVisitor.visit(compilationUnit, excludedMethods);
    transformationProcessor.setTransformationSource(unitTestVisitor);
    sb.append(transformationProcessor.transform(compilationUnit.toString()));
  }

  @Override
  public String toString() {
    return sb.toString();
  }
}
