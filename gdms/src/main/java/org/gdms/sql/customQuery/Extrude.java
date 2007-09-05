package org.gdms.sql.customQuery;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.DoubleValue;
import org.gdms.data.values.StringValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.spatial.GeometryValue;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.gdms.sql.strategies.FirstStrategy;

import com.hardcode.driverManager.DriverLoadException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

// call EXTRUDE from faceTrouee values ('id', 'the_geom', 'high');
public class Extrude implements CustomQuery {

	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {
		if (tables.length != 1)
			throw new ExecutionException("Extrude3D only operates on one table");
		if (values.length != 3)
			throw new ExecutionException(
					"Extrude3D only operates with three fields names (string values)");

		DataSource resultDs = null;
		try {
			final String gidFieldName = ((StringValue) values[0]).getValue();
			final String geomFieldName = ((StringValue) values[1]).getValue();
			final String highFieldName = ((StringValue) values[2]).getValue();

			final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
					tables[0]);
			sds.open();

			final int gidFieldIndex = sds.getFieldIndexByName(gidFieldName);
			// final int geomFieldIndex =
			// sds.getFieldIndexByName(geomFieldName);
			final int highFieldIndex = sds.getFieldIndexByName(highFieldName);
			sds.setDefaultGeometry(geomFieldName);

			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					new String[] { "gid", "shellHoleId", "type", "index",
							"the_geom" }, new Type[] {
							TypeFactory.createType(Type.STRING),
							TypeFactory.createType(Type.SHORT),
							TypeFactory.createType(Type.STRING),
							TypeFactory.createType(Type.SHORT),
							TypeFactory.createType(Type.GEOMETRY) });
			resultDs = dsf.getDataSource(driver);
			resultDs.open();
			final GeometryFactory geometryFactory = new GeometryFactory();

			final int rowCount = (int) sds.getRowCount();
			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				// TODO
				// "sds.getPK(rowIndex)" should replace
				// "sds.getFieldValue(rowIndex, gidFieldIndex)"
				final Value gid = sds.getFieldValue(rowIndex, gidFieldIndex);
				final double high = ((DoubleValue) sds.getFieldValue(rowIndex,
						highFieldIndex)).getValue();
				final Geometry g = sds.getGeometry(rowIndex);

				if (g instanceof Polygon) {
					final Polygon p = ((Polygon) g);
					Value wallType = ValueFactory.createValue("wall");
					/* exterior ring */
					final LineString shell = p.getExteriorRing();
					Value shellHoleId = ValueFactory.createValue((short) -1);
					for (int i = 1; i < shell.getNumPoints(); i++) {
						final Polygon wall = extrudeEdge(geometryFactory, shell
								.getCoordinateN(i - 1),
								shell.getCoordinateN(i), high);

						resultDs.insertFilledRow(new Value[] { gid,
								shellHoleId, wallType,
								ValueFactory.createValue((short) (i - 1)),
								new GeometryValue(wall) });
					}
					/* holes */
					final int nbOfHoles = p.getNumInteriorRing();
					for (int i = 0; i < nbOfHoles; i++) {
						final LineString hole = p.getInteriorRingN(i);
						shellHoleId = ValueFactory.createValue((short) i);
						for (int j = 1; j < hole.getNumPoints(); j++) {
							final Polygon wall = extrudeEdge(geometryFactory,
									hole.getCoordinateN(j - 1), hole
											.getCoordinateN(j), high);

							resultDs.insertFilledRow(new Value[] { gid,
									shellHoleId, wallType,
									ValueFactory.createValue((short) (j - 1)),
									new GeometryValue(wall) });
						}
					}

					/* floor */
					shellHoleId = ValueFactory.createValue((short) -1);
					wallType = ValueFactory.createValue("floor");

					resultDs.insertFilledRow(new Value[] { gid, shellHoleId,
							wallType, ValueFactory.createValue((short) 0),
							new GeometryValue(p) });

					/* ceiling */
					wallType = ValueFactory.createValue("ceiling");

					final LinearRing upperShell = translate(geometryFactory, p
							.getExteriorRing(), high);
					final LinearRing[] holes = new LinearRing[nbOfHoles];
					for (int i = 0; i < nbOfHoles; i++) {
						holes[i] = translate(geometryFactory, p
								.getInteriorRingN(i), high);
					}
					Polygon pp = geometryFactory.createPolygon(upperShell,
							holes);

					resultDs.insertFilledRow(new Value[] { gid, shellHoleId,
							wallType, ValueFactory.createValue((short) 0),
							new GeometryValue(pp) });
				}
			}
			resultDs.commit();
			sds.cancel();
			// spatial index for the new grid
			dsf.getIndexManager().buildIndex(resultDs.getName(), "the_geom",
					SpatialIndex.SPATIAL_INDEX);
			FirstStrategy.indexes = true;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (InvalidTypeException e) {
			throw new ExecutionException(e);
		} catch (DriverLoadException e) {
			throw new ExecutionException(e);
		} catch (DataSourceCreationException e) {
			throw new ExecutionException(e);
		} catch (FreeingResourcesException e) {
			throw new ExecutionException(e);
		} catch (NonEditableDataSourceException e) {
			throw new ExecutionException(e);
		} catch (IndexException e) {
			throw new ExecutionException(e);
		} catch (NoSuchTableException e) {
			throw new ExecutionException(e);
		}
		return resultDs;
	}

	private Polygon extrudeEdge(final GeometryFactory geometryFactory,
			final Coordinate beginPoint, Coordinate endPoint, final double high) {
		return geometryFactory.createPolygon(geometryFactory
				.createLinearRing(new Coordinate[] {
						beginPoint,
						new Coordinate(beginPoint.x, beginPoint.y, beginPoint.z
								+ high),
						new Coordinate(endPoint.x, endPoint.y, endPoint.z
								+ high), endPoint, beginPoint }), null);
	}

	private LinearRing translate(final GeometryFactory geometryFactory,
			final LineString ring, final double high) {
		final Coordinate[] arrayOfCoords = ring.getCoordinates();
		for (Coordinate item : arrayOfCoords) {
			item.z += high;
		}
		return geometryFactory.createLinearRing(arrayOfCoords);
	}

	public String getName() {
		return "EXTRUDE";
	}
}