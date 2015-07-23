package br.unirio.onibus.api.report.horarios;

import java.util.ArrayList;
import java.util.List;

import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.model.PosicaoVeiculo;
import br.unirio.onibus.api.model.Veiculo;
import br.unirio.onibus.api.reader.CarregadorLinhas;
import br.unirio.onibus.api.reader.CarregadorPosicoes;

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
	public List<QuadroHorarios> executa(String diretorioProcessados, int dia, int mes, int ano) throws Exception
	{
		List<String> nomesLinhas = new ArrayList<String>();
		List<QuadroHorarios> quadros = new ArrayList<QuadroHorarios>();
		
		CarregadorLinhas carregadorLinhas = new CarregadorLinhas();
		carregadorLinhas.executa(diretorioProcessados, dia, mes, ano, nomesLinhas);

		for (String nomeLinha : nomesLinhas)
		{
			if (nomeLinha.compareTo("desconhecido") != 0)
			{
				System.out.println("Processando " + nomeLinha + " ...");
				quadros.add(processaLinha(diretorioProcessados, dia, mes, ano, nomeLinha));
			}
		}
		
		return quadros;
	}

	/**
	 * Processa os dados de uma linha de ônibus
	 */
	private QuadroHorarios processaLinha(String diretorioProcessados, int dia, int mes, int ano, String nomeLinha) throws Exception 
	{
		Linha linha = new Linha(nomeLinha);
		QuadroHorarios quadro = new QuadroHorarios(linha);

		CarregadorPosicoes carregadorPosicoes = new CarregadorPosicoes();
		carregadorPosicoes.executa(diretorioProcessados, dia, mes, ano, linha);
		
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