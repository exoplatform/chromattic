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
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.expr.NameExpr;

import java.util.List;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class GroovyFromJavaSourceTestBuilder {
  private CompilationUnit compilationUnit;
  private StringBuilder sb = new StringBuilder();
  private String name;
  private List<String> deps;

  public GroovyFromJavaSourceTestBuilder(CompilationUnit compilationUnit, String name, List<String> deps) {
    this.compilationUnit = compilationUnit;
    this.name = name;
    this.deps = deps;
  }

  public void build(TransformationProcessor transformationProcessor, List<String> excludedMethods) {
    UnitTestVisitor unitTestVisitor = new UnitTestVisitor(name);
    for (String dep : deps)
    {
      int i = dep.lastIndexOf(".");
      String depPackage = dep.substring(0, i) + ".groovy";
      String depImport = depPackage + dep.substring(i);
      compilationUnit.getImports().add(new ImportDeclaration(new NameExpr(depImport), false, false));
    }
    unitTestVisitor.visit(compilationUnit, excludedMethods);
    transformationProcessor.setTransformationSource(unitTestVisitor);
    sb.append(transformationProcessor.transform(compilationUnit.toString()));
  }

  @Override
  public String toString() {
    return sb.toString();
  }
}
