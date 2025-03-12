
package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScalePic {

	public static void main(String[] args) throws IOException {
		//scale(4, "logo1", "logo1");
		//scale(18, "run_bow");
		//scale(16, "sonic_shooter");

		//resize(64, 0,0,"iron");
		scale(25, "logo", "logo");
		//resize(18, 1,1,"moonwalk");
		//resample(16, 0, 0, "logo", 9);
	}

	private static void scale(String name) throws IOException {
		scale(2, name, name);
		scale(8, name, name + "_large");
	}

	private static void resample(int size, int x0, int y0, String name, int scale) throws IOException {
		File in = new File("./temp/in/" + name + ".png");
		File out = new File("./temp/out/" + name + ".png");
		BufferedImage img = ImageIO.read(in);
		int sx = img.getWidth();
		int sy = img.getHeight();
		BufferedImage ans = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		int base = img.getRGB(0, 0);
		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++) {
				int col = img.getRGB(x * scale, y * scale);
				if (col == base) col = 0;
				ans.setRGB(x0 + x, y0 + y, col);
			}
		if (!out.exists()) {
			out.createNewFile();
		}
		ImageIO.write(ans, "PNG", out);
	}

	private static void resize(int size, int x0, int y0, String name) throws IOException {
		File in = new File("./temp/in/" + name + ".png");
		File out = new File("./temp/out/" + name + ".png");
		BufferedImage img = ImageIO.read(in);
		int sx = img.getWidth();
		int sy = img.getHeight();
		BufferedImage ans = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < sx; x++)
			for (int y = 0; y < sy; y++) {
				int col = img.getRGB(x, y);
				ans.setRGB(x0 + x, y0 + y, col);
			}
		if (!out.exists()) {
			out.createNewFile();
		}
		ImageIO.write(ans, "PNG", out);
	}

	private static void scale(int scale, String name, String out_name) throws IOException {
		File in = new File("./temp/in/" + name + ".png");
		File out = new File("./temp/out/" + out_name + ".png");
		BufferedImage img = ImageIO.read(in);
		int sx = img.getWidth();
		int sy = img.getHeight();
		BufferedImage ans = new BufferedImage(sx * scale, sy * scale, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < sx; x++)
			for (int y = 0; y < sy; y++) {
				int col = img.getRGB(x, y);
				for (int i = 0; i < scale; i++)
					for (int j = 0; j < scale; j++) {
						ans.setRGB(x * scale + i, y * scale + j, col);
					}
			}
		if (!out.exists()) {
			out.createNewFile();
		}
		ImageIO.write(ans, "PNG", out);
	}

	private static void merge(String a, String b) throws IOException {
		File ina = new File("./temp/in/" + a + ".png");
		File inb = new File("./temp/in/" + b + ".png");
		File out = new File("./temp/out/" + a + ".png");
		BufferedImage imga = ImageIO.read(ina);
		BufferedImage imgb = ImageIO.read(inb);
		int sx = imga.getWidth();
		int sy = imga.getHeight();
		BufferedImage ans = new BufferedImage(sx, sy, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < sx; x++)
			for (int y = 0; y < sy; y++) {
				int cola = imga.getRGB(x, y);
				int colb = imgb.getRGB(x, y);
				ans.setRGB(x, y, colb == 0 ? cola : colb);
			}
		if (!out.exists()) {
			out.createNewFile();
		}
		ImageIO.write(ans, "PNG", out);
	}

}
