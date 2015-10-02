package br.unirio.onibus.api.report.trajeto;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.model.Repositorio;

/**
 * Relatório de pontos fora da trajetória para os ônibus de uma data
 * 
 * @author Marcio
 */
public class RelatorioForaTrajeto 
{
	public void executa(Repositorio repositorio, DateTime data) throws Exception
	{
		List<String> linhas = new ArrayList<String>();
		repositorio.carregaLinhas(data, linhas);
		System.out.println("Numero de linhas na data: " + linhas.size());

		for (String nomeLinha : linhas)
		{
			Linha linha = new Linha(nomeLinha);
			repositorio.carregaPosicoes(linha, data);
			repositorio.carregaTrajeto(linha);
			
			int foraTrajeto = new VerificadorPosicoesVeiculo().executa(linha);
			
			if (foraTrajeto > 0)
				System.out.println(nomeLinha + ": " + foraTrajeto + "/" + linha.contaPosicoes() + " posicoes fora da linha ");
		}
		
		System.out.println("FIM");
	}
}