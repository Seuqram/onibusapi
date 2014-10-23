package br.unirio.onibus.api.calc;

import org.junit.Assert;
import org.junit.Test;

public class TestGeodesic
{
	@Test
	public void testDistance()
	{
		Assert.assertEquals(Geodesic.distance(-22.954471, -43.166832, -22.956684, -43.191895), 2.58, 0.01);
		Assert.assertEquals(Geodesic.distance(-22.954471, -43.166832, -22.988570, -43.227600), 7.28, 0.01);
		Assert.assertEquals(Geodesic.distance(-22.942811, -43.198933, -23.538403, -46.659627), 359.72, 0.01);
		Assert.assertEquals(Geodesic.distance(-3.699377, -38.529744, -23.538403, -46.659627), 2372.58, 0.01);
		Assert.assertEquals(Geodesic.distance(27.284319, -81.156697, -23.538403, -46.659627), 6756.37, 0.01);
		Assert.assertEquals(Geodesic.distance(27.284319, -81.156697, 40.371996, -3.599899), 7084.85, 0.01);
	}
	
	@Test
	public void testBearing()
	{
		Assert.assertEquals(Geodesic.bearing(52.205, 0.119, 48.857, 2.351), 156.16, 0.01);
	}

	@Test
	public void testTrackDistance()
	{
		Assert.assertEquals(Geodesic.trackDistance(-22.954471, -43.166832, -22.954471, -43.166832, -22.956684, -43.191895), 0.0, 0.01);
		Assert.assertEquals(Geodesic.trackDistance(-22.956684, -43.191895, -22.954471, -43.166832, -22.956684, -43.191895), 0.0, 0.01);

		Assert.assertEquals(Geodesic.trackDistance(41.791057, -94.046875, 41.81762, -94.127592, 41.848202, -94.087257), 6.84, 0.01);
		Assert.assertEquals(Geodesic.trackDistance(34.5, -116.5, 33.95, -118.4, 40.63333, -73.78333), 13.81, 0.01);
	}
}