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
import java.util.List;

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

  @Override
  public void writeTo(Writer writer) throws Exception {
    this.writer = new PrintWriter(writer);
    
    //
    writeTo();
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
  public void property(String name, int requiredType, boolean multiple, Collection<String> defaultValues) throws Exception {
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
  }

  @Override
  public void childNode(String name, String nodeTypeName) throws Exception {
    writer.print("+ ");
    writer.print(name);
    writer.print(" (");
    writer.print(nodeTypeName);
    writer.println(")");
  }
}
