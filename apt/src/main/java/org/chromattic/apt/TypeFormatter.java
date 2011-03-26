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
package org.chromattic.apt;

import org.reflext.api.TypeInfo;
import org.reflext.api.ClassTypeInfo;
import org.reflext.api.ParameterizedTypeInfo;
import org.reflext.api.TypeVariableInfo;
import org.reflext.api.ArrayTypeInfo;
import org.reflext.api.WildcardTypeInfo;
import org.reflext.api.VoidTypeInfo;
import org.reflext.api.SimpleTypeInfo;

import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class TypeFormatter {

  /** . */
  private final ClassTypeInfo context;

  /** . */
  private final FormatterStyle style;

  /** . */
  private final StringBuilder s;

  public TypeFormatter(ClassTypeInfo context, FormatterStyle style, StringBuilder s) {
    this.context = context;
    this.style = style;
    this.s = s;
  }

  public void format(TypeInfo ti) {
    format(ti, false);
  }

  private void format(TypeInfo ti, boolean fromArray) {
    if (ti instanceof ClassTypeInfo) {
      format((ClassTypeInfo)ti, fromArray);
    } else if (ti instanceof ParameterizedTypeInfo) {
      format((ParameterizedTypeInfo)ti, fromArray);
    } else if (ti instanceof TypeVariableInfo) {
      format((TypeVariableInfo)ti, fromArray);
    } else if (ti instanceof ArrayTypeInfo) {
      format((ArrayTypeInfo)ti, fromArray);
    } else if (ti instanceof WildcardTypeInfo) {
      format((WildcardTypeInfo)ti, fromArray);
    } else {
      throw new UnsupportedOperationException();
    }
  }

  private void format(ClassTypeInfo cti, boolean fromArray) {
    if (cti instanceof VoidTypeInfo) {
      switch (style) {
        case CAST:
        case TYPE_PARAMETER:
          throw new AssertionError();
        case RETURN_TYPE:
        case LITERAL:
          s.append("void");
          break;
      }
    }
    else {
      switch (style) {
        case CAST:
          if (!fromArray) {
            if (cti instanceof SimpleTypeInfo) {
              switch (((SimpleTypeInfo)cti).getLiteralType()) {
                case INT:
                  s.append("java.lang.Integer");
                  break;
                case BOOLEAN:
                  s.append("java.lang.Boolean");
                  break;
                case LONG:
                  s.append("java.lang.Long");
                  break;
                case DOUBLE:
                  s.append("java.lang.Double");
                  break;
                case FLOAT:
                  s.append("java.lang.Float");
                  break;
                default:
                  throw new UnsupportedOperationException();
              }
            } else {
              s.append(cti.getName());
            }
          } else {
            s.append(cti.getName());
          }
          break;
        case LITERAL:
        case TYPE_PARAMETER:
        case RETURN_TYPE:
          s.append(cti.getName());
          break;
      }
    }
  }

  private void format(ParameterizedTypeInfo pti, boolean fromArray) {
    TypeInfo rawType = pti.getRawType();
    format(rawType);
  }

  private void format(TypeVariableInfo tvi, boolean fromArray) {
    switch (style) {
      case LITERAL:
        format(tvi.getBounds().get(0));
        break;
      case TYPE_PARAMETER:
      case RETURN_TYPE:
      case CAST: {
        TypeInfo resolved = context.resolve(tvi);
        if (resolved instanceof TypeVariableInfo) {
          TypeVariableInfo resolvedTVI = (TypeVariableInfo)resolved;
          List<TypeInfo> bounds = resolvedTVI.getBounds();
          if (bounds.size() != 1) {
            throw new UnsupportedOperationException("Need to add support for multiple bounds");
          }
          TypeInfo bound = bounds.get(0);
          format(bound);
        } else {
          format(resolved);
        }
        break;
      }
    }
  }

  private void format(ArrayTypeInfo ati, boolean fromArray) {
    switch (style) {
      case LITERAL:
      case TYPE_PARAMETER:
      case RETURN_TYPE:
      case CAST: {
        TypeInfo componentTI = ati.getComponentType();
        format(componentTI, true);
        s.append("[]");
        break;
      }
    }
  }

  private void format(WildcardTypeInfo wti, boolean fromArray) {
    // Do nothing
  }
}
