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

package org.chromattic.metamodel.mapping2;

import org.chromattic.api.annotations.*;
import org.reflext.api.annotation.AnnotationType;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class Constants {

  /** . */
  static final AnnotationType<NamingPolicy, ?> NAMING_POLICY = AnnotationType.get(NamingPolicy.class);

  /** . */
  static final AnnotationType<PrimaryType, ?> PRIMARY_TYPE = AnnotationType.get(PrimaryType.class);

  /** . */
  static final AnnotationType<MixinType, ?> MIXIN_TYPE = AnnotationType.get(MixinType.class);

  /** . */
  static final AnnotationType<Create, ?> CREATE = AnnotationType.get(Create.class);

  /** . */
  static final AnnotationType<Destroy, ?> DESTROY = AnnotationType.get(Destroy.class);

  /** . */
  static final AnnotationType<FindById, ?> FIND_BY_ID = AnnotationType.get(FindById.class);

}
