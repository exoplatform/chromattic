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

package org.chromattic.groovy;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;

import java.lang.reflect.Modifier;
import java.util.List;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class GroovyUtils {
  public static enum GetSet {
    GET, SET;
  }

  public static final String ANNOTATIONS_PACKAGE = "org.chromattic.api.annotations.";

  public static String getsetName(GetSet getSet, String fieldName) {
    return String.format("%s%s%s",
      getSet.toString().toLowerCase()
      , fieldName.substring(0, 1).toUpperCase()
      , fieldName.substring(1)
      );
  }

  public static String fieldName(String getsetName) {
    if (
            !"get".equals(getsetName.substring(0 , 3))
            &&
            !"set".equals(getsetName.substring(0 , 3))
            ) throw new IllegalArgumentException("Invalid getter or setter name : " + getsetName);
    return String.format("%s%s", getsetName.substring(3, 4).toLowerCase(), getsetName.substring(4));
  }

  public static void createGetter(ClassNode classNode, FieldNode fieldNode) {
    classNode.addMethod(
      GroovyUtils.getsetName(GroovyUtils.GetSet.GET, fieldNode.getName())
      , Modifier.PUBLIC
      , fieldNode.getType()
      , new Parameter[]{}
      , new ClassNode[]{}
      , new ReturnStatement(new FieldExpression(fieldNode))
    );
  }

  public static void createSetter(ClassNode classNode, FieldNode fieldNode) {
    classNode.addMethod(
      GroovyUtils.getsetName(GroovyUtils.GetSet.SET, fieldNode.getName())
      , Modifier.PUBLIC
      , ClassHelper.VOID_TYPE
      , new Parameter[]{ new Parameter(fieldNode.getType(), "value") }
      , new ClassNode[]{}
      , new ExpressionStatement(new BinaryExpression(new PropertyExpression(new VariableExpression("this"), fieldNode.getName()), Token.newSymbol(Types.EQUAL, 0, 0), new VariableExpression("value")))
    );
  }

  public static MethodNode getGetter(ClassNode classNode, FieldNode fieldNode) {
    return classNode.getGetterMethod(GroovyUtils.getsetName(GroovyUtils.GetSet.GET, fieldNode.getName()));
  }

  public static MethodNode getSetter(ClassNode classNode, FieldNode fieldNode) {
    return classNode.getSetterMethod(GroovyUtils.getsetName(GroovyUtils.GetSet.SET, fieldNode.getName()));
  }

  public static boolean isChromatticAnnoted(FieldNode fieldNode) {
    for (AnnotationNode annotationNode : (List<AnnotationNode>) fieldNode.getAnnotations()) {
      if (annotationNode.getClassNode().getName().startsWith(GroovyUtils.ANNOTATIONS_PACKAGE)) return true;
    }
    return false;
  }

  public static boolean isChromatticAnnotedInHierarchy(ClassNode classNode, FieldNode fieldNode) {
    if (classNode == null) classNode = fieldNode.getDeclaringClass();
    ClassNode superClassNode = classNode.getSuperClass(); 
    if (!superClassNode.equals(ClassHelper.OBJECT_TYPE)) {
      MethodNode superMethodNode = superClassNode.getMethod(getsetName(GetSet.GET, fieldNode.getName()), new Parameter[]{});
      if (superMethodNode != null) return true;
      else isChromatticAnnotedInHierarchy(superClassNode, fieldNode);
    }
    return false;
  }
}
