package Algorithm;

import java.awt.Color;

public class HSLtoRGB {
	static double Hue2RGB(double v1, double v2, double vH)
	{
	    if (vH < 0) vH += 1;
	    if (vH > 1) vH -= 1;
	    if (6.0 * vH < 1) return v1 + (v2 - v1) * 6.0 * vH;
	    if (2.0 * vH < 1) return v2;
	    if (3.0 * vH < 2) return v1 + (v2 - v1) * ((2.0 / 3.0) - vH) * 6.0;
	    return (v1);
	}
	public static Color HSLtoRGBColor(double H,double S,double L,double Alpha){
	    double R,G,B;
	    double var_1, var_2;
	    if (S == 0)                       //HSL values = 0 ÷ 1
	    {
	        R = L * 255.0;                   //RGB results = 0 ÷ 255
	        G = L * 255.0;
	        B = L * 255.0;
	    }
	    else
	    {
	        if (L < 0.5) var_2 = L * (1 + S);
	        else         var_2 = (L + S) - (S * L);

	        var_1 = 2.0 * L - var_2;

	        R = 255.0 * Hue2RGB(var_1, var_2, H + (1.0 / 3.0));
	        G = 255.0 * Hue2RGB(var_1, var_2, H);
	        B = 255.0 * Hue2RGB(var_1, var_2, H - (1.0 / 3.0));
	    }
	    
	    return (new Color((int)R,(int)G,(int)B,(int)(255*Alpha)));
	}


}
