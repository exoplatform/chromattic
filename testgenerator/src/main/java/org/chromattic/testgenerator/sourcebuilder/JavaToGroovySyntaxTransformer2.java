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

package org.chromattic.testgenerator.sourcebuilder;

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
public class JavaToGroovySyntaxTransformer2 implements TransformationProcessor {
  protected TransformationSource transformationSource;

  public JavaToGroovySyntaxTransformer2() {
  }

  public JavaToGroovySyntaxTransformer2(TransformationSource transformationSource) {
    this.transformationSource = transformationSource;
  }

  public void setTransformationSource(TransformationSource transformationSource) {
    this.transformationSource = transformationSource;
  }

  public String transform(String source) {
    if (transformationSource == null) throw new IllegalStateException("transformationSource must be initialized before transformation.");
    String dst = source;
    dst = annotationBracket(dst);
    dst = genericCall(dst);
    return dst;
  }

  protected String annotationBracket(String source) {
    return source;
    /*String tmpSrc = source;
    for (AnnotationExpr expr : transformationSource.getAnnotationExprs()) {
      String tmpExpr = expr.toString();
      tmpExpr = tmpExpr.replaceAll("\\{", "[");
      tmpExpr = tmpExpr.replaceAll("\\}", "]");
      tmpSrc = tmpSrc.replace(expr.toString(), tmpExpr);
    }
    return tmpSrc;*/
  }

  protected String genericCall(String source) {
     return source;
    /*String tmpSrc = source;
    for (MethodCallExpr methodCallExpr : transformationSource.getMethodCallExprs()) {
      String tmpExpr = methodCallExpr.toString();
      if (methodCallExpr.getTypeArgs() != null) {
        tmpExpr = tmpExpr.replaceAll("<.*>", "");
        tmpSrc = tmpSrc.replace(methodCallExpr.toString(), tmpExpr);
      }
    }
    return tmpSrc;*/
  }
}