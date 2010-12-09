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

package org.chromattic.apt;

import org.chromattic.metamodel.mapping.BeanMapping;
import org.chromattic.metamodel.mapping.PropertyMapping;
import org.chromattic.api.PropertyLiteral;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.TypeInfo;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class PropertyLiteralGenerator {

  /** . */
  private final BeanMapping beanMapping;

  PropertyLiteralGenerator(BeanMapping beanMapping) {
    this.beanMapping = beanMapping;
  }

  void build(Filer filer) throws IOException {
    String qualifiedName = beanMapping.getBean().getClassType().getName() + "_";
    JavaFileObject jfo = filer.createSourceFile(qualifiedName); // Lack of the originating elt!!!!
    PrintWriter out = new PrintWriter(jfo.openWriter());
    build(out);
    out.close();
  }

  private void build(Appendable code) throws IOException {

    //
    ClassTypeInfo owner = beanMapping.getBean().getClassType();
    code.append("package ").append(owner.getPackageName()).append(";\n");
    code.append("import ").append(PropertyLiteral.class.getName()).append(";\n");

    //
    code.append("public class ").append(owner.getSimpleName()).append("_ {\n");

    for (PropertyMapping pm : beanMapping.getProperties().values()) {
      TypeInfo type = pm.getValue().getEffectiveType();
      StringBuilder toto = new StringBuilder();
      new TypeFormatter(owner, FormatterStyle.CAST, toto).format(type);

      code.append("public static final PropertyLiteral<").
        append(owner.getName()).
        append(",").
        append(toto).
        append("> ").append(pm.getName()).append(" = new PropertyLiteral<").
        append(owner.getName()).
        append(",").
        append(toto).
        append(">").
        append("(").
        append(owner.getName()).append(".class").
        append(",").
        append("\"").append(pm.getName()).append("\"").
        append(",").
        append(toto).append(".class").
        append(");\n");
    }

    code.append("}\n");
  }
}
