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

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class ChromatticASTAction {
  private ClassNode classNode;
  private FieldNode fieldNode;
  private AnnotationNode annotationNode;

  public ChromatticASTAction(ClassNode classNode, FieldNode fieldNode, AnnotationNode annotationNode) {
    this.classNode = classNode;
    this.fieldNode = fieldNode;
    this.annotationNode = annotationNode;
  }

  public ClassNode getClassNode() {
    return classNode;
  }

  public FieldNode getFieldNode() {
    return fieldNode;
  }

  public AnnotationNode getAnnotationNode() {
    return annotationNode;
  }

  @Override
  public String toString() {
    return "ChromatticASTAction{" +
            "classNode=" + classNode +
            ", fieldNode=" + fieldNode.getName() +
            ", annotationNode=" + annotationNode.getClassNode().getName() +
            '}';
  }
}
