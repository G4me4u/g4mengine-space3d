package com.g4mesoft;

import com.g4mesoft.graphic.filter.IPixelFilter;
import com.g4mesoft.graphics3d.PixelRenderer3D;

public class GlowOverlayFilter implements IPixelFilter {

	private final PixelRenderer3D glowRenderer;
	
	public GlowOverlayFilter(PixelRenderer3D glowRenderer) {
		this.glowRenderer = glowRenderer;
	}

	@Override
	public void filterPixels(int[] pixels, int offset, int width, int height, int stride) {
		int[] glowPixels = glowRenderer.getPixelBuffer();
		int gw = glowRenderer.getWidth();
		int gh = glowRenderer.getHeight();
		
		for (int y = 0; y < height; y++) {
			int index = offset + stride * y;
			int gy = y * gh / height;
			for (int x = 0; x < width; x++) {
				int pixel = pixels[index];
				int r = (pixel >>> 16) & 0xFF;
				int g = (pixel >>>  8) & 0xFF;
				int b = (pixel >>>  0) & 0xFF;
				
				int gx = x * gw / width;
		
				int s = 2;
				
				int glowPixel = glowPixels[gx + gy * gw];
				r += ((glowPixel >>> 16) & 0xFF) << s;
				g += ((glowPixel >>>  8) & 0xFF) << s;
				b += ((glowPixel >>>  0) & 0xFF) << s;
				
				if (r > 0xFF)
					r = 0xFF;
				if (g > 0xFF)
					g = 0xFF;
				if (b > 0xFF)
					b = 0xFF;
				
				pixels[index] = (r << 16) | (g << 8) | b;
				
				index++;
			}
		}
	}
}
