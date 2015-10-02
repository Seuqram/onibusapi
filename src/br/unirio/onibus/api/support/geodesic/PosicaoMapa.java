package br.unirio.onibus.api.support.geodesic;
import lombok.Getter;

/**
 * Classe que representa uma posição no mapa 
 * 
 * @author Marcio Barros
 */
public class PosicaoMapa
{
	private @Getter double latitude;
	private @Getter double longitude;
	
	/**
	 * Inicializa uma posição no mapa
	 */
	public PosicaoMapa(double latitude, double longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	/**
	 * Verifica se uma posição é igual a outra, considerando um erro mínimo
	 */
	public boolean igual(double latitude, double longitude)
	{
		return Math.abs(this.latitude - latitude) < 0.001 && Math.abs(this.longitude - longitude) < 0.001;
	}
}