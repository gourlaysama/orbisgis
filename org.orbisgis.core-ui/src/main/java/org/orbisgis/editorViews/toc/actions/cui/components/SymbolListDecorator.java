/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.editorViews.toc.actions.cui.components;

import org.orbisgis.renderer.symbol.CircleSymbol;
import org.orbisgis.renderer.symbol.EditableSymbol;
import org.orbisgis.renderer.symbol.LineSymbol;
import org.orbisgis.renderer.symbol.PolygonSymbol;
import org.orbisgis.renderer.symbol.Symbol;

public class SymbolListDecorator {

	private EditableSymbol sym;

	public SymbolListDecorator(EditableSymbol sym) {
		if (sym == null || sym.getName() == null || sym.getName() == "") {
			sym.setName(getSymbolType(sym));
		}
		this.sym = sym;
	}

	private String getSymbolType(Symbol symb) {
		if (symb instanceof PolygonSymbol) {
			return "Poligonal symbol";
		}
		if (symb instanceof CircleSymbol) {
			return "Circle symbol";
		}
		if (symb instanceof LineSymbol) {
			return "Line symbol";
		}
		return "NO_NAMED_SYMBOL";
	}

	public EditableSymbol getSymbol() {
		return sym;
	}

	public String toString() {
		return sym.getName();
	}

	public void setSymbol(EditableSymbol sym) {
		this.sym = sym;
	}

}