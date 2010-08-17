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

package org.chromattic.testgenerator.sourcetransformer;

import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.MethodCallExpr;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class JavaToGroovySyntaxTransformer {
  private SourceTransformation sourceTransformation;

  public JavaToGroovySyntaxTransformer(SourceTransformation sourceTransformation) {
    this.sourceTransformation = sourceTransformation;
  }

  public String transform(String source) {
    String dst = source;
    dst = annotationBracket(dst);
    dst = genericCall(dst);
    //dst = arrayCreation(dst);
    return dst;
  }

  private String annotationBracket(String source) {
    String tmpSrc = source;
    for (AnnotationExpr expr : sourceTransformation.getAnnotationExprs()) {
      String tmpExpr = expr.toString();
      tmpExpr = tmpExpr.replaceAll("\\{", "[");
      tmpExpr = tmpExpr.replaceAll("\\}", "]");
      tmpSrc = tmpSrc.replace(expr.toString(), tmpExpr);
    }
    return tmpSrc;
  }

  private String genericCall(String source) {
    String tmpSrc = source;
    for (MethodCallExpr methodCallExpr : sourceTransformation.getMethodCallExprs()) {
      String tmpExpr = methodCallExpr.toString();
      if (methodCallExpr.getTypeArgs() != null) {
        tmpExpr = tmpExpr.replaceAll("<.*>", "");
        tmpSrc = tmpSrc.replace(methodCallExpr.toString(), tmpExpr);
      }
    }
    return tmpSrc;
  }

  private String arrayCreation(String source) {
    String tmpSrc = source;
    /*for (Array : sourceTransformation.getArrayCreationExprs()) {
      
    }*/
    return tmpSrc;
    //return source.replace(source.toString(), "[\"a\", \"b\"]");
  }
}