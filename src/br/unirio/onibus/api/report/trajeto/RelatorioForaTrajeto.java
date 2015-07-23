package br.unirio.onibus.api.report.trajeto;

import java.util.ArrayList;
import java.util.List;

import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.reader.CarregadorLinhas;
import br.unirio.onibus.api.reader.CarregadorPosicoes;
import br.unirio.onibus.api.reader.CarregadorTrajeto;

/**
 * Relatório de pontos fora da trajetória para os ônibus de uma data
 * 
 * @author Marcio
 */
public class RelatorioForaTrajeto 
{
	public void executa(String diretorioBase, int dia, int mes, int ano) throws Exception
	{
		List<String> linhas = new ArrayList<String>();
		new CarregadorLinhas().executa(diretorioBase, dia, mes, ano, linhas);
		System.out.println("Numero de linhas na data: " + linhas.size());

		for (String nomeLinha : linhas)
		{
			Linha linha = new Linha(nomeLinha);
			new CarregadorPosicoes().executa(diretorioBase, dia, mes, ano, linha);
			new CarregadorTrajeto().carregaDiretorio(diretorioBase, linha);
			
			int foraTrajeto = new VerificadorPosicoesVeiculo().executa(linha);
			
			if (foraTrajeto > 0)
				System.out.println(nomeLinha + ": " + foraTrajeto + "/" + linha.contaPosicoes() + " posicoes fora da linha ");
		}
		
		System.out.println("FIM");
	}
}