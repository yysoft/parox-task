package com.daoman.task.utils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class StrUtils {
	/**
	 * 禁止实例化
	 */
	private StrUtils() {
	}

	/**
	 * 处理url
	 * 
	 * url为null返回null，url为空串或以http://或https://开头，则加上http://，其他情况返回原参数。
	 * 
	 * @param url
	 * @return
	 */
	public static String handelUrl(String url) {
		if (url == null) {
			return null;
		}
		url = url.trim();
		if (url.equals("") || url.startsWith("http://")
				|| url.startsWith("https://")) {
			return url;
		} else {
			return "http://" + url.trim();
		}
	}
	
	public static boolean checkMobile(String phoneNum){
		Pattern p = Pattern.compile("1\\d{10}");
		Matcher m = p.matcher(phoneNum);
		return m.matches();
	}
	
	/** 
     * 验证输入的邮箱格式是否符合 
     * @param email 
     * @return 是否合法 
     */
	public static boolean checkEmail(String email)  
    {  
		if(isEmpty(email)) return false;
		
        boolean valid = true;  
        final String pattern1 = "^([a-z0-9A-Z]+[-|\\._\\+]?)+[_a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";  
        final Pattern pattern = Pattern.compile(pattern1);  
        final Matcher mat = pattern.matcher(email);  
        if (!mat.find()) {  
        	valid = false;  
        }  
        return valid;
    }

	/**
	 * 分割并且去除空格
	 * 
	 * @param str
	 *            待分割字符串
	 * @param sep
	 *            分割符
	 * @param sep2
	 *            第二个分隔符
	 * @return 如果str为空，则返回null。
	 */
	public static String[] splitAndTrim(String str, String sep, String sep2) {
		if (StringUtils.isBlank(str)) {
			return null;
		}
		if (!StringUtils.isBlank(sep2)) {
			str = StringUtils.replace(str, sep2, sep);
		}
		String[] arr = StringUtils.split(str, sep);
		// trim
		for (int i = 0, len = arr.length; i < len; i++) {
			arr[i] = arr[i].trim();
		}
		return arr;
	}

	/**
	 * 文本转html
	 * 
	 * @param txt
	 * @return
	 */
	public static String txt2htm(String txt) {
		if (StringUtils.isBlank(txt)) {
			return txt;
		}
		StringBuilder sb = new StringBuilder((int) (txt.length() * 1.2));
		char c;
		boolean doub = false;
		for (int i = 0; i < txt.length(); i++) {
			c = txt.charAt(i);
			if (c == ' ') {
				if (doub) {
					sb.append(' ');
					doub = false;
				} else {
					sb.append("&nbsp;");
					doub = true;
				}
			} else {
				doub = false;
				switch (c) {
				case '&':
					sb.append("&amp;");
					break;
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				case '"':
					sb.append("&quot;");
					break;
				case '\n':
					sb.append("<br/>");
					break;
				default:
					sb.append(c);
					break;
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 剪切文本。如果进行了剪切，则在文本后加上"..."
	 * 
	 * @param s
	 *            剪切对象。
	 * @param len
	 *            编码小于256的作为一个字符，大于256的作为两个字符。
	 * @return
	 */
	public static String textCut(String s, int len, String append) {
		if (s == null) {
			return null;
		}
		int slen = s.length();
		if (slen <= len) {
			return s;
		}
		// 最大计数（如果全是英文）
		int maxCount = len * 2;
		int count = 0;
		int i = 0;
		for (; count < maxCount && i < slen; i++) {
			if (s.codePointAt(i) < 256) {
				count++;
			} else {
				count += 2;
			}
		}
		if (i < slen) {
			if (count > maxCount) {
				i--;
			}
			if (!StringUtils.isBlank(append)) {
				if (s.codePointAt(i - 1) < 256) {
					i -= 2;
				} else {
					i--;
				}
				return s.substring(0, i) + append;
			} else {
				return s.substring(0, i);
			}
		} else {
			return s;
		}
	}

/*	public static String htmlCut(String s, int len, String append) {
		String text = html2Text(s, len * 2);
		return textCut(text, len, append);
	}
*/
	/*public static String html2Text(String html, int len) {
		try {
			Lexer lexer = new Lexer(html);
			Node node;
			StringBuilder sb = new StringBuilder(html.length());
			while ((node = lexer.nextNode()) != null) {
				if (node instanceof TextNode) {
					sb.append(node.toHtml());
				}
				if (sb.length() > len) {
					break;
				}
			}
			return sb.toString();
		} catch (ParserException e) {
			throw new RuntimeException(e);
		}
	}*/
	
	/**
	 * 
	 * @param keyword 源词汇
	 * @param smart 是否智能分词
	 * @return 分词词组(,拼接)
	 */
	/*public static String getKeywords(String keyword, boolean smart) {
		StringReader reader = new StringReader(keyword);
		IKSegmenter iks = new IKSegmenter(reader, smart);
		StringBuilder buffer = new StringBuilder();
		try {
			Lexeme lexeme;
			while ((lexeme = iks.next()) != null) {
				buffer.append(lexeme.getLexemeText()).append(',');
			}
		} catch (IOException e) {
		}
		//去除最后一个,
		if (buffer.length() > 0) {
			buffer.setLength(buffer.length() - 1);
		}
		return buffer.toString();
	}
*/
	/**
	 * 检查字符串中是否包含被搜索的字符串。被搜索的字符串可以使用通配符'*'。
	 * 
	 * @param str
	 * @param search
	 * @return
	 */
	public static boolean contains(String str, String search) {
		if (StringUtils.isBlank(str) || StringUtils.isBlank(search)) {
			return false;
		}
		String reg = StringUtils.replace(search, "*", ".*");
		Pattern p = Pattern.compile(reg);
		return p.matcher(str).matches();
	}

	public static boolean containsKeyString(String str) {
		if (StringUtils.isBlank(str)) {
			return false;
		}
		if (str.contains("'") || str.contains("\"") || str.contains("\r")
				|| str.contains("\n") || str.contains("\t")
				|| str.contains("\b") || str.contains("\f")) {
			return true;
		}
		return false;
	}

	// 将""和'转义
	public static String replaceKeyString(String str) {
		if (containsKeyString(str)) {
			return str.replace("'", "\\'").replace("\"", "\\\"").replace("\r",
					"\\r").replace("\n", "\\n").replace("\t", "\\t").replace(
					"\b", "\\b").replace("\f", "\\f");
		} else {
			return str;
		}
	}
	
	public static String getSuffix(String str) {
		int splitIndex = str.lastIndexOf(".");
		return str.substring(splitIndex + 1);
	}
	
	public static String encode(String url) {
		String str=url;
		if(StringUtils.isBlank(str)){
			return str;
		}
		try {
			str = java.net.URLEncoder.encode(url, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	public static String decode(String url) {
		String str=url;
		if(StringUtils.isBlank(str)){
			return str;
		}
		try {
			str = java.net.URLDecoder.decode(url, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	/** 
	 * 反格式化byte 
	 *  
	 * @param s 
	 * @return 
	 */  
	public static byte[] hex2byte(String s) {  
	    byte[] src = s.toLowerCase().getBytes();  
	    byte[] ret = new byte[src.length / 2];  
	    for (int i = 0; i < src.length; i += 2) {  
	        byte hi = src[i];  
	        byte low = src[i + 1];  
	        hi = (byte) ((hi >= 'a' && hi <= 'f') ? 0x0a + (hi - 'a')  
	                : hi - '0');  
	        low = (byte) ((low >= 'a' && low <= 'f') ? 0x0a + (low - 'a')  
	                : low - '0');  
	        ret[i / 2] = (byte) (hi << 4 | low);  
	    }  
	    return ret;  
	}  
	  
	/** 
	 * 格式化byte 
	 *  
	 * @param b 
	 * @return 
	 */  
	public static String byte2hex(byte[] b) {  
	    char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',  
	            'B', 'C', 'D', 'E', 'F' };  
	    char[] out = new char[b.length * 2];  
	    for (int i = 0; i < b.length; i++) {  
	        byte c = b[i];  
	        out[i * 2] = Digit[(c >>> 4) & 0X0F];  
	        out[i * 2 + 1] = Digit[c & 0X0F];  
	    }
	  
	    return new String(out);
	}
	
	public static boolean isEmpty(String arg){
		if(arg==null || "".equals(arg)){
			return true;
		}		
		return false;
	}
	
	public static boolean isNumber(String s) {
		if (isEmpty(s)) {
			return false;
		} else {
			for (int i = 0; i < s.length(); i++) {
				if (!((s.charAt(i) >= '0') && (s.charAt(i) <= '9'))) {
					return false;
				}
			}
			return true;
		}
	}
	
	/**
	 * 随机生成字符串
	 * @param n
	 * @return
	 */
	public static String randomNumber(int n) {
		String sRand = "";
		Random random = new Random();
		for (int i = 0; i < n; i++) {
			String rand = String.valueOf(random.nextInt(10));
			sRand += rand;
		}
		return sRand;
	}
	
	public static final char[] N62_CHARS = { '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
		'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
		'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
		'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
		'X', 'Y', 'Z' };
	
	public static final char[] N36_CHARS = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
			'x', 'y', 'z' };
	
	private static StringBuilder longToNBuf(long l, char[] chars) {
		int upgrade = chars.length;
		StringBuilder result = new StringBuilder();
		int last;
		while (l > 0) {
			last = (int) (l % upgrade);
			result.append(chars[last]);
			l /= upgrade;
		}
		return result;
	}
	
	/**
	 * 长整数转换成N62
	 * 
	 * @param l
	 * @return
	 */
	public static String longToN62(long l) {
		return longToNBuf(l, N62_CHARS).reverse().toString();
	}
	
	public static String longToN36(long l) {
		return longToNBuf(l, N36_CHARS).reverse().toString();
	}
	
	/**
	 * 长整数转换成N62
	 * 
	 * @param l
	 * @param length
	 *            如N62不足length长度，则补足0。
	 * @return
	 */
	public static String longToN62(long l, int length) {
		StringBuilder sb = longToNBuf(l, N62_CHARS);
		for (int i = sb.length(); i < length; i++) {
			sb.append('0');
		}
		return sb.reverse().toString();
	}
	
	public static String longToN36(long l, int length) {
		StringBuilder sb = longToNBuf(l, N36_CHARS);
		for (int i = sb.length(); i < length; i++) {
			sb.append('0');
		}
		return sb.reverse().toString();
	}
	
	/**
	 * N62转换成整数
	 * 
	 * @param n62
	 * @return
	 */
	public static long n62ToLong(String n62) {
		return nToLong(n62, N62_CHARS);
	}
	
	public static long n36ToLong(String n36) {
		return nToLong(n36, N36_CHARS);
	}
	
	private static long nToLong(String s, char[] chars) {
		char[] nc = s.toCharArray();
		long result = 0;
		long pow = 1;
		for (int i = nc.length - 1; i >= 0; i--, pow *= chars.length) {
			int n = findNIndex(nc[i], chars);
			result += n * pow;
		}
		return result;
	}
	
	private static int findNIndex(char c, char[] chars) {
		for (int i = 0; i < chars.length; i++) {
			if (c == chars[i]) {
				return i;
			}
		}
		throw new RuntimeException("N62(N36)非法字符：" + c);
	}

	public static String filterHtml(String html){
		if(html==null)
			return null;
	    // 配置html标记。  
	    Pattern p = Pattern.compile("<(\\S*?)[^>]*>.*?|<.*?/>");  
	    //String html = "<td><a target=\"_blank\" href=\"http://www.baidu.com/baidu?cl=3&tn=baidutop10&fr=top1000&wd=%A1%B6%CC%C6%C9%BD%B4%F3%B5%D8%D5%F0%A1%B7%B9%AB%D3%B3\">《唐山大地震》公映</a></td>";  
	    Matcher m = p.matcher(html);  
	
	    String rs = new String(html);  
	    // 找出所有html标记。  
	    while (m.find()) {  
	        // 删除html标记。  
	        rs = rs.replace(m.group(), "");  
	    }
	    rs = rs.replaceAll("&nbsp;", "");
	    return rs;  
	}
	
	public static List<Long> splitToLong(String args){
		
		List<String> idStrList = splitToString(args);
		List<Long> idList = Lists.newArrayList();
		
		for(String id: idStrList){
			if(StrUtils.isNumber(id)){
				idList.add(Long.valueOf(id));
			}
		}
		
		return idList;
	}
	
	public static List<String>splitToString(String args){
		if(args==null){
			return Lists.newArrayList();
		}
		Splitter spliter = Splitter.on(",").omitEmptyStrings().trimResults();
		List<String> labels = Lists.newArrayList(spliter.split(args));
		return labels;
	}
	
	public static boolean equals(String str1, String str2) 
    {
        return org.apache.commons.lang.StringUtils.equals(str1, str2);
    }

	public static void main(String args[]) {
		//System.out.println(replaceKeyString("&nbsp;\r" + "</p>"));
		System.out.println(checkEmail("zby@parox.cn"));
	}
}
