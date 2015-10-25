package br.unirio.onibus.api.report.classificador;

import org.joda.time.DateTime;

import br.unirio.onibus.api.model.Linha;
import br.unirio.onibus.api.model.PosicaoVeiculo;
import br.unirio.onibus.api.model.Repositorio;
import br.unirio.onibus.api.model.TipoPosicaoVeiculo;
import br.unirio.onibus.api.model.TrajetoriaVeiculo;
import br.unirio.onibus.api.model.Veiculo;

/**
 * Classe que classifica as posições dos veículos de acordo com seus trajetos
 * 
 * @author marcio.barros
 */
public class ClassificadorPosicaoVeiculo
{
	/**
	 * Distância máxima aceitável de um ponto da rota
	 */
	private static double DISTANCIA_MAXIMA = 0.1;
	
	/**
	 * Repositório para acesso aos dados dos ônibus
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
			
			// Alisa o início da trajetória
			corrigeInicioTrajetoria(linha, veiculo.getTrajetoria(), TipoPosicaoVeiculo.Ida, 20);
			corrigeInicioTrajetoria(linha, veiculo.getTrajetoria(), TipoPosicaoVeiculo.Volta, 20);
			
			// Corrige pontos isolados na trajetória
			corrigePontosIsolados(linha, veiculo.getTrajetoria(), TipoPosicaoVeiculo.Ida);
			corrigePontosIsolados(linha, veiculo.getTrajetoria(), TipoPosicaoVeiculo.Volta);
			
			// Corrige viradas de mais de um ponto no meio da trajetória
			corrigeViradaTrajetoMeio(linha, veiculo.getTrajetoria(), TipoPosicaoVeiculo.Ida);
			corrigeViradaTrajetoMeio(linha, veiculo.getTrajetoria(), TipoPosicaoVeiculo.Volta);
			
			// TODO: alguns pontos estão resistindos às estratégias anteriores
			
			// TODO: corrigir os pontos de virada da trajetória
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
			System.out.println("Veículo: " + veiculo.getNumeroSerie());
			int numeroPosicao = 0;
			
			for (PosicaoVeiculo posicao : veiculo.getTrajetoria().getPosicoes())
			{
				String tipo = (posicao.getTipo() == TipoPosicaoVeiculo.Ida) ? "Ida" : "Volta";
				System.out.println("#" + numeroPosicao + ": " + tipo + " indice " + posicao.getIndiceTrajeto());
				numeroPosicao++;
			}
		}
		
		// TODO: eliminar trilhas com poucos pontos (menos de 50 posições)

		// TODO: ao final, poderia encontrar a projeção nos trajetos dos pontos identificados para os ônibus

		// TODO: melhorar o design para permitir aplicar diferentes estratégias? faz sentido?

		return true;
	}

	/**
	 * Estratégia para corrigir pontos isolados na rota - se houver um ponto com tipo diferente
	 * entre dois pontos de um mesmo tipo e estes dois pontos estiverem em sequência no trajeto,
	 * verifica se o ponto central não está próximo a um ponto intermediário entre eles no trajeto
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
	 * Estratégia para corrigir viradas de rota no meio do circuito - se três pontos vêm em sequência
	 * em um trajeto e um quarto ponto está no trajeto oposto, verifica se este quarto ponto poderia
	 * se aproximar da sequência que vinha sendo formada
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
	 * Estratégia para corrigir o início da trajetória
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
	 * Verifica se há uma tendência de ida ou volta no início da trajetória
	 */
	private boolean verificaTendenciaInicioTrajetoria(TrajetoriaVeiculo trajetoria, TipoPosicaoVeiculo tipo, int tamanhoInicio) 
	{
		// Se o tamanho da trajetória é muito pequeno, não há tendência ...
		if (trajetoria.conta() < tamanhoInicio)
			return false;

		// Pega o índice do primeiro ponto de um tipo na trajetória
		int indice = pegaPrimeiraPosicao(trajetoria, tipo);
		
		// Se não há pontos do tipo desejado, certamente não há tendência 
		if (indice < 0)
			return false;

		// Se o primeiro ponto de um tipo na trajetória aparece depois da metade do início, não há tendência deste tipo
		if (indice >= tamanhoInicio / 2)
			return false;
		
		// Verifica se os pontos de trajeto estão em sequência 
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

		// Se houver menos pontos do tipo desejado do que de outros tipos, não há tendência
		if (numeroPosicoesTipoDesejado < tamanhoInicio / 2)
			return false;
		
		return true;
	}

	/**
	 * Pega o índice do primeiro ponto de um tipo na trajetória
	 */
	private int pegaPrimeiraPosicao(TrajetoriaVeiculo trajetoria, TipoPosicaoVeiculo tipo) 
	{
		for (int i = 0; i < trajetoria.conta(); i++)
			if (trajetoria.pegaPosicaoIndice(i).getTipo() == tipo)
				return i;
		
		return -1;
	}
}