package com.android.ui.data;

import java.util.LinkedList;
import java.util.Queue;

public class UIPool {
	private static Queue<Element> infos = new LinkedList<Element>();
	
	public static synchronized void offer(Element element){
		infos.offer(element);
	}
	
	public static synchronized Element poll(){
		return infos.poll();
	}
}
