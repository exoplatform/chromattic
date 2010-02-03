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

import org.chromattic.api.format.DefaultObjectFormatter;

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
   * A special option that will lookup system properties when set to true to configure options by default.
   */
  public static final Option<Boolean> USE_SYSTEM_PROPERTIES =
    new Option<Boolean>(
      Option.Type.BOOLEAN,
      "org.chromattic.api.Option.use_system_properties",
      "use system properties");

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
      "org.chromattic.api.Option.root_node.path",
      "the root node path value");

  /**
   * A boolean option that creates the root node designated by the {@link #ROOT_NODE_PATH} option
   * when it does not exist.
   */
  public static final Option<Boolean> CREATE_ROOT_NODE =
    new Option<Boolean>(
      Option.Type.BOOLEAN,
      "org.chromattic.api.Option.root_node.create",
      "creates the chromattic root node when it does not exist");


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

  /**
   * Create and return an instance of the builder.
   *
   * @return the chromattic builder instance
   */
  public static ChromatticBuilder create() {
    String builderClassName = "org.chromattic.core.api.ChromatticBuilderImpl";
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
  private final Set<Class<?>> classes = new HashSet<Class<?>>();

  /** . */
  private final Options options = new Options();

  /**
   * Returns a configured option instance.
   *
   * @param name the option name
   * @return the corresponding option instance or null
   * @throws NullPointerException if the name is null
   */
  public Option.Instance<?> getOptionInstance(String name) throws NullPointerException {
    return options.getInstance(name);
  }

  /**
   * Returns a configured option instance.
   *
   * @param option the option to return
   * @param <D> the option data type
   * @return the option instance or null
   * @throws NullPointerException if the option is null
   */
  public <D> Option.Instance<D> getOptionInstance(Option<D> option) throws NullPointerException {
    return options.getInstance(option);
  }

  /**
   * Set the option value as a string.
   *
   * @param option the option to set
   * @param value the option value
   * @param <D> the option data type
   * @throws NullPointerException if any argument is null
   */
  public <D> void setOptionStringValue(Option<D> option, String value) throws NullPointerException {
    options.setStringValue(option, value, true);
  }

  /**
   * Set the option value.
   *
   * @param option the option to set
   * @param value the option value
   * @param <D> the option data type
   * @throws NullPointerException if any argument is null
   */
  public <D> void setOptionValue(Option<D> option, D value) throws NullPointerException {
    options.setValue(option, value, true);
  }

  /**
   * Returns the option value.
   *
   * @param option the option
   * @param <D> the option data type
   * @return the option value
   * @throws NullPointerException if the option parameter is null
   */
  public <D> D getOptionValue(Option<D> option) throws NullPointerException {
    return options.getValue(option);
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

    // Copy options
    Options options = new Options(this.options);

    // Configure system properties options
    if (!Boolean.FALSE.equals(options.getValue(USE_SYSTEM_PROPERTIES))) {
      for (Option<?> option : getSystemOptions()) {
        String value = System.getProperty(option.getName());
        if (value != null) {
          options.setStringValue(option, value, false);
        }
      }
    }

    // Configuration default options
    options.setValue(INSTRUMENTOR_CLASSNAME, "org.chromattic.apt.InstrumentorImpl", false);
    options.setValue(SESSION_LIFECYCLE_CLASSNAME, "org.chromattic.exo.ExoSessionLifeCycle", false);
    options.setValue(OBJECT_FORMATTER_CLASSNAME, DefaultObjectFormatter.class.getName(), false);
    options.setValue(CACHE_STATE_ENABLED, false, false);
    options.setValue(JCR_OPTIMIZE_HAS_PROPERTY_ENABLED, false, false);
    options.setValue(JCR_OPTIMIZE_HAS_NODE_ENABLED, false, false);
    options.setValue(ROOT_NODE_PATH, "/", false);
    options.setValue(CREATE_ROOT_NODE, false, false);

    //
    return boot(options, new HashSet<Class>(classes));
  }

  protected abstract Chromattic boot(Options options, Set<Class> classes) throws BuilderException;

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
          throw new NullPointerException("Cannot parse null value");
        }
        return doParse(value);
      }

      /**
       * Performs the effective parse, when called the value will never be null.
       *
       * @param value the value to parse
       * @return the parsed value
       */
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

  protected static class Options {

    /** . */
    protected final Map<String, Option.Instance<?>> entries = new HashMap<String, Option.Instance<?>>();

    private Options() {
    }

    /**
     * Copy constructor for internal usage.
     *
     * @param that the options to copy
     */
    private Options(Options that) {
      entries.putAll(that.entries);
    }

    public Option.Instance<?> getInstance(String name) throws NullPointerException {
      if (name == null)
      {
        throw new NullPointerException();
      }
      return entries.get(name);
    }

    public <D> Option.Instance<D> getInstance(Option<D> option) throws NullPointerException {
      if (option == null)
      {
        throw new NullPointerException();
      }
      @SuppressWarnings("unchecked") // Cast OK
      Option.Instance<D> instance = (Option.Instance<D>)entries.get(option.getName());
      return instance;
    }

    public <D> void setStringValue(Option<D> option, String value, boolean overwrite) throws NullPointerException {
      if (option == null) {
        throw new NullPointerException("Cannot set null option");
      }
      if (value == null) {
        throw new NullPointerException("Cannot set null value");
      }
      setValue(option, option.getType().parse(value), overwrite);
    }

    public <D> D getValue(Option<D> option) throws NullPointerException {
      Option.Instance<D> instance = getInstance(option);
      return instance != null ? instance.value : null;
    }

    public <D> void setValue(Option<D> option, D value, boolean overwrite) throws NullPointerException {
      if (option == null) {
        throw new NullPointerException("No null option");
      }
      if (value == null) {
        throw new NullPointerException("No null value");
      }
      if (overwrite || entries.get(option.getName()) == null) {
        Option.Instance<D> instance = new Option.Instance<D>(option, value);
        entries.put(option.getName(), instance);
      }
    }
  }
}
