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

package org.chromattic.test.query;

import org.chromattic.api.annotations.Query;
import org.chromattic.api.QueryLanguage;

import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public interface A {

  @Query Iterator<A> find1(String statement);

  @Query Iterator<A> find2(String statement, QueryLanguage language);

  @Query(language = QueryLanguage.XPATH) Iterator<A> find3(String statement);

  @Query(statement = "some statement", language = QueryLanguage.XPATH) Iterator<A> find4();

  @Query Iterator<Map<String, Object>> find5(String statement);

  @Query Iterator<Map<String, Object>> find6(String statement, QueryLanguage language);

  @Query(language = QueryLanguage.XPATH) Iterator<Map<String, Object>> find7(String statement);

  @Query(statement = "some statement", language = QueryLanguage.XPATH) Iterator<Map<String, Object>> find8();

}
