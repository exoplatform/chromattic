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

import org.chromattic.api.annotations.SetterDelegation;
import org.chromattic.groovy.exceptions.GetterDoNotExistException;
import org.chromattic.groovy.exceptions.SetterDoNotExistException;
import org.codehaus.groovy.ast.*;

import java.lang.reflect.Modifier;
import java.util.Iterator;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class ChromatticAnnotationMover {
  
  public void addFieldAnnotationToMethod(ClassNode classNode, FieldNode fieldNode, AnnotationNode annotationNode) throws GetterDoNotExistException {
    MethodNode getterNode = GroovyUtils.getGetter(classNode, fieldNode);
    if (getterNode == null) throw new GetterDoNotExistException("Cannot apply chromattic annotations because getter don't exist for : " + fieldNode.getName());
    getterNode.addAnnotation(annotationNode);
  }

  public void addSetterDelegationAnnotation(ClassNode classNode, FieldNode fieldNode) throws SetterDoNotExistException {
    MethodNode setterNode = GroovyUtils.getSetter(classNode, fieldNode);
    if (setterNode == null) throw new SetterDoNotExistException("Cannot apply annotation @SetterDelegation because setter don't exist for : " + fieldNode.getName());
    setterNode.addAnnotation(new AnnotationNode(new ClassNode(SetterDelegation.class)));
  }

  public void generateGetter(ClassNode classNode, FieldNode fieldNode) {
      GroovyUtils.createGetter(classNode, fieldNode);
  }

  public void generateGetter(ClassNode classNode, FieldNode fieldNode, AnnotationNode defaultAnnotatedNode) {
    generateGetter(classNode, fieldNode);
    try {
      addFieldAnnotationToMethod(classNode, fieldNode, defaultAnnotatedNode);
    } catch (GetterDoNotExistException ignore) { }
  }

  public void generateSetter(ClassNode classNode, FieldNode fieldNode) {
    GroovyUtils.createSetter(classNode, fieldNode);
    try {
      addSetterDelegationAnnotation(classNode, fieldNode);
    } catch (SetterDoNotExistException ignore) { }
  }

  public void removeChromatticAnnotation(FieldNode fieldNode) {
    Iterator<AnnotationNode> it = fieldNode.getAnnotations().iterator();
    while(it.hasNext())
      if (it.next().getClassNode().getName().startsWith(GroovyUtils.ANNOTATIONS_PACKAGE))
        it.remove();
  }
}