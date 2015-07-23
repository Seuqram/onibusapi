package br.unirio.onibus.api.report.horarios;

import java.util.List;

import br.unirio.onibus.api.support.console.IConsole;

/**
 * Classe responsável pela publicação do quadro de veículos
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
	 * Gera o cabeçalho do quadro de horários
	 */
	private void publicaCabecalho(IConsole console) 
	{
		console.print("Linha");
		
		for (int hora = 0; hora < 24; hora++)
			console.print("," + hora);

		console.println();
	}

	/**
	 * Publica uma entrada do quadro de horários
	 */
	private void publicaQuadro(IConsole console, QuadroHorarios quadro) 
	{
		console.print(quadro.getLinha().getIdentificador());

		for (int hora = 0; hora < 24; hora++)
			console.print("," + quadro.contaVeiculos(hora));
		
		console.println();
	}
}