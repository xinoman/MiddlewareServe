/**
 * 
 */
package com.its.core.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * 创建日期 2014-5-11 下午05:28:00
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class Doubleball {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Random r = new Random();
		String[] balls ={"01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33"};
		boolean[] used = new boolean[balls.length];
		String[] ball = new String[6];
		int num = 1;
		for(int j=0;j<100;j++){
			for(int i=0;i<ball.length;i++){
				int index = r.nextInt(balls.length);
				if(used[index]){
					continue;
				}
				used[index]=true;
				ball[i]=balls[index];
				num++;
				if(num==ball.length){
					break;
				}
			}
		}
		 Arrays.sort(ball);
		 ball = Arrays.copyOf(ball,ball.length+1);
		 int index = r.nextInt(16);
		 ball[ball.length-1]=balls[index];
		 System.out.println(Arrays.toString(ball));
		 
		 Integer it =new Integer(0);
			ArrayList<Integer> al=new ArrayList<Integer>();
			for(int i=0;i<33;i++)
			{
				al.add(new Integer(i+1));
			}
			
			int a=0;
			System.out.print("[红球]");
			for(int j=0;j<6;j++)
			{
				a=r.nextInt(33-j);
				it=al.remove(a);
				System.out.print(it+" ");
			}
			System.out.println(" [蓝球] "+r.nextInt(17));

	}

}
