/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.renderer.legend.carto;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.SymbolUtil;

public class LegendLine {

	private Symbol symbol;
	private String text;

	public LegendLine(Symbol symbol, String text) {
		this.symbol = symbol;
		this.text = text;
	}

	public void drawImage(Graphics g) {
		SymbolUtil.drawSymbolPreview((Graphics2D) g, symbol, 30, 20, true);
		g.setColor(Color.black);
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D r = fm.getStringBounds(getImageText(), g);
		g.drawString(getImageText(), 35, (int) (10 + r.getHeight() / 2));
	}

	private String getImageText() {
		if (text == null) {
			return "";
		} else {
			return text;
		}
	}

	public int[] getImageSize(Graphics g) {
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D stringBounds = fm.getStringBounds(getImageText(), g);
		int width = 35 + (int) stringBounds.getWidth();

		return new int[] { width, (int) Math.max(stringBounds.getHeight(), 20) };
	}

}
