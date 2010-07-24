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
import org.codehaus.groovy.control.SourceUnit;

import java.util.*;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class ChromatticASTTransformationVisitor {
  private final ChromatticAnnotationMover annotationMover = new ChromatticAnnotationMover();
  private final ChromatticFieldChecker fieldChecker = new ChromatticFieldChecker();
  private final ChromatticDelegate delegate = new ChromatticDelegate();

  public void visit(ASTNode[] nodes, SourceUnit sourceUnit) throws ChromatticASTTransformationException {
    for (ClassNode classNode : (List<ClassNode>) sourceUnit.getAST().getClasses()) {
      if (!classNode.isScript()) {
        Set<AnnotationNode> annotationNodeSet = new HashSet<AnnotationNode>();
        annotationNodeSet.addAll(classNode.getAnnotations());
        for (FieldNode fieldNode : classNode.getFields()) annotationNodeSet.addAll(fieldNode.getAnnotations());
        for (MethodNode methodNode : classNode.getMethods()) annotationNodeSet.addAll(methodNode.getAnnotations());
        for (AnnotationNode annotationNode : annotationNodeSet) {
          if (annotationNode.getClassNode().getName().startsWith(GroovyUtils.ANNOTATIONS_PACKAGE)) {
            visitClass(classNode);
          break;
          }
        }
      }
    }
  }

  private void visitClass(ClassNode classNode) throws ChromatticASTTransformationException {

    // Browse children to adapt groovy structure
    for (FieldNode fieldNode : classNode.getFields()) {
      for (AnnotationNode annotationNode : (List<AnnotationNode>)fieldNode.getAnnotations()) {
        if (annotationNode.getClassNode().getName().startsWith(GroovyUtils.ANNOTATIONS_PACKAGE)) {
          fieldChecker.checkChromaticFieldType(fieldNode);
          annotationMover.moveFieldAnnotationToMethod(classNode, annotationNode, fieldNode);
        }
      }
      annotationMover.removeChromatticAnnotation(fieldNode);
    }

    // Transform GroovyObject to ChromatticObject
    //System.out.println("[CHROMATTIC] Transform " + classNode.getName() + " to chromattic class");
    delegate.setGroovyInterceptable(classNode);
    delegate.addChromatticInvokeMethod(classNode);
    delegate.plugChromatticDelegation(classNode);
  }
}
