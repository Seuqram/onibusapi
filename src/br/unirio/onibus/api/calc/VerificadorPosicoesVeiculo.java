package br.unirio.onibus.api.calc;

import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.model.PosicaoVeiculo;
import br.unirio.onibus.api.model.Veiculo;

/**
 * Verifica se as posições dos veículos estão fora dos seus trajetos
 * 
 * @author Marcio
 */
public class VerificadorPosicoesVeiculo 
{
	/**
	 * Conta o número de posições de uma linha de veículos que estão fora dos trajeto
	 */
	public int executa(Linha linha)
	{
		int contador = 0;
		
		for (Veiculo veiculo : linha.getVeiculos())
			contador += contaPosicoesLongeTrajetos(linha, veiculo);
		
		return contador;
	}

	/**
	 * Conta o número de posições de um veículo que estão fora dos trajeto
	 */
	private int contaPosicoesLongeTrajetos(Linha linha, Veiculo veiculo) 
	{
		int contador = 0;
		
		for (PosicaoVeiculo posicao : veiculo.getTrajetoria().pegaPosicoes())
			if (verificaPosicaoLongeTrajetos(linha, veiculo, posicao))
				contador++;
		
		return contador;
	}

	/**
	 * Verifica se uma posição de veículo está fora dos trajetos de ida e volta
	 */
	private boolean verificaPosicaoLongeTrajetos(Linha linha, Veiculo veiculo, PosicaoVeiculo posicao) 
	{
		double distanciaIda = linha.getTrajetoIda().calculaDistancia(posicao.getLatitude(), posicao.getLongitude());
		double distanciaVolta = linha.getTrajetoVolta().calculaDistancia(posicao.getLatitude(), posicao.getLongitude());;
		return Math.min(distanciaIda, distanciaVolta) > 2.0;
	}
}