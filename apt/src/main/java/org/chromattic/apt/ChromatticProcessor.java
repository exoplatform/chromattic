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

import org.chromattic.api.annotations.NodeTypeDefs;
import org.chromattic.api.annotations.MixinType;
import org.chromattic.api.annotations.PrimaryType;
import org.chromattic.common.collection.SetMap;
import org.chromattic.metamodel.typegen.NodeType;
import org.chromattic.metamodel.typegen.NodeTypeBuilder;
import org.chromattic.metamodel.typegen.NodeTypeSerializer;
import org.chromattic.metamodel.typegen.XMLNodeTypeSerializer;
import org.chromattic.spi.instrument.MethodHandler;
import org.reflext.api.*;
import org.reflext.api.introspection.MethodIntrospector;
import org.reflext.api.visit.HierarchyScope;
import org.reflext.apt.JavaxLangReflectionModel;
import org.reflext.core.TypeResolverImpl;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.Writer;
import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@SupportedSourceVersion(SourceVersion.RELEASE_5)
@SupportedAnnotationTypes({
  "org.chromattic.api.annotations.PrimaryType",
  "org.chromattic.api.annotations.MixinType",
  "org.chromattic.api.annotations.Generate"})
public class ChromatticProcessor extends AbstractProcessor {

  /** . */
  private final TypeResolver<Object> domain = TypeResolverImpl.create(JavaxLangReflectionModel.getInstance());

  /** . */
  private ProcessingEnvironment env;

  @Override
  public void init(ProcessingEnvironment env) {

    //
    this.env = env;

    //
    super.init(env);
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    try {
      return _process(annotations, roundEnv);
    }
    catch (RuntimeException e) {
      e.printStackTrace();
      throw e;
    }
  }

  private boolean _process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

    Set<String> packageMetaData = new HashSet<String>();

    Set<? extends Element> a = roundEnv.getElementsAnnotatedWith(NodeTypeDefs.class);
    for (Element e : a)
    {
      PackageElement pkgElt = (PackageElement)e;
      String s = new StringBuilder().append(pkgElt.getQualifiedName()).toString();
      packageMetaData.add(s);
    }

    Set<Element> elts = new HashSet<Element>();
    elts.addAll(roundEnv.getElementsAnnotatedWith(PrimaryType.class));
    elts.addAll(roundEnv.getElementsAnnotatedWith(MixinType.class));

    try {
      process(roundEnv, elts, packageMetaData);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }

    return true;
  }

  private void process(
    RoundEnvironment roundEnv,
    Set<Element> elts,
    Set<String> packageMetaData) throws IOException {

    Filer filer = processingEnv.getFiler();

    NodeTypeBuilder visitor = new NodeTypeBuilder();

    SetMap<String, ClassTypeInfo> bilto = new SetMap<String, ClassTypeInfo>();

    for (Element elt : elts) {
      TypeElement typeElt = (TypeElement)elt;

      //
      ClassTypeInfo cti = (ClassTypeInfo)domain.resolve(typeElt);

      //
      for (String packageName : PackageNameIterator.with(cti.getPackageName()))
      {
        if (packageMetaData.contains(packageName)) {
          Set<ClassTypeInfo> set = bilto.get(packageName);
          set.add(cti);
          break;
        }
      }

      processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "About to process the type " + cti.getName());
      visitor.addType(cti);
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

    // Validate model
    visitor.generate();

    //
    for (String packageName : bilto.keySet()) {
      env.getMessager().printMessage(Diagnostic.Kind.NOTE, "Processing node type package " + packageName);
      List<NodeType> nodeTypes = new ArrayList<NodeType>();
      for (ClassTypeInfo cti : bilto.get(packageName)) {
        nodeTypes.add(visitor.getNodeType(cti));
      }
      FileObject file = filer.createResource(StandardLocation.SOURCE_OUTPUT, packageName, "nodetypes.xml");
      NodeTypeSerializer serializer = new XMLNodeTypeSerializer(nodeTypes);
      Writer writer = file.openWriter();
      serializer.writeTo(writer);
      writer.close();
    }
  }

  private void writeClass(RoundEnvironment roundEnv, StringBuilder out, ClassTypeInfo cti) {
    String simpleClassName = cti.getSimpleName() + "_Chromattic";
    out.append("package ").append(cti.getPackageName()).append(";\n");
    out.append("import ").append(Invoker.class.getName()).append(";\n");
    out.append("import ").append(Instrumented.class.getName()).append(";\n");

    //
    StringBuffer sb = new StringBuffer("public class ");
    sb.append(simpleClassName);
    sb.append(" extends ");
    if (cti.getKind() == ClassKind.INTERFACE) {
      sb.append(Object.class.getName());
      sb.append(" implements ");
      sb.append(cti.getSimpleName());
      sb.append(",");
      sb.append(Instrumented.class.getSimpleName());
    }
    else {
      sb.append(cti.getSimpleName());
      sb.append(" implements ");
      sb.append(Instrumented.class.getSimpleName());
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