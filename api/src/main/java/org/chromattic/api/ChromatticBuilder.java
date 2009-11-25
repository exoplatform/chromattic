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

package org.chromattic.api;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Arrays;

/**
 * The builder configures and create a Chromattic runtime.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class ChromatticBuilder {

  /**
   * The instrumentor class name for Chromattic's objects. The specified class must implement the
   * <tt>org.chromattic.spi.instrument.Intrumentor</tt> class.
   */
  public static final Option<String>  INSTRUMENTOR_CLASSNAME =
    new Option<String>(
      Option.Type.STRING,
      "org.chromattic.api.Option.instrumentor.classname",
      "intrumentor");

  /**
   * The JCR session life cycle class name. The specified class must implement the
   * <tt>org.chromattic.spi.jcr.SessionLifeCycle</tt> class.
   */
  public static final Option<String> SESSION_LIFECYCLE_CLASSNAME =
    new Option<String>(
      Option.Type.STRING,
      "org.chromattic.api.Option.session_lifecycle.classname",
      "session life cycle");

  /**
   * The object name formatter class name. The specified class must implement the
   * <tt>org.chromattic.api.format.ObjectFormatter</tt> class.
   */
  public static final Option<String> OBJECT_FORMATTER_CLASSNAME =
    new Option<String>(
      Option.Type.STRING,
      "org.chromattic.api.Option.object_formatter.classname",
      "object formatter");

  /**
   * The boolean indicating if caching is performed. When cache is enabled each session
   * maintains a cache that avoids to use the underlying JCR session. As a consequence
   * any change made directly to the JCR session will not be visible in the object domain.
   */
  public static final Option<Boolean> CACHE_STATE_ENABLED =
    new Option<Boolean>(
      Option.Type.BOOLEAN,
      "org.chromattic.api.Option.cache.state.enabled",
      "cache state enabled");

  /**
   * Enable / disable all JCR optimizations.
   */
  public static final Option<Boolean> JCR_OPTIMIZE_ENABLED =
    new Option<Boolean>(
      Option.Type.BOOLEAN,
      "org.chromattic.api.Option.optimize.jcr.enabled",
      "jcr optmisation enabled");

  /**
   * Enable / disable access to JCR has property.
   */
  public static final Option<Boolean> JCR_OPTIMIZE_HAS_PROPERTY_ENABLED =
    new Option<Boolean>(
      Option.Type.BOOLEAN,
      "org.chromattic.api.Option.optimize.jcr.has_property.enabled",
      "jcr has property optimization enabled");

  /**
   * Enable / disable access to JCR has property.
   */
  public static final Option<Boolean> JCR_OPTIMIZE_HAS_NODE_ENABLED =
    new Option<Boolean>(
      Option.Type.BOOLEAN,
      "org.chromattic.api.Option.optimize.jcr.has_node.enabled",
      "jcr has node optimization enabled");

  /**
   * The path of the root node. The default value is the path of the JCR workspace root node.
   */
  public static final Option<String> ROOT_NODE_PATH =
    new Option<String>(
      Option.Type.STRING,
      "org.chromattic.api.Option.root_node_path",
      "root node path");


  /**
   * Options configurable via system properties.
   */
  private final static Set<Option> systemOptions = Collections.unmodifiableSet(new HashSet<Option>(Arrays.asList(
    CACHE_STATE_ENABLED,
    JCR_OPTIMIZE_ENABLED,
    JCR_OPTIMIZE_HAS_PROPERTY_ENABLED,
    JCR_OPTIMIZE_HAS_NODE_ENABLED
  )));

  public static Set<Option> getSystemOptions() {
    return systemOptions;
  }

  public static ChromatticBuilder create() {
    String builderClassName = "org.chromattic.core.builder.ChromatticBuilderImpl";
    try {
      Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(builderClassName);
      if (ChromatticBuilder.class.isAssignableFrom(clazz)) {
        Class<? extends ChromatticBuilder> builderClass = clazz.asSubclass(ChromatticBuilder.class);
        return builderClass.newInstance();
      } else {
        throw new BuilderException("Instrumentor class " + builderClassName + " does not extends the " +
          ChromatticBuilder.class.getName() + " class");
      }
    }
    catch (InstantiationException e) {
      throw new BuilderException("Could not instanciate builder " + builderClassName, e);
    }
    catch (IllegalAccessException e) {
      throw new BuilderException("Could not instanciate builder " + builderClassName, e);
    }
    catch (ClassNotFoundException e) {
      throw new BuilderException("Could not load builder class " + builderClassName, e);
    }
  }

  /** . */
  protected final Set<Class<?>> classes = new HashSet<Class<?>>();

  /** . */
  protected final Map<String, Option.Instance<?>> options = new HashMap<String, Option.Instance<?>>();

  public Option.Instance<?> getOption(String name) {
    return options.get(name);
  }

  public <T> void setOption(Option<T> option, T value) {
    setOption(option, value, true);
  }

  public <T> void setOption(Option.Instance<T> optionInstance, boolean overwrite) {
    setOption(optionInstance.getOption(), optionInstance.getValue(), overwrite);
  }

  public <T> void setOption(Option<T> option, T value, boolean overwrite) {
    if (overwrite || options.get(option.getName()) == null) {
      Option.Instance<T> instance = new Option.Instance<T>(option, value);
      options.put(option.getName(), instance);
    }
  }

  /**
   * Adds a class definition.
   *
   * @param clazz the class to add
   * @throws NullPointerException if the provided class is null
   */
  public void add(Class<?> clazz) throws NullPointerException {
    if (clazz == null) {
      throw new NullPointerException();
    }
    classes.add(clazz);
  }

  /**
   * Builds the runtime and return a configured {@link org.chromattic.api.Chromattic} instance.
   *
   * @return the chromattic instance
   * @throws Exception any exception
   */
  public Chromattic build() throws Exception {
    return boot();
  }

  protected abstract Chromattic boot() throws Exception;

  /**
   * A configuration option.
   *
   * @param <D> the option data type
   */
  public final static class Option<D> {

    /**
     * The type of an option.
     *
     * @param <D> the data type
     */
    public abstract static class Type<D> {

      /** . */
      public static final Type<String> STRING = new Type<String>(String.class) {
        public String doParse(String value) {
          return value;
        }
      };

      /** . */
      public static final Type<Boolean> BOOLEAN = new Type<Boolean>(Boolean.class) {
        public Boolean doParse(String value) {
          return Boolean.valueOf(value);
        }
      };

      /** . */
      private final Class<D> javaType;

      private Type(Class<D> javaType) {
        this.javaType = javaType;
      }

      public final D parse(String value) {
        if (value == null) {
          return null;
        } else {
          return doParse(value);
        }
      }

      protected abstract D doParse(String value);

    }

    /**
     * The instance of an option.
     *
     * @param <D> the data type
     */
    public static class Instance<D> {

      /** . */
      private final Option<D> option;

      /** . */
      private final D value;

      private Instance(Option<D> option, D value) {
        if (option == null) {
          throw new NullPointerException("No null option accepted");
        }
        if (value == null) {
          throw new NullPointerException("No null option value accepted");
        }
        this.option = option;
        this.value = value;
      }

      public Option<D> getOption() {
        return option;
      }

      public D getValue() {
        return value;
      }

      @Override
      public boolean equals(Object obj) {
        if (obj == this) {
          return true;
        }
        if (obj instanceof Instance) {
          Instance that = (Instance)obj;
          return option.name.equals(that.option.name);
        }
        return false;
      }

      @Override
      public int hashCode() {
        return option.name.hashCode();
      }
    }

    /** . */
    private final String name;

    /** . */
    private final String displayName;

    /** . */
    private final Type<D> type;

    private Option(Type<D> type, String name, String displayName) {
      this.name = name;
      this.displayName = displayName;
      this.type = type;
    }

    public Type<D> getType() {
      return type;
    }

    public String getName() {
      return name;
    }

    public String getDisplayName() {
      return displayName;
    }

    public Option.Instance<D> getInstance(String value) {
      D t = type.parse(value);
      return t != null ? new Option.Instance<D>(this, t) : null;
    }
  }

  protected abstract <T> void configure(Option.Instance<T> option);
}
