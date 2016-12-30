package com.example.view;

import java.util.ArrayList;
import java.util.List;

import com.example.constant.ConstantUtil;
import com.example.factory.GameObjectFactory;
import com.example.mybeatplane.R;
import com.example.object.BigFish;
import com.example.object.EnemyFish;
import com.example.object.GameObject;
import com.example.object.MiddleFish;
import com.example.object.MissileGoods;
import com.example.object.MyFish;
import com.example.object.SmallFish;
import com.example.sounds.GameSoundPool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/*游戏进行的主界面*/
public class MainView extends BaseView{
	private int missileCount; 		// 导弹的数量
	private int middleFishScore;	// 中型鱼的积分
	private int bigFishScore;		// 大型鱼的积分
	private int missileScore;		// 导弹的积分
	private int sumScore;           // 游戏总得分
	private int myScore;
	private int speedTime;			// 游戏速度的倍数
	private float bg_x;				// 图片的坐标
	private float play_bt_w;
	private float play_bt_h;
	private float missile_bt_y;
	private boolean isPlay;			// 标记游戏运行状态
	private boolean isTouchFish;	// 判断玩家是否按下屏幕
	private Bitmap background; 		// 背景图片
	private Bitmap playButton; 		// 开始/暂停游戏的按钮图片
	private Bitmap missile_bt;		// 导弹按钮图标
	private MyFish myFish;		// 玩家的鱼
	private List<EnemyFish> enemyFishes;
	private MissileGoods missileGoods;
	private GameObjectFactory factory;
	public MainView(Context context,GameSoundPool sounds) {
		super(context,sounds);
		// TODO Auto-generated constructor stub
		isPlay = true;
		speedTime = 1;
		thread = new Thread(this);
		factory = new GameObjectFactory();						  //工厂类
		enemyFishes = new ArrayList<EnemyFish>();
		myFish = (MyFish) factory.createMyFish(getResources());//生产玩家的鱼
		myFish.setMainView(this);
		for(int i = 0; i < SmallFish.sumCount; i++){
			//生产小型鱼
			SmallFish smallFish = (SmallFish) factory.createSmallFish(getResources());
			enemyFishes.add(smallFish);
		}
		for(int i = 0; i < MiddleFish.sumCount; i++){
			//生产中型鱼
			MiddleFish middlePlane = (MiddleFish) factory.createMiddleFish(getResources());
			enemyFishes.add(middlePlane);
		}
		for(int i = 0; i < BigFish.sumCount; i++){
			//生产大型鱼
			BigFish bigPlane = (BigFish) factory.createBigFish(getResources());
			enemyFishes.add(bigPlane);
		}
		//生产导弹物品
		missileGoods = (MissileGoods) factory.createMissileGoods(getResources());
	}
	// 视图改变的方法
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		super.surfaceChanged(arg0, arg1, arg2, arg3);
	}
	// 视图创建的方法
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		super.surfaceCreated(arg0);
		initBitmap(); // 初始化图片资源
		for(GameObject obj:enemyFishes){
			obj.setScreenWH(screen_width,screen_height);
		}
		missileGoods.setScreenWH(screen_width, screen_height);
		myFish.setScreenWH(screen_width,screen_height);
		myFish.setAlive(true);
		if(thread.isAlive()){
			thread.start();
		}
		else{
			thread = new Thread(this);
			thread.start();
		}
	}
	// 视图销毁的方法
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		super.surfaceDestroyed(arg0);
		release();
	}
	// 响应触屏事件的方法
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP){
			isTouchFish = false;
			return true;
		}
		else if(event.getAction() == MotionEvent.ACTION_DOWN){
			float x = event.getX();
			float y = event.getY();
			if(x > 10 && x < 10 + play_bt_w && y > 10 && y < 10 + play_bt_h){
				if(isPlay){
					isPlay = false;
				}
				else{
					isPlay = true;
					synchronized(thread){
						thread.notify();
					}
				}
				return true;
			}
			//判断玩家鱼是否被按下
			else if(x > myFish.getObject_x() && x < myFish.getObject_x() + myFish.getObject_width()
					&& y > myFish.getObject_y() && y < myFish.getObject_y() + myFish.getObject_height()){
				if(isPlay){
					isTouchFish = true;
				}
				return true;
			}
			//判断导弹按钮是否被按下
			else if(x > 10 && x < 10 + missile_bt.getWidth()
					&& y > missile_bt_y && y < missile_bt_y + missile_bt.getHeight()){
				if(missileCount > 0){
					missileCount--;
					sounds.playSound(5, 0);
					for(EnemyFish pobj:enemyFishes){
						if(pobj.isCanCollide()){
							pobj.setAlive(false);
							if(pobj.isExplosion()){
								addGameScore(pobj.getScore());// 获得分数
							}
						}
					}
				}
				return true;
			}
		}
		//响应手指在屏幕移动的事件
		else if(event.getAction() == MotionEvent.ACTION_MOVE && event.getPointerCount() == 1){
			//判断触摸点是否为玩家的鱼
			if(isTouchFish){
				float x = event.getX();
				float y = event.getY();
				if(x > myFish.getMiddle_x() + 20){
					if(myFish.getMiddle_x() + myFish.getSpeed() <= screen_width){
						myFish.setMiddle_x(myFish.getMiddle_x() + myFish.getSpeed());
					}
				}
				else if(x < myFish.getMiddle_x() - 20){
					if(myFish.getMiddle_x() - myFish.getSpeed() >= 0){
						myFish.setMiddle_x(myFish.getMiddle_x() - myFish.getSpeed());
					}
				}
				if(y > myFish.getMiddle_y() + 20){
					if(myFish.getMiddle_y() + myFish.getSpeed() <= screen_height){
						myFish.setMiddle_y(myFish.getMiddle_y() + myFish.getSpeed());
					}
				}
				else if(y < myFish.getMiddle_y() - 20){
					if(myFish.getMiddle_y() - myFish.getSpeed() >= 0){
						myFish.setMiddle_y(myFish.getMiddle_y() - myFish.getSpeed());
					}
				}
				return true;
			}
		}
		return false;
	}
	// 初始化图片资源方法
	@Override
	public void initBitmap() {
		// TODO Auto-generated method stub
		playButton = BitmapFactory.decodeResource(getResources(),R.drawable.play);
		background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
		missile_bt = BitmapFactory.decodeResource(getResources(), R.drawable.missile_bt);
		scalex = screen_width / background.getWidth();
		scaley = screen_height / background.getHeight();
		play_bt_w = playButton.getWidth();
		play_bt_h = playButton.getHeight()/2;
		bg_x = 0;
		missile_bt_y = screen_height - 10 - missile_bt.getHeight();
	}
	//初始化游戏对象
	public void initObject(){
		for(EnemyFish obj:enemyFishes){
			//初始化小型敌机
			if(obj instanceof SmallFish){
				if(!obj.isAlive()){
					obj.initial(speedTime,0,0);
					break;
				}
			}
			//初始化中型敌机
			else if(obj instanceof MiddleFish){
				if(middleFishScore > 1000){
					if(!obj.isAlive()){
						obj.initial(speedTime,0,0);
						break;
					}
				}
			}
			//初始化大型敌机
			else if(obj instanceof BigFish){
				if(bigFishScore >= 2000){
					if(!obj.isAlive()){
						obj.initial(speedTime,0,0);
						break;
					}
				}
			}
		}
		//初始化导弹物品
		if(missileScore >= 3000){
			if(!missileGoods.isAlive()){
				missileGoods.initial(0,0,0);
				missileScore = 0;
			}
		}
		//提升等级
		if(sumScore >= speedTime*100000 && speedTime < 6){
			speedTime++;
		}
	}
	// 释放图片资源的方法
	@Override
	public void release() {
		// TODO Auto-generated method stub
		for(GameObject obj:enemyFishes){
			obj.release();
		}
		myFish.release();
		missileGoods.release();
		if(!playButton.isRecycled()){
			playButton.recycle();
		}
		if(!background.isRecycled()){
			background.recycle();
		}
		if(!missile_bt.isRecycled()){
			missile_bt.recycle();
		}
	}
	// 绘图方法
	@Override
	public void drawSelf() {
		// TODO Auto-generated method stub
		try {
			canvas = sfh.lockCanvas();
			canvas.drawColor(Color.BLACK); // 绘制背景色
			canvas.save();
			// 计算背景图片与屏幕的比例
			canvas.scale(scalex, scaley, 0, 0);
			canvas.drawBitmap(background, 0, bg_x, paint);   // 绘制背景图
			canvas.restore();
			//绘制按钮
			canvas.save();
			canvas.clipRect(10, 10, 10 + play_bt_w,10 + play_bt_h);
			if(isPlay){
				canvas.drawBitmap(playButton, 10, 10, paint);
			}
			else{
				canvas.drawBitmap(playButton, 10, 10 - play_bt_h, paint);
			}
			canvas.restore();
			//绘制导弹按钮
			if(missileCount > 0){
				paint.setTextSize(40);
				paint.setColor(Color.BLACK);
				canvas.drawBitmap(missile_bt, 10,missile_bt_y, paint);
				canvas.drawText("X "+String.valueOf(missileCount), 20 + missile_bt.getWidth(), screen_height - 25, paint);//绘制文字
			}
			//绘制导弹物品
			if(missileGoods.isAlive()){
				if(missileGoods.isCollide(myFish)){
					missileGoods.setAlive(false);
					missileCount++;
					sounds.playSound(6, 0);
				}
				else
					missileGoods.drawSelf(canvas);
			}

			//绘制敌方鱼
			for(EnemyFish obj:enemyFishes){
				if(obj.isAlive()){
					obj.drawSelf(canvas);
					//检测敌机是否与玩家的飞机碰撞
					if(obj.isCanCollide() && myFish.isAlive()){
						if(obj.isCollide(myFish)){
							if(myFish.getmyScore()>obj.getScore()){
								obj.setAlive(false);
								addGameScore(obj.getScore());
							} else{
								myFish.setAlive(false);
							}

						}
					}
				}
			}
			if(myFish.getmyScore()>1200){
				myFish.drawSelf(canvas);
			}
			if(!myFish.isAlive()){
				threadFlag = false;
				sounds.playSound(4, 0);			//飞机炸毁的音效
			}
			myFish.drawSelf(canvas);	//绘制玩家的飞机
			sounds.playSound(1, 0);	  //子弹音效
			//绘制导弹按钮
			if(missileCount > 0){
				paint.setTextSize(40);
				paint.setColor(Color.BLACK);
				canvas.drawBitmap(missile_bt, 10,missile_bt_y, paint);
				canvas.drawText("X "+String.valueOf(missileCount), 20 + missile_bt.getWidth(), screen_height - 25, paint);//绘制文字
			}
			//绘制积分文字
			paint.setTextSize(30);
			paint.setColor(Color.rgb(235, 161, 1));
			canvas.drawText("积分:"+String.valueOf(sumScore), 30 + play_bt_w, 40, paint);		//绘制文字
			canvas.drawText("等级 X "+String.valueOf(speedTime), screen_width - 150, 40, paint); //绘制文字
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			if (canvas != null)
				sfh.unlockCanvasAndPost(canvas);
		}
	}
	// 增加游戏分数的方法
	public void addGameScore(int score){
		middleFishScore += score;	// 中型敌机的积分
		bigFishScore += score;		// 大型敌机的积分
		missileScore += score;		// 导弹的积分
		sumScore += score;          // 游戏总得分
		myScore = myFish.getmyScore();
		myScore += score;
		myFish.setMyScore(myScore);
	}
	// 播放音效
	public void playSound(int key){
		sounds.playSound(key, 0);
	}
	// 线程运行的方法
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (threadFlag) {
			long startTime = System.currentTimeMillis();
			initObject();
			drawSelf();
			long endTime = System.currentTimeMillis();
			if(!isPlay){
				synchronized (thread) {
					try {
						thread.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				if (endTime - startTime < 100)
					Thread.sleep(100 - (endTime - startTime));
			} catch (InterruptedException err) {
				err.printStackTrace();
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Message message = new Message();
		message.what = 	ConstantUtil.TO_END_VIEW;
		message.arg1 = Integer.valueOf(sumScore);
		mainActivity.getHandler().sendMessage(message);
	}
}
