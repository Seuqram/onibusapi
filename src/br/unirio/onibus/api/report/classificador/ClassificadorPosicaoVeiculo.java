package br.unirio.onibus.api.report.classificador;

import org.joda.time.DateTime;

import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.model.PosicaoVeiculo;
import br.unirio.onibus.api.model.Repositorio;
import br.unirio.onibus.api.model.TipoPosicaoVeiculo;
import br.unirio.onibus.api.model.TrajetoriaVeiculo;
import br.unirio.onibus.api.model.Veiculo;

/**
 * Classe que classifica as posi��es dos ve�culos de acordo com seus trajetos
 * 
 * @author marcio.barros
 */
public class ClassificadorPosicaoVeiculo
{
	/**
	 * Dist�ncia m�xima aceit�vel de um ponto da rota
	 */
	private static double DISTANCIA_MAXIMA = 0.1;
	
	/**
	 * Reposit�rio para acesso aos dados dos �nibus
	 */
	private Repositorio repositorio;
	
	/**
	 * Inicializa o calculador
	 */
	public ClassificadorPosicaoVeiculo(Repositorio repositorio)
	{
		this.repositorio = repositorio;
	}
	
	/**
	 * Executa os c�lculos para uma data
	 */
	public boolean executa(Linha linha, DateTime data)
	{
		if (!repositorio.carregaTrajeto(linha))
			return false;

		if (!repositorio.carregaPosicoes(linha, data))
			return false;
		
		for (Veiculo veiculo : linha.getVeiculos())
		{
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
					int indiceIda = linha.getTrajetoIda().pegaIndicePontoMaisProximo(posicao.getLatitude(), posicao.getLongitude());
					posicao.setPosicaoTrajetoIda(indiceIda);
				}
				else
				{
					int indiceVolta = linha.getTrajetoVolta().pegaIndicePontoMaisProximo(posicao.getLatitude(), posicao.getLongitude());
					posicao.setPosicaoTrajetoVolta(indiceVolta);
				}
			}

			// Remove pontos de erro
			veiculo.getTrajetoria().removePosicoesErro();
			
			// Alisa o in�cio da trajet�ria
			corrigeInicioTrajetoria(linha, veiculo.getTrajetoria(), TipoPosicaoVeiculo.Ida, 20);
			corrigeInicioTrajetoria(linha, veiculo.getTrajetoria(), TipoPosicaoVeiculo.Volta, 20);
			
			// Corrige pontos isolados na trajet�ria
			corrigePontosIsolados(linha, veiculo.getTrajetoria(), TipoPosicaoVeiculo.Ida);
			corrigePontosIsolados(linha, veiculo.getTrajetoria(), TipoPosicaoVeiculo.Volta);
			
			// Corrige viradas de mais de um ponto no meio da trajet�ria
			corrigeViradaTrajetoMeio(linha, veiculo.getTrajetoria(), TipoPosicaoVeiculo.Ida);
			corrigeViradaTrajetoMeio(linha, veiculo.getTrajetoria(), TipoPosicaoVeiculo.Volta);
			
			// TODO: alguns pontos est�o resistindos �s estrat�gias anteriores
			
			// TODO: corrigir os pontos de virada da trajet�ria
			// Ex.: 
			// #0: Ida indice 4
			// #1: Ida indice 6
			// #2: Ida indice 8
			// #3: Ida indice 10
			// #4: Ida indice 12
			// #5: Ida indice 15
			// #6: Ida indice 25
			// #7: Ida indice 46
			// #8: Ida indice 58
			// #9: Ida indice 60
			// #10: Ida indice 64
			// #11: Ida indice 65
			// #12: Ida indice 75
			// #13: Ida indice 77
			// #14: Ida indice 79
			// #15: Ida indice 80
			// #16: Ida indice 82
			// #17: Ida indice 84
			// #18: Ida indice 86
			// #19: Ida indice 87
			// #20: Ida indice 88
			// #21: Ida indice 113
			// #22: Ida indice 123
			// #23: Ida indice 112		<------------ volta 0
			// #24: Ida indice 111		<------------ volta 2
			// #25: Volta indice 16
			// #26: Volta indice 17
		}
		
		// TEMP: Apresenta os resultados
		for (Veiculo veiculo : linha.getVeiculos())
		{
			System.out.println("Ve�culo: " + veiculo.getNumeroSerie());
			int numeroPosicao = 0;
			
			for (PosicaoVeiculo posicao : veiculo.getTrajetoria().getPosicoes())
			{
				String tipo = (posicao.getTipo() == TipoPosicaoVeiculo.Ida) ? "Ida" : "Volta";
				System.out.println("#" + numeroPosicao + ": " + tipo + " indice " + posicao.getIndiceTrajeto());
				numeroPosicao++;
			}
		}
		
		// TODO: eliminar trilhas com poucos pontos (menos de 50 posi��es)

		// TODO: ao final, poderia encontrar a proje��o nos trajetos dos pontos identificados para os �nibus

		// TODO: melhorar o design para permitir aplicar diferentes estrat�gias? faz sentido?

		return true;
	}

	/**
	 * Estrat�gia para corrigir pontos isolados na rota - se houver um ponto com tipo diferente
	 * entre dois pontos de um mesmo tipo e estes dois pontos estiverem em sequ�ncia no trajeto,
	 * verifica se o ponto central n�o est� pr�ximo a um ponto intermedi�rio entre eles no trajeto
	 */
	private void corrigePontosIsolados(Linha linha, TrajetoriaVeiculo trajetoria, TipoPosicaoVeiculo tipo) 
	{
		for (int i = 0; i < trajetoria.conta()-2; i++)
		{
			PosicaoVeiculo p1 = trajetoria.pegaPosicaoIndice(i+0);
			PosicaoVeiculo p2 = trajetoria.pegaPosicaoIndice(i+1);
			PosicaoVeiculo p3 = trajetoria.pegaPosicaoIndice(i+2);
			
			if (p1.getTipo() == tipo && p2.getTipo() != tipo && p3.getTipo() == tipo && p3.getIndiceTrajeto() >= p1.getIndiceTrajeto())
			{
				if (tipo == TipoPosicaoVeiculo.Ida)
				{
					double distanciaIda = linha.getTrajetoIda().calculaDistancia(p2.getLatitude(), p2.getLongitude());
					
					if (distanciaIda < DISTANCIA_MAXIMA)
					{
						int indiceIda = linha.getTrajetoIda().pegaIndicePontoMaisProximo(p2.getLatitude(), p2.getLongitude());
						
						if (p3.getIndiceTrajeto() >= indiceIda && p1.getIndiceTrajeto() <= indiceIda)
							p2.setPosicaoTrajetoIda(indiceIda);
					}
				}
				else
				{
					double distanciaVolta = linha.getTrajetoVolta().calculaDistancia(p2.getLatitude(), p2.getLongitude());
					
					if (distanciaVolta < DISTANCIA_MAXIMA)
					{
						int indiceVolta = linha.getTrajetoVolta().pegaIndicePontoMaisProximo(p2.getLatitude(), p2.getLongitude());
						
						if (p3.getIndiceTrajeto() >= indiceVolta && p1.getIndiceTrajeto() <= indiceVolta)
							p2.setPosicaoTrajetoVolta(indiceVolta);
					}
				}
			}
		}
	}

	/**
	 * Estrat�gia para corrigir viradas de rota no meio do circuito - se tr�s pontos v�m em sequ�ncia
	 * em um trajeto e um quarto ponto est� no trajeto oposto, verifica se este quarto ponto poderia
	 * se aproximar da sequ�ncia que vinha sendo formada
	 */
	private void corrigeViradaTrajetoMeio(Linha linha, TrajetoriaVeiculo trajetoria, TipoPosicaoVeiculo tipo) 
	{
		for (int i = 0; i < trajetoria.conta()-3; i++)
		{
			PosicaoVeiculo p1 = trajetoria.pegaPosicaoIndice(i+0);
			PosicaoVeiculo p2 = trajetoria.pegaPosicaoIndice(i+1);
			PosicaoVeiculo p3 = trajetoria.pegaPosicaoIndice(i+2);
			PosicaoVeiculo p4 = trajetoria.pegaPosicaoIndice(i+3);
			
			if (p1.getTipo() == tipo && p2.getTipo() == tipo && p3.getTipo() == tipo && p4.getTipo() != tipo && p1.getIndiceTrajeto() <= p2.getIndiceTrajeto() && p2.getIndiceTrajeto() <= p3.getIndiceTrajeto())
			{
				if (p4.getIndiceTrajeto() > 10)
				{
					if (tipo == TipoPosicaoVeiculo.Ida)
					{
						int indiceIda = linha.getTrajetoIda().pegaIndicePontoMaisProximo(p4.getLatitude(), p4.getLongitude(), p3.getIndiceTrajeto());
						p4.setPosicaoTrajetoIda(indiceIda);
					}
					else
					{
						int indiceVolta = linha.getTrajetoVolta().pegaIndicePontoMaisProximo(p4.getLatitude(), p4.getLongitude(), p3.getIndiceTrajeto());
						p4.setPosicaoTrajetoVolta(indiceVolta);
					}
				}
			}
		}
	}

	/**
	 * Estrat�gia para corrigir o in�cio da trajet�ria
	 */
	private void corrigeInicioTrajetoria(Linha linha, TrajetoriaVeiculo trajetoria, TipoPosicaoVeiculo tipo, int tamanhoInicio) 
	{
		if (!verificaTendenciaInicioTrajetoria(trajetoria, tipo, tamanhoInicio))
			return;
		
		for (int i = 0; i < tamanhoInicio; i++)
		{
			PosicaoVeiculo posicao = trajetoria.pegaPosicaoIndice(i);
			
			if (posicao.getTipo() != tipo)
			{
				if (posicao.getTipo() == TipoPosicaoVeiculo.Ida)
				{
					int indiceVolta = linha.getTrajetoVolta().pegaIndicePontoMaisProximo(posicao.getLatitude(), posicao.getLongitude());
					posicao.setPosicaoTrajetoVolta(indiceVolta);
				}
				else
				{
					int indiceIda = linha.getTrajetoIda().pegaIndicePontoMaisProximo(posicao.getLatitude(), posicao.getLongitude());
					posicao.setPosicaoTrajetoIda(indiceIda);
				}
			}
		}
	}

	/**
	 * Verifica se h� uma tend�ncia de ida ou volta no in�cio da trajet�ria
	 */
	private boolean verificaTendenciaInicioTrajetoria(TrajetoriaVeiculo trajetoria, TipoPosicaoVeiculo tipo, int tamanhoInicio) 
	{
		// Se o tamanho da trajet�ria � muito pequeno, n�o h� tend�ncia ...
		if (trajetoria.conta() < tamanhoInicio)
			return false;

		// Pega o �ndice do primeiro ponto de um tipo na trajet�ria
		int indice = pegaPrimeiraPosicao(trajetoria, tipo);
		
		// Se n�o h� pontos do tipo desejado, certamente n�o h� tend�ncia 
		if (indice < 0)
			return false;

		// Se o primeiro ponto de um tipo na trajet�ria aparece depois da metade do in�cio, n�o h� tend�ncia deste tipo
		if (indice >= tamanhoInicio / 2)
			return false;
		
		// Verifica se os pontos de trajeto est�o em sequ�ncia 
		int indiceTrajetoAtual = trajetoria.pegaPosicaoIndice(indice).getIndiceTrajeto();
		int numeroPosicoesTipoDesejado = 0;
		
		for (int i = indice; i < tamanhoInicio; i++)
		{
			PosicaoVeiculo posicao = trajetoria.pegaPosicaoIndice(i);
			
			if (posicao.getTipo() == tipo)
			{
				if (posicao.getIndiceTrajeto() < indiceTrajetoAtual)
					return false;

				indiceTrajetoAtual = posicao.getIndiceTrajeto();
				numeroPosicoesTipoDesejado++;
			}
		}

		// Se houver menos pontos do tipo desejado do que de outros tipos, n�o h� tend�ncia
		if (numeroPosicoesTipoDesejado < tamanhoInicio / 2)
			return false;
		
		return true;
	}

	/**
	 * Pega o �ndice do primeiro ponto de um tipo na trajet�ria
	 */
	private int pegaPrimeiraPosicao(TrajetoriaVeiculo trajetoria, TipoPosicaoVeiculo tipo) 
	{
		for (int i = 0; i < trajetoria.conta(); i++)
			if (trajetoria.pegaPosicaoIndice(i).getTipo() == tipo)
				return i;
		
		return -1;
	}
}