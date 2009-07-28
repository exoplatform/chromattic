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

package org.chromattic.common.logging;

import org.chromattic.common.Collections;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class Logger {

  /** . */
  private static final ConcurrentHashMap<String, Logger> loggers = new ConcurrentHashMap<String, Logger>();

  public static Logger getLogger(Class<?> name) throws NullPointerException {
    if (name == null) {
      throw new NullPointerException();
    }
    return getLogger(name.getName());
  }

  public static Logger getLogger(String name) throws NullPointerException {
    if (name == null) {
      throw new NullPointerException();
    }
    Logger logger = loggers.get(name);
    if (logger == null) {
      logger = Collections.putIfAbsent(loggers, name, new Logger(name));
    }
    return logger;
  }

  /** . */
  private final org.slf4j.Logger logger;

  public Logger(String name) {
    this.logger = org.slf4j.LoggerFactory.getLogger(name);
  }

  public String getName() {
    return logger.getName();
  }

  public boolean isTraceEnabled() {
    return logger.isInfoEnabled();
  }

  public void trace(String s) {
    logger.info(s);
  }

  public void trace(String s, Object o) {
    logger.info(s, o);
  }

  public void trace(String s, Object o, Object o1) {
    logger.info(s, o, o1);
  }

  public void trace(String s, Object... objects) {
    logger.info(s, objects);
  }

  public void trace(String s, Throwable throwable) {
    logger.info(s, throwable);
  }

  public void debug(String s) {
    logger.info(s);
  }

  public void debug(String s, Object o) {
    logger.info(s, o);
  }

  public void debug(String s, Object o, Object o1) {
    logger.info(s, o, o1);
  }

  public void debug(String s, Object... objects) {
    logger.info(s, objects);
  }

  public void debug(String s, Throwable throwable) {
    logger.info(s, throwable);
  }

  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  public void info(String s) {
    logger.info(s);
  }

  public void info(String s, Object o) {
    logger.info(s, o);
  }

  public void info(String s, Object o, Object o1) {
    logger.info(s, o, o1);
  }

  public void info(String s, Object... objects) {
    logger.info(s, objects);
  }

  public void info(String s, Throwable throwable) {
    logger.info(s, throwable);
  }

  public boolean isWarnEnabled() {
    return logger.isWarnEnabled();
  }

  public boolean isDebugEnabled() {
    return logger.isInfoEnabled();
  }

  public void warn(String s) {
    logger.warn(s);
  }

  public void warn(String s, Object o) {
    logger.warn(s, o);
  }

  public void warn(String s, Object... objects) {
    logger.warn(s, objects);
  }

  public void warn(String s, Object o, Object o1) {
    logger.warn(s, o, o1);
  }

  public void warn(String s, Throwable throwable) {
    logger.warn(s, throwable);
  }

  public boolean isErrorEnabled() {
    return logger.isErrorEnabled();
  }

  public void error(String s) {
    logger.error(s);
  }

  public void error(String s, Object o) {
    logger.error(s, o);
  }

  public void error(String s, Object o, Object o1) {
    logger.error(s, o, o1);
  }

  public void error(String s, Object... objects) {
    logger.error(s, objects);
  }

  public void error(String s, Throwable throwable) {
    logger.error(s, throwable);
  }
}
