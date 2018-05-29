package kmeansclustering;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
 

public class kmeans {
	
    public static void main(String [] args){
	if (args.length < 3){
	    System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
	    return;
	}
	try{
	    BufferedImage originalImage = ImageIO.read(new File(args[0]));
	    int k=Integer.parseInt(args[1]);
	    BufferedImage kmeansJpg = kmeans_helper(originalImage,k);
	    ImageIO.write(kmeansJpg, "jpg", new File(args[2])); 
	    
	}catch(IOException e){
	    System.out.println(e.getMessage());
	}	
    }
    
    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k){
	int w=originalImage.getWidth();
	int h=originalImage.getHeight();
	BufferedImage kmeansImage = new BufferedImage(w,h,originalImage.getType());
	Graphics2D g = kmeansImage.createGraphics();
	g.drawImage(originalImage, 0, 0, w,h , null);
	// Read rgb values from the image
	int[] rgb=new int[w*h];
	int count=0;
	for(int i=0;i<w;i++){
	    for(int j=0;j<h;j++){
		rgb[count++]=kmeansImage.getRGB(i,j);
	    }
	}
	// Call kmeans algorithm: update the rgb values
	rgb = kmeans1(rgb,k,w,h);

	// Write the new rgb values to the image
	count=0;
	for(int i=0;i<w;i++){
	    for(int j=0;j<h;j++){
		kmeansImage.setRGB(i,j,rgb[count++]);
	    }
	}
	return kmeansImage;
    }

    // Your k-means code goes here
    // Update the array rgb by assigning each entry in the rgb array to its cluster center
    private static int[] kmeans1 (int[] rgb, int k,int w,int h){
    	//Getting k random colours from the image
    	Random rand = new Random();
    	int max = w*h;
    	int min = 0;
    	int kcolor[] = new int[k];
    	
     	for(int i=0;i<k;i++)
    	{
    		kcolor[i] = rgb[rand.nextInt(max-min+1)+min];
    		
    	}
     	//Generate separate r,g and b for the original image to save computation time
     	int[] r = new int[w*h];
     	int[] b = new int [w*h];
     	int[] g = new int [w*h];
     	for(int i=0;i<w*h;i++)
     	{
     		r[i]=(rgb[i]>>16) & 0xff;
     		g[i] = (rgb[i]>>8) &0xff;
     		b[i] = rgb[i] &0xff;
     	}
     	
     	//Starting the k means algorithm and looping until convergence
     	
     	for(int i=0;i<50;i++)
     	{	int[] newkcolor = new int[k];
     		 newkcolor = kmeans2(kcolor,w,h,k,rgb,r,g,b);
     		 kcolor = newkcolor;
     	}
     	
     	double[][] table = new double[k+1][w*h];
     	table = gettable(kcolor,w,h,k,rgb,r,g,b);
     	for(int i=0;i<w*h;i++)
     	{
     		int var = (int) table[k][i];
     		rgb[i] = kcolor[var];
     	}
     	
    	return rgb;
    	
    }
    
    public static int[] kmeans2(int[] kcolor,int w, int h, int k, int[]rgb,int[]r,int[]g,int[] b)
    {	//Generating separate values of r,g and b for the k colors
    	int rk[] = new int[k];
		int gk[] = new int [k];
		int bk[] = new int[k];
    	for(int i=0;i<k;i++) {
    	rk[i] = (kcolor[i]>>16) & 0xff;
		gk[i] = (kcolor[i]>>8) & 0xff;
		bk[i] = (kcolor[i]) & 0xff;
    	}
    	
    	//generating distance table and assignment table 
    	double[][] table = new double[k+1][w*h];
    	for(int i=0;i<w*h;i++)
    	{	//Calculating distance
    		for(int j=0;j<k;j++)
    		{
    			table [j][i] = (double) Math.sqrt(Math.pow((r[i]-rk[j]), 2)+Math.pow((g[i]-gk[j]), 2) + Math.pow((b[i]-bk[j]), 2) );
    			table [k][i] = 0; //initializing the temporary assignment of cluster
    		}
    		//assigning each pixel to a cluster
    		for(int j=1;j<k;j++)
    		{
    			double temp = table [j][i]; //storing the new distance
    			int var = (int) table[k][i]; //storing temporary cluster assignment
    			if(temp<table[var][i])
    			{
    				table[k][i] = (double) j; //assigning the new cluster 
    			}
    				
    		}
    	}
    	
    	//Getting the new k colors
    	int[] newkcolor = new int[k];
    	for(int i=0;i<k;i++)
    	{	int count =0;
    		for(int j=0;j<w*h;j++)
    		{	
    			if((int)table[k][j]==i)
    			{
    				newkcolor[i]+=rgb[j];
    				count++;
    			}
    		}
    		if(count!=0)
    		{newkcolor[i] = newkcolor[i]/count;}
    	}
    	
    	return newkcolor;
    	
    }
public static double[][] gettable(int[] kcolor,int w,int h,int k, int[] rgb,int[]r,int[]g,int[]b)
{
	int rk[] = new int[k];
	int gk[] = new int [k];
	int bk[] = new int[k];
	for(int i=0;i<k;i++) {
	rk[i] = (kcolor[i]>>16) & 0xff;
	gk[i] = (kcolor[i]>>8) & 0xff;
	bk[i] = (kcolor[i]) & 0xff;
	}
	
	//generating distance table and assignment table 
	double[][] table = new double[k+1][w*h];
	for(int i=0;i<w*h;i++)
	{	//Calculating distance
		for(int j=0;j<k;j++)
		{
			table [j][i] = (double) Math.sqrt(Math.pow((r[i]-rk[j]), 2)+Math.pow((g[i]-gk[j]), 2) + Math.pow((b[i]-bk[j]), 2) );
			table [k][i] = 0; //initializing the temporary assignment of cluster
		}
		//assigning each pixel to a cluster
		for(int j=1;j<k;j++)
		{
			double temp = table [j][i]; //storing the new distance
			int var = (int) table[k][i]; //storing temporary cluster assignment
			if(temp<table[var][i])
			{
				table[k][i] = (double) j; //assigning the new cluster 
			}
				
		}
	}
	return table;
  }
}
