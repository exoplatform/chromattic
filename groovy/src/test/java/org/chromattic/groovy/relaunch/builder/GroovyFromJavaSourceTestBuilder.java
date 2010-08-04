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

package org.chromattic.groovy.relaunch.builder;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class GroovyFromJavaSourceTestBuilder {
  private final Class<?> model;
  private final CompilationUnit compilationUnit;
  private final StringBuilder sb = new StringBuilder();

  public GroovyFromJavaSourceTestBuilder(Class<?> model, CompilationUnit compilationUnit) {
    this.model = model;
    this.compilationUnit = compilationUnit;
  }

  public void build(List<String> imports) {
    new UnitVisitor().visit(compilationUnit, null);
    compilationUnit.getImports().add(new ImportDeclaration(compilationUnit.getPackage().getName() , false, true));
    compilationUnit.setPackage(new PackageDeclaration(new NameExpr(model.getPackage().getName())));
    sb.append(compilationUnit);
    for (ImportDeclaration importDeclaration : compilationUnit.getImports()) {
      imports.add(importDeclaration.getName().toString());
    }
  }

  @Override
  public String toString() {
    return sb.toString();
  }

  private class UnitVisitor extends VoidVisitorAdapter {

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
      n.setName(model.getSimpleName());
      super.visit(n, arg);
    }

    @Override
    public void visit(MethodCallExpr n, Object arg) {
      n.setTypeArgs(null);
      super.visit(n, arg);
    }
  }
}