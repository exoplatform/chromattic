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

package org.chromattic.groovy.relaunch.classloader;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.type.Type;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import org.chromattic.groovy.relaunch.classloader.exceptions.LoaderException;
import org.chromattic.groovy.relaunch.sourceloader.JavaSourceLoader;

import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class ChromatticTestClassLoader extends TestClassLoader {

  public ChromatticTestClassLoader(ClassLoader parent) {
    super(parent);
  }

  public Class<?> loadClass(String name, Class currentLoading) throws ClassNotFoundException {
    try {
      InputStream cuis = JavaSourceLoader.getSource(currentLoading.getName());
      if (cuis == null) return currentLoading;

      //
      CompilationUnit unit = JavaParser.parse(cuis);
      new UnitVisitor().visit(unit, null);
      //System.out.println(unit);
      return new GroovyClassLoader().parseClass(unit.toString());
    } catch (ParseException e) {
      throw new LoaderException(e);
    }
  }

  private class UnitVisitor extends VoidVisitorAdapter {
    
    @Override
    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
      List<FieldInfo> fieldInfos = new ArrayList<FieldInfo>();
      n.setModifiers(n.getModifiers() & ~Modifier.ABSTRACT);
      n.setModifiers(0);

      for (BodyDeclaration bodyDeclaration : n.getMembers()) {
        if (bodyDeclaration instanceof MethodDeclaration) {
          MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
          fieldInfos.add(new FieldInfo(methodDeclaration.getType(), methodDeclaration.getName(), methodDeclaration.getAnnotations(), methodDeclaration));
        }
      }

      for(FieldInfo fieldInfo : fieldInfos) {
        FieldDeclaration fieldDeclaration = new FieldDeclaration(Modifier.PRIVATE, fieldInfo.getType(), new VariableDeclarator(new VariableDeclaratorId(fieldName(fieldInfo.getName()))));
        fieldDeclaration.setAnnotations(fieldInfo.getAnnotationExprs());
        if (fieldInfo.getName().startsWith("get")) {
          n.getMembers().add(fieldDeclaration);
        }
        n.getMembers().remove(fieldInfo.getSourceDeclaration());
      }
      super.visit(n, arg);
    }
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
}
