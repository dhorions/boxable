package be.quodlibet.boxable.utils;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class ImageUtilsTest {

	@Test
	public void test300DpiJpeg() throws Exception {
		File file = new File(ImageUtilsTest.class.getResource("/300dpi.jpg").toURI());
		float[] dpi = ImageUtils.getDPI(file);
		Assert.assertEquals("Horizontal DPI is not 300", 300, dpi[0], 0.01f);
		Assert.assertEquals("Veritcal DPI is not 300", 300, dpi[1], 0.01f);
	}
	
	@Test
	public void test72DpiPng() throws Exception {
		File file = new File(ImageUtilsTest.class.getResource("/72dpi.png").toURI());
		float[] dpi = ImageUtils.getDPI(file);
		Assert.assertEquals("Horizontal DPI is not 72", 72, dpi[0], 0.01f);
		Assert.assertEquals("Veritcal DPI is not 72", 72, dpi[1], 0.01f);
	}
	
	
}
