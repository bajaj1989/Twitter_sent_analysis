package com.manish;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ReducerFunc extends Reducer<Text, LongWritable, Text, NullWritable>{

	//private TreeMap<String,Long> m = new TreeMap<String,Long>();

@Override
protected void reduce(Text key, Iterable<LongWritable> value,
		Reducer<Text, LongWritable, Text, NullWritable>.Context context)
		throws IOException, InterruptedException {
	
	Iterator x = value.iterator();
	long result = 0;
	long pos = 0;
	long neg = 0;
	long neutral = 0;
	long finalResult = 0;
	while(x.hasNext()){
		
		result = ((LongWritable)(x.next())).get();
		if(result == 1){
			pos++;
		}
		else if(result == -1){
			neg++;
		}
		else if(result == 0){
			neutral++;
		}
	}
	finalResult = pos - neg;
	String outKey = key.toString()+"\t"+pos+"\t"+neg+"\t"+neutral+"\t"+finalResult;
	Text outKey1 = new Text(outKey);
	context.write(outKey1, null);
}

}