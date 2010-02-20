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

package org.chromattic.core.api;

import org.chromattic.api.BuilderException;
import org.chromattic.common.ObjectInstantiator;
import org.chromattic.common.jcr.Path;
import org.chromattic.common.jcr.PathException;
import org.chromattic.spi.instrument.Instrumentor;
import org.chromattic.spi.jcr.SessionLifeCycle;
import org.chromattic.core.Domain;
import org.chromattic.metamodel.mapping.NodeTypeMapping;
import org.chromattic.metamodel.mapping.TypeMappingBuilder;
import org.chromattic.api.Chromattic;
import org.chromattic.api.ChromatticBuilder;
import org.chromattic.api.format.ObjectFormatter;
import org.reflext.api.*;
import org.reflext.jlr.JavaLangReflectTypeModel;
import org.reflext.jlr.JavaLangReflectMethodModel;
import org.reflext.core.TypeDomain;

import java.util.*;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ChromatticBuilderImpl extends ChromatticBuilder {


  public ChromatticBuilderImpl() {
  }

  private <T> T create(Option.Instance<String> optionInstance, Class<T> expectedClass) {
    String s = optionInstance.getValue();
    return ObjectInstantiator.newInstance(s, expectedClass);
  }

  @Override
  protected Chromattic boot(Options options, Set<Class> classes) throws BuilderException {

    TypeDomain<Type, Method> typeDomain = new TypeDomain<Type, Method>(new JavaLangReflectTypeModel(), new JavaLangReflectMethodModel());

    //
    TypeMappingBuilder mappingBuilder = new TypeMappingBuilder(true);
    Set<NodeTypeMapping> mappings = new HashSet<NodeTypeMapping>();
    for (Class clazz : classes) {
      ClassTypeInfo typeInfo = (ClassTypeInfo)typeDomain.getType(clazz);
      mappingBuilder.add(typeInfo);
    }
    mappings.addAll(mappingBuilder.build());

    //
    Boolean optimizeJCREnabled = options.getValue(JCR_OPTIMIZE_ENABLED);

    //
    final boolean hasPropertyOptimized;
    if (optimizeJCREnabled != null) {
      hasPropertyOptimized = optimizeJCREnabled;
    } else {
      hasPropertyOptimized = options.getValue(JCR_OPTIMIZE_HAS_PROPERTY_ENABLED);
    }

    //
    final boolean hasNodeOptimized;
    if (optimizeJCREnabled != null) {
      hasNodeOptimized = optimizeJCREnabled;
    } else {
      hasNodeOptimized = options.getValue(JCR_OPTIMIZE_HAS_NODE_ENABLED);
    }

    //
    boolean stateCacheEnabled = options.getValue(CACHE_STATE_ENABLED);

    //
    String rootNodePath;
    try {
      rootNodePath = Path.normalizeAbsolutePath(options.getValue(ROOT_NODE_PATH));
    }
    catch (PathException e) {
      throw new BuilderException("Root node path must be valid");
    }

    //
    int rootCreateMode;
    boolean createRootNode = options.getValue(CREATE_ROOT_NODE);
    if (createRootNode) {
      boolean lazyCreateMode = options.getValue(LAZY_CREATE_ROOT_NODE);
      if (lazyCreateMode) {
        rootCreateMode = Domain.LAZY_CREATE_MODE;
      } else {
        rootCreateMode = Domain.CREATE_MODE;
      }
    } else {
      rootCreateMode = Domain.NO_CREATE;
    }

    //
    Instrumentor instrumentor = create(options.getInstance(INSTRUMENTOR_CLASSNAME), Instrumentor.class);

    //
    ObjectFormatter objectFormatter = create(options.getInstance(OBJECT_FORMATTER_CLASSNAME), ObjectFormatter.class);

    //
    SessionLifeCycle sessionLifeCycle = create(options.getInstance(SESSION_LIFECYCLE_CLASSNAME), SessionLifeCycle.class);
    
    // Build domain
    Domain domain = new Domain(
      mappings,
      instrumentor,
      objectFormatter,
      stateCacheEnabled,
      hasPropertyOptimized,
      hasNodeOptimized,
      rootNodePath,
      rootCreateMode);

    //
    return new ChromatticImpl(domain, sessionLifeCycle);
  }
}