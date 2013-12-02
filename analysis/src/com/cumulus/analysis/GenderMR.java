import java.io.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

public class GenderMR {
	public static class GenderMapper extends Mapper<LongWritable, Text, Text, IntWritable>{

		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
			String[] str=value.toString().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			String location=str[1].substring(1,str[1].length()-1);
			String gender=str[2].substring(1,str[2].length()-1);
			if(!"".equals(location) && !"".equals(gender)){
				IntWritable flag=new IntWritable(Integer.parseInt(gender));
				context.write(new Text(location), flag);
			}
		}
	}
	public static class GenderReducer extends Reducer<Text, IntWritable, Text, Text> {

		@Override
		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException{
			int male=0;
			int female=0;
			for(IntWritable val:values){
				if(val.get()==0){
					male++;
				}else if(val.get()==1){
					female++;
				}else{
					//DO NOTHING
				}
			}
			String str=male+" "+female;
			context.write(key, new Text(str));
		}
	}
	public static void main(String[] args) throws Exception{
		Configuration conf = new Configuration();
		Job job = new Job(conf, "GenderMR");
		job.setJarByClass(GenderMR.class);
		job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setMapperClass(GenderMapper.class);
        job.setReducerClass(GenderReducer.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.waitForCompletion(true);
	}
}