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

import groovy.lang.GroovyInterceptable;
import org.chromattic.spi.instrument.MethodHandler;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;

import java.lang.reflect.Modifier;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class ChromatticDelegate {

  public void setGroovyInterceptable(ClassNode classNode) {
    for (ClassNode interfaceNode : classNode.getInterfaces())
      if (interfaceNode.getTypeClass().equals(GroovyInterceptable.class)) return;

    //
    classNode.addInterface(new ClassNode(GroovyInterceptable.class));
  }

  public void addInvokerField(ClassNode classNode) {
    GroovyUtils.createGetter(
      classNode
      , classNode.addField(
          "chromatticInvoker"
          , Modifier.PRIVATE
          , new ClassNode(MethodHandler.class)
          , new ConstantExpression(null)
        )
    );
  }

  public void plugGetProperty(ClassNode classNode) throws NoSuchMethodException {
    MethodNode methodNode = classNode.getMethod("getProperty", new Parameter[]{new Parameter(ClassHelper.STRING_TYPE, "p")});
    if (methodNode == null) throw new NoSuchMethodException();

    //
    methodNode.setCode(
      new BlockStatement(
        new Statement[] {
          new ReturnStatement(
            new StaticMethodCallExpression(
              new ClassNode(ChromatticGroovyInvocation.class)
              , "getProperty"
              , new ArgumentListExpression(
                  new Expression[] {
                    new VariableExpression("this")
                    , new VariableExpression("p")
                    , new FieldExpression(classNode.getField("chromatticInvoker"))
                  }
              )
            )
          )
        }
        ,new VariableScope()
      )
    );
  }

  public void plugSetProperty(ClassNode classNode) throws NoSuchMethodException {
    MethodNode methodNode = classNode.getMethod(
      "setProperty"
      , new Parameter[] {
          new Parameter(ClassHelper.STRING_TYPE, "p")
          , new Parameter(ClassHelper.OBJECT_TYPE, "v")
      }
    );
    if (methodNode == null) throw new NoSuchMethodException();

    //
    methodNode.setCode(
      new BlockStatement(
        new Statement[] {
          new ExpressionStatement(
            new StaticMethodCallExpression(
              new ClassNode(ChromatticGroovyInvocation.class)
              , "setProperty"
              , new ArgumentListExpression(
                  new Expression[] {
                    new VariableExpression("this")
                    , new VariableExpression("p")
                    , new VariableExpression("v")
                    , new FieldExpression(classNode.getField("chromatticInvoker"))
                  }
              )
            )
          )
        }
        ,new VariableScope()
      )
    );
  }

  public void plugInvokeMethod(ClassNode classNode) throws NoSuchMethodException {
    MethodNode methodNode = classNode.getMethod(
      "invokeMethod"
      , new Parameter[] {
          new Parameter(ClassHelper.STRING_TYPE, "m")
          , new Parameter(ClassHelper.OBJECT_TYPE, "p")
      }
    );
    if (methodNode == null) throw new NoSuchMethodException();

    //
    methodNode.setCode(
      new BlockStatement(
        new Statement[] {
          new ExpressionStatement(
            new StaticMethodCallExpression(
              new ClassNode(ChromatticGroovyInvocation.class)
              , "invokeMethod"
              , new ArgumentListExpression(
                  new Expression[] {
                    new VariableExpression("this")
                    , new VariableExpression("m")
                    , new VariableExpression("p")
                    , new FieldExpression(classNode.getField("chromatticInvoker"))
                  }
              )
            )
          )
        }
        ,new VariableScope()
      )
    );
  }

  public void generateGetProperty(ClassNode classNode) {
    classNode.addMethod(
      "getProperty"
      , Modifier.PUBLIC
      , ClassHelper.OBJECT_TYPE
      , new Parameter[] { new Parameter(ClassHelper.STRING_TYPE, "p") }
      , new ClassNode[] {}
      , new EmptyStatement()
    );
    try {
      plugGetProperty(classNode);
    } catch (NoSuchMethodException ignore) { }
  }

  public void generateSetProperty(ClassNode classNode) {
    classNode.addMethod(
      "setProperty"
      , Modifier.PUBLIC
      , ClassHelper.VOID_TYPE
      , new Parameter[] { new Parameter(ClassHelper.STRING_TYPE, "p"), new Parameter(ClassHelper.OBJECT_TYPE, "v") }
      , new ClassNode[] {}
      , new EmptyStatement()
    );
    try {
      plugSetProperty(classNode);
    } catch (NoSuchMethodException ignore) { }
  }

  public void generateInvokeMethod(ClassNode classNode) {
    classNode.addMethod(
      "invokeMethod"
      , Modifier.PUBLIC
      , ClassHelper.OBJECT_TYPE
      , new Parameter[] { new Parameter(ClassHelper.STRING_TYPE, "m"), new Parameter(ClassHelper.OBJECT_TYPE, "p") }
      , new ClassNode[] {}
      , new EmptyStatement()
    );
    try {
      plugInvokeMethod(classNode);
    } catch (NoSuchMethodException ignore) { }
  }
  
}