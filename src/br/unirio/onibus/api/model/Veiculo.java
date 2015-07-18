package br.unirio.onibus.api.model;

import lombok.Getter;

/**
 * Classe que representa um ve�culo
 * 
 * @author Marcio Barros
 */
public class Veiculo
{
	/**
	 * N�mero de s�rie do ve�culo
	 */
	private @Getter String numeroSerie;
	
	/**
	 * Posi��es do ve�culo
	 */
	private @Getter TrajetoriaVeiculo trajetoria;
	
	/**
	 * Inicializa o ve�culo
	 */
	public Veiculo(String numeroSerie)
	{
		this.numeroSerie = numeroSerie;
		this.trajetoria = new TrajetoriaVeiculo();
	}
}