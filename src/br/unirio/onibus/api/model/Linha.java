package br.unirio.onibus.api.model;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.joda.time.DateTime;

import br.unirio.onibus.api.support.geodesic.PosicaoMapa;

/**
 * Classe que representa uma linha de onibus, com seus veiculos e posicoes
 * 
 * @author Marcio Barros
 */
public class Linha
{
	/**
	 * Identificador da linha
	 */
	private @Getter String identificador;
	
	/**
	 * veiculos da linha
	 */
	private List<Veiculo> veiculos;
	
	/**
	 * Trajeto de ida do onibus
	 */
	private @Getter Trajetoria trajetoIda;
	
	/**
	 * Trajeto de volta do onibus
	 */
	private @Getter Trajetoria trajetoVolta;
	
	/**
	 * Pontos de parada do onibus
	 */
	private @Getter Trajetoria pontosParada;
	
	/**
	 * Inicializa a linha
	 */
	public Linha(String identificador)
	{
		this.identificador = identificador;
		this.veiculos = new ArrayList<Veiculo>();
		this.trajetoIda = new Trajetoria();
		this.trajetoVolta = new Trajetoria();
		this.pontosParada = new Trajetoria();
	}

	/**
	 * Adiciona uma posicao de veiculo na linha
	 */
	public void adiciona(String numeroSerie, DateTime data, double latitude, double longitude, double velocidade)
	{
		Veiculo veiculo = pegaVeiculoNumero(numeroSerie);
		
		if (veiculo == null)
		{
			veiculo = new Veiculo(numeroSerie);
			veiculos.add(veiculo);
		}
		
		veiculo.getTrajetoria().adiciona(data, latitude, longitude, velocidade);
	}

	/**
	 * Conta o numero de veiculos
	 */
	public int contaVeiculos()
	{
		return veiculos.size();
	}

	/**
	 * Retorna um veiculo, dado seu indice
	 */
	public Veiculo pegaVeiculoIndice(int indice)
	{
		return veiculos.get(indice);
	}

	/**
	 * Retorna um veiculo, dado seu numero de serie
	 */
	public Veiculo pegaVeiculoNumero(String numeroSerie)
	{
		for (Veiculo veiculo : veiculos)
			if (veiculo.getNumeroSerie().compareToIgnoreCase(numeroSerie) == 0)
				return veiculo;
		
		return null;
	}

	/**
	 * Retorna uma lista de veiculos
	 */
	public Iterable<Veiculo> getVeiculos()
	{
		return veiculos;
	}

	/**
	 * Remove um veiculo da linha
	 */
	public void removeVeiculo(int indice) 
	{
		veiculos.remove(indice);
	}

	/**
	 * Remove um conjunto de veiculos da linha
	 */
	public void removeVeiculos(int minIndice, int maxIndice) 
	{
		for (int i = maxIndice; i >= minIndice; i--)
			veiculos.remove(i);
	}

	/**
	 * Remove todas as posicoes dos onibus
	 */
	public void limpaVeiculos() 
	{
		veiculos.clear();
	}
	
	/**
	 * Conta o numero de posicoes registradas na linha
	 */
	public int contaPosicoes()
	{
		int count = 0;
		
		for (Veiculo veiculo : veiculos)
			count += veiculo.getTrajetoria().conta();
		
		return count;
	}

	/**
	 * Ordena as posicoes de todos os veiculos
	 */
	public void ordenaPosicoes() 
	{
		for (Veiculo veiculo : veiculos)
			veiculo.getTrajetoria().ordenaPosicoes();
	}

	/**
	 * Remove todas as posicoes dos veiculos depois de um horario
	 */
	public void removePosicoesDepoisHorario(int hora, int minuto) 
	{
		for (Veiculo veiculo : veiculos)
			veiculo.removePosicoesDepoisHorario(hora, minuto);
	}

	/**
	 * Remove todas as posicoes dos veiculos antes de um horario
	 */
	public void removePosicoesAntesHorario(int hora, int minuto) 
	{
		for (Veiculo veiculo : veiculos)
			veiculo.removePosicoesAntesHorario(hora, minuto);
	}

	/**
	 * Adiciona uma posicao no trajeto de ida do onibus
	 */
	public void adicionaTrajetoIda(double latitude, double longitude) 
	{
		trajetoIda.adiciona(new PosicaoMapa(latitude, longitude));
	}

	/**
	 * Adiciona uma posicao no trajeto de volta do onibus
	 */
	public void adicionaTrajetoVolta(double latitude, double longitude) 
	{
		trajetoVolta.adiciona(new PosicaoMapa(latitude, longitude)); 
	}

	/**
	 * Adiciona um ponto de parada na linha de onibus
	 */
	public void adicionaParada(double latitude, double longitude) 
	{
		pontosParada.adiciona(new PosicaoMapa(latitude, longitude));
	}

	/**
	 * Retorna o proximo veiculo a passar por um ponto em uma hora/minuto
	 */
	public Veiculo pegaProximoVeiculo(PosicaoMapa destino, int hora, int minuto, double erroAceitavel)
	{
		int minimoMinutos = 24 * 60;
		Veiculo proximoVeiculo = null;
		
		for (Veiculo veiculo : veiculos)
		{
			int minutos = veiculo.getTrajetoria().pegaMinutosProximaPassagemPosicao(destino, hora, minuto, erroAceitavel);
			
			if (minutos >= 0 && minutos < minimoMinutos)
				proximoVeiculo = veiculo;
		}

		return proximoVeiculo;
	}
}