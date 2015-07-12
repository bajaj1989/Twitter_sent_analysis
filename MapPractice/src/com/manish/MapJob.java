package com.manish;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
//import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class MapJob implements Tool{
	private Configuration conf;
	@Override
	public Configuration getConf() {
		return this.conf;
	}

	
	@Override
	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	@Override
	public int run(String[] args) throws Exception {
		
		//adding files to distributed cache
		//Configuration job = new Configuration();
		 DistributedCache.addCacheFile(new URI("/cache/positive.txt"), conf);
		 DistributedCache.addCacheFile(new URI("/cache/negative.txt"), conf);
		 DistributedCache.addCacheFile(new URI("/cache/Stopwords.txt"), conf);
		 Job twitterJob = new Job(getConf());
		twitterJob.setJobName("Kelly Word Map");
		twitterJob.setJarByClass(this.getClass());
		twitterJob.setMapperClass(MapperFunc.class);
		twitterJob.setReducerClass(ReducerFunc.class);
		twitterJob.setMapOutputKeyClass(Text.class);
		twitterJob.setMapOutputValueClass(LongWritable.class);
		twitterJob.setOutputKeyClass(Text.class);
		twitterJob.setNumReduceTasks(1);
//		wordCountJob.setPartitionerClass(WordCountPartitioner.class);
		//mapJob.setCombinerClass(WordCountCombiner.class);
		twitterJob.setOutputValueClass(NullWritable.class);
		FileInputFormat.setInputPaths(twitterJob, new Path(args[0]));
		FileOutputFormat.setOutputPath(twitterJob, new Path(args[1]));
		return twitterJob.waitForCompletion(true) == true ? 0 : -1;
	}
public static void main(String[] args) throws Exception {
	ToolRunner.run(new Configuration(), new MapJob(), args);
}
}