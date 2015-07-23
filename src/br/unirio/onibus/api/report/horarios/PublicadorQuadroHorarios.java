package br.unirio.onibus.api.report.horarios;

import java.util.List;

import br.unirio.onibus.api.support.console.IConsole;

/**
 * Classe respons�vel pela publica��o do quadro de ve�culos
 * 
 * @author Marcio
 */
public class PublicadorQuadroHorarios 
{
	/**
	 * Executa o publicador
	 */
	public void executa(IConsole console, List<QuadroHorarios> quadros)
	{
		publicaCabecalho(console);
		
		for (QuadroHorarios quadro : quadros)
			publicaQuadro(console, quadro);
	}
	
	/**
	 * Gera o cabe�alho do quadro de hor�rios
	 */
	private void publicaCabecalho(IConsole console) 
	{
		console.print("Linha");
		
		for (int hora = 0; hora < 24; hora++)
			console.print("," + hora);

		console.println();
	}

	/**
	 * Publica uma entrada do quadro de hor�rios
	 */
	private void publicaQuadro(IConsole console, QuadroHorarios quadro) 
	{
		console.print(quadro.getLinha().getIdentificador());

		for (int hora = 0; hora < 24; hora++)
			console.print("," + quadro.contaVeiculos(hora));
		
		console.println();
	}
}