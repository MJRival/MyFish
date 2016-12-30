package com.example.object;

import java.util.Random;
import com.example.mybeatplane.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
/*中型鱼的类*/
public class MiddleFish extends EnemyFish{
	private static int currentCount = 0;	 //	对象当前的数量
	private Bitmap middleFish;// 对象图片
	public static int sumCount = 4;	 	 	 //	对象总的数量
	public MiddleFish(Resources resources) {
		super(resources);
		// TODO Auto-generated constructor stub
		this.score = 2000;		// 为对象设置分数
	}
	//初始化数据
	@Override
	public void initial(int arg0,float arg1,float arg2){
		isAlive = true;
		Random ran = new Random();
		speed = ran.nextInt(2) + 6 * arg0;
		object_x = -object_width * (currentCount*2 + 1);
		object_y = ran.nextInt((int)(screen_height - object_height));
		currentCount++;
		if(currentCount >= sumCount){
			currentCount = 0;
		}
	}
	// 初始化图片资源
	@Override
	public void initBitmap() {
		// TODO Auto-generated method stub
		middleFish = BitmapFactory.decodeResource(resources, R.drawable.middlefish);
		object_width = middleFish.getWidth();			//获得每一帧位图的宽
		object_height = middleFish.getHeight();		//获得每一帧位图的高
	}
	// 对象的绘图函数
	@Override
	public void drawSelf(Canvas canvas) {
		// TODO Auto-generated method stub
		if(isAlive){
			if(!isExplosion){
				if(isVisible){
					canvas.save();
					canvas.clipRect(object_x,object_y,object_x + object_width,object_y + object_height);
					canvas.drawBitmap(middleFish, object_x, object_y,paint);
					canvas.restore();
				}
				logic();
			}
			else{
				canvas.save();
				canvas.clipRect(object_x,object_y,object_x + object_width,object_y + object_height);
				canvas.drawBitmap(middleFish, object_x, object_y,paint);
				canvas.restore();
			}
		}
	}
	// 释放资源
	@Override
	public void release() {
		// TODO Auto-generated method stub
		if(!middleFish.isRecycled()){
			middleFish.recycle();
		}
	}
}
