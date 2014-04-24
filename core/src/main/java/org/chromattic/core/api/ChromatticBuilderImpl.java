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
import org.chromattic.core.mapper.MapperBuilder;
import org.chromattic.core.mapper.ObjectMapper;
import org.chromattic.metamodel.mapping.BeanMappingBuilder;
import org.chromattic.metamodel.mapping.BeanMapping;
import org.chromattic.metamodel.type.SimpleTypeResolver;
import org.chromattic.spi.instrument.Instrumentor;
import org.chromattic.spi.jcr.SessionLifeCycle;
import org.chromattic.core.Domain;
import org.chromattic.api.Chromattic;
import org.chromattic.api.ChromatticBuilder;
import org.chromattic.api.format.ObjectFormatter;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.TypeResolver;
import org.reflext.core.TypeResolverImpl;
import org.reflext.jlr.JavaLangReflectReflectionModel;

import java.lang.reflect.Type;
import java.util.*;

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

  /** The mappers. */
  private Collection<ObjectMapper<?>> mappers;

  @Override
  protected void init(Set<Class<?>> classes) throws BuilderException {

    // Create resolvers
    SimpleTypeResolver propertyTypeResolver = new SimpleTypeResolver();
    TypeResolver<Type> typeResolver = TypeResolverImpl.create(JavaLangReflectReflectionModel.getInstance());

    // Build mappings
    Set<ClassTypeInfo> classTypes = new HashSet<ClassTypeInfo>();
    for (Class clazz : classes) {
      ClassTypeInfo typeInfo = (ClassTypeInfo)typeResolver.resolve(clazz);
      classTypes.add(typeInfo);
    }
    Map<ClassTypeInfo, BeanMapping> beanMappings = new BeanMappingBuilder().build(classTypes);
    Collection<BeanMapping> mappings = beanMappings.values();

    // Build mappers
    MapperBuilder builder = new MapperBuilder(propertyTypeResolver);
    Collection<ObjectMapper<?>> mappers = builder.build(mappings);

    //
    this.mappers = mappers;
  }

  @Override
  protected Chromattic boot(Configuration options) throws BuilderException {

    //
    Boolean optimizeJCREnabled = options.getOptionValue(JCR_OPTIMIZE_ENABLED);

    //
    final boolean hasPropertyOptimized;
    if (optimizeJCREnabled != null) {
      hasPropertyOptimized = optimizeJCREnabled;
    } else {
      hasPropertyOptimized = options.getOptionValue(JCR_OPTIMIZE_HAS_PROPERTY_ENABLED);
    }

    //
    final boolean hasNodeOptimized;
    if (optimizeJCREnabled != null) {
      hasNodeOptimized = optimizeJCREnabled;
    } else {
      hasNodeOptimized = options.getOptionValue(JCR_OPTIMIZE_HAS_NODE_ENABLED);
    }

    //
    String rootNodePath;
    try {
      rootNodePath = Path.normalizeAbsolutePath(options.getOptionValue(ROOT_NODE_PATH));
    }
    catch (PathException e) {
      throw new BuilderException("Root node path must be valid");
    }

    //
    int rootCreateMode;
    if (options.getOptionValue(CREATE_ROOT_NODE)) {
      boolean lazyCreateMode = options.getOptionValue(LAZY_CREATE_ROOT_NODE);
      if (lazyCreateMode) {
        rootCreateMode = Domain.LAZY_CREATE_MODE;
      } else {
        rootCreateMode = Domain.CREATE_MODE;
      }
    } else {
      rootCreateMode = Domain.NO_CREATE_MODE;
    }

    //
    String rootNodeType = options.getOptionValue(ROOT_NODE_TYPE);

    //
    boolean propertyCacheEnabled = options.getOptionValue(PROPERTY_CACHE_ENABLED);
    boolean propertyLoadGroupEnabled = options.getOptionValue(PROPERTY_LOAD_GROUP_ENABLED);

    //
    ObjectFormatter objectFormatter = null;
    Option.Instance<String> formatterOptionInstance = options.getOptionInstance(OBJECT_FORMATTER_CLASSNAME);
    if (formatterOptionInstance != null) {
      objectFormatter = create(formatterOptionInstance, ObjectFormatter.class);
    }

    //
    Instrumentor instrumentor = create(options.getOptionInstance(INSTRUMENTOR_CLASSNAME), Instrumentor.class);
    SessionLifeCycle sessionLifeCycle = create(options.getOptionInstance(SESSION_LIFECYCLE_CLASSNAME), SessionLifeCycle.class);

    // Update formatter on mappers if needed
    Collection<ObjectMapper<?>> mappers;
    if (objectFormatter != null) {
      ArrayList<ObjectMapper<?>> list = new ArrayList<ObjectMapper<?>>(this.mappers);
      for (int i = 0;i < list.size();i++) {
        ObjectMapper<?> mapper = list.get(i);
        if (mapper.getFormatter() == null) {
          list.set(i, mapper.with(objectFormatter));
        }
      }
      mappers = list;
    } else {
      mappers = this.mappers;
    }

    // Build domain
    Domain domain = new Domain(
      mappers,
      instrumentor,
      objectFormatter,
      propertyCacheEnabled,
      propertyLoadGroupEnabled,
      hasPropertyOptimized,
      hasNodeOptimized,
      rootNodePath,
      rootCreateMode,
      rootNodeType);

    //
    return new ChromatticImpl(domain, sessionLifeCycle);
  }
}