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

import org.chromattic.core.mapping.TypeMapping;
import org.chromattic.core.mapper.TypeMapper;
import org.chromattic.core.mapper.TypeMapperBuilder;
import org.chromattic.core.jcr.info.NodeInfoManager;
import org.chromattic.core.query.QueryManager;
import org.chromattic.spi.instrument.Instrumentor;
import org.chromattic.api.format.ObjectFormatter;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class Domain {

  /** . */
  private final Map<String, TypeMapper> typeMapperByNodeType;

  /** . */
  private final Map<Class<?>, TypeMapper> typeMapperByClass;

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
  final NodeInfoManager nodeInfoManager;

  /** . */
  final QueryManager queryManager;

  public Domain(
    Set<TypeMapping> typeMappings,
    Instrumentor instrumentor,
    ObjectFormatter objectFormatter,
    boolean stateCacheEnabled,
    boolean hasPropertyOptimized,
    boolean hasNodeOptimized,
    String rootNodePath) {

    //
    TypeMapperBuilder builder = new TypeMapperBuilder(typeMappings, instrumentor);

    //
    Map<String, TypeMapper> typeMapperByNodeType = new HashMap<String, TypeMapper>();
    Map<Class<?>, TypeMapper> typeMapperByClass = new HashMap<Class<?>, TypeMapper>();
    for (TypeMapper typeMapper : builder.build()) {
      if (typeMapperByNodeType.containsKey(typeMapper.getTypeName())) {
        throw new IllegalStateException("Duplicate node type name " + typeMapper);
      }
      typeMapperByNodeType.put(typeMapper.getTypeName(), typeMapper);
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
    this.queryManager = new QueryManager();
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

  public TypeMapper getTypeMapper(String nodeTypeName) {
    return typeMapperByNodeType.get(nodeTypeName);
  }

  public TypeMapper getTypeMapper(Class<?> clazz) {
    return typeMapperByClass.get(clazz);
  }

  public QueryManager getQueryManager() {
    return queryManager;
  }
}