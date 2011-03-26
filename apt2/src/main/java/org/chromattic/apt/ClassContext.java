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
package org.chromattic.apt;

import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.jvm.ClassReader;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ClassContext {

  /** . */
  private final JCTree.JCClassDecl classDecl;

  /** . */
  private final Map<Name, JCTree.JCVariableDecl> fieldDecls;

  /** . */
  private final Set<JCTree.JCMethodDecl> methodDecls;

  /** . */
  private final TreeMaker treeMaker;

  /** . */
  private final Name.Table nameTable;

  /** . */
  private final Symtab symtab;

  /** . */
  private final ClassReader reader;

  public ClassContext(Context context, JCTree.JCClassDecl classDecl) {

    //
    Map<Name, JCTree.JCVariableDecl> fieldDecls = new HashMap<Name, JCTree.JCVariableDecl>();
    Set<JCTree.JCMethodDecl> methods = new HashSet<JCTree.JCMethodDecl>();
    for (JCTree node : classDecl.defs) {
      if (node instanceof JCTree.JCVariableDecl) {
        JCTree.JCVariableDecl variableDecl = (JCTree.JCVariableDecl)node;
        fieldDecls.put(variableDecl.name, variableDecl);
      } else if (node instanceof JCTree.JCMethodDecl) {
        JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl)node;
        methods.add(methodDecl);
      }
    }

    //
    this.fieldDecls = fieldDecls;
    this.methodDecls = methods;
    this.classDecl = classDecl;
    this.treeMaker = TreeMaker.instance(context);
    this.nameTable = Name.Table.instance(context);
    this.symtab = Symtab.instance(context);
    this.reader = ClassReader.instance(context);
  }

  /** . */
  private final TreeTranslator translator = new TreeTranslator() {

    @Override
    public void visitSelect(JCTree.JCFieldAccess fieldAccess) {

      System.out.println("<field-access name='" + fieldAccess.name + "' selected='" + fieldAccess.selected.getClass().getName() + "'>");

      super.visitSelect(fieldAccess);

      System.out.println("</field-access>");

    }

    @Override
    public void visitIdent(JCTree.JCIdent jcIdent) {
      System.out.println("<ident name='" + jcIdent.name + "'>");

      super.visitIdent(jcIdent);    //To change body of overridden methods use File | Settings | File Templates.

      System.out.println("</ident>");
    }
  };

  public void process() {
    classDecl.accept(translator);
  }

/*
  public void process() {

    //
    for (JCTree.JCMethodDecl methodDecl : methodDecls) {
      process(methodDecl);
    }

    //
    for (JCTree.JCVariableDecl fieldDecl : fieldDecls.values()) {

      //
      String variableName = fieldDecl.name.toString();

      // Detyped getter
      JCTree.JCMethodDecl readPropertyDecl;
      {
        JCTree.JCStatement st0 = treeMaker.Return(treeMaker.Ident(fieldDecl));
        List<JCTree.JCStatement> statements = List.of(st0);
        JCTree.JCBlock methodBody = treeMaker.Block(0, statements);
        Name methodName = nameTable.fromString("_read_" + variableName);
        List<JCTree.JCTypeParameter> methodGenericParams = List.nil();
        List<JCTree.JCVariableDecl> parameters = List.nil();
        List<JCTree.JCExpression> throwsClauses = List.nil();
        JCTree.JCExpression methodType = fieldDecl.vartype;
        readPropertyDecl = treeMaker.MethodDef(
          treeMaker.Modifiers(Flags.PUBLIC, List.<JCTree.JCAnnotation>nil()),
          methodName,
          methodType,
          methodGenericParams,
          parameters,
          throwsClauses,
          methodBody,
          null
        );
      }
      classDecl.defs = classDecl.defs.append(readPropertyDecl);
      
      // Detyped setter
      JCTree.JCMethodDecl writePropertyDecl;
      {
        JCTree.JCStatement st0 = treeMaker.Exec(treeMaker.Assign(
          treeMaker.Select(treeMaker.Ident(nameTable.fromString("this")), fieldDecl.name),
          treeMaker.Ident(nameTable.fromString("value"))));
        JCTree.JCStatement st1 = treeMaker.Return(treeMaker.Ident(fieldDecl));
        List<JCTree.JCStatement> statements = List.of(st0, st1);
        JCTree.JCBlock methodBody = treeMaker.Block(0, statements);
        Name methodName = nameTable.fromString("_write_" + variableName);
        List<JCTree.JCTypeParameter> methodGenericParams = List.nil();
        JCTree.JCVariableDecl param = treeMaker.VarDef(
          treeMaker.Modifiers(Flags.FINAL),
          nameTable.fromString("value"),
          fieldDecl.vartype,
          null);
        List<JCTree.JCVariableDecl> parameters = List.of(param);
        List<JCTree.JCExpression> throwsClauses = List.nil();
        JCTree.JCExpression methodType = fieldDecl.vartype;
        writePropertyDecl = treeMaker.MethodDef(
          treeMaker.Modifiers(Flags.PUBLIC, List.<JCTree.JCAnnotation>nil()),
          methodName,
          methodType,
          methodGenericParams,
          parameters,
          throwsClauses,
          methodBody,
          null
        );
      }
      classDecl.defs = classDecl.defs.append(writePropertyDecl);
    }
  }
*/

/*
  private void process(JCTree.JCMethodDecl methodDecl) {
    for (JCTree.JCStatement statement : methodDecl.body.getStatements()) {
      process(statement);
    }
  }

  private void process(JCTree.JCExpression expression) {
    switch (expression.getKind()) {
      case IDENTIFIER:
      {
        JCTree.JCExpression.JCIdent ident = (JCTree.JCIdent)expression;
        if (fieldDecls.containsKey(ident.name)) {
          // Replace with a read
          

        }
        break;
      }
      case ASSIGNMENT:
      {
        JCTree.JCExpression.JCAssign assign = (JCTree.JCAssign)expression;
        if (assign.lhs instanceof JCTree.JCFieldAccess) {
          JCTree.JCFieldAccess fieldAccess = (JCTree.JCFieldAccess)assign.lhs;
          if (fieldAccess.selected instanceof JCTree.JCIdent) {
            JCTree.JCIdent ident = (JCTree.JCIdent)fieldAccess.selected;
            if (ident.getName().contentEquals("this")) {
              throw new UnsupportedOperationException();
            } else {
              throw new UnsupportedOperationException();
            }
          } else {
            throw new UnsupportedOperationException();
          }
        } else {
          throw new UnsupportedOperationException();
        }
//        break;
      }
      case STRING_LITERAL:
        break;
      case PLUS:
        JCTree.JCExpression.JCBinary binary = (JCTree.JCBinary)expression;
        process(binary.getLeftOperand());
        process(binary.getRightOperand());
        break;
      case METHOD_INVOCATION:
        JCTree.JCExpression.JCMethodInvocation invocation = (JCTree.JCMethodInvocation)expression;
        for (JCTree.JCExpression argumentExpression : invocation.getArguments()) {
          process(argumentExpression);
        }
        break;
      default:
        throw new UnsupportedOperationException("Cannot handle expression " + expression.getClass().getName() + " with kind " + expression.getKind());
    }
  }

  private void process(JCTree.JCStatement statement) {
    switch (statement.getKind()) {
      case EXPRESSION_STATEMENT:
        process(((JCTree.JCExpressionStatement)statement).expr);
        break;
      default:
        throw new UnsupportedOperationException("Cannot handle statement " + statement);
    }
  }
*/

}
