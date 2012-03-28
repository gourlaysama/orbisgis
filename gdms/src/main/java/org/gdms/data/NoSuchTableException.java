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
package org.gdms.data;

/**
 * This class represents the errors that happens when the DataSourceFactory
 * is asked for a table that doesn't exists. The common mistakes are
 * typing errors, forgetting to register the source, handling of two different
 * instances of a DataSourceFactory
 *
 * @author Fernando Gonzalez Cortes
 */
public class NoSuchTableException extends Exception {
	/**
	 * Creates a new NoSuchTableException object.
	 */
	public NoSuchTableException() {
		super();
	}

	/**
	 * Creates a new NoSuchTableException object.
	 *
	 * @param tableName
	 */
	public NoSuchTableException(String tableName) {
		super(tableName);
	}

	/**
	 * Creates a new NoSuchTableException object.
	 *
	 * @param arg0
	 */
	public NoSuchTableException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * Creates a new NoSuchTableException object.
	 *
	 * @param tableName
	 * @param arg1
	 */
	public NoSuchTableException(String tableName, Throwable arg1) {
		super(tableName, arg1);
	}

        private String format(String tableName) {
                return String.format("The table %s does not exist!", tableName);
        }

        @Override
        public String getMessage() {
                return format(super.getMessage());
        }
}