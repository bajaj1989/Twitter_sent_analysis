package com.manish;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MapperFunc extends Mapper<LongWritable, Text, Text, LongWritable> {

private Text temp = new Text();
HashMap<String,String> pos = new HashMap<String,String>();
HashMap<String,String> neg = new HashMap<String,String>();
HashMap<String,String> stop = new HashMap<String,String>();

Path[] cachefiles = new Path[03];

private static final LongWritable one =new LongWritable (1L);
@Override
	protected void setup(
			Mapper<LongWritable, Text, Text, LongWritable>.Context context)
			throws IOException, InterruptedException {
	//reading files in local mode
	/*File fileDirs = new File("/home/manish/Work/apache-flume-1.3.1-bin/conf/positive.txt");
	
	BufferedReader in = new BufferedReader(
	new InputStreamReader(new FileInputStream(fileDirs), "UTF-8"));

	while ((in.readLine()) != null) {
	   pos.put(in.readLine(), "");
	}
	
	File fileDirs1 = new File("/home/manish/Work/apache-flume-1.3.1-bin/conf/negative.txt");
	
	BufferedReader in1 = new BufferedReader(
	new InputStreamReader(new FileInputStream(fileDirs1), "UTF-8"));
	String s;
	while ((s=in1.readLine()) != null) {
	   neg.put(s,s);
	}
	
File fileDirs2 = new File("/home/manish/Work/apache-flume-1.3.1-bin/conf/Stopwords.txt");
	
	BufferedReader in2 = new BufferedReader(
	new InputStreamReader(new FileInputStream(fileDirs2), "UTF-8"));
	String s2;
	while ((s2=in2.readLine()) != null) {
	   stop.put(s2,s2);
	}*/
	
	// reading files from cache
	
	Configuration conf = context.getConfiguration();
	cachefiles = DistributedCache.getLocalCacheFiles(conf);
	BufferedReader in = new BufferedReader(new FileReader(cachefiles[0].toString())); 
	while ((in.readLine()) != null) {
		   pos.put(in.readLine(), "");
		}
	
	BufferedReader in1 = new BufferedReader(new FileReader(cachefiles[1].toString())); 
	while ((in1.readLine()) != null) {
		   neg.put(in1.readLine(), "");
		}
	BufferedReader in2 = new BufferedReader(new FileReader(cachefiles[2].toString()));
	String s2;
	while ((s2 = in2.readLine()) != null) {
		stop.put(s2,s2);
		}
	
	
	}
@Override
protected void map(LongWritable key, Text value,
		Mapper<LongWritable, Text, Text, LongWritable>.Context context)
		throws IOException, InterruptedException {
	
	String string = value.toString();
	StringTokenizer strTock = new StringTokenizer(string, "\t");
	//StringBuffer line = new StringBuffer();
	long posCount = 0;
	long negCount = 0;
	String time = null;
//	while (strTock.hasMoreTokens()) {
		if(!strTock.hasMoreTokens())
			return;
		String user = strTock.nextToken();
		if(!strTock.hasMoreTokens())
			return;
		time = strTock.nextToken();
		time = time.substring(11, 16);
		if(!strTock.hasMoreTokens())
			return;
		String text = strTock.nextToken();
		StringTokenizer strTock1 = new StringTokenizer(text, " ");
		while (strTock1.hasMoreTokens()) {
		String word = strTock1.nextToken();
		if(stop.get(word) != null)
		{
			if(stop.get(word).equals(word)){
				//word = "";
				continue;
			}
		}
		word = word.replaceAll("[^\\x00-\\x7F]", "");// remove chinese characters
		if(pos.get(word) != null)
		{
			if(pos.get(word).equals(word)){
				//word = "POSITIVE";
				posCount++;
			}
		}
		else if(neg.get(word) != null)
		{
			if(neg.get(word).equals(word)){
				//word = "NEGATIVE";
				negCount++;
			}
		}
		
		//line.append(word);
		if(posCount > negCount){
			one.set(1);
		}
		else if(posCount < negCount){
			one.set(-1);
		}
		else{
			one.set(0);
		}
		word = null;
	}	
//	}
		temp.set(time);
		context.write(temp, one);
	

}
}
