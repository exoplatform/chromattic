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

/**
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
      "org.chromattic.api.Option.instrumentor.classname",
      "intrumentor");

  /**
   * The JCR session life cycle class name. The specified class must implement the
   * <tt>org.chromattic.spi.jcr.SessionLifeCycle</tt> class.
   */
  public static final Option<String> SESSION_LIFECYCLE_CLASSNAME =
    new Option<String>(
      "org.chromattic.api.Option.session_lifecycle.classname",
      "session life cycle");

  /**
   * The object name formatter class name. The specified class must implement the
   * <tt>org.chromattic.api.format.ObjectFormatter</tt> class.
   */
  public static final Option<String> OBJECT_FORMATTER_CLASSNAME =
    new Option<String>(
      "org.chromattic.api.Option.object_formatter.classname",
      "object formatter");

  /**
   * The boolean indicating if caching is performed. When cache is enabled each session
   * maintains a cache that avoids to use the underlying JCR session. As a consequence
   * any change made directly to the JCR session will not be visible in the object domain.
   */
  public static final Option<Boolean> STATE_CACHE_ENABLED =
    new Option<Boolean>(
      "org.chromattic.api.Option.state_cache.enabled",
      "state cache enabled");

  /**
   * The path of the root node. The default value is the path of the JCR workspace root node.
   */
  public static final Option<String> ROOT_NODE_PATH =
    new Option<String>(
      "org.chromattic.api.Option.root_node_path",
      "root node path");

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
  protected final Map<String, OptionInstance<?>> options = new HashMap<String, OptionInstance<?>>();

  public <T> void setOption(Option<T> option, T value) {
    OptionInstance<T> instance = new OptionInstance<T>(option, value);
    options.put(option.getName(), instance);
  }

  public void add(Class<?> clazz) {
    classes.add(clazz);
  }

  public Chromattic build() throws Exception {
    return boot();
  }

  protected abstract Chromattic boot() throws Exception;

  /**
   * A configuration option.
   *
   * @param <T> the option type
   */
  public static class Option<T> {

    /** . */
    private final String name;

    /** . */
    private final String shortName;

    private Option(String name, String shortName) {
      this.name = name;
      this.shortName = shortName;
    }

    public String getName() {
      return name;
    }

    public String getShortName() {
      return shortName;
    }
  }

  protected abstract <T> void configure(OptionInstance<T> option);

  protected static class OptionInstance<T> {

    /** . */
    private final Option<T> option;

    /** . */
    private final T value;

    private OptionInstance(Option<T> option, T value) {
      if (option == null) {
        throw new NullPointerException("No null option accepted");
      }
      if (value == null) {
        throw new NullPointerException("No null option value accepted");
      }
      this.option = option;
      this.value = value;
    }

    public Option<T> getOption() {
      return option;
    }

    public T getValue() {
      return value;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj instanceof OptionInstance) {
        OptionInstance that = (OptionInstance)obj;
        return option.name.equals(that.option.name);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return option.name.hashCode();
    }
  }
}
