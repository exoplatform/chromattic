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
import org.chromattic.groovy.exceptions.InvokeMethodDoNotExistException;
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

  /**
   * Add MOP method if needed
   * @param classNode
   */
  public void addChromatticInvokeMethod(ClassNode classNode) {

    //System.out.println("[CHROMATTIC] Add invokeMethod");
    // generate :
    //   public Object invokeMethod(String m, Object p) {
    //     return this.class.getMethod(m, (Class<?>[]) p.collect { it.class }).invoke(this, p);
    //   }
    
    classNode.addMethod(
      "invokeMethod"
      , Modifier.PUBLIC
      , new ClassNode(Object.class)
      , new Parameter[] {
          new Parameter(new ClassNode(String.class), "m")
          , new Parameter(new ClassNode(Object.class), "p")
        }
      , new ClassNode[] {}
      , new ReturnStatement(
          new MethodCallExpression(
            new MethodCallExpression(
              new VariableExpression("class")
              , "getMethod"
              , new ArgumentListExpression(
                  new Expression[] {
                    new VariableExpression("m")
                    , new CastExpression(
                        new ClassNode(Class[].class)
                        , new MethodCallExpression(
                            new VariableExpression("p")
                            , "collect"
                            , createArrayToClassArrayClosure()
                        )
                      )
                  }
              )
            )
            , new ConstantExpression("invoke")
            , new ArgumentListExpression(
                new Expression[] {
                  new VariableExpression("this")
                  , new VariableExpression("p")
                }
              )
          )
      )
    );

    try {
      plugChromatticDelegation(classNode);
    } catch (InvokeMethodDoNotExistException ignore) { }
  }

  public void plugChromatticDelegation(ClassNode classNode) throws InvokeMethodDoNotExistException {
    MethodNode methodNode = getInvokeMethod(classNode);
    if (methodNode == null) throw new InvokeMethodDoNotExistException("MOP method don't exist");
    methodNode.setCode(
      new BlockStatement(
        new Statement[] {
          createChromatticDelegation()
          , methodNode.getCode()
        }
        , methodNode.getVariableScope()
      )
    );
  }

  public void addInvokerField(ClassNode classNode) {
    classNode.addField(
      "chromatticInvoker"
      , Modifier.PRIVATE
      , new ClassNode(MethodHandler.class)
      , new ConstantExpression(null)
    );
  }

  private Statement createChromatticDelegation() {
    // generate :
    //   if (this.class.getMethod(m, (Class<?>[]) p.collect { it.class }).getAnnotations().any {it.toString().startsWith(GroovyUtils.ANNOTATIONS_PACKAGE, 1)})
    //     return chromatticInvoker.invoke(this, m, p);
    return new IfStatement(
      new BooleanExpression(
        new MethodCallExpression(
          new MethodCallExpression(
            new MethodCallExpression(
              new VariableExpression("class")
              , "getMethod"
              , new ArgumentListExpression(
                  new Expression[] {
                    new VariableExpression("m")
                    , new CastExpression(
                        new ClassNode(Class[].class)
                        , new MethodCallExpression(
                            new VariableExpression("p")
                            , "collect"
                            , createArrayToClassArrayClosure()
                        )
                      )
                  }
              )
            )
            , new ConstantExpression("getAnnotations")
            , new ArgumentListExpression(new Expression[]{})
          )
          , "any"
          , createChromatticAnnotationPresentClosure() // Closure
        )
      )
      , new ReturnStatement(
          new MethodCallExpression(
            new VariableExpression("chromatticInvoker")
            , "invoke"
            , new ArgumentListExpression(
                new Expression[] {
                  new VariableExpression("this")
                  , new MethodCallExpression(
                      new VariableExpression("class")
                      , "getMethod"
                      , new ArgumentListExpression(
                          new Expression[] {
                            new VariableExpression("m")
                            , new CastExpression(
                                new ClassNode(Class[].class)
                                , new MethodCallExpression(
                                    new VariableExpression("p")
                                    , "collect"
                                    , createArrayToClassArrayClosure()
                                )
                              )
                          }
                      )
                    )
                  , new VariableExpression("p")
                }
            )
          )
      )
      , new EmptyStatement()
    );
  }

  private MethodNode getInvokeMethod(ClassNode classNode) {
    return classNode.getMethod(
      "invokeMethod"
      , new Parameter[] {
        new Parameter(new ClassNode(String.class), "m")
        , new Parameter(new ClassNode(Object.class), "p")
      }
    );
  }

  private ClosureExpression createArrayToClassArrayClosure() {
    ClosureExpression closureExpression =
      new ClosureExpression (
        new Parameter[] {}
        , new ExpressionStatement(new PropertyExpression(new VariableExpression("it"), "class"))
      );
    closureExpression.setVariableScope(new VariableScope());
    return closureExpression;
  }

  private ClosureExpression createChromatticAnnotationPresentClosure() {
    ClosureExpression closureExpression =
      new ClosureExpression (
        new Parameter[] {}
        , new ExpressionStatement(
            new MethodCallExpression(
              new MethodCallExpression(
                new VariableExpression("it")
                , "toString"
                , new ArgumentListExpression(new Expression[]{})
              )
              , "startsWith"
              , new ArgumentListExpression(new Expression[]{
                  new ConstantExpression(GroovyUtils.ANNOTATIONS_PACKAGE)
                  , new ConstantExpression(1)
              })
            )
          )
      );
    closureExpression.setVariableScope(new VariableScope());
    return closureExpression;
  }
  
}