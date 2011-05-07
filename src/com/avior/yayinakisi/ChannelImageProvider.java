package com.avior.yayinakisi;

import java.util.HashMap;
import java.util.Map;

import com.avior.yayinakisi.R;
import com.avior.yayinakisi.R.drawable;
import com.avior.yayinakisi.model.Channel;

public class ChannelImageProvider {
	private static Map<Integer, Integer> channelMap = new HashMap<Integer, Integer>(){{
		put(1, R.drawable.atv);
		put(2, R.drawable.cnbce);
		put(3, R.drawable.cnnturk);
		put(5, R.drawable.kanald);
		put(7, R.drawable.ntv);
		put(8, R.drawable.show);
		put(9, R.drawable.star);
		put(10, R.drawable.fox);
		put(11, R.drawable.trt1);
		put(14, R.drawable.tv8);
		put(15, R.drawable.haberturk);
		put(13, R.drawable.cine5);
		put(19, R.drawable.tnt);
		put(20, R.drawable.e2);
		put(6, R.drawable.ligtv);
		put(23, R.drawable.tv24);
	}};
	
	private static final Integer DEFAULT_LOGO = R.drawable.icon;
	
	public static int getChannelImageResource(Channel channel){
		Integer logoId = channelMap.get(channel.getCode());
		
		if(logoId == null){
			return DEFAULT_LOGO;
		} else {
			return logoId;
		}
	}
}
