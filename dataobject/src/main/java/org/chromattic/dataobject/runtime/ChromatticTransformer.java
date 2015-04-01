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

package org.chromattic.dataobject.runtime;

import org.chromattic.common.logging.Logger;
import org.chromattic.groovy.ChromatticDelegate;
import org.chromattic.groovy.GroovyUtils;
import org.chromattic.groovy.exceptions.NoSuchSetterException;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

import javax.inject.Inject;
import java.util.List;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
@GroovyASTTransformation(phase= CompilePhase.INSTRUCTION_SELECTION)
public class ChromatticTransformer implements ASTTransformation {
  private static final Logger log = Logger.getLogger(ChromatticTransformer.class);
  private final ChromatticDelegate delegate = new ChromatticDelegate();

  public void visit(final ASTNode[] nodes, final SourceUnit source) {

    List<ClassNode> classes = (List<ClassNode>)source.getAST().getClasses();

    //
    for (ClassNode classNode : classes) {
      for (FieldNode fieldNode : classNode.getFields()) {
        if (isInjected(fieldNode)) {
          if (GroovyUtils.getSetter(classNode, fieldNode) == null) {
            GroovyUtils.createSetter(classNode, fieldNode);
          }
          try
          {
            delegate.plugInjector(fieldNode, new ClassNode(ChromatticInjector.class));
          }
          catch (NoSuchSetterException ignore){ log.error(ignore.getMessage(),ignore);}
        }
      }
    }
  }

  private boolean isInjected(FieldNode fieldNode) {
    for (AnnotationNode annotationNode : (List<AnnotationNode>) fieldNode.getAnnotations()) {
      if (annotationNode.getClassNode().equals(new ClassNode(Inject.class))) {
        return true;
      }
    }
    return false;
  }
}
