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

import org.chromattic.groovy.exceptions.NoSuchDefaultConstructor;
import org.chromattic.spi.instrument.MethodHandler;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;

import java.lang.reflect.Modifier;
import java.util.List;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class ChromatticConstructor {
  public void setProtectedDefaultConstructor(ClassNode classNode) throws NoSuchDefaultConstructor {
    for(ConstructorNode constructorNode : (List<ConstructorNode>) classNode.getDeclaredConstructors()) {
      if (constructorNode.getParameters().length == 0)
        constructorNode.setModifiers(Modifier.PROTECTED);
      return;
    }
    throw new NoSuchDefaultConstructor("No default constructor found for the class : " + classNode.getName());
  }

  public void generateProtectedDefaultConstructor(ClassNode classNode) {
    classNode.addConstructor(
      Modifier.PROTECTED
      , new Parameter[]{}
      , new ClassNode[]{}
      , new EmptyStatement()
    );
  }

  public void generatePublicHandlerConstructor(ClassNode classNode) {
    classNode.addConstructor(
      Modifier.PUBLIC
      , new Parameter[]{ new Parameter(new ClassNode(MethodHandler.class), "chromatticInvoker") }
      , new ClassNode[]{}
      , new BlockStatement(
         new Statement[]{
           new ExpressionStatement(new ConstructorCallExpression(classNode, new ArgumentListExpression(new Expression[]{})))
           , new ExpressionStatement(new BinaryExpression(new PropertyExpression(new VariableExpression("this"), "chromatticInvoker"), Token.newSymbol(Types.EQUAL, 0, 0), new VariableExpression("chromatticInvoker")))
         }
         , new VariableScope()
      )
    );
  }
}
