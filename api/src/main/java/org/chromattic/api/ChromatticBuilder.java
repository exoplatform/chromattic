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

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The builder configures and create a Chromattic runtime.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class ChromatticBuilder {

  /** . */
  private static final Logger log = Logger.getLogger(ChromatticBuilder.class.getName());

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
  public static final Option<Boolean> PROPERTY_CACHE_ENABLED =
    new Option<Boolean>(
      Option.Type.BOOLEAN,
      "org.chromattic.api.Option.property.cache.enabled",
      "property cache enabled");

  /**
   * Todo.
   */
  public static final Option<Boolean> PROPERTY_READ_AHEAD_ENABLED =
    new Option<Boolean>(
      Option.Type.BOOLEAN,
      "org.chromattic.api.Option.property.read_ahead.enabled",
      "property read ahead enabled");

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
   * A boolean option that indicates that the root node should be lazyly created when it is required.
   */
  public static final Option<Boolean> LAZY_CREATE_ROOT_NODE =
    new Option<Boolean>(
      Option.Type.BOOLEAN,
      "org.chromattic.api.Option.root_node.lazy_create",
      "when root node is created it is done in a lazy manner");

  /**
   * A string value that is the root node type when Chromattic has to build the path to the root node.
   */
  public static final Option<String> ROOT_NODE_TYPE =
    new Option<String>(
      Option.Type.STRING,
      "org.chromattic.api.Option.root_node.root_node_type",
      "the root node type when it is created by Chromattic");

  /**
   * Create and return an instance of the builder.
   *
   * @return the chromattic builder instance
   */
  public static ChromatticBuilder create() {
    ServiceLoader<ChromatticBuilder> loader = ServiceLoader.load(ChromatticBuilder.class);
    Iterator<ChromatticBuilder> i =  loader.iterator();
    Throwable throwable = null;
    while (i.hasNext()) {
      try {
        ChromatticBuilder builder = i.next();
        log.log(Level.FINER, "Found ChromatticBuilder implementation " + builder.getClass().getName());
        return builder;
      }
      catch (ServiceConfigurationError error) {
        if (throwable == null) {
          throwable = error;
        }
        log.log(Level.FINER, "Could not load ChromatticBuilder implementation, will use next provider", error);
      }
    }
    throw new BuilderException("Could not instanciate builder", throwable);
  }

  /** The domain classes. */
  private final Set<Class<?>> classes;

  /** The default configuration. */
  private final Configuration config;

  /** . */
  private boolean initialized;

  /** For stuff that need to happen under synchronization. */
  private final Object lock = new Object();

  public ChromatticBuilder() {
    this.config = createDefaultConfiguration();
    this.initialized = false;
    this.classes = new HashSet<Class<?>>();
  }

  /**
   * <p>Create the default configuration. Subclass can override it to provide a suitable default configuration.
   * The returned object will likely be modified and therefore a new copy should be created every time this
   * method is invoked.</p>
   *
   * <p>The default implementation relies on the {@link ServiceLoader} to load an instance of {@link Configuration.Factory}
   * If no configuration is found then a builder exception is thrown./<p>
   *
   * @return the default configuration
   */
  protected Configuration createDefaultConfiguration() {
    ServiceLoader loader = ServiceLoader.load(Configuration.Factory.class);
    Iterator<Configuration.Factory> i = loader.iterator();
    while (i.hasNext()) {
      try {
        Configuration.Factory builder = i.next();
        log.log(Level.FINER, "Found ChromatticBuilder implementation " + builder.getClass().getName());
        return builder.create();
      }
      catch (ServiceConfigurationError ignore) {
        log.log(Level.FINER, "Could not load ChromatticBuilder implementation, will use next provider", ignore);
      }
    }
    throw new BuilderException("Could not instanciate configuration factory");
  }

  /**
   * Return the current configuration.
   *
   * @return the configuration
   */
  public final Configuration getConfiguration() {
    return config;
  }

  /**
   * Adds a class definition.
   *
   * @param clazz the class to add
   * @throws NullPointerException if the provided class is null
   * @throws IllegalStateException if the builder is already initialized
   */
  public void add(Class<?> clazz) throws NullPointerException, IllegalStateException {
    add(clazz, new Class<?>[0]);
  }

  /**
   * Adds a class definition.
   *
   * @param first the first class to add
   * @param other the other classes to add
   * @throws NullPointerException if the provided class is null
   * @throws IllegalStateException if the builder is already initialized
   */
  public void add(Class<?> first, Class<?>... other) throws NullPointerException, IllegalStateException {
    if (first == null) {
      throw new NullPointerException();
    }
    if (other == null) {
      throw new NullPointerException();
    }
    Set<Class<?>> toAdd = new HashSet<Class<?>>(1 + other.length);
    toAdd.add(first);
    for (Class<?> clazz : other) {
      if (clazz == null) {
        throw new IllegalArgumentException("No array containing a null class accepted");
      }
      toAdd.add(clazz);
    }
    synchronized (lock) {
      if (initialized) {
        throw new IllegalStateException("Cannot add a class to an initialized builder");
      }
      classes.addAll(toAdd);
    }
  }

  /**
   * Builds the runtime and return a configured {@link org.chromattic.api.Chromattic} instance.
   *
   * @return the chromattic instance
   * @throws BuilderException any builder exception
   */
  public final Chromattic build() throws BuilderException {
    return build(config);
  }

  /**
   * Builds the runtime and return a configured {@link org.chromattic.api.Chromattic} instance.
   *
   * @param config the configuration to use
   * @return the chromattic instance
   * @throws BuilderException any builder exception
   */
  public final Chromattic build(Configuration config) throws BuilderException {

    // Init if needed
    init();

    // Copy options
    config = new Configuration(config);

    //
    return boot(config);
  }

  /**
   * Initialize the builder, this operation should be called once per builder, unlike the {@link #build(Configuration)}
   * operation that can be called several times with different configurations. This operation is used to perform the
   * initialization that is common to any configuration such as building the meta model from the classes.
   *
   * @return whether or not initialization occured
   * @throws BuilderException any exception that would prevent the initialization to happen correctly
   */
  public final boolean init() throws BuilderException {
    // Init if needed
    synchronized (lock) {
      if (!initialized) {
        init(classes);
        initialized = true;
        return true;
      } else {
        return false;
      }
    }
  }

  protected abstract void init(Set<Class<?>> classes) throws BuilderException;

  protected abstract Chromattic boot(Configuration options) throws BuilderException;

  /**
   * Set the option value.
   *
   * @param option the option to set
   * @param value the option value
   * @param <D> the option data type
   * @throws NullPointerException if any argument is null
   */
  public <D> void setOptionValue(Option<D> option, D value) throws NullPointerException {
    config.setOptionValue(option, value, true);
  }

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

  public static class Configuration {


    /**
     * The configuration factory.
     */
    public abstract static class Factory {

      /**
       * Returns a configuration. The returned object will likely be modified so a new instance should
       * be provided on each invocation and no caching should be done.
       *
       * @return the configuration
       */
      public abstract Configuration create();
    }

    /** . */
    protected final Map<String, Option.Instance<?>> entries = new HashMap<String, Option.Instance<?>>();

    public Configuration() {
    }

    /**
     * Copy constructor for internal usage.
     *
     * @param that the options to copy
     */
    public Configuration(Configuration that) {
      if (that == null) {
        throw new NullPointerException("No null configuration accepted");
      }

      entries.putAll(that.entries);
    }

    /**
     * Returns a configured option instance.
     *
     * @param name the option name
     * @return the corresponding option instance or null
     * @throws NullPointerException if the name is null
     */
    public Option.Instance<?> getOptionInstance(String name) throws NullPointerException {
      if (name == null)
      {
        throw new NullPointerException();
      }
      return entries.get(name);
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
      if (option == null)
      {
        throw new NullPointerException();
      }
      @SuppressWarnings("unchecked") // Cast OK
      Option.Instance<D> instance = (Option.Instance<D>)entries.get(option.getName());
      return instance;
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
      Option.Instance<D> instance = getOptionInstance(option);
      return instance != null ? instance.value : null;
    }

    /**
     * Set the option value.
     *
     * @param option the option to set
     * @param value the option value
     * @param overwrite wheter or not to overwrite an existing value
     * @param <D> the option data type
     * @return whether or not the value was overwritten
     * @throws NullPointerException if any argument is null
     */
    public <D> boolean setOptionValue(Option<D> option, D value, boolean overwrite) throws NullPointerException {
      if (option == null) {
        throw new NullPointerException("No null option");
      }
      if (value == null) {
        throw new NullPointerException("No null value");
      }
      if (overwrite || entries.get(option.getName()) == null) {
        Option.Instance<D> instance = new Option.Instance<D>(option, value);
        entries.put(option.getName(), instance);
        return true;
      } else {
        return false;
      }
    }
  }
}
