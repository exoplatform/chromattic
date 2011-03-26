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

import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.jvm.ClassReader;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import org.chromattic.spi.instrument.MethodHandler;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.Set;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@SupportedSourceVersion(SourceVersion.RELEASE_5)
@SupportedAnnotationTypes("org.chromattic.apt.Aspectized")
public class ChromatticProcessor extends AbstractProcessor {

  /** . */
//  private final TypeDomain<Object, ExecutableElement> domain = new TypeDomain<Object, ExecutableElement>(new JavaxLangTypeModel(), new JavaxLangMethodModel());

  private JavacProcessingEnvironment processingEnv;

  private Trees trees;

  @Override
  public void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);    //To change body of overridden methods use File | Settings | File Templates.

    //
    this.processingEnv = (JavacProcessingEnvironment) processingEnv;
    this.trees = Trees.instance(processingEnv);
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

    Set<? extends Element> elts = roundEnv.getElementsAnnotatedWith(Aspectized.class);

    for (Element elt : elts) {
      TypeElement typeElt = (TypeElement)elt;

      //
      TreePath path = trees.getPath(typeElt);
      JCTree.JCCompilationUnit unit = (JCTree.JCCompilationUnit) path.getCompilationUnit();
      TreeMaker treeMaker = TreeMaker.instance(processingEnv.getContext());
      Name.Table nameTable = Name.Table.instance(processingEnv.getContext());
      Symtab symtab = Symtab.instance(processingEnv.getContext());
      ClassReader reader = ClassReader.instance(processingEnv.getContext());

      //
      JCTree.JCClassDecl type = null;
      for (JCTree s : unit.defs) {
        if (s instanceof JCTree.JCClassDecl) {
          type = (JCTree.JCClassDecl)s;
          break;
        }
      }

      // Find out all properties annotated

      ClassContext ctx = new ClassContext(processingEnv.getContext(), type);

      ctx.process();

      System.out.println("type = " + type);








/*
      // The method handler symbol
      Symbol.ClassSymbol handlerSymbol = reader.loadClass(nameTable.fromString("org.chromattic.spi.instrument.MethodHandler"));


      // Add the MethodHandler field
      JCTree.JCVariableDecl methodHandlerDec = treeMaker.VarDef(
        treeMaker.Modifiers(Flags.PUBLIC),
        nameTable.fromString("methodHandler"),
        treeMaker.Ident(handlerSymbol),
        null);
      type.defs = type.defs.append(methodHandlerDec);

      //
      boolean defaultCtor = false;
      for (JCTree def : type.defs) {
        if (def instanceof JCTree.JCMethodDecl) {
          JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl)def;
          if (methodDecl.getName().contentEquals("<init>") && methodDecl.params.size() == 0) {
            defaultCtor = true;
          }
        }
      }

      // Default ctor
      if (!defaultCtor) {
        // Method handler constructor
        final JCTree.JCMethodDecl ctor;
        List<JCTree.JCStatement> statements = List.nil();
        JCTree.JCBlock methodBody = treeMaker.Block(0, statements);
        Name methodName = nameTable.fromString("<init>");
        List<JCTree.JCTypeParameter> methodGenericParams = List.nil();
        List<JCTree.JCVariableDecl> parameters = List.nil();
        List<JCTree.JCExpression> throwsClauses = List.nil();
        JCTree.JCExpression methodType = treeMaker.Type(symtab.voidType);
        ctor = treeMaker.MethodDef(
          treeMaker.Modifiers(Flags.PUBLIC, List.<JCTree.JCAnnotation>nil()),
          methodName,
          methodType,
          methodGenericParams,
          parameters,
          throwsClauses,
          methodBody,
          null
        );
        type.defs = type.defs.append(ctor);
      }

      final JCTree.JCMethodDecl ctor;
      {

        Name methodName = nameTable.fromString("<init>");
        List<JCTree.JCTypeParameter> methodGenericParams = List.nil();
        JCTree.JCVariableDecl param = treeMaker.VarDef(
          treeMaker.Modifiers(Flags.FINAL),
          nameTable.fromString("value"),
          treeMaker.Ident(handlerSymbol),
          null);

        JCTree.JCStatement st1 = treeMaker.Exec(treeMaker.Assign(
          treeMaker.Select(treeMaker.Ident(nameTable.fromString("this")), nameTable.fromString("methodHandler")),
          treeMaker.Ident(nameTable.fromString("value"))));

        List<JCTree.JCStatement> statements = List.of(st1);

        JCTree.JCBlock methodBody = treeMaker.Block(0, statements);

        List<JCTree.JCVariableDecl> parameters = List.of(param);
        List<JCTree.JCExpression> throwsClauses = List.nil();
        JCTree.JCExpression methodType = treeMaker.Type(symtab.voidType);
        ctor = treeMaker.MethodDef(
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
      type.defs = type.defs.append(ctor);

      // Detyped getter
      JCTree.JCMethodDecl readPropertyDecl;
      {
        List<JCTree.JCStatement> statements = List.nil();
        JCTree.JCBlock methodBody = treeMaker.Block(0, statements);
        Name methodName = nameTable.fromString("_read_property");
        List<JCTree.JCTypeParameter> methodGenericParams = List.nil();
        JCTree.JCVariableDecl param = treeMaker.VarDef(
          treeMaker.Modifiers(Flags.FINAL),
          nameTable.fromString("value"),
          treeMaker.Ident(symtab.classes.get(nameTable.fromString("java.lang.String"))),
          null);
        List<JCTree.JCVariableDecl> parameters = List.of(param);
        List<JCTree.JCExpression> throwsClauses = List.nil();
        JCTree.JCExpression methodType = treeMaker.Type(symtab.voidType);
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
      type.defs = type.defs.append(readPropertyDecl);
*/

/*
      ClassContext ctx = new ClassContext(type);

      ctx.process();
*/

    }

    //
    return true;
  }


}