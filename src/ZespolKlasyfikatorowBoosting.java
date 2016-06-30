import java.util.ArrayList;
import java.util.Random;

public class ZespolKlasyfikatorowBoosting extends ZespolKlasyfikatorow {
	public double[][] tablicaWektorowWagObiektow;
	public double[] wagiKlasyfikatorow;
	ArrayList<KlasyfikatorKNN> listaKlasyfikatorow;
	
	public ZespolKlasyfikatorowBoosting(int liczbaSasiadow, String metryka, String metodaGlosowania, ArrayList<Obiekt> daneUczace, int liczbaKlasyfikatorowWZespole) {
		super(liczbaSasiadow, metryka, metodaGlosowania, daneUczace, liczbaKlasyfikatorowWZespole);
		tablicaWektorowWagObiektow = new double[liczbaKlasyfikatorowWZespole][daneUczace.size()];
		//ustalWagiDlaPierwszegoKlasyfikatora();
		wagiKlasyfikatorow = new double[liczbaKlasyfikatorowWZespole];
		generujListeKlasyfikatorow();
		//ustalWagiKlasyfikatorow();
	}

	@Override
	public String getClass(Obiekt obiekt_testowy) {
		String[] tablicaZwroconychEtykietKlas = new String[listaKlasyfikatorow.size()];
		KlasyfikatorKNN klasyfikator;
		for(int i = 0; i < listaKlasyfikatorow.size(); i++){
			klasyfikator = listaKlasyfikatorow.get(i);
			tablicaZwroconychEtykietKlas[i] = klasyfikator.getClass(obiekt_testowy);
		}
		String etykietaKlasy = getEtykietaKlasyGlosowanieWazone(tablicaZwroconychEtykietKlas);
		return etykietaKlasy;
	}
	/*
	private double[] obliczEpsilonDlaKazdegoKlasyfikatora(){
		double[] epsilon = new double[listaKlasyfikatorow.size()];
		KlasyfikatorKNN rozwazanyKlasyfikator;
		Obiekt obiektTestowy;
		for(int i = 0; i < listaKlasyfikatorow.size(); i++){
			rozwazanyKlasyfikator = listaKlasyfikatorow.get(i);
			double[] wektorWagObiektowDlaRozwazanegoKlasyfikatora = tablicaWektorowWagObiektow[i];
			for(int j = 0; j < zbiorUczacy.size(); j++){
				obiektTestowy = zbiorUczacy.get(j);
				if( !czyKlasyfikacjaPoprawna(rozwazanyKlasyfikator, obiektTestowy) ){
					epsilon[i] += wektorWagObiektowDlaRozwazanegoKlasyfikatora[j];
				}
			}
		}
		return epsilon;
	}*/
	
	private void generujListeKlasyfikatorow(){
		listaKlasyfikatorow = new ArrayList<KlasyfikatorKNN>();
		double[] wektorWagObiektow = getPierwszyWektorWag();
		tablicaWektorowWagObiektow[0] = wektorWagObiektow;
		ArrayList<Obiekt> pseudozbiorUczacy = getPseudozbiorUczacy(wektorWagObiektow);
		KlasyfikatorKNN aktualnyKlasyfikator = new KlasyfikatorKNN(liczbaSasiadow, metryka, metodaGlosowania, pseudozbiorUczacy);
		listaKlasyfikatorow.add(aktualnyKlasyfikator);
		ustalWageKlasyfikatora(0);
		for(int i = 1; i < liczbaKlasyfikatorowWZespole; i++){
			wektorWagObiektow = getKolejnyWektorWag(wektorWagObiektow, aktualnyKlasyfikator, i-1);
			tablicaWektorowWagObiektow[i] = wektorWagObiektow;
			pseudozbiorUczacy = getPseudozbiorUczacy(wektorWagObiektow);
			aktualnyKlasyfikator = new KlasyfikatorKNN(liczbaSasiadow, metryka, metodaGlosowania, pseudozbiorUczacy);
			listaKlasyfikatorow.add(aktualnyKlasyfikator);
			ustalWageKlasyfikatora(i);
		}
	}
	
	private void ustalWageKlasyfikatora(int indexRozwazanegoKlasyfikatora){
		//double[] epsilon = obliczEpsilonDlaKazdegoKlasyfikatora();
		double epsilon = obliczEpsilonKlasyfikatora(indexRozwazanegoKlasyfikatora);
		wagiKlasyfikatorow[indexRozwazanegoKlasyfikatora] = 0.5 * Math.log( (1 - epsilon) / epsilon );
	}
	
	private double obliczEpsilonKlasyfikatora(int indexRozwazanegoKlasyfikatora){
		double epsilon = 0;
		KlasyfikatorKNN rozwazanyKlasyfikator;
		Obiekt obiektTestowy;
		rozwazanyKlasyfikator = listaKlasyfikatorow.get(indexRozwazanegoKlasyfikatora);
		double[] wektorWagObiektowDlaRozwazanegoKlasyfikatora = tablicaWektorowWagObiektow[indexRozwazanegoKlasyfikatora];
		for(int j = 0; j < zbiorUczacy.size(); j++){
			obiektTestowy = zbiorUczacy.get(j);
			if( !czyKlasyfikacjaPoprawna(rozwazanyKlasyfikator, obiektTestowy) ){
				epsilon += wektorWagObiektowDlaRozwazanegoKlasyfikatora[j];
			}
		}
		return epsilon;
	}
	
	private double[] getPierwszyWektorWag(){
		double[] wektorWag = new double[zbiorUczacy.size()];
		for(int i = 0; i < zbiorUczacy.size(); i++)
			wektorWag[i] = 1.0 / zbiorUczacy.size();
		return wektorWag;
	}

	private double[] getKolejnyWektorWag(double[] poprzedniWektorWag, KlasyfikatorKNN aktualnyKlasyfikator, int indexKlasyfikatora){
		double[] wektorWag = poprzedniWektorWag;
		Obiekt obiektTestowy;
		for(int i = 0; i < zbiorUczacy.size(); i++){
			obiektTestowy = zbiorUczacy.get(i);
			if( czyKlasyfikacjaPoprawna(aktualnyKlasyfikator, obiektTestowy) ){
				wektorWag[i] = zmodyfikujWage(indexKlasyfikatora, wektorWag[i], true);
			} else {
				wektorWag[i] = zmodyfikujWage(indexKlasyfikatora, wektorWag[i], false);
			}
		}
		wektorWag = normalizujWektorWag(wektorWag);
		return wektorWag;
	}
	/*
	private double[] getWektorWag(int numerKlasyfikatora){
		double[] wektorWag = new double[zbiorUczacy.size()];
		//for(int i = 0; i < wektorWag.length; i++){
			//wektorWag[i] = tablicaWektorowWag[numerKlasyfikatora][i];
		//}
		//tablicaWektorowWag[numerKlasyfikatora];
		KlasyfikatorKNN aktualnyKlasyfikator = listaKlasyfikatorow.get(numerKlasyfikatora);
		Obiekt obiektTestowy;
		for(int i = 0; i < zbiorUczacy.size(); i++){
			obiektTestowy = zbiorUczacy.get(i);
			if( !czyKlasyfikacjaPoprawna(aktualnyKlasyfikator, obiektTestowy) ){
				wektorWag[i] = zwiekszWage(wektorWag[i]);
			}
		}
		wektorWag = normalizujWektorWag(wektorWag);
		return wektorWag;
	}*/
	
	private double zmodyfikujWage(int indexKlasyfikatora, double waga, boolean czyKlasyfikatorOdpowiedzialPoprawnie){
		double zmodyfikowanaWaga;
		if( czyKlasyfikatorOdpowiedzialPoprawnie )
			zmodyfikowanaWaga = waga * Math.pow( Math.E, -wagiKlasyfikatorow[indexKlasyfikatora] );
		else
			zmodyfikowanaWaga = waga * Math.pow( Math.E, wagiKlasyfikatorow[indexKlasyfikatora] );
		return zmodyfikowanaWaga;
	}
	
	private double[] normalizujWektorWag(double[] wektorWag){
		double[] znormalizowanyWektorWag = new double[wektorWag.length];
		double suma = getSumaElementowTablicy(wektorWag);
		for(int i = 0; i < wektorWag.length; i++){
			znormalizowanyWektorWag[i] = wektorWag[i] / suma;
		}
		return znormalizowanyWektorWag;
	}
	
	private double getSumaElementowTablicy(double[] tablica){
		double suma = 0;
		for(int i = 0; i < tablica.length; i++){
			suma += tablica[i];
		}
		return suma;
	}
	
	private boolean czyKlasyfikacjaPoprawna(KlasyfikatorKNN klasyfikator, Obiekt obiektTestowy){
		if( klasyfikator.getClass(obiektTestowy).equals(obiektTestowy.getEtykietaKlasy()) )
			return true;
		return false;
	}
	
	private ArrayList<Obiekt> getPseudozbiorUczacy(double[] tablicaWag){
		double[] dystrybuantaDyskretna = getDystrybuantaDyskretna(tablicaWag);
		ArrayList<Obiekt> podzbior = new ArrayList<Obiekt>();
		int indexObiektuZeZbioruUczacego;
		for(int i = 0; i < zbiorUczacy.size(); i++){
			indexObiektuZeZbioruUczacego = getIndexObiektuZeZbioruUczacego(dystrybuantaDyskretna);
			podzbior.add(zbiorUczacy.get(indexObiektuZeZbioruUczacego));
		}
		return podzbior;
	}
	
	private int getIndexObiektuZeZbioruUczacego(double[] dystrybuantaDyskretna){
		Random rand = new Random();
		double randomNumber = rand.nextDouble();
		for(int i = 0; i < dystrybuantaDyskretna.length; i++){
			if( randomNumber < dystrybuantaDyskretna[i] ){
				return i;
			}
		}
		return 0;
	}
	
	private double[] getDystrybuantaDyskretna(double[] tablicaWag){
		double[] dystrybuantaDyskretna = new double[tablicaWag.length];
		dystrybuantaDyskretna[0] = tablicaWag[0];
		for(int i = 1; i < tablicaWag.length; i++){
			dystrybuantaDyskretna[i] = dystrybuantaDyskretna[i-1] + tablicaWag[i];
		}
		return dystrybuantaDyskretna;
	}
	
	private String getEtykietaKlasyGlosowanieWazone(String[] tablicaZwroconychEtykietKlas){
		if(tablicaZwroconychEtykietKlas.length == 0)
			return "";
		ArrayList<String> listaEtykietKlas = Atrybuty.getAtrybutKlasowy().dziedzina;
		int liczbaKlas = listaEtykietKlas.size();
		double[] glosyWazone = new double[liczbaKlas];
		for(int i = 0; i < tablicaZwroconychEtykietKlas.length; i++){
			for(int j = 0; j < listaEtykietKlas.size(); j++){
				if( tablicaZwroconychEtykietKlas[i].equals(listaEtykietKlas.get(j)) ){
					glosyWazone[j] += wagiKlasyfikatorow[i];
					break;
				}
			}
		}
		int indexKlasyZMaxGlosami = getIndexOfMaxValue(glosyWazone);
		//return tablicaZwroconychEtykietKlas[indexKlasyZMaxGlosami];
		return listaEtykietKlas.get(indexKlasyZMaxGlosami);
	}
	
	private int getIndexOfMaxValue(double[] tablica){
		int indexOfMax = 0;
		double maxValue = 0;
		for(int i = 0; i < tablica.length; i++){
			if(tablica[i] > maxValue){
				maxValue = tablica[i];
				indexOfMax = i;
			}
		}
		return indexOfMax;
	}

}
