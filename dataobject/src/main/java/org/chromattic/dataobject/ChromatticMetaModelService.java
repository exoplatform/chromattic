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

package org.chromattic.dataobject;

import org.chromattic.metamodel.typegen.CNDNodeTypeSerializer;
import org.chromattic.metamodel.typegen.NodeType;
import org.chromattic.metamodel.typegen.NodeTypeSerializer;
import org.chromattic.metamodel.typegen.SchemaBuilder;
import org.chromattic.metamodel.typegen.XMLNodeTypeSerializer;
import org.exoplatform.services.jcr.ext.resource.UnifiedNodeReference;
import org.exoplatform.services.jcr.ext.script.groovy.JcrGroovyCompiler;
import org.exoplatform.services.jcr.ext.script.groovy.JcrGroovyResourceLoader;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.TypeResolver;
import org.reflext.core.TypeResolverImpl;
import org.reflext.jlr.JavaLangReflectReflectionModel;

import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ChromatticMetaModelService {

  public ChromatticMetaModelService() {
  }

  public void start() {
  }

  public void stop() {
  }

  public String generateNodeTypes(
    NodeTypeFormat format,
    String repository,
    String workspace,
    String path,
    String... doPaths) throws Exception {

    Map<String,  NodeType> doNodeTypes = generateNodeTypes(repository, workspace, path, doPaths);

    //
    NodeTypeSerializer serializer;
    switch (format) {
      case EXO:
        serializer = new XMLNodeTypeSerializer();
        break;
      case CND:
        serializer = new CNDNodeTypeSerializer();
        break;
      default:
        throw new AssertionError();
    }

    //
    for (NodeType nodeType : doNodeTypes.values()) {
      serializer.addNodeType(nodeType);
    }

    //
    StringWriter writer = new StringWriter();
    serializer.writeTo(writer);
    return writer.toString();
  }

  public Map<String, NodeType> generateNodeTypes(
    String repository,
    String workspace,
    String path,
    String... doPaths) throws Exception {

    // Generate classes
    Map<String, Class<?>> classes = generateClasses(repository, workspace, path, doPaths);

    // Generate class types
    TypeResolver<Type> domain = TypeResolverImpl.create(JavaLangReflectReflectionModel.getInstance());
    Map<ClassTypeInfo, String> doClassTypes = new HashMap<ClassTypeInfo, String>();
    for (Map.Entry<String, Class<?>> entry : classes.entrySet()) {
      doClassTypes.put((ClassTypeInfo)domain.resolve(entry.getValue()), entry.getKey());
    }

    // Generate bean mappings
    Map<String, NodeType> doNodeTypes = new HashMap<String, NodeType>();
    for (Map.Entry<ClassTypeInfo,  NodeType> entry : new SchemaBuilder().build(doClassTypes.keySet()).entrySet()) {
      ClassTypeInfo doClassType = entry.getKey();
      NodeType doNodeType = entry.getValue();
      String doPath = doClassTypes.get(doClassType);
      doNodeTypes.put(doPath, doNodeType);
    }

    //
    return doNodeTypes;
  }

  public Map<String, Class<?>> generateClasses(
    String repository,
    String workspace,
    String path,
    String... doPath) throws Exception {

    // Build the classloader url
    URL url = new URL("jcr://" + repository + "/" + workspace + "#" + path);

    //
    JcrGroovyCompiler compiler = new JcrGroovyCompiler();
    compiler.getGroovyClassLoader().setResourceLoader(new JcrGroovyResourceLoader(new java.net.URL[]{url}));

    //
    UnifiedNodeReference[] doRefs = new UnifiedNodeReference[doPath.length];
    for  (int i = 0;i < doPath.length;i++) {
      doRefs[i] = new UnifiedNodeReference(repository, workspace, doPath[i]);
    }

    // Compile to classes
    Class[] classes = compiler.compile(doRefs);
    Map<String, Class<?>> doClasses = new HashMap<String, Class<?>>();
    for (int i = 0;i< doPath.length;i++) {
      doClasses.put(doPath[i], classes[i]);
    }

    //
    return doClasses;
  }
}
