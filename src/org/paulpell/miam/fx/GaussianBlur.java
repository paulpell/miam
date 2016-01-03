package org.paulpell.miam.fx;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;

import org.paulpell.miam.logic.Log;

public class GaussianBlur
{

	private GaussianBlur() {
	}
	
	// working on a 3x3 basis

	/*private static double[][] coeff3 = new double[][]
			{
					{0.025, 0.050, 0.025},
					{0.050, 0.700, 0.050},
					{0.025, 0.050, 0.025}
			};*/
	private static float[] coeff3 = new float[]
			{
					0.045f, 0.080f, 0.045f,
					0.080f, 0.500f, 0.080f,
					0.045f, 0.080f, 0.045f
			};
			
	private static float[] coeff3_strong = new float[]
			{
					0.1f, 0.1f, 0.1f,
					0.1f, 0.2f, 0.1f,
					0.1f, 0.1f, 0.1f
			};

	private static double[] filter3 (double[] pixels, double[][] coeff)
	{
		int l = pixels.length;
		int n_bands = l / 9;
		
		double[] pix = new double[n_bands];
		for (int b=0; b<n_bands; ++b)
			pix[b] = 0;

		for (int j=0; j<3; ++j)
		{
			for (int i=0; i<3; ++i)
			{
				final double c = coeff[i][j];
				for (int b=0; b<n_bands; ++b)
					pix[b] +=  c * pixels[9*j + 3*i + b];
			}
		}
		
		return pix;
	}
	
	private static BufferedImage blurImg_old (BufferedImage img, double[][] coeff)
	{
		// prepare the new image
		int width = img.getWidth();
		int height = img.getHeight();
		int type = img.getType();
		BufferedImage img_out = new BufferedImage(width, height, type);
		WritableRaster raster_out = img_out.getRaster();
		img.copyData(raster_out);
		
		WritableRaster raster_in = img.getRaster();
		double[] pix_vals = null;
		int row =0, col=0;
		try
		{
			for ( row = 1; row < height - 1; ++row )
			{
				for ( col = 1; col < width - 1; ++col )
				{
					 pix_vals = raster_in.getPixels(col-1, row-1, 3, 3, (double[])null);
					 double[] filtered = filter3(pix_vals, coeff);
					 raster_out.setPixel(col, row, filtered);
				}
			}
		}
		catch (Exception e)
		{
			Log.logErr("Exception in filtering;");
			Log.logException(e);
		}
		
		img_out.setData(raster_out);
		return img_out;
	}
	
	private static BufferedImage blurImg (BufferedImage img, float[] coeff)
	{
		// prepare the new image
		int width = img.getWidth();
		int height = img.getHeight();
		int type = img.getType();
		BufferedImage img_out = new BufferedImage(width, height, type);
		// filter
		Kernel k = new Kernel(3, 3, coeff);
		BufferedImageOp op = new ConvolveOp(k);
		op.filter(img, img_out);
		
		return img_out;
	}
	
	
	public static BufferedImage blurGameImg (BufferedImage img)
	{
		return blurImg (img, coeff3);
	}
	
	public static BufferedImage blurImgStrongly (BufferedImage img)
	{
		return blurImg (img, coeff3_strong);
	}
}
