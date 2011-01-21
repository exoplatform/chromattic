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

package org.chromattic.dataobject;

import org.chromattic.metamodel.typegen.NodeType;

import java.util.Map;

/**
 * The data object service.
 *
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class DataObjectService {

  public DataObjectService() {
  }

  /**
   * Generates the node types for the specified data object paths. This operation returns the schema source
   * in the specified format.
   *
   * @param format the schema output format
   * @param source the compilation source
   * @param doPaths the data object paths
   * @return the data object paths
   * @throws DataObjectException anything that would prevent data object compilation
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if any data object path is null
   */
  public String generateSchema(
    NodeTypeFormat format,
    CompilationSource source,
    String... doPaths) throws DataObjectException, NullPointerException, IllegalArgumentException {

    //
    return new DataObjectCompiler(source, doPaths).generateSchema(format);
  }

  /**
   * Generates the node types for the specified data object paths. This operations returns a map
   * with the data object path as keys and the related node type as values.
   *
   * @param source the compilation source
   * @param doPaths the data object paths
   * @return the data object paths
   * @throws DataObjectException anything that would prevent data object compilation
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if any data object path is null
   */
  public Map<String, NodeType> generateSchema(
    CompilationSource source,
    String... doPaths) throws DataObjectException, NullPointerException, IllegalArgumentException {

    //
    return new DataObjectCompiler(source, doPaths).generateSchema();
  }

  /**
   * Compiles the specified classes and returns a map with a data object path as key and
   * the corresponding compiled data object class.
   *
   * @param source the compilation source
   * @param doPaths the data object paths
   * @return the compiled data object classes
   * @throws DataObjectException anything that would prevent data object compilation
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if any data object path is null
   */
  public Map<String, Class<?>> generateClasses(CompilationSource source,
    String... doPaths) throws DataObjectException, NullPointerException, IllegalArgumentException {

    //
    return new DataObjectCompiler(source, doPaths).generateClasses();
  }

  /**
   * Compiles the specified classes and returns an array containing all the classes generated during
   * the compilation. Note that the number of returned class can be greater than the number of provided
   * paths (classes can be generated for specific groovy needs, such as closure).
   *
   * @param source the compilation source
   * @param doPaths the data object paths
   * @return the compiled data object classes
   * @throws DataObjectException anything that would prevent data object compilation
   * @throws NullPointerException if any argument is null
   * @throws IllegalArgumentException if any data object path is null
   */
  public Class[] generateAllClasses(
    CompilationSource source,
    String... doPaths) throws DataObjectException, NullPointerException, IllegalArgumentException {

    //
    return new DataObjectCompiler(source, doPaths).generateAllClasses();
  }
}
