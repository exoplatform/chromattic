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

package org.chromattic.metamodel.typegen;

import javax.jcr.PropertyType;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class CNDNodeTypeSerializer extends NodeTypeSerializer {

  /** . */
  private PrintWriter writer;

  public CNDNodeTypeSerializer(List<NodeType> nodeTypes) {
    super(nodeTypes);
  }

  public CNDNodeTypeSerializer(List<NodeType> nodeTypes, Map<String, String> mappings) {
    super(nodeTypes, mappings);
  }

  public CNDNodeTypeSerializer(Map<String, String> mappings) {
    super(mappings);
  }

  public CNDNodeTypeSerializer() {
  }

  @Override
  public void writeTo(Writer writer) throws Exception {
    this.writer = new PrintWriter(writer);
    
    //
    writeTo();
  }

  @Override
  public void startNodeTypes(Map<String, String> mappings) throws Exception {
    for (Map.Entry<String, String> mapping : mappings.entrySet()) {
      writer.append('<').append(mapping.getKey()).append(" = ").append('\'').append(mapping.getValue()).append("'>\n");
    }
  }

  @Override
  public void startNodeType(String javaClassName, String name, boolean mixin, boolean orderableChildNodes, Collection<String> superTypeNames) throws Exception {
    writer.print("[");
    writer.print(name);
    writer.print("]");

    //
    int count = 0;
    for (String superTypeName : superTypeNames) {
      if (count == 0) {
        writer.print(" > ");
      } else {
        writer.print(", ");
      }
      writer.print(superTypeName);
      count++;
    }

    //
    writer.println();

    //
    if (orderableChildNodes) {
      writer.println("orderable");
    }

    //
    if (mixin) {
      writer.println("mixin");
    }
  }

  @Override
  public void property(
      String name,
      int requiredType,
      boolean multiple,
      Collection<String> defaultValues,
      Collection<String> valueConstraints) throws Exception {
    writer.print("- ");
    writer.print(name);
    writer.print(" (");
    writer.print(PropertyType.nameFromValue(requiredType));
    writer.println(")");

    //
    if (defaultValues != null) {
      int count = 0;
      for (String defaultValue : defaultValues) {
        if (count == 0) {
          writer.print("= ");
        } else {
          writer.print(", ");
        }
        writer.print(defaultValue);
        count++;
      }
      writer.println();
    }

    //
    if (multiple) {
      writer.println("multiple");
    }

    //
    if (valueConstraints != null) {
      boolean prolog = true;
      for (String valueConstraint : valueConstraints) {
        if (prolog) {
          writer.print("< ");
          prolog = false;
        } else {
          writer.print(", ");
        }
        writer.print(valueConstraint);
      }
    }
  }

  @Override
  public void childNode(String name, String nodeTypeName, boolean mandatory, boolean autocreated) throws Exception {
    writer.print("+ ");
    writer.print(name);
    writer.print(" (");
    writer.print(nodeTypeName);
    writer.print(")");
    writer.print(" =");
    writer.print(nodeTypeName);

    //
    if (mandatory) {
      writer.print(" mandatory");
    }
    if (autocreated) {
      writer.print(" autocreated");
    }

    //
    writer.println();
  }
}
