import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

/**
 * Class for working with images.
 *
 */
public class ImageUtil {

	/**
	 * Get rotated image.
	 * 
	 * @param source image to rotate
	 * @return
	 */
	public static Image getTappedImage(Image original) {
		
		int width = original.getWidth(null);
		int height = original.getHeight(null);
		
		if (width == -1 || height == -1) {
			return null;
		}
		
		BufferedImage source = new BufferedImage(height, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = source.getGraphics();
		g.drawImage(original, 0, 0, null);
		g.dispose();

		BufferedImage target = rotateImage(source);

		BufferedImage result = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);
		g = result.getGraphics();
		g.drawImage(target, 0, 0, null);
		g.dispose();
		
		return result;
	}
	

	/**
	 * Rotate image (90 degrees).
	 * 
	 * @param source image to rotate
	 * @return rotated image
	 */
    protected static BufferedImage rotateImage(BufferedImage source) {
        BufferedImage target;

        Graphics2D g = (Graphics2D) source.getGraphics();
        g.drawImage(source, 0, 0, null);

        AffineTransform at = new AffineTransform();

        /**
         * Rotate 90 degrees around image center
         */
        at.rotate(90.0 * Math.PI / 180.0, source.getWidth() / 2.0, source
                .getHeight() / 2.0);

        /**
         * Instantiate and apply affine transformation filter
         */
        BufferedImageOp bio;
        bio = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

        target = bio.filter(source, null);

        g.dispose();

        return target;
    }
}