import java.util.ArrayList;
import java.util.Random;

public class ZespolKlasyfikatorowBagging extends ZespolKlasyfikatorow {

	public ZespolKlasyfikatorowBagging(int liczbaSasiadow, String metryka, String metodaGlosowania, ArrayList<Obiekt> daneUczace, int liczbaKlasyfikatorowWZespole) {
		super(liczbaSasiadow, metryka, metodaGlosowania, daneUczace, liczbaKlasyfikatorowWZespole);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getClass(Obiekt obiekt_testowy) {
		ArrayList<KlasyfikatorKNN> listaKlasyfikatorow = new ArrayList<KlasyfikatorKNN>();
		ArrayList<Obiekt> pseudozbiorUczacy;
		for(int i = 0; i < liczbaKlasyfikatorowWZespole; i++){
			pseudozbiorUczacy = getPodzbiorUczacy();
			listaKlasyfikatorow.add(new KlasyfikatorKNN(liczbaSasiadow, metryka, metodaGlosowania, pseudozbiorUczacy));
		}
		String[] tablicaZwroconychEtykietKlas = new String[listaKlasyfikatorow.size()];
		KlasyfikatorKNN klasyfikator;
		for(int i = 0; i < listaKlasyfikatorow.size(); i++){
			klasyfikator = listaKlasyfikatorow.get(i);
			tablicaZwroconychEtykietKlas[i] = klasyfikator.getClass(obiekt_testowy);
		}
		String etykietaKlasy = getEtykietaKlasyWskazanaPrzezWiekszoscKlasyfikatorow(tablicaZwroconychEtykietKlas);
		return etykietaKlasy;
	}
	
	/*private void generujListeKlasyfikatorow(){
		
	}*/
	
	private ArrayList<Obiekt> getPodzbiorUczacy(){
		ArrayList<Obiekt> podzbior = new ArrayList<Obiekt>();
		Random random = new Random();
		int randomIndex;
		for(int i = 0; i < zbiorUczacy.size(); i++){
			randomIndex = random.nextInt(zbiorUczacy.size());
			podzbior.add(zbiorUczacy.get(randomIndex));
		}
		return podzbior;
	}
	
	private String getEtykietaKlasyWskazanaPrzezWiekszoscKlasyfikatorow(String[] tablicaZwroconychEtykietKlas){
		if(tablicaZwroconychEtykietKlas.length == 0)
			return "";
		int indexNajczestszej = 0;
		int[] liczbaTakichSamychWTablicy = new int[tablicaZwroconychEtykietKlas.length];
		for(int i = 0; i < tablicaZwroconychEtykietKlas.length; i++){
			for(int j = 0; j < tablicaZwroconychEtykietKlas.length; j++){
				if( tablicaZwroconychEtykietKlas[i].equals(tablicaZwroconychEtykietKlas[j]) ){
					liczbaTakichSamychWTablicy[i]++;
				}
			}
		}
		indexNajczestszej = getIndexOfMaxValue(liczbaTakichSamychWTablicy);
		return tablicaZwroconychEtykietKlas[indexNajczestszej];
	}
	
	private int getIndexOfMaxValue(int[] tablica){
		int indexOfMax = 0;
		int maxValue = 0;
		for(int i = 0; i < tablica.length; i++){
			if(tablica[i] > maxValue){
				maxValue = tablica[i];
				indexOfMax = i;
			}
		}
		return indexOfMax;
	}

}
