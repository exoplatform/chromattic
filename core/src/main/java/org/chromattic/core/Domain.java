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
import org.chromattic.core.jcr.SessionWrapper;
import org.chromattic.spi.instrument.Instrumentor;
import org.chromattic.api.format.ObjectFormatter;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class Domain {

  /** . */
  private static final ConcurrentHashMap<SessionWrapper, DomainSessionImpl> sessionMapping = new ConcurrentHashMap<SessionWrapper, DomainSessionImpl>();

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

  public Domain(
    Set<TypeMapping> typeMappings,
    Instrumentor instrumentor,
    ObjectFormatter objectFormatter,
    boolean stateCacheEnabled) {

    //
    TypeMapperBuilder builder = new TypeMapperBuilder(typeMappings, instrumentor);

    //
    Map<String, TypeMapper> typeMapperByNodeType = new HashMap<String, TypeMapper>();
    Map<Class<?>, TypeMapper> typeMapperByClass = new HashMap<Class<?>, TypeMapper>();
    for (TypeMapper typeMapper : builder.build()) {
      if (typeMapperByNodeType.containsKey(typeMapper.getNodeDef().getPrimaryNodeTypeName())) {
        throw new IllegalStateException("Duplicate node type name " + typeMapper.getNodeDef().getPrimaryNodeTypeName());
      }
      typeMapperByNodeType.put(typeMapper.getNodeDef().getPrimaryNodeTypeName(), typeMapper);
      typeMapperByClass.put(typeMapper.getObjectClass(), typeMapper);
    }

    //
    this.typeMapperByClass = typeMapperByClass;
    this.typeMapperByNodeType = typeMapperByNodeType;
    this.instrumentor = instrumentor;
    this.objectFormatter = objectFormatter;
    this.stateCacheEnabled = stateCacheEnabled;
  }

  public DomainSession getSession(SessionWrapper jcrSession) {
    if (jcrSession == null) {
      throw new NullPointerException();
    }

    // Integrate

    //
    DomainSessionImpl  session = sessionMapping.get(jcrSession);
    if (session == null) {
      session = new DomainSessionImpl(this, jcrSession);
      DomainSessionImpl phantomSession = sessionMapping.put(jcrSession, session);
      if (phantomSession != null) {
        session = phantomSession;
      }
    }
    return session;
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
}