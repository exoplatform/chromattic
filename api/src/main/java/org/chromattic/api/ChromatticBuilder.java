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

  public static final Option<String>  INSTRUMENTOR_CLASSNAME =
    new Option<String>(
      "org.chromattic.api.Option.instrumentor.classname",
      "intrumentor");

  public static final Option<String> SESSION_PROVIDER_CLASSNAME =
    new Option<String>(
      "org.chromattic.api.Option.repository_provider.classname",
      "repository provider");

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
    options.put(option.getName(), new OptionInstance<T>(option, value));
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
