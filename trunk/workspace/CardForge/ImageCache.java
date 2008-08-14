import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * For storing card images in a cache.
 */
public class ImageCache {

	/**
	 * Card size.
	 */
	protected static Rectangle cardSize = new Rectangle(93, 130);
	
	/**
	 * Cache for storing images.
	 */
	protected static HashMap<String, Image> cache = new HashMap<String, Image>(); 
	
	/**
	 * Load image from disk and then put it into the cache. 
	 * If image has been loaded before then no loading is needed.
	 * 
	 * @param card card to load image for
	 * @return {@link Image}
	 */
	public static Image getImage(Card card) {
		
		/**
		 * Try to get from cache.
		 */
		if (cache.containsKey(card.getName())) {
			return cache.get(card.getName());
		}
		
		/**
		 * Load image.
		 */
		BufferedImage image = null;
		Image resized = null;
		
		String imagePath = getImagePath(card);
		if (imagePath == null) {
			return null;
		}
		
		try {
			image = ImageIO.read(new File(imagePath));

			resized = image.getScaledInstance(cardSize.width, cardSize.height,
					java.awt.Image.SCALE_SMOOTH);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/**
		 * Put to cache.
		 */
		cache.put(card.getName(), resized);
		
		return resized;
	}
	
	/**
	 * Get path to image for specific card.
	 * 
	 * @param c card to get path for
	 * @return String if image exists, else null
	 */
	public static String getImagePath(Card c)
	  {
	    if(AllZone.NameChanger.shouldChangeCardName()) {
	    	return null;
	    }

	    String baseDir = Constant.IO.imageBaseDir;
	    String suffix = ".jpg";

	    String filename = baseDir + GuiDisplayUtil.cleanString(c.getName()) +suffix;
	    File file = new File(filename);

	    /**
	     * try current directory 
	     */
	    if(! file.exists())
	    {
	      filename = GuiDisplayUtil.cleanString(c.getName()) +suffix;
	      file = new File(filename);
	    }


	    if(file.exists())
	    {
	    	return filename;
	    } else
	    {
	    	return null;
	    }
	  }
}
