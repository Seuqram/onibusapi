package br.unirio.onibus.api.model;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.joda.time.DateTime;

/**
 * Classe que representa uma linha de �nibus, com seus ve�culos e posi��es
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
	 * Ve�culos da linha
	 */
	private List<Veiculo> veiculos;
	
	/**
	 * Trajeto de ida do �nibus
	 */
	private @Getter Trajetoria trajetoIda;
	
	/**
	 * Trajeto de volta do �nibus
	 */
	private @Getter Trajetoria trajetoVolta;
	
	/**
	 * Pontos de parada do �nibus
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
	 * Adiciona uma posi��o de ve�culo na linha
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
	 * Conta o n�mero de ve�culos
	 */
	public int contaVeiculos()
	{
		return veiculos.size();
	}

	/**
	 * Retorna um ve�culo, dado seu �ndice
	 */
	public Veiculo pegaVeiculoIndice(int indice)
	{
		return veiculos.get(indice);
	}

	/**
	 * Retorna um ve�culo, dado seu n�mero de s�rie
	 */
	public Veiculo pegaVeiculoNumero(String numeroSerie)
	{
		for (Veiculo veiculo : veiculos)
			if (veiculo.getNumeroSerie().compareToIgnoreCase(numeroSerie) == 0)
				return veiculo;
		
		return null;
	}

	/**
	 * Retorna uma lista de ve�culos
	 */
	public Iterable<Veiculo> getVeiculos()
	{
		return veiculos;
	}

	/**
	 * Conta o n�mero de posi��es registradas na linha
	 */
	public int contaPosicoes()
	{
		int count = 0;
		
		for (Veiculo veiculo : veiculos)
			count += veiculo.getTrajetoria().conta();
		
		return count;
	}

	/**
	 * Ordena as posi��es de todos os ve�culos
	 */
	public void ordenaPosicoes() 
	{
		for (Veiculo veiculo : veiculos)
			veiculo.getTrajetoria().ordenaPosicoes();
	}

	/**
	 * Adiciona uma posi��o no trajeto de ida do �nibus
	 */
	public void adicionaTrajetoIda(double latitude, double longitude) 
	{
		trajetoIda.adiciona(new PosicaoMapa(latitude, longitude));
	}

	/**
	 * Adiciona uma posi��o no trajeto de volta do �nibus
	 */
	public void adicionaTrajetoVolta(double latitude, double longitude) 
	{
		trajetoVolta.adiciona(new PosicaoMapa(latitude, longitude)); 
	}

	/**
	 * Adiciona um ponto de parada na linha de �nibus
	 */
	public void adicionaParada(double latitude, double longitude) 
	{
		pontosParada.adiciona(new PosicaoMapa(latitude, longitude));
	}

	/**
	 * Retorna o pr�ximo ve�culo a passar por um ponto em uma hora/minuto
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