package com.example.factory;

import android.content.res.Resources;
import com.example.object.BigFish;
import com.example.object.MiddleFish;
import com.example.object.GameObject;
import com.example.object.MissileGoods;
import com.example.object.MyFish;
import com.example.object.SmallFish;
/*游戏对象的工厂类*/
public class GameObjectFactory {
	//创建小型鱼的方法
	public GameObject createSmallFish(Resources resources){
		return new SmallFish(resources);
	}
	//创建中型鱼的方法
	public GameObject createMiddleFish(Resources resources){
		return new MiddleFish(resources);
	}
	//创建大型鱼的方法
	public GameObject createBigFish(Resources resources){
		return new BigFish(resources);
	}
	//创建玩家鱼的方法
	public GameObject createMyFish(Resources resources){
		return new MyFish(resources);
	}
	//创建导弹物品
	public GameObject createMissileGoods(Resources resources){
		return new MissileGoods(resources);
	}

}
