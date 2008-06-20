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
package org.gdms.sql.function.spatial.raster.hydrology;

import java.io.IOException;

import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.hydrology.D8OpConstrainedAccumulation;

public class D8ConstrainedAccumulation implements Function {
	public Value evaluate(Value[] args) throws FunctionException {
		GeoRaster grD8Direction = args[0].getAsRaster();
		GeoRaster grConstrained = args[1].getAsRaster();

		try {
			final Operation opConstrainedAccumulation = new D8OpConstrainedAccumulation(
					grConstrained);
			GeoRaster geoRaster = grD8Direction
			.doOperation(opConstrainedAccumulation);
			geoRaster.setNodataValue(GeoRaster.FLOAT_NO_DATA_VALUE);
			return ValueFactory.createValue(geoRaster);
		} catch (OperationException e) {
			throw new FunctionException("Cannot do the operation", e);
		} catch (UnsupportedOperationException e) {
			throw new FunctionException("Cannot set nodata value", e);
		} catch (IOException e) {
			throw new FunctionException("Cannot set nodata value", e);
		}
	}

	//TODO To be complete
	public String getDescription() {
		return "This function compute a constrained grid accumulation based on two grid : a grid direction and a integer grid that represents" +
				"some human constaints as hedgerow or roads.";
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] { TypeFactory
				.createType(Type.RASTER) }, new String[] { "raster" });
	}

	public String getName() {
		return "D8ConstrainedAccumulation";
	}

	public String getSqlOrder() {
		return "select D8ConstrainedAccumulation(d.raster, a.raster) from directions d, constrainedgrid a;";
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, types, 2);
		FunctionValidator.failIfNotOfType(this, types[0], Type.RASTER);
		FunctionValidator.failIfNotOfType(this, types[1], Type.RASTER);
	}

	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.RASTER);
	}

	public boolean isAggregate() {
		return false;
	}
}