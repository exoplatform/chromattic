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

package org.chromattic.core;

import org.chromattic.api.BuilderException;
import org.chromattic.api.format.DefaultObjectFormatter;
import org.chromattic.common.ObjectInstantiator;
import org.chromattic.common.jcr.Path;
import org.chromattic.common.jcr.PathException;
import org.chromattic.core.mapper.ObjectMapper;
import org.chromattic.core.jcr.type.TypeManager;
import org.chromattic.core.query.QueryManager;
import org.chromattic.metamodel.mapping.BeanMapping;
import org.chromattic.spi.instrument.Instrumentor;
import org.chromattic.api.format.ObjectFormatter;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.lang.annotation.Annotation;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chromattic.common.collection.Collections;
import org.chromattic.spi.instrument.MethodHandler;
import org.chromattic.spi.instrument.ProxyType;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class Domain {

  /** . */
  private static final ProxyType<?> NULL_PROXY_TYPE = new ProxyType<Object>() {
    public Object createProxy(MethodHandler handler) {
      throw new UnsupportedOperationException("Cannot create proxy for " + handler);
    }

    public MethodHandler getInvoker(Object proxy) {
      return null;
    }

    public Class<?> getType() {
      throw new UnsupportedOperationException("Cannot get proxy type for NULL_PROXY_TYPE");
    }
  };

  /** . */
  public static int LAZY_CREATE_MODE = 0;

  /** . */
  public static int CREATE_MODE = 1;

  /** . */
  public static int NO_CREATE_MODE = 2;

  /** . */
  private static final Set<Integer> CREATE_MODES = Collections.set(LAZY_CREATE_MODE, CREATE_MODE, NO_CREATE_MODE);

  /** . */
  private final Map<String, ObjectMapper> typeMapperByNodeType;

  /** . */
  private final Map<Class<?>, ObjectMapper> typeMapperByClass;

  /** . */
  private final Instrumentor  defaultInstrumentor;

  /** . */
  private final Map<Class<?>, ProxyType<?>> proxyTypeMap;

  /** . */
  private final Map<Class<?>, ProxyType<?>> chromatticTypeMap;

  /** . */
  final ObjectFormatter objectFormatter;

  /** . */
  final boolean propertyCacheEnabled;

  /** . */
  final boolean propertyLoadGroupEnabled;

  /** . */
  final boolean hasPropertyOptimized;
  /** . */

  final boolean hasNodeOptimized;

  /** . */
  final String rootNodePath;

  /** . */
  final List<String> rootNodePathSegments;

  /** . */
  final String rootNodeType;

  /** . */
  final int rootCreateMode;

  /** . */
  final TypeManager nodeInfoManager;

  /** . */
  final QueryManager queryManager;

  public Domain(
    Collection<ObjectMapper<?>> mappers,
    Instrumentor defaultInstrumentor,
    ObjectFormatter objectFormatter,
    boolean propertyCacheEnabled,
    boolean propertyLoadGroupEnabled,
    boolean hasPropertyOptimized,
    boolean hasNodeOptimized,
    String rootNodePath,
    int rootCreateMode,
    String rootNodeType) {

    //
    if (!CREATE_MODES.contains(rootCreateMode)) {
      throw new IllegalArgumentException("Invalid create mode " + rootCreateMode);
    }
    
    //
    Map<Class<?>, ProxyType<?>> proxyClassToProxyType = new HashMap<Class<?>, ProxyType<?>>();
    Map<Class<?>, ProxyType<?>> chromatticClassToProxyType = new HashMap<Class<?>, ProxyType<?>>();
    mapping: for (ObjectMapper<?> mapper : mappers) {
      BeanMapping beanMapping = mapper.getMapping();
      Class<?> clazz = (Class<?>)beanMapping.getBean().getClassType().unwrap();
      for (Annotation annotation : clazz.getAnnotations()) {
        if ("org.chromattic.groovy.annotations.GroovyInstrumentor".equals(annotation.annotationType().getName())) {
          Class<?> instrumentorClass = null;
          try {
            instrumentorClass = (Class<?>)annotation.annotationType().getMethod("value").invoke(annotation);
          } catch (Exception ignore) {}
          Instrumentor i = ObjectInstantiator.newInstance(instrumentorClass.getName(), Instrumentor.class);
          ProxyType<?> proxyType = i.getProxyType(clazz);
          proxyClassToProxyType.put(i.getProxyType(clazz).getType(), proxyType);
          chromatticClassToProxyType.put(clazz, proxyType);
          continue mapping;
        }
      }
      if (Object.class.equals(clazz)) {
        proxyClassToProxyType.put(clazz, NULL_PROXY_TYPE);
        chromatticClassToProxyType.put(clazz, NULL_PROXY_TYPE);
      } else {
        ProxyType<?> proxyType = defaultInstrumentor.getProxyType(clazz);
        proxyClassToProxyType.put(proxyType.getType(), proxyType);
        chromatticClassToProxyType.put(clazz, proxyType);
      }
    }

    //
    Map<String, ObjectMapper> typeMapperByNodeType = new HashMap<String, ObjectMapper>();
    Map<Class<?>, ObjectMapper> typeMapperByClass = new HashMap<Class<?>, ObjectMapper>();
    for (ObjectMapper typeMapper : mappers) {
      if (typeMapperByNodeType.containsKey(typeMapper.getNodeTypeName())) {
        throw new IllegalStateException("Duplicate node type name " + typeMapper);
      }
      typeMapperByNodeType.put(typeMapper.getNodeTypeName(), typeMapper);
      typeMapperByClass.put(typeMapper.getObjectClass(), typeMapper);
    }

    //
    final List<String> rootNodePathSegments;
    try {
      rootNodePathSegments = Path.splitAbsolutePath(Path.normalizeAbsolutePath(rootNodePath));
    }
    catch (PathException e) {
      throw new BuilderException("Root node path must be valid");
    }

    //
    this.typeMapperByClass = typeMapperByClass;
    this.typeMapperByNodeType = typeMapperByNodeType;
    this.defaultInstrumentor = defaultInstrumentor;
    this.objectFormatter = objectFormatter;
    this.propertyCacheEnabled = propertyCacheEnabled;
    this.propertyLoadGroupEnabled = propertyLoadGroupEnabled;
    this.hasPropertyOptimized = hasPropertyOptimized;
    this.hasNodeOptimized = hasNodeOptimized;
    this.rootNodePath = rootNodePath;
    this.rootNodePathSegments = rootNodePathSegments;
    this.nodeInfoManager = new TypeManager();
    this.queryManager = new QueryManager(rootNodePath);
    this.rootCreateMode = rootCreateMode;
    this.rootNodeType = rootNodeType;
    this.proxyTypeMap = proxyClassToProxyType;
    this.chromatticTypeMap = chromatticClassToProxyType;
  }

  public boolean isHasPropertyOptimized() {
    return hasPropertyOptimized;
  }

  public boolean isHasNodeOptimized() {
    return hasNodeOptimized;
  }

  public MethodHandler getHandler(Object o) {
    ProxyType<?> instrumentor = proxyTypeMap.get(o.getClass());
    return instrumentor != null ? instrumentor.getInvoker(o) : null;
  }

  public <O> ProxyType<O> getProxyType(Class<O> type) {
    return (ProxyType<O>)chromatticTypeMap.get(type);
  }

  public ObjectMapper getTypeMapper(String nodeTypeName) {
    return typeMapperByNodeType.get(nodeTypeName);
  }

  public ObjectMapper getTypeMapper(Class<?> clazz) {
    return typeMapperByClass.get(clazz);
  }

  public QueryManager getQueryManager() {
    return queryManager;
  }

  String decodeName(Node ownerNode, String internal) throws
    NullPointerException, UndeclaredThrowableException, IllegalStateException, RepositoryException {
    if (ownerNode == null) {
      throw new NullPointerException();
    }
    String nodeTypeName = ownerNode.getPrimaryNodeType().getName();
    ObjectMapper ownerMapper = getTypeMapper(nodeTypeName);
    ObjectFormatter formatter = null;
    if (ownerMapper != null) {
      formatter = ownerMapper.getFormatter();
    } else {
      formatter = objectFormatter;
    }
    return decodeName(formatter, internal);
  }

  public static String decodeName(ObjectFormatter formatter, String internal) throws
    UndeclaredThrowableException, IllegalStateException, RepositoryException {
    if (formatter == null) {
      formatter = DefaultObjectFormatter.getInstance();
    }

    //
    String external;
    try {
      external = formatter.decodeNodeName(null, internal);
    }
    catch (Exception e) {
      if (e instanceof IllegalStateException) {
        throw (IllegalStateException)e;
      }
      throw new UndeclaredThrowableException(e);
    }
    if (external == null) {
      throw new IllegalStateException();
    }
    return external;
  }

  /**
   * Encodes the name for the specified context.
   *
   * @param ownerNode the node
   * @param externalName the external name
   * @return the encoded name
   * @throws NullPointerException if the owner context argument is null
   * @throws UndeclaredThrowableException when the formatter throws an exception
   * @throws RepositoryException any repository exception
   */
  String encodeName(Node ownerNode, String externalName) throws
    NullPointerException, UndeclaredThrowableException, RepositoryException {
    if (ownerNode == null) {
      throw new NullPointerException();
    }
    String nodeTypeName = ownerNode.getPrimaryNodeType().getName();
    ObjectMapper ownerMapper = getTypeMapper(nodeTypeName);
    ObjectFormatter formatter;
    if (ownerMapper != null) {
      formatter = ownerMapper.getFormatter();
    } else {
      formatter = objectFormatter;
    }
    return encodeName(formatter, externalName);
  }

  public static String encodeName(ObjectFormatter formatter, String externalName) throws
    UndeclaredThrowableException, NullPointerException {
    if (externalName == null) {
      throw new NullPointerException("No null name accepted");
    }
    if (formatter == null) {
      formatter = DefaultObjectFormatter.getInstance();
    }

    //
    String internal;
    try {
      internal = formatter.encodeNodeName(null, externalName);
    }
    catch (Exception e) {
      if (e instanceof NullPointerException) {
        throw (NullPointerException)e;
      }
      if (e instanceof IllegalArgumentException) {
        throw (IllegalArgumentException)e;
      }
      throw new UndeclaredThrowableException(e);
    }
    if (internal == null) {
      throw new IllegalArgumentException("Name " + externalName + " was converted to null");
    }
    Path.validateLocalName(internal);
    return internal;
  }
}