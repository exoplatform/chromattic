/*
 * Copyright (C) 2007 J�lio Vilmar Gesser.
 *
 * This file is part of Java 1.5 parser and Abstract Syntax Tree.
 *
 * Java 1.5 parser and Abstract Syntax Tree is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Java 1.5 parser and Abstract Syntax Tree is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Java 1.5 parser and Abstract Syntax Tree.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Created on 05/10/2006
 */
package japa.parser.ast;

import japa.parser.ast.visitor.DumpVisitor;
import japa.parser.ast.visitor.DumpVisitorFactory;

/**
 * Abstract class for all nodes of the AST.
 * 
 * @author Julio Vilmar Gesser
 */
public abstract class ExtendedNode extends Node
{
   protected ExtendedNode()
   {
   }

   protected ExtendedNode(final int beginLine, final int beginColumn, final int endLine, final int endColumn)
   {
      super(beginLine, beginColumn, endLine, endColumn);
   }

   public final String toString(DumpVisitorFactory<DumpVisitor> factory) {
        DumpVisitor visitor = factory.createVisitor();
        accept(visitor, null);
        return visitor.getSource();
    }
}
