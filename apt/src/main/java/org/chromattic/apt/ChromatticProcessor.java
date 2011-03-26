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

import org.chromattic.api.annotations.NodeMapping;
import org.chromattic.spi.instrument.MethodHandler;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.MethodInfo;
import org.reflext.api.TypeInfo;
import org.reflext.api.VoidTypeInfo;
import org.reflext.api.ClassKind;
import org.reflext.api.introspection.MethodIntrospector;
import org.reflext.api.introspection.HierarchyScope;
import org.reflext.core.TypeDomain;
import org.reflext.apt.JavaxLangTypeModel;
import org.reflext.apt.JavaxLangMethodModel;

import javax.annotation.processing.SupportedSourceVersion;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.Filer;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.tools.JavaFileObject;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@SupportedSourceVersion(SourceVersion.RELEASE_5)
@SupportedAnnotationTypes("org.chromattic.api.annotations.NodeMapping")
public class ChromatticProcessor extends AbstractProcessor {

  /** . */
  private final TypeDomain<Object, ExecutableElement> domain = new TypeDomain<Object, ExecutableElement>(new JavaxLangTypeModel(), new JavaxLangMethodModel());

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

    Set<? extends Element> elts = roundEnv.getElementsAnnotatedWith(NodeMapping.class);

    for (Element elt : elts) {
      TypeElement typeElt = (TypeElement)elt;

      ClassTypeInfo cti = (ClassTypeInfo)domain.getType(typeElt);

      System.out.println("Going to create = " + cti.getName());
      Filer filer = processingEnv.getFiler();
      try {
        JavaFileObject jfo = filer.createSourceFile(typeElt.getQualifiedName() + "_Chromattic", typeElt);
        PrintWriter out = new PrintWriter(jfo.openWriter());
        StringBuilder builder = new StringBuilder();
        writeClass(roundEnv, builder, cti);
        out.write(builder.toString());
        out.close();
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }

    }

    //
    return true;
  }

  private void writeClass(RoundEnvironment roundEnv, StringBuilder out, ClassTypeInfo cti) {
    String simpleClassName = cti.getSimpleName() + "_Chromattic";
    out.append("package ").append(cti.getPackageName()).append(";\n");
    out.append("import ").append(Invoker.class.getName()).append(";\n");

    //
    StringBuffer sb = new StringBuffer("public class ");
    sb.append(simpleClassName);
    sb.append(" extends ");
    if (cti.getKind() == ClassKind.INTERFACE) {
      sb.append(Object.class.getName());
      sb.append(" implements ");
      sb.append(cti.getSimpleName());
    }
    else {
      sb.append(cti.getSimpleName());
    }
    sb.append(" {\n");

    //
    out.append(sb.toString());

    appendContructor(roundEnv, out, cti);
    appendAbstractMethods(simpleClassName, roundEnv, out, cti);
    out.append("}\n");
  }

  private void appendContructor(RoundEnvironment roundEnv, StringBuilder out, ClassTypeInfo cti) {
    out.append("public final ").append(MethodHandler.class.getName()).append(" handler;\n");
    out.append("public ").append(cti.getSimpleName()).append("_Chromattic(").append(MethodHandler.class.getName()).append(" handler) {\n");
    out.append("this.handler = handler;\n");
    out.append("}\n");
  }

  private Iterable<MethodInfo> getMethodsToImplement(ClassTypeInfo cti) {
    List<MethodInfo> methods = new ArrayList<MethodInfo>();
    MethodIntrospector introspector = new MethodIntrospector(HierarchyScope.ALL, true);
    for (MethodInfo method : introspector.getMethods(cti)) {
      if (method.isAbstract()) {
        methods.add(method);
      }
    }
    return methods;
  }

  private void appendAbstractMethods(String simpleClassName, RoundEnvironment roundEnv, StringBuilder out, ClassTypeInfo cti) {


    //
    int id = 0;

    //
    Iterable<MethodInfo> methods = getMethodsToImplement(cti);

    for (MethodInfo method : methods) {

      String methodId = "method_" + id++;
      String methodName = method.getName();
      List<TypeInfo> parameterTypes = method.getParameterTypes();
      TypeInfo rti = method.getReturnType();

      //
      String scope;
      switch (method.getAccess()) {
        case PACKAGE_PROTECTED:
          scope = "";
          break;
        case PROTECTED:
          scope = "protected";
          break;
        case PUBLIC:
          scope = "public";
          break;
        default:
          throw new AssertionError();
      }

      //
      out.append("private static final ").
          append(Invoker.class.getSimpleName()).
          append(" ").append(methodId).append(" = ").
          append(Invoker.class.getSimpleName()).
          append(".getDeclaredMethod(").
          append(method.getOwner().getName()).
          append(".class,").
          append('"').
          append(methodName).
          append('"');
      for (TypeInfo parameterType : parameterTypes) {
        out.append(",");
        new TypeFormatter(cti, FormatterStyle.LITERAL, out).format(parameterType);
        out.append(".class");
      }
      out.append(");\n");

      //
      out.append(scope).append(" ");
      new TypeFormatter(cti, FormatterStyle.RETURN_TYPE, out).format(rti);
      out.append(" ").append(methodName).append("(");

      //
      StringBuffer sb1 = new StringBuffer("Object[] args = new Object[]{");
      for (int i = 0; i < parameterTypes.size(); i++) {
        TypeInfo parameterType = parameterTypes.get(i);
        if (i > 0) {
          out.append(",");
          sb1.append(",");
        }
        new TypeFormatter(cti, FormatterStyle.TYPE_PARAMETER, out).format(parameterType);
        out.append(" arg_").append(i);
        sb1.append("arg_").append(i);
      }
      sb1.append("};\n");

      out.append(") {\n");

      out.append(sb1.toString());

      if (rti instanceof VoidTypeInfo) {
        out.append(methodId).append(".invoke(handler, this, args);");
      }
      else {
        out.append("return (");
        new TypeFormatter(cti, FormatterStyle.CAST, out).format(rti);
        out.append(")").append(methodId).append(".invoke(handler, this, args);");
      }

      out.append("}\n");
    }
  }
}