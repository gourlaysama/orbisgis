package org.orbisgis.geocatalog.resources;

import org.orbisgis.geocatalog.Catalog;
import org.orbisgis.geocatalog.IResourceAction;

public class ClearCatalogAction implements IResourceAction {

	public boolean accepts(IResource currentNode) {
		return true;
	}

	public void execute(Catalog catalog, IResource currentNode) {
		catalog.getCatalogModel().removeAllNodes();
	}

	public boolean acceptsEmptySelection() {
		return true;
	}

}
