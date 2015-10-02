package br.unirio.onibus.api.report.horarios;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.model.PosicaoVeiculo;
import br.unirio.onibus.api.model.Repositorio;
import br.unirio.onibus.api.model.Veiculo;

/**
 * Classe que gera um quadro de horários dos ônibus circulando em uma data 
 * 
 * @author Marcio
 */
public class GeradorQuadroHorarios 
{
	/**
	 * Gera os quadros de horário dos ônibus
	 */
	public List<QuadroHorarios> executa(Repositorio repositorio, DateTime data) throws Exception
	{
		List<String> nomesLinhas = new ArrayList<String>();

		if (!repositorio.carregaLinhas(data, nomesLinhas))
			return null;

		List<QuadroHorarios> quadros = new ArrayList<QuadroHorarios>();

		for (String nomeLinha : nomesLinhas)
		{
			if (nomeLinha.compareTo("desconhecido") != 0)
			{
				System.out.println("Processando " + nomeLinha + " ...");
				quadros.add(processaLinha(repositorio, data, nomeLinha));
			}
		}
		
		return quadros;
	}

	/**
	 * Processa os dados de uma linha de ônibus
	 */
	private QuadroHorarios processaLinha(Repositorio repositorio, DateTime data, String nomeLinha) throws Exception 
	{
		Linha linha = new Linha(nomeLinha);
		QuadroHorarios quadro = new QuadroHorarios(linha);

		if (!repositorio.carregaPosicoes(linha, data))
			return quadro;
		
		for (Veiculo veiculo : linha.getVeiculos())
		{
			int ultimaHora = -1;
			
			for (PosicaoVeiculo posicao : veiculo.getTrajetoria().pegaPosicoes())
			{
				int hora = posicao.getData().getHourOfDay();
				
				if (hora != ultimaHora)
				{
					quadro.adicionaVeiculo(hora, veiculo.getNumeroSerie());
					ultimaHora = hora;
				}
			}
		}

		return quadro;
	}
}