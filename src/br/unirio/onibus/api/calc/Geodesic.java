package br.unirio.onibus.api.calc;

public class Geodesic
{
	private static final double EARTH_RADIUS = 6371.0; // em Km

	/**
	 * Método usado para calcular a distância entre dois pontos em coordenadas geográficas
	 */
	public static double distance(double lat1, double lon1, double lat2, double lon2)
	{
		 return distanceHaversine(lat1, lon1, lat2, lon2);
	}

	/**
	 * Calcula a distância entre dois pontos em uma esfera usando a fórmula do arco-cosseno
	 * 
	 * Ver em https://en.wikipedia.org/wiki/Great-circle_distance
	 */
	public static double distanceArccosine(double lat1, double lon1, double lat2, double lon2)
	{
		 double rTheta = Math.toRadians(lon1 - lon2);
		 double rLat1 = Math.toRadians(lat1);
		 double rLat2 = Math.toRadians(lat2);
		 return Math.acos(Math.sin(rLat1) * Math.sin(rLat2) + Math.cos(rLat1) * Math.cos(rLat2) * Math.cos(rTheta)) * EARTH_RADIUS;
	}

	/**
	 * Calcula a distância entre dois pontos em uma esfera usando a fórmula de Haversine, 
	 * que é mais precisa para curtas distâncias
	 * 
	 * Ver em https://en.wikipedia.org/wiki/Great-circle_distance
	 * Ver em http://www.movable-type.co.uk/scripts/latlong.html
	 */
	public static double distanceHaversine(double lat1, double lon1, double lat2, double lon2)
	{
		double rTheta = Math.toRadians(lon1 - lon2);
		double rLat1 = Math.toRadians(lat1);
		double rLat2 = Math.toRadians(lat2);
		double a = Math.pow(Math.sin((rLat2 - rLat1) / 2), 2) + Math.cos(rLat1) * Math.cos(rLat2) * Math.pow(Math.sin((rTheta) / 2), 2);
		return Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)) * 2 * EARTH_RADIUS;
	}

	/**
	 * Calcula a distância entre dois pontos em uma esfera usando a fórmula de Vincenty, 
	 * que não tem os problemas para longas distâncias da Haversine
	 * 
	 * Ver em https://en.wikipedia.org/wiki/Great-circle_distance
	 */
	public static double distanceVincenty(double lat1, double lon1, double lat2, double lon2)
	{
		double rTheta = Math.toRadians(lon1 - lon2);
		double rLat1 = Math.toRadians(lat1);
		double rLat2 = Math.toRadians(lat2);
		double position1 = Math.sqrt(Math.pow(Math.cos(rLat2) * Math.sin(rTheta), 2) + Math.pow(Math.cos(rLat1) * Math.sin(rLat2) - Math.sin(rLat1) * Math.cos(rLat2) * Math.cos(rTheta), 2));
		double position2 = Math.sin(rLat1) * Math.sin(rLat2) + Math.cos(rLat1) * Math.cos(rLat2) * Math.cos(rTheta);
		return Math.atan2(position1, position2) * EARTH_RADIUS;
	}

	/**
	 * Calcula o ângulo de movimento entre dois pontos, em graus
	 * 
	 * Ver em http://www.movable-type.co.uk/scripts/latlong.html
	 */
	public static double bearing(double lat1, double lon1, double lat2, double lon2)
	{
		double rTheta = Math.toRadians(lon2 - lon1);
		double rLat1 = Math.toRadians(lat1);
		double rLat2 = Math.toRadians(lat2);
		double y = Math.sin(rTheta) * Math.cos(rLat2);
		double x = Math.cos(rLat1) * Math.sin(rLat2) - Math.sin(rLat1) * Math.cos(rLat2) * Math.cos(rTheta);
		double theta = Math.atan2(y, x);
		return (Math.toDegrees(theta) + 360) % 360;
	}

	/**
	 * Calcula a menor distância entre um ponto e uma trilha definida por dois pontos - método mais preciso
	 */
	public static double trackDistance(double lat, double lon, double latTrack1, double lonTrack1, double latTrack2, double lonTrack2)
	{
		double distance13 = distance(latTrack1, lonTrack1, lat, lon);
		double bearing13 = bearing(latTrack1, lonTrack1, lat, lon);
		double bearing12 = bearing(latTrack1, lonTrack1, latTrack2, lonTrack2);
		return Math.abs(Math.asin(Math.sin(distance13 / EARTH_RADIUS) * Math.sin(Math.toRadians(bearing13 - bearing12))) * EARTH_RADIUS);
	}
	
	/**
	 * Calcula a menor distância entre um ponto e uma trilha definida por dois pontos - aproximação linear
	 */
	public static double linearTrackDistance(double lat, double lon, double latTrack1, double lonTrack1, double latTrack2, double lonTrack2) 
	{
		double a = (lonTrack2 - lonTrack1) / (latTrack2 - latTrack1);
		double b = lonTrack1 - a * latTrack1;
		
		double ilat = (lon + a * lat - b) / (2 * a);
		double ilong = a * ilat + b;
		
		return Geodesic.distance(lat, lon, ilat, ilong);
	}
}