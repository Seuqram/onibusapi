package br.unirio.onibus.api;

import java.util.ArrayList;
import java.util.List;

import br.unirio.onibus.api.calc.VerificadorPosicoesVeiculo;
import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.reader.CarregadorLinhas;
import br.unirio.onibus.api.reader.CarregadorPosicoes;
import br.unirio.onibus.api.reader.CarregadorTrajeto;

public class MainProgram 
{
	public static final void main(String[] args) throws Exception
	{
		int dia = 29;
		int mes = 4;
		int ano = 2014;
		
		List<String> linhas = new ArrayList<String>();
		new CarregadorLinhas().executa("/Users/Marcio/Desktop/Processados", dia, mes, ano, linhas);
		System.out.println("Numero de linhas na data: " + linhas.size());

		for (String nomeLinha : linhas)
		{
			Linha linha = new Linha(nomeLinha);
			new CarregadorPosicoes().executa("/Users/Marcio/Desktop/Processados", dia, mes, ano, linha);
			new CarregadorTrajeto().executa("/Users/Marcio/Desktop/Processados", linha);
			
			int foraTrajeto = new VerificadorPosicoesVeiculo().executa(linha);
			
			if (foraTrajeto > 0)
				System.out.println(nomeLinha + ": " + foraTrajeto + "/" + linha.contaPosicoes() + " posicoes fora da linha ");
		}
		
		System.out.println("FIM");
	}
}