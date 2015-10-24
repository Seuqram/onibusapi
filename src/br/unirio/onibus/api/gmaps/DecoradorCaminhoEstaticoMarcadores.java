package br.unirio.onibus.api.gmaps;

import java.io.PrintWriter;
import java.util.Iterator;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import br.unirio.onibus.api.model.PosicaoVeiculo;
import br.unirio.onibus.api.model.TipoPosicaoVeiculo;
import br.unirio.onibus.api.model.TrajetoriaVeiculo;

/**
 * Classe que representa um caminho que pode ser adicionado a um mapa
 * 
 * @author Marcio
 */
public class DecoradorCaminhoEstaticoMarcadores implements IDecoradorMapas 
{
	public TrajetoriaVeiculo trajetoria;

	/**
	 * Inicializa o decorador
	 */
	public DecoradorCaminhoEstaticoMarcadores(TrajetoriaVeiculo trajetoria) 
	{
		this.trajetoria = trajetoria;
	}
	
	/**
	 * Gera o código que representa o caminho
	 */
	@Override
	public void gera(PrintWriter writer) 
	{
		Iterator<PosicaoVeiculo> iterator = trajetoria.getPosicoes().iterator();
		DateTimeFormatter sdf = DateTimeFormat.forPattern("HH:mm");
		int contador = 1;

		while (iterator.hasNext())
		{
			PosicaoVeiculo posicaoAtual = iterator.next();
			String conteudo = "#" + contador + " @" + sdf.print(posicaoAtual.getData()) + " (" + posicaoAtual.getLatitude() + ", " + posicaoAtual.getLongitude() + ")";
			String cor = pegaCorPosicaoVeiculo(posicaoAtual);

			writer.println("var marker" + contador + " = new google.maps.Marker({");
			writer.println("  position: new google.maps.LatLng(" + posicaoAtual.getLatitude() + "," + posicaoAtual.getLongitude() + "),");
			writer.println("  map: map,");
			writer.println("  icon: 'http://maps.google.com/mapfiles/ms/icons/" + cor + "-dot.png', ");
			writer.println("  title: '" + conteudo + "'");
			writer.println("});");

//			writer.println("marker" + contador + ".addListener('click', function() {");
//			writer.println("  var infowindow = new google.maps.InfoWindow({");
//			writer.println("    content: '" + conteudo + "'");
//			writer.println("  });");
//			writer.println("  infowindow.open(map, marker" + contador + ");");
//			writer.println("});");
//			writer.println();

			contador++;
		}
	}

	/**
	 * Retorna uma cor de acordo com o tipo de posição do veículo
	 */
	private String pegaCorPosicaoVeiculo(PosicaoVeiculo posicaoAtual) 
	{
		if (posicaoAtual.getTipo() == TipoPosicaoVeiculo.Ida)
			return "blue";
		
		if (posicaoAtual.getTipo() == TipoPosicaoVeiculo.Volta)
			return "green";

		return "yellow";
	}
}