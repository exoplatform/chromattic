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

package org.chromattic.testgenerator.visitor.rendering;

import japa.parser.ast.expr.MethodCallExpr;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class GroovyPropertiesVisitor extends GroovyCompatibilityVisitor
{
   @Override
   public void visit(MethodCallExpr n, Object arg) {
      if (n.getScope() != null) {
         n.getScope().accept(this, arg);
         printer.print(".");
      }

      if (isGetter(n))
      {
         printer.print(decapitalizeFromGetterSetter(n.getName()));
      }

      else if (isSetter(n))
      {
         printer.print(decapitalizeFromGetterSetter(n.getName()));
         printer.print(" = ");
         n.getArgs().iterator().next().accept(this, arg);
      }

      else
      {
         printer.print(n.getName());
         printArguments(n.getArgs(), arg);
      }
   }

   private boolean isGetter(MethodCallExpr methodCallExpr)
   {
      if (methodCallExpr.getName().length() < 4) return false;
      return
              methodCallExpr.getName().startsWith("get")
                      && (methodCallExpr.getArgs() == null || methodCallExpr.getArgs().size() == 0);
   }

   private boolean isSetter(MethodCallExpr methodCallExpr)
   {
      if (methodCallExpr.getName().length() < 4) return false;
      return
              methodCallExpr.getName().startsWith("set")
                      && (methodCallExpr.getArgs() != null && methodCallExpr.getArgs().size() == 1);
   }

   private String decapitalizeFromGetterSetter(String name)
   {
      String fieldName = name.substring(3);
      if (fieldName == null || fieldName.length() == 0) return fieldName;
      if (fieldName.length() > 2 && Character.isUpperCase(fieldName.charAt(2)) && Character.isUpperCase(fieldName.charAt(1)) && Character.isUpperCase(fieldName.charAt(0))) return fieldName;
      return fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
   }
}
