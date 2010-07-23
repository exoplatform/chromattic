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
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.stmt.ReturnStatement;

import java.lang.reflect.Modifier;
import java.util.Iterator;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class ChromatticAnnotationMover {
  
  public void moveFieldAnnotationToMethod(ClassNode classNode, AnnotationNode annotationNode, FieldNode fieldNode) {
    try {
      getGetter(classNode, fieldNode).addAnnotation(annotationNode);
    } catch (NullPointerException e) {
      // If getter doesn't exist, retry after getter creation
      createGetter(classNode, fieldNode);
      getGetter(classNode, fieldNode).addAnnotation(annotationNode);
    }
  }

  public void removeChromatticAnnotation(FieldNode fieldNode) {
    Iterator<AnnotationNode> it = fieldNode.getAnnotations().iterator();
    while(it.hasNext())
      if (it.next().getClassNode().getName().startsWith(GroovyUtils.ANNOTATIONS_PACKAGE))
        it.remove();
  }

  private MethodNode getGetter(ClassNode classNode, FieldNode fieldNode) {
    return classNode.getGetterMethod(GroovyUtils.getterName(fieldNode.getName()));
  }

  private void createGetter(ClassNode classNode, FieldNode fieldNode) {
  classNode.addMethod(
    GroovyUtils.getterName(fieldNode.getName())
    , Modifier.PUBLIC
    , fieldNode.getType()
    , new Parameter[]{}
    , new ClassNode[]{}
    , new ReturnStatement(new FieldExpression(fieldNode))
    );
  }
}
