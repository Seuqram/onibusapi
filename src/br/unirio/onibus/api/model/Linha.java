package br.unirio.onibus.api.model;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.joda.time.DateTime;

/**
 * Classe que representa uma linha de ônibus, com seus veículos e posições
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
	 * Veículos da linha
	 */
	private List<Veiculo> veiculos;
	
	/**
	 * Trajeto de ida do ônibus
	 */
	private @Getter Trajetoria trajetoIda;
	
	/**
	 * Trajeto de volta do ônibus
	 */
	private @Getter Trajetoria trajetoVolta;
	
	/**
	 * Pontos de parada do ônibus
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
	 * Adiciona uma posição de veículo na linha
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
	 * Conta o número de veículos
	 */
	public int contaVeiculos()
	{
		return veiculos.size();
	}

	/**
	 * Retorna um veículo, dado seu índice
	 */
	public Veiculo pegaVeiculoIndice(int indice)
	{
		return veiculos.get(indice);
	}

	/**
	 * Retorna um veículo, dado seu número de série
	 */
	public Veiculo pegaVeiculoNumero(String numeroSerie)
	{
		for (Veiculo veiculo : veiculos)
			if (veiculo.getNumeroSerie().compareToIgnoreCase(numeroSerie) == 0)
				return veiculo;
		
		return null;
	}

	/**
	 * Retorna uma lista de veículos
	 */
	public Iterable<Veiculo> getVeiculos()
	{
		return veiculos;
	}

	/**
	 * Conta o número de posições registradas na linha
	 */
	public int contaPosicoes()
	{
		int count = 0;
		
		for (Veiculo veiculo : veiculos)
			count += veiculo.getTrajetoria().conta();
		
		return count;
	}

	/**
	 * Ordena as posições de todos os veículos
	 */
	public void ordenaPosicoes() 
	{
		for (Veiculo veiculo : veiculos)
			veiculo.getTrajetoria().ordenaPosicoes();
	}

	/**
	 * Adiciona uma posição no trajeto de ida do ônibus
	 */
	public void adicionaTrajetoIda(double latitude, double longitude) 
	{
		trajetoIda.adiciona(new PosicaoMapa(latitude, longitude));
	}

	/**
	 * Adiciona uma posição no trajeto de volta do ônibus
	 */
	public void adicionaTrajetoVolta(double latitude, double longitude) 
	{
		trajetoVolta.adiciona(new PosicaoMapa(latitude, longitude)); 
	}

	/**
	 * Adiciona um ponto de parada na linha de ônibus
	 */
	public void adicionaParada(double latitude, double longitude) 
	{
		pontosParada.adiciona(new PosicaoMapa(latitude, longitude));
	}

	/**
	 * Retorna o próximo veículo a passar por um ponto em uma hora/minuto
	 */
	public Veiculo pegaProximoVeiculo(double latitude, double longitude, int hora, int minuto)
	{
		int minimoMinutos = 24 * 60;
		Veiculo proximoVeiculo = null;
		
		for (Veiculo veiculo : veiculos)
		{
			int minutos = veiculo.getTrajetoria().pegaMinutosProximaPassagemPosicao(latitude, longitude, hora, minuto);
			
			if (minutos >= 0 && minutos < minimoMinutos)
				proximoVeiculo = veiculo;
		}

		return proximoVeiculo;
	}
}