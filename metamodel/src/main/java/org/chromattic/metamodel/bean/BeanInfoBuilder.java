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

package org.chromattic.metamodel.bean;

import org.chromattic.metamodel.type.SimpleTypeMapping;
import org.chromattic.metamodel.type.SimpleTypeResolver;
import org.reflext.api.*;
import org.reflext.api.introspection.MethodIntrospector;
import org.reflext.api.visit.HierarchyVisitor;
import org.reflext.api.visit.HierarchyVisitorStrategy;

import java.util.*;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class BeanInfoBuilder {

  /** . */
//  private final Logger log = Logger.getLogger(BeanInfoBuilder.class);

  /** . */
  private final SimpleTypeResolver simpleTypeResolver;

  /** . */
  private final BeanFilter filter;

  public BeanInfoBuilder() {
    this((BeanFilter)null);
  }

  public BeanInfoBuilder(SimpleTypeResolver simpleTypeResolver) {
    this(simpleTypeResolver, null);
  }

  public BeanInfoBuilder(SimpleTypeResolver simpleTypeResolver, BeanFilter filter) {
    this.simpleTypeResolver = simpleTypeResolver;
    this.filter = filter;
  }

  public BeanInfoBuilder(BeanFilter filter) {
    this(new SimpleTypeResolver(), filter);
  }

  public Map<ClassTypeInfo, BeanInfo> build(ClassTypeInfo... classTypes) {
    return build(org.chromattic.common.collection.Collections.set(classTypes));
  }

  public Map<ClassTypeInfo, BeanInfo> build(Set<ClassTypeInfo> classTypes) {
    Context ctx = new Context(classTypes);
    ctx.build();
    return ctx.beans;
  }

  private class Context {

    private class BeanHierarchyVisitorStrategy<V extends HierarchyVisitor<V>> extends HierarchyVisitorStrategy<V> {

      /** . */
      private final ClassTypeInfo current;

      private BeanHierarchyVisitorStrategy(ClassTypeInfo current) {
        this.current = current;
      }

      @Override
      protected boolean accept(ClassTypeInfo type) {
        return type == current || !classTypes.contains(type);// NOSONAR
      }
    }

    /** The types to build. */
    private final Set<ClassTypeInfo> classTypes;

    /** The beans being built in a method call to the builder. */
    private final Map<ClassTypeInfo, BeanInfo> beans;

    private Context(Set<ClassTypeInfo> classTypes) {
      this.classTypes = classTypes;
      this.beans = new HashMap<ClassTypeInfo, BeanInfo>();
    }

    void build() {
      while (true) {
        Iterator<ClassTypeInfo> iterator = classTypes.iterator();
        if (iterator.hasNext()) {
          ClassTypeInfo cti = iterator.next();
          BeanInfo bean = resolve(cti);
        } else {
          break;
        }
      }
    }

    /**
     * Resolve the bean object from the specified class type. The returned bean
     * is in correct state. However it can trigger recursive resolving while the current
     * bean is in an incorrect state, i.e not finished to be fully constructed.
     *
     * To ensure the fact that we have a unique bean created per class type we must relax
     * the fact that objects are created in one step, i.e have:
     *
     * <ol>
     * <li>Instantiate bean</li>
     * <li>Make bean available for lookup</li>
     * <li>Terminate bean initialization</li>
     * </ol>
     *
     * Note: it could be possible to use future object to build the full state and leverage
     * multi processors.
     *
     * @param classType the bean class type
     * @return the corresponding bean instance
     */
    BeanInfo resolve(ClassTypeInfo classType) {
      BeanInfo bean = beans.get(classType);
      if (bean == null) {
        boolean accept;
        Boolean declared;

        if (classType.getKind() == ClassKind.CLASS || classType.getKind() == ClassKind.INTERFACE) {
          if (classType instanceof SimpleTypeInfo) {
            accept = false;
            declared = null;
//            BeanMappingBuilder.println("rejected simple type " + classType.getName());
          } else if (classType instanceof VoidTypeInfo) {
            accept = false;
            declared = null;
//            BeanMappingBuilder.println("rejected void " + classType.getName());
          } else {
            if (classTypes.remove(classType)) {
//              log.debug("resolved declared " + classType.getName());
              accept = true;
              declared = true;
            } else if (filter != null && filter.accept(classType)) {
              accept = true;
              declared = false;
//              BeanMappingBuilder.println("resolved transitively " + classType.getName());
            } else {
              accept = false;
              declared = null;
//              BeanMappingBuilder.println("rejected " + classType.getName());
            }
          }
        } else {
          accept = false;
          declared = null;
//          BeanMappingBuilder.println("rejected non class or non interface " + classType.getName());
        }
        if (accept) {
          bean = new BeanInfo(classType, declared);
          beans.put(classType, bean);
          build(bean);
        }
      }
      return bean;
    }

    void build(BeanInfo bean) {

      // Build parents
      for (ClassTypeInfo ancestorClassType = bean.classType.getSuperClass();ancestorClassType != null;ancestorClassType = ancestorClassType.getSuperClass()) {

        // Resolve the ancestor class type
        BeanInfo ancestorBean = resolve(ancestorClassType);

        // If the ancestor resolves as a bean then it becomes the parent bean and we are done
        if (ancestorBean != null) {
          bean.parent = ancestorBean;
          break;
        }
      }

      //
      buildProperties(bean);

      // Now resolve types references by method return type
      // this is needed for @Create for instance
      for (MethodInfo mi : bean.classType.getDeclaredMethods()) {
        TypeInfo rti = mi.getReturnType();
        if (rti instanceof ClassTypeInfo) {
          resolve((ClassTypeInfo)rti);
        }
      }
    }

    private PropertyInfo resolveProperty(BeanInfo bean, String propertyName) {

      // We may have null in case we were dealing with java.lang.Object for instance
      if (bean == null) {
        return null;
      }

      //
      if (bean.properties == null) {
        // Defensive: it means we are looking for a bean in an incorrect state
        throw new AssertionError();
      }

      //
      PropertyInfo property = bean.properties.get(propertyName);

      // Try in the parent
      if (property == null) {
        property = resolveProperty(bean.parent, propertyName);
      }

      //
      return property;
    }

    class ToBuild {
      final TypeInfo type;
      final MethodInfo getter;
      final MethodInfo setter;
      ToBuild(TypeInfo type, MethodInfo getter, MethodInfo setter) {
        this.type = type;
        this.getter = getter;
        this.setter = setter;
      }
    }

    /**
     * Build properties of a bean.
     *
     * @param bean the bean to build properties.
     */
    private void buildProperties(BeanInfo bean) {

      BeanHierarchyVisitorStrategy strategy = new BeanHierarchyVisitorStrategy(bean.classType);
      MethodIntrospector introspector = new MethodIntrospector(strategy, true);
      Map<String, MethodInfo> getterMap = introspector.getGetterMap(bean.classType);
      Map<String, Set<MethodInfo>> setterMap = introspector.getSetterMap(bean.classType);

      // Gather all properties on the bean
      Map<String, ToBuild> toBuilds = new HashMap<String,ToBuild>();
      for (Map.Entry<String, MethodInfo> getterEntry : getterMap.entrySet()) {
        String name = getterEntry.getKey();
        MethodInfo getter = getterEntry.getValue();
        TypeInfo getterTypeInfo = getter.getReturnType();

        //
        ToBuild toBuild = null;
        Set<MethodInfo> setters = setterMap.get(name);
        if (setters != null) {
          for (MethodInfo setter : setters) {
            TypeInfo setterTypeInfo = setter.getParameterTypes().get(0);
            if (getterTypeInfo.equals(setterTypeInfo)) {
              toBuild = new ToBuild(getterTypeInfo, getter, setter);
              break;
            }
          }
        }

        //
        if (toBuild == null) {
          toBuild = new ToBuild(getterTypeInfo, getter, null);
        }

        //
        if (toBuild != null) {
          toBuilds.put(name, toBuild);
        }
      }

      //
      setterMap.keySet().removeAll(toBuilds.keySet());
      for (Map.Entry<String, Set<MethodInfo>> setterEntry : setterMap.entrySet()) {
        String name = setterEntry.getKey();
        for (MethodInfo setter : setterEntry.getValue()) {
          TypeInfo setterTypeInfo = setter.getParameterTypes().get(0);
          toBuilds.put(name, new ToBuild(setterTypeInfo, null, setter));
        }
      }

      // Now we have all the info to build each property correctly
      Map<String, PropertyInfo<?, ?>> properties = new HashMap<String, PropertyInfo<?, ?>>();
      for (Map.Entry<String, ToBuild> toBuildEntry : toBuilds.entrySet()) {

        // Get parent property if any
        PropertyInfo parentProperty = resolveProperty(bean.parent, toBuildEntry.getKey());

        //
        TypeInfo type = toBuildEntry.getValue().type;

        // First resolve as much as we can
        TypeInfo resolvedType = bean.classType.resolve(type);

        //
        PropertyInfo<?, ?> property = null;

        // We could not resolve it, get the upper bound
        if (resolvedType instanceof TypeVariableInfo) {
          resolvedType = ((TypeVariableInfo)resolvedType).getBounds().get(0);
          resolvedType = bean.classType.resolve(resolvedType);
          // is it really enough ? for now it should be OK but we should check
        }

        // Now let's analyse
        if (resolvedType instanceof ParameterizedTypeInfo) {
          ParameterizedTypeInfo parameterizedType = (ParameterizedTypeInfo) resolvedType;
          TypeInfo rawType = parameterizedType.getRawType();
          if (rawType instanceof ClassTypeInfo) {
            ClassTypeInfo rawClassType = (ClassTypeInfo)rawType;
            String rawClassName = rawClassType.getName();
            final ValueKind.Multi collectionKind;
            final TypeInfo elementType;
            if (rawClassName.equals("java.util.Collection")) {
              collectionKind = ValueKind.COLLECTION;
              elementType = parameterizedType.getTypeArguments().get(0);
            } else if (rawClassName.equals("java.util.List")) {
              collectionKind = ValueKind.LIST;
              elementType = parameterizedType.getTypeArguments().get(0);
            } else if (rawClassName.equals("java.util.Map")) {
              TypeInfo keyType = parameterizedType.getTypeArguments().get(0);
              TypeInfo resolvedKeyType = bean.classType.resolve(keyType);
              if (resolvedKeyType instanceof ClassTypeInfo && resolvedKeyType.getName().equals("java.lang.String")) {
                elementType = parameterizedType.getTypeArguments().get(1);
                collectionKind = ValueKind.MAP;
              } else {
                elementType = null;
                collectionKind = null;
              }
            } else {
              elementType = null;
              collectionKind = null;
            }
            if (collectionKind != null) {
              if (elementType instanceof ParameterizedTypeInfo) {
                ParameterizedTypeInfo parameterizedElementType = (ParameterizedTypeInfo)elementType;
                TypeInfo parameterizedElementRawType = parameterizedElementType.getRawType();
                if (parameterizedElementRawType instanceof ClassTypeInfo) {
                  ClassTypeInfo parameterizedElementRawClassType = (ClassTypeInfo)parameterizedElementRawType;
                  String parameterizedElementRawClassName = parameterizedElementRawClassType.getName();
                  if (parameterizedElementRawClassName.equals("java.util.List")) {
                    TypeInfo listElementType = parameterizedElementType.getTypeArguments().get(0);
                    property = new PropertyInfo<SimpleValueInfo, ValueKind.Multi>(
                        bean,
                        parentProperty,
                        toBuildEntry.getKey(),
                        toBuildEntry.getValue().getter,
                        toBuildEntry.getValue().setter,
                        collectionKind,
                        createSimpleValueInfo(bean, listElementType, ValueKind.LIST));
                  }
                }
              } else {
                ClassTypeInfo elementClassType = bean.resolveToClass(elementType);
                if (elementClassType != null) {
                  BeanInfo relatedBean = resolve(elementClassType);
                  if (relatedBean != null) {
                    property = new PropertyInfo<BeanValueInfo, ValueKind.Multi>(
                        bean,
                        parentProperty,
                        toBuildEntry.getKey(),
                        toBuildEntry.getValue().getter,
                        toBuildEntry.getValue().setter,
                        collectionKind,
                        new BeanValueInfo(type, bean.resolveToClass(elementType), relatedBean));
                  } else {
                    if (collectionKind == ValueKind.LIST) {
                      property = new PropertyInfo<SimpleValueInfo, ValueKind.Single>(
                          bean,
                          parentProperty,
                          toBuildEntry.getKey(),
                          toBuildEntry.getValue().getter,
                          toBuildEntry.getValue().setter,
                          ValueKind.SINGLE,
                          createSimpleValueInfo(bean, elementType, collectionKind));
                    } else if (collectionKind == ValueKind.MAP) {
                      property = new PropertyInfo<SimpleValueInfo, ValueKind.Map>(
                          bean,
                          parentProperty,
                          toBuildEntry.getKey(),
                          toBuildEntry.getValue().getter,
                          toBuildEntry.getValue().setter,
                          ValueKind.MAP,
                          createSimpleValueInfo(bean, elementType, ValueKind.SINGLE));
                    }
                  }
                }
              }
            }
          }
        } else if (resolvedType instanceof ArrayTypeInfo) {
          final TypeInfo componentType = ((ArrayTypeInfo)resolvedType).getComponentType();
          if (componentType instanceof SimpleTypeInfo) {
            SimpleTypeInfo componentSimpleType = (SimpleTypeInfo)componentType;
            switch (componentSimpleType.getLiteralType()) {
              case BOOLEAN:
              case DOUBLE:
              case FLOAT:
              case LONG:
              case INT:
                property = new PropertyInfo<SimpleValueInfo, ValueKind.Single>(
                    bean,
                    parentProperty,
                    toBuildEntry.getKey(),
                    toBuildEntry.getValue().getter,
                    toBuildEntry.getValue().setter,
                    ValueKind.SINGLE,
                    createSimpleValueInfo(bean, componentType, ValueKind.ARRAY));
                break;
              default:
                break;
            }
          } else {
            property = new PropertyInfo<SimpleValueInfo, ValueKind.Single>(
                bean,
                parentProperty,
                toBuildEntry.getKey(),
                toBuildEntry.getValue().getter,
                toBuildEntry.getValue().setter,
                ValueKind.SINGLE,
                createSimpleValueInfo(bean, componentType, ValueKind.ARRAY));
          }
        } else if (resolvedType instanceof ClassTypeInfo) {
          BeanInfo related = resolve((ClassTypeInfo)resolvedType);
          if (related != null) {
            property = new PropertyInfo<BeanValueInfo, ValueKind.Single>(
                bean,
                parentProperty,
                toBuildEntry.getKey(),
                toBuildEntry.getValue().getter,
                toBuildEntry.getValue().setter,
                ValueKind.SINGLE,
                new BeanValueInfo(type, bean.resolveToClass(type), related));
          }
        }

        // Otherwise consider everything as a single valued simple value
        if (property == null) {

          property = new PropertyInfo<SimpleValueInfo, ValueKind.Single>(
              bean,
              parentProperty,
              toBuildEntry.getKey(),
              toBuildEntry.getValue().getter,
              toBuildEntry.getValue().setter,
              ValueKind.SINGLE,
              createSimpleValueInfo(bean, type, ValueKind.SINGLE));
        }

        //
        properties.put(property.getName(), property);
      }

      // Update properties
      bean.properties.putAll(properties);
    }

    private <K extends ValueKind> SimpleValueInfo createSimpleValueInfo(BeanInfo bean, TypeInfo type, K valueKind) {
      TypeInfo resolvedType = bean.getClassType().resolve(type);
      SimpleTypeMapping mapping = simpleTypeResolver.resolveType(resolvedType);
      return new SimpleValueInfo<K>(type, resolvedType, mapping, valueKind);
    }
  }
}
