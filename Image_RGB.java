import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.util.Iterator;

public class Image_RGB
{
	
	//mostly used for converting PNGs to JPGs losslessly
	//reads real_values of the pixels instead of rgb_values
	//purpose is to save images from path to new_path
	public void convertImage(String path, String new_path)
	{
		BufferedImage image=null;
		int[][][] real_values;
		try
		{
			image = ImageIO.read(Image_RGB.class.getResource(path));
			real_values = getRealValues(image);
		} catch(Exception e)
		{
			System.out.println("Error reading image: "+e.toString());
			System.out.println(path+" | "+new_path);
			real_values=new int[0][0][0];
		}
		
		
		//converts real values into single array for image creation
		int[] new_array=new int[real_values.length * real_values[0].length * real_values[0][0].length];
		int index=0;
		for(int x =0; x < real_values.length; x++)
		{
			for(int y =0; y < real_values[x].length; y++)
			{
				new_array[index]=real_values[x][y][0];
				new_array[index+1]=real_values[x][y][1];
				new_array[index+2]=real_values[x][y][2];

				if(real_values[x][y].length>3)
				{
					new_array[index+3]=real_values[x][y][3];
					index+=4;
				}
				else
					index+=3;
			}
		}
		
		String ext="";
		if (new_path.toLowerCase().contains(".jpg") || new_path.toLowerCase().contains(".jpeg"))
			ext="jpg";
		else if (new_path.toLowerCase().contains(".png"))
			ext="png";
		
		createImage(new_path, ext, image.getWidth(), image.getHeight(), new_array);
	}

	//mostly used for converting PNGs to JPGs losslessly
	//reads rgb_values of the pixels instead of real_values
	//purpose is to save images from path to new_path
	public void convertImage2(String path, String new_path)
	{
		BufferedImage image=null;
		int[][][] values;
		try
		{
			image = ImageIO.read(Image_RGB.class.getResource(path));
			values = getRGBValues(image);
		} catch(Exception e)
		{
			System.out.println("Error reading image: "+e.toString());
			System.out.println(path+" | "+new_path);
			values=new int[0][0][0];
		}
		
		//3D array into single array for image creation
		int[] new_array=new int[values.length * values[0].length * values[0][0].length];
		int index=0;
		for(int x =0; x < values.length; x++)
		{
			for(int y =0; y < values[x].length; y++)
			{
				new_array[index]=values[x][y][0];
				new_array[index+1]=values[x][y][1];
				new_array[index+2]=values[x][y][2];
				if(values[x][y].length>3)
				{
					new_array[index+3]=values[x][y][3];
					index+=4;
				}
				else
					index+=3;
			}
		}
		

		String ext="";
		if (new_path.toLowerCase().contains(".jpg") || new_path.toLowerCase().contains(".jpeg"))
			ext="jpg";
		else if (new_path.toLowerCase().contains(".png"))
			ext="png";
		
		createImage(new_path, ext, image.getWidth(), image.getHeight(), new_array);
	}
	
	public static void createImage(String path, String ext, int width, int height, int[] pixels)
	{   
		try
		{
			//wanting to create png
			if(ext=="png")
			{
				//creates an image using the array of pixel values
				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
				WritableRaster raster = image.getRaster();
				raster.setPixels(0,0,width,height,pixels);
				
				//writes new image to disk
				File outputfile = new File(path);
				ImageIO.write(image, ext, outputfile);
			}
			else if(ext=="jpg")
			{
				//creates an image using the array of pixel values
				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
				WritableRaster raster = image.getRaster();
				raster.setPixels(0,0,width,height,pixels);
					
				Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");
				ImageWriter writer = (ImageWriter)iter.next();
				ImageWriteParam iwp = writer.getDefaultWriteParam();
				
				iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				//ensures lossless jpg compression
				iwp.setCompressionQuality(1.0f);
				
				//writes new image to disk
				File file = new File(path);
				FileImageOutputStream output = new FileImageOutputStream(file);
				writer.setOutput(output);
				IIOImage image2 = new IIOImage(image, null, null);
				writer.write(null, image2, iwp);
				writer.dispose();
			}
		} catch (IOException e)
		{
				System.out.println("There was a problem saving the image: "+e.toString());
		}
	}

	//returns pixel values as 2D array of rgba array
	//rgba array example: [255,100,50,0]. Uses standard 0-255 color values
	public static int[][][] getRGBValues(BufferedImage image)
	{
		byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		int width = image.getWidth();
		int height = image.getHeight();
		//PNGs have alpha channels because of transparency
		boolean hasAlphaChannel = image.getAlphaRaster() != null;
				

		int[][][] result = new int[height][width][4];
		if (hasAlphaChannel)
		{
			int pixelLength = 4;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength)
			{
			
				int[] rgb=new int[pixelLength];
				//rgb[0]=((int) pixels[pixel + 3] & 0xff); // red
				//rgb[1]=((int) pixels[pixel + 2] & 0xff); // green
				//rgb[2]=((int) pixels[pixel+1] & 0xff); // blue
				//rgb[3]=((int) pixels[pixel] & 0xff); // alpha
				rgb[0]=((int) pixels[pixel + 3]); // red
				rgb[1]=((int) pixels[pixel + 2]); // green
				rgb[2]=((int) pixels[pixel+1]); // blue
				rgb[3]=((int) pixels[pixel]); // alpha
				
				result[row][col] = rgb;
				col++;
				if (col == width)
				{
					col = 0;
					row++;
				}
			}
		}
		else
		{
			int pixelLength = 3;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength)
			{
				int[] rgb=new int[pixelLength];
				//rgb[0]=((int) pixels[pixel + 2] & 0xff); // red
				//rgb[1]=((int) pixels[pixel + 1] & 0xff); // green
				//rgb[2]=((int) pixels[pixel] & 0xff); // blue
				//rgb[3]=-1;
				rgb[0]=((int) pixels[pixel + 2]); // red
				rgb[1]=((int) pixels[pixel + 1]); // green
				rgb[2]=((int) pixels[pixel]); // blue
				
				result[row][col] = rgb;
				col++;
				if (col == width)
				{
					col = 0;
					row++;
				}
			}
		}

		return result;
	}
	
	//returns pixel values as 2D array of rgba array
	//rgba array example: [255,100,50,0]. Uses different 0-255 color values
	//blue value is standard 0-255, but red and green are negative. 255 + (this red or green) = standard 0-255 value
	public static int[][][] getRealValues(BufferedImage image)
	{
		byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		int width = image.getWidth();
		int height = image.getHeight();
		boolean hasAlphaChannel = image.getAlphaRaster() != null;
		
			
		int[][][] result = new int[height][width][3];
		if (hasAlphaChannel)
		{
			final int pixelLength = 4;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength)
			{
				int[] abgr=new int[4];
				abgr[0]=pixels[pixel]; // alpha
				abgr[1]=pixels[pixel + 1]; // blue
				abgr[2]=pixels[pixel+2]; // green
				abgr[3]=pixels[pixel+3]; // red
				result[row][col] = abgr;
				col++;
				if (col == width)
				{
					col = 0;
					row++;
				}
			}
		}
		else
		{
			final int pixelLength = 3;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength)
			{
				int[] bgr=new int[3];
				// bgr[0]=pixels[pixel]; // blue
				// bgr[1]=pixels[pixel+1]; // green
				// bgr[2]=pixels[pixel+2]; // red
				bgr[2]=((int) pixels[pixel] & 0xff); // blue
				bgr[1]=((int) pixels[pixel+1] & 0xff); // green
				bgr[0]=((int) pixels[pixel+2] & 0xff); // red
				// bgr[2]=pixels[pixel]; // blue
				// bgr[1]=pixels[pixel+1]; // green
				// bgr[0]=pixels[pixel+2]; // red
				result[row][col] = bgr;
				col++;
				if (col == width)
				{
					col = 0;
					row++;
				}
			}
		}

		return result;
	}
}
