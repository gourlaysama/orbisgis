package org.orbisgis.renderer.classification;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.renderer.legend.Interval;

public class RangeMethod {

	private DataSource ds;
	private int nbCl;
	private Range[] ranges;
	private int rowCount;
	private String fieldName;

	public RangeMethod(DataSource ds, String fieldName, int nbCl)
			throws DriverException {
		this.ds = ds;
		// Number of ranges
		this.nbCl = nbCl;
		this.fieldName = fieldName;
		ranges = new Range[nbCl];
		rowCount = (int) ds.getRowCount();
	}

	/**
	 * Discrétisation quantiles : calcul des bornes et des tailles
	 *
	 * @throws DriverException
	 *
	 */
	public void disecQuantiles() throws DriverException {

		int i = 0;

		// Nombre d'individus par classes
		int nipc = rowCount / nbCl;
		int reste = rowCount % nbCl;
		// Calcul du nombre d'individus égal par classe
		for (i = 0; i < nbCl; i++) {
			// Répartition des individus dans les classes
			ranges[i] = new Range();
			ranges[i].setNumberOfItems(nipc);
			ranges[i].setPartOfItems(nipc * 100 / rowCount);
		}
		for (i = 0; i < reste; i++) {
			// Répartition du reste éventuel
			ranges[i].setNumberOfItems(ranges[i].getNumberOfItems() + 1);
			ranges[i].setPartOfItems((nipc + 1) * 100 / rowCount);
		}
		// Calcul bornes
		int compteur = 0;
		double[] valeurs = ClassificationUtils.getSortedValues(ds, fieldName);
		for (i = 0; i < nbCl; i++) {
			ranges[i].setMinRange(valeurs[compteur]);
			compteur += ranges[i].getNumberOfItems();
			if (compteur > (rowCount - 1))
				compteur = rowCount - 1;
			ranges[i].setMaxRange(valeurs[compteur]);
		}
	}

	/**
	 * Discretisation par equivalence
	 *
	 * @throws DriverException
	 *
	 */
	public void disecEquivalences() throws DriverException {
		// Discrétisation équivalences : calcul des bornes et des tailles
		int i = 0;
		double[] valeurs = ClassificationUtils.getSortedValues(ds, fieldName);
		double min = valeurs[0];
		double max = valeurs[rowCount - 1];
		double largeur = (max - min) / nbCl;
		int compteur = 0;
		int clec = 0;
		double debClec = 0;
		int dernier = 0;
		// Calcul bornes pour des classes de méme largeur
		ranges[0] = new Range();
		ranges[0].setMinRange(valeurs[0]);
		debClec = valeurs[0];
		for (i = 0; i < rowCount; i++) {
			compteur += 1;
			dernier += 1;

			if (valeurs[i] > (debClec + largeur)) {
				ranges[clec].setMaxRange(valeurs[i]);
				ranges[clec].setNumberOfItems(compteur - 1);
				ranges[clec].setPartOfItems((compteur - 1) * 100 / rowCount);
				compteur = 0;
				debClec = valeurs[i];
				if (clec < (nbCl - 1))
					clec += 1;
				else
					break;
				ranges[clec] = new Range();
				ranges[clec].setMinRange(valeurs[i]);
			}
		}
		if ((clec - 1) < (nbCl - 2)) {
			int diff = (nbCl - 2) - (clec - 1);
			for (i = 0; i < diff; i++) {
				ranges[clec + i] = new Range();
				ranges[clec + i].setMinRange(valeurs[rowCount - 1]);
				ranges[clec + i].setMaxRange(valeurs[rowCount - 1]);
				ranges[clec + i].setNumberOfItems(0);
				ranges[clec + i].setPartOfItems(0);
			}
		}
		ranges[nbCl - 1] = new Range();
		ranges[nbCl - 1].setMinRange(ranges[nbCl - 2].getMaxRange());
		ranges[nbCl - 1].setMaxRange(valeurs[rowCount - 1]);
		ranges[nbCl - 1].setNumberOfItems(rowCount - dernier + 1);
		ranges[nbCl - 1].setPartOfItems((rowCount - dernier + 1) * 100
				/ rowCount);
	}

	/**
	 * Discretisation par moyennes
	 *
	 * @throws DriverException
	 *
	 */
	public void disecMoyennes() throws DriverException {

		double[] valeurs = ClassificationUtils.getSortedValues(ds, fieldName);
		double min = valeurs[0];
		double max = valeurs[rowCount - 1];
		double M = 0;
		double Ma = 0, Ma1 = 0, Ma2 = 0;
		double Mb = 0, Mb1 = 0, Mb2 = 0;
		int Mi = 0;
		int Mai = 0, Ma1i = 0, Ma2i = 0;
		int Mbi = 0, Mb1i = 0, Mb2i = 0;
		// Modification si besoin est du nombre de classes
		if (nbCl != 4 && nbCl != 8) {
			if (Math.abs((nbCl - 4)) < Math.abs((nbCl - 8)))
				nbCl = 4;
			else
				nbCl = 8;
			// todo add a message dialog
		}
		M = getMoyenne(valeurs, 0, rowCount);
		Mi = getIndice(valeurs, M);
		Ma = getMoyenne(valeurs, 0, Mi);
		Mai = getIndice(valeurs, Ma);
		Ma1 = getMoyenne(valeurs, 0, Mai);
		Ma1i = getIndice(valeurs, Ma1);
		Ma2 = getMoyenne(valeurs, Mai, Mi);
		Ma2i = getIndice(valeurs, Ma2);
		Mb = getMoyenne(valeurs, Mi, rowCount);
		Mbi = getIndice(valeurs, Mb);
		Mb1 = getMoyenne(valeurs, Mi, Mbi);
		Mb1i = getIndice(valeurs, Mb1);
		Mb2 = getMoyenne(valeurs, Mbi, rowCount);
		Mb2i = getIndice(valeurs, Mb2);
		if (nbCl == 4) {
			ranges[0] = new Range();
			ranges[1] = new Range();
			ranges[2] = new Range();
			ranges[3] = new Range();

			ranges[0].setMinRange(min);
			ranges[0].setMaxRange(valeurs[Mai]);
			ranges[0].setNumberOfItems(Mai - 1);
			ranges[0].setPartOfItems((Mai - 1) * 100 / rowCount);

			ranges[1].setMinRange(valeurs[Mai]);
			ranges[1].setMaxRange(valeurs[Mi]);
			ranges[1].setNumberOfItems((Mi - 1) - (Mai - 1));
			ranges[1].setPartOfItems(((Mi - 1) - (Mai - 1)) * 100 / rowCount);

			ranges[2].setMinRange(valeurs[Mi]);
			ranges[2].setMaxRange(valeurs[Mbi]);
			ranges[2].setNumberOfItems((Mbi - 1) - (Mi - 1));
			ranges[2].setPartOfItems(((Mbi - 1) - (Mi - 1)) * 100 / rowCount);

			ranges[3].setMinRange(valeurs[Mbi]);
			ranges[3].setMaxRange(max);
			ranges[3].setNumberOfItems(rowCount - (Mbi - 1));
			ranges[3].setPartOfItems((rowCount - (Mbi - 1)) * 100 / rowCount);

		}
		if (nbCl == 8) {

			ranges[0] = new Range();
			ranges[1] = new Range();
			ranges[2] = new Range();
			ranges[3] = new Range();
			ranges[4] = new Range();
			ranges[5] = new Range();
			ranges[6] = new Range();
			ranges[7] = new Range();

			ranges[0].setMinRange(min);
			ranges[0].setMaxRange(valeurs[Ma1i]);
			ranges[0].setNumberOfItems(Ma1i - 1);
			ranges[0].setPartOfItems((Ma1i - 1) * 100 / rowCount);

			ranges[1].setMinRange(valeurs[Ma1i]);
			ranges[1].setMaxRange(valeurs[Mai]);
			ranges[1].setNumberOfItems((Mai - 1) - (Ma1i - 1));
			ranges[1].setPartOfItems(((Mai - 1) - (Ma1i - 1)) * 100 / rowCount);

			ranges[2].setMinRange(valeurs[Mai]);
			ranges[2].setMaxRange(valeurs[Ma2i]);
			ranges[2].setNumberOfItems((Ma2i - 1) - (Mai - 1));
			ranges[2].setPartOfItems(((Ma2i - 1) - (Mai - 1)) * 100 / rowCount);

			ranges[3].setMinRange(valeurs[Ma2i]);
			ranges[3].setMaxRange(valeurs[Mi]);
			ranges[3].setNumberOfItems((Mi - 1) - (Ma2i - 1));
			ranges[3].setPartOfItems(((Mi - 1) - (Ma2i - 1)) * 100 / rowCount);

			ranges[4].setMinRange(valeurs[Mi]);
			ranges[4].setMaxRange(valeurs[Mb1i]);
			ranges[4].setNumberOfItems((Mb1i - 1) - (Mi - 1));
			ranges[4].setPartOfItems(((Mb1i - 1) - (Mi - 1)) * 100 / rowCount);

			ranges[5].setMinRange(valeurs[Mb1i]);
			ranges[5].setMaxRange(valeurs[Mbi]);
			ranges[5].setNumberOfItems((Mbi - 1) - (Mb1i - 1));
			ranges[5].setPartOfItems(((Mbi - 1) - (Mb1i - 1)) * 100 / rowCount);

			ranges[6].setMinRange(valeurs[Mbi]);
			ranges[6].setMaxRange(valeurs[Mb2i]);
			ranges[6].setNumberOfItems((Mb2i - 1) - (Mbi - 1));
			ranges[6].setPartOfItems(((Mb2i - 1) - (Mbi - 1)) * 100 / rowCount);

			ranges[7].setMinRange(valeurs[Mb2i]);
			ranges[7].setMaxRange(max);
			ranges[7].setNumberOfItems(rowCount - (Mb2i - 1));
			ranges[7].setPartOfItems((rowCount - (Mb2i - 1)) * 100 / rowCount);

		}
	}

	/**
	 * Standart discretization
	 *
	 * @throws DriverException
	 *
	 */
	public void disecStandard() throws DriverException {
		// Discrétisation équivalences : calcul des bornes et des tailles
		double[] valeurs = ClassificationUtils.getSortedValues(ds, fieldName);
		double moyenne = getMoyenne(valeurs, 0, valeurs.length);
		double ec = getEcType(valeurs);
		if ((moyenne - (ec / 2)) < valeurs[0])
			if (nbCl != 3 && nbCl != 5 && nbCl != 7) {
				int ac3 = Math.abs((nbCl - 3));
				int ac5 = Math.abs((nbCl - 5));
				int ac7 = Math.abs((nbCl - 7));
				if (ac3 == Math.min(ac3, Math.min(ac5, ac7)))
					nbCl = 3;
				if (ac5 == Math.min(ac3, Math.min(ac5, ac7)))
					nbCl = 5;
				if (ac7 == Math.min(ac3, Math.min(ac5, ac7)))
					nbCl = 7;
			}
		int compteur = 0;
		int compteurI = 0;

		switch (nbCl) {
		case 3:
			ranges[0] = new Range();
			ranges[1] = new Range();
			ranges[2] = new Range();

			ranges[0].setMinRange(valeurs[0]);
			while (valeurs[compteur] < (moyenne - (ec / 2))) {
				compteur += 1;
			}
			ranges[0].setMaxRange(valeurs[compteur]);
			ranges[0].setNumberOfItems(compteur);
			ranges[0].setPartOfItems(compteur * 100 / valeurs.length);
			ranges[1].setMinRange(valeurs[compteur]);
			while (valeurs[compteur] < (moyenne + (ec / 2))) {
				compteur += 1;
				compteurI += 1;
			}
			ranges[1].setMaxRange(valeurs[compteur]);
			ranges[1].setNumberOfItems(compteurI);
			ranges[1].setPartOfItems(compteurI * 100 / valeurs.length);

			ranges[2].setMinRange(valeurs[compteur]);
			ranges[2].setMaxRange(valeurs[valeurs.length - 1]);
			ranges[2].setNumberOfItems(valeurs.length - compteur);
			ranges[2].setPartOfItems((valeurs.length - compteur) * 100
					/ valeurs.length);
			break;
		case 5:
			ranges[0] = new Range();
			ranges[1] = new Range();
			ranges[2] = new Range();
			ranges[3] = new Range();
			ranges[4] = new Range();
			ranges[0].setMinRange(valeurs[0]);
			while (valeurs[compteur] < (moyenne - (ec * 1.5))) {
				compteur += 1;
			}
			ranges[0].setMaxRange(valeurs[compteur]);
			ranges[0].setNumberOfItems(compteur);
			ranges[0].setPartOfItems(compteur * 100 / valeurs.length);
			ranges[1].setMinRange(valeurs[compteur]);
			while (valeurs[compteur] < (moyenne - (ec / 2))) {
				compteur += 1;
				compteurI += 1;
			}
			ranges[1].setMaxRange(valeurs[compteur]);
			ranges[1].setNumberOfItems(compteurI);
			ranges[1].setPartOfItems(compteurI * 100 / valeurs.length);
			ranges[2].setMinRange(valeurs[compteur]);
			compteurI = 0;
			while (valeurs[compteur] < (moyenne + (ec / 2))) {
				compteur += 1;
				compteurI += 1;
			}
			ranges[2].setMaxRange(valeurs[compteur]);
			ranges[2].setNumberOfItems(compteurI);
			ranges[2].setPartOfItems(compteurI * 100 / valeurs.length);

			ranges[3].setMinRange(valeurs[compteur]);
			compteurI = 0;
			while (valeurs[compteur] < (moyenne + (ec * 1.5))) {
				compteur += 1;
				compteurI += 1;
			}

			ranges[3].setMaxRange(valeurs[compteur]);
			ranges[3].setNumberOfItems(compteurI);
			ranges[3].setPartOfItems(compteurI * 100 / valeurs.length);

			ranges[4].setMinRange(valeurs[compteur]);
			ranges[4].setMaxRange(valeurs[valeurs.length - 1]);
			ranges[4].setNumberOfItems(valeurs.length - compteur);
			ranges[4].setPartOfItems((valeurs.length - compteur) * 100
					/ valeurs.length);

			break;
		case 7:
			ranges[0] = new Range();
			ranges[1] = new Range();
			ranges[2] = new Range();
			ranges[3] = new Range();
			ranges[4] = new Range();
			ranges[5] = new Range();
			ranges[6] = new Range();
			ranges[0].setMinRange(valeurs[0]);
			while (valeurs[compteur] < (moyenne - (ec * 2.5))) {
				compteur += 1;
			}

			ranges[0].setMaxRange(valeurs[compteur]);
			ranges[0].setNumberOfItems(compteur);
			ranges[0].setPartOfItems(compteur * 100 / valeurs.length);

			ranges[1].setMinRange(valeurs[compteur]);
			while (valeurs[compteur] < (moyenne - (ec * 1.5))) {
				compteur += 1;
				compteurI += 1;
			}
			ranges[1].setMaxRange(valeurs[compteur]);
			ranges[1].setNumberOfItems(compteurI);
			ranges[1].setPartOfItems(compteurI * 100 / valeurs.length);

			ranges[2].setMinRange(valeurs[compteur]);

			compteurI = 0;
			while (valeurs[compteur] < (moyenne - (ec / 2))) {
				compteur += 1;
				compteurI += 1;
			}

			ranges[2].setMaxRange(valeurs[compteur]);
			ranges[2].setNumberOfItems(compteurI);
			ranges[2].setPartOfItems(compteurI * 100 / valeurs.length);

			ranges[3].setMinRange(valeurs[compteur]);

			compteurI = 0;
			while (valeurs[compteur] < (moyenne + (ec / 2))) {
				compteur += 1;
				compteurI += 1;
			}

			ranges[3].setMaxRange(valeurs[compteur]);
			ranges[3].setNumberOfItems(compteurI);
			ranges[3].setPartOfItems(compteurI * 100 / valeurs.length);

			ranges[4].setMinRange(valeurs[compteur]);

			compteurI = 0;
			while (valeurs[compteur] < (moyenne + (ec * 1.5))) {
				compteur += 1;
				compteurI += 1;
			}

			ranges[4].setMaxRange(valeurs[compteur]);
			ranges[4].setNumberOfItems(compteurI);
			ranges[4].setPartOfItems(compteurI * 100 / valeurs.length);

			ranges[5].setMinRange(valeurs[compteur]);

			compteurI = 0;
			while (valeurs[compteur] < (moyenne - (ec * 2.5))) {
				compteur += 1;
				compteurI += 1;
			}
			ranges[5].setMaxRange(valeurs[compteur]);
			ranges[5].setNumberOfItems(compteurI);
			ranges[5].setPartOfItems(compteurI * 100 / valeurs.length);

			ranges[6].setMinRange(valeurs[compteur]);
			ranges[6].setMaxRange(valeurs[valeurs.length - 1]);
			ranges[6].setNumberOfItems(valeurs.length - compteur);
			ranges[6].setPartOfItems((valeurs.length - compteur) * 100
					/ valeurs.length);

		}
	}

	private double getMoyenne(double[] valeurs, int debut, int bout) {
		// Calcul de la moyenne de la variable en cours entre deux individus
		// triés
		int i = 0;
		double somme = 0;
		double moyenne = 0;
		for (i = debut; i < bout; i++) {
			somme += valeurs[i];
		}
		moyenne = somme / (bout - debut);
		return moyenne;
	}

	private int getIndice(double[] valeurs, double element) {
		int i = 0;
		for (i = 0; i < valeurs.length; i++) {
			if (valeurs[i] > element) {
				return i;
			}
		}
		return 0;
	}

	private double getEcType(double[] valeurs) {
		int i = 0;
		double somme = 0;
		double moyenne = getMoyenne(valeurs, 0, valeurs.length);
		for (i = 0; i < valeurs.length; i++) {
			somme += Math.pow((moyenne - valeurs[i]), 2);
		}
		return Math.sqrt((somme / valeurs.length));
	}

	public Range[] getRanges() {
		return ranges;

	}

	public Interval[] getIntervals() throws NumberFormatException,
			java.text.ParseException {
		Value val1 = null;
		Value val2 = null;
		Interval[] intervals = new Interval[ranges.length];

		for (int i = 0; i < ranges.length; i++) {
			Range ran = ranges[i];
			val1 = ValueFactory.createValue(ran.getMinRange());
			val2 = ValueFactory.createValue(ran.getMaxRange());

			Interval inter = new Interval(val1, true, val2, false);
			intervals[i] = inter;
		}
		return intervals;
	}

}
