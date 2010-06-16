/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.gdms.sql.strategies;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.parser.ParseException;
import org.orbisgis.progress.IProgressMonitor;

public class CreateViewOperator extends AbstractOperator implements Operator {

	private String viewName;
	private String statement;
	private DataSourceFactory dsf;

	public CreateViewOperator(String viewName, String statement,
			DataSourceFactory dsf) {
		this.viewName = viewName;
		this.statement = statement;
		this.dsf = dsf;
	}

	protected ObjectDriver getResultContents(IProgressMonitor pm) {
		try {
			dsf.getSourceManager().register(viewName, statement);
		} catch (SourceAlreadyExistsException e) {
			new ExecutionException("Cannot register view: " + viewName, e);
		} catch (ParseException e) {
			new ParseException("Cannot parse : " + statement);
		} catch (SemanticException e) {
			new SemanticException(
					"Cannot create view. The source already exists: "
							+ viewName);
		} catch (DriverException e) {
			new ExecutionException("Cannot create view:" + viewName, e);
		}
		return null;

	}

	public Metadata getResultMetadata() throws DriverException {
		return null;
	}

}
