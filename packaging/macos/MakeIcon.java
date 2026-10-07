import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Generates the placeholder app icon: a rounded square with a "J" and a small "2".
 *
 * Usage (from the repo root; needs a JDK and macOS iconutil for the .icns step):
 *   java packaging/macos/MakeIcon.java packaging/macos
 *
 * Writes packaging/macos/JavaOp2.icns and
 * plugins/SwingGui/src/main/resources/com/javaop/SwingGui/icon.png (512px, used for the Dock icon
 * when running outside the packaged app).
 */
public class MakeIcon {

	static BufferedImage render(int size) {
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		// macOS icon grid: artwork occupies ~80% of the canvas
		double inset = size * 0.1, body = size * 0.8, arc = body * 0.225;
		RoundRectangle2D shape = new RoundRectangle2D.Double(inset, inset, body, body, arc * 2, arc * 2);

		// soft drop shadow
		g.setColor(new Color(0, 0, 0, 60));
		g.fill(new RoundRectangle2D.Double(inset, inset + size * 0.012, body, body, arc * 2, arc * 2));

		g.setPaint(new GradientPaint(0, (float) inset, new Color(0x2B3A67),
				0, (float) (inset + body), new Color(0x0F7C8C)));
		g.fill(shape);

		// subtle top highlight
		g.setClip(shape);
		g.setPaint(new GradientPaint(0, (float) inset, new Color(255, 255, 255, 50),
				0, (float) (inset + body * 0.5), new Color(255, 255, 255, 0)));
		g.fill(new Rectangle2D.Double(inset, inset, body, body * 0.5));
		g.setClip(null);

		g.setColor(Color.WHITE);
		Font jFont = new Font(Font.SANS_SERIF, Font.BOLD, (int) (body * 0.72));
		centre(g, "J", jFont, size * 0.46, size * 0.52);
		Font twoFont = new Font(Font.SANS_SERIF, Font.BOLD, (int) (body * 0.26));
		g.setColor(new Color(0xBDF3F8));
		centre(g, "2", twoFont, size * 0.67, size * 0.60);
		g.dispose();
		return img;
	}

	/** Draws text with its visual centre at (cx, cy). */
	static void centre(Graphics2D g, String s, Font f, double cx, double cy) {
		GlyphVector gv = f.createGlyphVector(g.getFontRenderContext(), s);
		Rectangle2D b = gv.getVisualBounds();
		g.drawGlyphVector(gv, (float) (cx - b.getCenterX()), (float) (cy - b.getCenterY()));
	}

	public static void main(String[] args) throws Exception {
		File outDir = new File(args.length > 0 ? args[0] : ".");
		File iconset = new File(outDir, "JavaOp2.iconset");
		iconset.mkdirs();
		int[] sizes = { 16, 32, 128, 256, 512 };
		for (int s : sizes) {
			ImageIO.write(render(s), "png", new File(iconset, "icon_" + s + "x" + s + ".png"));
			ImageIO.write(render(s * 2), "png", new File(iconset, "icon_" + s + "x" + s + "@2x.png"));
		}
		File png = new File("plugins/SwingGui/src/main/resources/com/javaop/SwingGui/icon.png");
		png.getParentFile().mkdirs();
		ImageIO.write(render(512), "png", png);

		File icns = new File(outDir, "JavaOp2.icns");
		Process p = new ProcessBuilder("iconutil", "-c", "icns", "-o", icns.getPath(), iconset.getPath())
				.inheritIO().start();
		if (p.waitFor() != 0) throw new IllegalStateException("iconutil failed");
		for (File f : iconset.listFiles()) f.delete();
		iconset.delete();
	}
}
