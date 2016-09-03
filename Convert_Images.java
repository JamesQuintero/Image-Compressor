import java.io.*;
import java.util.*;

public class Convert_Images
{

	public static void main(String[] args)
	{
		Image_RGB image_converter = new Image_RGB();

		Scanner scanner = new Scanner(System.in);
		
		System.out.println("Will losslessly convert all png images found in a directory to jpg images.");
		
		
		System.out.print("Folder of images: ./");
		String dir_path = scanner.next();
		
		System.out.print("Destination for images: ./");
		String dest_path = scanner.next();
		
		
		File folder = new File("./"+dir_path);
		File dest_dir = new File("./"+dest_path);
		File[] list_of_files = folder.listFiles();
		

		//if destination doesn't exist, create it
		if(dest_dir.exists()==false)
		{
			try
			{
				dest_dir.mkdir();
			} 
			catch(Exception error){
					System.out.println("Couldn't create destination directory");
			}
		}


		try
		{
			System.out.println("Converting...");
			for (int x =0; x < list_of_files.length; x++)
			{
				//only converts pngs
				if(list_of_files[x].getName().toLowerCase().indexOf(".png")!=-1)
				{
					//removed last 3 characters, and hopefully that's the png extension
					String removed_png = list_of_files[x].getName().substring(0, list_of_files[x].getName().length()-3);
					String new_filename = removed_png + "jpg";
					String new_path="./"+dest_path+"/"+new_filename;
					
					//if destination image doesn't exist, then convert
					if (new File(new_path).isFile()==false)
						image_converter.convertImage(dir_path+"/"+list_of_files[x].getName(), new_path);
					else
						System.out.println("Skipped ./"+dir_path+"/"+list_of_files[x].getName());
					
					System.out.println("Converted ./"+dir_path+"/"+list_of_files[x].getName()+" -> "+new_path);
				}
				else
					System.out.println("Skipped ./"+dir_path+"/"+list_of_files[x].getName());
			}
		} catch(Exception error)
		{
			System.out.println("Something went wrong: "+error.toString());
		}
		
		System.out.println("Done converting.");
	}

}
