package org.orbisgis.core.ui.plugins.views.sqlConsole;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.parser.TokenMgrError;
import org.gdms.sql.strategies.Instruction;
import org.gdms.sql.strategies.SQLProcessor;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.MapContextManager;
import org.orbisgis.core.ui.plugins.views.OutputManager;
import org.orbisgis.progress.IProgressMonitor;

public class ExecuteScriptProcess implements BackgroundJob {

	private String script;
	

	private static final Logger logger = Logger
			.getLogger(ExecuteScriptProcess.class);

	public ExecuteScriptProcess(String script) {
		this.script = script;
	}

	public String getTaskName() {
		return "Executing script";
	}

	public void run(IProgressMonitor pm) {

		DataManager dataManager = (DataManager) Services
				.getService(DataManager.class);
		DataSourceFactory dsf = dataManager.getDSF();
		SQLProcessor sqlProcessor = new SQLProcessor(dsf);
		String[] instructions = new String[0];

		long t1 = System.currentTimeMillis();
		try {
			logger.debug("Preparing script: " + script);
			try {
				instructions = sqlProcessor.getScriptInstructions(script);
			} catch (SemanticException e) {
				Services.getErrorManager().error(
						"Semantic error in the script", e);
			} catch (ParseException e) {
				Services.getErrorManager().error("Cannot parse script", e);
			} catch (TokenMgrError e) {
				Services.getErrorManager().error("Cannot parse script", e);
			}

			MapContext vc = ((MapContextManager) Services
					.getService(MapContextManager.class))
					.getActiveMapContext();

			for (int i = 0; i < instructions.length; i++) {

				logger.debug("Preparing instruction: " + instructions[i]);
				try {
					Instruction instruction = sqlProcessor
							.prepareInstruction(instructions[i]);

					Metadata metadata = instruction.getResultMetadata();
					if (metadata != null) {
						boolean spatial = false;
						for (int k = 0; k < metadata.getFieldCount(); k++) {
							int typeCode = metadata.getFieldType(k)
									.getTypeCode();
							if ((typeCode == Type.GEOMETRY)
									|| (typeCode == Type.RASTER)) {
								spatial = true;
							}
						}

						DataSource ds = dsf.getDataSource(instruction,
								DataSourceFactory.DEFAULT, pm);

						if (pm.isCancelled()) {
							break;
						}

						if (spatial && vc != null) {

							try {
								final ILayer layer = dataManager
										.createLayer(ds);

								vc.getLayerModel().insertLayer(layer, 0);

							} catch (LayerException e) {
								Services.getErrorManager().error(
										"Impossible to create the layer:"
												+ ds.getName(), e);
								break;
							}
						} else {

							ds.open();
							StringBuilder aux = new StringBuilder();
							int fc = ds.getMetadata().getFieldCount();
							int rc = (int) ds.getRowCount();

							for (int j = 0; j < fc; j++) {
								aux.append(ds.getFieldName(j)).append("\t");
							}
							aux.append("\n");
							for (int row = 0; row < rc; row++) {
								for (int j = 0; j < fc; j++) {
									aux.append(ds.getFieldValue(row, j))
											.append("\t");
								}
								aux.append("\n");
								if (row > 100) {
									aux.append("and more... total " + rc
											+ " rows");
									break;
								}
							}
							ds.close();

							OutputManager om = Services
									.getService(OutputManager.class);
							om.println(aux.toString());
						}
					} else {
						instruction.execute(pm);

						if (pm.isCancelled()) {
							break;
						}

					}
				} catch (ExecutionException e) {
					Services.getErrorManager().error(
							"Error executing the instruction:"
									+ instructions[i], e);
					break;
				} catch (SemanticException e) {
					Services.getErrorManager().error(
							"Semantic error in instruction:"
									+ instructions[i], e);
					break;
				} catch (DataSourceCreationException e) {
					Services.getErrorManager().error(
							"Cannot create the DataSource:"
									+ instructions[i], e);
					break;
				} catch (ParseException e) {
					Services.getErrorManager().error(
							"Parse error in statement:" + instructions[i],
							e);
					break;
				}

				pm.progressTo(100 * i / instructions.length);
			}

		} catch (DriverException e) {
			Services.getErrorManager().error("Data access error:", e);
		}

		long t2 = System.currentTimeMillis();
		logger.debug("Execution time: " + ((t2 - t1) / 1000.0));
	}


}