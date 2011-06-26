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

package org.chromattic.testgenerator;

import javax.lang.model.element.TypeElement;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public enum GroovyOutputFormat
{
   GETTER_SETTER
           {
              @Override
              public String testName(TypeElement typeElt)
              {
                 return "GroovyGetSet_" + typeElt.getSimpleName();
              }

              @Override
              public String testName(TestRef ref)
              {
                 return "GroovyGetSet_" + getClassName(ref.getName());
              }
           },

   PROPERTIES
           {
              @Override
              public String testName(TypeElement typeElt)
              {
                 return "GroovyProperties_" + typeElt.getSimpleName();
              }

              @Override
              public String testName(TestRef ref)
              {
                 return "GroovyProperties_" + getClassName(ref.getName());
              }
           },

   CHROMATTIC
           {
              @Override
              public String testName(TypeElement typeElt)
              {
                 return typeElt.getSimpleName().toString();
              }

              @Override
              public String testName(TestRef ref)
              {
                 return getClassName(ref.getName()).toString();
              }
           },;


   // Package name
   public CharSequence getPackageName(TestRef ref)
   {
      return getPackageName(ref.getName());
   }

   public CharSequence getPackageName(TypeElement typeElt)
   {
      return getPackageName(typeElt.getQualifiedName());
   }

   public CharSequence getPackageName(CharSequence typeName)
   {
      int lastIndex = typeName.toString().lastIndexOf(".");
      return typeName.subSequence(0, lastIndex);
   }


   // Java file name
   public String javaFileName(TestRef ref)
   {
      return javaFileName(ref.getName());
   }

   public String javaFileName(TypeElement typeElt)
   {
      return javaFileName(typeElt.getQualifiedName().toString());
   }

   public String javaFileName(String classLiteral)
   {
      return getClassName(classLiteral) + ".java";
   }


   // Groovy file name
   public String groovyFileName(TestRef ref)
   {
      return groovyFileName(testName(ref));
   }

   public String groovyFileName(TypeElement typeElt)
   {
      return groovyFileName(testName(typeElt));
   }

   public String groovyFileName(String classLiteral)
   {
      return getClassName(classLiteral) + ".groovy";
   }


   // Other stuff
   public CharSequence getClassName(CharSequence typeName)
   {
      int lastIndex = typeName.toString().lastIndexOf(".") + 1;
      return typeName.subSequence(lastIndex, typeName.length());
   }

   public abstract String testName(TypeElement typeElt);
   public abstract String testName(TestRef ref);
}
