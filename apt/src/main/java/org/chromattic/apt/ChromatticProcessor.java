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
import org.reflext.api.ClassIntrospector;
import org.reflext.api.MethodInfo;
import org.reflext.api.TypeInfo;
import org.reflext.api.ParameterizedTypeInfo;
import org.reflext.api.VoidTypeInfo;
import org.reflext.api.TypeVariableInfo;
import org.reflext.api.SimpleTypeInfo;
import org.reflext.api.ArrayTypeInfo;
import org.reflext.api.ClassKind;
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
        writeClass(roundEnv, out, cti);
        out.close();
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }

    }

    //
    return true;
  }

  private void writeClass(RoundEnvironment roundEnv, PrintWriter out, ClassTypeInfo cti) {
    String simpleClassName = cti.getSimpleName() + "_Chromattic";
    out.write("package " + cti.getPackageName() + ";\n");
    out.write("import " + Invoker.class.getName() + ";\n");

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
    out.write(sb.toString());

    appendContructor(roundEnv, out, cti);
    appendAbstractMethods(simpleClassName, roundEnv, out, cti);
    out.write("}\n");
  }

  private void appendContructor(RoundEnvironment roundEnv, PrintWriter out, ClassTypeInfo cti) {
    out.println("public final " + MethodHandler.class.getName() + " handler;");
    out.println("public " + cti.getSimpleName() + "_Chromattic(" + MethodHandler.class.getName() + " handler) {");
    out.println("this.handler = handler;");
    out.println("}");
  }

  private Iterable<MethodInfo> getMethodsToImplement(ClassTypeInfo cti) {
    List<MethodInfo> methods = new ArrayList<MethodInfo>();
    ClassIntrospector introspector = new ClassIntrospector(cti);
    for (MethodInfo method : introspector.getMethods()) {
      if (method.isAbstract()) {
        methods.add(method);
      }
    }
    return methods;
  }

  private String getCast(ClassTypeInfo ctx, TypeInfo ti) {
    if (ti instanceof ClassTypeInfo) {
      if (ti instanceof SimpleTypeInfo) {
        switch (((SimpleTypeInfo)ti).getLiteralType()) {
          case INT:
            return "java.lang.Integer";
          case BOOLEAN:
            return "java.lang.Boolean";
          case LONG:
            return "java.lang.Long";
          case DOUBLE:
            return "java.lang.Double";
          case FLOAT:
            return "java.lang.Float";
          default:
            throw new UnsupportedOperationException();
        }
      }
      else {
        return ((ClassTypeInfo)ti).getName();
      }
    }
    else if (ti instanceof ParameterizedTypeInfo) {
      ParameterizedTypeInfo pti = (ParameterizedTypeInfo)ti;
      TypeInfo rawType = pti.getRawType();
      StringBuffer sb = new StringBuffer(getCast(ctx, rawType));
      sb.append('<');
      List<TypeInfo> arguments = pti.getTypeArguments();
      for (int i = 0; i < arguments.size(); i++) {
        TypeInfo argument = arguments.get(i);
        if (i > 0) {
          sb.append(',');
        }
        sb.append(getCast(ctx, argument));
      }
      sb.append('>');
      return sb.toString();
    }
    else if (ti instanceof TypeVariableInfo) {
      TypeVariableInfo tvi = (TypeVariableInfo)ti;
      return getDeclaration(ctx, ctx.resolve(tvi));
    }
    else if (ti instanceof ArrayTypeInfo) {
      ArrayTypeInfo ati = (ArrayTypeInfo)ti;
      return getDeclaration(ctx, ctx.resolve(ati.getComponentType())) + "[]";
    }
    else {
      throw new UnsupportedOperationException("Cannot handle declaration of " + ti);
    }
  }

  private String getDeclaration(ClassTypeInfo ctx, TypeInfo ti) {
    if (ti instanceof ClassTypeInfo) {
      if (ti instanceof VoidTypeInfo) {
        return "void";
      }
      else {
        return ((ClassTypeInfo)ti).getName();
      }
    }
    else if (ti instanceof ParameterizedTypeInfo) {
      ParameterizedTypeInfo pti = (ParameterizedTypeInfo)ti;
      TypeInfo rawType = pti.getRawType();
      StringBuffer sb = new StringBuffer(getDeclaration(ctx, rawType));
      sb.append('<');
      List<TypeInfo> arguments = pti.getTypeArguments();
      for (int i = 0; i < arguments.size(); i++) {
        TypeInfo argument = arguments.get(i);
        if (i > 0) {
          sb.append(',');
        }
        sb.append(getDeclaration(ctx, argument));
      }
      sb.append('>');
      return sb.toString();
    }
    else if (ti instanceof TypeVariableInfo) {
      TypeVariableInfo tvi = (TypeVariableInfo)ti;
      return getDeclaration(ctx, ctx.resolve(tvi));
    }
    else if (ti instanceof ArrayTypeInfo) {
      ArrayTypeInfo ati = (ArrayTypeInfo)ti;
      return getDeclaration(ctx, ctx.resolve(ati.getComponentType())) + "[]";
    }
    else {
      throw new UnsupportedOperationException("Cannot handle declaration of " + ti);
    }
  }

  private String getLiteral(ClassTypeInfo ctx, TypeInfo ti) {
    if (ti instanceof ClassTypeInfo) {
      if (ti instanceof VoidTypeInfo) {
        return "void";
      }
      else {
        return ((ClassTypeInfo)ti).getName();
      }
    }
    else if (ti instanceof ParameterizedTypeInfo) {
      ParameterizedTypeInfo pti = (ParameterizedTypeInfo)ti;
      TypeInfo rawType = pti.getRawType();
      return getDeclaration(ctx, rawType);
    }
    else if (ti instanceof TypeVariableInfo) {
      TypeVariableInfo tvi = (TypeVariableInfo)ti;
      return getDeclaration(ctx, ctx.resolve(tvi));
    }
    else if (ti instanceof ArrayTypeInfo) {
      ArrayTypeInfo ati = (ArrayTypeInfo)ti;
      return getDeclaration(ctx, ctx.resolve(ati.getComponentType())) + "[]";
    }
    else {
      throw new UnsupportedOperationException("Cannot handle declaration of " + ti);
    }
  }

  private void appendAbstractMethods(String simpleClassName, RoundEnvironment roundEnv, PrintWriter out, ClassTypeInfo cti) {


    //
    int id = 0;

    //
    Iterable<MethodInfo> methods = getMethodsToImplement(cti);

    for (MethodInfo method : methods) {

      String methodId = "method_" + id++;
      String methodName = method.getName();
      List<TypeInfo> parameterTypes = method.getParameterTypes();
      TypeInfo rti = method.getReturnType();
      String returnDeclaration = getDeclaration(cti, rti);

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
      out.write("private static final " + Invoker.class.getSimpleName() + " " + methodId + " = " + Invoker.class.getSimpleName() + ".getDeclaredMethod(" + simpleClassName + ".class,");
      out.write('"' + methodName + '"');
      for (TypeInfo parameterType : parameterTypes) {
        out.write(",");
        out.write(getLiteral(cti, parameterType));
        out.write(".class");
      }
      out.write(");\n");

      //
      out.write(scope + " " + returnDeclaration + " " + methodName + "(");
      StringBuffer sb1 = new StringBuffer("Object[] args = new Object[]{");
      for (int i = 0; i < parameterTypes.size(); i++) {
        TypeInfo parameterType = parameterTypes.get(i);
        String parameterDeclaration = getDeclaration(cti, parameterType);
        if (i > 0) {
          out.write(",");
          sb1.append(",");
        }
        out.write(parameterDeclaration + " arg_" + i);
        sb1.append("arg_").append(i);
      }
      sb1.append("};\n");

      out.write(") {\n");

      out.write(sb1.toString());

      if (rti instanceof VoidTypeInfo) {
        out.write(methodId + ".invoke(handler, this, args);");
      }
      else {
        out.write("return (" + getCast(cti, rti) + ")" + methodId + ".invoke(handler, this, args);");
      }

      out.write("}\n");

    }
  }
}