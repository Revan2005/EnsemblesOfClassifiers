import java.util.ArrayList;

public abstract class Standaryzacja {
	
	public static ArrayList<Obiekt> standaryzuj(ArrayList<Obiekt> dane){
		int liczbaAtrybutowNumerycznych = Atrybuty.getLiczbaAtrybutowZKlasa() - 1;
		double[] sredniaAtrybutu = new double [liczbaAtrybutowNumerycznych];
		double[] odchylenieStandardoweAtrybutu = new double [liczbaAtrybutowNumerycznych];	
		for(int index_atrybutu = 0; index_atrybutu < liczbaAtrybutowNumerycznych; index_atrybutu++){
			sredniaAtrybutu[index_atrybutu] =  getSrednia(dane, index_atrybutu);
			odchylenieStandardoweAtrybutu[index_atrybutu] = getOdchylenieStandardowe(dane, index_atrybutu, sredniaAtrybutu[index_atrybutu]);
		}
		/*za mimuw: "Standaryzacja: od wartosci kazdego atrybutu odejmujemy srednia i dzielimy
		 * przez odchylenie standardowe (przeprowadza zmienne o roznych jednostkach do zmiennych 
		 * niemianowanych)"
		 */
		for(int index_obiektu = 0; index_obiektu < dane.size(); index_obiektu++){
			for(int index_atrybutu = 0; index_atrybutu < liczbaAtrybutowNumerycznych; index_atrybutu++){
				dane.get(index_obiektu).wartosciAtrybutowNumerycznych[index_atrybutu] -= sredniaAtrybutu[index_atrybutu];
				dane.get(index_obiektu).wartosciAtrybutowNumerycznych[index_atrybutu] /= odchylenieStandardoweAtrybutu[index_atrybutu];
 			}
		}
		return dane;
	}
	
	private static double getSrednia(ArrayList<Obiekt> dane, int index_atrybutu){
		double suma = 0;
		for(int i = 0; i < dane.size(); i++)
			suma += dane.get(i).wartosciAtrybutowNumerycznych[index_atrybutu];
		double srednia = suma / dane.size();
		return srednia;
		
	}
	
	private static double getOdchylenieStandardowe(ArrayList<Obiekt> dane, int index_atrybutu, double sredniaAtrybutu){
		double wariancja;
		double sumaKwadratowOdleglosciOdSredniej = 0;
		double odleglosc;
		Obiekt rozwazanyObiekt;
		for(int i = 0; i < dane.size(); i++){
			rozwazanyObiekt = dane.get(i);
			odleglosc = sredniaAtrybutu - rozwazanyObiekt.wartosciAtrybutowNumerycznych[index_atrybutu];
			sumaKwadratowOdleglosciOdSredniej += Math.pow(odleglosc, 2);
		}
		wariancja = sumaKwadratowOdleglosciOdSredniej / dane.size();
		double odchylenieStandardowe = Math.sqrt(wariancja);
		return odchylenieStandardowe;
	}
	

}
