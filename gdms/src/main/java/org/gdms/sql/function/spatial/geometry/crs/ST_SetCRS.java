/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 * 
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 * 
 * This file is part of Gdms.
 * 
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 * 
 * For more information, please consult: <http://www.orbisgis.org/>
 * 
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.sql.function.spatial.geometry.crs;

import org.jproj.CoordinateReferenceSystem;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.spatial.geometry.AbstractScalarSpatialFunction;

/**
 * Sets the internal CRS of a geometry.
 * 
 * Note that this has nothing to do with the CRS constraint on a table column.
 * 
 * @author Antoine Gourlay
 */
public class ST_SetCRS extends AbstractScalarSpatialFunction {

        private CoordinateReferenceSystem crs;

        @Override
        public Value evaluate(DataSourceFactory dsf, Value... args) throws FunctionException {
                if (crs == null) {
                        crs = dsf.getCrsFactory().createFromName(args[1].getAsString());
                }

                return ValueFactory.createValue(args[0].getAsGeometry(), crs);
        }

        @Override
        public String getDescription() {
                return "Sets the internal CRS of a geometry without reprojecting it.";
        }

        @Override
        public String getName() {
                return "ST_SetCRS";
        }

        @Override
        public String getSqlOrder() {
                return "SELECT ST_SetCRS(the_geom, 'EPSG:4326') FROM table;";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(null), ScalarArgument.GEOMETRY,
                                ScalarArgument.STRING),};
        }
}
