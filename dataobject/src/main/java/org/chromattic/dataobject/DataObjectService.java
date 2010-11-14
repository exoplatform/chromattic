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

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * The data object service.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class DataObjectService {

  public DataObjectService() {
  }

  public void start() {
  }

  public void stop() {
  }

  /**
   * Generates the node types for the specified data object paths. This operation returns the schema source
   * in the specified format.
   *
   * @param format the schema output format
   * @param source the compilation source
   * @param doPaths the data object paths
   * @return the data object paths
   * @throws DataObjectException anything that would prevent data object compilation
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if any data object path is null
   */
  public String generateSchema(
    NodeTypeFormat format,
    CompilationSource source,
    String... doPaths) throws DataObjectException, NullPointerException, IllegalArgumentException {

    Map<String,  NodeType> doNodeTypes = generateSchema(source, doPaths);

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
    try {
      StringWriter writer = new StringWriter();
      serializer.writeTo(writer);
      return writer.toString();
    }
    catch (Exception e) {
      throw new DataObjectException("Unexpected io exception", e);
    }
  }

  /**
   * Generates the node types for the specified data object paths. This operations returns a map
   * with the data object path as keys and the related node type as values.
   *
   * @param source the compilation source
   * @param doPaths the data object paths
   * @return the data object paths
   * @throws DataObjectException anything that would prevent data object compilation
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if any data object path is null
   */
  public Map<String, NodeType> generateSchema(
    CompilationSource source,
    String... doPaths) throws DataObjectException, NullPointerException, IllegalArgumentException {

    // Generate classes
    Map<String, Class<?>> classes = generateClasses(source, doPaths);

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

  /**
   * Compiles the specified classes and returns a map with a data object path as key and
   * the corresponding compiled data object class.
   *
   * @param source the compilation source
   * @param doPaths the data object paths
   * @return the compiled data object classes
   * @throws DataObjectException anything that would prevent data object compilation
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if any data object path is null
   */
  public Map<String, Class<?>> generateClasses(
    CompilationSource source,
    String... doPaths) throws DataObjectException, NullPointerException, IllegalArgumentException {
    if (source == null) {
      throw new NullPointerException("No null source accepted");
    }
    for (String doPath : doPaths) {
      if (doPath == null) {
        throw new IllegalArgumentException("Data object paths must not contain a null value");
      }
    }

    // Build the classloader url
    try {
      URL url = new URL("jcr://" + source.getRepositoryRef() + "/" + source.getWorkspaceRef() + "#" + source.getPath());

      //
      JcrGroovyCompiler compiler = new JcrGroovyCompiler();
      compiler.getGroovyClassLoader().setResourceLoader(new JcrGroovyResourceLoader(new URL[]{url}));

      //
      UnifiedNodeReference[] doRefs = new UnifiedNodeReference[doPaths.length];
      for  (int i = 0;i < doPaths.length;i++) {
        doRefs[i] = new UnifiedNodeReference(source.getRepositoryRef(), source.getWorkspaceRef(), doPaths[i]);
      }

      // Compile to classes
      Class[] classes = compiler.compile(doRefs);
      Map<String, Class<?>> doClasses = new HashMap<String, Class<?>>();
      for (int i = 0;i< doPaths.length;i++) {
        doClasses.put(doPaths[i], classes[i]);
      }

      //
      return doClasses;
    }
    catch (IOException e) {
      throw new DataObjectException("Could not generate data object classes", e);
    }
  }
}
