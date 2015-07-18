package br.unirio.onibus.api.model;

import lombok.Getter;

/**
 * Classe que representa um veículo
 * 
 * @author Marcio Barros
 */
public class Veiculo
{
	/**
	 * Número de série do veículo
	 */
	private @Getter String numeroSerie;
	
	/**
	 * Posições do veículo
	 */
	private @Getter TrajetoriaVeiculo trajetoria;
	
	/**
	 * Inicializa o veículo
	 */
	public Veiculo(String numeroSerie)
	{
		this.numeroSerie = numeroSerie;
		this.trajetoria = new TrajetoriaVeiculo();
	}
}