package br.unirio.onibus.api.report.horarios;

import lombok.Getter;
import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.support.strings.StringCollection;

/**
 * Classe que representa o quadro de horários de uma linha
 * 
 * @author Marcio
 */
public class QuadroHorarios 
{
	private @Getter Linha linha;
	private StringCollection[] horas;
	
	/**
	 * Inicializa o quadro de horários de uma linha
	 */
	public QuadroHorarios(Linha linha)
	{
		this.linha = linha;
		horas = new StringCollection[24];
		
		for (int i = 0; i < 24; i++)
			horas[i] = new StringCollection();
	}
	
	/**
	 * Adiciona um veículo em uma hora
	 */
	public void adicionaVeiculo(int hora, String numeroSerie)
	{
		StringCollection sc = horas[hora];
		
		if (!sc.contains(numeroSerie))
			sc.add(numeroSerie);
	}
	
	/**
	 * Conta o número de veículos circulando em determinada hora
	 */
	public int contaVeiculos(int hora)
	{
		StringCollection sc = horas[hora];
		return sc.size();
	}

	/**
	 * Retorna os veículos circulando em determinada hora
	 */
	public Iterable<String> pegaVeiculos(int hora)
	{
		return horas[hora];
	}
}