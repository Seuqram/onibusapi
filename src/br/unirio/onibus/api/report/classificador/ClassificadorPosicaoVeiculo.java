package br.unirio.onibus.api.report.classificador;

import org.joda.time.DateTime;

import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.model.PosicaoVeiculo;
import br.unirio.onibus.api.model.Repositorio;
import br.unirio.onibus.api.model.Veiculo;
import br.unirio.onibus.api.support.geodesic.PosicaoMapa;

/**
 * Classe que classifica as posições dos veículos de acordo com seus trajetos
 * 
 * @author marcio.barros
 */
public class ClassificadorPosicaoVeiculo
{
	private static double DISTANCIA_MAXIMA = 0.1;
	
	private Repositorio repositorio;
	
	/**
	 * Inicializa o calculador
	 */
	public ClassificadorPosicaoVeiculo(Repositorio repositorio)
	{
		this.repositorio = repositorio;
	}
	
	/**
	 * Executa os cálculos para uma data
	 */
	public boolean executa(Linha linha, DateTime data)
	{	
		if (!repositorio.carregaTrajeto(linha))
			return false;

		if (!repositorio.carregaPosicoes(linha, data))
			return false;
		
		for (Veiculo veiculo : linha.getVeiculos())
		{
			System.out.println("Veículo: " + veiculo.getNumeroSerie());
			int numeroPosicao = 0;
			
			for (PosicaoVeiculo posicao : veiculo.getTrajetoria().getPosicoes())
			{
				double distanciaIda = linha.getTrajetoIda().calculaDistancia(posicao.getLatitude(), posicao.getLongitude());
				double distanciaVolta = linha.getTrajetoVolta().calculaDistancia(posicao.getLatitude(), posicao.getLongitude());

				if (distanciaIda > DISTANCIA_MAXIMA && distanciaVolta > DISTANCIA_MAXIMA)
				{
					posicao.setPosicaoErro();
				}
				else if (distanciaIda < distanciaVolta)
				{
					PosicaoMapa posicaoIda = linha.getTrajetoIda().pegaPontoInflexaoMaisProximo(posicao.getLatitude(), posicao.getLongitude());
					int indiceIda = linha.getTrajetoIda().pegaIndicePosicao(posicaoIda);
					posicao.setPosicaoTrajetoIda(indiceIda);
					System.out.println("#" + numeroPosicao + ": Ida indice " + indiceIda);
				}
				else
				{
					PosicaoMapa posicaoVolta = linha.getTrajetoVolta().pegaPontoInflexaoMaisProximo(posicao.getLatitude(), posicao.getLongitude());
					int indiceVolta = linha.getTrajetoVolta().pegaIndicePosicao(posicaoVolta);
					posicao.setPosicaoTrajetoVolta(indiceVolta);
					System.out.println("#" + numeroPosicao + ": Volta indice " + indiceVolta);
				}
				
				numeroPosicao++;
			}
		}
		
		for (Veiculo veiculo : linha.getVeiculos())
			veiculo.getTrajetoria().removePosicoesErro();
			
		return true;
	}
}