package br.unirio.onibus.api.controllers.mapaminuto;

import lombok.Getter;
import br.unirio.onibus.api.model.TipoPosicaoVeiculo;
import br.unirio.onibus.api.support.geodesic.PosicaoMapa;

/**
 * Classe que representa a posicao de um veiculo em um instante de tempo 
 * 
 * @author Vitor de Lima
 */
public class PosicaoPorMinuto extends PosicaoMapa
{
	private @Getter int indiceTrechoIda;
	private @Getter int indiceTrechoVolta;
	private @Getter PosicaoMapa posicaoMapaOriginal;
	private @Getter TipoPosicaoVeiculo tipoPosicao;
	
	/**
	 * Inicializa a posicao do veiculo no minuto
	 */
	public PosicaoPorMinuto(double latitude, double longitude, int indiceTrechoIda, int indiceTrechoVolta, PosicaoMapa posicaoMapaOriginal, TipoPosicaoVeiculo tipo)
	{
		super(latitude, longitude);
		this.indiceTrechoIda = indiceTrechoIda;
		this.indiceTrechoVolta = indiceTrechoVolta;
		this.posicaoMapaOriginal = posicaoMapaOriginal;
		this.tipoPosicao = tipo;
	}
}