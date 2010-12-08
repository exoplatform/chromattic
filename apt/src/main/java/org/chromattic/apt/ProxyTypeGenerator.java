/*
 * Copyright (C) 2010 eXo Platform SAS.
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

import org.chromattic.spi.instrument.MethodHandler;
import org.reflext.api.ClassKind;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.MethodInfo;
import org.reflext.api.TypeInfo;
import org.reflext.api.VoidTypeInfo;
import org.reflext.api.introspection.MethodIntrospector;
import org.reflext.api.visit.HierarchyScope;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates the proxy type implementation that is loaded by Chromattic at runtime.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class ProxyTypeGenerator {

  /** . */
  private final ClassTypeInfo type;

  ProxyTypeGenerator(ClassTypeInfo type) {
    this.type = type;
  }

  void build(StringBuilder code) {
    //
    String simpleClassName = type.getSimpleName() + "_Chromattic";
    code.append("package ").append(type.getPackageName()).append(";\n");
    code.append("import ").append(Invoker.class.getName()).append(";\n");
    code.append("import ").append(Instrumented.class.getName()).append(";\n");

    //
    code.append("public class ");
    code.append(simpleClassName);
    code.append(" extends ");
    if (type.getKind() == ClassKind.INTERFACE) {
      code.append(Object.class.getName());
      code.append(" implements ");
      code.append(type.getSimpleName());
      code.append(",");
      code.append(Instrumented.class.getSimpleName());
    }
    else {
      code.append(type.getSimpleName());
      code.append(" implements ");
      code.append(Instrumented.class.getSimpleName());
    }
    code.append(" {\n");

    //
    appendContructor(code);

    //
    appendMethods(code);

    //
    code.append("}\n");
  }

  private void appendContructor(StringBuilder code) {
    code.append("public final ").append(MethodHandler.class.getName()).append(" handler;\n");
    code.append("public ").append(type.getSimpleName()).append("_Chromattic(").append(MethodHandler.class.getName()).append(" handler) {\n");
    code.append("this.handler = handler;\n");
    code.append("}\n");
  }

  private void appendMethods(StringBuilder code) {
    //
    int id = 0;

    //
    Iterable<MethodInfo> methods = getMethodsToImplement();

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
      code.append("private static final ").
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
        code.append(",");
        new TypeFormatter(type, FormatterStyle.LITERAL, code).format(parameterType);
        code.append(".class");
      }
      code.append(");\n");

      //
      code.append(scope).append(" ");
      new TypeFormatter(type, FormatterStyle.RETURN_TYPE, code).format(rti);
      code.append(" ").append(methodName).append("(");

      //
      for (int i = 0; i < parameterTypes.size(); i++) {
        TypeInfo parameterType = parameterTypes.get(i);
        if (i > 0) {
          code.append(",");
        }
        new TypeFormatter(type, FormatterStyle.TYPE_PARAMETER, code).format(parameterType);
        code.append(" arg_").append(i);
      }
      code.append(") {\n");

      //
      code.append("Object[] args = new Object[]{");
      for (int i = 0; i < parameterTypes.size(); i++) {
        if (i > 0) {
          code.append(",");
        }
        code.append("arg_").append(i);
      }
      code.append("};\n");

      if (rti instanceof VoidTypeInfo) {
        code.append(methodId).append(".invoke(handler, this, args);");
      }
      else {
        code.append("return (");
        new TypeFormatter(type, FormatterStyle.CAST, code).format(rti);
        code.append(")").append(methodId).append(".invoke(handler, this, args);");
      }

      code.append("}\n");
    }
  }

  private Iterable<MethodInfo> getMethodsToImplement() {
    List<MethodInfo> methods = new ArrayList<MethodInfo>();
    MethodIntrospector introspector = new MethodIntrospector(HierarchyScope.ALL, true);
    for (MethodInfo method : introspector.getMethods(type)) {
      if (method.isAbstract()) {
        methods.add(method);
      }
    }
    return methods;
  }
}
