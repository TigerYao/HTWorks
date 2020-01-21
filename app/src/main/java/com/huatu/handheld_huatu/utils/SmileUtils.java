package com.huatu.handheld_huatu.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmileUtils {
	public static final String ee_1 = "[Hello]";
	public static final String ee_2 = "[Goodbye]";
	public static final String ee_3 = "[Smile]";
	public static final String ee_4 = "[Sad]";
	public static final String ee_5 = "[Too fast]";
	public static final String ee_6 = "[Too slow]";
	public static final String ee_7 = "[Agree]";
	public static final String ee_8 = "[Oppose]";
	public static final String ee_9 = "[Applaud]";
	public static final String ee_10 = "[Angry]";
	public static final String ee_11 = "[Pondering]";
	public static final String ee_12 = "[Bored]";
	public static final String ee_13 = "[Sweating]";
	public static final String ee_14 = "[Despise]";
	public static final String ee_15 = "[Doubt]";
	public static final String ee_16 = "[Wilt]";
	public static final String ee_17 = "[Flower]";
	public static final String ee_18 = "[Gift]";
	public static final String ee_19 = "[Silence]";
	public static final String ee_20 = "[Strive]";
	public static final String ee_21 = "[Embarrassed]";
	public static final String ee_22 = "[Applause]";
	public static final String ee_23 = "[Shy]";
	public static final String ee_24 = "[Panic]";
	public static final String ee_25 = "[Surprised]";
	public static final String ee_26 = "[Pick Nose]";
	public static final String ee_27 = "[Whimper]";
	public static final String ee_28 = "[Sob]";
	public static final String ee_29 = "[Hammer]";
	public static final String ee_30 = "[Thumbs Up]";
	public static final String ee_31 = "[Pucker]";
	public static final String ee_32 = "[Thumbs Down]";
	public static final String ee_33 = "[Drooling]";
	public static final String ee_34 = "[Chunkle]";
	public static final String ee_35 = "[Shhh]";
	public static final String ee_36 = "[Hypnotized]";

	private static final Factory spannableFactory = Factory.getInstance();

	private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

	static {
		addPattern(emoticons, ee_1, com.gensee.rtmpresourcelib.R.drawable.brow_nh);
		addPattern(emoticons, ee_2, com.gensee.rtmpresourcelib.R.drawable.brow_zj);
		addPattern(emoticons, ee_3, com.gensee.rtmpresourcelib.R.drawable.brow_gx);
		addPattern(emoticons, ee_4, com.gensee.rtmpresourcelib.R.drawable.brow_sx);
		addPattern(emoticons, ee_5, com.gensee.rtmpresourcelib.R.drawable.brow_tkl);
		addPattern(emoticons, ee_6, com.gensee.rtmpresourcelib.R.drawable.brow_tml);
		addPattern(emoticons, ee_7, com.gensee.rtmpresourcelib.R.drawable.brow_zt);
		addPattern(emoticons, ee_8, com.gensee.rtmpresourcelib.R.drawable.brow_fd);
		addPattern(emoticons, ee_9, com.gensee.rtmpresourcelib.R.drawable.brow_gz);
		addPattern(emoticons, ee_10, com.gensee.rtmpresourcelib.R.drawable.brow_fn);
		addPattern(emoticons, ee_11, com.gensee.rtmpresourcelib.R.drawable.brow_zdsk);
		addPattern(emoticons, ee_12, com.gensee.rtmpresourcelib.R.drawable.brow_wl);
		addPattern(emoticons, ee_13, com.gensee.rtmpresourcelib.R.drawable.brow_lh);
		addPattern(emoticons, ee_14, com.gensee.rtmpresourcelib.R.drawable.brow_bs);
		addPattern(emoticons, ee_15, com.gensee.rtmpresourcelib.R.drawable.brow_yw);
		addPattern(emoticons, ee_16, com.gensee.rtmpresourcelib.R.drawable.brow_dx);
		addPattern(emoticons, ee_17, com.gensee.rtmpresourcelib.R.drawable.brow_xh);
		addPattern(emoticons, ee_18, com.gensee.rtmpresourcelib.R.drawable.brow_lw);
		addPattern(emoticons, ee_19, com.gensee.rtmpresourcelib.R.drawable.emotion_bz);
		addPattern(emoticons, ee_20, com.gensee.rtmpresourcelib.R.drawable.emotion_fd);
		addPattern(emoticons, ee_21, com.gensee.rtmpresourcelib.R.drawable.emotion_gg);
		addPattern(emoticons, ee_22, com.gensee.rtmpresourcelib.R.drawable.emotion_gz);
		addPattern(emoticons, ee_23, com.gensee.rtmpresourcelib.R.drawable.emotion_hx);
		addPattern(emoticons, ee_24, com.gensee.rtmpresourcelib.R.drawable.emotion_jk);
		addPattern(emoticons, ee_25, com.gensee.rtmpresourcelib.R.drawable.emotion_jy);
		addPattern(emoticons, ee_26, com.gensee.rtmpresourcelib.R.drawable.emotion_kb);
		addPattern(emoticons, ee_27, com.gensee.rtmpresourcelib.R.drawable.emotion_kl);
		addPattern(emoticons, ee_28, com.gensee.rtmpresourcelib.R.drawable.emotion_ll);
		addPattern(emoticons, ee_29, com.gensee.rtmpresourcelib.R.drawable.emotion_qd);
		addPattern(emoticons, ee_30, com.gensee.rtmpresourcelib.R.drawable.emotion_qh);
		addPattern(emoticons, ee_31, com.gensee.rtmpresourcelib.R.drawable.emotion_qq);
		addPattern(emoticons, ee_32, com.gensee.rtmpresourcelib.R.drawable.emotion_rb);
		addPattern(emoticons, ee_33, com.gensee.rtmpresourcelib.R.drawable.emotion_se);
		addPattern(emoticons, ee_34, com.gensee.rtmpresourcelib.R.drawable.emotion_tx);
		addPattern(emoticons, ee_35, com.gensee.rtmpresourcelib.R.drawable.emotion_xu);
		addPattern(emoticons, ee_36, com.gensee.rtmpresourcelib.R.drawable.emotion_yun);
	}

	private static void addPattern(Map<Pattern, Integer> map, String smile,
			int resource) {
		map.put(Pattern.compile(Pattern.quote(smile)), resource);
	}
	
	public static boolean addSmiles(Context context, Spannable spannable) {
	    boolean hasChanges = false;
	    for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(spannable);
	        while (matcher.find()) {
	            boolean set = true;
	            for (ImageSpan span : spannable.getSpans(matcher.start(),
	                    matcher.end(), ImageSpan.class))
	                if (spannable.getSpanStart(span) >= matcher.start()
	                        && spannable.getSpanEnd(span) <= matcher.end())
	                    spannable.removeSpan(span);
	                else {
	                    set = false;
	                    break;
	                }
	            if (set) {
	                hasChanges = true;
	                spannable.setSpan(new ImageSpan(context, entry.getValue()),
	                        matcher.start(), matcher.end(),
	                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	            }
	        }
	    }
	    return hasChanges;
	}

	public static Spannable getSmiledText(Context context, CharSequence text) {
	    Spannable spannable = spannableFactory.newSpannable(text);
	    addSmiles(context, spannable);
	    return spannable;
	}
	
	public static boolean containsKey(String key){
		boolean b = false;
		for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(key);
	        if (matcher.find()) {
	        	b = true;
	        	break;
	        }
		}
		return b;
	}
	
	
}
