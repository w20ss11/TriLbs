package com.server.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class Count {
	private Map<String,ArrayList<String>> map_staticInfo=new HashMap<String,ArrayList<String>>();
	private int num;
	private Double[] double_count=new Double[num];
	private HashMap<String, ArrayList<String>> xy_map;
	public Count(Map<String,ArrayList<String>> map_staticInfo,Double[] double_count,int num,HashMap<String, ArrayList<String>> xy_map){
		this.map_staticInfo=map_staticInfo;
		this.double_count=double_count;
		this.num=num;
		this.xy_map=xy_map;
	}
	public int countFun(int k){
		//»ñÈ¡×ø±ê
		ArrayList<String> temp_1=xy_map.get("x");
		ArrayList<String> temp_2=xy_map.get("y");
		Integer[] x=new Integer[temp_1.size()];
		Integer[] y=new Integer[temp_2.size()];
		for(int i=0;i<temp_1.size();i++){
			x[i]=Integer.parseInt(temp_1.get(i));
			y[i]=Integer.parseInt(temp_2.get(i));
		}
//		temp_1.toArray(x);
//		temp_2.toArray(y);
		
		
		int at=-1;
		Map<Integer, Double> map_dis=new HashMap<Integer, Double>();
		for(int i=0;i<36;i++){
			System.out.println("i:"+i);
			Double d_temp = new Double(0.0);
			for(int j=0;j<num;j++){//4
				Double d=new Double(0.0);
				System.out.println("num:"+num+",j:"+j);
				for(int l=0;l<double_count.length;l++){
					if(double_count[l]==null)
						double_count[l]=(double) -50;
				}
				d=Double.parseDouble(map_staticInfo.get("apMacAddress"+j).get(i))-double_count[j];
				d_temp+=Math.pow(d, 2);
			}
			map_dis.put(i,Math.sqrt(d_temp));
			System.out.println(map_dis.toString());
		}
		ArrayList<Entry<Integer, Double>> list_afSort=sortHashmap(map_dis);
		int at_x=0;
		int at_y=0;
		for(int i=0;i<k;i++){
			at_x=+x[list_afSort.get(i).getKey()];
			at_y=+y[list_afSort.get(i).getKey()];
		}
		if(at_x/k>5 && at_y>5)
			at=1;
		else if(at_x/k>5 && at_y>5)
			at=2;
		else if(at_x/k>5 && at_y>5)
			at=3;
		else if(at_x/k>5 && at_y>5)
			at=4;
		return at;
	}
	private ArrayList<Entry<Integer, Double>> sortHashmap(Map<Integer, Double> map_dis) {
		List<Map.Entry<Integer, Double>> entries = new ArrayList<Map.Entry<Integer, Double>>(map_dis.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<Integer, Double>>() {
			public int compare(Map.Entry<Integer, Double> obj1 , Map.Entry<Integer, Double> obj2) {
				int res=obj2.getValue()>obj1.getValue()?-1:1;
				return res;
			}
		});
		System.out.println((ArrayList<Entry<Integer, Double>>) entries);
		return (ArrayList<Entry<Integer, Double>>) entries;

	}
}
