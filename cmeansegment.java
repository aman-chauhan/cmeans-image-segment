/**
 * 
 */
package CMSegment;

import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Scanner;
import java.util.Random;
import java.lang.Math;

/**
 * @author Aman Chauhan
 *
 */
public class cmeansegment
{
	@SuppressWarnings("finally")
	public static int [][][] copyimage(File file)
	{
		int x,y,width,height;
		int array[][][]=null;
		try
		{
			BufferedImage img=ImageIO.read(file);
			width=img.getWidth();
			height=img.getHeight();
			array=new int[height][width][4];
			Color c=null;
			int temp;
			for(y=0;y<height;++y)
			{
				for(x=0;x<width;++x)
				{
					temp=img.getRGB(x, y);
					array[y][x][0]=(temp>>24) & 0xFF;
					c=new Color(temp);
					array[y][x][1]=c.getRed();
					array[y][x][2]=c.getGreen();
					array[y][x][3]=c.getBlue();
				}
			}
			return array;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			return array;
		}
	}
	
	public static void createImage(int array[][][])
	{
		int height=array.length;
		int width=array[0].length;
		int i,j;
		BufferedImage img=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		for(i=0;i<width;++i)
		{
			for(j=0;j<height;++j)
			{
				img.setRGB(i, j, array[j][i][0]<<24|array[j][i][1]<<16|array[j][i][2]<<8|array[j][i][3]);
			}
		}
		File imgfile=new File("I:/Pictures/Miscelleneous/test.png");
		try
		{
			ImageIO.write(img, "png", imgfile);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		File file=new File("I:/Pictures/Miscelleneous/gokua.png");
		Scanner input=new Scanner(System.in);
		Random random = null;
		int array[][][]=copyimage(file);
		
		int clusters[][]=null;
		double distance[][]=null;
		double prob[][]=null;
		
		int i,j,k,l,m,w;
		int ccount;
		double itr,spd,sum=0.0,num,den;
		
		System.out.println("Enter the number of colors you want (2-10) - ");
		do
		{
			ccount=input.nextInt();
		}while(ccount>10||ccount<2);
		
		System.out.println("\nSpecify the speed of conversion (1-10)(1-slowest 10-fastest) - ");
		do
		{
			spd=input.nextDouble();
		}while(spd>10.0||spd<1.0);
		spd=(spd/10.0)+1;
		
		System.out.println("\nSpecify the accuracy of conversion (1-50)(1-lowest,fastest 50-highest,slowest) - ");
		do
		{
			itr=input.nextDouble();
		}while(itr>50.0||itr<1.0);
		
		clusters=new int[ccount][4];
		distance=new double[array.length*array[0].length][ccount];
		prob=new double[array.length*array[0].length][ccount];
		
		for(i=0;i<ccount;++i)
		{
			for(j=0;j<4;++j)
			{
				random=new Random();
				clusters[i][j]=random.nextInt(256);
			}
		}
		
		l=array.length*array[0].length;
		w=array[0].length;
		//h=array.length;
		
		for(k=0;k<itr;++k)
		{
			for(i=0;i<l;++i)
			{
				for(j=0;j<ccount;++j)
				{
					distance[i][j]=		Math.sqrt(Math.pow(array[i/w][i%w][0]-clusters[j][0], 2)
									+	Math.pow(array[i/w][i%w][1]-clusters[j][1], 2)
									+	Math.pow(array[i/w][i%w][2]-clusters[j][2], 2)
									+	Math.pow(array[i/w][i%w][3]-clusters[j][3], 2));
				}
			}
			System.out.println("Iteration "+(k+1)+" distances - done");
			
			for(i=0;i<l;++i)
			{
				sum=0.0;
				for(j=0;j<ccount;++j)
				{
					sum+=Math.pow(1.0/distance[i][j],1/(spd-1));
				}
				
				for(j=0;j<ccount;++j)
				{
					prob[i][j]=Math.pow(1.0/distance[i][j], 1/(spd-1))/sum;
				}
			}
			System.out.println("Iteration "+(k+1)+" probabilities - done");
			
			for(j=0;j<ccount;++j)
			{
				for(m=0;m<4;++m)
				{
					num=0.0;
					den=0.0;
					for(i=0;i<l;++i)
					{
						num+=array[i/w][i%w][m]*Math.pow(prob[i][j], spd);
						den+=Math.pow(prob[i][j], spd);
					}
					clusters[j][m]=(int)Math.floor(num/den);
				}
			}
			System.out.println("Iteration "+(k+1)+" clusters - done");
			
		}
		
		for(i=0;i<l;++i)
		{
			num=0.0;
			m=0;
			for(j=0;j<ccount;++j)
			{
				if(prob[i][j]>num)
				{
					num=prob[i][j];
					m=j;
				}
			}
			for(j=0;j<4;++j)
			{
				array[i/w][i%w][j]=clusters[m][j];
			}
		}
		System.out.println("Create image - done");
		
		createImage(array);
		
		input.close();
	}
}
