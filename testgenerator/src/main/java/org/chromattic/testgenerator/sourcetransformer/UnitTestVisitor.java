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

import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.ArrayCreationExpr;
import japa.parser.ast.expr.ArrayInitializerExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class UnitTestVisitor extends VoidVisitorAdapter implements SourceTransformation {
  private List<AnnotationExpr> annotationExprs = new ArrayList<AnnotationExpr>();
  private List<MethodCallExpr> methodCallExprs = new ArrayList<MethodCallExpr>();
  private List<ArrayCreationExpr> arrayCreationExprs = new ArrayList<ArrayCreationExpr>();
  private String suffix;

  public UnitTestVisitor(String suffix) {
    this.suffix = suffix;
  }

  @Override
  public void visit(ClassOrInterfaceDeclaration n, Object arg) {
    if (n.getAnnotations() != null)  annotationExprs.addAll(n.getAnnotations());
    n.setName(n.getName() + suffix);
    super.visit(n, arg);
  }

  @Override
  public void visit(MethodDeclaration n, Object arg) {
    if (n.getAnnotations() != null)  annotationExprs.addAll(n.getAnnotations());
    super.visit(n, arg);
  }

  @Override
  public void visit(MethodCallExpr n, Object arg) {
    methodCallExprs.add(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(ArrayCreationExpr n, Object arg) {
    arrayCreationExprs.add(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(ArrayInitializerExpr n, Object arg) {
    System.out.println(n);
    super.visit(n, arg);
  }

  public List<AnnotationExpr> getAnnotationExprs() {
    return annotationExprs;
  }

  public List<MethodCallExpr> getMethodCallExprs() {
    return methodCallExprs;
  }

  public List<ArrayCreationExpr> getArrayCreationExprs() {
    return arrayCreationExprs;
  }
}
