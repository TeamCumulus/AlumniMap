import java.io.*;
import java.text.DecimalFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

public class AgeMR {
	public static class AgeMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
		
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
			String[] str=value.toString().split("*");
			if(!"".equals(str[1]) && !"".equals(str[4])){
				IntWritable flag=new IntWritable(Integer.parseInt(str[4]));
				context.write(new Text(str[1]), flag);
			}
		}
	}
	public static class AgeReducer extends Reducer<Text, IntWritable, Text, Text>{
		
		@Override
		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException{
			double age=0.0;
			double count=0.0;
			for(IntWritable val:values){
				age+=val.get();
				count++;
			}
			DecimalFormat df=new DecimalFormat("#.00");
			String avg=df.format(age/count);
			context.write(key, new Text(avg));
		}
	}
	public static void main(String[] args) throws Exception{
		Configuration conf = new Configuration();
        Job job = new Job(conf, "AgeMR");
        job.setJarByClass(AgeMR.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapperClass(AgeMapper.class);
        job.setReducerClass(AgeReducer.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.waitForCompletion(true);
	}
}