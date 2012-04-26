/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
package org.gdms.driver.sql;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.values.Value;
import org.gdms.driver.AbstractDataSet;
import org.gdms.driver.DataSet;
import org.gdms.driver.Driver;
import org.gdms.driver.DriverException;
import org.gdms.driver.MemoryDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;
import org.gdms.sql.engine.SQLStatement;

/**
 *
 * @author Antoine Gourlay
 */
public class SqlStatementDriver extends AbstractDataSet implements MemoryDriver {

        private SQLStatement sql;
        private DataSourceFactory dsf;
        private DefaultSchema schema;
        private DataSet set;
        private DefaultMetadata metadata = new DefaultMetadata();

        public SqlStatementDriver(SQLStatement sql, DataSourceFactory dsf) throws DriverException {
                this.sql = sql;
                this.dsf = dsf;
                schema = new DefaultSchema("sql");
                schema.addTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME, metadata);
        }

        @Override
        public void open() throws DriverException {
                sql.setDataSourceFactory(dsf);
                sql.prepare();
                set = sql.execute();
                metadata.clear();
                metadata.addAll(sql.getResultMetadata());
        }

        @Override
        public void close() throws DriverException {
                sql.cleanUp();
        }

        @Override
        public Schema getSchema() throws DriverException {
                return schema;
        }

        @Override
        public DataSet getTable(String name) {
                if (DriverManager.DEFAULT_SINGLE_TABLE_NAME.equals(name)) {
                        return this;
                }
                return null;
        }

        @Override
        public void setDataSourceFactory(DataSourceFactory dsf) {
                if (dsf instanceof DataSourceFactory) {
                        this.dsf = (DataSourceFactory) dsf;
                }
                throw new UnsupportedOperationException();
        }

        @Override
        public int getSupportedType() {
                return SourceManager.LIVE | SourceManager.MEMORY | SourceManager.SQL;
        }

        @Override
        public int getType() {
                if (set instanceof Driver) {
                        return getSupportedType() | ((Driver) set).getType();
                } else {
                        return getSupportedType();
                }
        }

        @Override
        public String getTypeName() {
                return "SQL";
        }

        @Override
        public String getTypeDescription() {
                return "SQL view";
        }

        @Override
        public String getDriverId() {
                return "SQLView";
        }

        @Override
        public boolean isCommitable() {
                return false;
        }

        @Override
        public TypeDefinition[] getTypesDefinitions() {
                throw new UnsupportedOperationException();
        }

        @Override
        public String validateMetadata(Metadata metadata) throws DriverException {
                throw new UnsupportedOperationException();
        }

        @Override
        public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
                return set.getFieldValue(rowIndex, fieldId);
        }

        @Override
        public long getRowCount() throws DriverException {
                return set.getRowCount();
        }

        @Override
        public Number[] getScope(int dimension) throws DriverException {
                return set.getScope(dimension);
        }

        @Override
        public Metadata getMetadata() throws DriverException {
                return set.getMetadata();
        }
}