/**
 * Copyright (c) 2012, Matt DesLauriers All rights reserved.
 *
 *	Redistribution and use in source and binary forms, with or without
 *	modification, are permitted provided that the following conditions are met:
 *
 *	* Redistributions of source code must retain the above copyright notice, this
 *	  list of conditions and the following disclaimer.
 *
 *	* Redistributions in binary
 *	  form must reproduce the above copyright notice, this list of conditions and
 *	  the following disclaimer in the documentation and/or other materials provided
 *	  with the distribution.
 *
 *	* Neither the name of the Matt DesLauriers nor the names
 *	  of his contributors may be used to endorse or promote products derived from
 *	  this software without specific prior written permission.
 *
 *	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *	AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *	IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *	ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 *	LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *	CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *	SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *	INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *	CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *	ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *	POSSIBILITY OF SUCH DAMAGE.
 */
package OpenGLPackage;
import MapKernel.MapWizard;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import OpenGLPackage.mdesl.graphics.Color;
import OpenGLPackage.mdesl.graphics.SpriteBatch;
import OpenGLPackage.mdesl.graphics.Texture;
import OpenGLPackage.mdesl.graphics.TextureRegion;
import OpenGLPackage.mdesl.graphics.glutils.ShaderProgram;
import OpenGLPackage.mdesl.graphics.text.BitmapFont; 

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL41;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;


public class OpenGL_Basic_Screen extends SimpleGame implements Runnable {
	public static boolean Opened=false;
	@Override
	public void run() {
		try{
			Opened=true;
			Game game = new OpenGL_Basic_Screen();
			game.setDisplayMode(800, 600, false);
			game.start();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			Opened=false;
		}
	}
	public static void OpenGL_main(String[] args){
		try{
			Game game = new OpenGL_Basic_Screen();
			game.setDisplayMode(800, 600, false);
			game.start();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public static void OpenGL_Async_main(String[] args){
		if(Opened) return;
		new Thread(new OpenGL_Basic_Screen()).start();
	}

	Texture tex, tex2, tex3;
	TextureRegion tile;
	SpriteBatch batch;
	Texture fontTex;
	TextureRegion rect;
	
	float panX, panY, rot, zoom=1f;
	BitmapFont font;
	final float MOVE_SPEED = 10f;
	final float ZOOM_SPEED = 0.025f;
	final float ROT_SPEED = 0.05f;
	
	URL ResourcePath(String path){
		try{
		String str=(new File(MapKernel.GeoCityInfo_main.Append_Folder_Prefix(path))).getAbsolutePath();
		return new URL("file:/"+str);
		}catch(Exception ex){
			return null;
		}
	}
	protected void create() throws LWJGLException {
		super.create();
		
		//Load some textures
		try {
			//URL a=new URL("file:/C:/Java_Eclipse/GISViewer/./res/null.png");
			tex3 = new Texture(ResourcePath("res/null.png"), Texture.NEAREST);
			tex = new Texture(ResourcePath("res/tiles.png"), Texture.NEAREST);
			tex2 = new Texture(ResourcePath("res/ptsans_00.png"));
			tile = new TextureRegion(tex3, 128, 64, 64, 64);
			
			font = new BitmapFont(ResourcePath("res/ptsans.fnt"), ResourcePath("res/ptsans_00.png"));
			
			fontTex = new Texture(ResourcePath("res/ptsans_00.png"), Texture.NEAREST);
			
			//in Photoshop, we included a small white box at the bottom right of our font sheet
			//we will use this to draw lines and rectangles within the same batch as our text
			rect = new TextureRegion(fontTex, fontTex.getWidth()-2, fontTex.getHeight()-2, 1, 1);
			RecoveryScreen();
		} catch (IOException e) {
			// ... do something here ...
			Sys.alert("Error", "Could not decode images!");
			e.printStackTrace();
			System.exit(0);
		}
		glClearColor(0.5f, .5f, .5f, 1f);
		//create our sprite batch
		batch = new SpriteBatch();
	}
	
	void drawPoint(float x, float y, float thickness){
		x-=thickness/2;
		y-=thickness/2;
		drawRect(x,y,thickness,thickness,1);
	}
	
	void drawRect(float x, float y, float width, float height, float thickness) {
		x=x-(float)OriginalScreenX;
		y=(float)OriginalScreenY-y-height;
		batch.draw(rect, x, y, width, thickness);
		batch.draw(rect, x, y, thickness, height);
		batch.draw(rect, x, y+height-thickness, width, thickness);
		batch.draw(rect, x+width-thickness, y, thickness, height);
	}
	
	void drawLine(float x1, float y1, float x2, float y2, float thickness) {
		double dx_cos=-(y2-y1)/Math.sqrt((y2-y1)*(y2-y1)+(x2-x1)*(x2-x1));
		double dy_sin=(x2-x1)/Math.sqrt((y2-y1)*(y2-y1)+(x2-x1)*(x2-x1));
		x1+=dx_cos*thickness/2;
		x2+=dx_cos*thickness/2;
		y1+=dy_sin*thickness/2;
		y2+=dy_sin*thickness/2;
		x1=x1-(float)OriginalScreenX;
		x2=x2-(float)OriginalScreenX;
		y1=(float)OriginalScreenY-y1;
		y2=(float)OriginalScreenY-y2;
		float dx = x2-x1;
		float dy = y2-y1;
		float dist = (float)Math.sqrt(dx*dx + dy*dy);
		float rad = (float)Math.atan2(dy, dx);
		batch.draw(rect, x1, y1, dist, thickness, 0, 0, rad); 
	}
	
	protected void mousePressed(int x, int y, int button) {
		
	}

	protected void mouseWheelChanged(int delta) {
		
	}
	public static double Screen_X0=0;
	public static double Screen_Y0=0;
	public static double Screen_X1=0;
	public static double Screen_Y1=0;
	public static double Screen_Zoom=1;
	public float GetScreenX(double x){
		return (float)(Screen_Zoom*(x-Screen_X0));
	}
	public float GetScreenY(double y){
		return (float)(Screen_Zoom*(y-Screen_Y0));
	}
	public int GetOpenGLLineThickness(String info){
		try{
			if(info==null) return 1;
			int p1;
			if((p1 = info.indexOf("[LineWidth:")) != -1)
				return Integer.parseInt(info.substring(p1 + 11,info.indexOf(']', p1 + 11)));
			else return 1;
		}catch(Exception ex){
			System.err.println("GetVisualLineStroke_Err");
			ex.printStackTrace();
			return 1;
		}
	}
	public int GetOpenGLPointThickness(String info){
		try {
			if (info == null)
				return 4;
			int p1;
			if ((p1 = info.indexOf("[PointSize:")) != -1)
				return Integer.parseInt(info.substring(p1 + 11,
						info.indexOf(']', p1 + 11)));
			else
				return 4;
		} catch (Exception ex) {
			System.err.println("GetVisualPointSize_Err");
			ex.printStackTrace();
			return 4;
		}
	}
	void drawGame() {
		//get the instance of the view matrix for our batch
		Matrix4f view = batch.getViewMatrix();
		
		//reset the matrix to identity, i.e. "no camera transform"
		view.setIdentity();
		
		//scale the view
		if (zoom != 1f) {
			view.scale(new Vector3f(zoom, zoom, 1f));
		}
		
		//pan the camera by translating the view matrix
		view.translate(new Vector2f(panX, panY));
		
		//after translation, we can rotate...
		if (rot!=0f) {
			//we want to rotate by a center origin point, so first we translate
			view.translate(new Vector2f(Display.getWidth()/2, Display.getHeight()/2));
			
			//then we rotate
			view.rotate(rot, new Vector3f(0, 0, 1));
			
			//then we translate back
			view.translate(new Vector2f(-Display.getWidth()/2, -Display.getHeight()/2));
		}
		
		//apply other transformations here...
		
		//update the new view matrix
		batch.updateUniforms();
		
		GL11.glClearColor(0f, 0f, 0f, 1.0f);
		if (MapWizard.SingleItem.IsAllElementInvisible) return;
		//start the sprite batch
		batch.begin();

		//draw a tile from our sprite sheet
		//batch.draw(tile, 10, 10);
		
		
		/*
		for(int dx=0; dx<=10000; dx+=20)
			for(int dy=0; dy<=10000; dy+=20){
				batch.draw(tile, 10+dx, 100+dy, 9, 18); //we can stretch it with a new width/height
			}
		*/
		
		//we can also draw a region of a Texture on the fly like so:
		//batch.drawRegion(tex, 0, 0, 32, 32, 	  //srcX, srcY, srcWidth, srcHeight
		//					   10, 250, 32, 32);  //dstX, dstY, dstWidth, dstHeight
		
		//tint batch red
		//batch.setColor(Color.RED); 
		//batch.draw(tex2, 0, 0, Display.getWidth(), Display.getHeight());
		
		//reset color
		//--------------------------------------------------------------------------------
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(false,true)){
			try{
				Thread.sleep(10);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		};
		if (!MapWizard.SingleItem.IsAllLineInvisible)
			if (MapWizard.SingleItem.LineDatabaseFile != null) {
				int binary, choose, now, p1, p2;
				Line2D line;
				int DrawCount = 0;
				for (int i = 0; i < MapWizard.SingleItem.LineDatabase.LineNum; i++) {
					binary = MapWizard.SingleItem.LineDatabase.LineVisible[i];
					if (binary < 0)
						continue;
					if (DrawCount < MapWizard.SingleItem.VisualObjectMaxNum)
						if ((binary & MapWizard.SingleItem.Ox("1")) != 0) {
							if (((!MapWizard.SingleItem.ShowVisualFeature)&&((binary & MapWizard.SingleItem.Ox("1000")) != 0))
								||(MapWizard.SingleItem.ShowVisualFeature&&(MapWizard.SingleItem.LineDatabase.LineHint[i].indexOf("[LineVisible:]")!=-1)))
							{// For Line----------------------------
								choose = (binary >> 10) & MapWizard.SingleItem.Ox("111");
								java.awt.Color Origin=MapWizard.SingleItem.getChooseColor(choose);
								batch.setColor(Origin.getRed()/255f,Origin.getGreen()/255f,Origin.getBlue()/255f,Origin.getAlpha()/255f);
								int thickness=1;
								if (MapWizard.SingleItem.ShowVisualFeature) {
									thickness = GetOpenGLLineThickness(MapWizard.SingleItem.LineDatabase.LineHint[i]);
									Origin=MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.LineDatabase.LineHint[i],"Line");
									batch.setColor(Origin.getRed()/255f,Origin.getGreen()/255f,Origin.getBlue()/255f,Origin.getAlpha()/255f);
								}

								now = MapWizard.SingleItem.LineDatabase.LineHead[i];
								p1 = now;

								while (true) {
									p2 = MapWizard.SingleItem.LineDatabase.AllPointNext[p1];
									if (p2 == -1)
										break;
									float x1 = GetScreenX(MapWizard.SingleItem.LineDatabase.AllPointX[p1]);
									float y1 = GetScreenY(MapWizard.SingleItem.LineDatabase.AllPointY[p1]);
									float x2 = GetScreenX(MapWizard.SingleItem.LineDatabase.AllPointX[p2]);
									float y2 = GetScreenY(MapWizard.SingleItem.LineDatabase.AllPointY[p2]);
									drawLine(x1, y1, x2, y2, thickness);
									if ((MapWizard.SingleItem.ShowVisualFeature)
											&& (MapWizard.SingleItem.Screen.GetVisualArrow(MapWizard.SingleItem.LineDatabase.LineHint[i]))) {
										drawLine(GetScreenX(x2),GetScreenY(y2),
													GetScreenX(x2
															+ 0.2
															* (0.87 * (x1 - x2) - (y1 - y2) * 0.34)),
													GetScreenY(y2
															+ 0.2
															* ((y1 - y2) * 0.87 + 0.34 * (x1 - x2))),thickness);
										drawLine(GetScreenX(x2),GetScreenY(y2),
													GetScreenX(x2
															+ 0.2
															* (0.87 * (x1 - x2) + (y1 - y2) * 0.34)),
													GetScreenY(y2
															+ 0.2
															* ((y1 - y2) * 0.87 - 0.34 * (x1 - x2))),thickness);
										}
										DrawCount++;
									p1 = p2;
								}
							}
							if ( ((!MapWizard.SingleItem.ShowVisualFeature)&&((binary & MapWizard.SingleItem.Ox("100")) != 0))
									||(MapWizard.SingleItem.ShowVisualFeature&&(MapWizard.SingleItem.LineDatabase.LineHint[i].indexOf("[PointVisible:]")!=-1))
									) {// For Point-------------------------
								choose = (binary >> 7) & MapWizard.SingleItem.Ox("111");
								java.awt.Color Origin=MapWizard.SingleItem.getChooseColor(choose);
								batch.setColor(Origin.getRed()/255f,Origin.getGreen()/255f,Origin.getBlue()/255f,Origin.getAlpha()/255f);
								int thickness=1;
								if (MapWizard.SingleItem.ShowVisualFeature) {
									thickness = GetOpenGLPointThickness(MapWizard.SingleItem.LineDatabase.LineHint[i]);
									Origin=MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.LineDatabase.LineHint[i],"Point");
									batch.setColor(Origin.getRed()/255f,Origin.getGreen()/255f,Origin.getBlue()/255f,Origin.getAlpha()/255f);
								}

								now = MapWizard.SingleItem.LineDatabase.LineHead[i];
								while (now != -1) {
									float xx = GetScreenX(MapWizard.SingleItem.LineDatabase.AllPointX[now]);
									float yy = GetScreenY(MapWizard.SingleItem.LineDatabase.AllPointY[now]);
									drawPoint(xx,yy,thickness);
									DrawCount++;
									now = MapWizard.SingleItem.LineDatabase.AllPointNext[now];
								}
							}
							if(!MapWizard.SingleItem.IsAllFontInvisible)
							if (
									((!MapWizard.SingleItem.ShowVisualFeature)&&((binary & MapWizard.SingleItem.Ox("10")) != 0))
									||
									(MapWizard.SingleItem.ShowVisualFeature&&(MapWizard.SingleItem.LineDatabase.LineHint[i].indexOf("[WordVisible:]")!=-1))
								){// For Word--------------------------
								choose = (binary >> 4) & MapWizard.SingleItem.Ox("111");
								java.awt.Color Origin=MapWizard.SingleItem.getChooseColor(choose);
								batch.setColor(Origin.getRed()/255f,Origin.getGreen()/255f,Origin.getBlue()/255f,Origin.getAlpha()/255f);
								now = MapWizard.SingleItem.LineDatabase.LineHead[i];
								float xx = GetScreenX(MapWizard.SingleItem.LineDatabase.AllPointX[now]);
								float yy = GetScreenY(MapWizard.SingleItem.LineDatabase.AllPointY[now]);
								DrawCount++;
								font.drawText(batch, MapWizard.SingleItem.LineDatabase.getTitle(i), TransferFromLogicalX(xx),TransferFromLogicalY(yy));
							}
						}
				}
			}
		if (!MapWizard.SingleItem.IsAllPolygonInvisible)
			if (MapWizard.SingleItem.PolygonDatabaseFile != null) {
				int binary, choose, now, p1, p2;
				int DrawCount = 0;
				for (int i = 0; i < MapWizard.SingleItem.PolygonDatabase.PolygonNum; i++) {
					binary = MapWizard.SingleItem.PolygonDatabase.PolygonVisible[i];
					if (binary < 0)
						continue;
					if (DrawCount < MapWizard.SingleItem.VisualObjectMaxNum)
						if ((binary & MapWizard.SingleItem.Ox("1")) != 0) {
							if (
									((!MapWizard.SingleItem.ShowVisualFeature)&&((binary & MapWizard.SingleItem.Ox("1000")) != 0))
									||
									(MapWizard.SingleItem.ShowVisualFeature&&(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i].indexOf("[LineVisible:]")!=-1))
								){// For Line----------------------------
								int thickness=1;
								choose = (binary >> 10) & MapWizard.SingleItem.Ox("111");
								java.awt.Color Origin=MapWizard.SingleItem.getChooseColor(choose);
								batch.setColor(Origin.getRed()/255f,Origin.getGreen()/255f,Origin.getBlue()/255f,Origin.getAlpha()/255f);
								if (MapWizard.SingleItem.ShowVisualFeature) {
									thickness = GetOpenGLLineThickness(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i]);
									Origin = MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i],"Line");
									batch.setColor(Origin.getRed()/255f,Origin.getGreen()/255f,Origin.getBlue()/255f,Origin.getAlpha()/255f);
								}

								now = MapWizard.SingleItem.PolygonDatabase.PolygonHead[i];
								p1 = now;
								while (true) {
									p2 = MapWizard.SingleItem.PolygonDatabase.AllPointNext[p1];
									if (p2 == -1) p2 = now;
									float x1 =GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[p1]);
									float y1 =GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[p1]);
									float x2 =GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[p2]);
									float y2 =GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[p2]);
									drawLine(x1, y1, x2, y2, thickness);
										if ((MapWizard.SingleItem.ShowVisualFeature)
												&& (MapWizard.SingleItem.Screen.GetVisualArrow(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i]))) {
											drawLine(GetScreenX(x2),GetScreenY(y2),
													GetScreenX(x2
															+ 0.2
															* (0.87 * (x1 - x2) - (y1 - y2) * 0.34)),
													GetScreenY(y2
															+ 0.2
															* ((y1 - y2) * 0.87 + 0.34 * (x1 - x2))),thickness);
											drawLine(GetScreenX(x2),GetScreenY(y2),
													GetScreenX(x2
															+ 0.2
															* (0.87 * (x1 - x2) + (y1 - y2) * 0.34)),
													GetScreenY(y2
															+ 0.2
															* ((y1 - y2) * 0.87 - 0.34 * (x1 - x2))),thickness);
										}
										DrawCount++;
										p1 = p2;
										if (p1 == now) break;
									}
							}
							if (MapWizard.SingleItem.ShowVisualFeature) {//For Polygon
								float MBR_x1=1e10f;
								float MBR_y1=1e10f;
								float MBR_x2=-1e10f;
								float MBR_y2=-1e10f;
								float level_sum=0f;
								int line_num=0;
								now = MapWizard.SingleItem.PolygonDatabase.PolygonHead[i];
								p1 = now;
								while (true) {
									p2 = MapWizard.SingleItem.PolygonDatabase.AllPointNext[p1];
									if (p2 == -1) p2 = now;
									float x1 =GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[p1]);
									float y1 =GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[p1]);
									float x2 =GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[p2]);
									float y2 =GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[p2]);
									line_num++;
									level_sum+=(x2-x1)*(y2-y1);
									MBR_x1=Math.min(MBR_x1, Math.min(x1, x2));
									MBR_x2=Math.max(MBR_x2, Math.max(x1, x2));
									MBR_y1=Math.min(MBR_y1, Math.min(y1, y2));
									MBR_y2=Math.max(MBR_y2, Math.max(y1, y2));
									p1 = p2;
									if (p1 == now) break;
								}
								java.awt.Color Origin = MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i],"Polygon");
								batch.setColor(Origin.getRed()/255f,Origin.getGreen()/255f,Origin.getBlue()/255f,Origin.getAlpha()/255f);
								if(!MapWizard.SingleItem.IsAllPolygonColorInvisible)
								if(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i].indexOf("[PolygonVisible:]")!=-1)
								if((level_sum==0)&&(line_num<=5))
								{
									batch.draw(tile, TransferFromLogicalX(GetScreenX(MBR_x1)), 
											TransferFromLogicalY(GetScreenY(MBR_y2)), 
											GetScreenX(MBR_x2)-GetScreenX(MBR_x1), 
											GetScreenY(MBR_y2)-GetScreenX(MBR_y1));
								}
							}
							if (
									((!MapWizard.SingleItem.ShowVisualFeature)&&((binary & MapWizard.SingleItem.Ox("100")) != 0))
									||
									(MapWizard.SingleItem.ShowVisualFeature&&(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i].indexOf("[PointVisible:]")!=-1))
								){// For Point-------------------------
								choose = (binary >> 7) & MapWizard.SingleItem.Ox("111");
								java.awt.Color Origin=MapWizard.SingleItem.getChooseColor(choose);
								batch.setColor(Origin.getRed()/255f,Origin.getGreen()/255f,Origin.getBlue()/255f,Origin.getAlpha()/255f);
								int thickness = 1;
								if (MapWizard.SingleItem.ShowVisualFeature) {
									thickness = GetOpenGLPointThickness(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i]);
									Origin=MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i],"Point");
									batch.setColor(Origin.getRed()/255f,Origin.getGreen()/255f,Origin.getBlue()/255f,Origin.getAlpha()/255f);
								}
								now = MapWizard.SingleItem.PolygonDatabase.PolygonHead[i];
								while (now != -1) {
									float xx =GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[now]);
									float yy =GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[now]);
									drawPoint(xx,yy,thickness);
									DrawCount++;
									now = MapWizard.SingleItem.PolygonDatabase.AllPointNext[now];
								}

							}
							if(!MapWizard.SingleItem.IsAllFontInvisible)
							if (
									((!MapWizard.SingleItem.ShowVisualFeature)&&((binary & MapWizard.SingleItem.Ox("10")) != 0))
									||
									(MapWizard.SingleItem.ShowVisualFeature&&(MapWizard.SingleItem.PolygonDatabase.PolygonHint[i].indexOf("[WordVisible:]")!=-1))
								){// For Word--------------------------
								choose = (binary >> 4) & MapWizard.SingleItem.Ox("111");
								java.awt.Color Origin=MapWizard.SingleItem.getChooseColor(choose);
								batch.setColor(Origin.getRed()/255f,Origin.getGreen()/255f,Origin.getBlue()/255f,Origin.getAlpha()/255f);
								now = MapWizard.SingleItem.PolygonDatabase.PolygonHead[i];
								float xx =GetScreenX(MapWizard.SingleItem.PolygonDatabase.AllPointX[now]);
								float yy =GetScreenY(MapWizard.SingleItem.PolygonDatabase.AllPointY[now]);
								DrawCount++;
								font.drawText(batch, MapWizard.SingleItem.PolygonDatabase.getTitle(i), TransferFromLogicalX(xx),TransferFromLogicalY(yy));
							}
						}
				}
			}
		if ((!MapWizard.SingleItem.IsAllPointInvisible) && (!MapWizard.SingleItem.IsShowAlphaDistribution))
			if (MapWizard.SingleItem.PointDatabaseFile != null) {
				int binary, choose, now, p1, p2;
				Point2D Point;
				int DrawCount = 0;
				for (int i = 0; i < MapWizard.SingleItem.PointDatabase.PointNum; i++) {
					binary = MapWizard.SingleItem.PointDatabase.PointVisible[i];
					if (DrawCount > MapWizard.SingleItem.VisualObjectMaxNum) break;
					if ((binary & MapWizard.SingleItem.Ox("1")) != 0) {
						if (binary < 0) continue;
						if (
								((!MapWizard.SingleItem.ShowVisualFeature)&&((binary & MapWizard.SingleItem.Ox("100")) != 0))
								||
								(MapWizard.SingleItem.ShowVisualFeature&&(MapWizard.SingleItem.PointDatabase.PointHint[i].indexOf("[PointVisible:]")!=-1))
							){// For Point-------------------------
							choose = (binary >> 7) & MapWizard.SingleItem.Ox("111");
							java.awt.Color Origin=MapWizard.SingleItem.getChooseColor(choose);
							batch.setColor(Origin.getRed()/255f,Origin.getGreen()/255f,Origin.getBlue()/255f,Origin.getAlpha()/255f);
							
							int thickness = 1;
							if (MapWizard.SingleItem.ShowVisualFeature) {
								thickness = GetOpenGLPointThickness(MapWizard.SingleItem.PointDatabase.PointHint[i]);
								Origin=MapWizard.SingleItem.Screen.GetVisualColor(MapWizard.SingleItem.PointDatabase.PointHint[i],"Point");
								batch.setColor(Origin.getRed()/255f,Origin.getGreen()/255f,Origin.getBlue()/255f,Origin.getAlpha()/255f);
							}
							float xx =GetScreenX(MapWizard.SingleItem.PointDatabase.AllPointX[i]);
							float yy =GetScreenY(MapWizard.SingleItem.PointDatabase.AllPointY[i]);
							if (MapWizard.SingleItem.IsEngravePointShape) thickness=1;
							drawPoint(xx,yy,thickness);
							DrawCount++;
						}
						if(!MapWizard.SingleItem.IsAllFontInvisible)
						if (
								((!MapWizard.SingleItem.ShowVisualFeature)&&((binary & MapWizard.SingleItem.Ox("10")) != 0))
								||
								(MapWizard.SingleItem.ShowVisualFeature&&(MapWizard.SingleItem.PointDatabase.PointHint[i].indexOf("[WordVisible:]")!=-1))
								) {// For Word--------------------------
							choose = (binary >> 4) & MapWizard.SingleItem.Ox("111");
							java.awt.Color Origin=MapWizard.SingleItem.getChooseColor(choose);
							batch.setColor(Origin.getRed()/255f,Origin.getGreen()/255f,Origin.getBlue()/255f,Origin.getAlpha()/255f);
							float xx = GetScreenX(MapWizard.SingleItem.PointDatabase.AllPointX[i]);
							float yy = GetScreenY(MapWizard.SingleItem.PointDatabase.AllPointY[i]);
							font.drawText(batch, MapWizard.SingleItem.PointDatabase.getTitle(i), TransferFromLogicalX(xx),TransferFromLogicalY(yy));
						}
					}
				}
			}
		while(!MapKernel.MapWizard.SingleItem.Set_DB_Read_Write_Lock(false, false)){
			try{
				Thread.sleep(10);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		};
		/*
		batch.setColor(Color.WHITE);
		
		drawRect(137,128,31,282,2);//Yard61
		drawLine(137,165,137+31,165,2);
		drawRect(189,128,31,282,2);//Yard60
		drawLine(189,128+75,189+31,128+75,2);
		drawRect(227,128,31,282,2);//Yard59
		drawLine(227,165,227+31,165,2);
		drawRect(262,128,31,282,2);//Yard58
		drawLine(262,165,262+31,165,2);
		drawRect(299,128,31,282,2);//Yard57
		drawLine(299,135,299+31,135,2);
		drawRect(351,128,31,282,2);//Yard56
		drawLine(351,135,351+31,135,2);
		drawRect(389,128,31,282,2);//Yard55
		drawLine(389,165,389+31,165,2);
		drawRect(424,128,31,282,2);//Yard54
		drawLine(424,165,424+31,165,2);
		drawRect(461,128,31,334,2);//Yard53
		drawLine(461,135,461+31,135,2);
		drawRect(513,128,31,370,2);//Yard52
		drawLine(513,135,513+31,135,2);
		drawRect(551,128,31,374,2);//Yard51
		drawLine(551,165,551+31,165,2);
		drawRect(586,128,31,374,2);//Yard50
		drawLine(586,165,586+31,165,2);
		drawRect(623,128,31,374,2);//Yard49
		drawLine(623,165,623+31,165,2);
		drawRect(658,128,31,374,2);//Yard48
		drawLine(658,165,658+31,165,2);
		drawRect(696,128,31,374,2);//Yard47
		drawLine(696,135,696+31,135,2);
		
		drawRect(748,128,31,374,2);//Yard46
		drawLine(748,135,748+31,135,2);
		drawRect(783,128,31,374,2);//Yard45
		drawLine(783,165,783+31,165,2);
		drawRect(821,128,31,374,2);//Yard44
		drawLine(821,165,821+31,165,2);
		drawRect(856,128,31,374,2);//Yard43
		drawLine(856,165,856+31,165,2);
		drawRect(894,128,31,374,2);//Yard42
		drawLine(894,165,894+31,165,2);
		drawRect(929,128,31,374,2);//Yard41
		drawLine(929,165,929+31,165,2);
		drawRect(966,128,31,374,2);//Yard40
		drawLine(966,165,966+31,165,2);
		
		drawRect(1018,128,31,374,2);//Yard39
		drawLine(1018,135,1018+31,135,2);
		drawRect(1056,128,31,374,2);//Yard38
		drawLine(1056,135,1056+31,135,2);
		drawRect(1091,128,31,374,2);//Yard37
		drawLine(1091,165,1091+31,165,2);
		drawRect(1128,128,31,374,2);//Yard36
		drawLine(1128,165,1128+31,165,2);
		drawRect(1163,128,31,374,2);//Yard35
		drawLine(1163,165,1163+31,165,2);
		drawRect(1201,128,31,374,2);//Yard34
		drawLine(1201,165,1201+31,165,2);
		
		drawRect(1236,128,31,374,2);//Yard33
		drawLine(1236,165,1236+31,165,2);
		drawRect(1273,128,31,374,2);//Yard32
		drawLine(1273,165,1273+31,165,2);
		drawRect(1325,128,31,384,2);//Yard31
		drawLine(1325,135,1325+31,135,2);
		drawRect(1363,128,31,404,2);//Yard30
		drawLine(1363,135,1363+31,135,2);
		drawRect(1398,128,31,424,2);//Yard29
		drawLine(1398,165,1398+31,165,2);
		drawRect(1435,128,31,424,2);//Yard28
		drawLine(1435,165,1435+31,165,2);
		
		drawRect(1470,128,31,424,2);//Yard27
		drawLine(1470,165,1470+31,165,2);
		drawRect(1508,128,31,424,2);//Yard26
		drawLine(1508,165,1508+31,165,2);
		drawRect(1543,128,31,424,2);//Yard25
		drawLine(1543,165,1543+31,165,2);
		drawRect(1580,128,31,424,2);//Yard24
		drawLine(1580,165,1580+31,165,2);
		drawRect(1632,128,31,424,2);//Yard23
		drawLine(1632,135,1632+31,135,2);
		
		drawRect(1670,128,31,424,2);//Yard22
		drawLine(1670,135,1670+31,135,2);
		drawRect(1705,128,31,424,2);//Yard21
		drawLine(1705,165,1705+31,165,2);
		drawRect(1742,128,31,424,2);//Yard20
		drawLine(1742,165,1742+31,165,2);
		drawRect(1777,128,31,424,2);//Yard19
		drawLine(1777,165,1777+31,165,2);
		drawRect(1815,128,31,424,2);//Yard18
		drawLine(1815,165,1815+31,165,2);
		drawRect(1850,128,31,424,2);//Yard17
		drawLine(1850,165,1850+31,165,2);
		drawRect(1887,128,31,424,2);//Yard16
		drawLine(1887,165,1887+31,165,2);
		
		drawRect(1939,128,31,424,2);//Yard15
		drawLine(1939,135,1939+31,135,2);
		drawRect(1977,128,31,424,2);//Yard14
		drawLine(1977,135,1977+31,135,2);
		drawRect(2012,128,31,424,2);//Yard13
		drawLine(2012,165,2012+31,165,2);
		drawRect(2049,128,31,424,2);//Yard12
		drawLine(2049,165,2049+31,165,2);
		drawRect(2084,128,31,424,2);//Yard11
		drawLine(2084,165,2084+31,165,2);
		
		drawRect(2122,128,31,420,2);//Yard10
		drawLine(2122,165,2122+31,165,2);
		drawRect(2174,128,31,408,2);//Yard09
		drawLine(2174,135,2174+31,135,2);
		drawRect(2211,128,31,398,2);//Yard08
		drawLine(2211,135,2211+31,135,2);
		drawRect(2246,128,31,372,2);//Yard07
		drawLine(2246,165,2246+31,165,2);
		drawRect(2284,128,31,368,2);//Yard06
		drawLine(2284,165,2284+31,165,2);
		drawRect(2319,128,31,350,2);//Yard05
		drawLine(2319,165,2319+31,165,2);
		drawRect(2356,128,31,330,2);//Yard04
		drawLine(2356,165,2356+31,165,2);
		drawRect(2392,128,31,300,2);//Yard03
		drawLine(2392,165,2392+31,165,2);
		drawRect(2428,128,31,254,2);//Yard02
		drawLine(2428,135,2428+31,135,2);
		drawRect(2480,128,31,200,2);//Yard01
		drawLine(2480,135,2480+31,135,2);
		//=================================================
		drawRect(1320,585,265,25,2);//超限箱区
		drawRect(1320,620,265,25,2);
		drawRect(1610,585,265,25,2);
		drawRect(1610,620,145,25,2);
		//=================================================
		drawRect(-10,145,25,210,2);//危险品箱区
		drawRect(20,145,25,250,2);
		drawRect(50,145,25,250,2);
		drawRect(80,145,25,250,2);
		//=================================================
		drawLine(0,0,2350,0,2);
		drawLine(2350,0,2350,70,2);
		drawLine(2350,70,2660,70,2);
		drawLine(2660,70,2660,125,2);
		drawLine(2660,125,0,125,2);
		drawLine(0,125,0,0,2);
		//=================================================
		batch.setColor(Color.BLUE);
		font.drawText(batch, "61", TransferFromLogicalX(137+7),TransferFromLogicalY(128));
		font.drawText(batch, "60", TransferFromLogicalX(189+7),TransferFromLogicalY(128));
		font.drawText(batch, "59", TransferFromLogicalX(227+7),TransferFromLogicalY(128));
		font.drawText(batch, "58", TransferFromLogicalX(262+7),TransferFromLogicalY(128));
		font.drawText(batch, "57", TransferFromLogicalX(299+7),TransferFromLogicalY(128));
		font.drawText(batch, "56", TransferFromLogicalX(351+7),TransferFromLogicalY(128));
		font.drawText(batch, "55", TransferFromLogicalX(389+7),TransferFromLogicalY(128));
		font.drawText(batch, "54", TransferFromLogicalX(424+7),TransferFromLogicalY(128));
		//=======================================================
		font.drawText(batch, "53", TransferFromLogicalX(461+7),TransferFromLogicalY(128));
		font.drawText(batch, "52", TransferFromLogicalX(513+7),TransferFromLogicalY(128));
		font.drawText(batch, "51", TransferFromLogicalX(551+7),TransferFromLogicalY(128));
		font.drawText(batch, "50", TransferFromLogicalX(586+7),TransferFromLogicalY(128));
		font.drawText(batch, "49", TransferFromLogicalX(623+7),TransferFromLogicalY(128));
		font.drawText(batch, "48", TransferFromLogicalX(658+7),TransferFromLogicalY(128));
		font.drawText(batch, "47", TransferFromLogicalX(696+7),TransferFromLogicalY(128));
		
		font.drawText(batch, "46", TransferFromLogicalX(748+7),TransferFromLogicalY(128));
		font.drawText(batch, "45", TransferFromLogicalX(783+7),TransferFromLogicalY(128));
		font.drawText(batch, "44", TransferFromLogicalX(821+7),TransferFromLogicalY(128));
		font.drawText(batch, "43", TransferFromLogicalX(856+7),TransferFromLogicalY(128));
		font.drawText(batch, "42", TransferFromLogicalX(894+7),TransferFromLogicalY(128));
		font.drawText(batch, "41", TransferFromLogicalX(929+7),TransferFromLogicalY(128));
		font.drawText(batch, "40", TransferFromLogicalX(966+7),TransferFromLogicalY(128));
		
		font.drawText(batch, "39", TransferFromLogicalX(1018+7),TransferFromLogicalY(128));
		font.drawText(batch, "38", TransferFromLogicalX(1056+7),TransferFromLogicalY(128));
		font.drawText(batch, "37", TransferFromLogicalX(1091+7),TransferFromLogicalY(128));
		font.drawText(batch, "36", TransferFromLogicalX(1128+7),TransferFromLogicalY(128));
		font.drawText(batch, "35", TransferFromLogicalX(1163+7),TransferFromLogicalY(128));
		font.drawText(batch, "34", TransferFromLogicalX(1201+7),TransferFromLogicalY(128));
		
		font.drawText(batch, "33", TransferFromLogicalX(1236+7),TransferFromLogicalY(128));
		font.drawText(batch, "32", TransferFromLogicalX(1273+7),TransferFromLogicalY(128));
		font.drawText(batch, "31", TransferFromLogicalX(1325+7),TransferFromLogicalY(128));
		font.drawText(batch, "30", TransferFromLogicalX(1363+7),TransferFromLogicalY(128));
		font.drawText(batch, "29", TransferFromLogicalX(1398+7),TransferFromLogicalY(128));
		font.drawText(batch, "28", TransferFromLogicalX(1435+7),TransferFromLogicalY(128));
		
		font.drawText(batch, "27", TransferFromLogicalX(1470+7),TransferFromLogicalY(128));
		font.drawText(batch, "26", TransferFromLogicalX(1508+7),TransferFromLogicalY(128));
		font.drawText(batch, "25", TransferFromLogicalX(1543+7),TransferFromLogicalY(128));
		font.drawText(batch, "24", TransferFromLogicalX(1580+7),TransferFromLogicalY(128));
		font.drawText(batch, "23", TransferFromLogicalX(1632+7),TransferFromLogicalY(128));
		
		font.drawText(batch, "22", TransferFromLogicalX(1670+7),TransferFromLogicalY(128));
		font.drawText(batch, "21", TransferFromLogicalX(1705+7),TransferFromLogicalY(128));
		font.drawText(batch, "20", TransferFromLogicalX(1742+7),TransferFromLogicalY(128));
		font.drawText(batch, "19", TransferFromLogicalX(1777+7),TransferFromLogicalY(128));
		font.drawText(batch, "18", TransferFromLogicalX(1815+7),TransferFromLogicalY(128));
		font.drawText(batch, "17", TransferFromLogicalX(1850+7),TransferFromLogicalY(128));
		font.drawText(batch, "16", TransferFromLogicalX(1887+7),TransferFromLogicalY(128));
		
		font.drawText(batch, "15", TransferFromLogicalX(1939+7),TransferFromLogicalY(128));
		font.drawText(batch, "14", TransferFromLogicalX(1977+7),TransferFromLogicalY(128));
		font.drawText(batch, "13", TransferFromLogicalX(2012+7),TransferFromLogicalY(128));
		font.drawText(batch, "12", TransferFromLogicalX(2049+7),TransferFromLogicalY(128));
		font.drawText(batch, "11", TransferFromLogicalX(2084+7),TransferFromLogicalY(128));
		
		font.drawText(batch, "10", TransferFromLogicalX(2122+7),TransferFromLogicalY(128));
		font.drawText(batch, "09", TransferFromLogicalX(2174+7),TransferFromLogicalY(128));
		font.drawText(batch, "08", TransferFromLogicalX(2211+7),TransferFromLogicalY(128));
		font.drawText(batch, "07", TransferFromLogicalX(2246+7),TransferFromLogicalY(128));
		font.drawText(batch, "06", TransferFromLogicalX(2284+7),TransferFromLogicalY(128));
		font.drawText(batch, "05", TransferFromLogicalX(2319+7),TransferFromLogicalY(128));
		font.drawText(batch, "04", TransferFromLogicalX(2356+7),TransferFromLogicalY(128));
		font.drawText(batch, "03", TransferFromLogicalX(2392+7),TransferFromLogicalY(128));
		font.drawText(batch, "02", TransferFromLogicalX(2428+7),TransferFromLogicalY(128));
		font.drawText(batch, "01", TransferFromLogicalX(2480+7),TransferFromLogicalY(128));
		//===========================================================================
		batch.setColor(Color.BLACK);
		drawLine(2656,128,2656, 268,2);
		drawLine(2656,268,2520,329,2);
		drawLine(2520,329,2485,385,2);
		drawLine(2485,385,2458,423,2);
		drawLine(2458,423,2408,458,2);
		drawLine(2408,458,2374,478,2);
		drawLine(2374,478,2279,520,2);
		drawLine(2279,520,2124,564,2);
		drawLine(2124,564,2100,567,2);
		drawLine(2100,567,1401,567,2);
		drawLine(1401,567,1351,554,2);
		drawLine(1351,554,1301,525,2);
		drawLine(1301,525,1266,515,2);
		drawLine(1266,515,506,515,2);
		drawLine(506,515,410,446,2);
		drawLine(410,446,124,446,2);
		drawLine(124,446,124,135,2);
		drawLine(124,135,-25,135,2);
		*/
		//finish the sprite batch and push the tiles to the GPU
		batch.end();        
	}
	
	void drawHUD() {
		//draw the text with identity matrix, i.e. no camera transformation
		batch.getViewMatrix().setIdentity();
		batch.updateUniforms();
		
		batch.begin();
		// ... render any hud elements
		// ScreenBottomHint----------------------------------------------------
		batch.setColor(Color.RED);
		if (MapWizard.SingleItem.Screen.IsTextArea1Visible) {
			font.drawText(batch, MapWizard.SingleItem.Screen.TextArea1Content, 10 ,Display.getHeight()-20);
		}
		if (MapWizard.SingleItem.Screen.IsTextArea2Visible) {
			font.drawText(batch, MapWizard.SingleItem.Screen.TextArea2Content, Display.getWidth()/2+10,Display.getHeight()-20);
		}
		//--------------------------------------------------------------------------------
		batch.end();
	}
	public final double OriginalScreenX=0;
	public final double OriginalScreenY=500;
	public double ScreenX=OriginalScreenX;
	public double ScreenY=OriginalScreenY;
	private int LastPressedLogicalX=(int)1e9;
	private int LastPressedLogicalY=(int)1e9;
	protected void render() throws LWJGLException {
		super.render();		
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
		{
			panX += 4*MOVE_SPEED;
			ScreenX -= 4*MOVE_SPEED;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
		{
			panX -= 4*MOVE_SPEED;
			ScreenX += 4*MOVE_SPEED;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
		{
			panY -= 4*MOVE_SPEED;
			ScreenY -= 4*MOVE_SPEED;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_UP))
		{
			panY += 4*MOVE_SPEED;
			ScreenY += 4*MOVE_SPEED;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_Z))
		{
			zoom += 2*ZOOM_SPEED;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_X))
		{
			zoom -= 2*ZOOM_SPEED;
		}

		if(Mouse.isButtonDown(0)){
			double DragX=Mouse.getEventDX();
			double DragY=Mouse.getEventDY();
			if((Math.abs(DragX)+Math.abs(DragY)>10)){
				panX += DragX/zoom;
				ScreenX -= DragX/zoom;
				panY -= DragY/zoom;
				ScreenY -= DragY/zoom;
			}
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_C)||Keyboard.isKeyDown(Keyboard.KEY_V)||(Mouse.getDWheel()!=0)){
			double Old_zoom=zoom;
			if(Keyboard.isKeyDown(Keyboard.KEY_C)) zoom += 2*ZOOM_SPEED;
			else if(Keyboard.isKeyDown(Keyboard.KEY_V)) zoom-=2*ZOOM_SPEED;
			else zoom+=Mouse.getEventDWheel()*ZOOM_SPEED/20;
			double Zoomdx=(GetLogicalX()-ScreenX)-(GetLogicalX()-ScreenX)*Old_zoom/zoom;
			double Zoomdy=(ScreenY-GetLogicalY())-(ScreenY-GetLogicalY())*Old_zoom/zoom;
			ScreenX+=Zoomdx;
			ScreenY-=Zoomdy; 
			panX-=Zoomdx;
			panY-=Zoomdy;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_R)){
			RecoveryScreen();
		}
		
		zoom = Math.max(0.01f, zoom);
		
		if(org.lwjgl.input.Mouse.getEventButton()>0){
			int NowLogicalX=GetLogicalX();
			int NowLogicalY=GetLogicalY();
			Display.setTitle(NowLogicalX+"/"+NowLogicalY);
			if(MapKernel.MapWizard.SingleItem.BehaviorListener!=null){
				if(Math.abs(NowLogicalX-LastPressedLogicalX)+Math.abs(NowLogicalY-LastPressedLogicalY)!=0)
				MapKernel.MapWizard.SingleItem.BehaviorListener.MousePressedListener(NowLogicalX,NowLogicalY);
			}
		}
		
		/*
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			rot -= ROT_SPEED;
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			rot += ROT_SPEED;
		*/
		
		drawGame();
		
		drawHUD();
	}
	
	public void RecoveryScreen(){
		ScreenX=OriginalScreenX;
		ScreenY=OriginalScreenY;
		panX=0;
		panY=0;
        zoom=0.5f;
        Display.setTitle("OpenGL 2D Screen");
	}
	public int GetLogicalX(){
		return (int)(ScreenX+(org.lwjgl.input.Mouse.getX())/zoom);
	}
	public int GetLogicalY(){
		return (int)(ScreenY-(Display.getHeight()-org.lwjgl.input.Mouse.getY())/zoom);
	}
	public int TransferFromLogicalX(double x){
		return (int)(x-OriginalScreenX);
	}
	public int TransferFromLogicalY(double y){
		return (int)(OriginalScreenY-y);
	}
	protected void resize() throws LWJGLException {
		super.resize();
		batch.resize(Display.getWidth(), Display.getHeight());
	}
	
}