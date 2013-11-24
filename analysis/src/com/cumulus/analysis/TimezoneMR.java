import java.io.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

public class TimezoneMR {
	public static class TimezoneMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
		 
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
        	String[] str=value.toString().split("*");
        	if(!"".equals(str[1]) && !"".equals(str[8])){
        		IntWritable flag=new IntWritable(1);
                context.write(new Text(str[1]), flag);
        	}
        }
    }
	public static class TimezoneReducer extends Reducer<Text, IntWritable, Text, Text> {
		 
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException{
        	int count=0;
        	for(IntWritable val:values){
        		count+=val.get();
        	}
        	context.write(key, new Text(String.valueOf(count)));
        }
	}
	public static void main(String[] args) throws Exception{
        Configuration conf = new Configuration();
        Job job = new Job(conf, "TimezoneMR");
        job.setJarByClass(TimezoneMR.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapperClass(TimezoneMapper.class);
        job.setReducerClass(TimezoneReducer.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.waitForCompletion(true);
    }
}