import java.util.ArrayList;

public abstract class ZespolKlasyfikatorow {
	int liczbaKlasyfikatorowWZespole;
	//ArrayList<KlasyfikatorKNN> listaKlasyfikatorow;
	ArrayList<Obiekt> zbiorUczacy;
	int liczbaSasiadow;
	String metryka;
	String metodaGlosowania;
	
	public ZespolKlasyfikatorow(int liczbaSasiadow, String metryka, String metodaGlosowania, ArrayList<Obiekt> daneUczace, int liczbaKlasyfikatorowWZespole){
		this.liczbaKlasyfikatorowWZespole = liczbaKlasyfikatorowWZespole;
		//listaKlasyfikatorow = new ArrayList<KlasyfikatorKNN>();
		zbiorUczacy = daneUczace;
		this.liczbaSasiadow = liczbaSasiadow;
		this.metryka = metryka;
		this.metodaGlosowania = metodaGlosowania;
	}
	
	public abstract String getClass(Obiekt obiekt_testowy);

}
