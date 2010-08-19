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

import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.ArrayCreationExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.type.Type;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class UnitChromatticVisitor extends VoidVisitorAdapter implements SourceTransformation {
  private List<AnnotationExpr> annotationExprs = new ArrayList<AnnotationExpr>();
  private List<MethodCallExpr> methodCallExprs = new ArrayList<MethodCallExpr>();

  @Override
  public void visit(ClassOrInterfaceDeclaration n, Object arg) {
    if (n.getAnnotations() != null)  annotationExprs.addAll(n.getAnnotations());
    List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();
    n.setModifiers(n.getModifiers() & ~Modifier.ABSTRACT & ~Modifier.PUBLIC);

    for (BodyDeclaration bodyDeclaration : n.getMembers()) {
      if (bodyDeclaration instanceof MethodDeclaration) {
        MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
        if (methodDeclaration.getAnnotations() != null)  annotationExprs.addAll(methodDeclaration.getAnnotations());
        fieldInfos.add(new FieldInfo(methodDeclaration.getType(), methodDeclaration.getName(), methodDeclaration.getAnnotations(), methodDeclaration));
      }
    }

    for(FieldInfo fieldInfo : fieldInfos) {
      try {
        FieldDeclaration fieldDeclaration = new FieldDeclaration(Modifier.PRIVATE, fieldInfo.getType(), new VariableDeclarator(new VariableDeclaratorId(fieldName(fieldInfo.getName()))));
        fieldDeclaration.setAnnotations(new ArrayList<AnnotationExpr>());
        if (fieldInfo.getAnnotationExprs() != null) {
          for (AnnotationExpr annotationExpr : fieldInfo.getAnnotationExprs()) {
            if (
                    !annotationExpr.getName().getName().equals("Override")
                    && !annotationExpr.getName().getName().equals("Skip") 
                    ) {
              fieldDeclaration.getAnnotations().add(annotationExpr);
            }
          }
        }
        if (fieldInfo.getName().startsWith("get")) {
          n.getMembers().add(fieldDeclaration);
        }
        n.getMembers().remove(fieldInfo.getSourceDeclaration());
      } catch (IllegalArgumentException e) {
        continue;
      }
    }
    if (n.getAnnotations() != null) annotationExprs.addAll(n.getAnnotations());
    super.visit(n, arg);
  }

    @Override
    public void visit(MethodCallExpr n, Object arg) {
      methodCallExprs.add(n);
      super.visit(n, arg);
    }

  private class FieldInfo {
    private Type type;
    private String name;
    private List<AnnotationExpr> annotationExprs;
    private MethodDeclaration sourceDeclaration;

    private FieldInfo(Type type, String name, List<AnnotationExpr> annotationExprs, MethodDeclaration sourceDeclaration) {
      this.type = type;
      this.name = name;
      this.annotationExprs = annotationExprs;
      this.sourceDeclaration = sourceDeclaration;
    }

    public Type getType() {
      return type;
    }

    public String getName() {
      return name;
    }

    public List<AnnotationExpr> getAnnotationExprs() {
      return annotationExprs;
    }

    public MethodDeclaration getSourceDeclaration() {
      return sourceDeclaration;
    }
  }

  public static String fieldName(String getsetName) {
    if (
            !"get".equals(getsetName.substring(0 , 3))
            &&
            !"set".equals(getsetName.substring(0 , 3))
            ) throw new IllegalArgumentException("Invalid getter or setter name : " + getsetName);
    return String.format("%s%s", getsetName.substring(3, 4).toLowerCase(), getsetName.substring(4));
  }

  public List<AnnotationExpr> getAnnotationExprs() {
    return annotationExprs;
  }

  public List<MethodCallExpr> getMethodCallExprs() {
    return methodCallExprs;
  }

  public List<ArrayCreationExpr> getArrayCreationExprs() {
    return new ArrayList<ArrayCreationExpr>();
  }
}