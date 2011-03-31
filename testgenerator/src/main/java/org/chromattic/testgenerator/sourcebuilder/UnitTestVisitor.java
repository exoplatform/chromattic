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

package org.chromattic.testgenerator.sourcebuilder;

import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.ArrayCreationExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class UnitTestVisitor extends VoidVisitorAdapter<List<String>> implements TransformationSource {
  private List<AnnotationExpr> annotationExprs = new ArrayList<AnnotationExpr>();
  private List<MethodCallExpr> methodCallExprs = new ArrayList<MethodCallExpr>();
  private String name;

  public UnitTestVisitor(String name) {
    this.name = name;
  }

  @Override
  public void visit(ClassOrInterfaceDeclaration n, List<String> excludedMethods) {
    n.setAnnotations(null);
    List<MethodDeclaration> methodToRemove = new ArrayList<MethodDeclaration>();
    for (BodyDeclaration bodyDeclaration : n.getMembers()) {
      if(bodyDeclaration instanceof MethodDeclaration) {
        MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
        if (excludedMethods.contains(methodDeclaration.getName())) {
          methodToRemove.add(methodDeclaration);
        }
      }
    }
    n.getMembers().removeAll(methodToRemove);

    //
    if (n.getAnnotations() != null)  annotationExprs.addAll(n.getAnnotations());
    n.setName(name);
    super.visit(n, excludedMethods);
  }

  @Override
  public void visit(MethodDeclaration n, List<String> arg) {
    if (n.getAnnotations() != null)  annotationExprs.addAll(n.getAnnotations());
    super.visit(n, arg);
  }

  @Override
  public void visit(MethodCallExpr n, List<String> arg) {
    methodCallExprs.add(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(ArrayCreationExpr n, List<String> arg) {
    super.visit(n, arg);
  }

  public List<AnnotationExpr> getAnnotationExprs() {
    return annotationExprs;
  }

  public List<MethodCallExpr> getMethodCallExprs() {
    return methodCallExprs;
  }
}
