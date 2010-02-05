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

import org.chromattic.common.JCR;
import org.chromattic.core.mapper.ObjectMapper;
import org.chromattic.core.mapping.NodeTypeMapping;
import org.chromattic.core.mapper.MapperBuilder;
import org.chromattic.core.jcr.info.NodeInfoManager;
import org.chromattic.core.query.QueryManager;
import org.chromattic.spi.instrument.Instrumentor;
import org.chromattic.api.format.ObjectFormatter;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class Domain {

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
  final boolean createRootNode;

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
    boolean createRootNode) {

    //
    MapperBuilder builder = new MapperBuilder(typeMappings, instrumentor);

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
    this.typeMapperByClass = typeMapperByClass;
    this.typeMapperByNodeType = typeMapperByNodeType;
    this.instrumentor = instrumentor;
    this.objectFormatter = objectFormatter;
    this.stateCacheEnabled = stateCacheEnabled;
    this.hasPropertyOptimized = hasPropertyOptimized;
    this.hasNodeOptimized = hasNodeOptimized;
    this.rootNodePath = rootNodePath;
    this.nodeInfoManager = new NodeInfoManager();
    this.queryManager = new QueryManager(rootNodePath);
    this.createRootNode = createRootNode;
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

  String decodeName(EntityContext ctx, String internal, NameKind nameKind) throws RepositoryException {
    if (ctx == null) {
      throw new NullPointerException();
    }
    return decodeName(ctx.state.getNode(), internal, nameKind);
  }

  /**
   * Decodes an internal name that is owned by the specified node.
   *
   * @param ownerNode the owner node
   * @param internal the internal name
   * @param nameKind the kind of name
   * @return the external name or null
   * @throws RepositoryException any repository exception
   */
  String decodeName(Node ownerNode, String internal, NameKind nameKind) throws RepositoryException {
    if (ownerNode == null) {
      throw new NullPointerException();
    }
    if (nameKind == NameKind.PROPERTY) {
      return internal;
    }

    //
    ObjectFormatter formatter = null;
    String nodeTypeName = ownerNode.getPrimaryNodeType().getName();
    ObjectMapper parentMapper = getTypeMapper(nodeTypeName);
    if (parentMapper != null) {
      formatter = parentMapper.getFormatter();
    }
    if (formatter == null) {
      formatter = objectFormatter;
    }

    //
    String external;
    try {
      if (nameKind == NameKind.OBJECT) {
        external = formatter.decodeNodeName(null, internal);
      } else {
        // external = formatter.decodePropertyName(null, internal);
        throw new UnsupportedOperationException();
      }
    }
    catch (Exception e) {
      if (e instanceof IllegalStateException) {
        throw (IllegalStateException)e;
      }
      throw new UndeclaredThrowableException(e);
    }
    if (external == null) {
      if (nameKind == NameKind.OBJECT) {
        throw new IllegalStateException();
      }
    }
    return external;
  }

  /**
   * Encodes the name for the specified context.
   *
   * @param ownerCtx the context
   * @param external the external name
   * @param nameKind the name kind
   * @return the encoded name
   */
  String encodeName(EntityContext ownerCtx, String external, NameKind nameKind) {
    if (external == null) {
      throw new NullPointerException("No null name accepted");
    }
    if (nameKind == NameKind.PROPERTY) {
      return external;
    }

    //
    ObjectFormatter formatter = null;
    if (ownerCtx != null) {
      formatter = ownerCtx.mapper.getFormatter();
    }
    if (formatter == null) {
      formatter = objectFormatter;
    }

    //
    String internal;
    try {
      if (nameKind == NameKind.OBJECT) {
        internal = formatter.encodeNodeName(null, external);
      } else {
        // internal = formatter.encodePropertyName(null, external);
        throw new UnsupportedOperationException();
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
    JCR.validateName(internal);
    return internal;
  }
}