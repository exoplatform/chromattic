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
import org.chromattic.common.jcr.Path;
import org.chromattic.common.jcr.PathException;
import org.chromattic.core.mapper.ObjectMapper;
import org.chromattic.metamodel.mapping.NodeTypeMapping;
import org.chromattic.core.mapper.MapperBuilder;
import org.chromattic.core.jcr.info.NodeInfoManager;
import org.chromattic.core.query.QueryManager;
import org.chromattic.spi.instrument.Instrumentor;
import org.chromattic.api.format.ObjectFormatter;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.*;
import org.chromattic.common.collection.Collections;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class Domain {

  /** . */
  public static int LAZY_CREATE_MODE = 0;

  /** . */
  public static int CREATE_MODE = 1;

  /** . */
  public static int NO_CREATE = 2;

  /** . */
  private static final Set<Integer> CREATE_MODES = Collections.set(LAZY_CREATE_MODE, CREATE_MODE, NO_CREATE);

  /** . */
  private final Map<String, ObjectMapper> typeMapperByNodeType;

  /** . */
  private final Map<Class<?>, ObjectMapper> typeMapperByClass;

  /** . */
  private final Instrumentor instrumentor;
  
  /** . */
  final ObjectFormatter objectFormatter;

  /** . */
  final boolean stateCacheEnabled;

  /** . */
  final boolean hasPropertyOptimized;
  /** . */

  final boolean hasNodeOptimized;

  /** . */
  final String rootNodePath;

  /** . */
  final List<String> rootNodePathSegments;

  /** . */
  final int rootCreateMode;

  /** . */
  final NodeInfoManager nodeInfoManager;

  /** . */
  final QueryManager queryManager;

  public Domain(
    Set<NodeTypeMapping> typeMappings,
    Instrumentor instrumentor,
    ObjectFormatter objectFormatter,
    boolean stateCacheEnabled,
    boolean hasPropertyOptimized,
    boolean hasNodeOptimized,
    String rootNodePath,
    int rootCreateMode) {

    //
    MapperBuilder builder = new MapperBuilder(typeMappings, instrumentor);

    //
    if (!CREATE_MODES.contains(rootCreateMode)) {
      throw new IllegalArgumentException("Invalid create mode " + rootCreateMode);
    }

    //
    Map<String, ObjectMapper> typeMapperByNodeType = new HashMap<String, ObjectMapper>();
    Map<Class<?>, ObjectMapper> typeMapperByClass = new HashMap<Class<?>, ObjectMapper>();
    for (ObjectMapper typeMapper : builder.build()) {
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
    this.instrumentor = instrumentor;
    this.objectFormatter = objectFormatter;
    this.stateCacheEnabled = stateCacheEnabled;
    this.hasPropertyOptimized = hasPropertyOptimized;
    this.hasNodeOptimized = hasNodeOptimized;
    this.rootNodePath = rootNodePath;
    this.rootNodePathSegments = rootNodePathSegments;
    this.nodeInfoManager = new NodeInfoManager();
    this.queryManager = new QueryManager(rootNodePath);
    this.rootCreateMode = rootCreateMode;
  }

  public ObjectFormatter getObjectFormatter() {
    return objectFormatter;
  }

  public boolean isHasPropertyOptimized() {
    return hasPropertyOptimized;
  }

  public boolean isHasNodeOptimized() {
    return hasNodeOptimized;
  }

  public Instrumentor getInstrumentor() {
    return instrumentor;
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

  /**
   * Encodes the name for the specified context.
   *
   * @param owner the entity context
   * @param external the external name
   * @param nameKind the name kind
   * @return the encoded name
   */
  public String encodeName(EntityContext owner, String external, FORMATTING_MODE nameKind) {
    if (external == null) {
      throw new NullPointerException("No null name accepted");
    }
    if (nameKind == FORMATTING_MODE.VALIDATE_PROPERTY_NAME) {
      return external;
    }

    //
    ObjectFormatter formatter = null;
    if (owner != null) {
      formatter = owner.mapper.getFormatter();
    }

    //
    if (formatter == null) {
      formatter = objectFormatter;
    }

    //
    String internal = null;
    try {
      switch (nameKind) {
        case VALIDATE_OBJECT_NAME:
          internal = external;
          break;
        case VALIDATE_PROPERTY_NAME:
          throw new UnsupportedOperationException();
        case CONVERT_OBJECT_NAME:
          internal = formatter.encodeNodeName(null, external);
          break;
      }
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
      throw new IllegalArgumentException("Name " + external + " was converted to null");
    }
    Path.validateName(internal);
    return internal;
  }

  /**
   * Decodes an internal name that is owned by the specified context.
   *
   * @param owner the entity context
   * @param internal the internal name
   * @param nameKind the kind of name
   * @return the external name or null
   * @throws RepositoryException any repository exception
   */
  public String decodeName(EntityContext owner, String internal, FORMATTING_MODE nameKind) throws RepositoryException {
    ObjectMapper mapper = null;
    if (owner != null) {
      mapper = owner.mapper;
    }
    return decodeName(mapper, internal, nameKind);
  }

  /**
   * Decodes an internal name that is owned by the specified context.
   *
   * @param owner the node owner
   * @param internal the internal name
   * @param nameKind the kind of name
   * @return the external name or null
   * @throws RepositoryException any repository exception
   */
  public String decodeName(Node owner, String internal, FORMATTING_MODE nameKind) throws RepositoryException {
    if (owner == null) {
      throw new NullPointerException();
    }

    //
    String nodeTypeName = owner.getPrimaryNodeType().getName();
    ObjectMapper parentMapper = getTypeMapper(nodeTypeName);
    return decodeName(parentMapper, internal, nameKind);
  }

  private String decodeName(ObjectMapper owner, String internal, FORMATTING_MODE nameKind) throws RepositoryException {
    if (nameKind == FORMATTING_MODE.VALIDATE_PROPERTY_NAME) {
      return internal;
    }

    //
    ObjectFormatter formatter = null;
    if (owner != null) {
      formatter = owner.getFormatter();
    }
    if (formatter == null) {
      formatter = objectFormatter;
    }

    //
    String external = null;
    try {
      switch (nameKind) {
        case VALIDATE_OBJECT_NAME:
          external = internal;
          break;
        case VALIDATE_PROPERTY_NAME:
          throw new UnsupportedOperationException();
        case CONVERT_OBJECT_NAME:
          external = formatter.decodeNodeName(null, internal);
          break;
      }
    }
    catch (Exception e) {
      if (e instanceof IllegalStateException) {
        throw (IllegalStateException)e;
      }
      throw new UndeclaredThrowableException(e);
    }
    if (external == null) {
      if (nameKind == FORMATTING_MODE.CONVERT_OBJECT_NAME) {
        throw new IllegalStateException();
      }
    }
    return external;
  }
}