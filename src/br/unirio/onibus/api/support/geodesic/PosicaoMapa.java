package br.unirio.onibus.api.support.geodesic;
import lombok.Getter;

/**
 * Classe que representa uma posi��o no mapa 
 * 
 * @author Marcio Barros
 */
public class PosicaoMapa
{
	private @Getter double latitude;
	private @Getter double longitude;
	
	/**
	 * Inicializa uma posi��o no mapa
	 */
	public PosicaoMapa(double latitude, double longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	/**
	 * Verifica se uma posi��o � igual a outra, considerando um erro m�nimo
	 */
	public boolean igual(double latitude, double longitude)
	{
		return Math.abs(this.latitude - latitude) < 0.001 && Math.abs(this.longitude - longitude) < 0.001;
	}
}