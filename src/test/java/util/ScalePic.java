
package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScalePic {

	public static void main(String[] args) throws IOException {
		scale(18, 2, "curse");
		scale(18, 2, "cleanse");
		scale(18, 2, "bleed");
	}

	private static void scale(int original, int scale, String name) throws IOException {
		File in = new File("./temp/in/" + name + ".png");
		File out = new File("./temp/out/" + name + ".png");
		int total = original * scale;
		BufferedImage img = ImageIO.read(in);
		BufferedImage ans = new BufferedImage(total, total, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < original; x++)
			for (int y = 0; y < original; y++) {
				int col = img.getRGB(x, y);
				if (col >> 24 == 0) {
					col = 0xFFFFFFFF;
				}
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

}
