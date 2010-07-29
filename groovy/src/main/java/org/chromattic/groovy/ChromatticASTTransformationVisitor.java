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

import org.chromattic.groovy.exceptions.*;
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
  private final ChromatticConstructor constructor = new ChromatticConstructor();

  public void visit(ASTNode[] nodes, SourceUnit sourceUnit) throws ChromatticASTTransformationException {
    for (ClassNode classNode : (List<ClassNode>) sourceUnit.getAST().getClasses()) {
      if (!classNode.isScript()) {
        for (Object methodNode : classNode.getMethods()) System.out.println(methodNode);
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
    try {
      constructor.setPrivateDefaultConstructor(classNode);
    } catch (DefaultConstructorNotFound e) {
      constructor.generatePrivateDefaultConstructor(classNode);
    }
    constructor.generatePublicHandlerConstructor(classNode);

    // Browse children to adapt groovy structure
    for (FieldNode fieldNode : classNode.getFields()) {
      if (GroovyUtils.isChromatticAnnoted(fieldNode)) {

        //
        try {
          annotationMover.addSetterDelegationAnnotation(classNode, fieldNode);
        } catch (SetterNoSuchException e) {
          annotationMover.generateSetter(classNode, fieldNode);
        }
      }
      for (AnnotationNode annotationNode : (List<AnnotationNode>) fieldNode.getAnnotations()) {
        if (annotationNode.getClassNode().getName().startsWith(GroovyUtils.ANNOTATIONS_PACKAGE)) {
          fieldChecker.checkChromaticFieldType(fieldNode);

          //
          try {
            annotationMover.addFieldAnnotationToMethod(classNode, fieldNode, annotationNode);
          } catch (GetterNoSuchException e) {
            annotationMover.generateGetter(classNode, fieldNode, annotationNode);
          }
        }
      }
      annotationMover.removeChromatticAnnotation(fieldNode);
    }

    // Transform GroovyObject to ChromatticObject
    delegate.setGroovyInterceptable(classNode);
    delegate.addInvokerField(classNode);
    /*try {
      delegate.plugGetProperty(classNode);
    } catch (NoSuchMethodException e) {
      delegate.generateGetProperty(classNode);
    }
    try {
      delegate.plugSetProperty(classNode);
    } catch (NoSuchMethodException e) {
      delegate.generateSetProperty(classNode);
    }
    try {
      delegate.plugInvokeMethod(classNode);
    } catch (NoSuchMethodException e) {
      delegate.generateInvokeMethod(classNode);
    }*/
  }
}
